/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package memoryexperiments;

import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgtool.load.Load;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class PGMemoryUsageTOSC {
    
public static void measureMemory(String path){

    Graph g = Graph.create();
    g.init();

    Load ld = Load.create(g);
    ld.load(path);

    //System.out.println("# of Triples loaded :" + g.size());
    // System.out.println("TOSC SizeOf Graph: " + ObjectSizeCalculator.getObjectSize(g)/(1024.0*1024.0) + "Mb");
    
    System.out.println("0 "+ObjectSizeCalculator.getObjectSize(g)/(1024.0*1024.0));
    
}
}
 
