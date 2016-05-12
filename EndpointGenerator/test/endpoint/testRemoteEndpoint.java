/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoint;

import Graphs.TMGraph;
import Operations.TMOperation;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import fr.inria.edelweiss.kgtool.load.Load;
import fr.inria.edelweiss.kgtool.load.LoadException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
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
public class testRemoteEndpoint {

    static WebResource service;
    MultivaluedMap formData = new MultivaluedMapImpl();
    
    public testRemoteEndpoint() {
    }
    
    @BeforeClass
    public static void setUpClass() throws URISyntaxException {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        service = client.resource(new URI("http://172.16.9.3:20500/kgram"));
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
        service.path("sparql").path("reset").post();
        service.path("sparql").path("logreset").post();
    }

    //@Test
    public void testLog(){
	    String insert = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";

        formData.add("update", insert);
        service.path("sparql").path("update").type("application/x-www-form-urlencoded").post(formData);
        formData.clear();
    
	    String delete = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";

        formData.add("update", delete);
        service.path("sparql").path("update").type("application/x-www-form-urlencoded").post(formData);
        formData.clear();

        

        // The filtering by view is not working well
        // formData.add("from", "0");
        //formData.add("views", "CONSTRUCT WHERE {?x ?y 'Balzac'}");
        //formData.add("views", "CONSTRUCT WHERE {?x ?y 'A new book'}");
        InputStream logIS = service.path("sparql").path("log").accept(MediaType.APPLICATION_OCTET_STREAM).get(InputStream.class);

        // Deserialization of a collection with Gson
        //Gson gson = new Gson();
        //Type collectionType = new TypeToken<Collection<TMOperation>>(){}.getType();
        //Collection<TMOperation> ops = gson.fromJson(new InputStreamReader(logIS), collectionType);
        Collection<TMOperation> ops = getOps(logIS);

        assertEquals(2,ops.size());
        for(TMOperation op : ops){
            System.out.println(op);
        }
    }

    //@Test
    public void testLoadQuads(){
    
        formData.add("remote_path", "http://172.16.9.213/~luisdanielibanesgonzalez/datasets/ObjectVenezuela.nt");
        service.path("sparql").path("load").post(formData);

        // Tags are there
        String query = "SELECT ?tuple WHERE {tuple(?p ?s ?o ?tuple)} LIMIT 10";
        System.out.println(service.path("sparql").queryParam("query", query).accept("application/sparql-results+xml").get(String.class));
    
    }

    Collection<TMOperation> getOps(InputStream in){
        BufferedReader fr = new BufferedReader(new InputStreamReader(in));
        ArrayList<TMOperation> ops = new ArrayList<>();
        Gson gson = new Gson();
        String line;
        try {
            while((line = fr.readLine()) != null){
                ops.add(gson.fromJson(line, TMOperation.class));
            }
        } catch (IOException ex) {
            Logger.getLogger(testLocalEndpoint.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error("Fatal error reading input stream");
        }
        return ops;
    }

    //@Test
    public void testLogReset() throws IOException{
    
	    String insert = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";

        formData.add("update", insert);
        service.path("sparql").path("update").type("application/x-www-form-urlencoded").post(formData);
        formData.clear();
    
	    String delete = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "DELETE DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";

        formData.add("update", delete);
        service.path("sparql").path("update").type("application/x-www-form-urlencoded").post(formData);
        formData.clear();

        

        // The filtering by view is not working well
        //formData.add("from", "0");
        //formData.add("views", "CONSTRUCT WHERE {?x ?y 'Balzac'}");
        //formData.add("views", "CONSTRUCT WHERE {?x ?y 'A new book'}");
        InputStream logIS = service.path("sparql").path("log").accept(MediaType.APPLICATION_OCTET_STREAM).get(InputStream.class);

        // Deserialization of a collection with Gson
        //Gson gson = new Gson();
        //Type collectionType = new TypeToken<Collection<TMOperation>>(){}.getType();
        //Collection<TMOperation> ops = gson.fromJson(new InputStreamReader(logIS), collectionType);
        Collection<TMOperation> ops = getOps(logIS);

        assertEquals(2,ops.size());
        logIS.close();
    
        service.path("sparql").path("logreset").post();
    
        logIS = service.path("sparql").path("log").accept(MediaType.APPLICATION_OCTET_STREAM).get(InputStream.class);


        // Deserialization of a collection with Gson
        //Gson gson = new Gson();
        //Type collectionType = new TypeToken<Collection<TMOperation>>(){}.getType();
        //Collection<TMOperation> ops = gson.fromJson(new InputStreamReader(logIS), collectionType);
        ops = getOps(logIS);
        assertEquals(0,ops.size());
        logIS.close();
    }

   @Test
   public void testGrabPentasConstruct() throws LoadException{
   
       TMGraph target = new TMGraph("Prueba");
	    String insert = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA {"
			+ "<http://example/book1> dc:title 'A new book'."
			+ "<http://example/book1> dc:creator 'A.N. Other' ."
			+ "<http://example/book2> dc:creator 'Balzac' ."
			+ "<http://example/book2> dc:title 'La comedie humaine'."
			+ "}";

        formData.add("update", insert);
        service.path("sparql").path("update").type("application/x-www-form-urlencoded").post(formData);
        formData.clear();
         String view = "CONSTRUCT WHERE{ "
		+ "tuple(<http://purl.org/dc/elements/1.1/creator> ?x ?y ?tag) .}";
         
	  InputStream input = service.path("sparql").queryParam("query", view)
			  .accept("application/sparql-results+xml")
			  .get(InputStream.class);
	  Load ld = Load.create(target.getGraph());
	  ld.load(input);
      System.out.println(target.display());
   
   }
}