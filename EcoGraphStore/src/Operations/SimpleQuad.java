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

  private String graph;

  public SimpleQuad(){}
  
  public SimpleQuad(String g ,String subject, String predicate, String object){
  	super(subject,predicate,object);
    String stripgraph = g.replace("<", "").replace(">", "");
    if(IRIMatcher.isIRI(stripgraph)){
        graph = stripgraph;
    
    }else{
        if(g.equals("DEFAULT")){
            graph = "http://ns.inria.fr/edelweiss/2010/kgram/default"; 
        } else{
            throw new IllegalArgumentException("Invalid IRI for Graph :"+ stripgraph);
        }
    }
  }

  public SimpleQuad(Entity ent){
    super(ent);
    graph = ent.getGraph().getLabel();
  }


    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        String stripgraph = graph.replace("<", "").replace(">", "");
        if(IRIMatcher.isIRI(stripgraph)){
            this.graph = stripgraph;
        
        }else{
            if(graph.equals("DEFAULT")){
                this.graph = "http://ns.inria.fr/edelweiss/2010/kgram/default"; 
            } else{
                throw new IllegalArgumentException("Invalid IRI for Graph :"+ stripgraph);
            }
        }
    }
  

  public String printTriple(){
    return super.toString();
  }

  /*
   * Follows NQuad format with the exception of an explicit DEFAULT keyword
   * instead of the empty string to represent the default graph
   * // TODO: The ending point
   */
  @Override
  public String toString(){
    if(graph.equals("kg:default") || graph.equalsIgnoreCase("http://ns.inria.fr/edelweiss/2010/kgram/default")){
        return super.toString() + " DEFAULT";
    }else{
        return super.toString() + " <"+graph+">";
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
