/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Operations;

import java.util.List;

/**
 * Interface to represent operations:
 * Maybe not so good idea, For a new imp
 * I still need to implement a triple Proxy
 * @author luisdanielibanesgonzalez
 */
public interface Operation {
    
    // Quads must be returned to insert
    public List<SimplePenta> getInsert();
    
    public List<SimplePenta> getDelete();

    // This should  implements serializable, maybe someday...
    public String serialize();

    // Promess to implement as static.
    //public Operation deserialize(String serialization); 
    
}
