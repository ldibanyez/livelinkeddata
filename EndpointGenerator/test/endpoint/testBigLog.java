/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoint;

import Graphs.TMGraph;
import Operations.TMOperation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.io.FileUtils;
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
public class testBigLog {
    static WebResource service;
    static MultivaluedMap formData = new MultivaluedMapImpl();
    TMGraph target = new TMGraph("prueba");
    
    public testBigLog() {
    }
    
    @BeforeClass
    public static void setUpClass() throws URISyntaxException {
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

    /*@Test
    // Cut after 7 minutes
    // The big shot is to implement and applyEffect that accepts an InputStream
    public void testBigLogRemote(){

        InputStream logRes = service.path("sparql").path("log")
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .get(InputStream.class);
        List<TMOperation> ops = getOps(logRes);

		for(TMOperation op : ops){
			target.applyEffect(op);
		}
        System.out.println(target.size());
    }
    */ 

    @Test
    // 540 seconds...
    public void testBigLogDownload() throws IOException{

        InputStream logRes = service.path("sparql").path("log")
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .get(InputStream.class);
        Path p = Paths.get("/tmp/logs/logTest.logtest");
        FileUtils.copyInputStreamToFile(logRes, p.toFile());
        //TODO: A way to have list of operations, The JSON thing was cool, but
        // big operations make it choke. A DBpedia live style solution seems
        // the next step
        TMOperation op = new TMOperation(p.toFile()); 

		target.applyEffect(op);
        System.out.println(target.size());
    }

    /*
    
    List<TMOperation> getOps(InputStream in){
        BufferedReader fr = new BufferedReader(new InputStreamReader(in));
        ArrayList<TMOperation> ops = new ArrayList<>();
        Gson gson = new Gson();
        String line;
        try {
            while((line = fr.readLine()) != null){
                ops.add(gson.fromJson(line, TMOperation.class));
            }
        } catch (IOException ex) {
            throw new Error("Fatal error reading input stream");
        }
        return ops;
    }

    List<TMOperation> getOpsLocal(String path) throws IOException{
        ArrayList<TMOperation> ops = new ArrayList<>();
        Gson gson = new Gson();
        File f = new File(path);
        org.apache.commons.io.LineIterator it = FileUtils.lineIterator(f);
        while(it.hasNext()){
           ops.add(gson.fromJson(it.next(), TMOperation.class));
        }
        return ops;
    }
    */ 
}