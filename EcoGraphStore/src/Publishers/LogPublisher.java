/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Publishers;

import Operations.Operation;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple log, the others will read.
 * @author luisdanielibanesgonzalez
 */
public class LogPublisher implements Publisher {
    List<Operation> log;
    String path;

    public LogPublisher(String p){
        log = new ArrayList<>();
        path = p;
    }

    @Override
    public void handle(Operations.Operation op) {
        log.add(op);
    }

    public void flush(){
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, true)))) {
            for(Operation op : log){
               out.println(op.serialize());
            }
        } catch(IOException ex){
            throw new Error("IO Error: "+ ex.getMessage());
        }
        log.clear();
    }
}
