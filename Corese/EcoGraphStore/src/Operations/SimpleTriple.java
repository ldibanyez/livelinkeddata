/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Operations;

import fr.inria.edelweiss.kgram.api.core.Entity;
import java.util.Objects;

/**
 *
 * @author ibanez-l
 */
public class SimpleTriple {
  
	final String subject;
	final String object;
	final String predicate;
  
	public SimpleTriple(String s, String p, String o){
	 subject = s;
	 object = o;
	 predicate = p;
	}

    public SimpleTriple(Entity ent){
    
        subject = ent.getNode(0).getLabel();
        predicate = ent.getEdge().getEdgeNode().getLabel() ;
        object = ent.getNode(1).getLabel();
    }

	public String getSubject(){ return subject;}
	public String getObject(){ return object;}
	public String getPredicate(){ return predicate;}

    public boolean sameAs(Entity ent){
       return this.equals(new SimpleTriple(ent)); 
    }

    // Returns the triple in the tuple format
    public String toTupleFormat(){
        if(IRIMatcher.isIRI(object)){
        return "<"+predicate+"> <"+subject+"> <"+object+">";
        }else{
        return "<"+predicate+"> <"+subject+"> '"+object+"'";
        }
    
    }

    @Override
    public String toString(){
        if(IRIMatcher.isIRI(object)){
        return "<"+subject+"> <"+predicate+"> <"+object+">";
        }else{
        return "<"+subject+"> <"+predicate+"> '"+object+"'";
        
        }
    
    }

  @Override
  public int hashCode() {
	int hash = 5;
	hash = 71 * hash + Objects.hashCode(this.subject);
	hash = 71 * hash + Objects.hashCode(this.object);
	hash = 71 * hash + Objects.hashCode(this.predicate);
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
	final SimpleTriple other = (SimpleTriple) obj;
	if (!Objects.equals(this.subject, other.subject)) {
	  return false;
	}
	if (!Objects.equals(this.object, other.object)) {
	  return false;
	}
	if (!Objects.equals(this.predicate, other.predicate)) {
	  return false;
	}
	return true;
  }



  }
  
