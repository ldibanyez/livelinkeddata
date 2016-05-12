/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OtherTests;

import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.acacia.corese.triple.parser.NSManager;
import fr.inria.edelweiss.kgram.api.core.Node;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import fr.inria.edelweiss.kgtool.print.TripleFormat;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class SameAs {

    Graph g = Graph.create();
    QueryProcess exec = QueryProcess.create(g);
    
    public SameAs() {

        
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        g.init();
    }
    
    @After
    public void tearDown() {
        g.clearDefault();
        g.clearNamed();
    }


    @Test
    public void testOWL() throws EngineException{
    
	String insert = "PREFIX owl:<http://www.w3.org/2002/07/owl#>"
			+ "INSERT DATA {"
            + "<http://es.dbpedia.org/resource/Venezuela> <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Venezuela>"            + "<http://es.dbpedia.org/resource/Venezuela> owl:sameAs <http://dbpedia.org/resource/Venezuela>" 
			+ "}";
     exec.query(insert);

     System.out.println(g.size());
     System.out.println(g.display());
     Node n = g.getResource("http://www.w3.org/2002/07/owl#sameAs");
     System.out.println(n.toString());
     Node p = g.getPropertyNode("http://www.w3.org/2002/07/owl#sameAs");
     System.out.println(p.toString());
     Node q = g.getPropertyNode("owl:sameAs");
     System.out.println();
     assert(p.equals(n));

     NSManager nsm =  NSManager.create();
     System.out.println(nsm.getNamespace("owl"));


    }

    @Test
    public void testOther() throws EngineException{
    
	String insert = "PREFIX ex:<http://example.org/buho#>"
			+ "INSERT DATA {"
            + "<http://es.dbpedia.org/resource/Venezuela> <http://example.org/buho#sameAs> <http://dbpedia.org/resource/Venezuela>"            + "<http://es.dbpedia.org/resource/Venezuela> ex:sameAs <http://dbpedia.org/resource/Venezuela>" 
			+ "}";
     exec.query(insert);

     System.out.println(g.size());
     System.out.println(g.display());

    }
    
}
