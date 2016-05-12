/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Experiments;

import Graphs.EcoGraph;
import fr.inria.acacia.corese.exceptions.EngineException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author luisdanielibanesgonzalez
 * 
 * In this experiment we explore the overhead in memory consumption incurred
 * by the storage of ECo-IDs
 * 
 */
public class ECGMemoryUsageExpTOSC {


public static void main(String[] args){
    
    String[] CONF1 = {"MyGrapho" , "1" }; 
    String[] CONF2 = {"MyGrapho" , "1000000000" }; 
    String[] CONF3 = {"www.thisisadomain.com/withasubdomain/havingsixtyfour/characters" , "1" }; 
    String[] CONF4 = {"www.thisisadomain.com/withasubdomain/havingsixtyfour/characters", "1000000000" };
    String[] CONF5 = {"www.thisisadomain.com/withasubdomain/havingsixtyfour/characters"
            + "www.thisisadomain.com/withasubdomain/havingsixtyfour/characters", "1" }; 
    String[] CONF6 = {"www.thisisadomain.com/withasubdomain/havingsixtyfour/characters"
            + "www.thisisadomain.com/withasubdomain/havingsixtyfour/characters", "1000000000" }; 

    String[] CurrentConf = new String[2];
    //String[] CurrentConf = CONF4;
    switch (Integer.parseInt(args[0])) {
        
        case 1: CurrentConf = CONF1;
                break;
        case 2: CurrentConf = CONF2;
                break;
        case 3: CurrentConf = CONF3;
                break;
        case 4: CurrentConf = CONF4;
                break;
        case 5: CurrentConf = CONF5;
                break;
        case 6: CurrentConf = CONF6;
                break;
        default: CurrentConf = null;
                System.out.println("Invalid Configuration Number, choose 1-6");
                System.exit(1);
    }

   int numTriples = Integer.parseInt(args[1]);
//   int numTriples = 100000;
    
    EcoGraph graph = new EcoGraph(CurrentConf[0]);

    // We generate inserts of 1000 triples to avoid excessive memory consumption
    String insert = "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
         + "INSERT DATA {";
    int j = 1000;
    //int offset = 1;
    for (int i = 1 ; i<= numTriples ; i++){ 
        //insert += "<http://example/book"+i+"> dc:author 'author"+i+"' . \n";
        insert += "tuple(dc:author <http://example/book"+i+"> 'author"+i+"' '"
                +CurrentConf[0]+"#"+Integer.toString(Integer.parseInt(CurrentConf[1])+i)+"')  \n";

        if(i == j){
            try {
                insert += "}";
                graph.query(insert);
                j+= 1000;
                insert = "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
                     + "INSERT DATA {";
            } catch (EngineException ex) {
                Logger.getLogger(ECGMemoryUsageExpTOSC.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

     }
    if(args[2].equalsIgnoreCase("h")){

    System.out.println("TOSC SizeOf Name: " + ObjectSizeCalculator.getObjectSize(CurrentConf[0]));
    System.out.println("TOSC SizeOf counter: " + ObjectSizeCalculator.getObjectSize(CurrentConf[1]));
    System.out.println("TOSC SizeOf ECo-ID: " + ObjectSizeCalculator.getObjectSize(CurrentConf[0]+"#"+CurrentConf[1]));
    System.out.println("TOSC SizeOf Graph: " + ObjectSizeCalculator.getObjectSize(graph)/(1024.0*1024.0) + "Mb");
    System.out.println("TOSC SizeOf Vector: " + ObjectSizeCalculator.getObjectSize(graph.getVector()));

    //MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
    //System.out.println("Heap Memory Used: " +mem.getHeapMemoryUsage().getUsed()/1000000.0);
    //System.out.println("Non Heap Memory Used: "+ mem.getNonHeapMemoryUsage().getUsed()/1000000.0);
    //System.out.println("Graph Cardinality: "+ graph.cardinality());
    System.out.println("--------------------------------------------------------");
        /*
    try {
    TimeUnit.MINUTES.sleep(1);
     } catch(InterruptedException ex) {
    Thread.currentThread().interrupt();
     }
    * */
    } else if(args[2].equalsIgnoreCase("m")) {
    
    //System.out.println(SizeOf.humanReadable(SizeOf.deepSizeOf(graph))+"-"+SizeOf.deepSizeOf(graph.getVector()));    
    
    } else {   
        System.out.println("Invalid Output format, choose [H]uman or [M]achine");
        System.exit(1);
    }
 }
    
}
