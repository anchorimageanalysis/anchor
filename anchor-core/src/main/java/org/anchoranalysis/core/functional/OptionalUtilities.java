package org.anchoranalysis.core.functional;

import java.util.Optional;

/**
 * Additional utility functions for {@link Optional} and exceptions.
 * 
 * @author Owen Feehan
 *
 */
public class OptionalUtilities {
	
	private OptionalUtilities() {}

	/**
	 * Function used to map from one optional to another
	 * 
	 * @author Owen Feehan
	 *
	 * @param <S> source-type
	 * @param <T> target-type
	 * @param <E> exception that can be thrown during mapping
	 */ 
	@FunctionalInterface
	public interface MapFunction<S, T, E extends Throwable> {
		T apply(S in) throws E;
	}
	
	/**
	 * Function used to map from two optionals to another single optional
	 * 
	 * @author Owen Feehan
	 *
	 * @param <S> source-type
	 * @param <T> target-type
	 * @param <E> exception that can be thrown during mapping
	 */ 
	@FunctionalInterface
	public interface MapFunctionTwo<T, U, V, E extends Throwable> {
		T apply(U in1, V in2) throws E;
	}
	
	/**
	 * Consumes a value and throws an exception
	 * 
	 * @author Owen Feehan
	 *
	 * @param <S> source-type
	 * @param <E> exception that can be thrown during apply
	 */ 
	@FunctionalInterface
	public interface ConsumerWithException<S, E extends Throwable> {
		void accept(S in) throws E;
	}
		
	/**
	 * Like {@link Optional::map} but tolerates an exception in the mapping function, which is immediately thrown.
	 * 
	 * @param <S> incoming optional-type for map
	 * @param <T> outgoing optional-type for map
	 * @param <E> exception that may be thrown during mapping
	 * @param opt incoming optional
	 * @param mapFunc the function that does the mapping from incoming to outgoing
	 * @return the outgoing "mapped" optional
	 * @throws E an exception if the mapping function throws it
	 */
	public static <S,E extends Throwable> void ifPresent( Optional<S> opt, ConsumerWithException<S,E> consumerFunc ) throws E {
		if (opt.isPresent()) {
			consumerFunc.accept( opt.get() );
		}
	}

	/**
	 * Like {@link Optional::map} but tolerates an exception in the mapping function, which is immediately thrown.
	 * 
	 * @param <S> incoming optional-type for map
	 * @param <T> outgoing optional-type for map
	 * @param <E> exception that may be thrown during mapping
	 * @param opt incoming optional
	 * @param mapFunc the function that does the mapping from incoming to outgoing
	 * @return the outgoing "mapped" optional
	 * @throws E an exception if the mapping function throws it
	 */
	public static <S,T,E extends Throwable> Optional<T> map( Optional<S> opt, MapFunction<S,T,E> mapFunc ) throws E {
		if (opt.isPresent()) {
			T target = mapFunc.apply( opt.get() );
			return Optional.of(target);
		} else {
			return Optional.empty();
		}
	}
	
	/**
	 * Like {@link Optional::flatMap} but tolerates an exception in the mapping function, which is immediately thrown.
	 * 
	 * @param <S> incoming optional-type for map
	 * @param <T> outgoing optional-type for map
	 * @param <E> exception that may be thrown during mapping
	 * @param opt incoming optional
	 * @param mapFunc the function that does the mapping from incoming to outgoing
	 * @return the outgoing "mapped" optional
	 * @throws E an exception if the mapping function throws it
	 */
	public static <S,T,E extends Throwable> Optional<T> flatMap( Optional<S> opt, MapFunction<S,Optional<T>,E> mapFunc ) throws E {
		if (opt.isPresent()) {
			return mapFunc.apply( opt.get() );
		} else {
			return Optional.empty();
		}
	}
	
	/**
	 * Mapping only occurs if both Optionals are non-empty (equivalent to a logical AND on the optionals)
	 * 
	 * @param <T> outgoing optional-type for map
	 * @param <U> first incoming optional-type for map
	 * @param <V> second incoming optional-type for map
	 * @param optional1 first incoming optional
	 * @param optional2 second incoming optional
	 * @param mapFunc the function that does the mapping from both incoming objects to outgoing
	 * @return the outgoing "mapped" optional (empty() if either incoming optional is empty)
	 */
	public static <T,U,V,E extends Throwable> Optional<T> mapBoth( Optional<U> optional1, Optional<V> optional2, MapFunctionTwo<T, U, V, E> mapFunc) throws E {
		if (optional1.isPresent() && optional2.isPresent()) {
			return Optional.of(
				mapFunc.apply(optional1.get(), optional2.get())	
			);
		} else {
			return Optional.empty();
		}
	}
	
	/**
	 * The first optional if it's present, or failing that the second optional
	 * 
	 * @param <T> type of optionals
	 * @return a new optional that is optional1 OR optional2
	 */
	public static<T> Optional<T> orFlat( Optional<T> optional1, Optional<T> optional2 ) {
		if (optional1.isPresent()) {
			return optional1;
		} else {
			return optional2;
		}
	}
	
	/**
	 * Creates an Optional from a string that might be empty or null
	 * 
	 * @param possiblyEmptyString a string that might be empty or null
	 * @return the string, or empty() if the string is empty or null
	 */
	public static Optional<String> create( String possiblyEmptyString ) {
		if (possiblyEmptyString==null || possiblyEmptyString.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(possiblyEmptyString);
		}
	}
}
