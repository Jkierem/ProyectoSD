package shared.logic;

import shared.functional.NullaryFunction;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer<Implementer extends Interface,Interface extends Remote> {

    private NullaryFunction<Implementer> createRemoteObject;

    public RMIServer(NullaryFunction<Implementer> createRemoteObject) {
        this.createRemoteObject = createRemoteObject;
    }

    private Interface createStub() throws Exception {
        Implementer obj = this.createRemoteObject.apply();
        @SuppressWarnings("unchecked")
        Interface stub = (Interface) UnicastRemoteObject.exportObject(obj,0);
        return stub;
    }

    private void rebindStub( String name , Interface stub ) throws Exception {
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind(name, stub);
    }

    private void rebindStub( String name , Interface stub , int port ) throws Exception {
        Registry registry = LocateRegistry.getRegistry(port);
        registry.rebind(name, stub);
    }

    private void bindStub( String name , Interface stub ) throws Exception {
        Registry registry = LocateRegistry.getRegistry();
        registry.bind(name, stub);
    }

    private void bindStub( String name , Interface stub , int port ) throws Exception {
        Registry registry = LocateRegistry.getRegistry(port);
        registry.bind(name, stub);
    }

    public void createAndBind( String name ) throws Exception{
        this.bindStub(name , this.createStub() );
    }

    public void createAndBind( String name , int port ) throws Exception{
        this.bindStub(name , this.createStub() , port );
    }

    public void createAndRebind( String name ) throws Exception{
        this.rebindStub(name , this.createStub() );
    }

    public void createAndRebind( String name , int port ) throws Exception{
        this.rebindStub(name , this.createStub() , port );
    }

}
