package org.ovo307000.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer
{
    /**
     * 注册STOMP端点以启用WebSocket通信。
     * 此方法配置了WebSocket端点及其跨域设置，并启用了SockJS作为WebSocket的备用方案。
     *
     * @param registry 用于注册端点的StompEndpointRegistry实例
     */
    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry)
    {
        // 添加一个WebSocket端点，客户端可以通过“/ws”路径进行连接
        // 设置允许所有来源的跨域请求，使用SockJS协议作为WebSocket的兼容层

        // 允许所有来源的跨域请求
        registry.addEndpoint("/ws")
                // 使用 SockJS 协议，兼容 WebSocket
                .setAllowedOrigins("*")
                // 启用 SockJS，允许客户端使用 SockJS，如果浏览器不支持 WebSocket，可以使用 SockJS
                .withSockJS();
    }


    /**
     * 配置消息转换器以支持JSON格式。
     *
     * @param messageConverters 消息转换器列表，用于添加新的转换器
     * @return 返回false，指示配置消息转换器是可选操作
     */
    @Override
    public boolean configureMessageConverters(@NonNull final List<MessageConverter> messageConverters)
    {
        // 创建一个默认内容类型解析器实例
        final var resolver = new DefaultContentTypeResolver();
        // 创建一个用于JSON序列化和反序列化的消息转换器实例
        final var converter = new MappingJackson2MessageConverter();

        // 设置解析器的默认MIME类型为应用JSON
        resolver.setDefaultMimeType(MediaType.APPLICATION_JSON);

        // 为转换器设置一个新的对象映射器，用于处理JSON序列化和反序列化
        converter.setObjectMapper(new ObjectMapper());
        // 设置转换器的内容类型解析器，以便确定如何映射消息体
        converter.setContentTypeResolver(resolver);

        // 向给定的消息转换器列表中添加新的JSON转换器
        messageConverters.add(converter);


        // TODO 2024/9/28 15:21 @solow 这里返回 临时返回 false，暂时不注册消息转换器
        return false;
    }


    /**
     * 配置消息代理以支持 WebSocket 的通讯
     * 该方法主要用于设定哪些类型的消息传输可用，以及消息的目的地前缀
     *
     * @param registry 用于注册消息代理配置的接口
     */
    @Override
    public void configureMessageBroker(final MessageBrokerRegistry registry)
    {
        // 启用简单的 Broker，处理 /user 和 /topic 下的消息
        registry.enableSimpleBroker("/user", "/topic");

        // 设置应用目的地前缀为 /app，即消息的 Stomp header 中 destination 字段必须以"/app"开头
        registry.setApplicationDestinationPrefixes("/app");

        // 设置用户目的地前缀为"/user"，用于用户私有消息的路由
        registry.setUserDestinationPrefix("/user");
    }
}
