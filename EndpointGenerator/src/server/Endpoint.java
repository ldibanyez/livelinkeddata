/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import fr.inria.edelweiss.kgramserver.webservice.EmbeddedJettyServer;
import java.net.URI;
import java.net.URISyntaxException;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.vfs.FileSystemException;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class Endpoint {

    public enum ENDPOINT_TYPE {VANILLA,TMFILELOG,TMMEMORYLOG};

    public static void startEndpoint(int port, ENDPOINT_TYPE type) throws FileSystemException, URISyntaxException{
    
       /////////////// Second server in this JVM
        // ToDo: Avoid this stuff and use the EmbeddedJettyServer provided ::
        URI webappUri;
        Server server2 = new Server(port);

        ServletHolder jerseyServletHolder_s1 = new ServletHolder(ServletContainer.class);
        jerseyServletHolder_s1.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");

        // Load depends
        if(type.equals(ENDPOINT_TYPE.TMFILELOG)){
         webappUri = TMFileLog.EmbeddedJettyServer.extractResourceDir("webapp", false);
            jerseyServletHolder_s1.setInitParameter("com.sun.jersey.config.property.packages", "TMFileLog");
        }else if (type.equals(ENDPOINT_TYPE.VANILLA)){
            webappUri = EmbeddedJettyServer.extractResourceDir("webapp", false);
            jerseyServletHolder_s1.setInitParameter("com.sun.jersey.config.property.packages", "fr.inria.edelweiss.kgramserver.webservice");
        }else if (type.equals(ENDPOINT_TYPE.TMMEMORYLOG)){
         webappUri = TMMemoryLog.EmbeddedJettyServer.extractResourceDir("webapp", false);
            jerseyServletHolder_s1.setInitParameter("com.sun.jersey.config.property.packages", "TMMemoryLog");
        
        }else {throw new Error("Incorrect ENDPOINT_TYPE. Panic!");}

        
        Context servletCtx_s1 = new Context(server2, "/kgram", Context.SESSIONS);
        servletCtx_s1.addServlet(jerseyServletHolder_s1, "/*");
        /*
        logger.info("----------------------------------------------");
        logger.info("Corese/KGRAM endpoint started on http://localhost:" + port + "/kgram");
        logger.info("----------------------------------------------");
*/
        ResourceHandler resource_handler_s1 = new ResourceHandler();
        resource_handler_s1.setWelcomeFiles(new String[]{"index.html"});
//        resource_handler_s1.setResourceBase("/Users/gaignard/Documents/Dev/svn-kgram/Dev/trunk/kgserver/src/main/resources/webapp");
        resource_handler_s1.setResourceBase(webappUri.getRawPath());
        ContextHandler staticContextHandler_s1 = new ContextHandler();
        staticContextHandler_s1.setContextPath("/");
        staticContextHandler_s1.setHandler(resource_handler_s1);
        /*
        logger.info("----------------------------------------------");
        logger.info("Corese/KGRAM webapp UI started on http://localhost:" + port);
        logger.info("----------------------------------------------");
*/
        HandlerList handlers_s1 = new HandlerList();
        handlers_s1.setHandlers(new Handler[]{staticContextHandler_s1, servletCtx_s1});
        server2.setHandler(handlers_s1);

        try {
            server2.start();
        } catch (Exception e) {
            e.printStackTrace();
        } 
    
    }

    /*
     * arg[0] port
     * args[1] Type of endpoint
     * args [2] Name of endpoint (If TM type)
     */
    public static void main(String [] args) throws URISyntaxException, FileSystemException{
    
            int port = Integer.parseInt(args[0]);
            String type = args[1];

            if(type.equalsIgnoreCase("VANILLA")){
                startEndpoint(port,Endpoint.ENDPOINT_TYPE.VANILLA);
            
            }else if (type.equalsIgnoreCase("TMFILELOG")){
                startEndpoint(port,Endpoint.ENDPOINT_TYPE.TMFILELOG);
            
            }
            else if (type.equalsIgnoreCase("TMMEMORYLOG")){
                startEndpoint(port,Endpoint.ENDPOINT_TYPE.TMMEMORYLOG);
            
            }else {
                throw new Error("Wrong graph type");
            }
            if(!type.equalsIgnoreCase("VANILLA")){
            
            MultivaluedMap formData = new MultivaluedMapImpl();
            //formData.add("remote_path", path);

            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);
            WebResource service = client.resource(new URI("http://localhost:"+port+"/kgram"));
//            System.out.println(service.path("sparql").path("reset").post(String.class).toString());
            
                formData.add("id", args[2]);
                service.path("sparql").path("name").post(formData);
            } 
    
    }

}
