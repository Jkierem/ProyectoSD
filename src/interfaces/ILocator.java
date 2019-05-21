package interfaces;

import models.Resource;

import java.rmi.Remote;
import java.util.ArrayList;

public interface ILocator extends Remote {
    boolean register(String ip , ArrayList<Integer> inventory);
    ArrayList<String> resourceQuery( int resourceId );
    ArrayList<Resource> getResources();
}
