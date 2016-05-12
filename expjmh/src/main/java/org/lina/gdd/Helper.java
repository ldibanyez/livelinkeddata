/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lina.gdd;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import fr.inria.edelweiss.kgtool.load.LoadException;
import java.net.URISyntaxException;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class Helper {
    
   public static void delFromEndpoint(WebResource service, String predicate ,int number) throws URISyntaxException, LoadException{
    
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
            service.path("sparql").path("update").type("application/x-www-form-urlencoded").post(formData);
            formData.clear();
    
    
    }
}
