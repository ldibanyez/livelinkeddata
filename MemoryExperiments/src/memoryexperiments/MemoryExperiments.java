/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package memoryexperiments;

import fr.inria.acacia.corese.exceptions.EngineException;
import java.io.IOException;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class MemoryExperiments {

    /**
     * @param args the command line arguments
     * 0 : PG or TM
     * 1 : Path to dataset to load
     * 2 : Mode [C]oncurrency or [P]ath count
     * 3 : In [C] mode, number of concurrent insertions to measure, 
     *     in [P] mode, coefficient to put 
     * 4 : Base URI/ID to use for the pentas
     * 5 : Initial counter for the clock
     */
    public static void main(String[] args) throws IOException, EngineException {
        // TODO code application logic here
        switch (args[0]) {
            case "PG":
                PGMemoryUsageTOSC.measureMemory(args[1]);
                break;
            case "TM":
                TMMemoryUsageTOSC memmeter = new TMMemoryUsageTOSC(args[4],Integer.parseInt(args[5]));
                if(args[2].equals("C")){
                memmeter.measureMemory(args[1],Long.parseLong(args[3]),TMMemoryUsageTOSC.Mode.CONCURRENCE);
                } else if(args[2].equals("P")){
                memmeter.measureMemory(args[1],Long.parseLong(args[3]),TMMemoryUsageTOSC.Mode.PATH);
                }
                break;
            default:
                throw new Error("Bad Argument "+ args[0]);
        }
        
    }
}
