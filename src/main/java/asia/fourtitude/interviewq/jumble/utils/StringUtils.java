package asia.fourtitude.interviewq.jumble.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {
	public static String listToString(List<String> list) {
		return list.stream().map(Object::toString).collect(Collectors.joining(""));
	}

	public static boolean isPalindrome(String word) {
		if (org.apache.commons.lang3.StringUtils.isBlank(word) || word.trim().length() == 1)
			return false;

		List<String> letters = Arrays.asList(word.split(""));
		Collections.reverse(letters);
		return listToString(letters).equals(word);
	}
}
