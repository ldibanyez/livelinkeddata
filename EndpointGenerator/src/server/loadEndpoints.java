/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import Experiments.UpdateSampler;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import fr.inria.edelweiss.kgtool.load.Load;
import fr.inria.edelweiss.kgtool.load.LoadException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.vfs.FileSystemException;
/**
 *
 * @author luisdanielibanesgonzalez
 */
public class loadEndpoints {

    public static void loadMany(int startPort, int endPort, Endpoint.ENDPOINT_TYPE type ,String path) throws FileSystemException, URISyntaxException{
    
        for(int i = startPort ; i <= endPort ; i++){
            Endpoint.startEndpoint(i,type);

            //Give name to the TMgraph
        
            MultivaluedMap formData = new MultivaluedMapImpl();
            //formData.add("remote_path", path);

            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);
            WebResource service = client.resource(new URI("http://localhost:"+i+"/kgram"));
//            System.out.println(service.path("sparql").path("reset").post(String.class).toString());
            
            if(type.equals(Endpoint.ENDPOINT_TYPE.TMFILELOG)){
                formData.add("id", "TEST-TMGraph-"+i);
                service.path("sparql").path("name").post(formData);
            } 
            
            //String load = "LOAD <"+path+"> INTO GRAPH <kg:default>";
            //formData.add("update", load);
            // service.path("sparql").path("update").type("application/x-www-form-urlencoded").post(formData);

            if(path != null){
                formData.add("remote_path", path);
                service.path("sparql").path("load").post(formData);
            }


        }
    
    
    }


    public static void loadEndpoint(int port, Endpoint.ENDPOINT_TYPE type, String path) throws FileSystemException, URISyntaxException{
    
            Endpoint.startEndpoint(port,type);
    
            MultivaluedMap formData = new MultivaluedMapImpl();

            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);
            WebResource service = client.resource(new URI("http://localhost:"+port+"/kgram"));
            
            if(type.equals(Endpoint.ENDPOINT_TYPE.TMFILELOG) ||
                    type.equals(Endpoint.ENDPOINT_TYPE.TMMEMORYLOG)){
                formData.add("id", "TEST-TMGraph-"+port);
                service.path("sparql").path("name").post(formData);
            } 
            
            //String load = "LOAD <"+path+"> INTO GRAPH <kg:default>";
            //formData.add("update", load);
            // service.path("sparql").path("update").type("application/x-www-form-urlencoded").post(formData);

            formData.add("remote_path", path);
            formData.add("source", "http://ns.inria.fr/edelweiss/2010/kgram/default");
            service.path("sparql").path("load").post(formData);
    
    }

    public static void delfromEndpoint(int port, String predicate , double percentage) throws URISyntaxException, LoadException{
    
           if(percentage == 0.0){
            return;
           }
           if(percentage < 0){
            throw new Error("Negative percentage");
           }
        
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        MultivaluedMap formData = new MultivaluedMapImpl();
       
        WebResource service = client.resource(new URI("http://localhost:"+port+"/kgram"));
        UpdateSampler sampler = new UpdateSampler();

            //Get the remote graph from a construct
            Graph g = Graph.create();
            g.init();
            Load ld = Load.create(g);
            //ld.l;

            String count = "CONSTRUCT WHERE {?x "+predicate +" ?z}";
            InputStream input = service.path("sparql").queryParam("query", count).accept("application/sparql-results+xml").get(InputStream.class);
            ld.load(input);

            QueryProcess exec = QueryProcess.create(g);
       
            String delete = sampler.samplDeleteQuery(exec, predicate, percentage);
            formData.add("update",delete);
            service.path("sparql").path("update").type("application/x-www-form-urlencoded").post(formData);
            formData.clear();
    
    
    }
    

    public static void main(String args[]) throws LoadException{
        try {
            int port = Integer.parseInt(args[0]);
            // arg[2] VANILLA TMFILELOG TMMEMORYLOG
            String graphtype = args[1];
            String path;
            try{
            path = args[2];
            }catch(ArrayIndexOutOfBoundsException nul){
                path = null;
            }

            if(graphtype.equalsIgnoreCase("CORESE")){
                loadEndpoint(port,Endpoint.ENDPOINT_TYPE.VANILLA,path);
            
            }else if (graphtype.equalsIgnoreCase("TMFILE")){
                loadEndpoint(port,Endpoint.ENDPOINT_TYPE.TMFILELOG,path);
            
            }
            else if (graphtype.equalsIgnoreCase("TMMEMORY")){
                loadEndpoint(port,Endpoint.ENDPOINT_TYPE.TMMEMORYLOG,path);
            
            }else {
                throw new Error("Wrong graph type");
            }

            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);
            WebResource service = client.resource(new URI("http://localhost:"+port+"/kgram"));

            if(!graphtype.equalsIgnoreCase("TMFILE") && !graphtype.equalsIgnoreCase("TMMEMORY")){
            } else {
                service.path("sparql").path("logreset");
            }
            

            //delfromEndpoint(port,predicate,percentage);
            
            

        /*

        loadMany(10010,10019,false,"http://localhost/~luisdanielibanesgonzalez/datasets/ObjectEngland.ttl");
        loadMany(20010,20019,true,"http://localhost/~luisdanielibanesgonzalez/datasets/ObjectEngland.ttl");

        loadMany(10020,10029,false,"http://localhost/~luisdanielibanesgonzalez/datasets/ObjectGermany.ttl");
        loadMany(20020,20029,true,"http://localhost/~luisdanielibanesgonzalez/datasets/ObjectGermany.ttl");

        loadMany(10030,10039,false,"http://localhost/~luisdanielibanesgonzalez/datasets/ObjectItaly.ttl");
        loadMany(20030,20039,true,"http://localhost/~luisdanielibanesgonzalez/datasets/ObjectItaly.ttl");

        loadMany(10040,10049,false,"http://localhost/~luisdanielibanesgonzalez/datasets/ObjectCanada.ttl");
        loadMany(20040,20049,true,"http://localhost/~luisdanielibanesgonzalez/datasets/ObjectCanada.ttl");

        loadMany(10050,10059,false,"http://localhost/~luisdanielibanesgonzalez/datasets/ObjectAustralia.ttl");
        loadMany(20050,20059,true,"http://localhost/~luisdanielibanesgonzalez/datasets/ObjectAustralia.ttl");

        loadMany(10060,10069,false,"http://localhost/~luisdanielibanesgonzalez/datasets/ObjectIndia.ttl");
        loadMany(20060,20069,true,"http://localhost/~luisdanielibanesgonzalez/datasets/ObjectIndia.ttl");

        loadMany(10070,10079,false,"http://localhost/~luisdanielibanesgonzalez/datasets/ObjectUS.ttl");
        loadMany(20070,20079,true,"http://localhost/~luisdanielibanesgonzalez/datasets/ObjectUS.ttl");
        */

        } catch (FileSystemException ex) {
            Logger.getLogger(loadEndpoints.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(loadEndpoints.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
}
