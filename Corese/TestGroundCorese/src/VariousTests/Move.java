/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VariousTests;

import fr.inria.edelweiss.kgram.api.core.Entity;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import fr.inria.edelweiss.kgtool.load.Load;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class Move {

    public static void main(String [] args){

    Graph g = Graph.create();
    Load ld = Load.create(g);
    
    ld.load("/Users/luisdanielibanesgonzalez"
            + "/Documents/live-linked-data/papers/TechReports/TMGraphExp/datasets/ObjectFrance.ttl");
    QueryProcess exec = QueryProcess.create(g);
    String move = "MOVE <file:///Users/luisdanielibanesgonzalez/Documents/live-linked-data/papers/TechReports/TMGraphExp/datasets/ObjectFrance.ttl> TO DEFAULT";
        try {
            for(Entity ent: g.getEdges()){
                System.out.println(ent);
                System.out.println(ent.getGraph().toString());
                break;
            }
            exec.query(move);
            for(Entity ent: g.getEdges()){
                System.out.println(ent);
                System.out.println(ent.getGraph().toString());
                break;
            }
        } catch (Exception ex) {
        }

    }
}
