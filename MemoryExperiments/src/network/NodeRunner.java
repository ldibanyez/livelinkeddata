/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class NodeRunner {

    static XPath xPath =  XPathFactory.newInstance().newXPath();

    public enum EndpointType {MEMORY, FILE};
    
     public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

    transformer.transform(new DOMSource(doc), 
         new StreamResult(new OutputStreamWriter(out, "UTF-8")));
}

    static Document loadNetwork(String xmlpath) throws ParserConfigurationException{
    
        //Get the DOM Builder Factory
	    DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();
	    factory.setValidating(true);
        factory.setIgnoringElementContentWhitespace(true);

	    //Get the DOM Builder
	    DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new ErrorHandler() {
        @Override
        public void error(SAXParseException exception) throws SAXException {
            // do something more useful in each of these handlers
            exception.printStackTrace();
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            exception.printStackTrace();
        }

        @Override
        public void warning(SAXParseException exception) throws SAXException {
            exception.printStackTrace();
        }
    });
        builder.setEntityResolver(new EntityResolver() {
    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {
        if (systemId.contains("Network.dtd")) {
            return new InputSource(NodeRunner.class.getResourceAsStream("Network.dtd"));
        } else {
            throw new Error("Parsing something not following Network.dtd");
        }
    }
    });
        try {
            Document network = 
                    builder.parse(new FileInputStream(xmlpath));
            return network;
        } catch (SAXException ex) {
            Logger.getLogger(NodeRunner.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error("Problem parsing network xml");
        } catch (IOException ex) {
            Logger.getLogger(NodeRunner.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error("Problem parsing network xml");
        }
    
    
    }

    // The URI follows the kgram convention
    static String getURI(Document network, String nodeid){
        try {
            String host = xPath.compile("/network/participant[@id='"+nodeid+"']/host").evaluate(network);
            String port = xPath.compile("/network/participant[@id='"+nodeid+"']/port").evaluate(network);
            return "http://"+host.trim()+":"+port.trim()+"/kgram";
        } catch (XPathExpressionException ex) {
            Logger.getLogger(NodeRunner.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error("Problem getting URI");
        }
        
    }

    // Load the participants hosted by myHostName
    static List<Participant> getParticipants(Document network, String myHostName) throws XPathExpressionException, URISyntaxException{
    
        ArrayList<Participant> myHostedParts = new ArrayList<>();
        // The normalize space is to support pretty printed inputs
        // remember that space in text nodes is significant
        NodeList myHosted = (NodeList) xPath.compile("/network/participant[normalize-space(host)='"+myHostName+"']")
                    .evaluate(network,XPathConstants.NODESET);

        for (int i = 0; i < myHosted.getLength(); i++) {
            Node nodp = myHosted.item(i);
            Participant p = new Participant();
            p.setHost("http://"+myHostName.trim());
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
                source.setURI(getURI(network,
                        nodesource.getAttributes()
                        .getNamedItem("id").getNodeValue()));
                try {
                    source.setService();
                } catch (URISyntaxException ex) {
                    Logger.getLogger(NodeRunner.class.getName()).log(Level.SEVERE, null, ex);
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
    }

    static Process[] startEndpoints(List<Participant> myHostedParts, EndpointType type)
            throws IOException, InterruptedException {
    
        URLClassLoader cl = (URLClassLoader)ClassLoader.getSystemClassLoader();
        URL[] urls = cl.getURLs();
        // There should be only the MemoryExperiments.jar in the cp
        assert(urls.length == 1);
        String jarPath = urls[0].getFile();
        
        // Start and load endpoints
        
        Process[] processes = new Process[myHostedParts.size()];
        int pindex = 0;
        for(Participant p: myHostedParts){
            List<String> arguments = new ArrayList<>();
            //arguments.add("nohup");
            arguments.add("java");
            arguments.add("-cp"); arguments.add(jarPath);
            switch(type){
                case MEMORY: 
                    arguments.add("TMMemoryLog.EmbeddedJettyServer");
                case FILE:
                    arguments.add("TMFileLog.EmbeddedJettyServer");
            }
            arguments.add("-p"); arguments.add(Integer.toString(p.getPort()));
            // port  is unique in our setup
            //arguments.add("-n"); arguments.add(p.getURI());
            arguments.add("-n"); arguments.add("p"+Integer.toString(p.getPort()));
            arguments.add("-l"); arguments.add(p.getBasedata());
            //arguments.add("&>/tmp/log"+p.getPort());
            //arguments.add("&");
            ProcessBuilder pb = new ProcessBuilder(arguments);
            pb.redirectErrorStream(true);
            // this is not portable... check Alban's code
            pb.redirectOutput(new File("/tmp/log"+p.getPort()));
            //for(String arg : pb.command()){
            //   System.out.println(arg.toString());
            // }
            processes[pindex] = pb.start();
            pindex += 1;
            Thread.sleep(1000);
            while(!p.checkUp()){
                Thread.sleep(1000);
            }
            Logger.getLogger(NodeRunner.class.getName())
                .log(Level.INFO, "Endpoint for participant {0} successfuly started", p.getURI());
        }
        return processes;
    
    }

    static void writeStatus(Path myStatusFile,boolean idle) throws UnsupportedEncodingException, IOException{
        String responsecode;
        if(idle){
            responsecode = "1";
            Files.write(myStatusFile, responsecode.getBytes("UTF-8"),StandardOpenOption.TRUNCATE_EXISTING);
        } else {
            responsecode = "0";
            Files.write(myStatusFile, responsecode.getBytes("UTF-8"),StandardOpenOption.TRUNCATE_EXISTING);
        }
        
    
    }

    static boolean checkOthersIdle(Path statusDir,Path myStatusFile) throws IOException{
        boolean allIdle = true;
        // List all files in the statusDir except mine
        // this to avoid to update before time
        for(File stfile : FileUtils.listFiles(statusDir.toFile()
                ,FileFilterUtils.notFileFilter
                (FileFilterUtils.nameFileFilter(myStatusFile.getFileName().toString())),
                FileFilterUtils.falseFileFilter())){

            String status = new String(Files.readAllBytes(stfile.toPath()));
            Logger.getLogger(NodeRunner.class.getName())
                    .log(Level.INFO, "read {0} status as {1}",
                    new String[]{stfile.getName(),status});

            if(status.trim().equalsIgnoreCase("0")){
                return false;
            }
        }
        return allIdle;
    
    }

    
    public static void main(String [] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException, InterruptedException, URISyntaxException, Exception{
        String xmlnetwork = args[0];


        Document network = loadNetwork(xmlnetwork);

        //printDocument(network,System.out);

        String myHostName = args[1];

        String endpointtype = args[2];

        EndpointType et;

        if(endpointtype.equalsIgnoreCase("MEMORY")){
            et = EndpointType.MEMORY;
        } else if(endpointtype.equalsIgnoreCase("FILE")){
            et = EndpointType.FILE;
        } else {
            throw new Error("Please set the endpoint type");
        }

        if(args[3].equalsIgnoreCase("verbose")){
            Logger.getLogger(NodeRunner.class.getName()).setLevel(Level.ALL);
        } else if(args[3].equalsIgnoreCase("quiet")){
            Logger.getLogger(NodeRunner.class.getName()).setLevel(Level.SEVERE);
        } else {
            throw new Error("Please set verbosity");
        }

        List<Participant> myHostedParts = getParticipants(network,myHostName);

        Logger.getLogger(NodeRunner.class.getName())
                .log(Level.INFO, "{0} participants were loaded: \n {1}",
                new Object[]{myHostedParts.size(),myHostedParts});
        
        Process [] processes = {};
        try{
            processes = startEndpoints(myHostedParts,et);

        Path killerfile = Paths.get(args[4]);
        // Keep the processes alive until the filtokill
        // gets created
        Logger.getLogger(NodeRunner.class.getName())
                .log(Level.INFO, "Waiting for the appearance of {0}",killerfile);
        while(!Files.exists(killerfile)){
            Thread.sleep(1000);
        }

        }finally{
        
        Logger.getLogger(NodeRunner.class.getName())
                .log(Level.INFO, "Killerfile appeared, killing endpoints");
            for(Process proc : processes){
                proc.destroy();
            }
        }

        
        /*
        try{

        for(Participant p: myHostedParts){
            p.dynamizeAllPredicates();
        }
        Logger.getLogger(NodeRunner.class.getName())
                .log(Level.INFO, "Data Dynamized");

        //pull changes
        boolean idle;
        int i = 1;
        do{
        idle = true;
        for(Participant p: myHostedParts){
            idle = idle && !p.pull();
            //Thread.sleep(waittime);
        }
        writeStatus(myStatusFile,idle);
        // if all my endpoints are idle, check the others
        if(idle){
            idle = idle && checkOthersIdle(processDir,myStatusFile);
        }
            writeStatus(myStatusFile,idle);
        // if everyone is idle, stop
        if(idle){
            break;
        }else{
        
            Logger.getLogger(NodeRunner.class.getName())
                    .log(Level.INFO, "pulling round {0} finished and still not idle. Going to sleep", i);
            i+=1;
            Thread.sleep(waittime*myHostedParts.size());
        }
        
        }while(!idle);

        //Print Stats
        System.out.println("# Rounds to idleness: "+(i));
        System.out.println(
                "Participant  "
                + "NumPentas "
                + "LongestPolynome "
                + "AveragePolynome "
                + "HighestCoeff "
                + "AverageCoeff "
                + "");
        

        for(Participant p: myHostedParts){
            StringBuilder resline = new StringBuilder();
            resline.append(p.getURI()).append(" ");
            resline.append(p.countTriples()).append(" ");
            
            List<TrioMonoid> polys = p.getTagList();
            
            TrioMonoid longest = Collections.max(polys, TrioMonoid.numTermsComparator);
            resline.append(longest.numTerms()).append(" ");

            //average polynome
            int sum = 0;
            for(TrioMonoid tm : polys){
                sum += tm.numTerms();
            }
            resline.append(String.format("%.1f",((double)sum)/polys.size()));
            resline.append(" ");

            TrioMonoid highercoeff = 
                    Collections.max(polys, TrioMonoid.coeffComparator);
            resline.append(Collections.max(highercoeff.getCoeffs()));
            resline.append(" ");

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
            System.out.println(resline.toString());
        }

        for(Participant p: myHostedParts){
        Logger.getLogger(NodeRunner.class.getName())
                .log(Level.INFO, "{0}\n{1}", new Object[]{p.getURI(), p.printGraph()});
        }

        }
        finally{
            for(Process proc : processes){
                proc.destroy();
            }
        }
        * */
        
    }
    
}
