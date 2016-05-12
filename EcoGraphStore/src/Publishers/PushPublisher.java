/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Publishers;

import Operations.Operation;
import Operations.TMOperation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Pushes operations to a list of followers
 * @author luisdanielibanesgonzalez
 */
public class PushPublisher implements Publisher {

    HashMap<String,ArrayList<String>> followers =new HashMap<>();
    ArrayList<Operation> log = new ArrayList<>();
    int windowSize = 1;

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public void addFollower(String url){
        
        followers.put(url, new ArrayList<String>());
    }

    public ArrayList<String> getViews(String follower){
        return followers.get(follower);
    }

    public void setViews(String follower, ArrayList<String> views){
    
        followers.put(follower, views);
    }
    

    @Override
    public void handle(Operation op) {
        log.add(op);
        if(log.size() == windowSize){
            pushToAll();
        }
    }

    public void pushToAll(){

        for(String foll : followers.keySet()){
            for(String view : followers.get(foll) ){
            
            
            }
        }
    
    }

}
