/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Proxys;

import Operations.IVOperation;
import Operations.Operation;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple Proxy that reads from a monotonically increasing log
 * @author luisdanielibanesgonzalez
 */
public class LogProxy implements Proxy {

    String path;
    int lastRead;

    public LogProxy(String p){
        path = p;
        lastRead = 0;
    }

    @Override
    public List<Operation> nextOps() {
        List<Operation> ops = new ArrayList<>();
        try {
            LineNumberReader reader = new LineNumberReader(new FileReader(new File(path)));
            String line;
            while(reader.getLineNumber() < lastRead){
                line = reader.readLine();
            }
            while((line = reader.readLine()) != null){
                lastRead += 1;
                ops.add(IVOperation.deserialize(line));
            }
            reader.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(LogProxy.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex) {
            Logger.getLogger(LogProxy.class.getName()).log(Level.SEVERE, null, ex);
        } 

        return ops;
    }

    @Override
    public boolean hasNewOps() {
        return true;
    }

    @Override
    public int numNewOps() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
