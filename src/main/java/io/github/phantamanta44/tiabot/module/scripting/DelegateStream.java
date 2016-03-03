package io.github.phantamanta44.tiabot.module.scripting;

import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class DelegateStream<T> {
	
	private final Stream<T> delegate;
	
	public DelegateStream(Stream<T> delegate) {
		this.delegate = delegate;
	}

	public DelegateStream<T> filter(Predicate<? super T> predicate) {
		return new DelegateStream<>(delegate.filter(predicate));
	}

	public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
		return delegate.map(mapper);
	}

	public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
		return delegate.flatMap(mapper);
	}

	public DelegateStream<T> distinct() {
		return new DelegateStream<>(delegate.distinct());
	}

	public DelegateStream<T> sorted() {
		return new DelegateStream<>(delegate.sorted());
	}

	public DelegateStream<T> sorted(Comparator<? super T> comparator) {
		return new DelegateStream<>(delegate.sorted(comparator));
	}

	public DelegateStream<T> peek(Consumer<? super T> action) {
		return new DelegateStream<>(delegate.peek(action));
	}

	public DelegateStream<T> limit(long maxSize) {
		return new DelegateStream<>(delegate.limit(maxSize));
	}

	public DelegateStream<T> skip(long n) {
		return new DelegateStream<>(delegate.skip(n));
	}

	public void forEach(Consumer<? super T> action) {
		delegate.forEach(action);
	}

	public T reduce(T identity, BinaryOperator<T> accumulator) {
		return delegate.reduce(identity, accumulator);
	}

	public T reduce(BinaryOperator<T> accumulator) {
		return delegate.reduce(accumulator).orElse(null);
	}

	public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
		return delegate.reduce(identity, accumulator, combiner);
	}

	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
		return delegate.collect(supplier, accumulator, combiner);
	}

	public <R, A> R collect(Collector<? super T, A, R> collector) {
		return delegate.collect(collector);
	}

	public T min(Comparator<? super T> comparator) {
		return delegate.min(comparator).orElse(null);
	}

	public T max(Comparator<? super T> comparator) {
		return delegate.max(comparator).orElse(null);
	}

	public long count() {
		return delegate.count();
	}

	public boolean anyMatch(Predicate<? super T> predicate) {
		return delegate.anyMatch(predicate);
	}

	public boolean allMatch(Predicate<? super T> predicate) {
		return delegate.allMatch(predicate);
	}

	public boolean noneMatch(Predicate<? super T> predicate) {
		return delegate.noneMatch(predicate);
	}

	public T findFirst() {
		return delegate.findFirst().orElse(null);
	}

	public T findAny() {
		return delegate.findAny().orElse(null);
	}
	
	public T find(Predicate<T> predicate) {
		return delegate.filter(predicate)
				.findAny().orElse(null);
	}
	
	public <C> T find(C criteria, Function<T, C> mapper) {
		return delegate.filter(o -> mapper.apply(o).equals(criteria))
				.findAny().orElse(null);
	}
	
}
