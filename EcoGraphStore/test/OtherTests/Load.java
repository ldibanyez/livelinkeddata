/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OtherTests;

import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.api.core.Entity;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class Load {

    public static void main(String [] args) throws EngineException{
    
        Graph g = Graph.create();
        g.init();

        String load = "LOAD "
                + "<http://localhost/~luisdanielibanesgonzalez/datasets/ObjectFrance.ttl> "
                + "INTO GRAPH <kg:default>";
        
        // This one does not work, and it load into the default, if we follow the spec
        String load2 = "LOAD "
                + "<http://localhost/~luisdanielibanesgonzalez/datasets/ObjectFrance.ttl> "
                + "INTO GRAPH";

        QueryProcess exec = QueryProcess.create(g);

        exec.query(load);
            for(Entity ent: g.getEdges()){
                System.out.println(ent);
                System.out.println(ent.getGraph().toString());
                break;
            }
                
    
    
    }
    
}
