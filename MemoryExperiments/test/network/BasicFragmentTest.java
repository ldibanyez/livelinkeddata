/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import Operations.SimplePenta;
import Operations.SimpleTriple;
import Operations.TMOperation;
import java.util.ArrayList;
import java.util.List;
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
public class BasicFragmentTest {
    
    public BasicFragmentTest() {
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
     * Test of getTagQuery method, of class BasicFragment.
     */
    @Test
    public void testGetTagQuery() {
    }

    /**
     * Test of getFrom method, of class BasicFragment.
     */
    @Test
    public void testGetFrom() {
    }

    /**
     * Test of getSubject method, of class BasicFragment.
     */
    @Test
    public void testGetSubject() {
    }

    /**
     * Test of setSubject method, of class BasicFragment.
     */
    @Test
    public void testSetSubject() {
    }

    /**
     * Test of getPredicate method, of class BasicFragment.
     */
    @Test
    public void testGetPredicate() {
    }

    /**
     * Test of setPredicate method, of class BasicFragment.
     */
    @Test
    public void testSetPredicate() {
    }

    /**
     * Test of getObject method, of class BasicFragment.
     */
    @Test
    public void testGetObject() {
    }

    /**
     * Test of setObject method, of class BasicFragment.
     */
    @Test
    public void testSetObject() {
    }

    /**
     * Test of hashCode method, of class BasicFragment.
     */
    @Test
    public void testHashCode() {
    }

    /**
     * Test of equals method, of class BasicFragment.
     */
    @Test
    public void testEquals() {
    }

    /**
     * Test of toString method, of class BasicFragment.
     */
    @Test
    public void testToString() {
    }

    /**
     * Test of isStar method, of class BasicFragment.
     */
    @Test
    public void testIsStar() {
        BasicFragment starFragment = new BasicFragment();
        assertTrue(starFragment.isStar());
        
        BasicFragment fragment = new BasicFragment();
        fragment.setObject("\"Venezuela\"");
        assertFalse(fragment.isStar());
    }

    /**
     * Test of concernedSubOp method, of class BasicFragment.
     */
    @Test
    public void testConcernedSubOp() {
        SimplePenta sp1 = 
                new SimplePenta(
                    "DEFAULT",
                    "<http://dbpedia.org/resource/La_Guaira>",
                    "<http://dbpedia.org/ontology/Country>",
                    "<http://dbpedia.org/resource/Venezuela>",
                    "1*TEST#1");
        SimplePenta sp2 = 
                new SimplePenta(
                    "DEFAULT",
                    "<http://dbpedia.org/resource/La_Guaira>",
                    "<http://dbpedia.org/ontology/Country>",
                    "\"Venezuela\"",
                    "1*TEST#2");
        SimplePenta sp3 = 
                new SimplePenta(
                    "DEFAULT",
                    "<http://dbpedia.org/resource/La_Guaira>",
                    "<http://dbpedia.org/ontology/Country>",
                    "<http://dbpedia.org/resource/Venezuela>",
                    "1*TEST#5");
        SimplePenta sp4 = 
                new SimplePenta(
                    "DEFAULT",
                    "<http://dbpedia.org/resource/La_Guaira>",
                    "<http://dbpedia.org/ontology/Country>",
                    "\"Venezuela\"",
                    "1*TEST#6");
        List<SimplePenta> toIns = new ArrayList<>();
        List<SimplePenta> toDel = new ArrayList<>();
        toIns.add(sp1);
        toIns.add(sp2);
        toDel.add(sp3);
        toDel.add(sp4);
        List<String> mocktrace = new ArrayList<>();
        mocktrace.add("TEST");

        TMOperation op = new TMOperation(toIns,toDel,mocktrace);

        BasicFragment starFragment = new BasicFragment();
        TMOperation starOp = starFragment.concernedSubOp(op);
        assertEquals(op,starOp);

        BasicFragment frag = new BasicFragment();
        frag.setObject("<http://dbpedia.org/resource/Venezuela>");
        TMOperation subOp = frag.concernedSubOp(op);

        List<SimplePenta> toIns2 = new ArrayList<>();
        List<SimplePenta> toDel2 = new ArrayList<>();
        toIns2.add(sp1);
        toDel2.add(sp3);
        TMOperation expected = new TMOperation(toIns2,toDel2,mocktrace);

        assertEquals(expected,subOp);

        frag = new BasicFragment();
        frag.setObject("<http://dbpedia.org/resource/France>");
        subOp = frag.concernedSubOp(op);

        toIns2 = new ArrayList<>();
        toDel2 = new ArrayList<>();
        expected = new TMOperation(toIns2,toDel2,mocktrace);

        assertEquals(expected,subOp);
    }

    /**
     * Test of matches method, of class BasicFragment.
     */
    @Test
    public void testMatches() {

        SimpleTriple st1 = 
                new SimpleTriple(
                    "<http://dbpedia.org/resource/La_Guaira>",
                    "<http://dbpedia.org/ontology/Country>",
                    "<http://dbpedia.org/resource/Venezuela>");
        SimpleTriple st2 = 
                new SimpleTriple(
                    "<http://dbpedia.org/resource/La_Guaira>",
                    "<http://dbpedia.org/ontology/Country>",
                    "\"Venezuela\"");

        BasicFragment starFragment = new BasicFragment();
        assertTrue(starFragment.matches(st1));
        assertTrue(starFragment.matches(st2));

        BasicFragment predFragment = new BasicFragment();
        predFragment.setPredicate("<http://dbpedia.org/ontology/Country>");
        assertTrue(predFragment.matches(st1));
        assertTrue(predFragment.matches(st2));
        
        predFragment.setPredicate("<http://dbpedia.org/ontology/Pais>");
        assertFalse(predFragment.matches(st1));
        assertFalse(predFragment.matches(st2));

        BasicFragment objFragment = new BasicFragment();
        objFragment.setObject("\"Venezuela\"");
        assertFalse(objFragment.matches(st1));
        assertTrue(objFragment.matches(st2));
    }
}