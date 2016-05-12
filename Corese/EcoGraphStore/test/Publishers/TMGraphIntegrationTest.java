/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Publishers;

import Graphs.TMGraph;
import fr.inria.acacia.corese.exceptions.EngineException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class TMGraphIntegrationTest {

    /*
    public static void main(String [] args){
        
        TMGraph g = new TMGraph("Test");
        TMLogPublisher pub = new TMLogPublisher();
        g.setPublisher(pub);
	    String insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";
        try {
            g.query(insert1);

            System.out.println(pub.lastOp());
        } catch (EngineException ex) {
            Logger.getLogger(TMGraphIntegrationTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    */
}
