/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package memoryexperiments;

import Experiments.ObjectSizeCalculator;
import Graphs.TMGraph;
import Graphs.TMTagger;
import Operations.SimplePenta;
import Provenance.Token;
import Provenance.TrioMonoid;
import fr.inria.acacia.corese.exceptions.EngineException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.RandomStringUtils;


/**
 *
 * @author luisdanielibanesgonzalez
 */
public class TMMemoryUsageTOSC {
    
int initialCounter;
String baseID;
public enum Mode {CONCURRENCE,PATH,BOTH};
TMGraph g;

public TMMemoryUsageTOSC(String base, int initcounter){
    initialCounter = initcounter;
    baseID = base;
    g = new TMGraph("TEST");
    TMTagger tagger = (TMTagger)g.getGraph().getTagger();
    tagger.setCounter(initialCounter);
}

// Fill the graph with the triples of path, 
// generating triples all inserted by the specified ID
void fillBase(String path, String ID) throws IOException{

        TMGraph g2 = new TMGraph(ID);
        TMTagger tagger = (TMTagger)g2.getGraph().getTagger();
        tagger.setCounter(initialCounter);
        // The load bypasses the listening
        //g2.load(path);
        // This way is heavy, a quicker one would be to implement a streaming
        // load of pentas.
        String insert = "INSERT DATA { ";
        String load = new String(Files.readAllBytes(Paths.get(path)));
        insert += load;
        insert += " }";
        try {
            g2.query(insert);
        } catch (EngineException ex) {
            Logger.getLogger(TMMemoryUsageTOSC.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error("Error Inserting, check the input file format");
        }
        //System.out.println("Triples loaded auxiliary graph:" + g2.size());
        g.applyEffect(g2.getLastOperation());
}
    
public void measureMemory(String path, long numTimes, Mode mode) throws IOException, EngineException{

     /* emulates the reception numTimes times of a triple concurrently inserted at 
      * participants {baseID0-baseIDj} each through only one path
     */ 
    if(mode.equals(Mode.CONCURRENCE)){
           File f = new File(path);
           LineIterator lineit = FileUtils.lineIterator(f,"UTF-8");
           HashMap<Token,Long> poly = new HashMap<>();
           for(long j = 0; j< numTimes ; j++){
               //poly.put(new Token(baseID+j+"#"+(initialCounter+i)), Long.parseLong("1"));
               poly.put(new Token(baseID+j), Long.parseLong("1"));
           }
           TrioMonoid tm = new TrioMonoid(poly);
           while(lineit.hasNext()){
               String line = lineit.next();
               SimplePenta penta = new SimplePenta(line.replace(" .", " DEFAULT "+tm.toString()));
               g.addPenta(penta);
           }

      /* Emulates the reception by numTimes paths of a triple inserted at the
      * participant baseID without any concurrence
     */ 
    } else if(mode.equals(Mode.PATH)){
           File f = new File(path);
           LineIterator lineit = FileUtils.lineIterator(f,"UTF-8");
           while(lineit.hasNext()){
               String line = lineit.next();
               SimplePenta penta = new SimplePenta(line.replace(" .", " DEFAULT "+numTimes+"*"+baseID));
               g.addPenta(penta);
           }
            
        // this is not good, I think is better to look the topology
        // measures
    } else if(mode.equals(Mode.BOTH)){
        /*
        for(int i = 0; i< numTimes ; i++){
            fillBase(path,baseID);
        }
        for(int i = 0; i< numTimes ; i++){
            fillBase(path,RandomStringUtils.randomAlphabetic(sizeID));
        }
        */ 
    
    }
    //TODO: Human readable switch
    //System.out.println("# Triples loaded :" + g.size());
    //System.out.println("TOSC SizeOf TMGraph: " + ObjectSizeCalculator.getObjectSize(g)/(1024.0*1024.0) + "Mb");

    //System.out.println(numTimes+" "+ObjectSizeCalculator.getObjectSize(g));
    System.out.println(numTimes+" "+ObjectSizeCalculator.getObjectSize(g)/(1024.0*1024.0));
    //System.out.println(g.query("SELECT ?tag WHERE {tuple(?p ?s ?o ?tag)} LIMIT 5"));

    
}
}
 
