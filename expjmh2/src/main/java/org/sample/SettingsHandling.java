/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sample;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.CommandLineOptionException;
import org.openjdk.jmh.runner.options.CommandLineOptions;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 *
 * @author Luis-Daniel Ibáñez luis[dot]ibanez[at]univ-nantes.fr
 */
@State(Scope.Thread)
public class SettingsHandling {

    // @Param is the best way to handle settings
    @Param
    private String mySetting0;
    // Here we set defaults
    @Param
    private String mySetting1;
    // but a maximum of two or
    //java.lang.IllegalStateException: Comparing actual params with different key sets. 
    @Param
    private String mySetting2;

    @Setup(Level.Trial)
    public void init(){
        // Here I got a Null Pointer exception
        System.out.println("At Trial Level");
        System.out.println("Setting 0 "+ mySetting0);
        System.out.println("Setting 1 "+ mySetting1);
        System.out.println("Setting 2 "+ mySetting2);
    }
    
    @Setup(Level.Iteration)
    public void setup(){
        System.out.println("At Iteration Level");
        System.out.println("Setting 1 "+ mySetting1);
        System.out.println("Setting 2 "+ mySetting2);
    }

    // This sets the default JMH settings for this method
    @GenerateMicroBenchmark
    @Warmup(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(1)
    public String bench(){
        return mySetting1 + Math.random();
    }

    public static void main(String [] args) throws CommandLineOptionException, RunnerException, IOException{

              Options baseOpts = new OptionsBuilder()
                // This is the key line to use the CLI arguments
                // Defaults can be changed with annotations
                .parent(new CommandLineOptions(args))
                .build();

              System.out.println(baseOpts.getIncludes());
              System.out.println(baseOpts.getParameter("mySetting1"));
              System.out.println(baseOpts.getMeasurementIterations());
              
             new Runner(baseOpts).run(); 
             
        /* 
        * To run the main method 
        * changing the number of iterations and the first settings
        * java -cp microbenchmarks.java org/sample/SettingsHandling \
        *   -i 5 -p mySetting1=myValue1 "org.sample.SettingsHandling"
        *
        * Note that the -cp org/sample... guarantees the execution of the main method in
        * the SettingsHandling class, but we still need to pass to the Runner 
        * the class containing the methods to benchmark, if we don't put the final
        * "org.sample.SessionsHandling", the default value ".*" will be taken and 
        * all benchmarks in the package will be executed.
        * 
        * The -p switch does the magic to set the custom params
        */ 

          }
}
