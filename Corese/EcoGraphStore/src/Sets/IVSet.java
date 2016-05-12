/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Sets;

import Vectors.IntervalSequence;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author ibanez-l
 * 
 * An IVSet complies with the contract of a set plus methods that are only
 * for the distributed setting and common to all CRDT-Sets:
 * Merge
 * Compare
 * receiveAdd : Receive a CRDT translated add (not a plain add)
 * receiveRel : Receive a CRDT translated remove (not a plain remove)
 * 
 * It is based on the description made by 
 * Optimized OR-Sets without ordering constraints
 * M. Mukund, G. Shenoy and S.P. Suresh
 * TechReport Chennai Mathematical Institute 2013
 * 
 * I wanted first to do an intermediate ORSet abstract class, but a merge
 * between differently implemented CRDT Sets is not defined, need Java expert 
 * to see if I can define abstract methods to be applied solely between members of
 * each implementation (it seems unnatural)
 */
public class IVSet implements Set,Comparable {
  final String id;
  HashSet<IVTuple> payload;
  HashMap<String,IntervalSequence> vector;

  /**
   * Creates a new IVSet with the given Id.
   * @param id
   *   Needs to be unique among all replicas. The behaviour is not specified
   * if you fail to do so.
   */
  public IVSet(String id) {
	this.id = id;
	this.payload = new HashSet();
	this.vector = new HashMap<>();
	IntervalSequence inseq = new IntervalSequence();
	inseq.add(0);
	this.vector.put(id,inseq);
  }

  
  protected IVSet(String id, HashSet<IVTuple> payload, HashMap<String, IntervalSequence> vector) {
	this.id = id;
	this.payload = payload;
	this.vector = vector;
	//TODO: Class invariant validation.
  }

 
  /**
   * 
   * @return
   * Cardinality of this set _not_ of its payload.
   */
  @Override
  public int size() {
	return this.toSet().size();
  }

  @Override
  public boolean isEmpty() {
	return payload.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
	for (IVTuple tuple : payload){
		if(tuple.getElement().equals(o)){return true;}
	}
	return false;
  }

  @Override
  public Iterator iterator() {
	throw new UnsupportedOperationException("Not supported yet.");
  }

  private Set<Object> toSet(){
	HashSet set = new HashSet<>();
	for (IVTuple t : payload){
		if(!set.contains(t.getElement())){
		  set.add(t.getElement());
		}
	}
  	return set;
  }

  @Override
  public Object[] toArray() {
	return this.toSet().toArray();
  }

  @Override
  public Object[] toArray(Object[] ts) {
	throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean add(Object e) {
	if(e == null){
	  throw new NullPointerException("IVSet does not accept nulls");
	}
	if(this.contains(e)){
	  return false;
	}

	IVTuple tup = new IVTuple(e,id,vector.get(id).max()+1);
	payload.add(tup);
	vector.get(id).add(vector.get(id).max()+1);
	return true;
  }

  @Override
  public boolean remove(Object o) {
	//to avoid double traversal of the payload
	boolean present = false;
	Iterator<IVTuple> it;
	it = payload.iterator();
	while(it.hasNext()){
	    IVTuple t = it.next();
		if(t.getElement().equals(o)){
		  present = true;
		  it.remove();
		}
	}
		return present;
	}
	

  @Override
  public boolean containsAll(Collection clctn) {
	throw new UnsupportedOperationException("Not supported yet.");
  }

  // This is not IVSet Union, if clctn is an IVSet it adds the elements in it,
  // not the IVTuples.
  @Override
  public boolean addAll(Collection clctn) {
	if(clctn.contains(null)){
	  throw new NullPointerException("This Collection contains null, IVSet does not accept nulls");
	}else {
	  Set filt = new HashSet();
	  for(Object e : clctn){
		if(!this.contains(e)){
		  filt.add(e);
		}
	  }

	  if(filt.isEmpty()){return false;}

	  HashSet<IVTuple> s = new HashSet<>();
	  for(Object e : filt){
		IVTuple tup = new IVTuple(e,id,vector.get(id).max()+1);
		s.add(tup);
	  }
	  vector.get(id).add(vector.get(id).max()+1);
	  return this.payload.addAll(s);
	}
  }

  @Override
  public boolean retainAll(Collection clctn) {
	throw new UnsupportedOperationException("Not supported yet.");
  }

  // This is not IVSet Difference, see addAll
  @Override
  public boolean removeAll(Collection clctn) {
    boolean change = false;
     for(Object e : clctn){
	   change = change || this.remove(e);
	 }
	return change;
  }

  @Override
  public void clear() {
	this.payload.clear();
  }

  @Override
  public int compareTo(Object t) {
	throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String toString() {
	return "IVSet{" + "id=" + id + ", payload=" + payload.toString() + ", vector=" + vector.toString() + '}';
  }

  @Override
  public int hashCode() {
	int hash = 3;
	hash = 83 * hash + Objects.hashCode(this.id);
	hash = 83 * hash + Objects.hashCode(this.payload);
	hash = 83 * hash + Objects.hashCode(this.vector);
	return hash;
  }

  @Override
  public boolean equals(Object obj) {
	if (obj == null) {
	  return false;
	}
	if (getClass() != obj.getClass()) {
	  return false;
	}
	final IVSet other = (IVSet) obj;
	/*
	if (!Objects.equals(this.id, other.id)) {
	  return false;
	}
	*/
	if (!Objects.equals(this.payload, other.payload)) {
	  return false;
	}
	if (!Objects.equals(this.vector, other.vector)) {
	  return false;
	}
	return true;
  }


  // CRDT methods for the distributed setting

	public boolean merge(IVSet set){
	
      // Removing what the other already deleted but I did not 
	  boolean change = false;
	  Iterator it = this.payload.iterator();
	  while(it.hasNext()){
	  	IVTuple t = (IVTuple)it.next();
		if((set.hasSeen(t.replica,t.timestamp)) && !(set.payload.contains(t)))
		{
		  change = true;
		  it.remove();
		}
	  }

	  // Adding if I have not seen it
	  for(IVTuple t : set.payload){
	  	if(!(this.hasSeen(t.replica,t.timestamp))){
		  	change = true;
			this.payload.add(t);
		}
	  
	  }
	  
	  //Merge Vectors

	  HashSet<String> thisKeys = new HashSet<>(this.vector.keySet());
	  HashSet<String> thatKeys = new HashSet<>(set.vector.keySet());

	  HashSet<String> inBoth = new HashSet<>(thisKeys);
	  inBoth.retainAll(thatKeys);
	  for(String key : inBoth ){
		IntervalSequence inseq = this.vector.get(key);
		if(inseq.union(set.vector.get(key))){
		  this.vector.put(key, inseq);
		  change = true;
		} 
	  }

	  HashSet<String> onlyThat = new HashSet<>(thatKeys);
	  onlyThat.removeAll(thisKeys);
	  
	  for(String key : onlyThat){
		IntervalSequence inseq = set.vector.get(key);
	    this.vector.put(key, inseq);
		change = true;
	  }

	  return change;
	
	}
  
  // Inserts element and returns the 'serialized' downstream operation
  public String insert(Object e){

	if(e == null){
	  throw new NullPointerException("IVSet does not accept nulls");
	}
	if(this.contains(e)){
	  return "";
	}
	IVTuple tup = new IVTuple(e,id,vector.get(id).max()+1);
	payload.add(tup);
	vector.get(id).add(vector.get(id).max()+1);

	// XML serialization
	HashSet<Object> toIns = new HashSet<>();
	toIns.add(e);
	  Operation ins = new Operation(id,vector.get(id).max(),
			  toIns,new HashSet<IVTuple>());
	String ds = ins.serialize();

	return ds;
  
  }

  public String insertFrom(Collection clctn){

	if(clctn.contains(null)){
	  throw new NullPointerException("This Collection contains null, IVSet does not accept nulls");
	}else {
	  HashSet filt = new HashSet();
	  for(Object e : clctn){
		if(!this.contains(e)){
		  filt.add(e);
		}
	  }

	  HashSet<IVTuple> toIns = new HashSet<>();
	  for(Object e : filt){
		IVTuple tup = new IVTuple(e,id,vector.get(id).max()+1);
		toIns.add(tup);
	  }
	  vector.get(id).add(vector.get(id).max()+1);
	  this.payload.addAll(toIns);

	  Operation ins = new Operation(id,vector.get(id).max(),
			  filt,new HashSet<IVTuple>());
	  String ds = ins.serialize();

	  return ds;
	}
  }

  public String delete(Object o){
  
	if(o == null){
	  throw new NullPointerException("IVSet does not accept nulls");
	}

	XStream xst = new XStream(new StaxDriver());
	HashSet<IVTuple> s = new HashSet<>();
	
	Iterator<IVTuple> it;
	it = payload.iterator();
	while(it.hasNext()){
	    IVTuple t = it.next();
		if(t.getElement().equals(o)){
		  s.add(t);
		  it.remove();
		}
	}

	Operation del = new Operation(id,vector.get(id).max(),
			new HashSet<>(),s);
	String ds = del.serialize();
	return ds;

  }

  public String deleteFrom(Collection clctn){
  	
    HashSet toDel = new HashSet();
	for(Object e : clctn){
	  Iterator<IVTuple> it;
	  it = payload.iterator();
	  while(it.hasNext()){
		  IVTuple t = it.next();
		  if(t.getElement().equals(e)){
			toDel.add(t);
			it.remove();
		  }
	  }
	}
	Operation del = new Operation(id,vector.get(id).max(),
			new HashSet<>(),toDel);
	return del.serialize();
  }

  // CRDT version of Clear
  public String deleteAll(){
  
	HashSet<IVTuple> toDel = new HashSet<>(this.payload);
	
	Operation del = new Operation(id,vector.get(id).max()
			,new HashSet<>(),toDel);
	this.clear();
	return del.serialize();
  
  }

  // If this IVSet has seen the insertion ts from replica rep
  protected boolean hasSeen(String rep, int ts){
	if(this.vector.containsKey(rep)){
	  return this.vector.get(rep).contains(ts); 
	}else {
	  return false;
	} 
  }

  // Mark as seen in the interval vector the insertion ts from replica rep
  protected void markSeen(String rep, int ts){
	if(this.vector.containsKey(rep)){
	  this.vector.get(rep).add(ts); 
	}else {
	  IntervalSequence inseq = new IntervalSequence();
	  inseq.add(0);
	  inseq.add(ts);
	  this.vector.put(rep, inseq); 
	} 
  }

  public void applyEffect(String downstream){
	
	XStream xst = new XStream(new StaxDriver());
	Object obj = xst.fromXML(downstream);
	
	if(!(obj instanceof Operation)){
	  throw new ClassCastException("Incorrect Operation XML");
	}

	Operation op = (Operation) obj;

	
	 for(IVTuple t : op.getDelete()){
		if(this.hasSeen(t.getReplica(),t.getTimestamp())){
		  this.payload.remove(t);
		}else{
		  this.markSeen(t.replica,t.timestamp);
		}
		
	 }
	  // This is the problem with multiInsertion if things are pulled partially
	  //If i receive a bulk, i will ignore it thinking in double reception.
	 // Partial pattern operations can have problems.
	  if(this.hasSeen(op.getReplica(),op.getTimestamp())){ return;}
	  
	  for(Object o : op.getInsert()){
		  IVTuple t = new IVTuple(o,op.replica,op.timestamp);
		  this.payload.add(t);
		  this.markSeen(op.replica, op.timestamp); 
	  }	
  
  }
  
}
