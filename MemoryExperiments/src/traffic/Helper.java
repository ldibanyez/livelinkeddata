/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package traffic;

import Graphs.TMGraph;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgtool.load.LoadException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.io.input.CountingInputStream;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class Helper {

   public static void delFromEndpoint(WebResource endpoint, String predicate ,int number) throws URISyntaxException, LoadException{
    
           if(number == 0){
            return;
           }
           if(number < 0){
            throw new Error("Negative delete");
           }
           MultivaluedMap formData = new MultivaluedMapImpl();
       
       
        String delete = 
                "DELETE { ?x "+ predicate +" ?y }"
                + "WHERE { "
                + "{ "
                + "SELECT ?x ?y "
                + "WHERE { ?x "+ predicate +" ?y }"
                + "ORDER BY ASC(?x)"
                + "LIMIT "+number
                + "} "
                + "}";
            formData.add("update",delete);
            endpoint.path("sparql").path("update").type("application/x-www-form-urlencoded").post(formData);
            formData.clear();
    
    
    }

    public static void insEndpoint(WebResource endpoint, String predicate, int number){
    
           if(number == 0){
            return;
           }
           if(number < 0){
            throw new Error("Negative insert");
           }
           MultivaluedMap formData = new MultivaluedMapImpl();
           String baseSubject = "http://www.example.org/subject/";
           String baseObject = "http://www.example.org/object/";

           String insert= "INSERT DATA {";
           for(int i = 0 ; i<number ; i++){
               insert += "<"+baseSubject +i+ "> " + predicate+ " <" + baseObject +i +"> .\n"; 
           }
           insert += "}";

            formData.add("update",insert);
            endpoint.path("sparql").path("update").type("application/x-www-form-urlencoded").post(formData);
            formData.clear();
    
    
    }

    public static int takeViewCount(WebResource endpoint, String view, String predicate, TMGraph target){

        int c;
	try (InputStream input = endpoint.path("sparql").queryParam("query", view)
			  .accept("application/sparql-results+tsv")
			  .get(InputStream.class);
        CountingInputStream counter = new CountingInputStream(input);    
        BufferedReader buf = new BufferedReader(new InputStreamReader(counter));)
    {
        // TODO: JAVA 7 scanner?
        String line = buf.readLine();
        assert(line.contains("?subj"));
        String insert = "INSERT DATA {";
        line = buf.readLine();
        while(line != null){
            String[] sp = line.split("\t");
            insert += "tuple("+ predicate + " " + sp[0] + " " + sp[1] + " " + sp[2]+")\n";
            line = buf.readLine();
        }
        insert += "}";
        //buf.close();
        //input.close();
        
        // Put the result in a template target to avoid continuous calls to the endpoint
        target.query(insert);

        c = counter.getCount();
	  } catch (IOException ex) {
         // Logger.getLogger(TMGraphIncrementalDelete.class.getName()).log(Level.SEVERE, null, ex);
        throw new Error("IO Problem, check log");
      } catch (EngineException ex) {
         // Logger.getLogger(TMGraphIncrementalDelete.class.getName()).log(Level.SEVERE, null, ex);
          throw new Error("Query Problem, check log");
      } 
    return c;
    
    }

    public static void reloadEndpoint(WebResource endpoint, String data){
    
        MultivaluedMap formData = new MultivaluedMapImpl();
            // Reset endpoint
              endpoint.path("sparql").path("reset").post();

            // Load data in the endpoint

              formData.add("remote_path", data);
              endpoint.path("sparql").path("load").post(formData);
              formData.clear();
    
    }

}
