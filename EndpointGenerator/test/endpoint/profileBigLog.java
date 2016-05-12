/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoint;

import Graphs.TMGraph;
import Operations.TMOperation;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class profileBigLog {
    
    public static void main(String[] args) throws FileNotFoundException, URISyntaxException, IOException{
    
    TMGraph target = new TMGraph("prueba");
    /*
    WebResource service;
    MultivaluedMap formData = new MultivaluedMapImpl();
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        service = client.resource(new URI("http://172.16.9.3:20500/kgram"));
        String path = "http://172.16.9.213/~luisdanielibanesgonzalez/datasets/ObjectFrance.nt";
	// Reset endpoint
	  service.path("sparql").path("reset").post();
	  service.path("sparql").path("logreset").post();

	// Load data in the endpoint
      // nt load implemented by me working around bug that avoid tags

	  formData.add("remote_path", path);
	  service.path("sparql").path("load").post(formData);
	  formData.clear();
        InputStream logRes = service.path("sparql").path("log")
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .get(InputStream.class);
        Path p = Paths.get("/tmp/logs/logTest.logtest");
        FileUtils.copyInputStreamToFile(logRes, p.toFile());
        */ 
        //TODO: A way to have list of operations, The JSON thing was cool, but
        // big operations make it choke. A DBpedia live style solution seems
        // the next step
        Path p = Paths.get("/tmp/logs/logTest.logtest");
        TMOperation op = new TMOperation(p.toFile()); 

		target.applyEffect(op);
        System.out.println(target.size());
    
    }
}
