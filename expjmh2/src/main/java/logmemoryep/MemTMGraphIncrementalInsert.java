/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package logmemoryep;

import helper.Helper;
import Graphs.TMGraph;
import Operations.TMOperation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import fr.inria.acacia.corese.exceptions.EngineException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.logic.results.Result;
import org.openjdk.jmh.logic.results.RunResult;
import org.openjdk.jmh.runner.BenchmarkRecord;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.CommandLineOptionException;
import org.openjdk.jmh.runner.options.CommandLineOptions;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.util.internal.Statistics;

/**
 * Maintenance of a collab-view with TM-Graph
 * @author ibanez-l
 */


@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class MemTMGraphIncrementalInsert {

  //TODO 
  // Investigate JCommander
  // Investigate the use of superclasses
  private Properties settings = new Properties();
    
  @Param({"0.001","0.05","0.1","0.2","0.3","0.4","0.5"})
  public double percentage;
  @Param
  public String propPath;

  // Bug or feature? No more than 3 params
  //@Param
  private String basedata;
  //@Param
  private String view;
  //@Param
  private String predicate;
  //@Param
  private String endpointURI;

  private TMGraph target;
  private TMGraph templateTarget;
  private WebResource endpoint;
  private int numInsertions;
  private Gson gson = new Gson();
		  

  @Setup(org.openjdk.jmh.annotations.Level.Trial)
  public void init() throws FileNotFoundException, IOException{
      try {
          Properties prop = new Properties();
          prop.load(new FileReader(propPath));
          basedata = prop.getProperty("basedata");
          view = prop.getProperty("view");
          predicate = prop.getProperty("predicate");
          endpointURI = prop.getProperty("endpointURI");
      } catch (IOException ex) {
          Logger.getLogger(MemTMGraphIncrementalDelete.class.getName()).log(Level.SEVERE, null, ex);
          throw new Error("Bad properties File. See Log");
      }
  	target = new TMGraph("Test");
  	templateTarget = new TMGraph("Test");

	ClientConfig config = new DefaultClientConfig();
    Client client = Client.create(config);
      try {
          endpoint = client.resource(new URI(endpointURI));
	// Reset endpoint
    Helper.reloadEndpoint(endpoint, basedata);
      // Delete log to take in account only last delete
      endpoint.path("sparql").path("logreset").post();

      } catch (URISyntaxException ex) {
          Logger.getLogger(MemTMGraphIncrementalInsert.class.getName()).log(Level.SEVERE, null, ex);
          throw new Error("Bad URI "+ endpoint);
      }

      Helper.takeView(endpoint, view, predicate, templateTarget);
      assert(templateTarget.size() > 0);
	  //Logger.getLogger(TMGraphIncrementalRemote.class.getName()).log(Level.INFO, "Template Target size = {0}", templateTarget.size());
    
  } 

  
  @Setup(org.openjdk.jmh.annotations.Level.Iteration)
  public void prepare() throws URISyntaxException {

	// Reload endpoint
    Helper.reloadEndpoint(endpoint, basedata);
      // Delete log to take in account only last delete
      endpoint.path("sparql").path("logreset").post();

      // Reload target from template
      target.copy(templateTarget.getGraph());
      /*
      for(Entity ent: target.getGraph().getEdges()){
        System.out.println(ent.toString());
        break;
      }
      */
      
	  //Logger.getLogger(TMGraphIncrementalRemote.class.getName()).log(Level.INFO, "Target size before = {0}", target.size());


	// An insert happens at the source
	numInsertions =(int)(Math.floor(templateTarget.size()*percentage)); 
	Helper.insEndpoint(endpoint, predicate,numInsertions);

  }

  @TearDown(org.openjdk.jmh.annotations.Level.Iteration)
  public void check(){
      assert(templateTarget.size()+numInsertions == target.size());
	  //Logger.getLogger(MemTMGraphIncrementalInsert.class.getName())
      //        .log(Level.INFO, "{0} + {1} = {2}", 
      //        new Object[]{templateTarget.size(), numInsertions, target.size()});
      try {
          target.query("CLEAR ALL");
          //Files.deleteIfExists(logpath);
      } catch (EngineException ex) {
          Logger.getLogger(MemTMGraphIncrementalInsert.class.getName()).log(Level.SEVERE, null, ex);
          throw new Error("Problem Clearing during tearDown ");
      }   
  }

  @GenerateMicroBenchmark
  @BenchmarkMode(Mode.AverageTime)
  public TMGraph maintain(){
          // Is possible to better implement with
          // GSON stream API (not as easy as this)
          // JACKSON
         String logRes = endpoint.path("sparql").path("log")
                  .queryParam("from", "0")
                  .accept(MediaType.APPLICATION_JSON)
                  .get(String.class);
         Type collectionType = new TypeToken<List<TMOperation>>(){}.getType();
         List<TMOperation> ops = gson.fromJson(logRes, collectionType); 
         for(TMOperation op : ops){
          target.applyEffect(op);
         }
          return target;
  }
  
    public static void main(String[] args) throws RunnerException, IOException, CommandLineOptionException {

        Options opt = new OptionsBuilder()
                .parent(new CommandLineOptions(args))
                .build();

        Runner runner = new Runner(opt);
        SortedMap<BenchmarkRecord,RunResult> benchmark = runner.run();
        System.out.println("Param   Mean   MeanError(.95)  MeanError(.99)   Percentile(50)   Percentile(90)");
        for(Map.Entry<BenchmarkRecord,RunResult> entry : benchmark.entrySet()){
            BenchmarkRecord br = entry.getKey();
            Result r = entry.getValue().getPrimaryResult();
            Statistics stats = r.getStatistics();
            System.out.println(
                    br.getActualParam("percentage") + "   "+ 
                    String.format("%.3f",stats.getMean()) + "   "+ 
                    String.format("%.3f",stats.getMeanErrorAt(0.95)) + "   "+ 
                    String.format("%.3f",stats.getMeanErrorAt(0.99)) + "   "+ 
                    String.format("%.3f",stats.getPercentile(50.0)) + "   "+ 
                    String.format("%.3f",stats.getPercentile(90.0)) 
                    );

        }
    }


  
  
}
