/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Provenance;

import java.util.HashMap;
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
public class TrioMonoidTest {
    
    public TrioMonoidTest() {
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

    @Test
    public void testConstructor(){
        System.out.println("Constructor");

        // Normal input
        String input = "2*ID1 + 2*ID2 + 1*ID3 + 123456773333*ID4";
        TrioMonoid tm = new TrioMonoid(input);
        
        HashMap<Token,Long> poly = tm.polynome; 
        assert poly.size() == 4;
        System.out.println(poly.toString());

        // Negative input
        input = "-23*ID1";
        tm = new TrioMonoid(input);
        poly = tm.polynome; 
        assert poly.size() == 1;
        System.out.println(poly.toString());

        // Empty input
        input = "";
        tm = new TrioMonoid(input);
        poly = tm.polynome; 
        assert poly.isEmpty();
        
    }

    /**
     * Test of plus method, of class TrioMonoid.
     */
    @Test
    public void testPlus() {
        System.out.println("plus");

        String input = "2*ID1 + 2*ID2 + 1*ID3 + 123456773333*ID4";
        TrioMonoid tm = new TrioMonoid(input);

        // Addition
        TrioMonoid toAdd = new TrioMonoid("2*ID1 + 1*ID5");
        tm.plus(toAdd);

        TrioMonoid expected = new TrioMonoid("4*ID1 + 2*ID2 + 1*ID3 + 123456773333*ID4 + 1*ID5");
        assertEquals(expected,tm);
        System.out.println(tm.toString());

        // Substraction 
        TrioMonoid toSubstract = new TrioMonoid("-2*ID2 + -3333*ID4 + -3*ID5");
        tm.plus(toSubstract);

        expected = new TrioMonoid("4*ID1 + 1*ID3 + 123456770000*ID4 + -2*ID5");
        assertEquals(expected,tm);
        System.out.println(tm.toString());

    }

    /**
     * Test of truncPlus method, of class TrioMonoid.
     */
    @Test
    public void testTruncPlus() {
        System.out.println("plus");

        String input = "2*ID1 + 2*ID2 + 1*ID3 + 123456773333*ID4";
        TrioMonoid tm = new TrioMonoid(input);

        // Addition
        TrioMonoid toAdd = new TrioMonoid("2*ID1 + 1*ID5");
        tm.truncPlus(toAdd);

        TrioMonoid expected = new TrioMonoid("4*ID1 + 2*ID2 + 1*ID3 + 123456773333*ID4 + 1*ID5");
        assertEquals(expected,tm);
        System.out.println(tm.toString());

        // Substraction 
        TrioMonoid toSubstract = new TrioMonoid("-2*ID2 + -3333*ID4");
        tm.truncPlus(toSubstract);

        expected = new TrioMonoid("4*ID1 + 1*ID3 + 123456770000*ID4 + 1*ID5");
        assertEquals(expected,tm);
        System.out.println(tm.toString());

    }
    /**
     * Test of isZero method, of class TrioMonoid.
     */
    @Test
    public void testIsZero() {
        System.out.println("isZero");
        String input = "2*ID1 + 2*ID2 + 1*ID3 + 123456773333*ID4";
        TrioMonoid instance = new TrioMonoid(input);
        boolean result = instance.isZero();
        assertEquals(false, result);

        instance = new TrioMonoid("");
        result = instance.isZero();
        assertEquals(true, result);
        
    }

    /**
     * Test of Invert method
     */

    @Test
    public void testInvert() {
    
        System.out.println("Invert");
        String input = "2*ID1 + 2*ID2 + 1*ID3 + 123456773333*ID4";
        String output = "-2*ID1 + -2*ID2 + -1*ID3 + -123456773333*ID4";
        TrioMonoid instance = new TrioMonoid(input);
        instance.invert();
        TrioMonoid expected = new TrioMonoid(output);
        assertEquals(expected,instance);

        output = "2*ID1 + 2*ID2 + 1*ID3 + 123456773333*ID4";
        input = "-2*ID1 + -2*ID2 + -1*ID3 + -123456773333*ID4";
        instance = new TrioMonoid(input);
        instance.invert();
        expected = new TrioMonoid(output);
        assertEquals(expected,instance);

        input = "1*ID1";
        output = "-1*ID1";
        instance = new TrioMonoid(input);
        instance.invert();
        expected = new TrioMonoid(output);
        assertEquals(expected,instance);
    
    }

    /**
     * Test of toString method, of class TrioMonoid.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        String input = "2*ID1 + 2*ID2 + 1*ID3 + 123456773333*ID4";
        TrioMonoid instance = new TrioMonoid(input);
        String result = instance.toString();
        System.out.println(result);
        // TODO review the generated test code and remove the default call to fail.
    }
}