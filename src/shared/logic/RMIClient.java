package shared.logic;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Optional;

public class RMIClient<Interface extends Remote> {

    private String host;

    public RMIClient(String host) {
        this.host = host;
    }

    public Optional<Interface> getStub(String name){
        try {
            Registry registry = LocateRegistry.getRegistry(this.host);
            @SuppressWarnings("unchecked")
            Interface stub = (Interface) registry.lookup(name);
            return Optional.of(stub);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
