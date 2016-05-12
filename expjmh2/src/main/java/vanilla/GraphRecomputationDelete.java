/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vanilla;
import helper.Helper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgtool.load.Load;
import fr.inria.edelweiss.kgtool.load.LoadException;
import logfileep.FileTMGraphIncrementalDelete;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
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
 * Recomputation of the Collab-View in a Vanilla Graph
 * after deletion of triples
 * @author ibanez-l
 */


@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class GraphRecomputationDelete {

  //@Param({"0.0", "0.001","0.05","0.1","0.2","0.3","0.4","0.5"})
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

  private Graph target;
  private WebResource endpoint;
  private int initialSize;
  private int numDeletions;
		  
 
  @Setup(org.openjdk.jmh.annotations.Level.Trial)
  public void init() throws URISyntaxException {
      try {
          Properties prop = new Properties();
          prop.load(new FileReader(propPath));
          basedata = prop.getProperty("basedata");
          view = prop.getProperty("view");
          predicate = prop.getProperty("predicate");
          endpointURI = prop.getProperty("endpointURI");
      } catch (IOException ex) {
          Logger.getLogger(GraphRecomputationDelete.class.getName()).log(Level.SEVERE, null, ex);
          throw new Error("Bad properties File. See Log");
      }

          target = Graph.create();
          ClientConfig config = new DefaultClientConfig();
          Client client = Client.create(config);
          endpoint = client.resource(new URI(endpointURI));
  }
  
  @Setup(org.openjdk.jmh.annotations.Level.Iteration)
  public void prepare() throws URISyntaxException, LoadException {


    Helper.reloadEndpoint(endpoint, basedata);

	  try(InputStream input = endpoint.path("sparql").queryParam("query", view)
			  .accept("application/sparql-results+xml")
			  .get(InputStream.class);)
      {   
      Load ld = Load.create(target);
	  ld.load(input);
      }catch (IOException ex) {
          Logger.getLogger(FileTMGraphIncrementalDelete.class.getName()).log(Level.SEVERE, null, ex);
          throw new Error("IO problem reading InputStream");
      }
      initialSize = target.size();
      assert(initialSize > 0);
	  //Logger.getLogger(GraphRecomputationDelete.class.getName()).log(Level.INFO, "Target size before = {0}", initialSize);

	// A delete happens at the source
	  numDeletions =  (int)(Math.floor(initialSize*percentage)); 
	try {
		Helper.delFromEndpoint(endpoint, predicate,numDeletions);
	  } catch (URISyntaxException ex) {
		Logger.getLogger(FileTMGraphIncrementalDelete.class.getName()).log(Level.SEVERE, null, ex);
	}catch (LoadException ex) {
		Logger.getLogger(FileTMGraphIncrementalDelete.class.getName()).log(Level.SEVERE, null, ex);
	  }

  
  }

  @TearDown(org.openjdk.jmh.annotations.Level.Iteration)
  public void check(){
      assert(initialSize-numDeletions == target.size());
	  //Logger.getLogger(GraphRecomputationDelete.class.getName())
      //        .log(Level.INFO, "{0} - {1} = {2}", 
      //        new Object[]{initialSize, numDeletions, target.size()});
	  target.clearDefault();
	  target.clearNamed();
  }
	
  @GenerateMicroBenchmark
  @BenchmarkMode(Mode.AverageTime)
  @Warmup(iterations=10)
  @Measurement(iterations=10)
  @Fork(1)
  public Graph recompute(){
  	
	// The recomputation time is the clear of the previous result
	// plus the recomputation
	try (InputStream input = endpoint.path("sparql").queryParam("query", view)
			  .accept("application/sparql-results+xml")
			  .get(InputStream.class);)
    {
	  target.clearDefault();
	  target.clearNamed();
      Load ld = Load.create(target);
	  ld.load(input);
	  return target;
	  
	} catch (LoadException ex) {
		Logger.getLogger(GraphRecomputationDelete.class.getName()).log(Level.SEVERE, null, ex);
		throw new Error("Something wrong during recomputation");
	} catch (IOException ex) {
          Logger.getLogger(GraphRecomputationDelete.class.getName()).log(Level.SEVERE, null, ex);
          throw new Error("IO reading InputStream");
      }

  
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
