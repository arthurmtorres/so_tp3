package tp3.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import tp3.service.EstoqueService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class EstoqueGraphQLController {

    private final EstoqueService estoqueService;

    public EstoqueGraphQLController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @QueryMapping
    public List<Item> items() {
        return estoqueService.listarTodos().entrySet().stream()
                .map(entry -> new Item(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @QueryMapping
    public Item getItem(@Argument String name) {
        Integer stock = estoqueService.buscarPorNome(name);
        if (stock != null) {
            return new Item(name, stock);
        }
        return null;
    }

    @MutationMapping
    public EstoqueService.PurchaseResult purchase(@Argument String item, @Argument int quantity, @Argument String clientId) {
        return estoqueService.realizarCompra(item, quantity, clientId, "GraphQL");
    }

    @MutationMapping
    public EstoqueService.PurchaseResult cancel(@Argument String item, @Argument int quantity, @Argument String clientId) {
        return estoqueService.cancelarCompra(item, quantity, clientId, "GraphQL");
    }

    public record Item(String name, int stock) {}
}