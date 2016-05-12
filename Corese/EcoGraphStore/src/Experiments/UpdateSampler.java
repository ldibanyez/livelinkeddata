/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Experiments;

import Graphs.TMGraph;
import fr.inria.acacia.corese.api.IDatatype;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.core.Mapping;
import fr.inria.edelweiss.kgram.core.Mappings;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class UpdateSampler {

    Random rand = new Random();


    // Inserts in graph the given number of triples with the specified
    // predicate

    public void samplInsert(TMGraph graph, String predicate, int quantity){

        String namespace = "http://www.randomsamp.org/";
        String insert = "INSERT DATA {\n";
        for(int i = 0 ; i < quantity ; i++){
            insert += namespace+"OBJ-"+rand.nextInt(1000000)+ " " + predicate + " " 
                    + namespace+"SUBJ-"+rand.nextInt(1000000) +" .\n";
        }
        insert += "}";
        try {
            graph.query(insert);
        } catch (EngineException ex) {
            Logger.getLogger(UpdateSampler.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }

    // Inserts in graph the given number of triples with the specified
    // predicate

    public String samplInsertQuery(String predicate, int quantity){

        String namespace = "<http://www.randomsamp.org/>";
        String insert = "INSERT DATA {\n";
        for(int i = 0 ; i < quantity ; i++){
            insert += namespace+"OBJ-"+rand.nextInt(1000000)+ " " + predicate + " " 
                    + namespace+"SUBJ-"+rand.nextInt(1000000) +" .\n";
        }
        insert += "}";

        return insert;
    
    }
    // Deletes from graph the given number of triples.

    public void samplDelete(TMGraph graph, String predicate, double percentage){
        
        String count = "SELECT ?x "
                + "WHERE {?x "+predicate+" ?z}";
        try {
            int quantity = graph.query(count).nbSolutions(); 
            String select = "SELECT ?x ?z WHERE {"
                + "?x "+predicate+" ?z .}"
                + "LIMIT "+ ((int) Math.round(quantity*percentage));
            Mappings map = graph.query(select);
            String delete = "DELETE DATA {\n";
           for (Mapping m : map){
                IDatatype dtsub = (IDatatype) m.getValue("?x");
                IDatatype dtobj = (IDatatype) m.getValue("?z");
                delete += dtsub.toString() + " "+ predicate + " " + dtobj.toString()+".\n";
                } 
           delete += "}";
           graph.query(delete);
        } catch (EngineException ex) {
            Logger.getLogger(UpdateSampler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public String samplDeleteQuery(QueryProcess qp, String predicate, double percentage){
        String count = "SELECT ?x"
                + "WHERE {?x "+predicate+" ?z}";
        try {
            int quantity = qp.query(count).nbSolutions(); 
            String select = "SELECT ?x ?z WHERE {"
                + "?x "+predicate+" ?z .}"
                + "LIMIT "+ ((int) Math.round(quantity*percentage));
            Mappings map = qp.query(select);
            String delete = "DELETE DATA {\n";
           for (Mapping m : map){
                IDatatype dtsub = (IDatatype) m.getValue("?x");
                IDatatype dtobj = (IDatatype) m.getValue("?z");
                delete += dtsub.toString() + " "+ predicate + " " + dtobj.toString()+".\n";
                } 
           delete += "}";
           return delete;
        } catch (EngineException ex) {
            Logger.getLogger(UpdateSampler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    
    }
    
}
