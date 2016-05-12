/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Publishers;

import Operations.Operation;
import Operations.TMOperation;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.input.ReversedLinesFileReader;

/**
 *
 * Publishes a log file locally in disk
 * 
 * @author luisdanielibanesgonzalez
 * 
 */
public class FilePublisher implements Publisher{

    //ArrayList<TMOperation> log = new ArrayList<>();
    final Path path;
    PrintWriter writer;
    int lastline = 0;

    
    public FilePublisher(String logpath){
        try {
            path = Paths.get(logpath);
            BufferedWriter out = Files.newBufferedWriter(path, Charset.forName("UTF-8"));
            writer = new PrintWriter(out);
        } catch (IOException ex) {
            Logger.getLogger(FilePublisher.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error("IO error creating Log Publisher "+ex.getMessage());
        }
    }
/*
 * Prints the JSON one line representation in the file 
 */
    @Override
    public void handle(Operation op) {
        if (!(op instanceof TMOperation)){
            throw new Error(op.toString() +" is not a TMOperation");
        }else{
            TMOperation tmop = (TMOperation) op;
            writer.println(tmop.toJSON());
            writer.flush();
            lastline++;
        }
    }

    public void close(){
        writer.close();
    }

    public void destroy() throws IOException{
    
        writer.close();
        Files.delete(path);
    }

    public void reset(){
        writer.close();
        try {
            Files.deleteIfExists(path);
            BufferedWriter out = Files.newBufferedWriter(path, Charset.forName("UTF-8"));
            writer = new PrintWriter(out);
        } catch (IOException ex) {
            Logger.getLogger(FilePublisher.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error("IO error resetting Log Publisher "+ex.getMessage());
        }
    }

    public Path getPath(){
        return path;
    }

    public int getLastLine(){
        return lastline;
     }
    /*
     * Returns the requested sublog in JSON format, to avoid wasting resources
     * serializing to send to client
     */
    public List<String> getFromJSON(int from) throws IOException{
        ArrayList<String> sublog = new ArrayList<>();
        try(ReversedLinesFileReader tailer = 
                new ReversedLinesFileReader(path.toFile());)
        {
            String line = tailer.readLine();
            int i = lastline;
            while(line != null && i > from){
               sublog.add(0, line);
               i--;
               line = tailer.readLine();
            }
        
        }     
    
        return sublog;
    } 
    /*
    public int lastOp(){
        return log.size();
    }

    public List<TMOperation> getLog(){
        return log;
    }

    * */


    /* Get Operations from a given point of the log that concern a set of collabviews
     * if only a part of an operation concerns, we create a new one only
     * with this information but with the same trace.
     * Views are Construct where the Service Keyword is implicitly assumed to be this graph
     * 
     * 
    public ArrayList<TMOperation> getConcernedOperations(int from, Set<String> views){
        

        ArrayList<TMOperation> ops = new ArrayList<>();

        if(views.isEmpty()){
            ops.addAll(from, log);
            return ops;
        }

        //@TODO If the log was purged, an exception should be raised
        // By now we assume eternal log

        //@TODO This is terribly slow and unreliable
        // Maybe too much for a first iteration

        for(int i = from; i < log.size() ; i++){
            TMOperation o = log.get(i);

            TMGraph insGraph = getInsGraph(o);
            TMGraph delGraph = getDelGraph(o);
            for(String view : views){
                try {
                    Mappings mapIns = insGraph.query(view);
                    Graph concernedIns = (Graph)mapIns.getGraph();
                    Mappings mapDel = delGraph.query(view);
                    Graph concernedDel = (Graph)mapDel.getGraph();
                    ops.add(this.getTMOp(o, concernedIns, concernedDel));
                } catch (EngineException ex) {
                    Logger.getLogger(FilePublisher.class.getName()).log(Level.SEVERE, null, ex);
                    throw new Error(view + " is not a valid View here");
                }
                
            }
        
        }

        return ops;

        
    }

    /* Returns a graph containing the pentas of the toInsert part
     * of the operation.
     * 
    TMGraph getInsGraph(TMOperation op){

        TMGraph insGraph = new TMGraph("INSGRAPH");
        String insert = "INSERT DATA { ";
        
        for(SimplePenta penta : op.getInsert()){
            insert += "GRAPH <" + penta.getGraph()+ "> \n"
                    + "{tuple("
				  + penta.getPredicate() +" "
				  + penta.getSubject() + " "
				  + penta.getObject() + " "
				  + "'" + penta.getTag() + "'"
                    + ")}\n";    
        }
        insert += "}";
        try{
          insGraph.query(insert, true);
        }catch (Exception e){
            throw new Error("Exception querying "+ insert);
        }
        return insGraph;
    
    }
    
    TMGraph getDelGraph(TMOperation op){

        TMGraph delGraph = new TMGraph("INSGRAPH");
        String insert = "INSERT DATA { ";
        
        for(SimplePenta penta : op.getDelete()){
            insert += "GRAPH <" + penta.getGraph()+ "> \n"
                    + "{tuple("
				  + penta.getPredicate() +" "
				  + penta.getSubject() + " "
				  + penta.getObject() + " "
				  + "'" + penta.getTag() + "'"
                    + ")}\n";    
        }
        insert += "}";
        try{
          delGraph.query(insert, true);
        }catch (Exception e){
            throw new Error("Exception querying "+ insert);
        }
        return delGraph;
    
    }

    //Construct an operation from original op and their concerned graphs
    TMOperation getTMOp(TMOperation original, Graph cIns, Graph cDel){

        
        ArrayList<SimplePenta> ins = new ArrayList<>();
        ArrayList<SimplePenta> del = new ArrayList<>();
        try{
            
            for(Entity ent : cIns.getEdges()){
               SimplePenta p = new SimplePenta(ent);
               ins.add(p);
            }

            for(Entity ent: cDel.getEdges()){
               SimplePenta p = new SimplePenta(ent);
               del.add(p);
            }
        }catch (Exception e){
            throw new Error("Exception "+ e.toString());
        }
        TMOperation op = new TMOperation(original.getId(),ins,del);
        op.setTrace(original.getTrace());
        return op;
    
    }
     */
}
