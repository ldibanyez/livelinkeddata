/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Operations;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Operation optimized to work with Interval Vectors
 * Assumes insertions comes from only one replica (the author one)
 * and that tags' format is 'IdReplica#int', where int is a monotonicly 
 * increasing value. Therefore, they can be reconstructed by using only the 
 * last tick of the replica's clock.
 * 
 * @author ibanez-l
 */
public class IVOperation implements Operation{

	final String replica;
    // Timestamp of the last inserted triple
	final int lasttick;
    final List<SimpleQuad> toInsert;
    final List<SimplePenta> toDelete;

	public IVOperation(String r, int i, List<SimpleQuad> ins, List<SimplePenta> dels){
        replica = r;
		lasttick = i;
        toInsert = new ArrayList<>(ins);
        toDelete = new ArrayList<>(dels);
	}

	public String getReplica(){ 
	  String r = "";
	  return r.concat(replica);}
	
	public int getTimestamp(){ 
	  int ts = 0;
	  ts += lasttick;
	  return ts;
    }

    // Construct the pentas from the lasttick
    @Override
	public List<SimplePenta> getInsert(){
        List pentas = new ArrayList<>(); 
        for (int i = 0 ; i< toInsert.size() ; i++){
            SimpleQuad st = toInsert.get(i);
            SimplePenta sq = new SimplePenta(st.getGraph(),st.getSubject(),st.
                getPredicate(),st.getObject(),
                replica + "#" + String.valueOf((lasttick-toInsert.size())+i+1));
            pentas.add(sq);
        }
		return pentas; 
	}

    @Override
	public List<SimplePenta> getDelete(){
		return new ArrayList<>(toDelete);
	}

	public static IVOperation deserialize(String ser){
	
	XStream xst = new XStream(new StaxDriver());
	Object o = xst.fromXML(ser);
	if(!(o instanceof IVOperation)){
	  throw new ClassCastException("Invalid QuadOperation serialization");
	}

	return (IVOperation) o;
	}

    @Override
	public String serialize(){
	  XStream xst = new XStream(new StaxDriver());
	  return xst.toXML(this); 
	}

	public boolean sameInsert(IVOperation qo){
		return Objects.equals(this.toInsert, qo.toInsert);
	}

	public boolean sameDelete(IVOperation qo){
		return Objects.equals(this.toDelete, qo.toDelete);
	}

  @Override
  public int hashCode() {
	int hash = 7;
	hash = 71 * hash + Objects.hashCode(this.replica);
	hash = 71 * hash + this.lasttick;
	return hash;
  }

  // Only one operation must exist with the same replica and lasttick
  @Override
  public boolean equals(Object obj) {
	if (obj == null) {
	  return false;
	}
	if (getClass() != obj.getClass()) {
	  return false;
	}
	final IVOperation other = (IVOperation) obj;
	if (!Objects.equals(this.replica, other.replica)) {
	  return false;
	}
	if (this.lasttick != other.lasttick) {
	  return false;
	}
	return true;
  }

  }


  
