/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import Operations.TMOperation;
import Provenance.TrioMonoid;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class Participant {

    private String host;
    private int port;
    private double insDyn;
    private double delDyn;
    private String URI;
    private String basedata;
    // From URI to Set of Views
    private HashMap<Participant,Set<BasicFragment>> views;
    private HashMap<Participant,Integer> lastseen = new HashMap<>();
    private WebResource service;


    private String COUNTQUERY="SELECT (COUNT(*) AS ?no) {?s ?p ?o}";
    private String CLEARQUERY="CLEAR ALL";
    private MultivaluedMap formData = new MultivaluedMapImpl();
    private Gson gson= new Gson();
    //private ClientConfig config = new DefaultClientConfig();
    //private Client client = Client.create(config);

    private final static Logger LOGGER = 
            Logger.getLogger(NetworkRunner.class.getName());
        

    public Participant(){
    }

    public Participant(String host, int port, String URI) throws URISyntaxException{
    
        this.host = host;
        this.port = port;
        this.setService();
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public String getBasedata() {
        return basedata;
    }

    public void setBasedata(String basedata) {
        this.basedata = basedata;
    }

    public HashMap<Participant, Set<BasicFragment>> getViews() {
        return views;
    }

    public void setViews(HashMap<Participant, Set<BasicFragment>> views) {
        this.views = views;
        for(Participant uri : this.views.keySet()){
            lastseen.put(uri, 0);
        }
    }

    public double getInsDyn() {
        return insDyn;
    }

    public void setInsDyn(double insDyn) {
        this.insDyn = insDyn;
    }

    public double getDelDyn() {
        return delDyn;
    }

    public void setDelDyn(double delDyn) {
        this.delDyn = delDyn;
    }

    public final void setService() throws URISyntaxException{
    
     ClientConfig config = new DefaultClientConfig();
     Client client = Client.create(config);
      service = client.resource(new URI(URI));
    }

    public WebResource getService() {
        return service;
    }

    public void insertPredicate(String predicate, int number){
    
       if(number == 0){
        return;
       }
       if(number < 0){
        throw new Error("Negative insert");
       }
       String baseSubject = "http://www.example.org/subject/";
       String baseObject = "http://www.example.org/object/";

       StringBuilder insert= new StringBuilder("INSERT DATA {");
       // This random indirectly affects the probability of concurrent insertion
       // maybe is ok to leave it like this (very low)
       // and force the concurrence by loading crafted .nt documents

       Random rand = new java.util.Random();
       for(int i = 0 ; i<number ; i++){
           int r = rand.nextInt(Integer.MAX_VALUE);
           insert.append("<").append(baseSubject).append(r).append("> ");
           insert.append(predicate);
           insert.append(" <") .append(baseObject).append(r).append("> .\n"); 
       }
       insert.append("}");

        formData.add("update",insert.toString());
        service.path("sparql").path("update").type("application/x-www-form-urlencoded").post(formData);
        formData.clear();
    
    }

    public void deletePredicate(String predicate ,int number){
    
       if(number == 0){
        return;
       }
       if(number < 0){
        throw new Error("Negative delete");
       }
   
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

    // Dynamize with respect to what??
    // wrt a fixed predicate
    // wrt all present predicates
    // same thing with subjects and objects...
    public void dynamizePredicate(String predicate){
        String count = service.path("sparql").queryParam("query",
                "SELECT (COUNT(*) AS ?no) { ?s "+predicate+ " ?o  }")
			  .accept("application/sparql-results+csv")
			  .get(String.class);
        count = count.replace("no\n","");
        int c = Integer.parseInt(count.trim());

        deletePredicate(predicate,
                (int)Math.floor(c*this.delDyn));
        insertPredicate(predicate,
                (int)Math.floor(c*this.insDyn));
    }

    public void dynamizeAllPredicates() throws Exception{
        
        String preds = service.path("sparql").queryParam("query", 
                "SELECT DISTINCT ?pred {?s ?pred ?o}")
                .accept("application/sparql-results+csv")
                .get(String.class);
        preds = preds.replace("pred", "");
        preds = preds.trim();
        if (preds.isEmpty()){
            throw new Exception("Empty Graph, cannot dynamize");
        }
        String [] predicates = preds.split("\n");
        for(String p : predicates){
            dynamizePredicate("<"+p+">");
        }
    
    }

    /*
     * Computes all remote views
     * 
     * Note on this method:
     * As it generates an operation, it can conflict with the log pull, i.e,
     * if you compute the view and then pull, you will have two times the same triple
     * artificially...
     * 
     * Assuming eternal log, one can compute the view by filtering all the operations
     * although this is expensive.
     * with a non-eternal log the options are:
     *  1) force sync before computing the view
     *  2) a more complex protocol, eventually will be needed for the dynamicity
     * 
     *  With the assumptions for this deployment: fixed views and no recomputation
     *  it seems we can use only pulling. This means also that we can control
     *  the concurrence rate with the base data
     *  
     * 
     */
    public void computeViews(){

        for(Participant source : views.keySet()){
            for(BasicFragment frag : views.get(source)){
                TMOperation insert = frag.getFrom(source);
                List<TMOperation> listinsert = new ArrayList<>();
                listinsert.add(insert);
                formData.add("ops", gson.toJson(listinsert));
                service.path("sparql").path("apply")
                        .type("application/x-www-form-urlencoded")
                        .post(formData);
                /*
                formData.add("update",insert);
                service.path("sparql").path("update")
                        .type("application/x-www-form-urlencoded")
                        .post(formData);
                */ 
                formData.clear();
            }
        }
    
    
    }


    private boolean pullFile(){
    
        boolean somethingpulled=false;
        WebResource endpoint; 
        
        LOGGER.log(Level.INFO, "Participant {0} starts pulling", this.getURI());
        
        for(Participant source: views.keySet()){
            endpoint = source.getService();
            Integer lastcontact = lastseen.get(source);

            LOGGER.log(Level.FINER, 
                "From participant {0}, last op from him was {1}",
                new Object[]{source.getURI(),lastcontact});

            //List<TMOperation> lstops = new ArrayList<>();
            int receivedops = 0;
            List<TMOperation> concerning = new ArrayList<>();
            try(InputStream in = endpoint.path("sparql").path("log")
                    .queryParam("from", lastcontact.toString())
                    .accept(MediaType.APPLICATION_OCTET_STREAM)
                    .get(InputStream.class); 
                InputStreamReader inread = new InputStreamReader(in,"UTF-8");
                JsonReader reader = new JsonReader(inread))
            { 
                reader.beginArray();
                while (reader.hasNext()) {
                    TMOperation op = gson.fromJson(reader,
                            TMOperation.class);
                    for(BasicFragment frag : views.get(source)){
                        TMOperation subop = frag.concernedSubOp(op);
                        if(!(subop.getInsert().isEmpty() && 
                           subop.getDelete().isEmpty())){
                            // remember we receive in reversed order
                            concerning.add(0,subop);
                            if(concerning.size() > 100000){
                                LOGGER.log(Level.FINER, 
                                    "Sending a chunk of {0} operations ",
                                    concerning.size());
                                formData.add("operation", 
                                        gson.toJson(concerning));
                                service.path("sparql").path("apply")
                                         .post(formData);
                                formData.clear();
                                concerning.clear();
                            }
                        }
                    }
                    receivedops++;
                }
                reader.endArray();
            } catch(ClientHandlerException ex){
                
                LOGGER.log(Level.SEVERE, ex.getMessage());
                throw new Error(
                        "Connection exception while pulling from source " 
                        + source.getURI()); 
                
            } catch(IOException ioex){
                
                LOGGER.log(Level.SEVERE, ioex.getMessage());
                throw new Error(
                        "IO exception while pulling from source "
                        + source.getURI()); 
            }

            LOGGER.log(Level.FINER, 
                "Pulled a list of {0} operations", receivedops);
                //"Pulled a list of {0} operations", lstops.size());

            //somethingpulled = somethingpulled || (lstops.size() > 0); 
            somethingpulled = somethingpulled || (receivedops > 0); 
            //lastseen.put(source, lastseen.get(source) + lstops.size());
            lastseen.put(source, lastseen.get(source) + receivedops);

            LOGGER.log(Level.FINER, 
                "The updated last seen operation {0}", lastseen.get(source));
            
            LOGGER.log(Level.FINER, 
                "Sending the last chunk of operations: size {0}", concerning.size());
                
            if(!concerning.isEmpty()){
                 String json = gson.toJson(concerning);
                 formData.add("operation", json);
                 
                 /*
                Logger.getLogger(Participant.class.getName())
                    .log(Level.FINER, 
                    "Sending a JSON code of length {0}", json.length());
                 */
                try{
                     service.path("sparql").path("apply")
                             .post(formData);
                 }catch(ClientHandlerException ex){
                    Logger.getLogger(Participant.class.getName())
                            .log(Level.SEVERE, ex.getMessage());
                    throw new Error(
                            "Connection exception at target while pulling "
                            + this.getURI());
                 }
                 formData.clear();
                }
        }

        return somethingpulled;
    
    }

    public boolean pull(NodeRunner.EndpointType et){
        switch(et){
            case MEMORY:
               return pullMemory();
            case FILE: 
                return pullFile();
            default:
                throw new Error("Wrong Endpoint Type");
        }
    
    }

    public boolean pullMemory(){
        boolean somethingpulled=false;
        WebResource endpoint; 
        
        LOGGER.log(Level.INFO, "Participant {0} starts pulling", this.getURI());
        
        for(Participant source: views.keySet()){
            endpoint = source.getService();
            Integer lastcontact = lastseen.get(source);

            LOGGER.log(Level.FINER, 
                "From participant {0}, last op from him was {1}",
                new Object[]{source.getURI(),lastcontact});

            //List<TMOperation> lstops = new ArrayList<>();
            int receivedops = 0;
            List<TMOperation> concerning = new ArrayList<>();
            try(InputStream in = endpoint.path("sparql").path("log")
                    .queryParam("from", lastcontact.toString())
                    .accept(MediaType.APPLICATION_OCTET_STREAM)
                    .get(InputStream.class); 
                InputStreamReader inread = new InputStreamReader(in,"UTF-8");
                JsonReader reader = new JsonReader(inread);)
            { 
                reader.beginArray();
                while (reader.hasNext()) {
                    TMOperation op = gson.fromJson(reader, TMOperation.class);
                    for(BasicFragment frag : views.get(source)){
                        TMOperation subop = frag.concernedSubOp(op);
                        if(!(subop.getInsert().isEmpty() && 
                           subop.getDelete().isEmpty())){
                            concerning.add(subop);
                            if(concerning.size() > 100000){
                                LOGGER.log(Level.FINER, 
                                    "Sending a chunk of {0} operations ",
                                    concerning.size());
                                formData.add("operation", 
                                        gson.toJson(concerning));
                                service.path("sparql").path("apply")
                                         .post(formData);
                                formData.clear();
                                concerning.clear();
                            }
                        }
                    }
                    //lstops.add(op);
                    receivedops++;
                }
                reader.endArray();
                //reader.close(); 
            } catch(ClientHandlerException ex){
                
                LOGGER.log(Level.SEVERE, ex.getMessage());
                throw new Error(
                        "Connection exception while pulling from source " 
                        + source.getURI()); 
                
            } catch(IOException ioex){
                
                LOGGER.log(Level.SEVERE, ioex.getMessage());
                throw new Error(
                        "IO exception while pulling from source "
                        + source.getURI()); 
            }

            LOGGER.log(Level.FINER, 
                "Pulled a list of {0} operations", receivedops);
                //"Pulled a list of {0} operations", lstops.size());

            //somethingpulled = somethingpulled || (lstops.size() > 0); 
            somethingpulled = somethingpulled || (receivedops > 0); 
            //lastseen.put(source, lastseen.get(source) + lstops.size());
            lastseen.put(source, lastseen.get(source) + receivedops);

            LOGGER.log(Level.FINER, 
                "The updated last seen operation {0}", lastseen.get(source));
            
            LOGGER.log(Level.FINER, 
                "Sending the last chunk of operations: size {0}", concerning.size());
                
            if(!concerning.isEmpty()){
                 String json = gson.toJson(concerning);
                 formData.add("operation", json);
                 
                 /*
                Logger.getLogger(Participant.class.getName())
                    .log(Level.FINER, 
                    "Sending a JSON code of length {0}", json.length());
                 */
                try{
                     service.path("sparql").path("apply")
                             .post(formData);
                 }catch(ClientHandlerException ex){
                    Logger.getLogger(Participant.class.getName())
                            .log(Level.SEVERE, ex.getMessage());
                    throw new Error(
                            "Connection exception at target while pulling "
                            + this.getURI());
                 }
                 formData.clear();
                }
        }

        return somethingpulled;
    
    }
    public int countTriples(){
        return(Integer.parseInt(
                service.path("sparql").queryParam("query", COUNTQUERY)
			  .accept("application/sparql-results+csv")
			  .get(String.class).replace("no\n", "").trim()));
    }

    public int countTriplesFragment(BasicFragment frag){

        String count = "SELECT (COUNT(*) AS ?no) {"
                + frag.getSubject() + " "
                + frag.getPredicate()+" "
                + frag.getObject()+"}";
        
        return(Integer.parseInt(
                service.path("sparql").queryParam("query", count)
			  .accept("application/sparql-results+csv")
			  .get(String.class).replace("no\n", "").trim()));
    }

    public void logreset(){
       service.path("sparql").path("logreset").post();
    
    }
    
    public void reset(){
       service.path("sparql").path("reset").post();
    }

    public void reload(){
    
        reset();
       // Load data in the endpoint

       formData.add("remote_path", basedata);
       service.path("sparql").path("load").post(formData);
       logreset();
       formData.clear();
    }

    public List<TrioMonoid> getTagList() throws IOException{
        String tagquery = "SELECT ?tag WHERE"
                + "{tuple(?p ?s ?o ?tag)}";
        List<TrioMonoid> tags = new ArrayList<>();
        
        try (InputStream input = service.path("sparql").queryParam("query", tagquery)
			  .accept("application/sparql-results+csv")
			  .get(InputStream.class);
            BufferedReader buf = new BufferedReader(new InputStreamReader(input));)
        {
            String line = buf.readLine();
            assert(line.equals("tag"));
            while((line = buf.readLine()) != null){
                TrioMonoid tag = new TrioMonoid(line.trim());
                tags.add(tag);
            }
        
        }

        return tags;
    }


    public String printGraph(){
        String selectall = "SELECT ?subject ?predicate ?object ?tag "
                + "WHERE {tuple (?predicate ?subject ?object ?tag)}";
        return service.path("sparql").queryParam("query", selectall)
			  .accept("application/sparql-results+tsv")
			  .get(String.class);    
    }

    public boolean checkUp(){
        if(service == null){return false;}
        try{
            return countTriples()>0;
        }catch(Exception ex){
            return false;
        }
    
    }


    @Override
    public String toString() {
        String res = "URI: "+ URI +"\n"
                + "basedata: " + basedata + "\n"
                + "Insert Dynam "+ insDyn + "\n"
                + "Delete Dynam "+ delDyn + "\n" ;
        res += "Views:  \n";
        for(Participant source : views.keySet()){
            res += "Source: "+ source.getURI() + "\n";
            for(BasicFragment view : views.get(source)){
                res += "\t" + view.toString() +"\n";
            }
        }
        return res;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.URI);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Participant other = (Participant) obj;
        if (!Objects.equals(this.URI, other.URI)) {
            return false;
        }
        return true;
    }
    
    
    
}
