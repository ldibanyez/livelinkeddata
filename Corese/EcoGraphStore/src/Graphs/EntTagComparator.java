/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphs;

import fr.inria.edelweiss.kgram.api.core.Entity;
import java.util.Comparator;

/**
 *
 * @author luisdanielibanesgonzalez
 * Compare by the numeric tag.
 * Note that this is useless if the quads don't come from the same replica
 */
public class EntTagComparator implements Comparator<Entity> {
    
    @Override
    public int compare(Entity sq1 ,Entity sq2){
        String tag1 = sq1.getNode(2).getLabel();
        String[] arr1 = tag1.split("#");
        int n1 = Integer.parseInt(arr1[1]);
        String tag2 = sq2.getNode(2).getLabel();
        String[] arr2 = tag2.split("#");
        int n2 = Integer.parseInt(arr2[1]);

        if(n1 == n2) {return 0;}
        else if(n1 < n2) {return -1;}
        else {return 1;}
        
    } 
    
}
