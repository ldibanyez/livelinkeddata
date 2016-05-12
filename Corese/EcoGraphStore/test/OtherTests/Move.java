/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OtherTests;

import fr.inria.edelweiss.kgram.api.core.Entity;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import fr.inria.edelweiss.kgtool.load.Load;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class Move {

    public static void main(String [] args){

    Graph g = Graph.create();

    String insert = ""
                + "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
            + "GRAPH <MyNamedGraph> {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
            + "}"
			+ "}";
    
    QueryProcess exec = QueryProcess.create(g);
    
    String moveDefault = "MOVE <MyNamedGraph> TO DEFAULT";
    String move = "MOVE <MyNamedGraph> TO <kg:default>";

        try {
            exec.query(insert);
            System.out.println(moveDefault);
            exec.query(moveDefault);
            for(Entity ent: g.getEdges()){
                System.out.println(ent);
                System.out.println(ent.getGraph().toString());
                break;
            }
            System.out.println(move);
            exec.query(move);
            for(Entity ent: g.getEdges()){
                System.out.println(ent);
                System.out.println(ent.getGraph().toString());
                break;
            }
        } catch (Exception ex) {
        }

    }
}
