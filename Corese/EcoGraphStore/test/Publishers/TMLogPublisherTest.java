/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Publishers;

import Graphs.TMGraph;
import Operations.SimplePenta;
import Operations.TMOperation;
import fr.inria.edelweiss.kgram.api.core.Entity;
import fr.inria.edelweiss.kgraph.core.Graph;
import java.util.ArrayList;
import java.util.HashSet;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author luisdanielibanesgonzalez
 */



public class TMLogPublisherTest {
/*
    
    SimplePenta p1 = new SimplePenta("http://example/G1", "<http://example/book1>", "<http://example/title>", "'A new book'","1.G1#1");
        SimplePenta p2 = new SimplePenta("http://example/G1", "<http://example/book2>", "<http://example/title>", "'A newer book'","1.G1#2");
        SimplePenta p3 = new SimplePenta("http://example/G3", "<http://example/book3>", "<http://example/title>", "'The newest book'","1.G3#1");
        SimplePenta p4 = new SimplePenta("http://example/G4", "<http://example/book4>", "<http://example/title>", "'An old book'","1.G4#1");
        ArrayList<SimplePenta> ins = new ArrayList<>();
        ArrayList<SimplePenta> del = new ArrayList<>();
        TMOperation op;
        TMLogPublisher pub = new TMLogPublisher();
    
    public TMLogPublisherTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        ins.add(p1);
        ins.add(p2);
        del.add(p3);
        del.add(p4);
        op = new TMOperation("OP1",ins,del);
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testHandle() {
    }

    @Test
    public void testGetConcernedOperations() {
        System.out.println("ConcernedOperations");

        //TODO:
        // The views need to have the tuple format
        // Compile this query and add it programatically is better

        String view1 = "CONSTRUCT "
                + "WHERE {tuple(?pred ?sub 'The newest book' ?tag). }";

        String view2 = "CONSTRUCT "
                + "WHERE {tuple(?pred <http://example/book1> ?obj ?tag) . }";

        String view3 = "CONSTRUCT "
                + "WHERE {tuple(?pred ?sub 'A new book' ?tag). }";

        HashSet<String >viewSet = new HashSet<>();
        viewSet.add(view1);
        pub.handle(op);
        ArrayList<TMOperation> ops = pub.getConcernedOperations(0, viewSet);
        System.out.println(ops.toString());
        // Assert ins empty.
        // Assert del has the expected triple.

        viewSet.add(view2);
        ops = pub.getConcernedOperations(0, viewSet);
        System.out.println(ops.toString());
        // Assert ins has the expected triple.
        // Assert del has the expected triple.

        viewSet.add(view3);
        // Ins should have two times the concerned triple
        // preparation for the dinamicity.
        ops = pub.getConcernedOperations(0, viewSet);
        System.out.println(ops.toString());
        
    }

    @Test
    public void testGetInsGraph() {
        System.out.println("InsGraph");
        TMGraph g = pub.getInsGraph(op);
        for(Entity ent : g.getGraph().getEdges()){
            System.out.println(ent.toString());
        
        }
    }

    @Test
    public void testGetDelGraph() {
        System.out.println("DelGraph");
        TMGraph g = pub.getDelGraph(op);
        for(Entity ent : g.getGraph().getEdges()){
            System.out.println(ent.toString());
        }
    }

    @Test
    public void testGetTMOp() {
        System.out.println("Get Operation");
        TMOperation newop = pub.getTMOp(op, pub.getInsGraph(op).getGraph(), pub.getDelGraph(op).getGraph());
        System.out.println(newop.toString());
        
    }
    */
}
