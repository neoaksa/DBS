package chatServer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import chat.PresenceService;
import chat.RegistrationInfo;
import java.util.Vector;
import java.util.Iterator;


public class chatServer implements PresenceService {

    //save all registed client
    Vector<RegistrationInfo> clients = new Vector<RegistrationInfo>();

    public chatServer() {
        super();
    }
    
    // add a new client
    public boolean register(RegistrationInfo reg) throws RemoteException{
         for(RegistrationInfo client : this.clients){
            if(client.getUserName().equals(reg.getUserName())){
                return false;
            }
        }   
        clients.add(reg);
        return true;
    }
    
    public boolean updateRegistrationInfo(RegistrationInfo reg) throws RemoteException{
        for(RegistrationInfo client : this.clients){
            if(client.getUserName().equals(reg.getUserName())){
                client.setHost(reg.getHost());
                client.setStatus(reg.getStatus());
                return true;
            }
        }
        return false;
    }
    
    public void unregister(String userName) throws RemoteException{
         for(Iterator<RegistrationInfo> iterator = clients.iterator(); iterator.hasNext(); ) {
                  RegistrationInfo value = iterator.next();
                  if(value.getUserName().equals(userName)){
                            iterator.remove();
                  }
         }
    }
    
    public RegistrationInfo lookup(String name) throws RemoteException{
        for(RegistrationInfo client : this.clients){
            if(client.getUserName().equals(name)){
                return client;
            }
        }
        return null;
    }
    
    public Vector<RegistrationInfo> listRegisteredUsers() throws RemoteException{
        return this.clients;
    }

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "chatServer";
            PresenceService engine = new chatServer();
            PresenceService stub =
                    (PresenceService) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("chatServer bound");
        } catch (Exception e) {
            System.err.println("chatServer exception:");
            e.printStackTrace();
        }
    }
}
