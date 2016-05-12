/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LiveLinkedData;

import fr.inria.edelweiss.kgram.core.Mappings;

/**
 *
 * @author ibanez-l
 */
public class LiveLinkedData {
  
  public static void main(String[] args){
  
	try {
  BroadcastStore bs1;
  BroadcastStore bs2;
  BroadcastStore bs3;
  BroadcastStore bs4;

//Test Ring
  
  bs1 = new BroadcastStore("Store1", 50100);
  bs2 = new BroadcastStore("Store2", 50200);
  bs3 = new BroadcastStore("Store3", 50300);
  bs4 = new BroadcastStore("Store4", 50400);

	bs1.addFollower("localhost:50200");
	bs2.addFollower("localhost:50300");
	bs3.addFollower("localhost:50400");
	bs4.addFollower("localhost:50100");

	String insert1 = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book1> dc:creator 'A.N. Other' "
			+ "}";

	Mappings map = bs1.query(insert1);

	Thread.sleep(10000);

	assert(bs1.sameGraph(bs2));
	assert(bs1.sameGraph(bs3));
	assert(bs1.sameGraph(bs4));
	System.out.println(bs1.display());
	System.out.println(bs2.display());

	bs1.close();
	bs2.close();
	bs3.close();
	bs4.close();
  //Test Diamond
  
  bs1 = new BroadcastStore("Store1", 50100);
  bs2 = new BroadcastStore("Store2", 50200);
  bs3 = new BroadcastStore("Store3", 50300);
  bs4 = new BroadcastStore("Store4", 50400);

	bs1.addFollower("localhost:50200");
	bs1.addFollower("localhost:50300");
	bs2.addFollower("localhost:50400");
	bs3.addFollower("localhost:50400");
	bs4.addFollower("localhost:50100");

	map = bs1.query(insert1);
	Thread.sleep(10000);
	assert(bs1.sameGraph(bs2));
	assert(bs1.sameGraph(bs3));
	assert(bs1.sameGraph(bs4));
	System.out.println(bs2.display());

	bs1.close();
	bs2.close();
	bs3.close();
	bs4.close();

  //Test Strongly Connected
  
  bs1 = new BroadcastStore("Store1", 50100);
  bs2 = new BroadcastStore("Store2", 50200);
  bs3 = new BroadcastStore("Store3", 50300);
  bs4 = new BroadcastStore("Store4", 50400);

	bs1.addFollower("localhost:50200");
	bs1.addFollower("localhost:50300");
	bs1.addFollower("localhost:50400");
	bs2.addFollower("localhost:50100");
	bs2.addFollower("localhost:50300");
	bs2.addFollower("localhost:50400");
	bs3.addFollower("localhost:50100");
	bs3.addFollower("localhost:50300");
	bs3.addFollower("localhost:50400");
	bs4.addFollower("localhost:50100");
	bs4.addFollower("localhost:50300");
	bs4.addFollower("localhost:50200");

	map = bs1.query(insert1);
	Thread.sleep(10000);
	assert(bs1.sameGraph(bs2));
	assert(bs1.sameGraph(bs3));
	assert(bs1.sameGraph(bs4));

	System.out.println(bs2.display());

	bs1.close();
	bs2.close();
	bs3.close();
	bs4.close();
	} catch (Exception ex) {
	  ex.printStackTrace();
	}

	System.exit(1);
  
  }
}
