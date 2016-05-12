/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lina.gdd;

import Graphs.TMGraph;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.core.Mappings;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import fr.inria.edelweiss.kgtool.load.Load;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * Benchmark Collab-View Evaluation on a vanille Corese Graph
 * @author ibanez-l
 */


@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class QueryTMGraphBenchmark {

  private TMGraph g;


  @Setup
  public void init(){

	String path = "/home/ibanez-l/Dropbox/MegaBase.ttl";
	
  	g = new TMGraph("Test");
	g.load(path);
  }

  @GenerateMicroBenchmark
  @BenchmarkMode({Mode.AverageTime, Mode.SampleTime})
  public int queryPredicate() throws EngineException{
  
	String view = "CONSTRUCT WHERE{ "
		+ "tuple(<http://dbpedia.org/ontology/birthPlace> ?subj ?obj ?tag).}";
	Mappings map = g.query(view);
	return map.size();
  
  }
  
  @GenerateMicroBenchmark
  @BenchmarkMode({Mode.AverageTime, Mode.SampleTime})
  public int queryObject() throws EngineException{
  
	String view = "CONSTRUCT WHERE{ "
		+ "tuple(?pred ?subj <http://dbpedia.org/resource/France> ?tag) .}";
	Mappings map = g.query(view);
	return map.size();
  
  }
  
  
  @GenerateMicroBenchmark
  @BenchmarkMode({Mode.AverageTime, Mode.SampleTime})
  public int querySubject() throws EngineException{
  
	String view = "CONSTRUCT WHERE{ "
		+ "tuple(?pred <http://dbpedia.org/resource/Donald_Knuth> ?obj ?tag).}";
	Mappings map = g.query(view);
	return map.size();
  
  }
}
