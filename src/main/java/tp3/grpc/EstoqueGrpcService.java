package tp3.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import tp3.service.EstoqueService;

import java.util.Map;

@GrpcService
public class EstoqueGrpcService extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final EstoqueService estoqueService;

    public EstoqueGrpcService(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @Override
    public void listItems(Empty request, StreamObserver<ItemList> responseObserver) {
        ItemList.Builder listBuilder = ItemList.newBuilder();
        
        for (Map.Entry<String, Integer> entry : estoqueService.listarTodos().entrySet()) {
            listBuilder.addItems(ItemReply.newBuilder()
                    .setName(entry.getKey())
                    .setStock(entry.getValue())
                    .build());
        }

        responseObserver.onNext(listBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getItem(ItemRequest request, StreamObserver<ItemReply> responseObserver) {
        Integer stock = estoqueService.buscarPorNome(request.getId());
        
        if (stock != null) {
            ItemReply reply = ItemReply.newBuilder()
                    .setName(request.getId())
                    .setStock(stock)
                    .build();
            responseObserver.onNext(reply);
        } else {
            responseObserver.onNext(ItemReply.newBuilder().setName(request.getId()).setStock(0).build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void purchase(PurchaseRequest request, StreamObserver<PurchaseReply> responseObserver) {
        EstoqueService.PurchaseResult result = estoqueService.realizarCompra(
                request.getItem(), 
                request.getQuantity(), 
                request.getClientId(), 
                "gRPC"
        );

        PurchaseReply reply = PurchaseReply.newBuilder()
                .setSuccess(result.isSuccess())
                .setRemaining(result.getRemaining())
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void cancel(PurchaseRequest request, StreamObserver<PurchaseReply> responseObserver) {
        EstoqueService.PurchaseResult result = estoqueService.cancelarCompra(
                request.getItem(), 
                request.getQuantity(), 
                request.getClientId(), 
                "gRPC"
        );

        PurchaseReply reply = PurchaseReply.newBuilder()
                .setSuccess(result.isSuccess())
                .setRemaining(result.getRemaining())
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}