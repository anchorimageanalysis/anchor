package org.anchoranalysis.core.name.store;

/*
 * #%L
 * anchor-bean
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */


import java.util.Set;

import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.value.NameValue;

/**
 * Evaluates items via their Getter as soon as they are added
 * 
 * @author Owen Feehan
 *
 * @param <T> item-type in store
 */
public class EagerEvaluationStore<T> extends NamedProviderStore<T> {

	private NameValueSet<T> delegate = new NameValueSet<>();
	
	@Override
	public T getException(String key) throws GetOperationFailedException {
		T obj = delegate.getException(key);
		if (obj==null) {
			throw new GetOperationFailedException( String.format("Object '%s' does not exist in store ",key) );
		}
		return obj;
	}

	@Override
	public Set<String> keys() {
		return delegate.keys();
	}

	@Override
	public void add(String name, Operation<T> getter) throws OperationFailedException {

		try {
			NameValue<T> item = new NameValue<>(
				new String(name),
				getter.doOperation()
			);
			
			delegate.add(item);
		} catch (ExecuteException e) {
			throw new OperationFailedException(e);
		}
		
	}

	@Override
	public T getNull(String key) throws GetOperationFailedException {
		return delegate.getException(key);
	}



}
