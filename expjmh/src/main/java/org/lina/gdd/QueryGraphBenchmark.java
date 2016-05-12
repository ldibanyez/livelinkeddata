/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lina.gdd;

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
public class QueryGraphBenchmark {

  private Graph g;


  @Setup
  public void init(){

	String path = "/home/ibanez-l/live-linked-data/datasources/ObjectFrance.ttl";
	
  	g = Graph.create();
	g.init();
	Load ld = Load.create(g);
	ld.load(path,"kg:default");
  }

  @GenerateMicroBenchmark
  @BenchmarkMode({Mode.AverageTime})
  public int queryPredicate() throws EngineException{
  
	String view = "CONSTRUCT WHERE{ "
		+ "?x <http://dbpedia.org/ontology/birthPlace> ?y .}";
	QueryProcess exec = QueryProcess.create(g);
	Mappings map = exec.query(view);
	return map.size();
  
  }
  
  @GenerateMicroBenchmark
  @BenchmarkMode({Mode.AverageTime})
  public int queryObject() throws EngineException{
  
	String view = "CONSTRUCT WHERE{ "
		+ "?x ?y <http://dbpedia.org/resource/France> .}";
	QueryProcess exec = QueryProcess.create(g);
	Mappings map = exec.query(view);
	return map.size();
  
  }
  
  
  @GenerateMicroBenchmark
  @BenchmarkMode({Mode.AverageTime})
  public int querySubject() throws EngineException{
  
	String view = "CONSTRUCT WHERE{ "
		+ "<http://dbpedia.org/resource/University_of_Nantes> ?y ?z.}";
	QueryProcess exec = QueryProcess.create(g);
	Mappings map = exec.query(view);
	return map.size();
  
  }
}
