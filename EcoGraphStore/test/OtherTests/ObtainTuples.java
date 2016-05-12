/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OtherTests;

import Graphs.TMGraph;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.api.core.Entity;
import fr.inria.edelweiss.kgram.core.Mappings;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class ObtainTuples {

    public static void main(String[] args){
        
        TMGraph tmg = new TMGraph("TEST");
        String insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
	//		+ "<http://example/book1> dc:creator 'A.N. Other' ."
	//		+ "<http://example/book2> dc:creator 'Balzac' ."
	//		+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";
            Mappings map;
        try {
            map = tmg.query(insert1);

            System.out.println("getEdges");
            for(Entity ent : tmg.getGraph().getEdges()){
    System.out.println(ent.getGraph().getLabel());
    System.out.println(ent.getNode(0).toString());
    System.out.println(ent.getEdge().getEdgeNode().toString());
    System.out.println(ent.getNode(1).toString());
    System.out.println(ent.getNode(2).getLabel());
    System.out.println("toString:");
    System.out.println(ent.toString());
            
            }
        }catch(Exception ex){
                
            }
            
    System.out.println("Now a graph");
        Graph g = Graph.create();
        g.setTag(true);
        QueryProcess qp = QueryProcess.create(g);
        try {
            map = qp.query(insert1);

            System.out.println("getEdges");
            for(Entity ent : g.getEdges()){
    System.out.println(ent.getGraph().getLabel());
    System.out.println(ent.getNode(0).toString());
    System.out.println(ent.getEdge().getEdgeNode().toString());
    System.out.println(ent.getNode(1).toString());
    System.out.println(ent.getNode(2).getLabel());
    System.out.println("toString:");
    System.out.println(ent.toString());
            
            }
            /*
            System.out.println("");
            for(Entity ent : g.getAllNodesDirect()){
    System.out.println(ent.getGraph().getLabel());
    System.out.println(ent.getNode(0).toString());
    System.out.println(ent.getEdge().getEdgeNode().toString());
    System.out.println(ent.getNode(1).toString());
    System.out.println(ent.getNode(2).getLabel());
            
            }

            System.out.println("getAllNodes");
            for(Entity ent : g.getAllNodes()){
    System.out.println(ent.getGraph().getLabel());
    System.out.println(ent.getNode(0).toString());
    System.out.println(ent.getEdge().getEdgeNode().toString());
    System.out.println(ent.getNode(1).toString());
    System.out.println(ent.getNode(2).getLabel());
            
            }
*/
            
        } catch (EngineException ex) {
            Logger.getLogger(ObtainTuples.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
