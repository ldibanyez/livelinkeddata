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
public class SimpleQuad extends SimpleTriple{

  final String graph;
  
  public SimpleQuad(String g ,String subject, String predicate, String object){
  	super(subject,predicate,object);
    if(IRIMatcher.isIRI(g)){
        graph = g;
    
    }else{
        if(g.equals("DEFAULT")){
            graph = "http://ns.inria.fr/edelweiss/2010/kgram/default"; 
        } else{
            throw new Error("Invalid IRI for Graph :"+ g);
        }
    }
  }

  public SimpleQuad(Entity ent){
    super(ent);
    graph = ent.getGraph().getLabel();
  }


  public String getGraph() {
	String t = "";
	return t.concat(graph);
  }

  public String printTriple(){
    return super.toString();
  }

  @Override
  public String toString(){
    if(graph.equals("kg:default")){
        return "DEFAULT "+ super.toString();
    }else{
        return "<"+graph +"> "+ super.toString();
    }
  }

    @Override
  public boolean sameAs(Entity ent){
        return this.equals(new SimpleQuad(ent)); 
  }
  @Override
  public int hashCode() {
	int hash = 5 * super.hashCode();
	hash = 41 * hash + Objects.hashCode(this.graph);
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
	final SimpleQuad other = (SimpleQuad) obj;
	if (!Objects.equals(this.graph, other.graph)) {
	  return false;
	}
	return super.equals(obj);
  }

  
}
