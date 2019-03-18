package com.calendar.scheduler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class SocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	
	@Override
    public void configureMessageBroker(MessageBrokerRegistry messageBrokerConfig) {
		messageBrokerConfig.enableSimpleBroker("/topic");
    }	
	
	/* 
	 * @see org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer#registerStompEndpoints(org.springframework.web.socket.config.annotation.StompEndpointRegistry)
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry stompRegistry) {
		stompRegistry.addEndpoint("/websocket").withSockJS();
	}

	
}
