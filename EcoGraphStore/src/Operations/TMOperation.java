/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Operations;

import Graphs.TMGraph;
import com.google.gson.Gson;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.core.Mappings;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

/**
 *
 * @author luisdanielibanesgonzalez
 *  Operation annotated with a semiring, and with the chain of provenance
 */
public class TMOperation implements Operation{

    // Author of this operation
    // Timestamp of the last inserted triple
    // Premature optimization, I'm not sure we can do it
	//final int lasttick;
    //final List<SimpleQuad> toInsert;
    private List<SimplePenta> toInsert = new ArrayList<>();
    private List<SimplePenta> toDelete = new ArrayList<>();
    // The trace of the operation
    private List<String> trace = new ArrayList<>();

    public TMOperation(List<SimplePenta> ins, List<SimplePenta> del, List<String> trac){
        //this.lasttick = lt;
        this.toDelete = new ArrayList<>(del);
        this.toInsert = new ArrayList<>(ins);
        this.trace = new ArrayList<>(trac);
    }

    public TMOperation(String serialization, SerialType type){
        if(type.equals(SerialType.JSON)){
            Gson gson =  new Gson();
            TMOperation op = gson.fromJson(serialization, TMOperation.class);
            this.toDelete = new ArrayList<>(op.toDelete);
            this.toInsert = new ArrayList<>(op.toInsert);
            this.trace = new ArrayList<>(op.trace);
        
        } else{
            StringReader sr = new StringReader(serialization);
            try {
                LineIterator it = IOUtils.lineIterator(sr);
                String tr = it.nextLine();
                //TODO: Regular expression
                if(!tr.startsWith("TRACE")){
                    throw new IllegalArgumentException("Input lacks TRACE");
                }
                String[] tracesplit = tr.split(" ");
                for(int i = 1; i< tracesplit.length ; i++){
                    this.trace.add(tracesplit[i]);
                }

                tr = it.nextLine();
                if(!tr.equals("DELETE")){
                    throw new IllegalArgumentException("Input lacks DELETE");
                }
                while(it.hasNext()) {
                    String line = it.nextLine();
                    if(!line.equals("INSERT")){
                        this.toDelete.add(new SimplePenta(line));
                    } else {break;}
                 }
                while(it.hasNext()){
                    String line = it.nextLine();
                    this.toInsert.add(new SimplePenta(line));
                }
            } finally {
                IOUtils.closeQuietly(sr);
            } 
        
        }
    
    }

    public TMOperation(File f) throws FileNotFoundException{
    
        FileReader fr = new FileReader(f);
        try {
            LineIterator it = IOUtils.lineIterator(fr);
            String tr = it.nextLine();
            //TODO: Regular expression
            if(!tr.startsWith("TRACE")){
                throw new IllegalArgumentException("Input lacks TRACE");
            }
            String[] tracesplit = tr.split(" ");
            for(int i = 1; i< tracesplit.length ; i++){
                this.trace.add(tracesplit[i]);
            }

            tr = it.nextLine();
            if(!tr.equals("DELETE")){
                throw new IllegalArgumentException("Input lacks DELETE");
            }
            while(it.hasNext()) {
                String line = it.nextLine();
                if(!line.equals("INSERT")){
                    this.toDelete.add(new SimplePenta(line));
                } else {break;}
             }
            while(it.hasNext()){
                String line = it.nextLine();
                this.toInsert.add(new SimplePenta(line));
            }
        } finally {
            IOUtils.closeQuietly(fr);
        } 
    
    }

    public TMOperation(InputStream is) throws IOException{
    
        
        try (BufferedReader buf = new BufferedReader(new InputStreamReader(is))) 
        {
            String line = buf.readLine();
            //TODO: Regular expression
            if(!line.startsWith("TRACE")){
                throw new IllegalArgumentException("Input lacks TRACE");
            }
            String[] tracesplit = line.split(" ");
            for(int i = 1; i< tracesplit.length ; i++){
                this.trace.add(tracesplit[i]);
            }

            line = buf.readLine();
            if(!line.equals("DELETE")){
                throw new IllegalArgumentException("Input lacks DELETE");
            }
            line = buf.readLine();
            while(line != null) {
                if(!line.equals("INSERT")){
                    this.toDelete.add(new SimplePenta(line));
                } else {break;}
                line = buf.readLine();
             }
            line = buf.readLine();
            while(line != null){
                this.toInsert.add(new SimplePenta(line));
                line = buf.readLine();
            }
        }     
    }

    public void setTrace(List<String> t){
    
        //TODO, check that is really a trace
        this.trace = t;
    }

    //Deep Copy
    public TMOperation(TMOperation base){
        //this.lasttick = base.lasttick;
        this.toInsert = new ArrayList<>(base.toInsert);
        this.toDelete = new ArrayList<>(base.toDelete);
        this.trace = new ArrayList<>(base.trace);
    }

    
    // Put the stamp of passed by store ID in this operation
    public void stamp(String id){
        trace.add(id);
    }

    public List<String> getTrace(){
        return trace;
    }

    @Override
	public List<SimplePenta> getInsert(){
        List pentas = new ArrayList<>(); 
            //System.out.println(lasttick);
        for (int i = 0 ; i< toInsert.size() ; i++){
            SimplePenta st = toInsert.get(i);
            SimplePenta sq = new SimplePenta(st.getGraph(),st.getSubject(),st.
                getPredicate(),st.getObject(),
                // String representation of a monomial
               st.getTag());
            pentas.add(sq);
        }
		return pentas; 
	}

    @Override
    public List<SimplePenta> getDelete() {
        List pentas = new ArrayList<>(); 
        for (int i = 0 ; i< toDelete.size() ; i++){
            SimplePenta st = toDelete.get(i);
            SimplePenta sq = new SimplePenta(st.getGraph(),st.getSubject(),st.
                getPredicate(),st.getObject(),
                // String representation of a monomial
               st.getTag());
            pentas.add(sq);
        }
		return pentas; 
    }

    @Override
    public String toString(){

        return this.toNPenta();
    }

    /*
     * TODO N-Quads like serialization and then ttl and RDF/XML
     */
    @Override
    public String serialize() {
        // shameless JSON
        return toNPenta();
    }

    public String toJSON(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public TMOperation subOperation(String GraphPattern) throws EngineException{
    
        ArrayList<String> subTrace = new ArrayList<>(this.trace);

        ArrayList<SimplePenta> subDelete = new ArrayList<>();
        TMGraph g = new TMGraph("subChecker");
        for(SimplePenta p : this.toDelete){
            g.addPenta(p);
            Mappings map = g.query("select * where {" + GraphPattern +"}");
            if(map.size() > 0){
                subDelete.add(p);
            }
            g.delPenta(p);
            assert(g.size() == 0);
        }
        
        ArrayList<SimplePenta> subInsert = new ArrayList<>();
        for(SimplePenta p : this.toInsert){
            g.addPenta(p);
            Mappings map = g.query("select * where {" + GraphPattern +"}");
            if(map.size() > 0){
                subInsert.add(p);
            }
            g.delPenta(p);
            assert(g.size() == 0);
        }

        return new TMOperation(subInsert,subDelete,subTrace);
    
    }

    
    // With Named Graph
    // TODO: Write to a Stream
    // TODO: Write the NPenta and the operation spec
    public String toNPenta(){
       StringBuilder npentas = new StringBuilder("TRACE ");
       // The trace could be in json?
       for(String s : this.trace){
            npentas.append(s).append(" ");                     
       }
       npentas.append('\n');
       npentas.append("DELETE\n");
       for(SimplePenta toDel : this.toDelete){
            npentas.append(toDel).append('\n');                     
       }
       npentas.append("INSERT\n");
       for(SimplePenta toIns : this.toInsert){
            npentas.append(toIns).append('\n');                     
       }
       //npentas.deleteCharAt(npentas.length()-1);
       return npentas.toString();
    }

    /*
     * Write NPenta representation of the operation to a File
     */
    public void toNPentaFile(File f) throws FileNotFoundException, IOException{
         FileOutputStream outfile = new FileOutputStream(f); 
         try{
             IOUtils.write("TRACE ", outfile);
             for(String s : this.trace){
                IOUtils.write(s + " ", outfile); 
             }
             IOUtils.write("\n",outfile);
             IOUtils.write("DELETE\n",outfile);
             IOUtils.writeLines(this.toDelete, "\n", outfile);
             IOUtils.write("INSERT\n",outfile);
             IOUtils.writeLines(this.toInsert, "\n", outfile);
         } finally{
            IOUtils.closeQuietly(outfile);
         }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TMOperation other = (TMOperation) obj;
        if (!Objects.equals(this.toInsert, other.toInsert)) {
            return false;
        }
        if (!Objects.equals(this.toDelete, other.toDelete)) {
            return false;
        }
        if (!Objects.equals(this.trace, other.trace)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.toInsert);
        hash = 79 * hash + Objects.hashCode(this.toDelete);
        hash = 79 * hash + Objects.hashCode(this.trace);
        return hash;
    }

    
}
