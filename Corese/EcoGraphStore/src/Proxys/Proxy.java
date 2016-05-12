/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Proxys;

import Operations.Operation;
import java.util.List;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public interface Proxy {

    public List<Operation> nextOps();

    public boolean hasNewOps();

    public int numNewOps();
    
}
