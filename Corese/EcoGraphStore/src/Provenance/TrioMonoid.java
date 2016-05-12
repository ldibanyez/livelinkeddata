/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Provenance;

import java.util.HashMap;
import java.util.Objects;

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
        String[] strpoly = in.split(" \\+ ");
        for(String t : strpoly ){
            String [] term = t.split("\\*");
            Long coefficient = new Long(term[0]);
            Token tok = new Token(term[1]);
            this.polynome.put(tok,coefficient);
        }
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

    public boolean isZero(){
        return polynome.isEmpty();
    }

    // String representation to store in Corese
    @Override
    public String toString(){
        String res = "";
        for(Token tok: polynome.keySet()){
         res += polynome.get(tok)+"*"+tok.getToken() + " + ";
        }
        res = res.replaceFirst("\\s\\+\\s$", "");
        return res;
    
    }
    
}
