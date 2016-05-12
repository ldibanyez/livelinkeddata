/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lina.gdd;

import Graphs.TMGraph;
import Operations.TMOperation;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.api.core.Entity;
import fr.inria.edelweiss.kgtool.load.Load;
import fr.inria.edelweiss.kgtool.load.LoadException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.io.FileUtils;
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
 * Maintenance of a collab-view with TM-Graph
 * @author ibanez-l
 */


@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class TMGraphIncrementalRemote {

  //@Param({"0.0", "0.001","0.05","0.1","0.2","0.3"})
  public double percentage = 0.1;

  private TMGraph target;
  private TMGraph templateTarget;
  private String view = "CONSTRUCT WHERE{ "
		+ "tuple(<http://dbpedia.org/ontology/birthPlace> ?x ?y ?tag) .}";
  private String uri = "http://172.16.9.3:20500/kgram";
  private String predicate = "<http://dbpedia.org/ontology/birthPlace>";
  private String path = "http://172.16.9.213/~luisdanielibanesgonzalez/datasets/ObjectFrance.nt";
  private Path logpath = Paths.get("/tmp/logs/logTest.logtest");
  //private String path = "/Users/luisdanielibanesgonzalez/Sites/datasets/ObjectFrance.nt";
  private WebResource service;
		  

  @Setup(org.openjdk.jmh.annotations.Level.Trial)
  public void init(){
  	target = new TMGraph("Test");
  	templateTarget = new TMGraph("Test");
	
	ClientConfig config = new DefaultClientConfig();
    Client client = Client.create(config);
      try {
          service = client.resource(new URI(uri));
        // Reset endpoint
          service.path("sparql").path("reset").post();
          service.path("sparql").path("logreset").post();

        // Reload data in the endpoint
          // nt load implemented by me working around bug that avoid tags

          MultivaluedMap formData = new MultivaluedMapImpl();
          formData.add("remote_path", path);
          service.path("sparql").path("load").post(formData);
          formData.clear();
      } catch (URISyntaxException ex) {
          Logger.getLogger(TMGraphIncrementalRemote.class.getName()).log(Level.SEVERE, null, ex);
          throw new Error("Bad URI "+ uri);
      }
	try (InputStream input = service.path("sparql").queryParam("query", view)
			  .accept("application/sparql-results+tsv")
			  .get(InputStream.class);
        BufferedReader buf = new BufferedReader(new InputStreamReader(input));)
    {
        // TODO: This should be in a proxy
	    
        // TODO: JAVA 7 scanner?
        String line = buf.readLine();
        assert(line.contains("?subj"));
        String insert = "INSERT DATA {";
        line = buf.readLine();
        while(line != null){
            String[] sp = line.split("\t");
            insert += "tuple("+ predicate + " " + sp[0] + " " + sp[1] + " " + sp[2]+")\n";
            line = buf.readLine();
        }
        insert += "}";
        buf.close();
        input.close();
        templateTarget.query(insert);

	  Logger.getLogger(TMGraphIncrementalRemote.class.getName()).log(Level.INFO, "Template Target size = {0}", templateTarget.size());
    
	  } catch (IOException ex) {
          Logger.getLogger(TMGraphIncrementalRemote.class.getName()).log(Level.SEVERE, null, ex);
        throw new Error("IO Problem, check log");
      } catch (EngineException ex) {
          Logger.getLogger(TMGraphIncrementalRemote.class.getName()).log(Level.SEVERE, null, ex);
          throw new Error("Query Problem, check log");
      } 
  } 

  
  @Setup(org.openjdk.jmh.annotations.Level.Iteration)
  public void prepare() throws URISyntaxException {

	// Reset endpoint
	  service.path("sparql").path("reset").post();
      service.path("sparql").path("logreset").post();

	// Reload data in the endpoint
      // nt load implemented by me working around bug that avoid tags

      MultivaluedMap formData = new MultivaluedMapImpl();
	  formData.add("remote_path", path);
	  service.path("sparql").path("load").post(formData);
	  formData.clear();

      // Reload target from template
      target.copy(templateTarget.getGraph());
      /*
      for(Entity ent: target.getGraph().getEdges()){
        System.out.println(ent.toString());
        break;
      }
      */
      
	  Logger.getLogger(TMGraphIncrementalRemote.class.getName()).log(Level.INFO, "Target size before = {0}", target.size());

      // Delete log to take in account only last delete
      service.path("sparql").path("logreset").post();

	// A delete happens at the source
      try{
	
		Helper.delFromEndpoint(service, predicate, 
                (int)(Math.floor(templateTarget.size()*percentage)));

	  } catch (URISyntaxException ex) {
		Logger.getLogger(TMGraphIncrementalRemote.class.getName()).log(Level.SEVERE, null, ex);
        throw new Error("Problem with URI");
	}catch (LoadException ex) {
		Logger.getLogger(TMGraphIncrementalRemote.class.getName()).log(Level.SEVERE, null, ex);
        throw new Error("Problem Loading ");
      } 

  
  }

  @TearDown(org.openjdk.jmh.annotations.Level.Iteration)
  public void check(){
	  Logger.getLogger(TMGraphIncrementalRemote.class.getName()).log(Level.INFO, "Target size after = {0}", target.size());
      try {
          target.query("CLEAR ALL");
          Files.deleteIfExists(logpath);
      } catch (EngineException ex) {
          Logger.getLogger(TMGraphIncrementalRemote.class.getName()).log(Level.SEVERE, null, ex);
          throw new Error("Problem Clearing during tearDown ");
      } catch (IOException ex) {
          Logger.getLogger(TMGraphIncrementalRemote.class.getName()).log(Level.SEVERE, null, ex);
          throw new Error("IO/Problem clearing log during tearDown");
      }
  
  }

  @GenerateMicroBenchmark
  @BenchmarkMode(Mode.AverageTime)
  public TMGraph maintain(){
      try {  	
          InputStream logRes = service.path("sparql").path("log")
                  .accept(MediaType.APPLICATION_OCTET_STREAM)
                  .get(InputStream.class);
          FileUtils.copyInputStreamToFile(logRes, logpath.toFile());
          //TODO: A way to have list of operations, The JSON thing was cool, but
          // big operations make it choke. A DBpedia live style solution seems
          // the next step
          TMOperation op = new TMOperation(logpath.toFile()); 
          target.applyEffect(op);

      } catch (IOException ex) {
          Logger.getLogger(TMGraphIncrementalRemote.class.getName()).log(Level.SEVERE, null, ex);
          throw new Error("IO problem reading the download log");
      }
          return target;
  }
  
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + TMGraphIncrementalRemote.class.getSimpleName() + ".*")
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }


  
  
}
