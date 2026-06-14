package tp3.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tp3.service.EstoqueService;

import java.util.Map;

@RestController
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @GetMapping("/health")
    public String health() {
        return "OK - Serviço Ativo";
    }

    @GetMapping("/items")
    public Map<String, Integer> getItems() {
        return estoqueService.listarTodos();
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<Integer> getItem(@PathVariable String id) {
        Integer stock = estoqueService.buscarPorNome(id);
        if (stock != null) {
            return ResponseEntity.ok(stock);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/purchase")
    public EstoqueService.PurchaseResult purchase(@RequestBody PurchaseRequest request) {
        // Passando "REST" como origem
        return estoqueService.realizarCompra(request.item(), request.quantity(), request.clientId(), "REST");
    }

    @PostMapping("/cancel")
    public EstoqueService.PurchaseResult cancel(@RequestBody PurchaseRequest request) {
        // Passando "REST" como origem
        return estoqueService.cancelarCompra(request.item(), request.quantity(), request.clientId(), "REST");
    }

    public record PurchaseRequest(String item, int quantity, String clientId) {}
}