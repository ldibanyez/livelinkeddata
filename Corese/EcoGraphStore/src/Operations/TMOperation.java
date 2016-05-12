/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Operations;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author luisdanielibanesgonzalez
 *  Operation annotated with a semiring, and with the chain of provenance
 */
public class TMOperation implements Operation{

    // Author of this operation
    final String id;
    // Timestamp of the last inserted triple
    // Premature optimization, I'm not sure we can do it
	//final int lasttick;
    //final List<SimpleQuad> toInsert;
    final List<SimplePenta> toInsert;
    final List<SimplePenta> toDelete;
    // The trace of the operation
    List<String> trace;

    public TMOperation(String ide, List<SimplePenta> ins, List<SimplePenta> del){
        this.id = ide;
        //this.lasttick = lt;
        this.toInsert = ins;
        this.toDelete = del;
        trace = new ArrayList();
        trace.add(this.id);
    }

    public TMOperation(String serialization){
        Gson gson =  new Gson();
        TMOperation op = gson.fromJson(serialization, TMOperation.class);
        this.id = op.id;
        this.toDelete = op.toDelete;
        this.toInsert = op.toInsert;
        this.trace = op.trace;
        
    }

    public String getId() {
        return id;
    }
    

    public void setTrace(List<String> t){
    
        //TODO, check that is really a trace
        this.trace = t;
    }

    //Deep Copy
    public TMOperation(TMOperation base){
        this.id = new String(base.id);
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

        String str = "";
        str += "To Insert: \n";
        for (SimplePenta p : this.getInsert()){
            str += p.toString() + "\n ";
        }
        str += "To Delete: \n";
        for (SimplePenta p : this.getDelete()){
            str += p.toString() + "\n ";
        }

        return str;
    }

    /*
     * TODO N-Quads like serialization and then ttl and RDF/XML
     */
    @Override
    public String serialize() {
        // shameless JSON
        Gson gson = new Gson();
        return gson.toJson(this);
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
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
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
        hash = 79 * hash + Objects.hashCode(this.id);
        hash = 79 * hash + Objects.hashCode(this.toInsert);
        hash = 79 * hash + Objects.hashCode(this.toDelete);
        hash = 79 * hash + Objects.hashCode(this.trace);
        return hash;
    }

    
}
