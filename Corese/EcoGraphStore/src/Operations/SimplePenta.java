/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Operations;

import fr.inria.edelweiss.kgram.api.core.Entity;
import java.util.Objects;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class SimplePenta extends SimpleQuad {
    
  final String tag;
    
  public SimplePenta(String g, String subject, String predicate, String object, String t){
  	super(g,subject,predicate,object);
	tag = t; 
  }

  public SimplePenta(Entity ent){
    super(ent);
    this.tag = ent.getNode(2).getLabel();
  }

    @Override
  public boolean sameAs(Entity ent){
    return this.equals(new SimplePenta(ent));
  }

  public String getTag() {
	String t = "";
	return t.concat(tag);
  }

  @Override
  public String toString(){
    
      return super.toString() + " "+ this.tag;
  }

  public String printQuad(){
    return super.toString();
  }

  @Override
  public int hashCode() {
	int hash = 5 * super.hashCode();
	hash = 41 * hash + Objects.hashCode(this.tag);
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
	final SimplePenta other = (SimplePenta) obj;
	if (!Objects.equals(this.tag, other.tag)) {
	  return false;
	}
	return super.equals(obj);
  }
  
}
