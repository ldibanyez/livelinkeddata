/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lina.gdd;

import java.util.Map;
import org.openjdk.jmh.logic.results.Result;
import org.openjdk.jmh.logic.results.RunResult;
import org.openjdk.jmh.runner.BenchmarkRecord;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 *
 * @author ibanez-l
 */
public class main {
  
  public static void main(String [] args) throws RunnerException{
  
  	
 	 Options opt = new OptionsBuilder()
                       .include(".*" + GraphRecomputationLocal.class.getSimpleName() + ".*")
                       .warmupIterations(5)
                       .measurementIterations(5)
                       .forks(1)
                       .build();
       
      Map<BenchmarkRecord,RunResult> records = new Runner(opt).run();

    for (Map.Entry<BenchmarkRecord, RunResult> result : records.entrySet()) {
        Result r = result.getValue().getPrimaryResult();
        System.out.println("API replied benchmark score: "
            + r.getScore() + " "
            + r.getScoreUnit() + " over "
            + r.getStatistics().getN() + " iterations");
    }
  
  }
}
