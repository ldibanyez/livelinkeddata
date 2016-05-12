/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OtherTests;

import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class DeletewithLimit {

    public static void main(String [] args) throws EngineException{
        
        Graph g = Graph.create();
        g.init();
        String insert = "PREFIX ex:<http://www.example.org/>"
                + "INSERT DATA {"
                + "  ex:AAA ex:predicate ex:object"
                + "  ex:AAB ex:predicate ex:object"
                + "  ex:AAC ex:predicate ex:object"
                + "  ex:AAD ex:predicate ex:object"
                + "  ex:AAE ex:predicate ex:object"
                + "  ex:AAF ex:predicate ex:object"
                + "}";

        QueryProcess exec = QueryProcess.create(g);
        exec.query(insert);
        String delete = "PREFIX ex:<http://www.example.org/>"
                + "DELETE { ?x ex:predicate ?y }"
                + "WHERE { "
                + "{ "
                + "SELECT ?x ?y "
                + "WHERE {?x ex:predicate ?y}"
                + "ORDER BY ASC(?x)"
                + "LIMIT 3"
                + "} "
                + "}";
        exec.query(delete);
        System.out.println(g.display());
        
    
    }
    
}
