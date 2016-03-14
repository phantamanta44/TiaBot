package io.github.phantamanta44.tiabot.util;

import java.util.Collection;
import java.util.Random;

public class CollectionUtils {

	@SuppressWarnings("unchecked")
	public static <T> T any(Collection<T> coll, Random rand) {
		Object[] asArray = coll.toArray();
		return (T)asArray[rand.nextInt(asArray.length)];
	}
	
}
