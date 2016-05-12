/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sample;

import java.util.Map;
import java.util.concurrent.TimeUnit;
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
import org.openjdk.jmh.runner.options.VerboseMode;
import org.openjdk.jmh.util.internal.Statistics;

/**
 *
 * @author luisdanielibanesgonzalez
 */
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class MedianCalculation {

    public int dummy;
    @Param({"13","131313"})
    public int param;

    @Setup(org.openjdk.jmh.annotations.Level.Trial)
    public void setUp(){
        dummy = 0;
    }

    @TearDown(org.openjdk.jmh.annotations.Level.Iteration)
    public void tearDown(){
        assert(dummy > 0);
        dummy = 0;
    }

    @GenerateMicroBenchmark
    @BenchmarkMode(Mode.AverageTime)
    public int benchmark(){
        for(int i=0 ; i<100000 ; i++ ){
            dummy += param*Math.random();
        }
        return dummy;
    }

    public static void main(String [] args) throws RunnerException, CommandLineOptionException{
    
        Options opt = new OptionsBuilder()
                .parent(new CommandLineOptions(args))
                .include(".*" + MedianCalculation.class.getSimpleName() + ".*")
                //.warmupIterations(10)
                //.measurementIterations(11)
                //.forks(1)
                //.verbosity(VerboseMode.SILENT)
                .build();

        // runSingle is a shortcut method when the options define only
        // one benchmark to execute
        //RunResult result = new Runner(opt).runSingle();

        
        for(Map.Entry<BenchmarkRecord,RunResult> entry : (new Runner(opt).run()).entrySet()){
            BenchmarkRecord br = entry.getKey();
            System.out.println("BenchMarkRecords: "+entry.getKey().toString());
            System.out.println("Param "+ br.getActualParam("param"));
            Result r = entry.getValue().getPrimaryResult();

            System.out.println("result.getScoreUnit: "+ r.getScoreUnit());
            System.out.println("result.getLabel(): " + r.getLabel());
            System.out.println("toString() :" + r.toString());
            Statistics stats = r.getStatistics();
            System.out.println(String.format("stats.getMean(): %.3f",stats.getMean()));
            System.out.println(String.format("stats.getMeanErrorAt(0.95): %.3f", stats.getMeanErrorAt(0.95)));
            System.out.println(String.format("stats.getPercentile(50.0): %.3f", stats.getPercentile(50.0)));
            System.out.println(String.format("stats.getPercentile(90.0): %.3f", stats.getPercentile(90.0)));
        }
    
    
    }
    
}
