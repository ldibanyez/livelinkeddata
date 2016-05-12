/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Proxys;

import Graphs.TMGraph;
import Operations.Operation;
import Operations.TMOperation;
import Publishers.FilePublisher;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class TMProxy implements Proxy {

    ArrayList<FilePublisher> following = new ArrayList<>();
    ArrayList<Integer> lastSeen = new ArrayList<>();
    ArrayList<Set<String>> views = new ArrayList<>();


    public void addFollowing(FilePublisher g, Set<String> v){
        following.add(g);
        lastSeen.add(Integer.valueOf(0));
        views.add(v);
        
    }    

    
    @Override
    public List<Operation> nextOps() {
        ArrayList<Operation> ops = new ArrayList<>();

        for(int i = 0 ; i < following.size() ; i++){
            FilePublisher tml = following.get(i);
            Integer ls = lastSeen.get(i);
            Set<String> v = views.get(i);
            //ops.addAll(tml.getConcernedOperations(ls, views.get(i)));
            //lastSeen.set(i, tml.lastOp());
        }

        return ops;
    }

    @Override
    public boolean hasNewOps() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int numNewOps() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
