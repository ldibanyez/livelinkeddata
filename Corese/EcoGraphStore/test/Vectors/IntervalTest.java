/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Vectors;

import Vectors.Interval.IntervalException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * @author ibanez-l
 */
public class IntervalTest {
  
  public IntervalTest() {
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
   * Test of contains method, of class Interval.
   */
  @Test
  public void testContains() {
	try {
	  System.out.println("contains");
	  int n = 5;
	  Interval instance = new Interval(3,4);
	  boolean expResult = false;
	  boolean result = instance.contains(n);
	  assertEquals(expResult, result);

	  instance = new Interval(3,5);
	  expResult = true;
	  result = instance.contains(n);
	  assertEquals(expResult, result);
	  
	  instance = new Interval(5,5);
	  expResult = true;
	  result = instance.contains(n);
	  assertEquals(expResult, result);
	  
	  instance = new Interval(8,19);
	  expResult = false;
	  result = instance.contains(n);
	  assertEquals(expResult, result);
	  
	} catch (IntervalException ex) {
	  Logger.getLogger(IntervalTest.class.getName()).log(Level.SEVERE, null, ex);
	}
  }

  @Rule
  public ExpectedException thrown=  ExpectedException.none();

  /**
   * Test of setLowerLimit method, of class Interval.
   */
  @Test
  public void testSetLowerLimitRight() throws Exception {
	System.out.println("setLowerLimitRight");
	int n = 5;
	Interval instance = new Interval(1,6);
	instance.setLowerLimit(n);
  }

  @Test
  public void testSetLowerLimitWrong() throws Exception {
	System.out.println("setLowerLimitWrong");
	thrown.expect(IntervalException.class);
	int n = 4;
	Interval instance = new Interval(2,3);
	instance.setLowerLimit(n);
  }

  /**
   * Test of setUpperLimit method, of class Interval.
   */

  
  @Test
  public void testSetUpperLimitWrong() throws Exception {
	thrown.expect(IntervalException.class);
    // thrown.expectMessage("Name must not be null");
	System.out.println("setUpperLimitWrong");
	int n = 2;
	Interval instance = new Interval(3,5);
	instance.setUpperLimit(n);

  }

  @Test
  public void testSetUpperLimitRight() throws Exception {
	System.out.println("setUpperLimitRight");
	int n = 6;
	Interval instance = new Interval(3,5);
	instance.setUpperLimit(n);

  }

  /**
   * Test of overlaps method, of class Interval.
   */
  @Test
  public void testOverlaps() {
	try {
	  System.out.println("overlaps");
	  Interval i;
	  Interval instance;
	  boolean expResult;
	  boolean result;
	  // Equal intervals overlap
	  i = new Interval(2,6);
	  instance = new Interval(2,6);
	  expResult = true;
	  result = instance.overlaps(i);
	  assertEquals(expResult, result);

	  // intervals with a tangent, overlap 
	  i = new Interval(2,6);
	  instance = new Interval(6,6);
	  expResult = true;
	  result = instance.overlaps(i);
	  assertEquals(expResult, result);

	  // Non overlapping in both senses 	
	  i = new Interval(2,6);
	  instance = new Interval(7,9);
	  expResult = false;
	  result = instance.overlaps(i);
	  assertEquals(expResult, result);
	  result = i.overlaps(instance);
	  assertEquals(expResult, result);
	  
	} catch (IntervalException ex) {
	  Logger.getLogger(IntervalTest.class.getName()).log(Level.SEVERE, null, ex);
	}
  }

  /**
   * Test of compareTo method, of class Interval.
   */
  @Test
  public void testCompareTo() {
	try {
	  System.out.println("compareTo");
	  Interval t = new Interval(3,6);
	  Interval instance = new Interval(3,6);
	  int expResult = 0;
	  int result = instance.compareTo(t);
	  assertEquals(expResult, result);

	  // lower lower limit => compares down
	  t = new Interval(4,5); 
	  expResult = -1;
	  result = instance.compareTo(t);
	  assertEquals(expResult, result);

	  // greater lower limit => compares up
	  t = new Interval(2,9); 
	  expResult = 1;
	  result = instance.compareTo(t);
	  assertEquals(expResult, result);

	  // equal lower limits => compare upper limits
	  t = new Interval(3,9); 
	  expResult = -1;
	  result = instance.compareTo(t);
	  assertEquals(expResult, result);

	} catch (IntervalException ex) {
	  Logger.getLogger(IntervalTest.class.getName()).log(Level.SEVERE, null, ex);
	}
  }

  @Test
  public void testIsSuccessor(){
	try {
	  Interval i1 = new Interval(1,4);
	  Interval i2 = new Interval(0,4);
	  Interval i3 = new Interval(1,5);
	  Interval i4 = new Interval(0,5);
	  assertTrue(i1.isSuccesor(i2));
	  assertTrue(i1.isSuccesor(i3));
	  assertFalse(i1.isSuccesor(i4));
	} catch (IntervalException ex) {
	  Logger.getLogger(IntervalTest.class.getName()).log(Level.SEVERE, null, ex);
	}


	}

  /**
   * Test of hashCode method, of class Interval.
   */
  // @Test
  public void testHashCode() {
	System.out.println("hashCode");
	Interval instance = new Interval();
	int expResult = 0;
	int result = instance.hashCode();
	assertEquals(expResult, result);
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
  }

  /**
   * Test of equals method, of class Interval.
   */
  // @Test
  public void testEquals() {
	System.out.println("equals");
	Object obj = null;
	Interval instance = new Interval();
	boolean expResult = false;
	boolean result = instance.equals(obj);
	assertEquals(expResult, result);
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
  }

  /**
   * Test of toString method, of class Interval.
   */
  // @Test
  public void testToString() {
	System.out.println("toString");
	Interval instance = new Interval();
	String expResult = "";
	String result = instance.toString();
	assertEquals(expResult, result);
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
  }
}
