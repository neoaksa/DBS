package edu.gvsu.cis;

/**
 * <p>Title: Lab2</p>
 * <p>Description: Old School Instant Messaging Application </p>
 * @author Jonathan Engelsma
 * @version 1.0
 */

import org.zeromq.ZMQ;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import java.net.UnknownHostException;

import static java.lang.Thread.sleep;

public class MyPresenceServer implements PresenceService {
	
	Hashtable<String,RegistrationInfo> regData;
	pubThread srvpub;
	StringBuilder msg = null;

	public MyPresenceServer() {
		super();
		this.regData = new Hashtable<String,RegistrationInfo>();
		this.srvpub = new pubThread();
		Thread t = new Thread(this.srvpub);
		t.start();
	}

	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
		    // Have the server start the RMI Registry
            LocateRegistry.createRegistry(1099);

            // create the presence service object and bind in RMI.
			String name = "PresenceService";
			PresenceService engine = new MyPresenceServer();
			PresenceService stub =
				(PresenceService) UnicastRemoteObject.exportObject(engine, 0);
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind(name, stub);
			System.out.println("PresenceServiceEngine bound");
			Object o = new Object();
			synchronized (o) {
				o.wait();
			}
		} catch (Exception e) {
			System.err.println("PresenceServiceEngine exception:");
			e.printStackTrace();
		}
	}

	public Vector<RegistrationInfo> listRegisteredUsers() throws RemoteException {
		System.out.println("in listRegisteredUsers");
		Set<String> keys = this.regData.keySet();
		Vector<RegistrationInfo> retVal = new Vector<RegistrationInfo>();
		for(String key: keys) {
			retVal.add(this.regData.get(key));
		}
		return retVal;
	}

	public RegistrationInfo lookup(String name) throws RemoteException {
		System.out.println("Lookup");
		RegistrationInfo entry = this.regData.get(name);
		return entry;
	}

	public boolean register(RegistrationInfo reg) throws RemoteException {
		if(!this.regData.containsKey(reg.getUserName())) {
			this.regData.put(reg.getUserName(), reg);
			return true;
		} else {
			return false;
		}
	} 

	public void unregister(String userName) throws RemoteException {
		this.regData.remove(userName);
	}

	public boolean updateRegistrationInfo(RegistrationInfo reg)
	throws RemoteException {
		if(this.regData.get(reg.getUserName()) != null) {
			this.regData.put(reg.getUserName(), reg);
			return true;
		} else {
			return false;
		}
		
	}
	public void broadcast(String msg) throws RemoteException{
		System.out.println("in broadcast");
		this.msg = new StringBuilder(msg);
	}

	//publish class
	class pubThread implements Runnable{

		/**
		 * Thread's entry point.
		 */
		public void run() {

			try (ZMQ.Context context = ZMQ.context(1)) {
				// Socket to talk to clients
				ZMQ.Socket socket = context.socket(ZMQ.PUB);
				try {
					String myHost = InetAddress.getLocalHost().getHostAddress();
					socket.bind("tcp://"+myHost+":9999");
					System.out.println("Successfully bind to ZMQ pub.");
				}catch(UnknownHostException e){
					System.out.println("unknown host Exception!");
				}
				while(true){
					try{
						Thread.sleep(1000);
					} catch (InterruptedException e){}
//					System.out.println(MyPresenceServer.this.msg);
					if(MyPresenceServer.this.msg!=null) {

//						System.out.println("msg:"+MyPresenceServer.this.msg);
						socket.sendMore ("A");
						socket.send(MyPresenceServer.this.msg.toString());
						MyPresenceServer.this.msg = null;
					}
				}
			}
		}
	}
}


