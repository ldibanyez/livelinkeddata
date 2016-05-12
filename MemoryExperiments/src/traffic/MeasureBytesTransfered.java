/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package traffic;

import Graphs.TMGraph;
import Operations.TMOperation;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgtool.load.Load;
import fr.inria.edelweiss.kgtool.load.LoadException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CountingInputStream;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class MeasureBytesTransfered {

    //enum endpointType {VANILLA,TMFILE,TMMEM};

    public static int countVanilla(WebResource endpoint, String view, Graph target){
        int c;
        try (InputStream input = endpoint.path("sparql").queryParam("query", view)
                  .accept("application/sparql-results+xml")
                  .get(InputStream.class);
             CountingInputStream counter = new CountingInputStream(input);)   
        {
              Load ld = Load.create(target);
              ld.load(counter);
              c = counter.getCount();

        } catch (LoadException ex) {
            Logger.getLogger(MeasureBytesTransfered.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error("Problem Loading from Vanilla Endpoint");
        }catch (IOException ex) {
            Logger.getLogger(MeasureBytesTransfered.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error("Problem reading InputStream from Vanilla Endpoint");
        }

        return c;
    }

    public static int countTMFile(WebResource endpoint, String view, TMGraph target){
        int c;
      try (InputStream input = endpoint.path("sparql").path("log")
                  .accept(MediaType.APPLICATION_OCTET_STREAM)
                  .get(InputStream.class);
             CountingInputStream counter = new CountingInputStream(input);)   
      {
          //FileUtils.copyInputStreamToFile(logRes, logpath.toFile());
          //TMOperation op = new TMOperation(logpath.toFile()); 
          TMOperation op = new TMOperation(counter);
          //TODO: Apply effect with a stream
          target.applyEffect(op);
          c = counter.getCount();

      } catch (IOException ex) {
          Logger.getLogger(MeasureBytesTransfered.class.getName()).log(Level.SEVERE, null, ex);
          throw new Error("IO problem reading the download log");
      }

        return c;
    }

    public static int countTMMem(WebResource endpoint, String view, TMGraph target){
        int c;
   Gson gson = new Gson();
        try(InputStream input = endpoint.path("sparql").path("log")
                  .queryParam("from", "0")
                  .accept(MediaType.APPLICATION_JSON)
                  .get(InputStream.class);
             CountingInputStream counter = new CountingInputStream(input);)   
        {
         Type collectionType = new TypeToken<List<TMOperation>>(){}.getType();
         List<TMOperation> ops = gson.fromJson(IOUtils.toString(counter,"UTF-8"), collectionType); 
         for(TMOperation op : ops){
          target.applyEffect(op);
         }
         c = counter.getCount();
        } catch (IOException ex) {
            Logger.getLogger(MeasureBytesTransfered.class.getName()).log(Level.SEVERE, null, ex);
          throw new Error("IO problem reading the download log");
        }
    
        return c;
    
    }


    public static void main(String [] args) throws IOException, URISyntaxException, LoadException{

        Properties props = new Properties();
        props.load(new FileReader(args[0]));
        
    
        String endpointURI = props.getProperty("endpointURI");
        String endpointType = props.getProperty("endpointType");
        String view = props.getProperty("view");
        String predicate = props.getProperty("predicate");
        String basedata = props.getProperty("basedata");
        int basesize = 0;
        double[] percentages = {0.01,0.05,0.1,0.2,0.3,0.4,0.5};

        
        WebResource endpoint;
        
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        try {
            endpoint = client.resource(new URI(endpointURI));
        } catch (URISyntaxException ex) {
            throw new Error("Bad endpoint URI " + endpointURI);
        }
        ArrayListMultimap<Double,Integer> results = 
                ArrayListMultimap.create(percentages.length, 2);


        if(endpointType.equalsIgnoreCase("VANILLA")){

            Graph target = Graph.create();

            //First the full recomputation

             Helper.reloadEndpoint(endpoint, basedata);
             int countzero =countVanilla(endpoint,view,target);
             // put two times as the zero value is equal to insert and delete
             results.put(Double.valueOf(0.0), countzero);
             results.put(Double.valueOf(0.0), countzero);
              

            basesize = target.size();
            
            for(double p : percentages){

                target.clearDefault();
                target.clearNamed();
	            int numUpdates =(int)(Math.floor(basesize*p)); 
            

              Helper.reloadEndpoint(endpoint, basedata);
              Helper.delFromEndpoint(endpoint, predicate, numUpdates) ;

             results.put(Double.valueOf(p), countVanilla(endpoint,view,target));
            
            }
            
            for(double p : percentages){

                target.clearDefault();
                target.clearNamed();
	            int numUpdates =(int)(Math.floor(basesize*p)); 
            
              Helper.reloadEndpoint(endpoint, basedata);

              Helper.insEndpoint(endpoint, predicate, numUpdates) ;

             results.put(Double.valueOf(p), countVanilla(endpoint,view,target));
            
            }

        }else if(endpointType.equalsIgnoreCase("TMFILE")){

            TMGraph target = new TMGraph("TEST");

            //First the full recomputation

             Helper.reloadEndpoint(endpoint, basedata);
             int countzero = Helper.takeViewCount(endpoint, view, predicate, target);
             results.put(Double.valueOf(0.0), 
                     countzero);
             results.put(Double.valueOf(0.0), 
                     countzero);
             endpoint.path("sparql").path("logreset").post();
              

            basesize = target.size();
            
            for(double p : percentages){

	            int numUpdates =(int)(Math.floor(basesize*p)); 
            
              Helper.reloadEndpoint(endpoint, basedata);
             endpoint.path("sparql").path("logreset").post();
              Helper.delFromEndpoint(endpoint, predicate, numUpdates) ;

          try {
              target.query("CLEAR ALL");
          } catch (EngineException ex) {
              throw new Error("Problem Clearing during tearDown ");
          }   
             results.put(Double.valueOf(p), countTMFile(endpoint,view,target));
            
            }
            
            for(double p : percentages){

	            int numUpdates =(int)(Math.floor(basesize*p)); 
            
              Helper.reloadEndpoint(endpoint, basedata);
             endpoint.path("sparql").path("logreset").post();

              Helper.insEndpoint(endpoint, predicate, numUpdates) ;

          try {
              target.query("CLEAR ALL");
          } catch (EngineException ex) {
              throw new Error("Problem Clearing during tearDown ");
          }   
             results.put(Double.valueOf(p), countTMFile(endpoint,view,target));
            
            }

        
        }else if(endpointType.equalsIgnoreCase("TMMEM")){

            TMGraph target = new TMGraph("TEST");

            //First the full recomputation

             Helper.reloadEndpoint(endpoint, basedata);
             int countzero = Helper.takeViewCount(endpoint, view, predicate, target);
             results.put(Double.valueOf(0.0), 
                     countzero);
             results.put(Double.valueOf(0.0), 
                     countzero);
             endpoint.path("sparql").path("logreset").post();
              

            basesize = target.size();
            
            for(double p : percentages){

	            int numUpdates =(int)(Math.floor(basesize*p)); 
            
              Helper.reloadEndpoint(endpoint, basedata);
             endpoint.path("sparql").path("logreset").post();
              Helper.delFromEndpoint(endpoint, predicate, numUpdates) ;

          try {
              target.query("CLEAR ALL");
          } catch (EngineException ex) {
              throw new Error("Problem Clearing during tearDown ");
          }   
             results.put(Double.valueOf(p), countTMMem(endpoint,view,target));
            
            }
            
            for(double p : percentages){

	            int numUpdates =(int)(Math.floor(basesize*p)); 
            
              Helper.reloadEndpoint(endpoint, basedata);
             endpoint.path("sparql").path("logreset").post();

              Helper.insEndpoint(endpoint, predicate, numUpdates) ;

          try {
              target.query("CLEAR ALL");
          } catch (EngineException ex) {
              throw new Error("Problem Clearing during tearDown ");
          }   
             results.put(Double.valueOf(p), countTMMem(endpoint,view,target));
            
            }

        
        }else{
            throw new Error("Wrong Type of Endpoint "+ args[1]);
        }


            System.out.println("# Endpoint Type "+ endpointType);
            System.out.println("# View "+ view);
            System.out.println("# Size of the View "+basesize);
            System.out.println("# %Updated  DELETED(Kbs)  INSERTED(Kbs)");
            ArrayList<Double> sortedKeys = new ArrayList(results.keySet());
            Collections.sort(sortedKeys);
            for(Double perc : sortedKeys){
                System.out.println(String.format("%.2f ",perc)
                        + String.format("%.2f ", 
                            results.get(perc).get(0)/1000.0)
                        + String.format("%.2f ", 
                            results.get(perc).get(1)/1000.0)
                        );
            }


    }
    
}
