/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Vectors;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author ibanez-l
 */
public class Interval implements Comparable<Interval>{

    private int lowerLimit;
    private int upperLimit;


public Interval(){
  
    lowerLimit = 0;
    upperLimit = 0;

}
   
public Interval(int l, int h) throws IntervalException{
  
    if (h<l) {throw new IntervalException("Lower Limit greater than Upper Limit!");}
    lowerLimit = l;
    upperLimit = h;

}


public boolean contains(int n){

    return lowerLimit <= n && n <= upperLimit; 	

}


public void setLowerLimit(int n) throws IntervalException{ 

  if (upperLimit<n) 
  {throw new IntervalException("Lower Limit greater than Upper Limit!");}
  lowerLimit = n;
}

public void setUpperLimit(int n) throws IntervalException{ 

  if (lowerLimit>n) 
  {throw new IntervalException("Upper limit less than Lower Limit!");}
  upperLimit = n;
}

public boolean overlaps(Interval i){
   // We assume valid intervals
   return (this.contains(i.lowerLimit) || this.contains(i.upperLimit))
		   || (i.contains(this.lowerLimit) || i.contains(this.upperLimit));
}

  @Override
  public int compareTo(Interval t) {
      if(this.equals(t)){
	return 0;
      }
      if(this.lowerLimit < t.lowerLimit){
	 return -1; 
      } else if (this.lowerLimit > t.lowerLimit) {
	 return 1; 
      } else {
	if(this.upperLimit < t.upperLimit){
	  return -1;
	}  else {return 1;}
      }
  }

  public int getLowerLimit() {
	return lowerLimit;
  }

  public int getUpperLimit() {
	return upperLimit;
  }

  // Immediate successor
  public boolean isUpperSuccessor(Interval inter){
	return (this.lowerLimit == inter.lowerLimit) && 
			(this.upperLimit == inter.upperLimit +1);
  
  }

  public boolean isLowerSuccessor(Interval inter){
	return (this.lowerLimit == inter.lowerLimit + 1) && 
			(this.upperLimit == inter.upperLimit);
  
  }

  public boolean isSuccesor(Interval inter){
  	Set<Integer> thisUnpack = this.unpack(); 
  	Set<Integer> interUnpack = inter.unpack(); 
	interUnpack.removeAll(thisUnpack);
	return interUnpack.size() == 1;
  }

  public Set<Integer> unpack(){
	HashSet<Integer> s = new HashSet<>();
	for(int i = lowerLimit; i<= upperLimit; i++){
		s.add(new Integer(i));
	}
	return s;
  }

public class IntervalException extends Exception{

  public IntervalException() { super(); }
  public IntervalException(String message) { super(message); }
  public IntervalException(String message, Throwable cause) { super(message, cause); }
  public IntervalException(Throwable cause) { super(cause); }
  
  }

  @Override
  public int hashCode() {
	int hash = 7;
	hash = 73 * hash + this.lowerLimit;
	hash = 73 * hash + this.upperLimit;
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
	final Interval other = (Interval) obj;
	if (this.lowerLimit != other.lowerLimit) {
	  return false;
	}
	if (this.upperLimit != other.upperLimit) {
	  return false;
	}
	return true;
  }

  @Override
  public String toString() {
	return "(" + lowerLimit + ","  + upperLimit + ")";
  }

}

