package tp3.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class EstoqueWebSocketHandler extends TextWebSocketHandler {

    // Lista thread-safe para guardar todos os clientes (navegadores/apps) conectados
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("Novo cliente WebSocket conectado: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        System.out.println("Cliente WebSocket desconectado: " + session.getId());
    }

    // Método que o nosso EstoqueService vai chamar para avisar todo mundo
    public void notificarTodos(String mensagemJson) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(mensagemJson));
                } catch (IOException e) {
                    System.err.println("Erro ao enviar mensagem via WebSocket");
                }
            }
        }
    }
}