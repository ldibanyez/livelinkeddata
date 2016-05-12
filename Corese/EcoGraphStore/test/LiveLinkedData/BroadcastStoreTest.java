/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LiveLinkedData;

import fr.inria.edelweiss.kgram.core.Mappings;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This does not work with Kryonet, there are multiple calls to the constructor
 * so Java Bind exceptions everywhere. Maybe I need to implement the tearDown?
 * Check docs
 * @author ibanez-l
 */
public class BroadcastStoreTest {
  BroadcastStore bs1;
  BroadcastStore bs2;
  BroadcastStore bs3;
  BroadcastStore bs4;
  
  public BroadcastStoreTest() {
	try {
	  bs1 = new BroadcastStore("Store1", 50100);
	  //bs2 = new BroadcastStore("Store2", 50200);
	  //bs3 = new BroadcastStore("Store3", 50300);
	  //bs4 = new BroadcastStore("Store4", 50400);
	} catch (IOException ex) {
	  Logger.getLogger(BroadcastStoreTest.class.getName()).log(Level.SEVERE, null, ex);
	}
  }

  @Test
  public void testAddFollower() {
  }

  @Test
  public void testRing() {
	
	try{
/*
	bs1.addFollower(50200);
	bs2.addFollower(50300);
	bs3.addFollower(50400);
	bs4.addFollower(50100);

	String insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book1> dc:creator 'A.N. Other' "
			+ "}";

	Mappings map = bs1.query(insert1);
	assertTrue(bs1.sameGraph(bs2));
	assertTrue(bs1.sameGraph(bs3));
	assertTrue(bs1.sameGraph(bs4));
	*/
	
	}catch(Exception ex){
	ex.printStackTrace();
	fail("Exception!");
	}

	
  }

  @Test
  public void testDiamond() throws Exception {
  }

  @Test
  public void testStronglyConnected() throws Exception {
  }
}
