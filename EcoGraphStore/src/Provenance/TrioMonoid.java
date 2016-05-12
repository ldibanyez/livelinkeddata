/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Provenance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author luisdanielibanesgonzalez
 *  The Trio Communitative Monoid with inverse (K,+,-,0)
 *  I.E. the quotient monoid of polynomials
 */
public class TrioMonoid {

    // Token to coefficient
    HashMap<Token,Long> polynome;

    // Construct from String representation
    // ToDO: Check input and raise error
    public TrioMonoid(String in){
        this.polynome = new HashMap<>();
        if(in.equals("")) {return;}
        String[] strpoly = in.split("\\+");
        for(String t : strpoly ){
            String [] term = t.split("\\*");
            Long coefficient = new Long(term[0]);
            Token tok = new Token(term[1]);
            this.polynome.put(tok,coefficient);
        }
    }

    public TrioMonoid(Map<Token,Long> poly){
        polynome = new HashMap<>(poly);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + Objects.hashCode(this.polynome);
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
        final TrioMonoid other = (TrioMonoid) obj;
        if (!Objects.equals(this.polynome, other.polynome)) {
            return false;
        }
        return true;
    }

    // Normal plus that keeps the negative coefficients
    // zeros are cancelled though
    public void plus(TrioMonoid tm){
        
        for(Token t : tm.polynome.keySet()){
            if(polynome.containsKey(t)){
                Long myCoeff = polynome.get(t);
                Long hisCoeff = tm.polynome.get(t);
                polynome.put(t, myCoeff + hisCoeff);
            }else {
                polynome.put(t, tm.polynome.get(t));
            }
            if(polynome.get(t) == 0){
                polynome.remove(t);
            }
        }
    }
    
    // The plus that truncates (set semantics)
    public void truncPlus(TrioMonoid tm){
        this.plus(tm);
        for(Token t : tm.polynome.keySet()){
            if(polynome.containsKey(t)){
                if(polynome.get(t) < 1){
                    polynome.remove(t);
                }
            }
        }
    }

    // turn into the inverse 
    public void invert(){

        for(Token t : polynome.keySet()){
            Long currVal = polynome.get(t);
            polynome.put(t, -1*currVal);
        }
        
    }

    public boolean isNegative(){
        for(Long coeff : polynome.values()){
            if(coeff > 0){
                return false;
            }
        }
        return true;

    }

    public boolean isZero(){
        return polynome.isEmpty();
    }

    // String representation to store in Corese
    @Override
    public String toString(){
        String res = "";
        for(Token tok: polynome.keySet()){
         res += polynome.get(tok)+"*"+tok.getToken() + "+";
        }
        //res = res.replaceFirst("\\s\\+\\s$", "");
        res = res.replaceFirst("\\+$", "");
        return res;
    
    }

    public int numTerms(){
        return this.polynome.size();
    }

    public Set<Token> getTokens(){
        return new HashSet<>(polynome.keySet());
    
    }

    public List<Long> getCoeffs(){
        return new ArrayList<>(polynome.values());
    
    }

    /*
     * Note: This comparator is inconsistent with equals
     */
    public static Comparator<TrioMonoid> numTermsComparator 
          = new Comparator<TrioMonoid>() {

        @Override
        public int compare(TrioMonoid o1, TrioMonoid o2) {
            int s1 = o1.numTerms();
            int s2 = o2.numTerms();
            if(s1 == s2){
                return 0;
            }else if(s1 < s2){
                return -1;
            }else{
                return 1;
            }
        }
          
          };
    
    /*
     * Note: This comparator is inconsistent with equals
     */
    public static Comparator<TrioMonoid> coeffComparator 
          = new Comparator<TrioMonoid>() {

        @Override
        public int compare(TrioMonoid o1, TrioMonoid o2) {
            Long c1 = Collections.max(o1.getCoeffs());
            Long c2 = Collections.max(o2.getCoeffs());
            return c1.compareTo(c2);
        }
          
          };
}
