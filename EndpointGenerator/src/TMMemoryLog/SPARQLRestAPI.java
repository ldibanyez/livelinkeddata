/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TMMemoryLog;

import fr.inria.acacia.corese.triple.parser.Dataset;
import fr.inria.edelweiss.kgram.core.Mappings;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import fr.inria.edelweiss.kgtool.print.CSVFormat;
import fr.inria.edelweiss.kgtool.print.JSOND3Format;
import fr.inria.edelweiss.kgtool.print.JSONFormat;
import fr.inria.edelweiss.kgtool.print.ResultFormat;
import fr.inria.edelweiss.kgtool.print.TSVFormat;
import fr.inria.edelweiss.kgtool.print.TripleFormat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;

import Graphs.TMGraph;
import Operations.TMOperation;
import Publishers.InMemoryLog;
import Publishers.TMLogPublisher;
import com.google.gson.Gson;
import fr.inria.acacia.corese.exceptions.EngineException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import javax.ws.rs.core.MediaType;
import org.apache.commons.io.IOUtils;

/**
 * KGRAM engine exposed as a rest web service. The engine can be remotely
 * initialized, populated with an RDF file, and queried through SPARQL requests.
 * 
 * Modified to expose a TMGraph
 *
 * @author Eric TOGUEM, eric.toguem@uy1.uninet.cm
 * @author Alban Gaignard, alban.gaignard@cnrs.fr
 * @author Luis-Daniel Ibáñez luis.ibanez@univ-nantes.fr
 */
@Path("sparql")
public class SPARQLRestAPI {

    private Logger logger = Logger.getLogger(RemoteProducer.class);
    //private static Graph graph = Graph.create(false);
    private static TMGraph graph;
    private static QueryProcess exec;
    private static InMemoryLog pub;
    private String headerAccept = "Access-Control-Allow-Origin";

    /**
     * This webservice is used assign a name to the TMGraph     
     *
     * It turns out that I don't know how to manipulate the javascript controller
     * So we left this as TODO
     */
    @POST
    @Path("/name")
    public Response nameGraph(@DefaultValue("Test") 
                            @FormParam("id") String id) {
        String output;
        if(graph != null){
            logger.info(output = "This graph already has a name");
            return Response.status(403).header(headerAccept, "*").entity(output).build();
        
        }else{
            graph = new TMGraph(id);
            exec = QueryProcess.create(graph.getGraph());
            // TODO, this should be a Property somewhere
            //pub = new TMLogPublisher("/home/ibanez/EndpointLoading/logs/"+id+".log");
            //pub = new TMLogPublisher("/tmp/logs/"+id+".log");
            pub = new InMemoryLog();
            graph.setPublisher(pub);
            logger.info(output = "The TMGraph has successfully been assigned the ID "+ id);
        }
        return Response.status(200).header(headerAccept, "*").entity(output).build();
    }

    /* For a future iteration
    @GET
    @Produces("application/sparql-results+json")
    @Path("/log")
    public Response getLog(@QueryParam("from") String from,
            @QueryParam("views") List<String> views) {
        try {
        if(graph == null){
            logger.error("Graph has not yet been identified/initialized");
            return Response.status(403).header(headerAccept, "*").entity("Graph has not yet been idenitified/initialized").build();
        }
            logger.info("The from :" + from);
            logger.info("The views :" + views.toString());

            TMLogPublisher pub = (TMLogPublisher)graph.getPublisher();
            logger.info("The log size :" + pub.lastOp());

            
            ArrayList<TMOperation> ops = pub.getConcernedOperations(Integer.parseInt(from), new HashSet<String>(views));
            Gson gson = new Gson();
            String serial = gson.toJson(ops);

            logger.info("The response :" + serial);
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(serial).build();

        } catch (Exception ex) {
            logger.error("Error while querying the remote KGRAM engine");
            ex.printStackTrace();
            return Response.status(500).header(headerAccept, "*").entity("Error while querying the remote KGRAM engine").build();
        }
    }
    * */

    /* This getLog is with a file
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("/log")
    public Response getLog() {
        try {
        if(graph == null){
            logger.error("Graph has not yet been identified/initialized");
            return Response.status(403).header(headerAccept, "*").entity("Graph has not yet been identified/initialized").build();
        }
            //File file = new File(((TMLogPublisher)graph.getPublisher()).getPath());
            java.nio.file.Path ph = ((TMLogPublisher)graph.getPublisher()).getPath();
            InputStream inps = Files.newInputStream(ph, StandardOpenOption.READ);
            return Response.ok(inps, MediaType.APPLICATION_OCTET_STREAM).build();

        } catch (Exception ex) {
            logger.error("Error retrieving log");
           ex.printStackTrace();
            return Response.status(500).header(headerAccept, "*").entity("Error retrieving log").build();
        }    
    }
    */

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/log")
    public Response getLog(@QueryParam("from") String from) {
        try {
        if(graph == null){
            logger.error("Graph has not yet been identified/initialized");
            return Response.status(403).header(headerAccept, "*").entity("Graph has not yet been identified/initialized").build();
        }
            //File file = new File(((TMLogPublisher)graph.getPublisher()).getPath());
            List<TMOperation> ops = pub.getFrom(Integer.parseInt(from));
            Gson gson = new Gson();
            return Response.ok(gson.toJson(ops), MediaType.APPLICATION_JSON).build();

        } catch (Exception ex) {
            logger.error("Error retrieving log");
           ex.printStackTrace();
            return Response.status(500).header(headerAccept, "*").entity("Error retrieving log").build();
        }    
    }

    
    /**
     * This webservice is used to reset the endpoint. This could be useful if we
     * would like our endpoint to point on another dataset
     * 
     * Entailment removed until support on TMGraph
     * 
     */
    @POST
    @Path("/reset")
    public Response initRDF() {
        String output;
        if(graph == null){
            logger.info(output = "Graph has not been named, call the /name service");
            return Response.status(403).header(headerAccept, "*").entity(output).build();
        }
            graph = new TMGraph(graph.getId());
            exec = QueryProcess.create(graph.getGraph());
            pub.reset();
            graph.setPublisher(pub);
            
        logger.info(output = "Endpoint successfully resetted");
        return Response.status(200).header(headerAccept, "*").entity(output).build();
    }

    /**
     * This webservice is used to reset/purge the log. 
     * 
     * 
     */
    @POST
    @Path("/logreset")
    public Response logReset() {
        String output;
        if(graph == null){
            logger.info(output = "Graph has not been named, call the /name service");
            return Response.status(403).header(headerAccept, "*").entity(output).build();
        }
          pub.reset();
            
        logger.info(output = "Log successfully resetted");
        //logger.info(output = "Not Yet Implemented");
        return Response.status(200).header(headerAccept, "*").entity(output).build();
    }
//    @POST
//    @Path("/upload")
//    @Consumes("multipart/form-data")
//    public Response uploadFile(@FormDataParam("file") InputStream f) {
//
//        // your code here to copy file to destFile
//        System.out.println("Received file " + f);
//
//        String output;
//        logger.info(output = "File uploaded.");
//        return Response.status(200).header(headerAccept, "*").entity(output).build();
//    }

    /**
     * This webservice is used to load a dataset to the endpoint. Therefore, if
     * we have many files for our datastore, we could load them by recursivelly
     * calling this webservice
     */
    @POST
    @Path("/load")
    public Response loadRDF(@FormParam("remote_path") String remotePath, @FormParam("source") String source) {
        if(graph == null){
            logger.error("Graph has not yet been identified/initialized");
            return Response.status(403).header(headerAccept, "*").entity("Graph has not yet been idenitified/initialized").build();
        }

        String output = "File Uploaded";
        if (source != null) {
            if (source.isEmpty()) {
                source = null;
            } else if (!source.startsWith("http://")) {
                source = "http://" + source;
            }
        }

        if (remotePath == null) {
            String error = "Null remote path";
            logger.error(error);
            return Response.status(404).header(headerAccept, "*").entity(error).build();
        }

        logger.debug(remotePath);

        if (remotePath.startsWith("http")) {
            if (remotePath.endsWith(".rdf") || remotePath.endsWith(".ttl") || remotePath.endsWith(".rdfs") || remotePath.endsWith(".owl")) {
                graph.load(remotePath, source);
            } else {
                try {
                    URL remoteURL = new URL(remotePath); 
                    logger.info("URL "+ remoteURL);
                    String insert = "INSERT DATA { "
                            + IOUtils.toString(remoteURL)
                            + "}";
                    graph.query(insert);
                    //TODO loading of .n3 or .nt
                    //return Response.status(404).header(headerAccept, "*").entity(output).build();
                    //return Response.status(404).header(headerAccept, "*").entity(output).build();
                } catch (MalformedURLException ex) {
                    java.util.logging.Logger.getLogger(SPARQLRestAPI.class.getName()).log(Level.SEVERE, null, ex);
                    return Response.status(500).header(headerAccept, "*").entity("Malformed remote path URL "+remotePath).build();
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(SPARQLRestAPI.class.getName()).log(Level.SEVERE, null, ex);
                    return Response.status(500).header(headerAccept, "*").entity("Problem accessing URL "+remotePath).build();
                } catch (EngineException ex) {
                    java.util.logging.Logger.getLogger(SPARQLRestAPI.class.getName()).log(Level.SEVERE, null, ex);
                    return Response.status(500).header(headerAccept, "*").entity("Problem loading quads "+remotePath).build();
                }
            }

        } else {
            logger.info("Loading " + remotePath);
            File f = new File(remotePath);
            if (!f.exists()) {
                logger.error(output = "File " + remotePath + " not found on the server!");
                return Response.status(404).header(headerAccept, "*").entity(output).build();
            }
            if (f.isDirectory()) {
                graph.load(remotePath, source);
            } else if (remotePath.endsWith(".rdf") || remotePath.endsWith(".rdfs") || remotePath.endsWith(".ttl") || remotePath.endsWith(".owl")) {
                graph.load(remotePath, source);
            } else if (remotePath.endsWith(".n3") || remotePath.endsWith(".nt")) {
                FileInputStream fis = null;
                logger.warn("NOT Loaded " + f.getAbsolutePath());
            }
        }
        logger.info(output = "Successfully loaded " + remotePath);
        return Response.status(200).header(headerAccept, "*").entity(output).build();
    }

    @GET
    @Produces("application/sparql-results+xml")
    public Response getTriplesXMLForGet(@QueryParam("query") String query,
            @QueryParam("default-graph-uri") List<String> defaultGraphUris,
            @QueryParam("named-graph-uri") List<String> namedGraphUris) {
        try {
            return Response.status(200).header(headerAccept, "*").entity(
                    ResultFormat.create(exec.query(query, createDataset(defaultGraphUris, namedGraphUris))).toString()).build();
        } catch (Exception ex) {
            logger.error("Error while querying the remote KGRAM engine");
            ex.printStackTrace();
            return Response.status(500).header(headerAccept, "*").entity("Error while querying the remote KGRAM engine").build();
        }
    }

    @GET
    @Produces("application/sparql-results+json")
    public Response getTriplesJSONForGet(@QueryParam("query") String query,
            @QueryParam("default-graph-uri") List<String> defaultGraphUris,
            @QueryParam("named-graph-uri") List<String> namedGraphUris) {

        if(graph == null){
            logger.error("Graph has not yet been identified/initialized");
            return Response.status(403).header(headerAccept, "*").entity("Graph has not yet been idenitified/initialized").build();
        }
        
        try {
            return Response.status(200).header(headerAccept, "*").entity(JSONFormat.create(exec.query(query, createDataset(defaultGraphUris, namedGraphUris))).toString()).build();
        } catch (Exception ex) {
            logger.error("Error while querying the remote KGRAM engine");
            ex.printStackTrace();
            return Response.status(500).header(headerAccept, "*").entity("Error while querying the remote KGRAM engine").build();
        }
    }

    @GET
    @Produces("application/sparql-results+json")
    @Path("/d3")
    public Response getTriplesJSONForGetWithGraph(@QueryParam("query") String query,
            @QueryParam("default-graph-uri") List<String> defaultGraphUris,
            @QueryParam("named-graph-uri") List<String> namedGraphUris) {
        try {
        if(graph == null){
            logger.error("Graph has not yet been identified/initialized");
            return Response.status(403).header(headerAccept, "*").entity("Graph has not yet been idenitified/initialized").build();
        }

            Mappings m = exec.query(query, createDataset(defaultGraphUris, namedGraphUris));

            String mapsD3 = "{ \"mappings\" : "
                    + JSONFormat.create(m).toString()
                    + " , "
                    + "\"d3\" : "
                    + JSOND3Format.create((Graph) m.getGraph()).toString()
                    + " }";

//            System.out.println(mapsD3);
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(mapsD3).build();

        } catch (Exception ex) {
            logger.error("Error while querying the remote KGRAM engine");
            ex.printStackTrace();
            return Response.status(500).header(headerAccept, "*").entity("Error while querying the remote KGRAM engine").build();
        }
    }

    @GET
    @Produces("application/sparql-results+csv")
    public Response getTriplesCSVForGet(@QueryParam("query") String query,
            @QueryParam("default-graph-uri") List<String> defaultGraphUris,
            @QueryParam("named-graph-uri") List<String> namedGraphUris) {
        try {
        if(graph == null){
            logger.error("Graph has not yet been identified/initialized");
            return Response.status(403).header(headerAccept, "*").entity("Graph has not yet been idenitified/initialized").build();
        }
            return Response.status(200).header(headerAccept, "*").entity(CSVFormat.create(exec.query(query, createDataset(defaultGraphUris, namedGraphUris))).toString()).build();
        } catch (Exception ex) {
            logger.error("Error while querying the remote KGRAM engine");
            return Response.status(500).header(headerAccept, "*").entity("Error while querying the remote KGRAM engine").build();
        }
    }

    @GET
    @Produces("application/sparql-results+tsv")
    public Response getTriplesTSVForGet(@QueryParam("query") String query,
            @QueryParam("default-graph-uri") List<String> defaultGraphUris,
            @QueryParam("named-graph-uri") List<String> namedGraphUris) {
        try {
        if(graph == null){
            logger.error("Graph has not yet been identified/initialized");
            return Response.status(403).header(headerAccept, "*").entity("Graph has not yet been idenitified/initialized").build();
        }
            return Response.status(200).header(headerAccept, "*").entity(TSVFormat.create(exec.query(query, createDataset(defaultGraphUris, namedGraphUris))).toString()).build();
        } catch (Exception ex) {
            logger.error("Error while querying the remote KGRAM engine");
            return Response.status(500).header(headerAccept, "*").entity("Error while querying the remote KGRAM engine").build();
        }
    }

    @GET
    @Produces("application/rdf+xml")
    public Response getRDFGraphXMLForGet(@QueryParam("query") String query,
            @QueryParam("default-graph-uri") List<String> defaultGraphUris,
            @QueryParam("named-graph-uri") List<String> namedGraphUris) {
        if(graph == null){
            logger.error("Graph has not yet been identified/initialized");
            return Response.status(403).header(headerAccept, "*").entity("Graph has not yet been idenitified/initialized").build();
        }
        try {
            return Response.status(200).header(headerAccept, "*").entity(ResultFormat.create(exec.query(query, createDataset(defaultGraphUris, namedGraphUris))).toString()).build();
        } catch (Exception ex) {
            logger.error("Error while querying the remote KGRAM engine");
            return Response.status(500).header(headerAccept, "*").entity("Error while querying the remote KGRAM engine").build();
        }
    }

    @GET
    @Produces("text/turtle")
    public Response getRDFGraphNTripleForGet(@QueryParam("query") String query,
            @QueryParam("default-graph-uri") List<String> defaultGraphUris,
            @QueryParam("named-graph-uri") List<String> namedGraphUris) {
        try {
        if(graph == null){
            logger.error("Graph has not yet been identified/initialized");
            return Response.status(403).header(headerAccept, "*").entity("Graph has not yet been idenitified/initialized").build();
        }
            Mappings maps = exec.query(query, createDataset(defaultGraphUris, namedGraphUris));
            String ttl = TripleFormat.create(maps, true).toString();
            logger.debug(query);
            logger.debug(ttl);
            return Response.status(200).header(headerAccept, "*").entity(ttl).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while querying the remote KGRAM engine");
            return Response.status(500).header(headerAccept, "*").entity("Error while querying the remote KGRAM engine").build();
        }
    }

    @POST
    @Produces("application/sparql-results+xml")
    public Response getTriplesXMLForPost(@DefaultValue("")
            @FormParam("query") String query,
            @FormParam("default-graph-uri") List<String> defaultGraphUris,
            @FormParam("named-graph-uri") List<String> namedGraphUris, String message) {
        try {
            if (query.equals("")) {
                query = message;
            }
        if(graph == null){
            logger.error("Graph has not yet been identified/initialized");
            return Response.status(403).header(headerAccept, "*").entity("Graph has not yet been idenitified/initialized").build();
        }
            return Response.status(200).header(headerAccept, "*").entity(ResultFormat.create(exec.query(query, createDataset(defaultGraphUris, namedGraphUris))).toString()).build();
        } catch (Exception ex) {
            logger.error("Error while querying the remote KGRAM engine");
            return Response.status(500).header(headerAccept, "*").entity("Error while querying the remote KGRAM engine").build();
        }
    }

    @POST
    @Produces("application/sparql-results+json")
    public Response getTriplesJSONForPost(@DefaultValue("")
            @FormParam("query") String query,
            @FormParam("default-graph-uri") List<String> defaultGraphUris,
            @FormParam("named-graph-uri") List<String> namedGraphUris,
            String message) {
        try {
            if (query.equals("")) {
                query = message;
            }
        if(graph == null){
            logger.error("Graph has not yet been identified/initialized");
            return Response.status(403).header(headerAccept, "*").entity("Graph has not yet been idenitified/initialized").build();
        }
            return Response.status(200).header(headerAccept, "*").entity(JSONFormat.create(exec.query(query, createDataset(defaultGraphUris, namedGraphUris))).toString()).build();
        } catch (Exception ex) {
            logger.error("Error while querying the remote KGRAM engine");
            return Response.status(500).header(headerAccept, "*").entity("Error while querying the remote KGRAM engine").build();
        }
    }

    @POST
    @Produces("application/sparql-results+csv")
    public Response getTriplesCSVForPost(@DefaultValue("")
            @FormParam("query") String query,
            @FormParam("default-graph-uri") List<String> defaultGraphUris,
            @FormParam("named-graph-uri") List<String> namedGraphUris,
            String message) {
        try {
            if (query.equals("")) {
                query = message;
            }
        if(graph == null){
            logger.error("Graph has not yet been identified/initialized");
            return Response.status(403).header(headerAccept, "*").entity("Graph has not yet been idenitified/initialized").build();
        }
            return Response.status(200).header(headerAccept, "*").entity(CSVFormat.create(exec.query(query, createDataset(defaultGraphUris, namedGraphUris))).toString()).build();
        } catch (Exception ex) {
            logger.error("Error while querying the remote KGRAM engine");
            return Response.status(500).header(headerAccept, "*").entity("Error while querying the remote KGRAM engine").build();
        }
    }

    @POST
    @Produces("application/sparql-results+tsv")
    public Response getTriplesTSVForPost(@DefaultValue("")
            @FormParam("query") String query,
            @FormParam("default-graph-uri") List<String> defaultGraphUris,
            @FormParam("named-graph-uri") List<String> namedGraphUris,
            String message) {
        try {
            if (query.equals("")) {
                query = message;
            }
        if(graph == null){
            logger.error("Graph has not yet been identified/initialized");
            return Response.status(403).header(headerAccept, "*").entity("Graph has not yet been idenitified/initialized").build();
        }
            return Response.status(200).header(headerAccept, "*").entity(TSVFormat.create(exec.query(query, createDataset(defaultGraphUris, namedGraphUris))).toString()).build();
        } catch (Exception ex) {
            logger.error("Error while querying the remote KGRAM engine");
            return Response.status(500).header(headerAccept, "*").entity("Error while querying the remote KGRAM engine").build();
        }
    }

    @POST
    @Produces("application/rdf+xml")
    public Response getRDFGraphXMLForPost(@DefaultValue("")
            @FormParam("query") String query,
            @FormParam("default-graph-uri") List<String> defaultGraphUris,
            @FormParam("named-graph-uri") List<String> namedGraphUris,
            String message) {
        try {
            if (query.equals("")) {
                query = message;
            }
        if(graph == null){
            logger.error("Graph has not yet been identified/initialized");
            return Response.status(403).header(headerAccept, "*").entity("Graph has not yet been idenitified/initialized").build();
        }
            return Response.status(200).header(headerAccept, "*").entity(ResultFormat.create(exec.query(query, createDataset(defaultGraphUris, namedGraphUris))).toString()).build();
        } catch (Exception ex) {
            logger.error("Error while querying the remote KGRAM engine");
            return Response.status(500).header(headerAccept, "*").entity("Error while querying the remote KGRAM engine").build();
        }
    }

    @POST
    @Produces("text/nt")
    public Response getRDFGraphNTripleForPost(@DefaultValue("")
            @FormParam("query") String query,
            @FormParam("default-graph-uri") List<String> defaultGraphUris,
            @FormParam("named-graph-uri") List<String> namedGraphUris,
            String message) {
        try {
        if(graph == null){
            logger.error("Graph has not yet been identified/initialized");
            return Response.status(403).header(headerAccept, "*").entity("Graph has not yet been idenitified/initialized").build();
        }
            return Response.status(200).header(headerAccept, "*").entity(TripleFormat.create(exec.query(query, createDataset(defaultGraphUris, namedGraphUris))).toString()).build();
        } catch (Exception ex) {
            logger.error("Error while querying the remote KGRAM engine");
            return Response.status(500).header(headerAccept, "*").entity("Error while querying the remote KGRAM engine").build();
        }
    }

    /// SPARQL 1.1 Update ///
    //update via URL-encoded POST
    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Path("/update")
    public Response updateTriplesEncoded(@FormParam("update") String query,
            @FormParam("default-graph-uri") List<String> defaultGraphUris,
            @FormParam("named-graph-uri") List<String> namedGraphUris) {
        try {
            logger.info(query);
        if(graph == null){
            logger.error("Graph has not yet been identified/initialized");
            return Response.status(403).header(headerAccept, "*").entity("Graph has not yet been identified/initialized").build();
        }
            if (query != null) {
                // I don't know this way of querying the graph
                // seems for specifying in the POST the active graphs
                //exec.query(message, createDataset(defaultGraphUris, namedGraphUris));
                graph.query(query);
            } else {
                logger.warn("Null update query !");
            }

            return Response.status(200).header(headerAccept, "*").build();
        } catch (Exception ex) {
            logger.error("Error while querying the remote KGRAM engine");
            ex.printStackTrace();
            return Response.status(500).header(headerAccept, "*").entity("Error while updating the Corese/KGRAM endpoint").build();
        }
    }

    /// SPARQL 1.1 Update ///
    //Direct update 
    @POST
    @Consumes("application/sparql-update")
    @Path("/update")
    public Response updateTriplesDirect(String message,
            @QueryParam("default-graph-uri") List<String> defaultGraphUris,
            @QueryParam("named-graph-uri") List<String> namedGraphUris) {
        try {
            logger.info(message);
        if(graph == null){
            logger.error("Graph has not yet been identified/initialized");
            return Response.status(403).header(headerAccept, "*").entity("Graph has not yet been idenitified/initialized").build();
        }
            if (message != null) {
                // I don't know this way of querying the graph
                // seems for specifying in the POST the active graphs
                //exec.query(message, createDataset(defaultGraphUris, namedGraphUris));
                graph.query(message);
            } else {
                logger.warn("Null update query !");
            }

            return Response.status(200).header(headerAccept, "*").build();
        } catch (Exception ex) {
            logger.error("Error while querying the remote KGRAM engine");
            ex.printStackTrace();
            return Response.status(500).header(headerAccept, "*").entity("Error while updating the Corese/KGRAM endpoint").build();
        }
    }

    @HEAD
    public Response getTriplesForHead(@QueryParam("query") String query,
            @QueryParam("default-graph-uri") List<String> defaultGraphUris,
            @QueryParam("named-graph-uri") List<String> namedGraphUris) {
        try {
        if(graph == null){
            logger.error("Graph has not yet been identified/initialized");
            return Response.status(403).header(headerAccept, "*").entity("Graph has not yet been idenitified/initialized").build();
        }
            Mappings mp = exec.query(query, createDataset(defaultGraphUris, namedGraphUris));
            return Response.status(mp.size() > 0 ? 200 : 400).header(headerAccept, "*").entity("Query has no response").build();
        } catch (Exception ex) {
            logger.error("Error while querying the remote KGRAM engine");
            return Response.status(500).header(headerAccept, "*").entity("Error while querying the remote KGRAM engine").build();
        }
    }

    /**
     * Creates a Corese/KGRAM Dataset based on a set of default or named graph
     * URIs. For *strong* SPARQL compliance, use dataset.complete() before
     * returning the dataset.
     *
     * @param defaultGraphUris
     * @param namedGraphUris
     * @return a dataset if the parameters are not null or empty.
     */
    private Dataset createDataset(List<String> defaultGraphUris, List<String> namedGraphUris) {
        if (((defaultGraphUris != null) && (!defaultGraphUris.isEmpty())) || ((namedGraphUris != null) && (!namedGraphUris.isEmpty()))) {
            Dataset ds = Dataset.newInstance(defaultGraphUris, namedGraphUris);
            return ds;
        } else {
            return null;
        }
    }

    /**
     * This function is used to copy the InputStream into a local file.
     */
    private void writeToFile(InputStream uploadedInputStream,
            File uploadedFile) throws IOException {
        OutputStream out = new FileOutputStream(uploadedFile);
        int read = 0;
        byte[] bytes = new byte[1024];
        while ((read = uploadedInputStream.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }
        out.flush();
        out.close();
    }
}
