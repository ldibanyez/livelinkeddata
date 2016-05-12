/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MultivaluedMap;

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

           StringBuilder insert= new StringBuilder("INSERT DATA {");
           for(int i = 0 ; i<number ; i++){
               insert.append("<").append(baseSubject).append(i).append("> ");
               insert.append(predicate)
                       .append(" <")
                       .append(baseObject)
                       .append(i)
                       .append("> .\n"); 
           }
           insert.append("}");

            formData.add("update",insert.toString());
            endpoint.path("sparql").path("update").type("application/x-www-form-urlencoded").post(formData);
            formData.clear();
    
    
    }

    public static void takeView(WebResource endpoint, String view, String predicate, TMGraph target){

    StringBuilder insert = new StringBuilder("INSERT DATA {");
	try (InputStream input = endpoint.path("sparql").queryParam("query", view)
			  .accept("application/sparql-results+tsv")
			  .get(InputStream.class);
        BufferedReader buf = new BufferedReader(new InputStreamReader(input));)
    {
        // TODO: JAVA 7 scanner?
        String line = buf.readLine();
        assert(line.contains("subj"));
            line = buf.readLine();
            while(line != null){
                String[] sp = line.split(" ");
                insert.append("tuple(");
                insert.append(predicate).append(" ");
                //TODO: This does not support literals
                insert.append("<"+sp[0]+">").append(" ");
                insert.append("<"+sp[1]+">").append(" ");
                insert.append("'"+sp[2]+"'").append(")\n");
                line = buf.readLine();
            }
            insert.append("}");
        //buf.close();
        //input.close();
        
        // Put the result in a template target to avoid continuous calls to the endpoint
        target.query(insert.toString());

    
	  } catch (IOException ex) {
         // Logger.getLogger(TMGraphIncrementalDelete.class.getName()).log(Level.SEVERE, null, ex);
        throw new Error("IO Problem, check log");
      } catch (EngineException ex) {
         Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, insert.toString());
         Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
          throw new Error("Query Problem, check log");
      } 
    
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
