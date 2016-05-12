/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import Provenance.TrioMonoid;
import com.sun.jersey.api.client.WebResource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class ParticipantTest {

    static Participant part;
    static Participant source;
    
    public ParticipantTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws URISyntaxException, IOException {
        source = new Participant();
        source.setPort(10900);
        source.setHost("localhost");
        source.setURI("http://localhost:10900/kgram");
        source.setService();
        source.setBasedata("http://localhost/~luisdanielibanesgonzalez/datasets/MiniBase.nt");

        List<String> arguments = new ArrayList<>();
        arguments.add("-p");
        arguments.add("20900");
        arguments.add("-n");
        arguments.add("TESTPULL");
        try {
            TMFileLog.EmbeddedJettyServer.main(arguments.toArray(new String[arguments.size()]));
            //TMMemoryLog.EmbeddedJettyServer.main(arguments.toArray(new String[arguments.size()]));
        } catch (Exception ex) {
            throw new Error("Something wrong starting the test endpoint "+ ex.getMessage());
        }

        part = new Participant();
        part.setHost("localhost");
        part.setPort(20900);
        part.setURI("http://localhost:20900/kgram");
        part.setService();
        //To programatically start another server is difficult
        // Is probably better to have handlers
        // push that when a competetnt engineer refactor this for Jetty 9

        // So, please start a server yourself
        // java TMMemoryLog.EmbeddedJettyServer -p 10900 -n TESTPARTICIPANT -l "http://localhost/~luisdanielibanesgonzalez/datasets/MiniBase.nt"

    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
        part.reset();
        part.setViews(new HashMap<Participant,Set<BasicFragment>>());
        source.reload();
        
    }

    /**
     * Test of getHost method, of class Participant.
     */
    //@Test
    public void testGetHost() {
    }

    /**
     * Test of setHost method, of class Participant.
     */
    //@Test
    public void testSetHost() {
    }

    /**
     * Test of getPort method, of class Participant.
     */
    //@Test
    public void testGetPort() {
    }

    /**
     * Test of setPort method, of class Participant.
     */
    //@Test
    public void testSetPort() {
    }

    /**
     * Test of getURI method, of class Participant.
     */
    //@Test
    public void testGetURI() {
    }

    /**
     * Test of setURI method, of class Participant.
     */
    //@Test
    public void testSetURI() {
    }

    /**
     * Test of getBasedata method, of class Participant.
     */
    //@Test
    public void testGetBasedata() {
    }

    /**
     * Test of setBasedata method, of class Participant.
     */
    //@Test
    public void testSetBasedata() {
    }

    /**
     * Test of getViews method, of class Participant.
     */
    //@Test
    public void testGetViews() {
    }

    /**
     * Test of setViews method, of class Participant.
     */
    //@Test
    public void testSetViews() {
    }

    /**
     * Test of getInsDyn method, of class Participant.
     */
    //@Test
    public void testGetInsDyn() {
    }

    /**
     * Test of setInsDyn method, of class Participant.
     */
    //@Test
    public void testSetInsDyn() {
    }

    /**
     * Test of getDelDyn method, of class Participant.
     */
    //@Test
    public void testGetDelDyn() {
    }

    /**
     * Test of setDelDyn method, of class Participant.
     */
    //@Test
    public void testSetDelDyn() {
    }

    /**
     * Test of setService method, of class Participant.
     */
    //@Test
    public void testSetService() throws Exception {
    }

    /**
     * Test of insertPredicate method, of class Participant.
     */
    //@Test
    public void testInsertPredicate() {
        part.insertPredicate("<http://dbpedia.org/ontology/deathPlace>", 10);
        WebResource endpoint = part.getService();
        String checkquery= "SELECT (COUNT(*) as ?no) "
                + "{?s <http://dbpedia.org/ontology/deathPlace> ?o }";
        assertEquals(10,
                Integer.parseInt(
                endpoint.path("sparql").queryParam("query", checkquery)
			  .accept("application/sparql-results+csv")
			  .get(String.class).replace("no\n", "").trim()));
    }

    /**
     * Test of deletePredicate method, of class Participant.
     */
    //@Test
    public void testDeletePredicate() {
        part.insertPredicate("<http://dbpedia.org/ontology/deathPlace>", 10);
        part.deletePredicate("<http://dbpedia.org/ontology/deathPlace>", 5);

        WebResource endpoint = part.getService();
        String checkquery= "SELECT (COUNT(*) as ?no) "
                + "{?s <http://dbpedia.org/ontology/deathPlace> ?o }";

        assertEquals(5,
                Integer.parseInt(
                endpoint.path("sparql").queryParam("query", checkquery)
			  .accept("application/sparql-results+csv")
			  .get(String.class).replace("no\n", "").trim()));
    }

    /**
     * Test of dynamizePredicate method, of class Participant.
     */
    //@Test
    public void testDynamizePredicate() {
        part.insertPredicate("<http://dbpedia.org/ontology/deathPlace>", 100);

        part.setInsDyn(0.5);
        part.setDelDyn(0.1);
        
        part.dynamizePredicate("<http://dbpedia.org/ontology/deathPlace>");

        WebResource endpoint = part.getService();
        String checkquery= "SELECT (COUNT(*) as ?no) "
                + "{?s <http://dbpedia.org/ontology/deathPlace> ?o }";

        assertEquals(140,
                Integer.parseInt(
                endpoint.path("sparql").queryParam("query", checkquery)
			  .accept("application/sparql-results+csv")
			  .get(String.class).replace("no\n", "").trim()));
    }

    @Test
    public void testDynamizeAllPredicates() throws Exception{
        part.insertPredicate("<http://dbpedia.org/ontology/deathPlace>", 10);
        part.insertPredicate("<http://dbpedia.org/ontology/birthPlace>", 10);
        part.insertPredicate("<http://dbpedia.org/ontology/country>", 10);

        part.setInsDyn(0.5);
        part.setDelDyn(0.1);

        part.dynamizeAllPredicates();
        BasicFragment frag = new BasicFragment();

        frag.setPredicate("<http://dbpedia.org/ontology/deathPlace>");
        assertEquals(14,part.countTriplesFragment (frag));

        frag.setPredicate("<http://dbpedia.org/ontology/birthPlace>");
        assertEquals(14,part.countTriplesFragment (frag));
        
        frag.setPredicate("<http://dbpedia.org/ontology/country>");
        assertEquals(14,part.countTriplesFragment (frag));
    }

    /**
     * Test of computeViews method, of class Participant.
     */
    @Test
    public void testComputeViews() throws URISyntaxException {
        HashMap<Participant,Set<BasicFragment>> v = new HashMap<>();
        
        // * View
        BasicFragment frag = new BasicFragment();
        HashSet<BasicFragment> setfrag = new HashSet<>();
        setfrag.add(frag);
        v.put(source,setfrag);
        part.setViews(v);

        part.computeViews();

        assertEquals( part.countTriples(),
                source.countTriples());

        part.reset();

        // View with an object
        setfrag.clear();
        frag= new BasicFragment();
        frag.setObject("<http://dbpedia.org/resource/Venezuela>");
        setfrag.add(frag);
        v.put(source,setfrag);
        part.setViews(v);

        part.computeViews();

        // One triple only in the MiniBase
        //TODO: Count triples based on fragment
        assertEquals( part.countTriples(),
                1);

        part.reset();
        // View with a subject
        setfrag.clear();
        frag= new BasicFragment();
        frag.setSubject("<http://dbpedia.org/resource/Aeropostal_Alas_de_Venezuela>");
        setfrag.add(frag);
        v.put(source,setfrag);
        part.setViews(v);

        part.computeViews();

        // I know that all triples of the test dataset match this pattern
        assertEquals( part.countTriples(),1);
    }

    /**
     * Test of pull method, of class Participant.
     */
    @Test
    public void testPull() {
    
        // The target materialize the view
        System.out.println("testPullStar");

        HashMap<Participant,Set<BasicFragment>> v = new HashMap<>();
        BasicFragment frag = new BasicFragment();
        HashSet<BasicFragment> setfrag = new HashSet<>();
        setfrag.add(frag);
        v.put(source,setfrag);
        part.setViews(v);

        part.computeViews();
        
        // The source gets dynamized

        source.setInsDyn(1);
        source.setDelDyn(0.5);

        source.dynamizePredicate("<http://dbpedia.org/ontology/birthPlace>");

        // the pull
        assertTrue(part.pull(NodeRunner.EndpointType.FILE));
        
        
        assertEquals( source.countTriplesFragment(frag), 
                part.countTriplesFragment(frag));
    }

    //@Test
    public void testPullPredicate() {
    
        // The target materialize the view
        System.out.println("testPullPredicate");

        HashMap<Participant,Set<BasicFragment>> v = new HashMap<>();
        BasicFragment frag = new BasicFragment();
        frag.setPredicate("<http://dbpedia.org/ontology/birthPlace>");
        HashSet<BasicFragment> setfrag = new HashSet<>();
        setfrag.add(frag);
        v.put(source,setfrag);
        part.setViews(v);

        part.computeViews();
        
        // The source gets dynamized

        source.setDelDyn(2);
        source.setInsDyn(0.5);

        source.dynamizePredicate("<http://dbpedia.org/ontology/birthPlace>");

        // the pull
        //assertTrue(part.pull());
        
        
        assertEquals( source.countTriplesFragment(frag),
                part.countTriplesFragment(frag));

        // A second pull should return nothing and have no effect

        //assertFalse(part.pull());
        assertEquals( source.countTriplesFragment(frag),
                part.countTriplesFragment(frag));
    }
    /**
     * Test of toString method, of class Participant.
     */
    //@Test
    public void testGetTagList() throws IOException {

        part.setBasedata("http://localhost/~luisdanielibanesgonzalez/datasets/MiniBase.nt");
        part.reload();

        List<TrioMonoid> tags = part.getTagList();
        assertEquals(16,tags.size());
        for(TrioMonoid tm : tags){
            assertEquals(1,tm.getTokens().size());
            assertEquals(1,tm.getCoeffs().size());
            assertEquals(new Long(1),tm.getCoeffs().get(0));
            System.out.println(tm.toString());
        }

        
    }
    /**
     * Test of toString method, of class Participant.
     */
    //@Test
    public void testToString() {
    }
}