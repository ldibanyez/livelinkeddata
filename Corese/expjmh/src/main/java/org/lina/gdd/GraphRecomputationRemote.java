/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lina.gdd;

import Experiments.UpdateSampler;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.core.Mappings;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import fr.inria.edelweiss.kgtool.load.Load;
import fr.inria.edelweiss.kgtool.load.LoadException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MultivaluedMap;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * Recomputation of the Collab-View in a Vanilla Graph
 * after deletion of triples
 * @author ibanez-l
 */


@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class GraphRecomputationRemote {

  @Param({"0.0", "0.001","0.05","0.1","0.2","0.3"})
  public double percentage;

  private Graph target;
  private String view = "CONSTRUCT WHERE{ "
		+ "?x <http://dbpedia.org/ontology/birthPlace> ?y .}";
  private String uri = "http://172.16.9.213:10000/kgram";
  private String predicate = "<http://dbpedia.org/ontology/birthPlace>";
  private String path = "http://172.16.9.213/~luisdanielibanesgonzalez/datasets/MegaBase.ttl";
  private WebResource service;
  private Load ld;
		  
  @Setup
  public void init() throws URISyntaxException {

  	target = Graph.create();
	target.init();
	
	ClientConfig config = new DefaultClientConfig();
    Client client = Client.create(config);
    MultivaluedMap formData = new MultivaluedMapImpl();
      service = client.resource(new URI(uri));

	// Reset endpoint

	  service.path("sparql").path("reset");

	// Load data in the endpoint

	  formData.add("remote_path", path);
	  service.path("sparql").path("load").post(formData);
	  formData.clear();

	// Move to Default Graph
	  String move = "MOVE <"+path+"> TO <kg:default>";
	  formData.add("update",move);
	  service.path("sparql").path("update").type("application/x-www-form-urlencoded").post(formData);
	  formData.clear();
	  
	// The target computes the view for the first time
	try {
	  ld = Load.create(target);
	  InputStream input = service.path("sparql").queryParam("query", view)
			  .accept("application/sparql-results+xml")
			  .get(InputStream.class);
	  ld.load(input);
	  Logger.getLogger(TMGraphIncrementalLocal.class.getName()).log(Level.INFO, "Target size = {0}", target.size());
	} catch (LoadException ex) {
	  throw new Error("Something went wrong during the load of the answer");
	} 

	// A delete happens at the source
	
	try {
		delFromEndpoint(uri,predicate, percentage);
	  } catch (URISyntaxException ex) {
		Logger.getLogger(GraphRecomputationRemote.class.getName()).log(Level.SEVERE, null, ex);
	}catch (LoadException ex) {
		Logger.getLogger(GraphRecomputationRemote.class.getName()).log(Level.SEVERE, null, ex);
	  }

  
  }

	
  @GenerateMicroBenchmark
  @BenchmarkMode({Mode.AverageTime, Mode.SampleTime})
  public Graph recompute(){
  	
	// The recomputation time is the clear of the previous result
	// plus the recomputation
	try {
	  target.clearDefault();
	  InputStream input = service.path("sparql").queryParam("query", view)
			  .accept("application/sparql-results+xml")
			  .get(InputStream.class);
	  ld.load(input);
	  return target;
	  
	} catch (LoadException ex) {
		Logger.getLogger(GraphRecomputationRemote.class.getName()).log(Level.SEVERE, null, ex);
		throw new Error("Something wrong during recomputation");
	}

  
  }
  
    public static void delFromEndpoint(String uri, String predicate , double percentage) throws URISyntaxException, LoadException{
    
           if(percentage == 0.0){
            return;
           }
           if(percentage < 0){
            throw new Error("Negative percentage");
           }
        
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        MultivaluedMap formData = new MultivaluedMapImpl();
       
        WebResource service = client.resource(new URI(uri));
        UpdateSampler sampler = new UpdateSampler();

            //Get the remote graph from a construct
            Graph g = Graph.create();
            g.init();
            Load ld = Load.create(g);
            //ld.l;

            String count = "CONSTRUCT WHERE {?x "+predicate +" ?z}";
            InputStream input = service.path("sparql").queryParam("query", count).accept("application/sparql-results+xml").get(InputStream.class);
            ld.load(input);

            QueryProcess exec = QueryProcess.create(g);
       
            String delete = sampler.samplDeleteQuery(exec, predicate, percentage);
            formData.add("update",delete);
            service.path("sparql").path("update").type("application/x-www-form-urlencoded").post(formData);
            formData.clear();
    
    
    }
    

  
  
}
