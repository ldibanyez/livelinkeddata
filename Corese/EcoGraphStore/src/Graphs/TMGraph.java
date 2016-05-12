/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphs;

import Operations.IRIMatcher;
import Operations.SimplePenta;
import Operations.TMOperation;
import Provenance.TrioMonoid;
import Proxys.Proxy;
import Publishers.Publisher;
import Vectors.IntervalSequence;
import fr.inria.acacia.corese.api.IDatatype;
import fr.inria.acacia.corese.cg.datatype.DatatypeMap;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.acacia.corese.triple.parser.NSManager;
import fr.inria.edelweiss.kgram.api.core.Entity;
import fr.inria.edelweiss.kgram.api.core.Node;
import fr.inria.edelweiss.kgram.core.Mapping;
import fr.inria.edelweiss.kgram.core.Mappings;
import fr.inria.edelweiss.kgraph.core.EdgeImpl;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import fr.inria.edelweiss.kgtool.load.Load;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ibanez-l
 * SemiRing Annotated Graph
 * Assumes causal delivery from the feed
 * 
 */
public class TMGraph {

  Graph g;
  // Promise to be unique
  String id;
  //HashMap<String,IntervalSequence> vector;
  // Logical Clock
  int lclock;
  TMTagger tagger;
  OperationListener listener;

  //upstream
  List<Proxy> proxies;
  
  //downstream
  TMOperation lastop;
  Publisher publisher;

  public TMGraph(String ident){
  	g = Graph.create();
	g.init();
	g.setTag(true);
	id = ident;
    lclock = 0;
	IntervalSequence inseq = new IntervalSequence();
	inseq.add(0);
	lastop = null;
	tagger = new TMTagger(id);
	g.setTagger(tagger);
    listener = new OperationListener();
    g.addListener(listener);
  }

  public void setPublisher(Publisher p){
      publisher = p;
  }

  public Publisher getPublisher(){
    return publisher;
  }

    public String getId() {
        return id;
    }


  public void addProxy(Proxy p){
    proxies.add(p);
  }

  public void copy(Graph toUnion){
      this.g.copy(toUnion);  
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
	List<SimplePenta> ins = new ArrayList<>();
	if(!listener.getLastInserted().isEmpty()){
      //EntTagComparator entcomp = new EntTagComparator();
      List<Entity> lastCRDT = new ArrayList<>(listener.getLastInserted());
      //System.out.println(lastCRDT.toString());
      //Collections.sort(lastCRDT,entcomp);
	  for(Entity ent: lastCRDT){
        SimplePenta q = new SimplePenta(ent);
		ins.add(q); 
        lclock += 1;
	  }
	}

	// XML serialization
	//lastop = new IVOperation(id,vector.get(id).max(), ins,del);
	lastop = new TMOperation(id,ins,del);
    if(publisher != null){ publisher.handle(lastop);}

	}
	g.setTag(true);
	return m;
  }

  // Downstream
  public TMOperation getLastOperation(){
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

  TrioMonoid getTrioMonoid(Entity ent){
    String tag = ent.getNode(2).getLabel();
    return new TrioMonoid(tag);
  }

  public void applyEffect(Operations.TMOperation upstream){
	// No need to turn off tags, as the delete is issued with the
	// exact triple
	//g.setTag(false);
	QueryProcess exec = QueryProcess.create(g);
  
	if(!this.hasSeen(upstream)){

	for(SimplePenta toDel : upstream.getDelete()){
          TrioMonoid poly = new TrioMonoid(toDel.getTag());
          poly.invert();
            String predicate = toDel.getPredicate();
            String subject = toDel.getSubject();
            Node pred = g.getResource(predicate);
            // Resources as predicates and Properties as predicates are not the same
            // for CORESE
            if(pred == null){
                pred = g.getPropertyNode(predicate);
            }
            Node subj = g.getResource(subject);

            // If the penta does not exist, we do nothing, as we truncate
            if(pred != null && subj != null){

            Iterable<Entity> iter = g.getEdges(pred, subj, 0);
            if(iter != null){
                for(Entity ent: iter){
                    if(ent.getNode(1).getLabel().equals(toDel.getObject())
                          && (ent.getGraph().getLabel()).equals(toDel.getGraph()))
                    {
                        TrioMonoid tagValue = new TrioMonoid(ent.getNode(2).getLabel());
                        tagValue.truncPlus(poly);

                        if(tagValue.isZero()){
                            // Here I can use the graph API because I
                            // have the entity instantiated
                            g.delete(ent);
                        }else{
                            IDatatype dt = DatatypeMap.newInstance(tagValue.toString());
                            Node newTag = g.getNode(dt, true, true);
                            EdgeImpl ee = (EdgeImpl) ent;
                            ee.setTag(newTag);
                        }
                        break;
                    }
                }
            }

                
            }
            
		} 	


	  for(SimplePenta toIns: upstream.getInsert()){
          // Change TO
          // Select the tag, and create a TrioMonoid,
          // Make the plus, save it
          // In fact, it seems both can be done in the same pack
            TrioMonoid poly = new TrioMonoid(toIns.getTag());

            String graph = toIns.getPredicate();
            String predicate = toIns.getPredicate();
            String subject = toIns.getSubject();
            String object = toIns.getSubject();

            
            Node pred = g.getResource(predicate);
            // Resources as predicates and Properties as predicates are not the same
            // for CORESE
            if(pred == null){
                pred = g.getPropertyNode(predicate);
            }
            Node subj = g.getResource(subject);

            // Could be nice to use graph API too here, but I would
            // need to code the difference between a property with literal 
            // and a resource.
            // I still think I need a way to turn off the listening
            String insTup = "INSERT DATA{ "
              + "GRAPH <" + toIns.getGraph() + ">" 
              + "{tuple("+ toIns.toTupleFormat()
              + " '" + poly.toString() + "')"
              + "}}";

            try {

                if(pred == null || subj == null){
                    exec.query(insTup);
                } else
                {    
                // Two resources can exist without being linked, making
                // the search result null.
                // Very lucky to catch this with current tests
                Iterable<Entity> iter = g.getEdges(pred, subj, 0);
                if(iter == null){
                    exec.query(insTup);
                }else{

                    boolean found = false;
                    // This should be refactored by using some sort of nice method
                    for(Entity ent: iter){
                        if(ent.getNode(1).getLabel().equals(toIns.getObject())
                              && (ent.getGraph().getLabel()).equals(toIns.getGraph()))
                        {
                            TrioMonoid tagValue = new TrioMonoid(ent.getNode(2).getLabel());
                            tagValue.truncPlus(poly);
                            IDatatype dt = DatatypeMap.newInstance(tagValue.toString());
                            Node newTag = g.getNode(dt, true, true);
                            EdgeImpl ee = (EdgeImpl) ent;
                            ee.setTag(newTag);
                            found = true;
                            break;
                        }
                    }

                    if(!found){
                        exec.query(insTup); }
                    
                    }
                //end else
                }
                
                
		} catch (EngineException ex) {
            throw new Error("This query is wrong "+ insTup +"\n Message: "+ex.getMessage());
		}
    
    }


    TMOperation downstream = new TMOperation(upstream);
    this.markSeen(downstream);
    this.lastop = downstream;
    if(publisher != null){ publisher.handle(upstream);}
      // Acknowledge that I received something from a replica
      // Even if it was only transit.
	  //this.markSeen(op.replica,op.lasttick);
  
   }
  }

  public Graph getGraph(){ return g;}


  // Abstract cardinality of all the graph
  // Remember that cardinality of the default graph
  public int cardinality(){
	String select = "SELECT DISTINCT * WHERE {GRAPH ?g {?s ?p ?o}}";
	QueryProcess exec = QueryProcess.create(g);
	try {
	  Mappings m = exec.query(select);
	  return m.size();
	} catch (EngineException ex) {
	  Logger.getLogger(TMGraph.class.getName()).log(Level.SEVERE, null, ex);
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
	  Logger.getLogger(TMGraph.class.getName()).log(Level.SEVERE, null, ex);
	}

	return -1;
  }

  // Payload size (CRDT)
  public int size(){
	return g.size(); 
  }

  public int sizeOfGraph(String graph){
	String select = "SELECT * "
            + "FROM " + graph  
            + " WHERE {tuple(?s ?p ?o ?tag)}";
	QueryProcess exec = QueryProcess.create(g);
	try {
	  Mappings m = exec.query(select);
	  return m.size();
	} catch (EngineException ex) {
	  Logger.getLogger(TMGraph.class.getName()).log(Level.SEVERE, null, ex);
	}

	return -1;
  }
  // Abstract equivalence of graphs
  // Done this awfully because compare is broken
  // TODO, implement a new one for the same store.
  public boolean sameGraph(TMGraph eg){

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

  public void load(String path){
    Load ld = Load.create(this.g);
    ld.load(path);
  
  }

  public void load(String remPath, String source){
    Load ld = Load.create(this.g);
    ld.load(remPath,source);
  
  }

  public boolean compare(TMGraph eg){
	// Is broken with quads !!
  	return g.compare(eg.g);
  }

  // CRDT equivalence of payloads
  public boolean samePayload(TMGraph eg){

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


  // Same payload and same vector
  public boolean equivalent(TMGraph eg){
	return this.samePayload(eg);
  
  }

  public String display() {
  	return  id + "\n" +
			g.display(); 
  }
  
  // Have I seen this operation?
  // 
  protected boolean hasSeen(TMOperation op){
      return op.getTrace().contains(id);
  }

  // Mark as seen/executed by me
  protected void markSeen(TMOperation op){
      op.stamp(id);
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
	final TMGraph other = (TMGraph) obj;
	if (!Objects.equals(this.id, other.id)) {
	  return false;
	}
	return true;
  }

  
  // Methods for testing
  
}
