package store;

import interfaces.IProductSystem;
import shared.logic.RMIServer;
import shared.utils.OptionalList;

import java.util.List;

public class MarketRunner {
    @SuppressWarnings("Duplicates")
    public static void main(String[] args) {
        OptionalList<String> argv = new OptionalList<>(List.of(args));
        boolean verbose = argv.containsAny(List.of("-v","--verbose"));
        String fileName = argv.findNextOrElse( "--path","No path supplied");
        String authHost = argv.findNextOrElse( "--auth","localhost" );
        System.out.println("Auth host: " + authHost);
        System.out.println("Product file: " + fileName);
        System.out.println("Verbose: " + verbose);
        try {
            Store store = new Store(fileName);
            ProductSystem system = new ProductSystem(store,authHost,verbose);
            RMIServer<ProductSystem, IProductSystem> server = new RMIServer<>(() -> system);
            System.out.println("Rebinding Market...");
            server.createAndRebind("Market");
            System.out.println("Server started...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
