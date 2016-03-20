package io.github.phantamanta44.tiabot.util.data;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class CollectionUtils {

	public static <T> T any(T[] array) {
		return any(array, new Random());
	}
	
	public static <T> T any(T[] array, Random rand) {
		return array[rand.nextInt(array.length)];
	}
	
	public static <T> T any(Collection<T> coll) {
		return any(coll, new Random());
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T any(Collection<T> coll, Random rand) {
		Object[] asArray = coll.toArray();
		return (T)any(asArray, rand);
	}
	
	public static <T> T any(List<T> list, Random rand) {
		return list.get(rand.nextInt(list.size()));
	}
	
}
