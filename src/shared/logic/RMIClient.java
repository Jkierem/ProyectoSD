package shared.logic;

import shared.exceptions.StubNotFoundException;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient<Interface extends Remote> {

    private String host;

    public RMIClient(String host) {
        this.host = host;
    }

    public Interface getStub(String name) throws StubNotFoundException {
        try {
            Registry registry = LocateRegistry.getRegistry(this.host);
            @SuppressWarnings("unchecked")
            Interface stub = (Interface) registry.lookup(name);
            return stub;
        } catch (Exception e) {
            throw new StubNotFoundException(name , this.host);
        }
    }
}
