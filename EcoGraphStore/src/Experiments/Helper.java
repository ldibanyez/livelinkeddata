/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Experiments;

import Graphs.TMGraph;
import Publishers.FilePublisher;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import fr.inria.edelweiss.kgtool.load.Load;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class Helper {
    
public static Graph reloadCountry(String country) throws FileNotFoundException, IOException{
  		
    Properties config = new Properties();
    config.load(new FileInputStream("./config.properties"));
	String path = config.getProperty("path");

    Graph g = Graph.create(); 
    g.init();
    Load ld = Load.create(g);
    ld.load(path +"Object"+country+".ttl");

    QueryProcess exec = QueryProcess.create(g);
    String move = "MOVE <file://"+path+"Object"+country+".ttl> TO <kg:default>";
        try {
            exec.query(move);
        } catch (EngineException ex) {
            Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
        }
    return g;

}
public static TMGraph reloadCountry(String country, int salt) throws FileNotFoundException, IOException{
    Properties config = new Properties();
    config.load(new FileInputStream("./config.properties"));
	String path = config.getProperty("path");

    TMGraph g = new TMGraph("http://thisisamockedURLtoidentifythesource/"+country+"-"+salt); 
    g.load(path +"Object"+country+".ttl");

    String move = "MOVE <file://"+path+"Object"+country+".ttl> TO <kg:default>";
        try {
            g.query(move);
        } catch (EngineException ex) {
            Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
        }


    //TMLogPublisher pub = new FilePublisher();
    //g.setPublisher(pub);
    return g; 

}
    
}
