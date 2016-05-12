/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import Provenance.TrioMonoid;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import network.NodeRunner.EndpointType;
import org.apache.commons.lang.time.StopWatch;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class NetworkRunner {

    static XPath xPath =  XPathFactory.newInstance().newXPath();
    private final static Logger LOGGER = 
            Logger.getLogger(NetworkRunner.class.getName());
    
    // Load the participants hosted by myHostName
    static List<Participant> getParticipants(Document network) {
    
        try{
        ArrayList<Participant> myHostedParts = new ArrayList<>();
        // The normalize space is to support pretty printed inputs
        // remember that space in text nodes is significant
        NodeList myHosted = (NodeList) xPath.compile("/network/participant")
                    .evaluate(network,XPathConstants.NODESET);

        for (int i = 0; i < myHosted.getLength(); i++) {
            Node nodp = myHosted.item(i);
            Participant p = new Participant();
            p.setHost("http://"+xPath
                    .compile("host")
                    .evaluate(nodp).trim());
            p.setPort(Integer.parseInt(xPath
                    .compile("port")
                    .evaluate(nodp).trim())
                    );
            p.setURI(p.getHost()+":"+p.getPort()+"/kgram");
            p.setBasedata(xPath.compile("basedata").evaluate(nodp).trim());
            p.setInsDyn(Double.parseDouble(xPath.compile("dynamic/insert").evaluate(nodp).trim()));
            p.setDelDyn(Double.parseDouble(xPath.compile("dynamic/delete").evaluate(nodp).trim()));
            //System.out.println("Source " +mySources.item(i).getAttributes().getNamedItem("id").getNodeValue());
            HashMap<Participant,Set<BasicFragment>> sv = new HashMap<>();
            NodeList sources = (NodeList) xPath
                    .compile("source")
                    .evaluate(nodp,XPathConstants.NODESET);
            for(int j = 0 ; j < sources.getLength() ; j++){
                Node nodesource = sources.item(j);
                NodeList views = nodesource.getChildNodes();
                HashSet<BasicFragment> s = new HashSet<>();
                for(int k =0 ; k < views.getLength() ; k++){
                    if(views.item(k) instanceof Element){
                        Node nodeview = views.item(k);
                        BasicFragment frag = new BasicFragment();
                        frag.setSubject(xPath.compile("subject").evaluate(nodeview).trim());
                        frag.setPredicate(xPath.compile("predicate").evaluate(nodeview).trim());
                        frag.setObject(xPath.compile("object").evaluate(nodeview).trim());
                        s.add(frag);
                    }
                }
                Participant source = new Participant();
                source.setURI(NodeRunner.getURI(network,
                        nodesource.getAttributes()
                        .getNamedItem("id").getNodeValue()));
                try {
                    source.setService();
                } catch (URISyntaxException ex) {
                    Logger.getLogger(NetworkRunner.class.getName()).log(Level.SEVERE, null, ex);
                    throw new Error
                            ("Problem Loading participant " 
                            + ex.getMessage()+ " " +source.getURI());
                }
                sv.put(source, s);
            }
            p.setViews(sv);
            p.setService();
            myHostedParts.add(p);
        }

        return myHostedParts;
        } catch (XPathExpressionException ex) {
            Logger.getLogger(NetworkRunner.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error("Problem with XPath query during network loading");
        } catch (URISyntaxException ex) {
            Logger.getLogger(NetworkRunner.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error("URI error setting service, check network definition");
        }
    }

    static void printStats(List<Participant> allParticipants){
    
    
        System.out.println(
                "Participant  "
                + "NumPentas "
                + "LongestPolynome "
                + "AveragePolynome "
                + "HighestCoeff "
                + "AverageCoeff "
                + "");

        long totalpentas = 0;
        int longestpoly = 0;
        double sumavgpoly = 0;
        long highestcoeff = 0;
        double sumavgcoeff = 0;
        

        for(Participant p: allParticipants){
            StringBuilder resline = new StringBuilder();
            resline.append(p.getURI()).append(" ");
            resline.append(p.countTriples()).append(" ");
            totalpentas += p.countTriples();
            
            List<TrioMonoid> polys;
            try {
                polys = p.getTagList();
            } catch (IOException ex) {
                Logger.getLogger(NetworkRunner.class.getName()).log(Level.SEVERE, null, ex);
                throw new Error("Something went wrong extracting tags from participant "+ p.getURI());
            }
            
            TrioMonoid longest = Collections.max(polys, TrioMonoid.numTermsComparator);
            resline.append(longest.numTerms()).append(" ");
            longestpoly = Math.max(longestpoly, longest.numTerms());

            //average polynome
            int sum = 0;
            for(TrioMonoid tm : polys){
                sum += tm.numTerms();
            }
            resline.append(String.format("%.1f",((double)sum)/polys.size()));
            resline.append(" ");
            sumavgpoly +=((double)sum)/polys.size();

            TrioMonoid highercoeff = 
                    Collections.max(polys, TrioMonoid.coeffComparator);
            Long themax = Collections.max(highercoeff.getCoeffs());
            resline.append(themax);
            resline.append(" ");
            highestcoeff = Math.max(highestcoeff, themax.longValue());

            //average coefficient
            Long sumcoeff = new Long(0);
            int numcoeff = 0;
            for(TrioMonoid tm : polys){
                for(Long coeff : tm.getCoeffs()){
                    sumcoeff += coeff;
                }
                numcoeff += tm.getCoeffs().size();
            }
            resline.append(String.format("%.1f",((double)sumcoeff)/numcoeff));
            sumavgcoeff += ((double)sumcoeff)/numcoeff;
            System.out.println(resline.toString());
        }
        
        System.out.println(
                "#TotalParticipants  "
                + "NetTotalPentas "
                + "NetLongestPolynome "
                + "NetAveragePolynome "
                + "NetHighestCoeff "
                + "NetAverageCoeff "
                + "");

        System.out.println(
                String.format("%d ", allParticipants.size())
                + String.format("%d ", totalpentas)
                + String.format("%d ", longestpoly)
                + String.format("%.1f ", 
                    (sumavgpoly/(float)allParticipants.size()))
                + String.format("%d ", highestcoeff)
                + String.format("%.1f ", 
                    (sumavgcoeff/(float)allParticipants.size()))
                + "");
    
    
    }
       private static String getTimeString(long millis) {
        int minutes = (int) (millis / (1000 * 60));
        int seconds = (int) ((millis / 1000) % 60);
        int milliseconds = (int) (millis % 1000);
        return String.format("%d:%02d.%03d", minutes, seconds, milliseconds);
    } 
    
    public static void main(String args[]) throws ParserConfigurationException, XPathExpressionException, InterruptedException, IOException, Exception{
    
        
        String xmlnetwork = args[0];
        Document network = NodeRunner.loadNetwork(xmlnetwork);

        LOGGER.setUseParentHandlers(false);
        
        ConsoleHandler ch = new ConsoleHandler();
        if(args[1].equalsIgnoreCase("verbose")){
            LOGGER.setLevel(Level.ALL);
            ch.setLevel(Level.ALL);
        } else if(args[1].equalsIgnoreCase("quiet")){
            LOGGER.setLevel(Level.INFO);
            ch.setLevel(Level.INFO);
        } else {
            throw new Error("Please set verbosity");
        }
        LOGGER.addHandler(ch);

        String endpointtype = args[2];
        EndpointType et;

        if(endpointtype.equalsIgnoreCase("MEMORY")){
            et = EndpointType.MEMORY;
        } else if(endpointtype.equalsIgnoreCase("FILE")){
            et = EndpointType.FILE;
        } else {
            throw new Error("Please set the endpoint type");
        }

        int partialprint = Integer.parseInt(args[3]);

        int cut = Integer.parseInt(args[4]);

        List<Participant> allParticipants = getParticipants(network);
            LOGGER.log(Level.INFO, "{0} participants were loaded: \n {1}",
                new Object[]{allParticipants.size(),allParticipants});


        for(Participant p: allParticipants){
            while(!p.checkUp()){
                Thread.sleep(500);
            }
            LOGGER.log(Level.INFO, "Dynamizing {0}",p.getURI());
            p.dynamizeAllPredicates();
        }
            LOGGER.log(Level.INFO, "Data Dynamized at all participants");

        //pull changes
        boolean idle;
        boolean[] idlenesses = new boolean[allParticipants.size()]; 
        int i = 1;
        
        StopWatch timer = new StopWatch();
        timer.start();
        
        do{
            idle = true;
            int j = 0;
            for(Participant p: allParticipants){
                try{
                // El compilador se hace el astuto aquí y corta luego
                // luego del primer falso. Implicando que en grafos densos
                // alguien puede jalar bastante tarde...
                //idle = idle && !p.pull();
                idlenesses[j] = !p.pull(et);
                j++;
                }catch (OutOfMemoryError err){
                
                    LOGGER.log(Level.SEVERE, 
                        "Ran out of memory while pulling from {0} at round {1}",
                        new Object[]{p.getURI(),i});
                    throw err;
                }
            }
            // if everyone is idle, stop
            for(int k = 0 ; k< idlenesses.length ; k++){
                idle = idle && idlenesses[k];
            }
            
            if(idle){
                break;
            }else{
                
                if(i % partialprint == 0){
                    timer.suspend();
                    System.out.println
                            ("# Partial result at round: "+(i));
                    System.out.println
                            ("# Time elapsed  ~ "+getTimeString(timer.getTime())+"s");
                    printStats(allParticipants);
                    timer.resume();
                }

                if(i == cut){
                    System.out.println("# Maximum number of iterations reached: "+(i));
                    System.out.println
                            ("# Time elapsed  ~ "+getTimeString(timer.getTime())+"s");
                    printStats(allParticipants);
                    break;
                }

                // Not needed if we want to check aliveness with partial prints
                //Logger.getLogger(NetworkRunner.class.getName())
                //        .log(Level.INFO, "pulling round {0} finished and still not idle. Going to sleep", i);
                
                i+=1;
                //Thread.sleep(waittime);
            }
        }while(!idle);

        timer.stop();
        //Print Stats
        if(i!=cut){
            System.out.println("# Idleness achieved at round: "+(i));
            System.out.println
                    ("# Time elapsed  ~ "+getTimeString(timer.getTime())+"s");
            printStats(allParticipants);
        }

        for(Participant p: allParticipants){
            LOGGER.log(Level.INFO, "{0}\n{1}",
                    new Object[]{p.getURI(), p.printGraph()});
        }
    
    }
    
}
