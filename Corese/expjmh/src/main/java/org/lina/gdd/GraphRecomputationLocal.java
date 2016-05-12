/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lina.gdd;

import Experiments.UpdateSampler;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.core.Mappings;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import fr.inria.edelweiss.kgtool.load.Load;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class GraphRecomputationLocal {

  @Param({"0" ,"0.001","0.05","0.1","0.2","0.3","0.4","0.5"})
  public double percentage;

  private Graph source;
  //private Graph target;
  private String view = "CONSTRUCT WHERE{ "
		+ "?x <http://dbpedia.org/ontology/birthPlace> ?y .}";
  private String path = "http://172.16.9.213/~luisdanielibanesgonzalez/datasets/ObjectFrance.ttl";
  private String predicate = "<http://dbpedia.org/ontology/birthPlace>";
		  
  @Setup
  public void init() {
  	source = Graph.create();
	source.init();
	Load ld = Load.create(source);
	ld.load(path,"http://ns.inria.fr/edelweiss/2010/kgram/default");
	Logger.getLogger(GraphRecomputationLocal.class.getName()).log(Level.INFO, "Source size = {0}", source.size());

	
	QueryProcess execsource = QueryProcess.create(source);
	// The target evaluates the view for the first time
	/* No need of a target, we measure the time of generating a new graph
  	target = Graph.create();
	target.init();
	
	try {
	  Mappings map = execsource.query(view);
	  target.copy((Graph)map.getGraph());
	  Logger.getLogger(GraphRecomputationLocal.class.getName()).log(Level.INFO, "Target size = {0}", target.size());
	} catch (EngineException ex) {
	  throw new Error("Something went wrong during the delete query");
	}
	*/ 

	// A delete happens at the source

	UpdateSampler sampler = new UpdateSampler();
	
	String del = sampler.samplDeleteQuery(execsource, predicate, percentage);
	try {
	 Mappings map = execsource.query(del);
	Logger.getLogger(GraphRecomputationLocal.class.getName()).log(Level.INFO, "Deleted Triples = {0}", map.nbDelete());
	} catch (EngineException ex) {
	  throw new Error("Something went wrong during the delete query");
	}
  
  }

  @GenerateMicroBenchmark
  //@BenchmarkMode({Mode.AverageTime, Mode.SampleTime})
  @BenchmarkMode(Mode.AverageTime)
  public Graph recompute(){
  	
	try {
	  QueryProcess execsource = QueryProcess.create(source);
	  Mappings map = execsource.query(view);
	  return (Graph)map.getGraph();
	  
	} catch (EngineException ex) {
	  throw new Error("Something went wrong during the delete query "+ ex.getMessage());
	}

  
  }
  

  
  
}
