/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphs;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class TMIndexTagger extends TMTagger{

    BiMap<String,String> uriToindex;
    BiMap<String,String> indexTouri;
    int index;

    public TMIndexTagger(String ident){
        super(ident);
        uriToindex = HashBiMap.create();
        indexTouri = uriToindex.inverse();
        uriToindex.put(id, "$0");
        index = 1;
    }

    /*
     * Sets the $0 index, which corresponds to 
     */
    public boolean putIndex(String uri){
        if(uriToindex.containsKey(uri)){
            return false;
        }else{
            uriToindex.put(uri, "$"+index);
            index+=1;
            return true;
        }
    }

    public String getIndexValue(String uri){
        return uriToindex.get(uri);
    }

    public String getURI(String index){
        return indexTouri.get(index);
    }

    /*
     * Tag with $0, the convention for the id of this tagger (thus, id of the graph)
     */
    @Override
    public String tag(){
        counter += 1;
	    return "1*$0#"+counter;
    }
    
}
