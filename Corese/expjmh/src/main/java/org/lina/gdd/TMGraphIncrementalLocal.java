/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lina.gdd;

import Experiments.UpdateSampler;
import Graphs.TMGraph;
import Operations.TMOperation;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.core.Mappings;
import fr.inria.edelweiss.kgraph.core.Graph;
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
public class TMGraphIncrementalLocal {

  @Param({"0", "0.001","0.05","0.1","0.2","0.3","0.4","0.5"})
  public double percentage;

  private TMGraph source;
  private TMGraph target;
  private String path = "http://172.16.9.213/~luisdanielibanesgonzalez/datasets/ObjectFrance.ttl";
  private String predicate = "<http://dbpedia.org/ontology/birthPlace>";
  private String view = "CONSTRUCT WHERE{ "
		+ "tuple(<http://dbpedia.org/ontology/birthPlace> ?subj ?obj ?tag).}";
  private TMOperation op;
		  
  @Setup
  public void init() {
  	source = new TMGraph("Source");
	source.load(path,"http://ns.inria.fr/edelweiss/2010/kgram/default");
	Logger.getLogger(TMGraphIncrementalLocal.class.getName()).log(Level.INFO, "Source size = {0}", source.size());
	
	target = new TMGraph("Target");
	try {
	  Mappings map = source.query(view);
	  target.copy((Graph)map.getGraph());
	  Logger.getLogger(TMGraphIncrementalLocal.class.getName()).log(Level.INFO, "Target size = {0}", target.size());
	} catch (EngineException ex) {
	  Logger.getLogger(TMGraphIncrementalLocal.class.getName()).log(Level.SEVERE, null, ex);
	}

	UpdateSampler sampler = new UpdateSampler();
	
    sampler.samplDelete(source, predicate, percentage);
	op = source.getLastOperation();
	Logger.getLogger(TMGraphIncrementalLocal.class.getName()).log(Level.INFO, "Operation size = {0}", op.getInsert().size()+ op.getDelete().size());
	//Logger.getLogger(TMGraphIncrementalLocal.class.getName()).log(Level.INFO, "Operation = {0}", op.toString());
  
  }

  
  @GenerateMicroBenchmark
  //@BenchmarkMode({Mode.AverageTime, Mode.SampleTime})
  @BenchmarkMode(Mode.AverageTime)
  public TMGraph maintain(){
  	
	target.applyEffect(op);
	//Logger.getLogger(TMGraphIncrementalLocal.class.getName()).log(Level.INFO, "Target size after applying = {0}", target.size());
	return target;
  }

  
}
