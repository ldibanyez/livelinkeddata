/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Publishers;

import Operations.Operation;

/**
 *  A publisher handles the operations coming from a graph
 *  Classify them, order them, filter them, broadcast...
 * @author luisdanielibanesgonzalez
 */
public interface Publisher {
    
    public void handle(Operation op);
}
