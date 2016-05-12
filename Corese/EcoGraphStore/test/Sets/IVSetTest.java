/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Sets;

import Vectors.IntervalSequence;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ibanez-l
 */
public class IVSetTest {
  
  public IVSetTest() {
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
   * Test of size method, of class IVSet.
   */
  @Test
  public void testSize() {
	System.out.println("size");
	//Empty IVSET is 0
	IVSet instance = new IVSet("Number1");
	int expResult = 0;
	int result = instance.size();
	assertEquals(expResult, result);

	// Duplicating object with many elements from different sites
//	IntervalSequence inseq1 = new IntervalSequence();
//	inseq1.add(1);
//	IntervalSequence inseq2 = new IntervalSequence();
//	inseq2.add(2);
//	IntervalSequence inseq3 = new IntervalSequence();
//	inseq3.add(3);
//	HashMap<String,IntervalSequence> vec = new HashMap<>();
//	vec.put("3", inseq3);
//	vec.put("2", inseq2);
//	vec.put("1", inseq1);
//	Integer i = new Integer(1);
//	Integer j = new Integer(2);
//	Integer k = new Integer(3);
//	IVTuple t = new IVTuple(i,"1",1);
	


	// Size is the same with duplicates
	
  }

  /**
   * Test of isEmpty method, of class IVSet.
   */
  @Test
  public void testIsEmpty() {
	System.out.println("isEmpty");

	// New set is empty
	IVSet instance = new IVSet("1");
	boolean expResult = true;
	boolean result = instance.isEmpty();
	assertEquals(expResult, result);

	// Not empty after add
	instance.add(new Integer(1));
	expResult = false;
	result = instance.isEmpty();
	assertEquals(expResult,result);

	//Empty after delete
	instance.remove(new Integer(1));
	expResult = true;
	result = instance.isEmpty();
	assertEquals(expResult,result);
	
  }

  /**
   * Test of contains method, of class IVSet.
   */
  @Test
  public void testContains() {
	System.out.println("contains");
	Integer i = new Integer(1);
	Integer j = new Integer(2);
	IVSet instance = new IVSet("1");
	
	// empty does not contain
	boolean expResult = false;
	boolean result = instance.contains(i);
	assertEquals(expResult, result);

	//Non empty contains
	instance.add(i);
	expResult = true;
	result = instance.contains(new Integer(1));
	assertEquals(expResult, result);

	//Non empty not contains
	expResult = false;
	result = instance.contains(new Integer(2));
	assertEquals(expResult, result);

  }

  /**
   * Test of iterator method, of class IVSet.
   */
  @Test
  public void testIterator() {
	System.out.println("iterator");
	/*
	IVSet instance = null;
	Iterator expResult = null;
	Iterator result = instance.iterator();
	assertEquals(expResult, result);
	*/
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
  }

  /**
   * Test of toArray method, of class IVSet.
   */
  @Test
  public void testToArray_0args() {
	System.out.println("toArray");
	fail("The test case is a prototype.");
	IVSet instance = null;
	Object[] expResult = null;
	Object[] result = instance.toArray();
	assertArrayEquals(expResult, result);
	// TODO review the generated test code and remove the default call to fail.
  }

  /**
   * Test of toArray method, of class IVSet.
   */
  @Test
  public void testToArray_ObjectArr() {
	System.out.println("toArray");
	fail("The test case is a prototype.");
	Object[] ts = null;
	IVSet instance = null;
	Object[] expResult = null;
	Object[] result = instance.toArray(ts);
	assertArrayEquals(expResult, result);
	// TODO review the generated test code and remove the default call to fail.
  }

  /**
   * Test of add method, of class IVSet.
   */
  @Test
  public void testAdd() {
	System.out.println("add");

	// Adding on empty
	Integer i = new Integer(1);
	IVSet instance = new IVSet("Number1");
	boolean expResult = true;
	boolean result = instance.add(i);
	assertEquals(expResult, result);
	assertEquals(instance.size(),1);
	
	// Adding new in non empty
	Integer j = new Integer(2);
	expResult = true;
	result = instance.add(j);
	assertEquals(expResult, result);
	assertEquals(instance.size(),2);

	// Adding element already there
	Integer k = new Integer(1);
	expResult = false;
	result = instance.add(k);
	assertEquals(expResult, result);
	assertEquals(instance.size(),2);
	//TODO: Clock should not move.
	
  }

  /**
   * Test of insert method, of class IVSet.
   */
  @Test
  public void testInsert() {
	System.out.println("insert");
	Integer e = new Integer(1);
	IVSet instance = new IVSet("Number1");
	instance.insert(e);
	assertTrue(instance.contains(e));

	// How to test the expected XML?
  }

  /**
   * Test of delete method, of class IVSet.
   */
  @Test
  public void testDelete() {
	System.out.println("delete");
	Integer e = new Integer(1);
	Integer f = new Integer(2);
	IVSet instance = new IVSet("Number1");
	instance.insert(e);
	instance.insert(f);
	instance.delete(e);
	assertFalse(instance.contains(e));

	
	// How to test the expected XML?
  }

  
  /**
   * Test of remove method, of class IVSet.
   */
  @Test
  public void testRemove() {
	System.out.println("remove");
	IVSet instance = new IVSet("Number1");
	Integer i = new Integer(1);
	Integer j = new Integer(2);
	Integer k = new Integer(3);
	instance.add(i);
	instance.add(j);
	instance.add(k);

	Integer a = new Integer(5);
	boolean expResult = false;
	boolean result = instance.remove(a);
	assertEquals(expResult, result);
	assert(instance.contains(new Integer(1)));
	assert(instance.contains(new Integer(2)));
	assert(instance.contains(new Integer(3)));

	a = new Integer(2);
	expResult = true;
	result = instance.remove(a);
	assertEquals(expResult, result);
	assertTrue(instance.contains(new Integer(1)));
	assertFalse(instance.contains(new Integer(2)));
	assertTrue(instance.contains(new Integer(3)));

  }

  /**
   * Test of containsAll method, of class IVSet.
   */
  @Test
  public void testContainsAll() {
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
  }

  /**
   * Test of addAll method, of class IVSet.
   */
  @Test
  public void testAddAll() {

	// Nothing was present
	System.out.println("addAll");
	Collection clctn = new HashSet();
	clctn.add(1);
	clctn.add(2);
	clctn.add(3);
	IVSet instance = new IVSet("S1");

	// Preparing "Mock"
	HashSet<IVTuple> payload = new HashSet<>();
	payload.add(new IVTuple(1,"S1",1));
	payload.add(new IVTuple(2,"S1",1));
	payload.add(new IVTuple(3,"S1",1));
	HashMap<String,IntervalSequence> vec = new HashMap<>();
	IntervalSequence inseq = new IntervalSequence();
	inseq.add(0);
	inseq.add(1);
	vec.put("S1",inseq);

	assertTrue(instance.addAll(clctn));
	assertEquals(instance.payload,payload);
	assertEquals(instance.vector,vec);

	// Something was there

	instance = new IVSet("S1");
	IVSet instance2 = new IVSet("S2");
	String ins = instance2.insert(2);
	instance.applyEffect(ins);

	// Preparing "Mock"
	payload = new HashSet<>();
	payload.add(new IVTuple(1,"S1",1));
	payload.add(new IVTuple(2,"S2",1));
	payload.add(new IVTuple(3,"S1",1));
	vec = new HashMap<>();
	inseq = new IntervalSequence();
	inseq.add(0);
	inseq.add(1);
	IntervalSequence inseq1 = new IntervalSequence();
	inseq1.add(0);
	inseq1.add(1);
	vec.put("S1",inseq);
	vec.put("S2",inseq1);

	assertTrue(instance.addAll(clctn));
	assertEquals(instance.payload,payload);
	assertEquals(instance.vector,vec);

	// Everything was present

	instance = new IVSet("S1");
	instance.add(1);
	instance.add(2);
	instance.add(3);

	// Preparing "Mock"
	payload = new HashSet<>();
	payload.add(new IVTuple(1,"S1",1));
	payload.add(new IVTuple(2,"S1",2));
	payload.add(new IVTuple(3,"S1",3));
	vec = new HashMap<>();
	inseq = new IntervalSequence();
	inseq.add(0);
	inseq.add(1);
	inseq.add(2);
	inseq.add(3);
	vec.put("S1",inseq);

	assertFalse(instance.addAll(clctn));
	assertEquals(instance.payload,payload);
	assertEquals(instance.vector,vec);

  }

  /**
   * Test of retainAll method, of class IVSet.
   */
  @Test
  public void testRetainAll() {
	System.out.println("retainAll");
	/*
	Collection clctn = null;
	IVSet instance = null;
	boolean expResult = false;
	boolean result = instance.retainAll(clctn);
	assertEquals(expResult, result);
	*/ 
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
  }

  /**
   * Test of removeAll method, of class IVSet.
   */
  @Test
  public void testRemoveAll() {
	System.out.println("removeAll");
	/*
	Collection clctn = null;
	IVSet instance = null;
	boolean expResult = false;
	boolean result = instance.removeAll(clctn);
	assertEquals(expResult, result);
	*/
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
  }

  /**
   * Test of clear method, of class IVSet.
   */
  @Test
  public void testClear() {
	System.out.println("clear");
	IVSet instance = new IVSet("S1");
	instance.add(1);
	instance.add(2);
	instance.add(3);
	instance.remove(1);
	HashMap<String,IntervalSequence> vec = new HashMap<>(instance.vector);
	instance.clear();
	assertTrue(instance.isEmpty());
	assertEquals(instance.vector,vec);
  }

  /**
   * Test of compareTo method, of class IVSet.
   */
  @Test
  public void testCompareTo() {
	System.out.println("compareTo");
	/*
	Object t = null;
	IVSet instance = null;
	int expResult = 0;
	int result = instance.compareTo(t);
	assertEquals(expResult, result);
	*/ 
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
  }

  @Test
  public void testApplyEffect() {
	System.out.println("ApplyEffect");
	IVSet instance1 = new IVSet("S1");
	IVSet instance2 = new IVSet("S2");

	//TODO: Asserts relating vector.

	//simple insert
	String ins = instance1.insert(new Integer(1));
	instance2.applyEffect(ins);
	assertTrue(instance2.contains(new Integer(1)));

	//simple delete
	String del = instance2.delete(new Integer(1));
	instance1.applyEffect(del);
	assertFalse(instance1.contains(new Integer(1)));
  
	//multiple insert
	instance1 = new IVSet("S1");
	instance2 = new IVSet("S2");
	// To put the empty S2 intervalSequence in S1
	instance1.merge(instance2);
	Collection c = new HashSet();
	c.add(1);
	c.add(2);
	c.add(3);

	String multins = instance1.insertFrom(c);
	instance2.applyEffect(multins);
	assertEquals(instance1,instance2);

	//multiple delete
	c.remove(2);
	String multidel = instance1.deleteFrom(c);
	instance2.applyEffect(multidel);
	assertEquals(instance1,instance2);

	// deleteAll (clear)
	instance1 = new IVSet("S1");
	instance2 = new IVSet("S2");
	String ins1 = instance1.insert(1);
	String ins2 = instance1.insert(2);

	instance2.applyEffect(ins1);
	instance2.applyEffect(ins2);

	String cl = instance1.deleteAll();
	instance2.applyEffect(cl);
	assertTrue(instance2.isEmpty());

	//Receiving something from a replica never seen before
	// i.e. Dynamic adding

	// Insert something already deleted

	// Delete something not yet seen
	
  }

  @Test
  public void testMerge() {

	IVSet instance1 = new IVSet("S1");
	instance1.add(1);
	instance1.add(2);
	
	IVSet instance2 = new IVSet("S2");
	instance2.add(3);
	instance2.add(4);
	
	IVSet instance3 = new IVSet("S3");
	instance3.add(1);
	instance3.add(3);

  	//Idempotent
	IVSet instance = new IVSet("S1");
	instance.add(1);
	instance.add(2);

	instance1.merge(instance1);
	assertEquals(instance1,instance);

	// Preparing "Mock"
	HashSet<IVTuple> payload = new HashSet<>();
	payload.add(new IVTuple(1,"S1",1));
	payload.add(new IVTuple(2,"S1",2));
	payload.add(new IVTuple(3,"S2",1));
	payload.add(new IVTuple(4,"S2",2));
	HashMap<String,IntervalSequence> vec = new HashMap<>();
	IntervalSequence inseq = new IntervalSequence();
	inseq.add(0);
	inseq.add(1);
	inseq.add(2);
	IntervalSequence inseq1 = new IntervalSequence();
	inseq1.add(0);
	inseq1.add(1);
	inseq1.add(2);
	vec.put("S1",inseq);
	vec.put("S2",inseq1);
	
	instance1 = new IVSet("S1");
	instance1.add(1);
	instance1.add(2);

	// It is what expected (Disjoint sets)
	// TODO: Inserts to manage updates from not yet seen replica
	assertTrue(instance1.merge(instance2));
	assertEquals(instance1.vector,vec);
	assertEquals(instance1.payload,payload);

	
	//TODO The parameter did not change

	//Commutativity
	instance1 = new IVSet("S1");
	instance1.add(1);
	instance1.add(2);
	instance2.merge(instance1);
	assertEquals(instance2.vector,vec);
	assertEquals(instance2.payload,payload);

	// It is what expected (NonDisjoint sets)
	instance1 = new IVSet("S1");
	instance1.add(1);
	instance1.add(2);

	// Preparing "Mock"
	payload = new HashSet<>();
	payload.add(new IVTuple(1,"S1",1));
	payload.add(new IVTuple(2,"S1",2));
	payload.add(new IVTuple(1,"S3",1));
	payload.add(new IVTuple(3,"S3",2));
	vec = new HashMap<>();
	inseq = new IntervalSequence();
	inseq.add(0);
	inseq.add(1);
	inseq.add(2);
	inseq1 = new IntervalSequence();
	inseq1.add(0);
	inseq1.add(1);
	inseq1.add(2);
	vec.put("S1",inseq);
	vec.put("S3",inseq1);

	instance1.merge(instance3);
	assertEquals(instance1.vector,vec);
	assertEquals(instance1.payload,payload);


	// A < B => A U B = B
  
	instance1 = new IVSet("S1");
	String add1 = instance1.insert(1);
	String add2 = instance1.insert(2);
	instance2 = new IVSet("S2");
	instance2.applyEffect(add1);
	instance2.applyEffect(add2);
	instance1.remove(1);
	instance1.add(3);

	// Preparing "Mock"
	payload = new HashSet<>();
	payload.add(new IVTuple(2,"S1",2));
	payload.add(new IVTuple(3,"S1",3));
	vec = new HashMap<>();
	inseq = new IntervalSequence();
	inseq.add(0);
	inseq.add(1);
	inseq.add(2);
	inseq.add(3);
	inseq1 = new IntervalSequence();
	inseq1.add(0);
	vec.put("S1",inseq);
	vec.put("S2",inseq1);

	assertTrue(instance1.merge(instance2));
	assertEquals(instance1.vector,vec);
	assertEquals(instance1.payload,payload);

	// Clocks merge even if payload is the same

	instance1 = new IVSet("S1");
	add1 = instance1.insert(1);
	add2 = instance1.insert(2);
	instance2 = new IVSet("S2");
	instance2.applyEffect(add1);
	instance2.applyEffect(add2);
	instance1.add(3);
	instance1.remove(3);

	// Preparing "Mock"
	payload = new HashSet<>();
	payload.add(new IVTuple(1,"S1",1));
	payload.add(new IVTuple(2,"S1",2));
	vec = new HashMap<>();
	inseq = new IntervalSequence();
	inseq.add(0);
	inseq.add(1);
	inseq.add(2);
	inseq.add(3);
	inseq1 = new IntervalSequence();
	inseq1.add(0);
	vec.put("S1",inseq);
	vec.put("S2",inseq1);

	assertTrue(instance2.merge(instance1));
	assertEquals(instance2.vector,vec);
	assertEquals(instance2.payload,payload);
  
  }

  @Test
  public void testIntentions(){

	IVSet instance1 = new IVSet("S1");
	IVSet instance2 = new IVSet("S2");
	IVSet instance3 = new IVSet("S3");
	IVSet instance4 = new IVSet("S3");
	String ins1;
	String ins2;
	String del1;
	String del2;
	HashSet<IVTuple> payload = new HashSet<>();
	HashMap<String,IntervalSequence> vec = new HashMap<>();
  
  	// Two insertions (same element) 
	ins1 = instance1.insert(1);
	ins2 = instance2.insert(1);
	
	// Preparing "Mock"
	payload.add(new IVTuple(1,"S1",1));
	payload.add(new IVTuple(1,"S2",1));
	IntervalSequence inseq = new IntervalSequence();
	inseq.add(0);
	inseq.add(1);
	IntervalSequence inseq1 = new IntervalSequence();
	inseq1.add(0);
	inseq1.add(1);
	IntervalSequence inseq2 = new IntervalSequence();
	inseq2.add(0);
	vec.put("S1",inseq);
	vec.put("S2",inseq1);
	vec.put("S3",inseq2);

	instance3.applyEffect(ins1);
	instance3.applyEffect(ins2);
	instance4.applyEffect(ins2);
	instance4.applyEffect(ins1);
	assertEquals(instance3.payload,payload);
	assertEquals(instance3.vector,vec);
	assertEquals(instance4.payload,payload);
	assertEquals(instance4.vector,vec);

	//Commutativity insertion-deletion
	payload = new HashSet<>();
	vec = new HashMap<>();
	instance1 = new IVSet("S1");
	instance2 = new IVSet("S2");
	instance3 = new IVSet("S3");
	instance4 = new IVSet("S3");

	// Preparing "Mock"
	inseq = new IntervalSequence();
	inseq.add(0);
	inseq.add(1);
	inseq2 = new IntervalSequence();
	inseq2.add(0);
	vec.put("S1",inseq);
	vec.put("S3",inseq2);

	ins1 = instance1.insert(1);
	del1 = instance1.delete(1);

	instance3.applyEffect(ins1);
	instance3.applyEffect(del1);
  
	instance4.applyEffect(del1);
	instance4.applyEffect(ins1);

	assertEquals(instance3.payload,payload);
	assertEquals(instance3.vector,vec);
	assertEquals(instance4.payload,payload);
	assertEquals(instance4.vector,vec);

	// Subcase: Duplicated insertion
	instance4.applyEffect(ins1);
	instance3.applyEffect(ins1);

	assertEquals(instance3.payload,payload);
	assertEquals(instance3.vector,vec);
	assertEquals(instance4.payload,payload);
	assertEquals(instance4.vector,vec);

	
	//Insertion and Deletion (same elements)
	payload = new HashSet<>();
	vec = new HashMap<>();
	instance1 = new IVSet("S1");
	instance2 = new IVSet("S2");
	instance3 = new IVSet("S3");
	instance4 = new IVSet("S3");

	// Preparing "Mock"
	payload.add(new IVTuple(1,"S2",1));
	inseq = new IntervalSequence();
	inseq.add(0);
	inseq.add(1);
	inseq1 = new IntervalSequence();
	inseq1.add(0);
	inseq1.add(1);
	inseq2 = new IntervalSequence();
	inseq2.add(0);
	vec.put("S1",inseq);
	vec.put("S2",inseq1);
	vec.put("S3",inseq2);

	ins1 = instance1.insert(1);
	ins2 = instance2.insert(1);
	del1 = instance1.delete(1);

	instance3.applyEffect(ins1);
	instance3.applyEffect(del1);
	instance3.applyEffect(ins2);
  
	instance4.applyEffect(ins2);
	instance4.applyEffect(ins1);
	instance4.applyEffect(del1);

	assertEquals(instance3.payload,payload);
	assertEquals(instance3.vector,vec);
	assertEquals(instance4.payload,payload);
	assertEquals(instance4.vector,vec);


	//Two deletions


	
	
  }
}
