/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import Operations.Operation;
import Operations.TMOperation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.vfs.FileSystemException;
import server.Endpoint;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class testService {

     //Executes queries on the time
  
    
    public static void main(String [] args ) throws FileSystemException, URISyntaxException{
    
        Endpoint.startEndpoint(10900, true);

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource(new URI("http://localhost:" + 10900 + "/kgram"));

        MultivaluedMap formData = new MultivaluedMapImpl();

        //name graph
        formData.add("id","TEST");
        service.path("sparql").path("name").post(formData);
        formData.clear();
        

        // load into a named graph with this name

        formData.add("remote_path", "http://localhost/~luisdanielibanesgonzalez/datasets/ObjectVenezuela.ttl");
        service.path("sparql").path("load").post(formData);
        formData.clear();
        
        // load into Default Graph

        formData.add("remote_path", "http://localhost/~luisdanielibanesgonzalez/datasets/ObjectVenezuela.ttl");
        formData.add("source", "http://ns.inria.fr/edelweiss/2010/kgram/default");
        service.path("sparql").path("load").post(formData);
        formData.clear();

        // query

        String query = "SELECT (COUNT(*) AS ?no) { ?s ?p ?o  }";
        System.out.println(service.path("sparql").queryParam("query", query).accept("application/sparql-results+xml").get(String.class));

        // Update

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

        // recount
        System.out.println(service.path("sparql").queryParam("query", query).accept("application/sparql-results+xml").get(String.class));

        //log access
        formData.add("from", "0");

        // The filtering by view is not working well
        //formData.add("views", "CONSTRUCT WHERE {?x ?y 'Balzac'}");
        //formData.add("views", "CONSTRUCT WHERE {?x ?y 'A new book'}");
        String logRes = service.path("sparql").path("log").queryParams(formData).accept("application/sparql-results+json").get(String.class);

        // Deserialization of a collection with Gson
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<TMOperation>>(){}.getType();
        Collection<TMOperation> ops = gson.fromJson(logRes, collectionType);

        for(Operation op : ops){
            System.out.println(op);
        }



        // logReset
        service.path("sparql").path("logreset");
    
        // Full Reset
        service.path("sparql").path("reset");
    
    }
    
}
