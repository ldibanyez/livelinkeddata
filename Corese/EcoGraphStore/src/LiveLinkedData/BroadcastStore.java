/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LiveLinkedData;

import Graphs.EcoGraph;
import LiveLinkedData.Network.OperationWrap;
import LiveLinkedData.Network.RespMessage;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.core.Mappings;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Store that sends updates to a list of followers through a direct TCP 
 * connection.
 *
 * @author ibanez-l
 */
public class BroadcastStore extends EcoGraph{

  // Address:Port
  Set<String> followers;
  Server server;
  Client client;

  BroadcastStore(String ident, int port) throws IOException{
  	super(ident);
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
	server.addListener(new Listener() {
	  @Override
	   public void received (Connection connection, Object object) {
		  if (object instanceof OperationWrap) {
			 OperationWrap operation = (OperationWrap)object;
             //TODO: Fix this
			 //applyEffect(operation.op);
			 RespMessage r = new RespMessage();
			 r.text = "Applied";
			 connection.sendTCP(r);
		  }
		 }
	});
	System.out.println(port);
	server.bind(port);
	server.start();

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
  
  @Override
  public Mappings query(String qry) throws EngineException{
  	return this.query(qry, false);
  }

  @Override
  public Mappings query(String qry, boolean tagQuery) throws EngineException{
	

	Mappings map = super.query(qry, tagQuery);
	if(map.getQuery().isUpdate()){
	  for(String follower : followers){
		try {
		  System.out.println("Connecting to "+follower);
		  String[] s = follower.split(":");
		  client.connect(5000, s[0], Integer.parseInt(s[1]));
		  OperationWrap request = new OperationWrap();
          // TODO: Fix this
		  //request.op = this.getLastOperation();
		  System.out.println("Sending "+request.op);
		  client.sendTCP(request);
		} catch (Exception ex) {
		  //TODO, better logging
		  ex.printStackTrace();
		}
	  }
	}
	return map;
  }

  
  public void close(){
  	server.close();
  }
}
