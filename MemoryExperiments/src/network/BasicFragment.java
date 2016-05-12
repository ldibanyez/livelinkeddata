/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import Operations.SimplePenta;
import Operations.SimpleTriple;
import Operations.TMOperation;
import com.sun.jersey.api.client.WebResource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class BasicFragment {

    private String subject;
    private String predicate;
    private String object;
    //TODO add Graph

    public BasicFragment(){
        subject = "?subject";
        predicate = "?predicate";
        object = "?object";
    }

    public BasicFragment(String s, String p, String o){
        //Todo Validation
        subject = s;
        predicate = p;
        object = o;
    }

    public boolean isStar(){
        return this.subject.equals("?subject") &&
                this.predicate.equals("?predicate") &&
                this.object.equals("?object");
    }

    String getTagQuery(){
    
        String query = "SELECT ?predicate ?subject ?object ?tag WHERE "
                + "{"
                + "tuple("
                + this.predicate +" "
                + this.subject + " "
                + this.object + " "
                + "?tag)"
                + "}";
        return query;
    
    }

    /*
     * Returns a subOperation with only triples that concern this pattern
     */
    public TMOperation concernedSubOp(TMOperation in){
       if (this.isStar()){ return in;}
       
       List<SimplePenta> filterIns = new ArrayList<>();
       List<SimplePenta> filterDel = new ArrayList<>();
       for(SimplePenta penta : in.getDelete()){
            if(this.matches(penta)){
                filterDel.add(penta);
            }
       }
       for(SimplePenta penta : in.getInsert()){
            if(this.matches(penta)){
                filterIns.add(penta);
            }
       }

       return new TMOperation(filterIns,filterDel,in.getTrace());
    
    }

    public boolean matches(SimpleTriple triple){
        boolean subjMatch = subject.equals("?subject") 
                || triple.getQuotedSubject().equals(subject);
        boolean predMatch = predicate.equals("?predicate") 
                || triple.getQuotedPredicate().equals(predicate);
        boolean objMatch = object.equals("?object") 
                || triple.getQuotedObject().equals(object);

        return subjMatch && predMatch && objMatch;
    
    }

    public TMOperation getFrom(Participant p){

        WebResource remoteSource = p.getService();
        try (InputStream input = remoteSource.path("sparql")
                  .queryParam("query", getTagQuery())
                  .accept("application/sparql-results+tsv")
                  .get(InputStream.class);
            BufferedReader buf = new BufferedReader(new InputStreamReader(input));)
        {
            // TODO: JAVA 7 scanner?
            String line = buf.readLine();
            assert(line.contains("tag"));
            List<SimplePenta> toIns = new ArrayList<>();
            StringBuilder insert = new StringBuilder("INSERT DATA {");
            line = buf.readLine();
            while(line != null){
                SimplePenta penta = new SimplePenta("DEFAULT","","","","");
                String[] sp = line.split(" ");
                //System.out.println("LINE "+ line + "length: "+sp.length);
                assert(sp.length == 4);
                insert.append("tuple(");
                if(sp[0].isEmpty()){
                    insert.append(predicate).append(" ");
                    penta.setPredicate(predicate);
                } else{
                    penta.setPredicate(sp[0].trim());
                    insert.append(sp[0]).append(" ");
                }
                if(sp[1].isEmpty()){
                    penta.setSubject(subject);
                    insert.append(subject).append(" ");
                } else{
                    penta.setSubject(sp[1].trim());
                    insert.append(sp[1]).append(" ");
                }
                if(sp[2].isEmpty()){
                    penta.setObject(object);
                    insert.append(object).append(" ");
                } else{
                    penta.setObject(sp[2].trim());
                    insert.append(sp[2]).append(" ");
                }
                // Tag should always be there
                assert(!sp[3].isEmpty());
                penta.setTag(sp[3]);
                insert.append(sp[3]).append(")\n");
                line = buf.readLine();
                toIns.add(penta);
            }
            insert.append("}");
            ArrayList<String> trace = new ArrayList<>();
            trace.add(p.getURI());
            //return insert.toString();
            return new TMOperation(toIns
                    ,new ArrayList<SimplePenta>()
                    ,trace);
          } catch (IOException ex) {
             // Logger.getLogger(TMGraphIncrementalDelete.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error("IO Problem, check log");
          }     
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        if(subject == null || subject.isEmpty()){
            this.subject = "?subject";
        }else{
            this.subject = subject;
        }
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        if(predicate == null || predicate.isEmpty()){
            this.predicate = "?predicate";
        }else{
            this.predicate = predicate;
        }
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        if(object == null || object.isEmpty()){
            this.object = "?object";
        }else{
            this.object = object;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BasicFragment other = (BasicFragment) obj;
        if (!Objects.equals(this.subject, other.subject)) {
            return false;
        }
        if (!Objects.equals(this.predicate, other.predicate)) {
            return false;
        }
        if (!Objects.equals(this.object, other.object)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString(){
    
        return "CONSTRUCT WHERE {"+subject+" "+predicate+" "+object+"}";
    
    }
}
