package tp3.service;

import org.springframework.stereotype.Service;
import tp3.websocket.EstoqueWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class EstoqueService {

    private final Map<String, Integer> estoque = new ConcurrentHashMap<>();
    private final EstoqueWebSocketHandler webSocketHandler; 

    public EstoqueService(EstoqueWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
        estoque.put("cadeira", 5);
        estoque.put("mesa", 3);
        estoque.put("monitor", 2);
    }

    public Map<String, Integer> listarTodos() {
        return this.estoque;
    }

    public Integer buscarPorNome(String nome) {
        return estoque.get(nome.toLowerCase());
    }

    public synchronized PurchaseResult realizarCompra(String item, int quantidade, String clientId, String origem) {
        String itemChave = item.toLowerCase();
        
        if (!estoque.containsKey(itemChave)) {
            return new PurchaseResult(false, 0);
        }

        int estoqueAtual = estoque.get(itemChave);
        if (estoqueAtual >= quantidade) {
            int novoEstoque = estoqueAtual - quantidade;
            estoque.put(itemChave, novoEstoque);

            dispararEvento("compra_realizada", itemChave, novoEstoque, origem);

            if (novoEstoque <= 1) {
                dispararEvento("estoque_baixo_esgotado", itemChave, novoEstoque, origem);
            }
            
            return new PurchaseResult(true, novoEstoque);
        }

        return new PurchaseResult(false, estoqueAtual);
    }

    public synchronized PurchaseResult cancelarCompra(String item, int quantidade, String clientId, String origem) {
        String itemChave = item.toLowerCase();
        
        if (!estoque.containsKey(itemChave)) {
            return new PurchaseResult(false, 0);
        }

        int novoEstoque = estoque.get(itemChave) + quantidade;
        estoque.put(itemChave, novoEstoque);

        dispararEvento("stock_update", itemChave, novoEstoque, origem);

        return new PurchaseResult(true, novoEstoque);
    }

    private void dispararEvento(String evento, String item, int estoqueRestante, String origem) {
        String json = String.format("{\"event\": \"%s\", \"item\": \"%s\", \"stock\": %d, \"origin\": \"%s\"}",
                evento, item, estoqueRestante, origem);
        webSocketHandler.notificarTodos(json);
    }

    public static class PurchaseResult {
        private final boolean success;
        private final int remaining;

        public PurchaseResult(boolean success, int remaining) {
            this.success = success;
            this.remaining = remaining;
        }
        public boolean isSuccess() { return success; }
        public int getRemaining() { return remaining; }
    }
}