/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Operations;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class SimplePentaTest {

    String tag = "TAG";
    String GRAPH1 = "http://example.org/graph1";
    String RESOURCE = "http://example.org/test";
    String RESOURCE2 = "http://example.org/test2";
    String RESOURCEP = "http://example.org/resourceAsPredicate";
    String LITERAL = "LITERAL";
    
    public SimplePentaTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of sameAs method, of class SimplePenta.
     */
    @Test
    public void testSameAs() {
        fail("Not yet tested");
    }

    /**
     * Test of getTag method, of class SimplePenta.
     */
    @Test
    public void testGetTag() {
        SimplePenta sp = new SimplePenta(GRAPH1,RESOURCE,RESOURCEP,RESOURCE2,tag);
        assertEquals(tag,sp.getTag());
    }

    /**
     * Test of toString method, of class SimplePenta.
     */
    @Test
    public void testToString() {
        SimplePenta sp = new SimplePenta(GRAPH1,RESOURCE,RESOURCEP,RESOURCE2,tag);
        String spstring = sp.toString();
        System.out.println(spstring);
        SimplePenta sp2 = new SimplePenta(spstring);
        String spstring2 = sp2.toString();
        assertEquals(sp,sp2);
        assertEquals(spstring,spstring2);
    }

    @Test
    public void testToStringLiteral() {
        SimplePenta sp = new SimplePenta(GRAPH1,RESOURCE,RESOURCEP,LITERAL,tag);
        String spstring = sp.toString();
        System.out.println(spstring);
        SimplePenta sp2 = new SimplePenta(spstring);
        String spstring2 = sp2.toString();
        assertEquals(sp,sp2);
        assertEquals(spstring,spstring2);
    }

    /**
     * Test of printQuad method, of class SimplePenta.
     */
    @Test
    public void testPrintQuad() {
        fail("Not yet tested");
    }

    /**
     * Test of hashCode method, of class SimplePenta.
     */
    @Test
    public void testHashCode() {
        fail("Not yet tested");
    }

    /**
     * Test of equals method, of class SimplePenta.
     */
    @Test
    public void testEquals() {
        SimplePenta sp = new SimplePenta(GRAPH1,RESOURCE,RESOURCEP,RESOURCE2,tag);
        SimplePenta sp2 = new SimplePenta(GRAPH1,RESOURCE,RESOURCEP,RESOURCE2,tag);
        assertTrue(sp.equals(sp2));
        assertTrue(sp2.equals(sp));
        SimplePenta sp3 = new SimplePenta(GRAPH1,RESOURCE,RESOURCEP,RESOURCE2,tag+"XX");
        assertFalse(sp.equals(sp3));
        assertFalse(sp3.equals(sp));
    }

    @Test
    public void testEqualsLiteral(){
        SimplePenta sp = new SimplePenta(GRAPH1,RESOURCE,RESOURCEP,LITERAL,tag);
        SimplePenta sp2 = new SimplePenta(GRAPH1,RESOURCE,RESOURCEP,LITERAL,tag);
        assertTrue(sp.equals(sp2));
        assertTrue(sp2.equals(sp));
        SimplePenta sp3 = new SimplePenta(GRAPH1,RESOURCE,RESOURCEP,LITERAL,tag+"XX");
        assertFalse(sp.equals(sp3));
        assertFalse(sp3.equals(sp));
    
    }
}