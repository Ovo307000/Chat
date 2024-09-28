'use strict';

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const connectingElement = document.querySelector('.connecting');
const chatArea = document.querySelector('#chat-messages');
const logout = document.querySelector('#logout');

let stompClient = null;
let nickname = null;
let fullname = null;
let selectedUserId = null;

/**
 * 连接到WebSocket服务器并切换界面
 *
 * 该函数的主要作用是获取用户输入的昵称和真实姓名，然后尝试建立与WebSocket服务器的连接
 * 当连接成功时，界面会从用户名输入页切换到聊天页
 *
 * @param {Event} event 触发此函数的事件，通常是从表单提交事件中调用
 */
function connect(event) {
    // 获取并修剪用户输入的昵称和真实姓名
    nickname = document.querySelector('#nickname').value.trim();
    fullname = document.querySelector('#fullname').value.trim();

    // 如果昵称和真实姓名都有效，则进行连接操作
    if (nickname && fullname) {
        // 切换页面，隐藏用户名输入页，显示聊天页
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        // 创建SockJS对象，用于连接WebSocket服务器
        const socket = new SockJS('/ws');
        // 使用Stomp.js库封装SockJS对象
        stompClient = Stomp.over(socket);

        // 尝试连接WebSocket服务器，连接成功调用onConnected，失败调用onError
        stompClient.connect({}, onConnected, onError);
    }
    // 阻止默认的事件行为，例如表单的提交行为
    event.preventDefault();
}



/**
 * 当用户连接成功时调用的函数
 *
 * 订阅个人队列和公共频道的消息，并向服务器注册当前在线用户
 */
function onConnected() {
    // 订阅个人消息队列，接收发给当前用户的消息
    stompClient.subscribe(`/user/${nickname}/queue/messages`, onMessageReceived);
    // 订阅公共频道，接收所有用户可以接收的消息
    stompClient.subscribe(`/user/public`, onMessageReceived);

    // 向服务器发送消息，注册当前在线用户
    stompClient.send("/app/user.addUser", {}, JSON.stringify({
        nickName: nickname, // 用户昵称
        fullName: fullname, // 用户全名
        status: 'ONLINE' // 用户在线状态
    }));
    // 更新页面上显示的当前用户全名
    document.querySelector('#connected-user-fullname').textContent = fullname;
    // 查询并显示当前在线用户列表
    findAndDisplayConnectedUsers().then();
}


/**
 * 异步获取并显示当前在线用户
 * 该函数通过Ajax请求获取服务器上在线用户的列表，然后在网页上显示这些用户
 */
async function findAndDisplayConnectedUsers() {
    // 发起一个GET请求，获取在线用户数据
    const connectedUsersResponse = await fetch('/users');
    // 解析服务器响应中的JSON数据，获取在线用户列表
    let connectedUsers = await connectedUsersResponse.json();
    // 过滤掉当前用户的昵称，确保列表中不包含重复的当前用户
    connectedUsers = connectedUsers.filter(user => user.nickName !== nickname);
    // 获取用于显示在线用户列表的DOM元素
    const connectedUsersList = document.getElementById('connectedUsers');
    // 清空用户列表，准备显示新的在线用户列表
    connectedUsersList.innerHTML = '';

    // 遍历在线用户列表，为每个用户创建并添加显示元素到页面上
    connectedUsers.forEach(user => {
        appendUserElement(user, connectedUsersList);
        // 除最后一个用户外，其他用户后面添加分隔线，以区分不同用户
        if (connectedUsers.indexOf(user) < connectedUsers.length - 1) {
            const separator = document.createElement('li');
            separator.classList.add('separator');
            connectedUsersList.appendChild(separator);
        }
    });
}


/**
 * 向已连接用户列表中添加一个用户元素
 *
 * 此函数创建一个新的列表项，并将其添加到已连接用户的列表中它负责生成用户对应的 HTML 元素，
 * 包括头像、用户名和接收的消息数，并将这些元素组装成一个可点击的列表项
 *
 * @param {Object} user - 包含用户信息的对象，包括昵称（nickName）和全名（fullName）
 * @param {HTMLElement} connectedUsersList - 代表用户列表的 HTML 元素，用于附加新用户元素
 */
function appendUserElement(user, connectedUsersList) {
    // 创建一个新的列表项元素
    const listItem = document.createElement('li');
    // 添加用户项的样式类
    listItem.classList.add('user-item');
    // 设置列表项的 ID 为用户的昵称
    listItem.id = user.nickName;

    // 创建用户头像的图片元素
    const userImage = document.createElement('img');
    // 设置图片的源路径
    userImage.src = '../img/user_icon.png';
    // 设置图片的替代文本为用户的全名
    userImage.alt = user.fullName;

    // 创建显示用户名的 span 元素
    const usernameSpan = document.createElement('span');
    // 设置 span 元素的文本内容为用户的全名
    usernameSpan.textContent = user.fullName;

    // 创建显示接收消息数的 span 元素，并初始化为 0
    const receivedMsgs = document.createElement('span');
    receivedMsgs.textContent = '0';
    // 添加消息数样式的类，并初始隐藏
    receivedMsgs.classList.add('nbr-msg', 'hidden');

    // 将头像、用户名和消息数元素添加到列表项中
    listItem.appendChild(userImage);
    listItem.appendChild(usernameSpan);
    listItem.appendChild(receivedMsgs);

    // 为列表项添加点击事件监听器
    listItem.addEventListener('click', userItemClick);

    // 将新的用户列表项添加到已连接用户的列表中
    connectedUsersList.appendChild(listItem);
}


/**
 * 当用户项被点击时触发
 * 移除所有用户项的'active'类，并为被点击的用户项添加'active'类
 * 更新选中的用户ID，并获取并显示与选中用户相关的聊天记录
 * 清空特定用户的未读消息数
 * @param {Event} event - 点击事件
 */
function userItemClick(event) {
    // 移除所有用户项的'active'类
    document.querySelectorAll('.user-item').forEach(item => {
        item.classList.remove('active');
    });
    // 显示消息表单
    messageForm.classList.remove('hidden');

    // 获取被点击的用户项，并为其添加'active'类
    const clickedUser = event.currentTarget;
    clickedUser.classList.add('active');

    // 更新选中的用户ID
    selectedUserId = clickedUser.getAttribute('id');
    // 获取并显示与选中用户相关的聊天记录
    fetchAndDisplayUserChat().then();

    // 清空特定用户的未读消息数
    const nbrMsg = clickedUser.querySelector('.nbr-msg');
    nbrMsg.classList.add('hidden');
    nbrMsg.textContent = '0';

}

/**
 * 显示一条消息
 * 根据发送者ID决定消息的类型（发送者或接收者）
 * @param {string} senderId - 消息发送者的ID
 * @param {string} content - 消息内容
 */
function displayMessage(senderId, content) {
    // 创建消息容器并设置类型
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('message');
    if (senderId === nickname) {
        messageContainer.classList.add('sender');
    } else {
        messageContainer.classList.add('receiver');
    }

    // 创建并添加消息文本
    const message = document.createElement('p');
    message.textContent = content;
    messageContainer.appendChild(message);
    chatArea.appendChild(messageContainer);
}

/**
 * 异步获取并显示用户之间的聊天记录
 */
async function fetchAndDisplayUserChat() {
    // 获取用户聊天记录
    const userChatResponse = await fetch(`/messages/${nickname}/${selectedUserId}`);
    const userChat = await userChatResponse.json();
    // 清空聊天区域并显示新获取的聊天记录
    chatArea.innerHTML = '';
    userChat.forEach(chat => {
        displayMessage(chat.senderId, chat.content);
    });
    // 滚动到底部
    chatArea.scrollTop = chatArea.scrollHeight;
}

/**
 * 当WebSocket连接错误时触发
 */
function onError() {
    // 显示错误信息并设置颜色为红色
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

/**
 * 发送消息
 * 如果输入的消息内容非空且STOMP客户端已连接，则发送消息
 * @param {Event} event - 提交事件
 */
function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        // 创建并发送聊天消息
        const chatMessage = {
            senderId: nickname, recipientId: selectedUserId, content: messageInput.value.trim(), timestamp: new Date()
        };
        stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
        // 显示发送的消息并清空输入框
        displayMessage(nickname, messageInput.value.trim());
        messageInput.value = '';
    }
    // 滚动到底部
    chatArea.scrollTop = chatArea.scrollHeight;
    event.preventDefault();
}

/**
 * 异步处理接收到的消息
 * 更新聊天记录并处理未读消息数
 */
async function onMessageReceived(payload) {
    // 更新在线用户列表
    await findAndDisplayConnectedUsers();
    console.log('Message received', payload);
    // 解析并显示接收到的消息
    const message = JSON.parse(payload.body);
    if (selectedUserId && selectedUserId === message.senderId) {
        displayMessage(message.senderId, message.content);
        chatArea.scrollTop = chatArea.scrollHeight;
    }

    // 更新用户项状态
    if (selectedUserId) {
        document.querySelector(`#${selectedUserId}`).classList.add('active');
    } else {
        messageForm.classList.add('hidden');
    }

    const notifiedUser = document.querySelector(`#${message.senderId}`);
    if (notifiedUser && !notifiedUser.classList.contains('active')) {
        const nbrMsg = notifiedUser.querySelector('.nbr-msg');
        nbrMsg.classList.remove('hidden');
        nbrMsg.textContent = '';
    }
}

/**
 * 当用户登出时触发
 * 通知服务器用户已断开连接，然后刷新页面
 */
function onLogout() {
    // 通知服务器用户断开连接
    stompClient.send("/app/user.disconnectUser", {}, JSON.stringify({
        nickName: nickname,
        fullName: fullname,
        status: 'OFFLINE'
    }));
    // 刷新页面
    window.location.reload();
}

// 监听表单提交事件
usernameForm.addEventListener('submit', connect, true); // step 1
messageForm.addEventListener('submit', sendMessage, true);
logout.addEventListener('click', onLogout, true);

// 当窗口关闭前触发登出函数
window.onbeforeunload = () => onLogout();
