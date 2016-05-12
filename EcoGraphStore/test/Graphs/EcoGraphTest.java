/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphs;

import Operations.IVOperation;
import Vectors.IntervalSequence;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.core.Mappings;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import java.util.Map;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author ibanez-l
 */
public class EcoGraphTest {

  EcoGraph g1,g2,receiver1,receiver2;
  String insert1;
  String insert2;
  String delete1;
  String delete2;
  String delins;
  String delins2;
  String select;
  Mappings map;
  
  public EcoGraphTest() {
	g1 = new EcoGraph("G1");	
	g2 = new EcoGraph("G2");	
	receiver1 = new EcoGraph("Receiver1");
	receiver2 = new EcoGraph("Receiver2");
  }

 @Test
  public void testGetVector(){
  	Map<String,IntervalSequence> IS = g1.getVector();
	assertTrue(IS.equals(g1.vector));
	IS.get(g1.id).add(3);
	assertFalse(IS.equals(g1.vector));
	
  }

  // Updates on one graph are not broken

  @Test
  public void testInsertDataSimple() throws Exception {
  
	System.out.println("INSERT DATA simple");

	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";

	Map<String,IntervalSequence> vecBefore = g1.getVector();
	g1.query(insert1);
	Map<String,IntervalSequence> vecAfter = g1.getVector();
	System.out.println(g1.display());
	// Cardinality and size are the same
	assertEquals(4,g1.size());
	assertEquals(4,g1.cardinality());

	// Clock moved 4 ticks.
	// All others (if any) are the same
	// assertTrue(vecBefore.get(g1.id). == vecAfter.get(g1.id) - 4);
	Set<String> keys = vecBefore.keySet();
	keys.remove(g1.id);
	for(String key : keys){
		assertEquals(vecBefore.get(key),vecAfter.get(g1.id));
	}

	// TODO: The downstreamed triples are the same as the inserted

	System.out.println("-----------------");
  
  }

  @Test 
  public void testInsertDataSomeRepeated() throws EngineException {
	//Insert repeated triples
	System.out.println("INSERT DATA Some repeated");
	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";
	g1.query(insert1);
  
	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book3> dc:creator 'G. Flaubert' ."
			+ "<http://example/book3> dc:title 'Mme Bovary'."
			+ "}";

	Map<String,IntervalSequence> vecBefore = g1.getVector();
	g1.query(insert1);
	Map<String,IntervalSequence> vecAfter = g1.getVector();

	System.out.println(g1.display());
	//Size and cardinality changed accordingly
	assertEquals(6,g1.size());
	assertEquals(6,g1.cardinality());

	// 6 triples inserted, 6 ticks on the clock
	// All others (if any) are the same
	// assertTrue(vecBefore.get(g1.id).isSuccessor(vecAfter.get(g1.id)));
	Set<String> keys = vecBefore.keySet();
	keys.remove(g1.id);
	for(String key : keys){
		assertEquals(vecBefore.get(key),vecAfter.get(g1.id));
	}

	// We have four triples with id 1 
    /*
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g1.id+"#1"+"')}";
	map = g1.query(select,true);
	assertEquals(4,map.size());

	//  And two with id 2
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g1.id+"#2"+"')}";
	map = g1.query(select,true);
	assertEquals(2,map.size());
    */

	System.out.println("-----------------");
  
	// TODO: The downstreamed triples are the same as the inserted

  }

  @Test 
  public void testInsertDataAllRepeated() throws EngineException {
	//Insert repeated triples
	System.out.println("INSERT DATA All repeated");
	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";
	g1.query(insert1);
  
	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
		//	+ "<http://example/book3> dc:creator 'G. Flaubert' ."
		//	+ "<http://example/book3> dc:title 'Mme Bovary'."
			+ "}";

	Map<String,IntervalSequence> vecBefore = g1.getVector();
	g1.query(insert1);
	Map<String,IntervalSequence> vecAfter = g1.getVector();

	Graph graph = g1.getGraph();
	//System.out.println(graph.display());
	//Size and cardinality did not changed
	assertEquals(4,g1.size());
	assertEquals(4,g1.cardinality());
	// Vector did not changed
	assertEquals(vecBefore.keySet(),vecAfter.keySet());
	for(String key : vecBefore.keySet()){
	  assertEquals(vecBefore.get(key),vecAfter.get(key));
	}
	// Tags did not change
    /*
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g1.id+"#"+vecAfter.get(g1.id).max() + "')}";

	map = g1.query(select,true);
	assertEquals(4,map.size());
*/
	// TODO: The downstreamed triples set is empty

	System.out.println("-----------------");
  
  }

  @Test
  public void testDeleteNonExistant() throws EngineException{

	System.out.println("DELETE DATA non present");

	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";
	g1.query(insert1);
	
	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book3> dc:creator 'G. Flaubert' ."
			+ "<http://example/book3> dc:title 'Mme Bovary'."
			+ "}";
	
	g1.query(insert1);

	delete1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE DATA {"
			+ "<http://example/book6> dc:title 'A new book'."
			+ "<http://example/book6> dc:creator 'A.N. Other' ."
			+ "<http://example/book3> dc:creator 'G. Flabert' ."
			+ "<http://example/book3> dc:title 'Mme Bobary'."
			+ "}";

	Map<String,IntervalSequence> vecBefore = g1.getVector();
	g1.query(delete1);
	Map<String,IntervalSequence> vecAfter = g1.getVector();
	Graph graph = g1.getGraph();
	//System.out.println(graph.display());

	// Cardinality and Size unchanged
	assertEquals(6,g1.size());
	assertEquals(6,g1.cardinality());

	// Vector did not change
	assertEquals(vecBefore.keySet(),vecAfter.keySet());
	for(String key : vecBefore.keySet()){
	  assertEquals(vecBefore.get(key),vecAfter.get(key));
	}

	// Tags did not changed
	// We have four triples with id 1 
    /*
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g1.id+"#1"+"')}";
	map = g1.query(select,true);
	assertEquals(4,map.size());
	//  And two with id 2
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g1.id+"#2"+"')}";
	map = g1.query(select,true);
	assertEquals(2,map.size());
    */
	
	// TODO: The downstreamed triples set is empty.

	System.out.println("-----------------");

  }

  @Test
  public void testDeleteSomeExistant() throws Exception{
	//Delete DATA some present triples

	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";
	g1.query(insert1);
	
	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book3> dc:creator 'G. Flaubert' ."
			+ "<http://example/book3> dc:title 'Mme Bovary'."
			+ "}";
	
	g1.query(insert1);


	System.out.println("DELETE DATA some present");
	delete1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE DATA {"
			+ "<http://example/book1> dc:title 'A new dictionary'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book3> dc:creator 'G. Flaubert' ."
			+ "<http://example/book3> dc:title 'Mme Bobary'."
			+ "}";

	Map<String,IntervalSequence> vecBefore = g1.getVector();
	g1.query(delete1);
	Map<String,IntervalSequence> vecAfter = g1.getVector();
	Graph graph = g1.getGraph();
	//System.out.println(graph.display());
	// Size and cardinality unchanged
	assertEquals(4,g1.size());
	assertEquals(4,g1.cardinality());
	// Vector did not change
	assertEquals(vecBefore.keySet(),vecAfter.keySet());
	for(String key : vecBefore.keySet()){
	  assertEquals(vecBefore.get(key),vecAfter.get(key));
	}

    /*
	// Tags did not changed
	// We have three triples with id 1 
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g1.id+"#1"+"')}";
	map = g1.query(select,true);
	assertEquals(3,map.size());

	//  And one with id 2
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g1.id+"#2"+"')}";
	map = g1.query(select,true);
	assertEquals(1,map.size());
    */

	
	// TODO: The downstreamed triples are the same as the inserted

	System.out.println("-----------------");
  }

  @Test
  public void testDeleteInsertEdit() throws EngineException{

	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";
	g1.query(insert1);

	System.out.println("DELETE-INSERT Edition");
	delins = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE { ?x dc:creator ?z } "
			+ "INSERT {?x dc:author ?z } "
			+ "WHERE {?x dc:creator ?z }";
	Map<String,IntervalSequence> vecBefore = g1.getVector();
	g1.query(delins);
	Map<String,IntervalSequence> vecAfter = g1.getVector();
	//Graph graph = g1.getGraph();
	//System.out.println(graph.display());
	// In this case we have same cardinalities
	assertEquals(4,g1.size());
	assertEquals(4,g1.cardinality());

	// clock moved only one tick in g1 component
	// All others (if any) are the same
	// assertTrue(vecBefore.get(g1.id).isSuccessor(vecAfter.get(g1.id)));
	Set<String> keys = vecBefore.keySet();
	keys.remove(g1.id);
	for(String key : keys){
		assertEquals(vecBefore.get(key),vecAfter.get(g1.id));

	}
    /*
	// We have two triples with id 1 
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g1.id+"#1"+"')}";
	map = g1.query(select,true);
	assertEquals(2,map.size());

	//  And two with id 2
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g1.id+"#2"+"')}";
	map = g1.query(select,true);
	assertEquals(2,map.size());
    */

	// TODO: The downstreamed triples are the same as the del/ins
	
	
	System.out.println("-----------------");
  
  
  }

  @Test
  public void testInsertPatternNonExistant() throws EngineException{

	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";
	g1.query(insert1);

	System.out.println("INSERT pattern");
	insert2 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT {?z dc:category 'Writer' } "
			+ "WHERE {?x dc:creator ?z }";
	Map<String,IntervalSequence> vecBefore = g1.getVector();
	g1.query(insert2);
	Map<String,IntervalSequence> vecAfter = g1.getVector();
	//Graph graph = g1.getGraph();
	//System.out.println(graph.display());
	// Cardinality 
	assertEquals(6,g1.size());
	assertEquals(6,g1.size());
	//  TODO: Change test from one id per operation to id per triple
	// All others (if any) are the same
	// assertTrue(vecBefore.get(g1.id).isSuccessor(vecAfter.get(g1.id)));
	Set<String> keys = vecBefore.keySet();
	keys.remove(g1.id);
	for(String key : keys){
		assertEquals(vecBefore.get(key),vecAfter.get(g1.id)); 
	}
    /*
	// We have four triples with id 1 
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g1.id+"#1"+"')}";
	map = g1.query(select,true);
	assertEquals(4,map.size());

	//  And two with id 2
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g1.id+"#2"+"')}";
	map = g1.query(select,true);
	assertEquals(2,map.size());
    */
	
	System.out.println("-----------------");
  
  }

  // Eye to this one, which is the good output?
  @Test
  public void testInsertPatternExistant() throws EngineException{
  
	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";
	g1.query(insert1);

	System.out.println("INSERT pattern with Existant Triples");
	insert2 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT {?x dc:creator 'Balzac' } "
			+ "WHERE {?x ?y ?z }";
	Map<String,IntervalSequence> vecBefore = g1.getVector();
	g1.query(insert2);
	Map<String,IntervalSequence> vecAfter = g1.getVector();
	//System.out.println(g1.display());
	// Cardinality 
	assertEquals(5,g1.size());
	assertEquals(5,g1.cardinality());
	// clock moved only one tick in g1 component
	// All others (if any) are the same
	assertTrue(vecBefore.get(g1.id).isSuccessor(vecAfter.get(g1.id)));
	Set<String> keys = vecBefore.keySet();
	keys.remove(g1.id);
	for(String key : keys){
		assertEquals(vecBefore.get(key),vecAfter.get(g1.id)); 
	}
    /*
	// We have four triples with id 1 
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g1.id+"#1"+"')}";
	map = g1.query(select,true);
	assertEquals(4,map.size());

	//  And one with id 2
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g1.id+"#2"+"')}";
	map = g1.query(select,true);
	assertEquals(1,map.size());
    */
  }

  @Test
  public void testDeletePattern() throws EngineException{
  
	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";
	g1.query(insert1);

	System.out.println("DELETE pattern");
	delete1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE {?x dc:creator ?z } "
			+ "WHERE {?x ?y ?z }";
	Map<String,IntervalSequence> vecBefore = g1.getVector();
	g1.query(delete1);
	Map<String,IntervalSequence> vecAfter = g1.getVector();
	//System.out.println(g1.display());

	// Size and cardinality 
	assertEquals(2,g1.size());
	assertEquals(2,g1.cardinality());
	// Vector did not change
	assertEquals(vecBefore.keySet(),vecAfter.keySet());
	for(String key : vecBefore.keySet()){
	  assertEquals(vecBefore.get(key),vecAfter.get(key));
	}

    /*
	// Tags did not changed
	// We have two triples with id 1 
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g1.id+"#1"+"')}";
	map = g1.query(select,true);
	assertEquals(2,map.size());
    */

	System.out.println("-----------------");

  
  }
  
  @Test
  public void testClear() throws Exception {

	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";
	g1.query(insert1);

	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book3> dc:creator 'G. Flaubert' ."
			+ "<http://example/book3> dc:title 'Mme Bovary'."
			+ "}";
	
	g1.query(insert1);


	System.out.println("Clear");
	delete1 = "CLEAR DEFAULT";
	Map<String,IntervalSequence> vecBefore = g1.getVector();
	g1.query(delete1);
	Map<String,IntervalSequence> vecAfter = g1.getVector();
	//System.out.println(g1.display());
	// Size and cardinality 0
	assertEquals(0,g1.size());
	assertEquals(0,g1.cardinality());
	// Vector did not change
	assertEquals(vecBefore.keySet(),vecAfter.keySet());
	for(String key : vecBefore.keySet()){
	  assertEquals(vecBefore.get(key),vecAfter.get(key));
	}
	System.out.println("-----------------");

  }

  @Test
  public void testGetGraph() {
  }

  // Compare is broken when using quads...!
  //@Test
  public void testCompare() throws EngineException{

	System.out.println("Compare graphs");
	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' "
			+ ".}";

	Graph c1 = Graph.create();
	c1.init();
	Graph c2 = Graph.create();
	c2.init();
	QueryProcess exec1 = QueryProcess.create(c1);
	QueryProcess exec2 = QueryProcess.create(c2);

	assertTrue(c1.compare(c2));
	assertTrue(g1.compare(g2));
	exec1.query(insert1);
	exec2.query(insert1);
	g1.query(insert1);
	g2.query(insert1);
	assertTrue(c1.compare(c2));
	assertTrue(g1.compare(g2));
	insert2 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book3> dc:title 'Isla misteriosa'."
			+ "<http://example/book3> dc:creator 'Jules Verne' "
			+ ".}";
	g2.query(insert2);
	assertFalse(c1.compare(c2));
	assertFalse(g1.compare(g2));


 }

 @Test 
  public void testSameGraph() throws EngineException{
  
	System.out.println("Test SameGraph");
	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' "
			+ ".}";
	assertTrue(g1.sameGraph(g2));
	
	g1.query(insert1);
	g2.query(insert1);
	assertTrue(g1.sameGraph(g2));
	assertTrue(g2.sameGraph(g1));
	
	insert2 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book3> dc:title 'Isla misteriosa'."
			+ "<http://example/book3> dc:creator 'Jules Verne' "
			+ ".}";
	g2.query(insert2);
	assertFalse(g1.sameGraph(g2));
	assertFalse(g2.sameGraph(g1));
  
	delete2 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE DATA {"
			+ "<http://example/book3> dc:title 'Isla misteriosa'."
			+ "<http://example/book3> dc:creator 'Jules Verne' "
			+ ".}";
	g2.query(delete2);
	assertTrue(g1.sameGraph(g2));
	assertTrue(g2.sameGraph(g1));
  }
  
  // CRDT Convergence and Intentions


  @Test
  public void testTwoInsertsDisjoint() throws EngineException{
  
	System.out.println("INSERT DATA disjoint");
	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";
	insert2 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' . "
			+ "}";

	g1.query(insert1);
	g2.query(insert2);
	receiver1.applyEffect(g1.getLastOperation());
	receiver1.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g1.getLastOperation());
    System.out.println(receiver1.display());
    System.out.println(receiver2.display());
	assertEquals(4,receiver1.size());
	assertEquals(4,receiver2.size());
	assertEquals(4,receiver1.cardinality());
	assertEquals(4,receiver2.cardinality());
	assertTrue(receiver1.sameGraph(receiver2));
	/*
	Graph gr1 = receiver1.getGraph();
	Graph gr2 = receiver2.getGraph();
	System.out.println(gr1.display());
	System.out.println("#############");
	System.out.println(gr2.display());
	*/ 
    /*
	// We have two triples with id G1#1 
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g1.id+"#1"+"')}";
	map = receiver1.query(select);
	assertEquals(2,map.size());
	map = receiver2.query(select);
	assertEquals(2,map.size());

	//  And two with id G2#2
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g2.id+"#1"+"')}";
	map = receiver1.query(select,true);
	assertEquals(2,map.size());
	map = receiver2.query(select,true);
	assertEquals(2,map.size());
	assertTrue(receiver1.samePayload(receiver2));
    */

	// Vector assertions. 

	// Size 3 for each (g1, g2 and themselves)
	assertEquals(3,receiver1.vector.size());
	assertEquals(3,receiver2.vector.size());

	// Two tick for g1.id and g2.id
	assertEquals(2,receiver1.vector.get(g1.id).max());
	assertEquals(2,receiver2.vector.get(g1.id).max());
	assertEquals(2,receiver1.vector.get(g2.id).max());
	assertEquals(2,receiver2.vector.get(g2.id).max());

	System.out.println("-----------------");
  
  }

  @Test
  public void testInsertNonDisjoint() throws EngineException{
  
	System.out.println("INSERT DATA Non-disjoint");
	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' "
			+ "}";
	insert2 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book1> dc:creator 'A.N. Other' "
			+ "}";

	g1.query(insert1);
	g2.query(insert2);
	receiver1.applyEffect(g1.getLastOperation());
	receiver1.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g1.getLastOperation());
	/*
	Graph gr1 = receiver1.getGraph();
	Graph gr2 = receiver2.getGraph();
	System.out.println("Receiver 1 ==>");
	System.out.println(gr1.display());
	System.out.println("Receiver 2 ==>");
	System.out.println(gr2.display());
	*/
	assertEquals(receiver1.size(),8);
	assertEquals(receiver2.size(),8);
	assertEquals(receiver1.cardinality(),4);
	assertEquals(receiver2.cardinality(),4);
	assertTrue(receiver1.sameGraph(g2));
	assertTrue(receiver1.samePayload(receiver2));

	// Vector assertions. 

	// Size 3 for each (g1, g2 and themselves)
	assertEquals(3,receiver1.vector.size());
	assertEquals(3,receiver2.vector.size());

	// Four ticks for g1.id and g2.id
	assertEquals(4,receiver1.vector.get(g1.id).max());
	assertEquals(4,receiver2.vector.get(g1.id).max());
	assertEquals(4,receiver1.vector.get(g2.id).max());
	assertEquals(4,receiver2.vector.get(g2.id).max());
	System.out.println("-----------------");
  
  }

  //TODO: The right test case is mutual synchro, vs a third party consuming 
  @Test
  public void testSamePayloadSameStart() throws EngineException{

	System.out.println("Test ");
	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";

	assertTrue(g1.samePayload(g2));

	g1.query(insert1);
	g2.query(insert1);
	assertFalse(g1.samePayload(g2));
	
	delete2 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE DATA {"
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";
	g2.query(delete2);
	g2.applyEffect(g1.getLastOperation());
	assertTrue(g1.samePayload(g2));
	System.out.println("-----------------");
  
  }

  @Test
  public void testSamePayloadDiffStart() throws EngineException{

	System.out.println("Test Same Payload");
	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";
	insert2 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "}";

	assertTrue(g1.samePayload(g2));

	g1.query(insert1);
	g2.query(insert2);
	assertFalse(g1.samePayload(g2));
	
	g2.applyEffect(g1.getLastOperation());
	g1.applyEffect(g2.getLastOperation());
	assertTrue(g1.samePayload(g2));


	receiver1.applyEffect(g1.getLastOperation());
	receiver1.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g1.getLastOperation());
	receiver2.applyEffect(g2.getLastOperation());

	assertTrue(receiver1.samePayload(receiver2));
	System.out.println("-----------------");
  
  }

  // Disjoint abstractly, coming from the same operation

  @Test
  public void testDeleteDisjoint1() throws EngineException{
  
	System.out.println("DELETE DATA disjoint 1");

	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book1> dc:creator 'A.N. Other' "
			+ "}";

	delete1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE DATA {"
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "}";
	delete2 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE DATA {"
			+ "<http://example/book1> dc:creator 'A.N. Other' .}";

	g1.query(insert1);
	g2.applyEffect(g1.getLastOperation());
    System.out.println(g1.listener.getLastInserted());
    System.out.println(g1.getLastOperation());
	receiver1.applyEffect(g1.getLastOperation());
	receiver2.applyEffect(g1.getLastOperation());

	g1.query(delete1);
	g2.query(delete2);
	receiver1.applyEffect(g1.getLastOperation());
	receiver1.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g1.getLastOperation());

    System.out.println(g1.getLastOperation());
    System.out.println(g2.getLastOperation());
    System.out.println(receiver1.display());
    System.out.println(receiver2.display());
	assertEquals(2,receiver1.size());
	assertEquals(2,receiver2.size());
	assertEquals(receiver1.cardinality(),2);
	assertEquals(receiver2.cardinality(),2);

	assertTrue(receiver1.samePayload(receiver2));
    /*
	// We have two triples with id G1#1 
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g1.id+"#1"+"')}";
	map = receiver1.query(select,true);
	assertEquals(2,map.size());
    */
	// Vector assertions. 

	// Size 3 for each 
    System.out.println(receiver1.vector.size());
	assertEquals(2,receiver1.vector.size());
	assertEquals(2,receiver2.vector.size());

	// One tick for g1.id, zero for g2.id (Insert was coming from I)
	assertEquals(4,receiver1.vector.get(g1.id).max());
	// TODO: Answer the question: A transition node should be reflected in the vector?
    //assertEquals(0,receiver1.vector.get(g2.id).max());
	assertEquals(4,receiver2.vector.get(g1.id).max());
	//assertEquals(0,receiver2.vector.get(g2.id).max());
	
	System.out.println("-----------------");

  }
		 

  // Disjoint at the CRDT level
  @Test
  public void testDeleteDisjoint2() throws EngineException{
  
	System.out.println("DELETE DATA disjoint 2");

	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book1> dc:creator 'A.N. Other' "
			+ "}";

	delete1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE DATA {"
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "}";
	delete2 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE DATA {"
			+ "<http://example/book1> dc:creator 'A.N. Other' .}";

	g1.query(insert1);
	g2.query(insert1);
	receiver1.applyEffect(g1.getLastOperation());
	receiver1.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g1.getLastOperation());

	g1.query(delete1);
	g2.query(delete2);
	receiver1.applyEffect(g1.getLastOperation());
	receiver1.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g1.getLastOperation());
	/*
	Graph gr1 = receiver1.getGraph();
	Graph gr2 = receiver2.getGraph();
	System.out.println("Receiver 1 ==>");
	System.out.println(gr1.display());
	System.out.println("Receiver 2 ==>");
	System.out.println(gr2.display());
	*/
	assertEquals(6,receiver1.size());
	assertEquals(6,receiver2.size());
	assertEquals(receiver1.cardinality(),4);
	assertEquals(receiver2.cardinality(),4);

	assertTrue(receiver1.sameGraph(receiver2));
	assertTrue(receiver1.samePayload(receiver2));
	// I've just remember that this is the problem when playing with tuple queries
	// Seems something is messed with the index and some triples could get ignored
	// check Olivier's mails
    /*
	// We have three triples with id G1#1 
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g1.id+"#1"+"')}";
	map = receiver1.query(select,true);
	assertEquals(3,map.size());

	//  And three with id G2#2
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g2.id+"#1"+"')}";
	map = receiver1.query(select,true);
	assertEquals(3,map.size());
    */
	// Vector assertions. 

	// Size 3 for each (g1, g2 and themselves)
	assertEquals(3,receiver1.vector.size());
	assertEquals(3,receiver2.vector.size());

	// Four ticks for g1.id and g2.id
	assertEquals(4,receiver1.vector.get(g1.id).max());
	assertEquals(4,receiver2.vector.get(g1.id).max());
	assertEquals(4,receiver1.vector.get(g2.id).max());
	assertEquals(4,receiver2.vector.get(g2.id).max());
	
	System.out.println("-----------------");

  }
  // Same triple different tags
  @Test
  public void testDeleteNonDisjoint() throws EngineException{
  
	System.out.println("DELETE DATA NonDisjoint");

	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book1> dc:creator 'A.N. Other' "
			+ "}";

	delete1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE DATA {"
			+ "<http://example/book1> dc:creator 'A.N. Other' .}";

    // Concurrent insertion exchanged.
	g1.query(insert1);
	g2.query(insert1);
	g2.applyEffect(g1.getLastOperation());
	g1.applyEffect(g2.getLastOperation());

	receiver1.applyEffect(g1.getLastOperation());
	receiver1.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g1.getLastOperation());
    System.out.println(receiver1.display());
    System.out.println(receiver2.display());

	receiver1.query(delete1);

	assertEquals(6,receiver1.size());
	assertEquals(3,receiver1.cardinality());

    /*
	//  three with id G1#1
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g1.id+"#1"+"')}";
	map = receiver1.query(select,true);
	assertEquals(3,map.size());

	//  And three with id G2#1
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g2.id+"#1"+"')}";
	map = receiver1.query(select,true);
	assertEquals(3,map.size());
    */
	receiver2.applyEffect(receiver1.getLastOperation());
    System.out.println(receiver1.getLastOperation());
    System.out.println(receiver1.display());
    System.out.println(receiver2.display());
	assertTrue(receiver2.samePayload(receiver1));

	// Vector assertions. 

    // Three for all
    //
	assertEquals(3,receiver1.vector.size());
	assertEquals(3,receiver2.vector.size());

	// One tick for g1.id and g2.id
	assertEquals(4,receiver1.vector.get(g1.id).max());
	assertEquals(4,receiver2.vector.get(g1.id).max());
	assertEquals(4,receiver1.vector.get(g2.id).max());
	assertEquals(4,receiver2.vector.get(g2.id).max());
	//assertEquals(0,receiver2.vector.get(receiver1.id).max());

	System.out.println("-----------------");
  }

  // Disjoint abstractly, coming from the same operation
  @Test
  public void testDeleteInsertDisjoint1() throws EngineException{


	System.out.println("DELETE/INSERT disjoint 1");

	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book1> dc:creator 'A.N. Other' "
			+ "}";

	delins = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE { ?x dc:creator ?z } "
			+ "INSERT {?x dc:author ?z } "
			+ "WHERE {?x dc:creator ?z }";

	delins2 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE { ?x dc:title ?z } "
			+ "INSERT {?x dc:name ?z } "
			+ "WHERE {?x dc:title ?z }";

	g1.query(insert1);
	g2.applyEffect(g1.getLastOperation());
	receiver1.applyEffect(g1.getLastOperation());
	receiver2.applyEffect(g1.getLastOperation());
	
	g1.query(delins);
	g2.query(delins2);

	receiver1.applyEffect(g1.getLastOperation());
	receiver1.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g1.getLastOperation());

	assertEquals(4,receiver1.size());
	assertEquals(4,receiver1.cardinality());

    /*
	// two triples  with id G1#2
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g1.id+"#2"+"')}";
	map = receiver1.query(select,true);
	assertEquals(2,map.size());

	// two triples  with id G2#1
	// as G2 consumed from G1 at the beginning
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g2.id+"#1"+"')}";
	map = receiver1.query(select,true);
	assertEquals(2,map.size());
    */
	assertTrue(receiver1.samePayload(receiver2));
	
	// Vector assertions. 

	// Size 3 for each (g1, g2 and themselves)
	assertEquals(3,receiver1.vector.size());
	assertEquals(3,receiver2.vector.size());

	// two tick for g1.id, one for g2.id
	assertEquals(6,receiver1.vector.get(g1.id).max());
	assertEquals(6,receiver2.vector.get(g1.id).max());
	assertEquals(2,receiver1.vector.get(g2.id).max());
	assertEquals(2,receiver2.vector.get(g2.id).max());

	System.out.println("-----------------");

}

  // Disjoint at the CRDT level too
  @Test
  public void testDeleteInsertDisjoint2() throws EngineException{


	System.out.println("DELETE/INSERT disjoint 2");

	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book1> dc:creator 'A.N. Other' "
			+ "}";

	delins = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE { ?x dc:creator ?z } "
			+ "INSERT {?x dc:author ?z } "
			+ "WHERE {?x dc:creator ?z }";

	delins2 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE { ?x dc:title ?z } "
			+ "INSERT {?x dc:name ?z } "
			+ "WHERE {?x dc:title ?z }";

	g1.query(insert1);
	g2.query(insert1);
	receiver1.applyEffect(g1.getLastOperation());
	receiver1.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g1.getLastOperation());
	
	g1.query(delins);
	g2.query(delins2);

	receiver1.applyEffect(g1.getLastOperation());
	receiver1.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g1.getLastOperation());

	assertEquals(8,receiver1.size());
	assertEquals(8,receiver1.cardinality());

    /*
	// two triples  with id G1#1
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g1.id+"#1"+"')}";
	map = receiver1.query(select,true);
	assertEquals(2,map.size());

	// two triples  with id G2#1
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g2.id+"#1"+"')}";
	map = receiver1.query(select,true);
	assertEquals(2,map.size());
    */

	assertTrue(receiver1.samePayload(receiver2));

	// Vector assertions. 

	// Size 3 for each (g1, g2 and themselves)
	assertEquals(3,receiver1.vector.size());
	assertEquals(3,receiver2.vector.size());

	// six ticks for g1.id and g2.id
	assertEquals(6,receiver1.vector.get(g1.id).max());
	assertEquals(6,receiver2.vector.get(g1.id).max());
	assertEquals(6,receiver1.vector.get(g2.id).max());
	assertEquals(6,receiver2.vector.get(g2.id).max());
	System.out.println("-----------------");

}

  // Different triples, same tag
  @Test
  public void testDeleteInsertNonDisjoint() throws EngineException{


	System.out.println("DELETE/INSERT Non Disjoint");

	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book1> dc:creator 'A.N. Other' "
			+ "}";

	delins = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE { ?x dc:creator ?z } "
			+ "INSERT {?x dc:author ?z } "
			+ "WHERE {?x dc:creator ?z }";


	g1.query(insert1);
	g2.query(insert1);


	receiver1.applyEffect(g1.getLastOperation());
	receiver1.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g1.getLastOperation());

	receiver1.query(delins);

	receiver2.applyEffect(receiver1.getLastOperation());
    System.out.println(receiver1.getLastOperation());

	assertEquals(6,receiver2.size());
	assertEquals(4,receiver2.cardinality());

    /*
	// two triples  with id G1#1
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g1.id+"#1"+"')}";
	map = receiver1.query(select,true);
	assertEquals(2,map.size());

	// two triples  with id G2#1
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + g2.id+"#1"+"')}";
	map = receiver1.query(select,true);
	assertEquals(2,map.size());

	// two triples  with id receiver1#1
	select = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "SELECT * WHERE{tuple(?p ?s ?o "
			+ "'" + receiver1.id+"#1"+"')}";
	map = receiver1.query(select,true);
	assertEquals(2,map.size());
	assertTrue(receiver1.samePayload(receiver2));
    */
	// Vector assertions. 

	// Size 3 for 1 (g1, g2 and itself)
	// Size 4 for 2 (g1, g2, r1 and itself)
	assertEquals(3,receiver1.vector.size());
	assertEquals(4,receiver2.vector.size());

	// four ticks for g1.id and g2.id
	// two ticks for r1.
	assertEquals(4,receiver1.vector.get(g1.id).max());
	assertEquals(4,receiver2.vector.get(g1.id).max());
	assertEquals(4,receiver1.vector.get(g2.id).max());
	assertEquals(4,receiver2.vector.get(g2.id).max());
	assertEquals(2,receiver1.vector.get(receiver1.id).max());
	assertEquals(2,receiver2.vector.get(receiver1.id).max());

	System.out.println("-----------------");
}

@Test 
public void testPartialTransactionDelete() throws EngineException{

	System.out.println("Partial delete of a transaction");

	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";

	delete1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE DATA{ "
			+ "<http://example/book1> dc:title 'A new book'."
			+ "}";

    g1.query(insert1);
    IVOperation op1 = g1.getLastOperation();
    System.out.println(op1);
    g1.query(delete1);
    IVOperation op2 = g1.getLastOperation();
    System.out.println(op2);

    receiver1.applyEffect(op1);
    receiver2.applyEffect(op2);
    System.out.println(receiver1.display());
    System.out.println(receiver2.display());

    receiver1.applyEffect(op2);
    receiver2.applyEffect(op1);
    
    System.out.println(receiver1.display());
    System.out.println(receiver2.display());
	assertTrue(receiver1.samePayload(receiver2));

}
  

  //@Test TODO
  public void testDSInsertPattern(){
  
  }

  //@Test TODO
  public void testDSDeletePattern(){
  
  }

  // Operations are logged in anycase.

  @Test
  public void testgetLastOperation2() throws EngineException{
  
	System.out.println("Test getLastOperation");

	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			//+ "<http://example/book2> dc:creator 'Balzac' ."
			//+ "<http://example/book1> dc:creator 'A.N. Other' "
			+ "}";

	g1.query(insert1);

	IVOperation op = g1.getLastOperation();
  
	insert2 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "}";

	delete1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE DATA {"
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "}";

	delins = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE { ?x dc:creator ?z } "
			+ "INSERT {?x dc:author ?z } "
			+ "WHERE {?x dc:creator ?z }";

	// The application of an empty op does not change the last op
	g1.query(insert1);
	assertEquals(op,
			g1.getLastOperation());
	g1.query(delete1);
	assertEquals(op,
			g1.getLastOperation());
	g1.query(delins);
	assertEquals(op,
			g1.getLastOperation());


	insert2 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book2> dc:creator 'Balzac' "
			+ "<http://example/book1> dc:creator 'A.N. Other' "
			+ "}";

	delins = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE { ?x dc:creator ?z } "
			+ "INSERT {?x dc:author ?z } "
			+ "WHERE {?x dc:creator ?z }";

	delete2 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "}";


	// A non empty op changes it 
	g1.query(insert2);
	assertFalse(op.equals(
			g1.getLastOperation()));
	g1.query(delins);
	assertFalse(op.equals(
			g1.getLastOperation()));
	g1.query(delete2);
	assertFalse(op.equals(
			g1.getLastOperation()));

	// Now the applyEffects

	// All applyEffects change the Last Operation
	// DISCUSSION: The inserts detected as repeated too?
	// I think yes, is responsability of the broadcaster or the consumer

  }


}
