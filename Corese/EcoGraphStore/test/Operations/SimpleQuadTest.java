/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Operations;

import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.api.core.Entity;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import java.util.HashSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class SimpleQuadTest {

    Graph g = Graph.create();
    String GRAPH1 = "http://example.org/graph1";
    String GRAPH2 = "http://example.org/graph2";
    String DEFAULT = "http://ns.inria.fr/edelweiss/2010/kgram/default";
    
    public SimpleQuadTest() throws EngineException {
        String insert = "PREFIX ex:<http://example.org/things/>"
                + "INSERT DATA{"
                + "ex:subject ex:predicate ex:object ." 
                + "GRAPH <"+ GRAPH1 +">{"
                + "ex:subject ex:predicate ex:object ." 
                + "  }"
                + "GRAPH <"+ GRAPH2 +">{"
                + "ex:subject ex:predicate ex:object ." 
                + "  }"
                + "}";
        QueryProcess exec = QueryProcess.create(g);
        exec.query(insert);
    }

    /**
     * Test of getGraph method, of class SimpleQuad.
     */
    @Test
    public void testGetGraph() {
        
        HashSet<String> graphs = new HashSet<>();
        for(Entity ent : g.getEdges()){
            SimpleQuad sq = new SimpleQuad(ent);
            graphs.add(sq.getGraph());
        }
        System.out.println(graphs.toString());
        assert(graphs.contains(GRAPH1));
        assert(graphs.contains(GRAPH2));
        //The graph is stored as a property
        assert(graphs.contains(DEFAULT));
    }

    /**
     * Test of toString method, of class SimpleQuad.
     */
    @Test
    public void testToString() {
        String RESOURCE = "http://example.org/test";
        String RESOURCE2 = "http://example.org/test2";
        String RESOURCEP = "http://example.org/resourceAsPredicate";
        String LITERAL = "LITERAL";
        SimpleQuad sq = new SimpleQuad(GRAPH1,RESOURCE,RESOURCEP,RESOURCE2);
        assertEquals(("<"+GRAPH1+"> <"+RESOURCE+"> <"+RESOURCEP+"> <"+RESOURCE2+">"),sq.toString());
        sq = new SimpleQuad(GRAPH1,RESOURCE,RESOURCEP,LITERAL);
        assertEquals(("<"+GRAPH1+"> <"+RESOURCE+"> <"+RESOURCEP+"> '"+LITERAL+"'"),sq.toString());

        // But it is printed as "DEFAULT"
        sq = new SimpleQuad("DEFAULT",RESOURCE,RESOURCEP,LITERAL);
        assertEquals(("DEFAULT <"+RESOURCE+"> <"+RESOURCEP+"> '"+LITERAL+"'"),sq.toString());

    }

    /**
     * Test of sameAs method, of class SimpleQuad.
     */
    @Test
    public void testSameAs() {
        fail("Not Yet Implemented");
    }

    /**
     * Test of hashCode method, of class SimpleQuad.
     */
    @Test
    public void testHashCode() {
        fail("Not Yet Implemented");
    }

    /**
     * Test of equals method, of class SimpleQuad.
     */
    @Test
    public void testEquals() {
        fail("Not Yet Implemented");
    }
}