package org.anchoranalysis.feature.io.csv.name;

import java.util.Iterator;
import java.util.Optional;

import org.apache.commons.collections.iterators.SingletonIterator;

/**
 * A name with only one part, and is always unique
 * 
 * @author Owen Feehan
 *
 */
public class SimpleName implements MultiName {

	private String name;
	
	public SimpleName(String name) {
		super();
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<String> iterator() {
		return new SingletonIterator(name);
	}
	
	@Override
	public Optional<String> directoryPart() {
		return Optional.empty();
	}
	
	@Override
	public String filePart() {
		return name;
	}
	
	@Override
	public int compareTo(MultiName other) {
		
		if (other instanceof SimpleName) {
			return name.compareTo(
				((SimpleName) other).name
			);
		} else {
			return -1;
		}
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleName other = (SimpleName) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
