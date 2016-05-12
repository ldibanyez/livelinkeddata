/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lina.gdd;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import fr.inria.edelweiss.kgraph.core.Graph;
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
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Recomputation of the Collab-View in a Vanilla Graph
 * after deletion of triples
 * @author ibanez-l
 */


@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class GraphRecomputationRemote {

  //@Param({"0.0", "0.001","0.05","0.1","0.2","0.3"})
  public double percentage = 0.1;

  private Graph target;
  private String view = "CONSTRUCT WHERE{ "
		+ "?x <http://dbpedia.org/ontology/birthPlace> ?y .}";
  private String uri = "http://172.16.9.3:20501/kgram";
  private static String predicate = "<http://dbpedia.org/ontology/birthPlace>";
  private String path = "http://172.16.9.213/~luisdanielibanesgonzalez/datasets/ObjectFrance.ttl";
  private static WebResource service;
		  
  @Setup(org.openjdk.jmh.annotations.Level.Iteration)
  public void init() throws URISyntaxException, LoadException {

  	target = Graph.create();
	
	ClientConfig config = new DefaultClientConfig();
    Client client = Client.create(config);
    MultivaluedMap formData = new MultivaluedMapImpl();
      service = client.resource(new URI(uri));

	// Reset endpoint
	  service.path("sparql").path("reset").post();

	// Load data in the endpoint
      // nt load implemented by me working around bug that avoid tags

	  formData.add("remote_path", path);
	  service.path("sparql").path("load").post(formData);
	  formData.clear();

	  InputStream input = service.path("sparql").queryParam("query", view)
			  .accept("application/sparql-results+xml")
			  .get(InputStream.class);
      Load ld = Load.create(target);
	  ld.load(input);
	  Logger.getLogger(GraphRecomputationRemote.class.getName()).log(Level.INFO, "Target size before = {0}", target.size());

	// A delete happens at the source
	
	try {
		Helper.delFromEndpoint(service, predicate, 
                (int)(Math.floor(target.size()*percentage)));
	  } catch (URISyntaxException ex) {
		Logger.getLogger(TMGraphIncrementalRemote.class.getName()).log(Level.SEVERE, null, ex);
	}catch (LoadException ex) {
		Logger.getLogger(TMGraphIncrementalRemote.class.getName()).log(Level.SEVERE, null, ex);
	  }

  
  }

  @TearDown(org.openjdk.jmh.annotations.Level.Iteration)
  public void check(){
	  Logger.getLogger(GraphRecomputationRemote.class.getName()).log(Level.INFO, "Target size after = {0}", target.size());
  }
	
  @GenerateMicroBenchmark
  @BenchmarkMode(Mode.AverageTime)
  public Graph recompute(){
  	
	// The recomputation time is the clear of the previous result
	// plus the recomputation
	try {
	  target.clearDefault();
	  InputStream input = service.path("sparql").queryParam("query", view)
			  .accept("application/sparql-results+xml")
			  .get(InputStream.class);
      Load ld = Load.create(target);
	  ld.load(input);
	  return target;
	  
	} catch (LoadException ex) {
		Logger.getLogger(GraphRecomputationRemote.class.getName()).log(Level.SEVERE, null, ex);
		throw new Error("Something wrong during recomputation");
	}

  
  }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + GraphRecomputationRemote.class.getSimpleName() + ".*")
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
  
  
}
