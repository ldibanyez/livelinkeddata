/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TMMemoryLog;

import fr.inria.acacia.corese.api.IDatatype;
import fr.inria.edelweiss.kgram.api.core.Node;
import fr.inria.edelweiss.kgram.core.Mapping;
import fr.inria.edelweiss.kgram.core.Mappings;
import fr.inria.edelweiss.kgram.core.Query;
import fr.inria.edelweiss.kgtool.print.TSVFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class TSVFormatPrefix {
    
	static final String EOL = "\n";
	static final String SEP = " ";
	static final String QUOTE = "\"";

	static final String[] SPECIAL = {",", QUOTE, "\n"};

	Mappings lm;
	Query query;
	List<String> select;

	TSVFormatPrefix(Mappings m){
		lm = m;
		setQuery(m.getQuery());
	}
	
	public static TSVFormatPrefix create(Mappings m){
		return new TSVFormatPrefix(m);
	}
	
	String eol(){
		return EOL;
	}
	
	String sep(){
		return SEP;
	}
	
	void setQuery(Query q){
		query = q;
		select = new ArrayList<> ();
		for (Node node : q.getSelect()){
			select.add(node.getLabel());
		}
	}
	
    @Override
	public String toString(){
		StringBuilder str = new StringBuilder(variables() + eol());
		str.append(values());
		return str.toString();
	}
	
	String variables(){
		String str = "";
		Query q = lm.getQuery();
		boolean first = true;
		for (String var : select){
			if (first) {
				first = false;
			}
			else {
				str += sep();
			}
			str += getVariable(var);
		}
		return str;
	}
	
	String getVariable(String var){
		return var.substring(1);
	}
	
	StringBuilder values(){
		StringBuilder str = new StringBuilder("");
		
		for (Mapping map : lm){
			boolean first = true;
			
			for (String var : select){
				if (first) {
					first = false;
				}
				else {
					str.append(sep());
				}
				
				Node node = map.getNode(var);
				if (node != null){
					str.append(getLabel(node));
				}
			}
			
			str.append(eol());
		}
		
		return str;
		
	}
	//
    String getLabel(Node node){
		String label = node.getLabel();
        /*
		if (isSpecial(label)){
			label = escape(label);
			label = QUOTE + label + QUOTE;
		}
        * */
		return label;
	}

	/*
	String getLabel(Node node){

       		if (node.getValue() instanceof IDatatype){
			IDatatype dt = (IDatatype) node.getValue();
			if (dt.isNumber()){
				return dt.getLabel();
			}
			if (dt.getCode() == IDatatype.LITERAL && ! dt.hasLang()){
				// untyped plain literal
				return QUOTE + dt.getLabel() + QUOTE;
			}
			return dt.toSparql(false);
			//return dt.toSparql(true);
		}
		return node.toString();
		//return node.getLabel();

	}
    * */
	
	String escape(String str){
		if (str.contains(QUOTE)){
			int index = str.indexOf(QUOTE);
			str = str.substring(0, index) + QUOTE + QUOTE + escape(str.substring(index+1));
		}
		return str;
	}
	
	boolean isSpecial(String str){
		for (String pat : SPECIAL){
			if (str.contains(pat)){
				return true;
			}
		}
		return false;
	}
	

}

    
