package org.anchoranalysis.bean.store;

import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.functional.Operation;

/**
 * 
 * @author Owen Feehan
 *
 * @param <S> source-type
 * @param <D> destination-type
 * @param <E> exception-type if something goes wrong
 */
class CurriedObjectBridge<S, D, E extends Throwable> implements Operation<D,E> {

	private FunctionWithException<S,D,E> bridge;
	private S sourceObject;
	
	public CurriedObjectBridge(	FunctionWithException<S,D,E> bridge, S sourceObject) {
		super();
		this.bridge = bridge;
		this.sourceObject = sourceObject;
	}

	@Override
	public D doOperation() throws E {
		return bridge.apply(sourceObject);
	}
}
