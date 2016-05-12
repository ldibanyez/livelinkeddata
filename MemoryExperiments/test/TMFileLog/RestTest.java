/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TMFileLog;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
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
public class RestTest {
    static WebResource service;
    MultivaluedMap formData = new MultivaluedMapImpl();
    
    public RestTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws URISyntaxException {
        List<String> arguments = new ArrayList<>();
        arguments.add("-p");
        arguments.add("40900");
        arguments.add("-n");
        arguments.add("TESTFileLog");
        try {
            TMFileJSONLog.EmbeddedJettyServer.main(arguments.toArray(new String[arguments.size()]));
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
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testGetLog(){
        System.out.println("TestGetLog");
        try(InputStream in = service.path("sparql").path("log")
                .queryParam("from","0")
            .accept(MediaType.APPLICATION_OCTET_STREAM)
            .get(InputStream.class);
            InputStreamReader inreader = new InputStreamReader(in);
            BufferedReader buf = new BufferedReader(inreader);) 
        {
            String line = buf.readLine();
            while(line != null){
            System.out.println(line);
            line= buf.readLine();
            
            }
        
        } catch(IOException ioex){
            throw new Error("IO Exception");
        }
    }
}