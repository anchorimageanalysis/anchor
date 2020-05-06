package org.anchoranalysis.image.bean.threshold.relation;

import org.anchoranalysis.bean.shared.relation.threshold.RelationToThreshold;
import org.anchoranalysis.core.relation.GreaterThan;
import org.anchoranalysis.core.relation.RelationToValue;
import org.anchoranalysis.image.binary.values.BinaryValues;

/**
 * Selects anything that is NOT "low" pixels from a binary mask
 * 
 * <p>Uses the default "low" value of 255</p>
 * 
 * <p>Note this is not the same as selecting "high" pixels which would only select pixels of value 255. There's fuzzy undefined space > 1 and < 255</p>.
 * 
 * @author Owen Feehan
 *
 */
public class BinaryNotLowVoxels extends RelationToThreshold {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public double threshold() {
		return BinaryValues.getDefault().getOffInt();
	}

	@Override
	public RelationToValue relation() {
		return new GreaterThan();
	}
}
