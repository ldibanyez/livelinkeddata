/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphs;

import Operations.SimplePenta;
import Operations.TMOperation;
import Provenance.TrioMonoid;
import fr.inria.acacia.corese.api.IDatatype;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.api.core.Entity;
import fr.inria.edelweiss.kgram.api.core.Node;
import fr.inria.edelweiss.kgram.core.Mapping;
import fr.inria.edelweiss.kgram.core.Mappings;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author ibanez-l
 */
public class TMGraphTest {

  TMGraph g1,g2,receiver1,receiver2;
  String insert1;
  String insert2;
  String delete1;
  String delete2;
  String delins;
  String delins2;
  String select;
  Mappings map;
  
  public TMGraphTest() {
	g1 = new TMGraph("G1");	
	g2 = new TMGraph("G2");	
	receiver1 = new TMGraph("Receiver1");
	receiver2 = new TMGraph("Receiver2");
  }

  @Before
  public void setUp(){
	g1 = new TMGraph("G1");	
	g2 = new TMGraph("G2");	
	receiver1 = new TMGraph("Receiver1");
	receiver2 = new TMGraph("Receiver2");
    
  }
          

  // Extracts the monoid of a given triple in a given graph
  TrioMonoid getMonoid(TMGraph g, String prefix ,String subject, String predicate, String object){
      /*
	String selTag = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
                  + "SELECT ?tag WHERE {"
                  + " {tuple("
			      + "dc:creator"
				  + "<http://example/book2>"  + " "
				  + "'Balzac'" + " "
				  + "?tag" + ")"
				  + "}}";
     */
    TrioMonoid monoid = new TrioMonoid("");
	String selTag = prefix 
                  + "SELECT ?tag WHERE {"
                  + " {tuple("
			      + predicate
				  + subject  + " "
				  + object + " "
				  + "?tag" + ")"
				  + "}}";
    try {
       Mappings map = g.query(selTag);
      // Weknow there is only one
      String tag1 = "";
      for (Mapping m : map){
        IDatatype dt = (IDatatype) map.getValue("?tag");
        tag1 = dt.getLabel();                 
      }
      monoid = new TrioMonoid(tag1);
      return monoid;
      
    }catch (Exception e){
         e.printStackTrace();
     }
  
      return monoid;
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

	g1.query(insert1);
	System.out.println(g1.display());
	// Cardinality and size are the same
	assertEquals(4,g1.size());
	assertEquals(4,g1.cardinality());

    // The monoid is the one expected
    // Note: This is based on the sequential adding of Corese
    // Is this changes, a more complex test with a mock object
    // will be needed.
    //TrioMonoid expected = new TrioMonoid("1*G1#4");
    TrioMonoid expected = new TrioMonoid("1*G1");
    TrioMonoid result = getMonoid(g1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            ,"<http://example/book2>"
            ,"dc:title"
            ,"'La comedie humaine'");

    assertEquals(result,expected);

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

	g1.query(insert1);

	System.out.println(g1.display());
	//Size and cardinality changed accordingly
	assertEquals(6,g1.size());
	assertEquals(6,g1.cardinality());

    // Not repeated, no change
    //TrioMonoid expected = new TrioMonoid("1*G1#4");
    TrioMonoid expected = new TrioMonoid("1*G1");
    TrioMonoid result = getMonoid(g1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            ,"<http://example/book2>"
            ,"dc:title"
            ,"'La comedie humaine'");

    assertEquals(result,expected);

    // Repeated, did not change
    //expected = new TrioMonoid("1*G1#2");
    expected = new TrioMonoid("1*G1");

    result = getMonoid(g1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            ,"<http://example/book1>"
            ,"dc:creator"
            ,"'A.N. Other'");

    assertEquals(result,expected);

    // Newly inserted have their annotation
    //expected = new TrioMonoid("1*G1#6");
    expected = new TrioMonoid("1*G1");
    result = getMonoid(g1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            ,"<http://example/book3>"
            ,"dc:title"
            ,"'Mme Bovary'");

    assertEquals(result,expected);

	System.out.println("-----------------");
  
	// TODO: The downstreamed triples are the same as the inserted

  }

	//Insert repeated triples
    // Maybe is subsumed in the previous test case.
  @Test 
  public void testInsertDataAllRepeated() throws EngineException {
    // In this case (only one graph) the insert has no effect  
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

	g1.query(insert1);

	//System.out.println(graph.display());
	//Size and cardinality did not changed
	assertEquals(4,g1.size());
	assertEquals(4,g1.cardinality());

    // Repeated, did not change
    //TrioMonoid expected = new TrioMonoid("1*G1#2");
    TrioMonoid expected = new TrioMonoid("1*G1");
    TrioMonoid result = getMonoid(g1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            ,"<http://example/book1>"
            ,"dc:creator"
            ,"'A.N. Other'");

    assertEquals(result,expected);

	System.out.println("-----------------");
  
  }

  // A delete of something that does not exist does not affect
  // existent elements
  @Test
  public void testDeleteNonExistant() throws EngineException{

	System.out.println("DELETE DATA non existant");

	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "<http://example/book3> dc:creator 'G. Flaubert' ."
			+ "<http://example/book3> dc:title 'Mme Bovary'."
			+ "}";
	g1.query(insert1);
	
	delete1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE DATA {"
			+ "<http://example/book6> dc:title 'A new book'."
			+ "<http://example/book6> dc:creator 'A.N. Other' ."
			+ "<http://example/book3> dc:creator 'G. Flobert' ."
			+ "<http://example/book3> dc:title 'Mme Bobary'."
			+ "}";

	g1.query(delete1);
	//System.out.println(graph.display());

	// Cardinality and Size unchanged
	assertEquals(6,g1.size());
	assertEquals(6,g1.cardinality());

    // This test would be much better if we had a nice equals function
    //TrioMonoid expected = new TrioMonoid("1*G1#6");
    TrioMonoid expected = new TrioMonoid("1*G1");
    TrioMonoid result = getMonoid(g1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            ,"<http://example/book3>"
            ,"dc:title"
            ,"'Mme Bovary'");

    assertEquals(result,expected);

	System.out.println("-----------------");

  }

  //Delete some triples must erase those and only those triples
  @Test
  public void testDeleteSomeExistant() throws Exception{
	System.out.println("DELETE DATA some existant");

	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "<http://example/book3> dc:creator 'G. Flaubert' ."
			+ "<http://example/book3> dc:title 'Mme Bovary'."
			+ "}";
	g1.query(insert1);
	
	delete1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE DATA {"
			+ "<http://example/book1> dc:title 'A new dictionary'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book3> dc:creator 'G. Flaubert' ."
			+ "<http://example/book3> dc:title 'Mme Bobary'."
			+ "}";

	g1.query(delete1);

    // Two were deleted
	assertEquals(4,g1.size());
	assertEquals(4,g1.cardinality());

	
    // Not deleted, not affected
    //TrioMonoid expected = new TrioMonoid("1*G1#6");
    TrioMonoid expected = new TrioMonoid("1*G1");
    TrioMonoid result = getMonoid(g1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            ,"<http://example/book3>"
            ,"dc:title"
            ,"'Mme Bovary'");
    assertEquals(result,expected);

    // deleted, is gone
    expected = new TrioMonoid("");
    result = getMonoid(g1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            ,"<http://example/book1>"
            ,"dc:creator"
            ,"'A.N. Other'");

    assertEquals(result,expected);
	System.out.println("-----------------");
  }

  // The delete insert behaves after the del/ins model
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
	g1.query(delins);

	// The same cardinality
	assertEquals(4,g1.size());
	assertEquals(4,g1.cardinality());

    // Not edited, not affected
    //TrioMonoid expected = new TrioMonoid("1*G1#1");
    TrioMonoid expected = new TrioMonoid("1*G1");
    TrioMonoid result = getMonoid(g1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            ,"<http://example/book1>"
            ,"dc:title"
            ,"'A new book'");
    assertEquals(result,expected);

    // deleted, is gone
    expected = new TrioMonoid("");
    result = getMonoid(g1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            ,"<http://example/book1>"
            ,"dc:creator"
            ,"'A.N. Other'");

    assertEquals(result,expected);
	
    // edited, new annotation
    // Again, this relies on deterministic sequential processing by Corese
    //expected = new TrioMonoid("1*G1#5");
    expected = new TrioMonoid("1*G1");
    result = getMonoid(g1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            ,"<http://example/book1>"
            ,"dc:author"
            ,"'A.N. Other'");

    assertEquals(result,expected);
	
	System.out.println("-----------------");
  
  
  }
  
  //We do not touch patterns, so technically, they should be included
  // in the previouse cases.

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
	g1.query(insert2);
	Graph graph = g1.getGraph();
	System.out.println(graph.display());
	// Cardinality 
	assertEquals(6,g1.size());
	assertEquals(6,g1.size());
	//  TODO: Change test from one id per operation to id per triple
	// All others (if any) are the same
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
	g1.query(insert2);
	//System.out.println(g1.display());
	// Cardinality 
	assertEquals(5,g1.size());
	assertEquals(5,g1.cardinality());
	// clock moved only one tick in g1 component

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
	g1.query(delete1);
	//System.out.println(g1.display());

	// Size and cardinality 
	assertEquals(2,g1.size());
	assertEquals(2,g1.cardinality());

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
	g1.query(delete1);
	//System.out.println(g1.display());
	// Size and cardinality 0
	assertEquals(0,g1.size());
	assertEquals(0,g1.cardinality());
	System.out.println("-----------------");

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
  
  // Reception of operations from other graphs

  //Disjoint insertion from two sources
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
    System.out.println(g1.getLastOperation().toString());
    System.out.println(g2.getLastOperation().toString());

    // Must commute, as these are concurrent operations
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

	// Monoid assertions. 

    // Each receiver has the right annotations
    // Relies on sequential deterministic processing of Corese
    //TrioMonoid expected = new TrioMonoid("1*G2#2");
    TrioMonoid expected = new TrioMonoid("1*G2");
    TrioMonoid result1 = getMonoid(receiver1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            ,"<http://example/book1>"
            ,"dc:creator"
            ,"'A.N. Other'");
    TrioMonoid result2 = getMonoid(receiver2,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            ,"<http://example/book1>"
            ,"dc:creator"
            ,"'A.N. Other'");

    assertEquals(expected,result1);
    assertEquals(expected,result2);

    //expected = new TrioMonoid("1*G1#1");
    expected = new TrioMonoid("1*G1");
    result1 = getMonoid(receiver1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            ,"<http://example/book2>"
            ,"dc:creator"
            ,"'Balzac'");
    result2 = getMonoid(receiver2,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            ,"<http://example/book2>"
            ,"dc:creator"
            ,"'Balzac'");

    assertEquals(result1,expected);
    assertEquals(result2,expected);

	System.out.println("-----------------");
  
  }

  // Receiving two times the same operation makes the annotation grow
  @Test
  public void testNonIdempotent() throws EngineException{
  
	System.out.println("NON Idempotency");
  
	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";
	g1.query(insert1);
	receiver1.applyEffect(g1.getLastOperation());

	receiver2.applyEffect(g1.getLastOperation());
	receiver2.applyEffect(receiver1.getLastOperation());
    
    //TrioMonoid expected = new TrioMonoid("2*G1#2");
    TrioMonoid expected = new TrioMonoid("2*G1");
    TrioMonoid result = getMonoid(receiver2,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            ,"<http://example/book2>"
            ,"dc:title"
            ,"'La comedie humaine'");
    assertEquals(expected,result);
  }

  // When receiving concurrent insertions the annotations are correctly
  // added
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

    // Concurrent, so we must tolerate disorder
    receiver1.applyEffect(g1.getLastOperation());
	receiver1.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g1.getLastOperation());

    
    //Cardinalities and sizes are the same
	assertEquals(receiver1.size(),4);
	assertEquals(receiver2.size(),4);
	assertEquals(receiver1.cardinality(),4);
	assertEquals(receiver2.cardinality(),4);

	System.out.println(receiver1.display());
	System.out.println(receiver2.display());

	// Monoid assertions. 
    // Concurrent inserted triples annotations are added as expected

    TrioMonoid monoid1 = getMonoid(receiver1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> ",
            "<http://example/book2>",
            "dc:creator",
            "'Balzac'");
    
    TrioMonoid monoid2 = getMonoid(receiver2,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> ",
            "<http://example/book2>",
            "dc:creator",
            "'Balzac'");
    
    //TrioMonoid expected = new TrioMonoid("1*G2#3+1*G1#1");
    TrioMonoid expected = new TrioMonoid("1*G2+1*G1");
    assertEquals(monoid1,expected);
    assertEquals(monoid2,expected);
    
	System.out.println("-----------------");
  
  }


  // Receiving two disjoint deletes must affect only concerned elements
  // case from a monomial to zero.

  @Test
  public void testDelete1() throws EngineException{
      // Commutativity of the delete
  
	System.out.println("DELETE DATA monomial to zero");

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
    //System.out.println(g1.listener.getLastInserted());
    //System.out.println(g1.getLastOperation());
	receiver1.applyEffect(g1.getLastOperation());
	receiver2.applyEffect(g1.getLastOperation());

	g1.query(delete1);
	g2.query(delete2);
    // Deletes are concurrent, we must tolerate disorder
	receiver1.applyEffect(g1.getLastOperation());
	receiver1.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(g1.getLastOperation());

    /*
    System.out.println(g1.getLastOperation());
    System.out.println(g2.getLastOperation());
    System.out.println(receiver1.display());
    System.out.println(receiver2.display());
    * 
    */
	assertEquals(2,receiver1.size());
	assertEquals(2,receiver2.size());
	assertEquals(receiver1.cardinality(),2);
	assertEquals(receiver2.cardinality(),2);

	// Monoid assertions. 
	
    //TrioMonoid expected = new TrioMonoid("1*G1#1");
    TrioMonoid expected = new TrioMonoid("1*G1");
    TrioMonoid monoid1 = getMonoid(receiver1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> ",
            "<http://example/book1>",
            "dc:title",
            "'A new book'");
    
    TrioMonoid monoid2 = getMonoid(receiver2,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> ",
            "<http://example/book1>",
            "dc:title",
            "'A new book'");
    

    assertEquals(monoid1,expected);
    assertEquals(monoid2,expected);

    expected = new TrioMonoid("");

    monoid1 = getMonoid(receiver1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            ,"<http://example/book2>"
            ,"dc:creator"
            ,"'Balzac'");
    monoid2 = getMonoid(receiver2,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            ,"<http://example/book2>"
            ,"dc:creator"
            ,"'Balzac'");

    assertEquals(monoid1,expected);
    assertEquals(monoid2,expected);
    
	System.out.println("-----------------");

  }
		 

  // When deleting the annotation is correctly substracted
  // case from polynomial to polynomial
  @Test
  public void testDelete2() throws EngineException{
  
	System.out.println("DELETE DATA annotation substraction");

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

    // Receiver has triples with annotations with size greater than 1
    // produced by a concurrent insertion.
	g1.query(insert1);
	g2.query(insert1);
	receiver1.applyEffect(g1.getLastOperation());
	receiver1.applyEffect(g2.getLastOperation());

	g1.query(delete1);
	receiver1.applyEffect(g1.getLastOperation());
	assertEquals(receiver1.size(),4);
	assertEquals(receiver1.cardinality(),4);

    // Only the annotation coming from g1 is canceled
    //TrioMonoid expected = new TrioMonoid("1*G2#3");
    TrioMonoid expected = new TrioMonoid("1*G2");
    TrioMonoid monoid1 = getMonoid(receiver1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> ",
            "<http://example/book2>",
            "dc:creator",
            "'Balzac'");
    
    TrioMonoid monoid2 = getMonoid(receiver1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> ",
            "<http://example/book2>",
            "dc:creator",
            "'Balzac'");
    

    assertEquals(expected,monoid1);
    assertEquals(expected,monoid2);


	System.out.println("-----------------");

  }
  // Correct substraction of a coefficient
  @Test
  public void testDelete3() throws EngineException{
  
	System.out.println("DELETE DATA coefficient substraction");

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

	g1.query(insert1);
	g2.applyEffect(g1.getLastOperation());

    // All triples have coefficient 2
	receiver1.applyEffect(g1.getLastOperation());
	receiver1.applyEffect(g2.getLastOperation());
    System.out.println(receiver1.display());

	g1.query(delete1);

	receiver1.applyEffect(g1.getLastOperation());
    System.out.println(receiver1.getLastOperation());
    System.out.println(receiver1.display());
    System.out.println(receiver2.display());

	// Monoid assertions. 

    //TrioMonoid expected = new TrioMonoid("1*G1#4");
    TrioMonoid expected = new TrioMonoid("1*G1");
    TrioMonoid monoid1 = getMonoid(receiver1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> ",
            "<http://example/book1>",
            "dc:creator",
            "'A.N. Other'");
    
    TrioMonoid monoid2 = getMonoid(receiver1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> ",
            "<http://example/book1>",
            "dc:creator",
            "'A.N. Other'");

    assertEquals(expected,monoid1);
    assertEquals(expected,monoid2);
	System.out.println("-----------------");
  }

  // Truncation on a cycle
  @Test
  public void testDelete4() throws EngineException{
  
	System.out.println("DELETE DATA Truncation");

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

    // The classic diamond with cycle
	g1.query(insert1);
	g2.applyEffect(g1.getLastOperation());
	receiver1.applyEffect(g1.getLastOperation());

    // All triples have coefficient 2
	receiver2.applyEffect(receiver1.getLastOperation());
	receiver2.applyEffect(g2.getLastOperation());

    // receiver2 will delete both occurences
	receiver2.query(delete1);

    // g1 will receive this, but he only has one occurrence
	g1.applyEffect(receiver2.getLastOperation());

	// Monoid assertions. 

    assertEquals(3,g1.cardinality());

    TrioMonoid expected = new TrioMonoid("");
    TrioMonoid monoid = getMonoid(g1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> ",
            "<http://example/book1>",
            "dc:creator",
            "'A.N. Other'");

    assertEquals(expected,monoid);
	System.out.println("-----------------");
  }

    @Test
    public void testAddPenta(){
        System.out.println("AddPenta");
        SimplePenta toAdd = new SimplePenta("http://ns.inria.fr/edelweiss/2010/kgram/default",
                    "<http://www.example.org/book1>",
                    "<http://purl.org/dc/elements/1.1/creator>",
                    // BUG: Spaces in literals are not well handled
                    //"'A.N. Other'",
                    "http://authors.org/John_Doe",
                    "1*ID#1233");
        g1.addPenta(toAdd);
        assertEquals(1,g1.size());

        for(Entity ent : g1.getGraph().getEdges()){
            SimplePenta check = new SimplePenta(ent);
            assertEquals(toAdd,check);
            break;
        }
    
    }

    @Test
    public void testDelPenta(){
        System.out.println("AddPenta");
        SimplePenta toAdd = new SimplePenta("http://ns.inria.fr/edelweiss/2010/kgram/default",
                    "<http://www.example.org/book1>",
                    "<http://purl.org/dc/elements/1.1/creator>",
                    // BUG: Spaces in literals are not well handled
                    //"'A.N. Other'",
                    "http://authors.org/John_Doe",
                    "1*ID#1233");
        g1.addPenta(toAdd);
        assertEquals(1,g1.size());

        g1.delPenta(toAdd);
        assertEquals(0,g1.size());
    
    }
    @Test
    public void testLoadPentas() throws EngineException, IOException{
        System.out.println("Test Load Pentas");

        try(BufferedWriter out = new BufferedWriter(new FileWriter("/tmp/testLoadPentas.test"))){
            out.write(new SimplePenta("DEFAULT",
                    "<http://www.example.org/book1>",
                    "<http://purl.org/dc/elements/1.1/creator>",
                    // BUG: Spaces in literals are not well handled
                    //"'A.N. Other'",
                    "http://authors.org/John_Doe",
                    "1*ID#1233").toString());
            out.newLine();
            out.write(new SimplePenta("<http://mygraphs.org/graph1>",
                    "<http://www.example.org/book1>",
                    "<http://purl.org/dc/elements/1.1/creator>",
                    // BUG: Spaces in literals are not well handled
                    //"'A.N. Other'",
                    "http://authors.org/John_Doe",
                    "1*ID#3233").toString());
            out.flush();
        }

        File temp = new File("/tmp/testLoadPentas.test");
        g1.loadPentas(temp);

        FileUtils.deleteQuietly(temp);
        assertEquals(2,g1.size());
    }

    @Test
    public void testExportPentas() throws EngineException, IOException{

        //BUG: Spaces in literals are not well handled
        
	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A_new_book'."
			+ "<http://example/book1> dc:creator 'A.N._Other' ."
			+ "<http://example/book3> dc:creator 'G._Flaubert' ."
			+ "<http://example/book3> dc:title 'Mme_Bovary'."
			+ "}";
     g1.query(insert1);
     g1.exportPentas("/tmp/testExportPentas.test");

     File temp = new File("/tmp/testExportPentas.test");
     g2.loadPentas(temp);

     FileUtils.deleteQuietly(temp);
     assertEquals(g1.size(),g2.size());
     assert(g1.sameGraph(g2)); 
    }

    @Test
    public void testProperties() throws EngineException{

        System.out.println("Handling properties like sameAs");

        /* The problem with the properties is that they get listened, and 
         * therefore, packed as operations and transmitted
         * as "owl:sameAs" instead of the full name
         * 
         */
        
	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            + "PREFIX owl:<http://www.w3.org/2002/07/owl#>"
			+ "INSERT DATA {"
            + "<http://example/book1> dc:similar <http://library/book4588> ."
            + "<http://es.dbpedia.org/resource/Venezuela> <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Venezuela>" 
			+ "}";
	insert2 = "PREFIX dc: <http://purl.org/dc/elements/1.1#> "
			+ "INSERT DATA {"
            + "<http://example/book1> dc:similar <http://library/book4588> ."
            + "<http://es.dbpedia.org/resource/Venezuela> <http://www.w3.org/2002/07/owl/sameAs> <http://dbpedia.org/resource/Venezuela>" 
			+ "}";
    g1.query(insert1);
    g2.query(insert1);
    g1.applyEffect(g2.getLastOperation());

    System.out.println(g1.size());
    System.out.println(g2.size()); 
    System.out.println(g1.display());
    System.out.println(g2.display()); 

    assertEquals(g1.size(),g2.size());
    
    
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
    TMOperation op1 = g1.getLastOperation();
    System.out.println(op1);
    g1.query(delete1);
    TMOperation op2 = g1.getLastOperation();
    System.out.println(op2);

    /* This part requires disorder tolerance
     * Future work with a new test bed
    receiver1.applyEffect(op1);
    receiver2.applyEffect(op2);
    System.out.println(receiver1.display());
    System.out.println(receiver2.display());

    receiver1.applyEffect(op2);
    receiver2.applyEffect(op1);
    */
    
    receiver1.applyEffect(op1);
    receiver2.applyEffect(op1);
    System.out.println(receiver1.display());
    System.out.println(receiver2.display());

    receiver1.applyEffect(op2);
    receiver2.applyEffect(op2);
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

	TMOperation op = g1.getLastOperation();
  
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

	// The application of non-effect op does not change the last op
    // In other word, non-effect operations are ignored
	g1.query(insert2);
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

  // Cycle detection
  // This is more an integration test, the unit test should be in the 
  // operation class

  // Simple back-forth loop
  @Test
  public void testCycleDetection1() throws EngineException{
    
	System.out.println("Cycle Detection 1");

	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "}";

	g1.query(insert1);
	g2.applyEffect(g1.getLastOperation());

	g1.applyEffect(g2.getLastOperation());

	// Monoid assertions. 

    // Cycle should have been detected and no change in the annotation
    //TrioMonoid expected = new TrioMonoid("1*G1#1");
    TrioMonoid expected = new TrioMonoid("1*G1");
    TrioMonoid monoid1 = getMonoid(g1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> ",
            "<http://example/book1>",
            "dc:title",
            "'A new book'");
    
    assertEquals(expected,monoid1);
	System.out.println("-----------------");
  
  }

  //  Long cycle 
  @Test
  public void testCycleDetection2() throws EngineException{
    
	System.out.println("Cycle Detection 1");

	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "}";

	g1.query(insert1);
	g2.applyEffect(g1.getLastOperation());
	receiver1.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(receiver1.getLastOperation());

	g1.applyEffect(receiver2.getLastOperation());

	// Monoid assertions. 

    // Cycle should have been detected and no change in the annotation
    //TrioMonoid expected = new TrioMonoid("1*G1#1");
    TrioMonoid expected = new TrioMonoid("1*G1");
    TrioMonoid monoid1 = getMonoid(g1,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> ",
            "<http://example/book1>",
            "dc:title",
            "'A new book'");
    
    assertEquals(expected,monoid1);
	System.out.println("-----------------");
  
  }
  
  // Sub-Cycle 
  @Test
  public void testCycleDetection3() throws EngineException{
    
	System.out.println("Cycle Detection 1");

	insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "}";

	g1.query(insert1);
	g2.applyEffect(g1.getLastOperation());
	receiver1.applyEffect(g2.getLastOperation());
	receiver2.applyEffect(receiver1.getLastOperation());

	g2.applyEffect(receiver2.getLastOperation());

	// Monoid assertions. 

    // Cycle should have been detected and no change in the annotation
    //TrioMonoid expected = new TrioMonoid("1*G1#1");
    TrioMonoid expected = new TrioMonoid("1*G1");
    TrioMonoid monoid1 = getMonoid(g2,
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> ",
            "<http://example/book1>",
            "dc:title",
            "'A new book'");
    
    assertEquals(expected,monoid1);
	System.out.println("-----------------");
  
  }


}
