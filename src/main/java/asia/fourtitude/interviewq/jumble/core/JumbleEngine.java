package asia.fourtitude.interviewq.jumble.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

public class JumbleEngine {

	/**
	 * From the input `word`, produces/generates a copy which has the same letters,
	 * but in different ordering.
	 *
	 * Example: from "elephant" to "aeehlnpt".
	 *
	 * Evaluation/Grading: a) pass unit test: JumbleEngineTest#scramble() b)
	 * scrambled letters/output must not be the same as input
	 *
	 * @param word The input word to scramble the letters.
	 * @return The scrambled output/letters.
	 */
	public String scramble(String word) {
		/*
		 * Refer to the method's Javadoc (above) and implement accordingly. Must pass
		 * the corresponding unit tests.
		 */

		if (StringUtils.isBlank(word))
			throw new IllegalArgumentException("Input word cannot be NULL, blank or empty !");

		List<String> letters = Arrays.asList(word.split(""));

		if(letters.size() == 1)
			return word;
		
		while (asia.fourtitude.interviewq.jumble.utils.StringUtils.listToString(letters).equals(word)) {
			Collections.shuffle(letters);
		}

		return asia.fourtitude.interviewq.jumble.utils.StringUtils.listToString(letters);
	}

	/**
	 * Retrieves the palindrome words from the internal word list/dictionary
	 * ("src/main/resources/words.txt").
	 *
	 * Word of single letter is not considered as valid palindrome word.
	 *
	 * Examples: "eye", "deed", "level".
	 *
	 * Evaluation/Grading: a) able to access/use resource from classpath b) using
	 * inbuilt Collections c) using "try-with-resources" functionality/statement d)
	 * pass unit test: JumbleEngineTest#palindrome()
	 *
	 * @return The list of palindrome words found in system/engine.
	 * @see https://www.google.com/search?q=palindrome+meaning
	 */
	public Collection<String> retrievePalindromeWords() {
		/*
		 * Refer to the method's Javadoc (above) and implement accordingly. Must pass
		 * the corresponding unit tests.
		 */
		List<String> dictionary = retrieveDictionary();
		List<String> collection = dictionary.stream()
				.filter((word) -> asia.fourtitude.interviewq.jumble.utils.StringUtils.isPalindrome(word))
				.collect(Collectors.toList());

		return collection;
	}

	/**
	 * Picks one word randomly from internal word list.
	 *
	 * Evaluation/Grading: a) pass unit test: JumbleEngineTest#randomWord() b)
	 * provide a good enough implementation, if not able to provide a fast lookup c)
	 * bonus points, if able to implement a fast lookup/scheme
	 *
	 * @param length The word picked, must of length.
	 * @return One of the word (randomly) from word list. Or null if none matching.
	 */
	public String pickOneRandomWord(Integer length) {
		List<String> dictionary = retrieveDictionary();
		dictionary = dictionary.stream().filter((word) -> word.length() == length).collect(Collectors.toList());

		if (dictionary.isEmpty())
			return null;

		Collections.shuffle(dictionary);

		return dictionary.get(0);
	}

	/**
	 * Checks if the `word` exists in internal word list. Matching is case
	 * insensitive.
	 *
	 * Evaluation/Grading: a) pass related unit tests in "JumbleEngineTest" b)
	 * provide a good enough implementation, if not able to provide a fast lookup c)
	 * bonus points, if able to implement a fast lookup/scheme
	 *
	 * @param word The input word to check.
	 * @return true if `word` exists in internal word list.
	 */
	public boolean exists(String word) {
		List<String> dictionary = retrieveDictionary();
		return dictionary.stream().filter((w) -> w.equalsIgnoreCase(word)).count() > 0;
	}

	/**
	 * Finds all the words from internal word list which begins with the input
	 * `prefix`. Matching is case insensitive.
	 *
	 * Invalid `prefix` (null, empty string, blank string, non letter) will return
	 * empty list.
	 *
	 * Evaluation/Grading: a) pass related unit tests in "JumbleEngineTest" b)
	 * provide a good enough implementation, if not able to provide a fast lookup c)
	 * bonus points, if able to implement a fast lookup/scheme
	 *
	 * @param prefix The prefix to match.
	 * @return The list of words matching the prefix.
	 */
	public Collection<String> wordsMatchingPrefix(String prefix) {
		if (StringUtils.isBlank(prefix) || StringUtils.isNumeric(prefix))
			return null;

		List<String> dictionary = retrieveDictionary();
		return dictionary.stream().filter((word) -> word.toLowerCase().startsWith(prefix.toLowerCase()))
				.collect(Collectors.toList());
	}

	public Collection<String> wordsMatchingSuffix(String suffix) {
		if (StringUtils.isBlank(suffix) || StringUtils.isNumeric(suffix))
			return null;

		List<String> dictionary = retrieveDictionary();
		return dictionary.stream().filter((word) -> word.toLowerCase().endsWith(suffix.toLowerCase()))
				.collect(Collectors.toList());
	}

	/**
	 * Finds all the words from internal word list that is matching the searching
	 * criteria.
	 *
	 * `startChar` and `endChar` must be 'a' to 'z' only. And case insensitive.
	 * `length`, if have value, must be positive integer (>= 1).
	 *
	 * Words are filtered using `startChar` and `endChar` first. Then apply `length`
	 * on the result, to produce the final output.
	 *
	 * Must have at least one valid value out of 3 inputs (`startChar`, `endChar`,
	 * `length`) to proceed with searching. Otherwise, return empty list.
	 *
	 * Evaluation/Grading: a) pass related unit tests in "JumbleEngineTest" b)
	 * provide a good enough implementation, if not able to provide a fast lookup c)
	 * bonus points, if able to implement a fast lookup/scheme
	 *
	 * @param startChar The first character of the word to search for.
	 * @param endChar   The last character of the word to match with.
	 * @param length    The length of the word to match.
	 * @return The list of words matching the searching criteria.
	 */
	public Collection<String> searchWords(Character startChar, Character endChar, Integer length) {
		if (startChar != null && !String.valueOf(startChar).matches("(?i)^[a-z]$"))
			throw new IllegalArgumentException("Invalid startChar input.");

		if (endChar != null && !String.valueOf(endChar).matches("(?i)^[a-z]$"))
			throw new IllegalArgumentException("Invalid endChar input.");

		if (startChar == null && endChar == null)
			throw new IllegalArgumentException("startChar or endChar input cannot be NULL.");

		if (length != null && length <= 0)
			throw new IllegalArgumentException("Invalid length, must be more than zero.");

		List<String> dictionary = retrieveDictionary();

		List<String> result = new ArrayList<String>();

		if (!dictionary.isEmpty() && startChar != null) {
			result = dictionary.stream()
					.filter((word) -> (word.toLowerCase().startsWith(String.valueOf(startChar).toLowerCase())))
					.collect(Collectors.toList());
		}

		if (!result.isEmpty() && endChar != null) {
			result = result.stream()
					.filter((word) -> (word.toLowerCase().endsWith(String.valueOf(endChar).toLowerCase())))
					.collect(Collectors.toList());
		}

		if (!result.isEmpty() && length != null) {
			result = result.stream().filter((word) -> (word.length() == length)).collect(Collectors.toList());
		}

		return result;
	}

	/**
	 * Generates all possible combinations of smaller/sub words using the letters
	 * from input word.
	 *
	 * The `minLength` set the minimum length of sub word that is considered as
	 * acceptable word.
	 *
	 * If length of input `word` is less than `minLength`, then return empty list.
	 *
	 * Example: From "yellow" and `minLength` = 3, the output sub words: low, lowly,
	 * lye, ole, owe, owl, well, welly, woe, yell, yeow, yew, yowl
	 *
	 * Evaluation/Grading: a) pass related unit tests in "JumbleEngineTest" b)
	 * provide a good enough implementation, if not able to provide a fast lookup c)
	 * bonus points, if able to implement a fast lookup/scheme
	 *
	 * @param word      The input word to use as base/seed.
	 * @param minLength The minimum length (inclusive) of sub words. Expects
	 *                  positive integer. Default is 3.
	 * @return The list of sub words constructed from input `word`.
	 */
	public Collection<String> generateSubWords(String word, Integer minLength) {
		final int length = minLength == null ? 3 : minLength;

		if (StringUtils.isBlank(word) || length <= 0
				|| StringUtils.isNotBlank(word) && word.trim().length() < length)
			return new ArrayList<String>();

		word = word.trim();

		List<String> letters = Arrays.asList(word.split(""));
		Set<String> wordCollection = new HashSet<String>();

		// search by any start/end letter
		for (String letterA : letters) {
			for (String letterB : letters) {
				wordCollection.addAll(searchWords(letterA.toCharArray()[0], null, null));
				wordCollection.addAll(searchWords(null, letterA.toCharArray()[0], null));
				wordCollection.addAll(searchWords(letterB.toCharArray()[0], null, null));
				wordCollection.addAll(searchWords(null, letterB.toCharArray()[0], null));
				wordCollection.addAll(searchWords(letterA.toCharArray()[0], letterA.toCharArray()[0], null));
				wordCollection.addAll(searchWords(letterB.toCharArray()[0], letterB.toCharArray()[0], null));
				wordCollection.addAll(searchWords(letterA.toCharArray()[0], letterB.toCharArray()[0], null));
				wordCollection.addAll(searchWords(letterB.toCharArray()[0], letterA.toCharArray()[0], null));
			}
		}

		// search by prefix
		for (int i = 0; i < letters.size(); i++) {
			String prefix = "";

			if (i == 0) {
				prefix = letters.get(i);
			} else {
				for (int j = 0; j < i + 1; j++) {
					prefix += letters.get(j);
				}
			}

			wordCollection.addAll(wordsMatchingPrefix(prefix));
		}

		// search by suffix
		for (int i = 0; i < letters.size(); i++) {
			String suffix = "";

			if (i == 0) {
				suffix = letters.get(i);
			} else {
				for (int j = 0; j < i + 1; j++) {
					suffix += letters.get(j);
				}
			}

			wordCollection.addAll(wordsMatchingSuffix(suffix));
		}

		List<String> collection = wordCollection.stream().filter((w) -> w.length() >= length)
				.sorted()
				.collect(Collectors.toList());

		return collection;
	}

	/**
	 * Creates a game state with word to guess, scrambled letters, and possible
	 * combinations of words.
	 *
	 * Word is of length 6 characters. The minimum length of sub words is of length
	 * 3 characters.
	 *
	 * @param length    The length of selected word. Expects >= 3.
	 * @param minLength The minimum length (inclusive) of sub words. Expects
	 *                  positive integer. Default is 3.
	 * @return The game state.
	 */
	public GameState createGameState(Integer length, Integer minLength) {
		Objects.requireNonNull(length, "length must not be null");
		if (minLength == null) {
			minLength = 3;
		} else if (minLength <= 0) {
			throw new IllegalArgumentException("Invalid minLength=[" + minLength + "], expect positive integer");
		}
		if (length < 3) {
			throw new IllegalArgumentException("Invalid length=[" + length + "], expect greater than or equals 3");
		}
		if (minLength > length) {
			throw new IllegalArgumentException(
					"Expect minLength=[" + minLength + "] greater than length=[" + length + "]");
		}
		String original = this.pickOneRandomWord(length);
		if (original == null) {
			throw new IllegalArgumentException("Cannot find valid word to create game state");
		}
		String scramble = this.scramble(original);
		Map<String, Boolean> subWords = new TreeMap<>();
		for (String subWord : this.generateSubWords(original, minLength)) {
			subWords.put(subWord, Boolean.FALSE);
		}
		return new GameState(original, scramble, subWords);
	}

	private List<String> retrieveDictionary() {
		List<String> dictionary = new ArrayList<>();

		BufferedReader reader;

		try {
			ClassPathResource resource = new ClassPathResource("words.txt");
			InputStream inputStream = resource.getInputStream();
			reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			String line = reader.readLine();

			while (line != null) {
				dictionary.add(line);
				line = reader.readLine();
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return dictionary;
	}
}
