/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphs;

import fr.inria.edelweiss.kgraph.api.Tagger;

/**
 *
 * @author ibanez-l
 */
public class TMTagger implements Tagger{

  String id;
  int counter;

  public TMTagger(String ident){
	id = ident;
    counter = 0;
  }

  @Override
  public String tag() {
    counter += 1;
	return "1*"+id+"#"+counter;
    
  }
  
	} 
