package shared.logic;

import shared.functional.NullaryFunction;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer<Implementer extends Interface,Interface extends Remote> {

    NullaryFunction<Implementer> createRemoteObject;

    public RMIServer(NullaryFunction<Implementer> createRemoteObject) {
        this.createRemoteObject = createRemoteObject;
    }

    public Interface createStub() throws Exception {
        Implementer obj = this.createRemoteObject.apply();
        @SuppressWarnings("unchecked")
        Interface stub = (Interface) UnicastRemoteObject.exportObject(obj,0);
        return stub;
    }

    public void registerStub( String name , Interface stub ) throws Exception {
        Registry registry = LocateRegistry.getRegistry();
        registry.bind(name, stub);
    }

    public void registerStub( String name , Interface stub , int port ) throws Exception {
        Registry registry = LocateRegistry.getRegistry(port);
        registry.bind(name, stub);
    }

    public void createAndRegister( String name ) throws Exception{
        this.registerStub(name , this.createStub() );
    }

    public void createAndRegister( String name , int port ) throws Exception{
        this.registerStub(name , this.createStub() , port );
    }
}
