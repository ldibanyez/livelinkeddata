/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Operations;

import Operations.SimpleQuad;
import Operations.SimpleTriple;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author ibanez-l
 * QuadOperation with Simple triples inserted (SU-Set's trick to save space)
 */
public class QuadOperationOpt {

	final String replica;
	final int timestamp;
	// the 'Inserted' are simple triples to save space (SU-set's trick)
	final Set<SimpleTriple> toInsert;
	final Set<SimpleQuad> toDelete;

	QuadOperationOpt(String r, int i, Set<SimpleTriple> ins, Set<SimpleQuad> dels){
		replica = r;
		timestamp = i;
		toInsert = ins;
		toDelete = dels; 
	}

	public String getReplica(){ 
	  String r = "";
	  return r.concat(replica);}
	
	public int getTimestamp(){ 
	  int ts = 0;
	  ts += timestamp;
	  return ts;}

	public Set<SimpleTriple> getInsert(){
		return new HashSet<>(toInsert);
	}

	public Set<SimpleQuad> getDelete(){
		return new HashSet<>(toDelete);
	}

	public static QuadOperationOpt deserialize(String ser){
	
	XStream xst = new XStream(new StaxDriver());
	Object o = xst.fromXML(ser);
	if(!(o instanceof QuadOperationOpt)){
	  throw new ClassCastException("Invalid QuadOperation serialization");
	}

	return (QuadOperationOpt) o;
	}

	public String serialize(){
	  XStream xst = new XStream(new StaxDriver());
	  return xst.toXML(this); 
	}

	public boolean sameInsert(QuadOperationOpt qo){
		return Objects.equals(this.toInsert, qo.toInsert);
	}

	public boolean sameDelete(QuadOperationOpt qo){
		return Objects.equals(this.toDelete, qo.toDelete);
	}

  @Override
  public int hashCode() {
	int hash = 7;
	hash = 71 * hash + Objects.hashCode(this.replica);
	hash = 71 * hash + this.timestamp;
	return hash;
  }

  // Only one operation must exist with the same replica and timestamp
  @Override
  public boolean equals(Object obj) {
	if (obj == null) {
	  return false;
	}
	if (getClass() != obj.getClass()) {
	  return false;
	}
	final QuadOperationOpt other = (QuadOperationOpt) obj;
	if (!Objects.equals(this.replica, other.replica)) {
	  return false;
	}
	if (this.timestamp != other.timestamp) {
	  return false;
	}
	return true;
  }

	
  
  }


  
