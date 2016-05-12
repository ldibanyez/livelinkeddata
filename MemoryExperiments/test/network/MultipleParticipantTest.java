/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class MultipleParticipantTest {

    static List<Participant> allParticipants;
    static Process[] endpoints;
    MultivaluedMap formData = new MultivaluedMapImpl();

    public MultipleParticipantTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws ParserConfigurationException, XPathExpressionException, URISyntaxException, IOException, InterruptedException {
        Document network = NodeRunner.
                loadNetwork("/Users/luisdanielibanesgonzalez/NetBeansProjects/MemoryExperiments/NetworkDefs/Complete-10.xml");
        allParticipants = 
                NodeRunner.getParticipants(network,"localhost");
        // start the endpoints manually
        //endpoints = NodeRunner.startEndpoints(allParticipants);
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
        formData.clear();
    }

    /*
     * We test that the repeated post of an insert
     */
    @Test
    public void testMultiplePull(){
    
        // Snippet from NetworkRunner
        boolean idle;
        do{
            idle = true;
            /*
            for(Participant p: allParticipants){
                idle = idle && !p.pull();
            }
            * */
            
        }while(!idle);
    
        NetworkRunner.printStats(allParticipants);
         
        for(Process proc : endpoints){
            proc.destroy();
        }
    }
}