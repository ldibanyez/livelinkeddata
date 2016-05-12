/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OtherTests;

import Graphs.TMGraph;
import fr.inria.acacia.corese.api.IDatatype;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.api.core.Entity;
import fr.inria.edelweiss.kgram.core.Mapping;
import fr.inria.edelweiss.kgram.core.Mappings;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import fr.inria.edelweiss.kgtool.load.Load;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class Construct {

    TMGraph g= new TMGraph("TEST");

    @Test
    public void testConstruct(){
        
	String insert = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";
	String construct = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            + "CONSTRUCT {tuple(?pred ?sub ?obj ?tag)} "
            + "WHERE {tuple(?sub ?pred ?obj ?tag) .}";
            //+ "WHERE {GRAPH ?graph {tuple(?sub ?pred ?obj ?tag) .}}";
    String extract = "SELECT ?g ?sub ?pred ?obj ?tag"
            + "WHERE{GRAPH ?g {tuple(?pred ?sub ?obj ?tag)}}";
        try {
            g.query(insert);
            Graph g2 = g.getGraph();
            g2.setTag(false);
            QueryProcess exec = QueryProcess.create(g2); 
            Mappings map = exec.query(construct);
            Graph g3 = exec.getGraph(map);
            for(Entity ent : g3.getEdges()){
                System.out.println(ent.toString());
            
            }


            /*
            map = exec.query(extract);   
            for(Mapping m: map){
               IDatatype graph = (IDatatype) m.getValue("?g"); 
               IDatatype sub = (IDatatype) m.getValue("?sub"); 
               IDatatype pred = (IDatatype) m.getValue("?pred"); 
               IDatatype obj = (IDatatype) m.getValue("?obj"); 
               IDatatype tag = (IDatatype) m.getValue("?tag"); 
            }*/
            
        } catch (EngineException ex) {
            Logger.getLogger(Construct.class.getName()).log(Level.SEVERE, null, ex);
        }




    }
    
}
