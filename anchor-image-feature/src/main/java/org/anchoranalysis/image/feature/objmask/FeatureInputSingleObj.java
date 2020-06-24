package org.anchoranalysis.image.feature.objmask;

import java.util.Optional;

import org.anchoranalysis.feature.input.FeatureInputNRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.objectmask.ObjectMask;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * An input representing a single object-mask (with maybe an NRG-stack associated)
 * 
 * <p>Equals and hash-code must be sensibly defined as these inputs can be used as keys
 * in a cache</p>.
 * 
 * @author Owen Feehan
 *
 */
public class FeatureInputSingleObj extends FeatureInputNRGStack {

	private ObjectMask objMask;
	
	public FeatureInputSingleObj(ObjectMask objMask) {
		this(
			objMask,
			Optional.empty()
		);
	}
	
	public FeatureInputSingleObj(ObjectMask objMask, NRGStackWithParams nrgStack) {
		this(
			objMask,
			Optional.of(nrgStack)
		);
	}
	
	public FeatureInputSingleObj(ObjectMask objMask, Optional<NRGStackWithParams> nrgStack) {
		super(nrgStack);
		this.objMask = objMask;
	}

	public ObjectMask getObjMask() {
		return objMask;
	}

	public void setObjMask(ObjectMask objMask) {
		this.objMask = objMask;
	}
	
	@Override
	public String toString() {
		return objMask.toString();
	}

	/** This assumes objMask's equals() is cheap i.e. shallow-equals not deep-equals */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {
			return false;
		}
		FeatureInputSingleObj rhs = (FeatureInputSingleObj) obj;
		return new EqualsBuilder()
			.appendSuper( super.equals(obj) )
            .append(objMask, rhs.objMask)
            .isEquals();
	}

	/** This assumes objMask's equals() is cheap i.e. shallow-equals not deep-equals */
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.appendSuper( super.hashCode() )
			.append(objMask)
			.toHashCode();
	}
}
