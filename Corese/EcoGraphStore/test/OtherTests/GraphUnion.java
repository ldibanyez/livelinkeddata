/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OtherTests;

import Graphs.TMGraph;
import Publishers.TMLogPublisher;
import fr.inria.edelweiss.kgraph.core.Graph;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class GraphUnion {

    public static void main(String args[]){
    
    TMGraph China = new TMGraph("China");
    China.load("/Users/luisdanielibanesgonzalez"
            + "/Documents/live-linked-data/papers/TechReports/TMGraphExp/datasets/ObjectChina.ttl");

    TMGraph Venezuela = new TMGraph("Venezuela");
    Venezuela.load("/Users/luisdanielibanesgonzalez"
            + "/Documents/live-linked-data/papers/TechReports/TMGraphExp/datasets/ObjectVenezuela.ttl");

    Graph union = Graph.create();
    Graph vg = Venezuela.getGraph();
    Graph cg = China.getGraph();
    union.copy(cg);
    System.out.println(union.size());
    union.copy(vg);
    System.out.println(union.size());

    China.copy(Venezuela.getGraph());
    System.out.println(China.size());
    
    }
    
}
