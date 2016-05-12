/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphs;

import fr.inria.edelweiss.kgram.api.core.Entity;
import fr.inria.edelweiss.kgram.core.Mappings;
import fr.inria.edelweiss.kgram.core.Query;
import fr.inria.edelweiss.kgraph.api.GraphListener;
import fr.inria.edelweiss.kgraph.core.Graph;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author luisdanielibanesgonzalez
 * This listener makes available the last Deleted/Inserted triples of the graph.
 */
public class OperationListener implements GraphListener {

    Graph graph;
    List<Entity> lastInserted;
    List<Entity> lastDeleted;
    //TODO: Something to turn off listening to be able to process
    // incoming operations.
    
    
    public OperationListener(){
        lastInserted = new ArrayList<>();
        lastDeleted = new ArrayList<>();
    }

    public List<Entity> getLastInserted(){
        return new ArrayList<>(lastInserted);
        
    }

    public List<Entity> getLastDeleted(){
        return new ArrayList<>(lastDeleted);
    }

    @Override
    public void addSource(Graph g) {
        graph = g;
    }

    @Override
    public boolean onInsert(Graph g, Entity ent) {
        return true;
    }

    @Override
    public void insert(Graph g, Entity ent) {
        lastInserted.add(ent);
    }

    @Override
    public void delete(Graph g, Entity ent) {
        lastDeleted.add(ent);
    }

    @Override
    public void start(Graph g, Query q) {
        if(!q.isUpdate()){return;}
        lastInserted.clear();
        lastDeleted.clear();
    }


    //@Override
    public void finish(Graph graph, Query query, Mappings mpngs) {
        /*
        ArrayList<SimpleQuad> ins = new ArrayList<>();
        for(Entity ent : lastInserted){
            ins.add(new SimpleQuad(ent));
        }
        ArrayList<SimplePenta> del = new ArrayList<>();
        for(Entity ent : lastDeleted){
            del.add(new SimplePenta(ent));
        }
        
        graph.setLastOp(new IVOperation(ins,del));
        * */
    }

    //@Override
    public void load(String string) {
    }

    // This method dissapeared in 3.1.1
    //@Override
    public void finish(Graph graph, Query query) {
    }
    
}
