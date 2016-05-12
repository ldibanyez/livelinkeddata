/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Vectors;

import Vectors.Interval.IntervalException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Sequence of non-overlapping intervals
 * @author ibanez-l
 */
public class IntervalSequence implements Iterable<Interval>{
    
  TreeSet<Interval> sequence;

  public IntervalSequence() {
	this.sequence = new TreeSet<>();
  }

  public IntervalSequence(TreeSet<Interval> S) {
	this.sequence = S;
  }

  // Adds an integer to the sequence, merging intervals as needed
  public boolean add(int n){

	if(this.contains(n)) {
	   // No change, already in the sequence.
       return false; 		
	}
	try {
	  Interval i = new Interval(n,n);
	  Interval left = sequence.lower(i);
	  if(left == null){
		left = new Interval(Integer.MIN_VALUE,Integer.MIN_VALUE);
	  }
	  Interval right = sequence.higher(i);
	  if(right == null){
		right = new Interval(Integer.MAX_VALUE,Integer.MAX_VALUE);
	  }
	  // n causes the merge of two intervals
	  if(left.getUpperLimit()+1 == n && right.getLowerLimit()-1 == n){
	  	Interval ins = new Interval(left.getLowerLimit(),right.getUpperLimit());
		sequence.remove(left);
		sequence.remove(right);
		sequence.add(ins);
	  } 
	  // Merge only with the left interval
	  else if(left.getUpperLimit()+1 == n && n < right.getLowerLimit()-1){
	  	Interval ins = new Interval(left.getLowerLimit(),n);
		sequence.remove(left);
		sequence.add(ins);
	  }
	  // Merge only with the right interval
	  else if(left.getUpperLimit()+1 < n && n == right.getLowerLimit()-1){
	  	Interval ins = new Interval(n,right.getUpperLimit());
		sequence.remove(right);
		sequence.add(ins);
	  }
	  // No merge, insert the interval (n,n)
	  else if(left.getUpperLimit()+1 < n && n < right.getLowerLimit()-1){
		sequence.add(i);
	  }
	  return true;
	} catch (IntervalException ex) {
	  Logger.getLogger(IntervalSequence.class.getName()).log(Level.SEVERE, null, ex);
	}
	
	return true;
  }

  public boolean addInterval(Interval inter){
  
	boolean change = false;
	for(int i = inter.getLowerLimit(); i<= inter.getUpperLimit(); i++){
		if(this.add(i)){ change = true;}
	}
	return change;
	
  }

  public boolean union(IntervalSequence inseq){
  
	boolean change = false;
	for(Interval inter : inseq.sequence){
		if(this.addInterval(inter)){ change = true;}
	}
	return change;
  }

  public boolean contains(int n){
  
	for(Interval i : sequence){
		if(n < i.getLowerLimit()) {return false;}
		if(i.contains(n)) {return true;}
	}
	return false;
  }

  public int max(){
  	return sequence.last().getUpperLimit();
  }

  public int min(){
  	return sequence.first().getLowerLimit();
  }

  public Set<Integer> unpack(){
	HashSet<Integer> s = new HashSet<>();
	for(Interval inter : sequence){
		s.addAll(inter.unpack());
	} 
  	return s;
  }

  // Immediate successor
   public boolean isSuccessor(IntervalSequence inter){
  	Set<Integer> thisUnpack = this.unpack(); 
  	Set<Integer> interUnpack = inter.unpack(); 
	interUnpack.removeAll(thisUnpack);
	return interUnpack.size() == 1;
  }
  
  /**
   *
   * @param obj
   * @return
   */
  @Override
  public boolean equals(Object obj) {
	if (obj == null) {
	  return false;
	}
	if (getClass() != obj.getClass()) {
	  return false;
	}
	final IntervalSequence other = (IntervalSequence) obj;
	if (!Objects.equals(this.sequence, other.sequence)) {
	  return false;
	}
	return true;
  }

  @Override
  public int hashCode() {
	int hash = 7;
	hash = 67 * hash + Objects.hashCode(this.sequence);
	return hash;
  }

  @Override
  public String toString() {
	String s = "[";
	for(Interval i : sequence){
	  s += i.toString() + ",";
	}
	s += "]";
	return s;
  }

  @Override
  public Iterator<Interval> iterator() {
	return sequence.iterator();
  }

}
