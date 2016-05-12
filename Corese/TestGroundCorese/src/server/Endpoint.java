/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import fr.inria.edelweiss.kgramserver.webservice.EmbeddedJettyServer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
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


    public static void startEndpoint(int port, boolean TM) throws FileSystemException, URISyntaxException{
    
       /////////////// Second server in this JVM
        URI webappUri1 = EmbeddedJettyServer.extractResourceDir("webapp", true);
        Server server2 = new Server(port);

        ServletHolder jerseyServletHolder_s1 = new ServletHolder(ServletContainer.class);
        jerseyServletHolder_s1.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");

        // Load depends
        if(TM){
            jerseyServletHolder_s1.setInitParameter("com.sun.jersey.config.property.packages", "webservice");
        }else{
            jerseyServletHolder_s1.setInitParameter("com.sun.jersey.config.property.packages", "fr.inria.edelweiss.kgramserver.webservice");
        }

        
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
        resource_handler_s1.setResourceBase(webappUri1.getRawPath());
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

    public static void main(String[] args){
        try {
            startEndpoint(21212,true);
        } catch (FileSystemException ex) {
            Logger.getLogger(Endpoint.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(Endpoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    
    }
    
}
