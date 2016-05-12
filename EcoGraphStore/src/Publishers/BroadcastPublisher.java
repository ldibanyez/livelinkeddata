/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Publishers;

import LiveLinkedData.Network;
import Publishers.Network.RespMessage;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Broadcast to all followers
 * Uses Kryo library
 * @author luisdanielibanesgonzalez
 * 
 * TODO: With this new pattern of publisher and proxy, now the publisher 
 * implements the server and the client the proxy...
 */
public class BroadcastPublisher {
    
  // Should be the same id of the graph.
  final String URI; 
  // Address:Port
  Set<String> followers;
  Server server;
  Client client;

  BroadcastPublisher(String uri, int port) throws IOException{
    URI = uri;
	followers = new HashSet<>();
    server = new Server() {
	  @Override
		protected Connection newConnection () {
				// By providing our own connection implementation, we can store per
				// connection state without a connection ID to state look up.
				return new BroadConnection();
		}
	};
	Network.register(server);
    /*
	server.addListener(new Listener() {
	  @Override
	   public void received (Connection connection, Object object) {
		  if (object instanceof OperationWrap) {
			 OperationWrap operation = (OperationWrap)object;
			 applyEffect(operation.op);
			 RespMessage r = new RespMessage();
			 r.text = "Applied";
			 connection.sendTCP(r);
		  }
		 }
	});
	System.out.println(port);
	server.bind(port);
	server.start();
   */
    // Client Init;
	client = new Client();
	Network.register(client);
	client.addListener(new Listener() {
	  public void received (Connection connection, Object object) {
		  if (object instanceof RespMessage) {
		  RespMessage response = (RespMessage)object;
		  System.out.println(response.text);
		  }
	  }
	});
	client.start();
  }

// This holds per connection state.
static class BroadConnection extends Connection {
     public String name;
}

  public void addFollower(String addr){
  
  	followers.add(addr);
  }
	
  public Set<String> getFollowers(){
  	return followers;
  }
  
  public void close(){
  	server.close();
  }
}
