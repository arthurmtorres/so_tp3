package tp3.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final EstoqueWebSocketHandler webSocketHandler;

    public WebSocketConfig(EstoqueWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Expõe o endpoint na URL: ws://localhost:8080/ws/estoque
        registry.addHandler(webSocketHandler, "/ws/estoque").setAllowedOrigins("*");
    }
}