/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Vectors;

import Vectors.Interval.IntervalException;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ibanez-l
 */
public class IntervalSequenceTest {

  TreeSet<Interval> initial;
  
  public IntervalSequenceTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
  }
  
  @AfterClass
  public static void tearDownClass() {
  }
  
  @Before
  public void setUp() {
	try {
	  initial = new TreeSet<Interval>();
	  initial.add(new Interval(3,4));
	  initial.add(new Interval(6,8));
	  initial.add(new Interval(12,12));
	  initial.add(new Interval(14,16));
	} catch (IntervalException ex) {
	  Logger.getLogger(IntervalSequenceTest.class.getName()).log(Level.SEVERE, null, ex);
	}
  }
  
  @After
  public void tearDown() {
  }

  @Test
  public void testAddonEmpty() throws Exception{
	System.out.println("addonEmpty");
	IntervalSequence instance = new IntervalSequence();
	TreeSet<Interval> ctrlT = new TreeSet<>();
	ctrlT.add(new Interval(2,2));
	IntervalSequence ctrl = new IntervalSequence(ctrlT);
	boolean expResult = true;
	boolean result = instance.add(2);
	assertEquals(expResult, result);
	assertEquals(instance, ctrl);
  
  }

  /**
   * Test of add method, of class IntervalSequence.
   */
  @Test
  public void testAdd() {
	try {
	  System.out.println("add");
	  IntervalSequence instance = new IntervalSequence(initial);
	  IntervalSequence ctrl = new IntervalSequence(initial);

	  // Already in the sequence
	  
	  boolean expResult = false;
	  boolean result = instance.add(7);
	  assertEquals(expResult, result);
	  assertEquals(instance, ctrl);
	  
	  // Insert in the head

	  instance = new IntervalSequence(new TreeSet<>(initial));
	  TreeSet<Interval> ctrlT = new TreeSet<>();
	  ctrlT.add(new Interval(1,1));
	  ctrlT.add(new Interval(3,4));
	  ctrlT.add(new Interval(6,8));
	  ctrlT.add(new Interval(12,12));
	  ctrlT.add(new Interval(14,16));
	  ctrl = new IntervalSequence(ctrlT);
	  
	  expResult = true;
	  result = instance.add(1);
	  assertEquals(expResult, result);
	  assertEquals(instance, ctrl);

	  // Merge with the head

	  instance = new IntervalSequence(new TreeSet<>(initial));
	  ctrlT = new TreeSet<>();
	  ctrlT.add(new Interval(2,4));
	  ctrlT.add(new Interval(6,8));
	  ctrlT.add(new Interval(12,12));
	  ctrlT.add(new Interval(14,16));
	  ctrl = new IntervalSequence(ctrlT);
	  
	  expResult = true;
	  result = instance.add(2);
	  assertEquals(expResult, result);
	  assertEquals(instance, ctrl);

	  // Merge in the middle

	  instance = new IntervalSequence(new TreeSet<>(initial));
	  ctrlT = new TreeSet<>();
	  ctrlT.add(new Interval(3,8));
	  ctrlT.add(new Interval(12,12));
	  ctrlT.add(new Interval(14,16));
	  ctrl = new IntervalSequence(ctrlT);
	  
	  expResult = true;
	  result = instance.add(5);
	  assertEquals(expResult, result);
	  assertEquals(instance, ctrl);

	  // Singleton interval in the middle

	  instance = new IntervalSequence(new TreeSet<>(initial));
	  ctrlT = new TreeSet<>();
	  ctrlT.add(new Interval(3,4));
	  ctrlT.add(new Interval(6,8));
	  ctrlT.add(new Interval(10,10));
	  ctrlT.add(new Interval(12,12));
	  ctrlT.add(new Interval(14,16));
	  ctrl = new IntervalSequence(ctrlT);
	  
	  expResult = true;
	  result = instance.add(10);
	  assertEquals(expResult, result);
	  System.out.println(instance.toString());
	  System.out.println(ctrl.toString());
	  assertEquals(instance, ctrl);

	  // Merge with the tail 

	  instance = new IntervalSequence(new TreeSet<>(initial));
	  ctrlT = new TreeSet<>();
	  ctrlT.add(new Interval(3,4));
	  ctrlT.add(new Interval(6,8));
	  ctrlT.add(new Interval(12,12));
	  ctrlT.add(new Interval(14,17));
	  ctrl = new IntervalSequence(ctrlT);
	  
	  expResult = true;
	  result = instance.add(17);
	  assertEquals(expResult, result);
	  assertEquals(instance, ctrl);

	  // Insert in the tail 

	  instance = new IntervalSequence(new TreeSet<>(initial));
	  ctrlT = new TreeSet<>();
	  ctrlT.add(new Interval(3,4));
	  ctrlT.add(new Interval(6,8));
	  ctrlT.add(new Interval(12,12));
	  ctrlT.add(new Interval(14,16));
	  ctrlT.add(new Interval(20,20));
	  ctrl = new IntervalSequence(ctrlT);
	  
	  expResult = true;
	  result = instance.add(20);
	  assertEquals(expResult, result);
	  assertEquals(instance, ctrl);

	} catch (IntervalException ex) {
	  Logger.getLogger(IntervalSequenceTest.class.getName()).log(Level.SEVERE, null, ex);
	}
  }

  /**
   * Test of contains method, of class IntervalSequence.
   */
  @Test
  public void testContains() {
	System.out.println("contains");
	int n = 1;
	IntervalSequence instance = new IntervalSequence(new TreeSet<>(initial));
	boolean expResult = false;
	boolean result = instance.contains(n);
	assertEquals(expResult, result);

	n = 9;
	expResult = false;
	result = instance.contains(n);
	assertEquals(expResult, result);

	n = 12;
	expResult = true;
	result = instance.contains(n);
	assertEquals(expResult, result);

	n = 15;
	expResult = true;
	result = instance.contains(n);
	assertEquals(expResult, result);
	
	n = 25;
	expResult = false;
	result = instance.contains(n);
	assertEquals(expResult, result);
	
  }
}
