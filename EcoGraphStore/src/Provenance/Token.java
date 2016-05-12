/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Provenance;

import java.util.Objects;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class Token {

    String token;

    public Token(String tk){
        token = tk;
    }

    public String getToken(){
        return token;
    }

    @Override
    public String toString(){
        return token;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.token);
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
        final Token other = (Token) obj;
        if (!Objects.equals(this.token, other.token)) {
            return false;
        }
        return true;
    }
    
}
