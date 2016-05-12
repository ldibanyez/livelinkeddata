/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OtherTests;

import Graphs.TMTagger;
import fr.inria.acacia.corese.api.IDatatype;
import fr.inria.acacia.corese.cg.datatype.DatatypeMap;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.api.core.Entity;
import fr.inria.edelweiss.kgram.api.core.Node;
import fr.inria.edelweiss.kgraph.core.EdgeImpl;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class GraphApi {

    public static void main(String [] args ) throws EngineException{

    Graph g = Graph.create();
    g.init();
    TMTagger tagger = new TMTagger("TAG");
    g.setTagger(tagger);
    g.setTag(true);
    QueryProcess exec = QueryProcess.create(g);
    
	String insert = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator <http://example/ANOther> ."
			+ "<http://example/book2> dc:creator <http://example/Balzac> ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "<http://example/book2> dc:review 'Superb!'."
			+ "<http://example/book2> dc:review 'Exquisit!'."
			+ "}";

    exec.query(insert);

    System.out.println("getEdges()");

    for(Entity ent : g.getEdges()){
        System.out.println(ent.toString());
    }

    System.out.println("getEdges(http://purl.org/dc/elements/1.1/creator)");
    
    for(Entity ent : g.getEdges("http://purl.org/dc/elements/1.1/creator")){
        System.out.println(ent.toString());
    }

    System.out.println("getEdges(Node predicate, Node subject, int i)");

    Node pred = g.getResource("http://purl.org/dc/elements/1.1/creator");
    Node subj = g.getResource("http://example/book2");

    for(Entity ent : g.getEdges(pred, subj, 0)){
        System.out.println(ent.toString());
    }

    // This one fails
    System.out.println("getEdges(Node predicate, Node object , int i)");
    Node obj = g.getPropertyNode("<http://example/Balzac>");
    for(Entity ent : g.getEdges(pred, obj, 0)){
        System.out.println(ent.toString());
    }

    // The three elements defined
    System.out.println("getEdges(Node predicate, Node subject, Node object, int i)");
    pred = g.getResource("http://purl.org/dc/elements/1.1/review");
    subj = g.getResource("http://example/book2");
    obj = g.getPropertyNode("Superb!");

    for(Entity ent : g.getEdges(pred, subj, obj, 0)){
        System.out.println(ent.toString());
    }
    
    System.out.println("Editing");

    pred = g.getResource("http://purl.org/dc/elements/1.1/creator");
    subj = g.getResource("http://example/book2");

    for(Entity ent : g.getEdges(pred, subj, 0)){
		IDatatype dt = DatatypeMap.newInstance("New Tag");
		Node tag = g.getNode(dt, true, true);
		EdgeImpl ee = (EdgeImpl) ent;
		ee.setTag(tag);
    }

    for(Entity ent : g.getEdges()){
        System.out.println(ent.toString());
    }


    
    }
    
}
