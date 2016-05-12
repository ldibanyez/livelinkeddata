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
public class SimpleTripleTest {

    Graph g = Graph.create();
    final String RESOURCE = "http://example.org/test";
    final String RESOURCE2 = "http://example.org/test2";
    final String RESOURCEP = "http://example.org/resourceAsPredicate";
    final String LITERAL = "LITERAL";
    final String PROPERTY = "http://www.w3.org/2002/07/owl#sameAs";
    
    public SimpleTripleTest() throws EngineException {
        // Mix of properties, resources and literals
        // TODO: Blank Nodes
        String insert = ""
			+ "INSERT DATA {"
                + "<"+RESOURCE +"> <"+RESOURCEP+"> <"+RESOURCE2 +"> ."
                + "<"+RESOURCE +"> <"+RESOURCEP+"> <"+ LITERAL +"> ."
                + "<"+RESOURCE +"> <"+PROPERTY+"> <"+ RESOURCE2 +"> ."
                + "<"+PROPERTY +"> <"+RESOURCEP+"> <"+ RESOURCE2 +"> ."
                + "<"+RESOURCE +"> <"+RESOURCEP+"> <"+ PROPERTY +"> ."
			+ "}";
        QueryProcess exec = QueryProcess.create(g);
        exec.query(insert);
    }

    /**
     * Test of getSubject method, of class SimpleTriple.
     */
    @Test
    public void testGetSubject() {

        HashSet<String> subjects = new HashSet<>();
        for(Entity ent : g.getEdges()){
            SimpleTriple st = new SimpleTriple(ent);
            subjects.add(st.getSubject());
        }
        // Resource and Property are identified completely
        assert(subjects.contains(RESOURCE));
        assert(subjects.contains(PROPERTY));

        
    }

    /**
     * Test of getObject method, of class SimpleTriple.
     */
    @Test
    public void testGetObject() {

        HashSet<String> subjects = new HashSet<>();
        for(Entity ent : g.getEdges()){
            SimpleTriple st = new SimpleTriple(ent);
            subjects.add(st.getObject());
        }

        assert(subjects.contains(RESOURCE2));
        assert(subjects.contains(PROPERTY));
        assert(subjects.contains(LITERAL));

    }

    /**
     * Test of getPredicate method, of class SimpleTriple.
     */
    @Test
    public void testGetPredicate() {
        HashSet<String> subjects = new HashSet<>();
        for(Entity ent : g.getEdges()){
            SimpleTriple st = new SimpleTriple(ent);
            subjects.add(st.getPredicate());
        }

        assert(subjects.contains(RESOURCEP));
        assert(subjects.contains(PROPERTY));

    }

    /**
     * Test of sameAs method, of class SimpleTriple.
     */
    @Test
    public void testSameAs() {
        fail("Not Yet Implemented");
    }

    /**
     * Test of sameAs method, of class SimpleTriple.
     */
    @Test
    public void testToString() {
        SimpleTriple st = new SimpleTriple(RESOURCE,PROPERTY,RESOURCE2);
        assertEquals("<"+RESOURCE+"> <"+PROPERTY+"> <"+RESOURCE2+">",st.toString());
        
        st = new SimpleTriple(RESOURCE,PROPERTY,LITERAL);
        assertEquals("<"+RESOURCE+"> <"+PROPERTY+"> '"+LITERAL+"'",st.toString());

    }

    /**
     * Test of hashCode method, of class SimpleTriple.
     */
    @Test
    public void testHashCode() {
        fail("Not Yet Implemented");
    }

    /**
     * Test of equals method, of class SimpleTriple.
     */
    @Test
    public void testEquals() {
        SimpleTriple t1 = new SimpleTriple(RESOURCE,RESOURCEP,RESOURCE2);
        SimpleTriple t2 = new SimpleTriple(RESOURCE,RESOURCEP,RESOURCE2);
        SimpleTriple t3 = new SimpleTriple(RESOURCE,PROPERTY,RESOURCE2);
        SimpleTriple t4 = new SimpleTriple(RESOURCE,RESOURCEP,LITERAL);
        SimpleTriple t5 = new SimpleTriple(RESOURCE2,RESOURCEP,RESOURCE);

        assert(t1.equals(t2));
        assertFalse(t1.equals(t3));
        assertFalse(t1.equals(t4));
        assertFalse(t1.equals(t5));
    }
}