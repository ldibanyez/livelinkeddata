/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LiveLinkedData;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

/**
 *
 * @author ibanez-l
 */
public class Network {

// This class is a convenient place to keep things common to both the client and server.

        // This registers objects that are going to be sent over the network.
        static public void register (EndPoint endPoint) {
                Kryo kryo = endPoint.getKryo();
                kryo.register(RespMessage.class);
                kryo.register(OperationWrap.class);
        }


        static public class RespMessage {
                public String text;
        }
  static public class OperationWrap{
  	String op;
  }

}
