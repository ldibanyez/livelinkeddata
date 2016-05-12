/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TMMemoryLog;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.vfs.FileSystemException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class testDifferentQueryCalls {

    static WebResource service;
    MultivaluedMap formData = new MultivaluedMapImpl();
    //String view = "SELECT DISTINCT ?p { ?s ?p ?o  }";
    //String view = "SELECT  ?tag { tuple(?p ?s ?o ?tag) }";
    /*
    String view = "SELECT ?predicate ?subject ?object ?tag "
            + "WHERE{ "
		//+ "tuple(<http://purl.org/dc/elements/1.1/creator> ?subject ?object ?tag) .}";
		+ "tuple(?predicate ?subject ?object ?tag) .}";
        */
    String view = "CONSTRUCT "
            + "WHERE{ "
		+ "tuple(<http://purl.org/dc/elements/1.1/creator> ?subj ?obj ?tag) .}";
    public testDifferentQueryCalls() {
    }
    
    @BeforeClass
    public static void setUpClass() throws FileSystemException, URISyntaxException {
        List<String> arguments = new ArrayList<>();
        arguments.add("-p");
        arguments.add("40900");
        arguments.add("-n");
        arguments.add("TESTQueryCalls");
        try {
            TMMemoryLog.EmbeddedJettyServer.main(arguments.toArray(new String[arguments.size()]));
        } catch (Exception ex) {
            throw new Error("Something wrong starting the test endpoint "+ ex.getMessage());
        }
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        service = client.resource(new URI("http://localhost:" + 40900 + "/kgram"));
        //name graph
        MultivaluedMap formData = new MultivaluedMapImpl();
	    String insert = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book2> dc:creator <http://example.org/Balzac> ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "<http://example/book2> <http://www.w3.org/2002/07/owl#sameAs> <http://outexample/book5>."
                + "<http://dbpedia.org/resource/%C3%81lex_Gonz%C3%A1lez_(shortstop,_born_1977)> <http://dbpedia.org/ontology/birthPlace> <http://dbpedia.org/resource/Venezuela> ."
			+ "}";

        formData.add("update", insert);
        service.path("sparql").path("update").type("application/x-www-form-urlencoded").post(formData);
        formData.clear();
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp(){
    }
    
    @After
    public void tearDown() {
    }


    /* Interesting testing question where to put this
    @Test
    public void testName(){

        //name graph
        formData.add("id","TEST");
        service.path("sparql").path("name").post(formData);
    }
    */
    
    @Test
    public void testSPARQLXML(){
        System.out.println("sparql-results+xml");
       System.out.println(service.path("sparql").queryParam("query", view)
			  .accept("application/sparql-results+xml")
			  .get(String.class));
    }
    
    @Test
    public void testJSON(){
        System.out.println("sparql-results+json");
       System.out.println(service.path("sparql").queryParam("query", view)
			  .accept("application/sparql-results+json")
			  .get(String.class));
    }


    @Test
    public void testJSONGraph() throws IOException{
        System.out.println("d3 sparql-results+json");
       System.out.println(service.path("sparql").path("d3").queryParam("query", view)
			  .accept("application/sparql-results+json")
			  .get(String.class));
    }
    
    @Test
    public void testCSV(){
        System.out.println("sparql-results+csv");
       System.out.println(service.path("sparql").queryParam("query", view)
			  .accept("application/sparql-results+csv")
			  .get(String.class));
    
    }

    @Test
    public void testTSV(){
        System.out.println("sparql-results+tsv");
       System.out.println(service.path("sparql").queryParam("query", view)
			  .accept("application/sparql-results+tsv")
			  .get(String.class));
    }

    @Test
    public void testRDFXML(){
        System.out.println("rdf+xml");
       System.out.println(service.path("sparql").queryParam("query", view)
			  .accept("application/rdf+xml")
			  .get(String.class));
    }

    @Test
    public void testTurtle(){
        System.out.println("turtle");
       System.out.println(service.path("sparql").queryParam("query", view)
			  .accept("text/turtle")
			  .get(String.class));
    }


}