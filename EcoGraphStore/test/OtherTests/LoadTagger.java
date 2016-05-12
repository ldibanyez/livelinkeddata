/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OtherTests;

import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.api.core.Entity;
import fr.inria.edelweiss.kgraph.api.Tagger;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import fr.inria.edelweiss.kgtool.load.Load;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import static org.junit.Assert.assertEquals;
import org.junit.*;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class LoadTagger {

    Graph g; 
    
    public LoadTagger() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {

        g = Graph.create();
        g.init();
        g.setTag(true);
        Tagger t = new SimpleTagger();
        g.setTagger(t);
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testLoadRemote() throws EngineException{
        String load = "LOAD <http://ns.inria.fr/fabien.gandon/foaf.rdf> INTO GRAPH";
        QueryProcess exec = QueryProcess.create(g);
        exec.query(load);

        //System.out.println(g.display());
        for(Entity ent : g.getEdges()){
            System.out.println(ent.toString());
            assertEquals("TAG",ent.getNode(2).getLabel());
        }
    
    }

    @Test
    public void testLoadRemote2() throws EngineException{
        String load = "LOAD <http://ns.inria.fr/fabien.gandon/foaf.rdf> INTO GRAPH <kg:default>";
        QueryProcess exec = QueryProcess.create(g);
        exec.query(load);

        //System.out.println(g.display());
        for(Entity ent : g.getEdges()){
            System.out.println(ent.toString());
            assertEquals("TAG",ent.getNode(2).getLabel());
        }
    
    }

    @Test
    public void testLoadLocal() throws MalformedURLException, IOException{
    
       URL website = new URL("http://ns.inria.fr/fabien.gandon/foaf.rdf");
       ReadableByteChannel rbc = Channels.newChannel(website.openStream());
       FileOutputStream fos = new FileOutputStream("/tmp/rdfdata.rdf");
       fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE); 

        Load ld = Load.create(g);
        ld.load("/tmp/rdfdata.rdf", "kg:default");

        //System.out.println(g.display());
        for(Entity ent : g.getEdges()){
            System.out.println(ent.toString());
            assertEquals("TAG",ent.getNode(2).getLabel());
        }

    }


    class SimpleTagger implements Tagger{

        @Override
        public String tag() {
            return "TAG";
        }
    
    }
}
