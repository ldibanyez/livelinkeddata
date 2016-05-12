/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OtherTests;

import Graphs.TMGraph;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class DefaultGraph {


    public static void main(String [] args) throws EngineException{
        
        Graph g = Graph.create();
        g.init();

        String insert1="PREFIX ex:<http://www.example.org/>"
                + "INSERT DATA {"
                + "ex:resource1 ex:pred1 ex:resource2 ."
                + "ex:resource1 ex:pred1 ex:resource3 ."
                + "ex:resource1 ex:pred1 ex:resource4 ."
                + "}";

        String insert2="PREFIX ex:<http://www.example.org/>"
                + "INSERT DATA {"
                + "ex:resource1 ex:pred1 ex:resource2 ."
                + "GRAPH <kg:default>{" 
                + "ex:resource1 ex:pred1 ex:resource3 ."
                + "}"
                + "GRAPH <http://ns.inria.fr/edelweiss/2010/kgram/default> {"
                + "ex:resource1 ex:pred1 ex:resource4 ."
                + "}"
                + "}";

        QueryProcess exec = QueryProcess.create(g);

        exec.query(insert1);
        System.out.println(g.display());
        exec.query("CLEAR ALL");
        System.out.println(g.display());

        exec.query(insert2);
        System.out.println(g.display());

        System.out.println("TMGRAPH");

        TMGraph tmg = new TMGraph("TEST");
        tmg.query(insert1);
        System.out.println(tmg.display());
        tmg.query("CLEAR ALL");
        System.out.println(tmg.display());
        tmg.query(insert2);
        System.out.println(tmg.display());
    
    }
    
}
