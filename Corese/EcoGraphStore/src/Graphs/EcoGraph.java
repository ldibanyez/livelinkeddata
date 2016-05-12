/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphs;

import Operations.IVOperation;
import Operations.SimplePenta;
import Operations.SimpleQuad;
import Operations.SimpleTriple;
import Proxys.Proxy;
import Publishers.Publisher;
import Vectors.Interval;
import Vectors.Interval.IntervalException;
import Vectors.IntervalSequence;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.api.core.Entity;
import fr.inria.edelweiss.kgram.core.Mappings;
import fr.inria.edelweiss.kgraph.core.EdgeImpl;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ibanez-l
 * Eventually consistent graph with interval vectors
 * 
 */
public class EcoGraph {

  Graph g;
  // Promise to be unique
  String id;
  HashMap<String,IntervalSequence> vector;
  IVTagger tagger;
  OperationListener listener;

  //upstream
  List<Proxy> proxies;
  
  //downstream
  IVOperation lastop;
  Publisher publisher;

  public EcoGraph(String ident){
  	g = Graph.create();
	g.init();
	g.setTag(true);
	id = ident;
	this.vector = new HashMap<>();
	IntervalSequence inseq = new IntervalSequence();
	inseq.add(0);
	this.vector.put(id,inseq);
	lastop = null;
	tagger = new IVTagger(id);
	g.setTagger(tagger);
    listener = new OperationListener();
    g.addListener(listener);
  }

  public void setPublisher(Publisher p){
      publisher = p;
  }

  public void setLastOp(IVOperation op){
    lastop = op;
  }

  public void addProxy(Proxy p){
    proxies.add(p);
  }

  public Mappings query(String qry) throws EngineException{
  	return this.query(qry, false);
  }

  // Intercept and rewrite SPARQL Update to SUSET
  public Mappings query(String qry, boolean tagQuery) throws EngineException{

	// If is a tuple() query, we need to turn off the tagging
	if(tagQuery){
	g.setTag(false);}
	
	QueryProcess exec = QueryProcess.create(g);
	Mappings m = exec.query(qry);

    // Bug of Corese 3.1, workaround proposed by Olivier in the meantime
	//if(m.getQuery().isUpdate() || m.getQuery().isDelete()){
    if(exec.getAST(m).isSPARQLUpdate()){
	
	// Intercepted Del!
	// As the work is already done in Corese, we only pass to the serialization
	// the lastCRDTDelete, if a refactor is done in Corese to have the triples 
	// before, executing, then we will have something here
	List<SimplePenta> del = new ArrayList<>();
	if(!(listener.getLastDeleted().isEmpty())){
        for (Iterator<Entity> it = listener.getLastDeleted().iterator(); it.hasNext();) {
            Entity ent = it.next();
            // getLabel does not work here, toString makes the right entre-guimee
            SimplePenta p = new SimplePenta(ent);
            del.add(p);
        }
	}

	//Intercepted Insert!
	List<SimpleQuad> ins = new ArrayList<>();
	if(!listener.getLastInserted().isEmpty()){
      // The counter in tagger must be the same as this clock
      EntTagComparator entcomp = new EntTagComparator();
      List<Entity> lastCRDT = new ArrayList<>(listener.getLastInserted());
      //System.out.println(lastCRDT.toString());
      Collections.sort(lastCRDT,entcomp);
	  for(Entity ent: lastCRDT){
        if(vector.containsKey(getAuthorSite(ent))){  
        vector.get(getAuthorSite(ent)).add(getClockTick(ent));
        }else {
        vector.put(getAuthorSite(ent), new IntervalSequence());
        vector.get(getAuthorSite(ent)).add(getClockTick(ent));
        }
        //vector.get(id).add(getClockTick(ent));
		SimpleQuad q = new SimpleQuad(ent); 
		ins.add(q); 
	  }
	}

	// XML serialization
	lastop = new IVOperation(id,vector.get(id).max(), ins,del);
    if(publisher != null){ publisher.handle(lastop);}

	}
	g.setTag(true);
	return m;
  }

  // Downstream
  public IVOperation getLastOperation(){
    //TODO: Defensive Copy
	return lastop;
  }

  String getAuthorSite(Entity ent){
    String tag = ent.getNode(2).getLabel();
    String[] arr = tag.split("#");
    return arr[0];
  }

  int getClockTick(Entity ent){
    String tag = ent.getNode(2).getLabel();
    String[] arr = tag.split("#");
    return Integer.parseInt(arr[1]);
  }


  public void applyEffect(Operations.Operation upstream){
	// No need to turn off tags, as the delete is issued with the
	// exact triple
	//g.setTag(false);
	QueryProcess exec = QueryProcess.create(g);
  
    if(publisher != null){ publisher.handle(upstream);}
	for(SimplePenta e : upstream.getDelete()){
	    String tag = e.getTag();
		String[] arr = tag.split("#");
		if(this.hasSeen(arr[0], Integer.parseInt(arr[1]))){
		  String delTup = "DELETE DATA {"
                  + "GRAPH <" + e.getGraph() + ">" 
                  + " {tuple("
				  + e.getPredicate() +" "
				  + e.getSubject() + " "
				  + e.getObject() + " "
				  + "'" + tag + "')"
				  + "}}";
            try {
              exec.query(delTup);
            } catch (EngineException ex) {
              ex.printStackTrace();
            }
		} else {
		  this.markSeen(arr[0], Integer.parseInt(arr[1]));
		}
	}


	  for(SimplePenta e: upstream.getInsert()){
	    String tag = e.getTag();
		String[] arr = tag.split("#");
		if(!this.hasSeen(arr[0], Integer.parseInt(arr[1]))){
		  String insTup = "INSERT DATA{ "
                  + "GRAPH <" + e.getGraph() + ">" 
                  + "{tuple("
				  + e.getPredicate() +" "
				  + e.getSubject() + " "
				  + e.getObject() + " "
				  + "'" + tag + "')"
				  + "}}";
		try {
		  exec.query(insTup);
		  this.markSeen(arr[0], Integer.parseInt(arr[1]));
		} catch (EngineException ex) {
		  ex.printStackTrace();
		}
	  }
	}
      // Acknowledge that I received something from a replica
      // Even if it was only transit.
	  //this.markSeen(op.replica,op.lasttick);
  
  }

  public Graph getGraph(){ return g;}

  // Defensive Copy of the vector
  public Map<String,IntervalSequence> getVector(){

	HashMap<String,IntervalSequence> vectorCopy = new HashMap<>();
	for(String key : vector.keySet()){
	  IntervalSequence copy = new IntervalSequence();
	  for (Interval inter : vector.get(key)) {
		try {
		  Interval interCopy = new Interval(inter.getLowerLimit()
					  ,inter.getUpperLimit());
		  copy.addInterval(interCopy);
		} catch (IntervalException ex) {
		  Logger.getLogger(EcoGraph.class.getName()).log(Level.SEVERE, null, ex);
		}
	  }
	  vectorCopy.put(key,copy);
	}

	return vectorCopy;
  }

  // Abstract cardinality of all the graph
  // Remember that cardinality of the default graph
  public int cardinality(){
	String select = "SELECT DISTINCT * WHERE {GRAPH ?g {?s ?p ?o}}";
	QueryProcess exec = QueryProcess.create(g);
	try {
	  Mappings m = exec.query(select);
	  return m.size();
	} catch (EngineException ex) {
	  Logger.getLogger(EcoGraph.class.getName()).log(Level.SEVERE, null, ex);
	}

	return -1;
  }
  
  public int cardinalityOfGraph(String graph){
	String select = "SELECT DISTINCT * "
            + "FROM " + graph  
            + " WHERE {?s ?p ?o}";
	QueryProcess exec = QueryProcess.create(g);
	try {
	  Mappings m = exec.query(select);
	  return m.size();
	} catch (EngineException ex) {
	  Logger.getLogger(EcoGraph.class.getName()).log(Level.SEVERE, null, ex);
	}

	return -1;
  }

  // Payload size (CRDT)
  public int size(){
	return g.size(); 
  }

  public int sizeOfGraph(String graph){
    g.setTag(false);
	String select = "SELECT * "
            + "FROM " + graph  
            + " WHERE {tuple(?s ?p ?o ?tag)}";
	QueryProcess exec = QueryProcess.create(g);
	try {
	  Mappings m = exec.query(select);
      g.setTag(false);
	  return m.size();
	} catch (EngineException ex) {
	  Logger.getLogger(EcoGraph.class.getName()).log(Level.SEVERE, null, ex);
	}

	return -1;
  }
  // Abstract equivalence of graphs
  // Done this awfully because compare is broken
  // TODO, implement a new one for the same store.
  public boolean sameGraph(EcoGraph eg){

	QueryProcess exec1 = QueryProcess.create(g);
	QueryProcess exec2 = QueryProcess.create(eg.g);
	String select = "CONSTRUCT {?s ?p ?o} WHERE "
			+ "{ ?s ?p ?o }";
	try {
	  Mappings m1 = exec1.query(select);
	  Mappings m2 = exec2.query(select);
	  Graph copy1 = exec1.getGraph(m1);
	  Graph copy2 = exec2.getGraph(m2);
	  if(copy1.size() != copy2.size()) {return false;}

	  Iterable<Entity> triples1 = copy1.getEdges(); 
	  for(Entity ent : triples1){
	  	if(!copy2.exist((EdgeImpl) ent))
		{ 
		return false;}
	  }
	  return true;

	} catch (EngineException ex) {
	  ex.printStackTrace();
	  return false;
	}

	
  }

  public boolean compare(EcoGraph eg){
	// Is broken with quads !!
  	return g.compare(eg.g);
  }

  // CRDT equivalence of payloads
  public boolean samePayload(EcoGraph eg){

	  //System.out.println(g.size());
	  //System.out.println(eg.g.size());
	  if(g.size() != eg.g.size()) {return false;}

	  Iterable<Entity> triples1 = eg.g.getEdges(); 
	  for(Entity e : triples1){
	  	if(!g.exist((EdgeImpl) e))
		{ return false;}
	  }
	  return true;
  
  }

  public boolean sameVector(EcoGraph eg){

	if(!vector.keySet().equals(eg.vector.keySet())){
		return false;
	}
	for(String key : eg.vector.keySet()){
	  IntervalSequence here = vector.get(key);
	  IntervalSequence there = eg.vector.get(key);
		if(!here.equals(there)){
			return false;
		}
	}
	return true;
  }

  // Same payload and same vector
  public boolean equivalent(EcoGraph eg){
	return this.sameVector(eg) && this.samePayload(eg);
  
  }

  public String display() {
  	return  id + "\n" +
			vector.toString() + "\n" +
			g.display(); 
  }
  
  // Copied from IVSet
  // TODO: refactor into helper or into IntervalSequence
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

  @Override
  public int hashCode() {
	int hash = 5;
	hash = 83 * hash + Objects.hashCode(this.id);
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
	final EcoGraph other = (EcoGraph) obj;
	if (!Objects.equals(this.id, other.id)) {
	  return false;
	}
	return true;
  }

  
  // Methods for testing
  
}
