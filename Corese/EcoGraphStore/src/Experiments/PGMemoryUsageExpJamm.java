/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Experiments;

import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.core.Mappings;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import org.github.jamm.MemoryMeter;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class PGMemoryUsageExpJamm {
    
public static void main(String[] args){
    Graph graph = Graph.create();
	QueryProcess exec = QueryProcess.create(graph);

    int numTriples = Integer.parseInt(args[0]);
    // We generate inserts of 1000 triples to avoid excessive memory consumption
    String insert = "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
         + "INSERT DATA {";
    int j = 1000;
    //int offset = 1;
    for (int i = 1 ; i<= numTriples ; i++){ 
        //insert += "<http://example/book"+i+"> dc:author 'author"+i+"' . \n";
        insert += "<http://example/book"+i+"> dc:author 'author"+i+"' .";

        if(i == j){
            try {
                insert += "}";
                Mappings m = exec.query(insert);
                j+= 1000;
                insert = "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
                     + "INSERT DATA {";
            } catch (EngineException ex) {
            }
        }

     }
    if(args[1].equalsIgnoreCase("h")){
    MemoryMeter meter = new MemoryMeter();
    
    System.out.println("Jamm SizeOf Graph: " + meter.measureDeep(graph)/(1024.0*1024.0) + "Mb");
    //System.out.println("Graph Cardinality: "+ graph.size());
    //System.out.println("--------------------------------------------------------");
    } else if(args[1].equalsIgnoreCase("m")) {
    
    
    } else {   
        System.out.println("Invalid Output format, choose [H]uman or [M]achine");
        System.exit(1);
    }
    
}
}
 
