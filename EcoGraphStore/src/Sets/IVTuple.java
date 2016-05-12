/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Sets;

import java.util.Objects;

/**
 *
 * @author ibanez-l
 */
 final class IVTuple {
  
	final Object element;
	final String replica;
	final int timestamp;

	public IVTuple(Object element, String replica, int timestamp) {
	  this.element = element;
	  this.replica = replica;
	  this.timestamp = timestamp;
	}

	public Object getElement() {
	  return element;
	}

	public String getReplica() {
	  return replica;
	}

	public int getTimestamp() {
	  return timestamp;
	}

	@Override
	public String toString() {
	  return "IVTuple{" + "element=" + element + ", replica=" + replica + ", timestamp=" + timestamp + '}';
	}

	@Override
	public int hashCode() {
	  int hash = 7;
	  hash = 23 * hash + Objects.hashCode(this.element);
	  hash = 23 * hash + Objects.hashCode(this.replica);
	  hash = 23 * hash + this.timestamp;
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
	  final IVTuple other = (IVTuple) obj;
	  if (!Objects.equals(this.element, other.element)) {
		return false;
	  }
	  if (!Objects.equals(this.replica, other.replica)) {
		return false;
	  }
	  if (this.timestamp != other.timestamp) {
		return false;
	  }
	  return true;
	}

 }