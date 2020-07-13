package org.anchoranalysis.anchor.mpp.feature.nrg.scheme;

/*
 * #%L
 * anchor-mpp
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


import java.util.HashMap;
import java.util.Iterator;

import org.anchoranalysis.core.name.value.SimpleNameValue;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;

/**
 * A set of NRGSchemes each with a name.
 * 
 * SharedFeatures and a CachedCalculationList are also associated
 * 
 * @author Owen Feehan
 *
 */
public class NamedNRGSchemeSet implements Iterable<SimpleNameValue<NRGScheme>> {

	private HashMap<String,SimpleNameValue<NRGScheme>> delegate = new HashMap<>();
	private SharedFeatureMulti sharedFeatures;
	
	public NamedNRGSchemeSet(SharedFeatureMulti sharedFeatures) {
		super();
		this.sharedFeatures = sharedFeatures;
	}

	public SharedFeatureMulti getSharedFeatures() {
		return sharedFeatures;
	}
	public void setSharedFeatures(SharedFeatureMulti sharedFeatures) {
		this.sharedFeatures = sharedFeatures;
	}
	
	public boolean add(String name, NRGScheme nrgScheme) {
		delegate.put(
			name,
			new SimpleNameValue<>(name, nrgScheme)
		);
		return true;
	}
	
	public SimpleNameValue<NRGScheme> get(String name) {
		return delegate.get(name);
	}
	
	@Override
	public Iterator<SimpleNameValue<NRGScheme>> iterator() {
		return delegate.values().iterator();
	}
		
}
