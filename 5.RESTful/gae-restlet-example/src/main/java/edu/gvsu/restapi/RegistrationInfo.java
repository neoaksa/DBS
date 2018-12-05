
package edu.gvsu.restapi;

/**
 * <p>Title: Lab2</p>
 * <p>Description: Old School Instant Messaging Application </p>
 * @author Jonathan Engelsma
 * @version 1.0
 */
import java.io.*;
import org.json.JSONObject;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import org.json.JSONObject;
/**
 * This class represents the information that the chat client registers
 * with the presence server.
 */

@Entity
public class RegistrationInfo implements Serializable
{
    @Id private String userName;
    private String host;
    private boolean status;
    private int port;

    /**
     * RegistrationInfo  constructor.
     * @param uname Name of the user being registered.
     * @param h Name of the host their client is running on.
     * @param p The port # their client is listening for connections on.
     * @param s The status, true if the client is available, false otherwise.
     */
//    public RegistrationInfo(String uname, String h, int p, boolean s)
//    {
//        this.userName = uname;
//        this.host = h;
//        this.port = p;
//        this.status = s;
//    }


    //return a jason object
    public JSONObject toJSON() {
        try{
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("userName", this.userName);
            jsonobj.put("host", this.host);
            jsonobj.put("status", this.status);
            jsonobj.put("port", this.port);
            return jsonobj;
        }catch(Exception e){
            return null;
        }
    }
    /**
     * Convert this object into an HTML representation.
     * @param fragment if true, generate an html fragment, otherwise a complete document.
     * @return an HTML representation.
     */
    public String toHtml(boolean fragment)
    {
        String retval = "";
        if(fragment) {
            StringBuffer sb = new StringBuffer();
            sb.append("<b>UserName:</b> ");
            sb.append(this.userName);
            sb.append("<b> Host: </b>");
            sb.append(host);
            sb.append("<b> Port: </b>");
            sb.append(port);
            sb.append("<b> Status: </b>");
            sb.append(status);
            sb.append(" <a href=\"/users/" + this.userName + "\">View</a>");
            sb.append("<br/>");
            retval = sb.toString();
        } else {
            StringBuffer sb = new StringBuffer("<html><head><title>Users</title></head><body><h1>User</h1>");
            sb.append("<b>UserName:</b> ");
            sb.append(this.userName + "<br/>");
            sb.append("<b>Host:</b> ");
            sb.append(this.host + "<br/>");
            sb.append("<b>Port:</b> ");
            sb.append(this.port + "<br/>");
            sb.append("<b> Status: </b>");
            sb.append(status + "<br/>");
            sb.append("<br/><br/>Return to <a href=\"/users\">All Users<a>.</body></html>");
            retval = sb.toString();
        }
        return retval;
    }

    /**
     * Determine the name of the user.
     * @return The name of the user.
     */
    public String getUserName()
    {
        return this.userName;
    }

    /**
     * Determine the host the user is on.
     * @return The name of the host client resides on.
     */
    public String getHost()
    {
        return this.host;
    }

    /**
     * Get the port the client is listening for connections on.
     * @return port value.
     */
    public int getPort()
    {
        return this.port;
    }
    
    /**
     * Get the status of the client - true means availability, false means don't disturb.
     * @return status value.
     */
    public boolean getStatus()
    {
    	return this.status;
    }

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
    
}