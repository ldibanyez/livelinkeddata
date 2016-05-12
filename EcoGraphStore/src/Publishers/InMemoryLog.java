/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Publishers;

import Operations.Operation;
import Operations.TMOperation;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class InMemoryLog implements Publisher{

    private ArrayList<TMOperation> log;

    public InMemoryLog() {
        this.log = new ArrayList<>();
    }

    @Override
    public void handle(Operation op) {
        log.add((TMOperation)op);
    }

    public List<TMOperation> getFrom(int from){
    
        return log.subList(from, log.size());
    }

    public void reset(){
        log.clear();
    }
}
