package asia.fourtitude.interviewq.jumble.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import asia.fourtitude.interviewq.jumble.TestConfig;
import asia.fourtitude.interviewq.jumble.core.JumbleEngine;
import asia.fourtitude.interviewq.jumble.model.GameGuessInput;

@WebMvcTest(GameApiController.class)
@Import(TestConfig.class)
class GameApiControllerTest {

	static final ObjectMapper OM = new ObjectMapper();

	@Autowired
	private MockMvc mvc;

	@Autowired
	JumbleEngine jumbleEngine;

	/*
	 * NOTE: Refer to "RootControllerTest.java", "GameWebControllerTest.java"
	 * as reference. Search internet for resource/tutorial/help in implementing
	 * the unit tests.
	 *
	 * Refer to "http://localhost:8080/swagger-ui/index.html" for REST API
	 * documentation and perform testing.
	 *
	 * Refer to Postman collection ("interviewq-jumble.postman_collection.json")
	 * for REST API documentation and perform testing.
	 */

	@Test
	void whenCreateNewGame_thenSuccess() throws Exception {
		/*
		 * Doing HTTP GET "/api/game/new"
		 *
		 * Input: None
		 *
		 * Expect: Assert these
		 * a) HTTP status == 200
		 * b) `result` equals "Created new game."
		 * c) `id` is not null
		 * d) `originalWord` is not null
		 * e) `scrambleWord` is not null
		 * f) `totalWords` > 0
		 * g) `remainingWords` > 0 and same as `totalWords`
		 * h) `guessedWords` is empty list
		 */

		MvcResult result = mvc
				.perform(MockMvcRequestBuilders.get("/api/game/new"))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result", is("Created new game.")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.id", notNullValue()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.original_word", notNullValue()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.scramble_word", notNullValue()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.total_words", greaterThan(0)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.remaining_words", greaterThan(0)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.guessed_words", hasSize(0)))
				.andReturn();

		int total_words = JsonPath.read(result.getResponse().getContentAsString(), "$.total_words");
		int remaining_words = JsonPath.read(result.getResponse().getContentAsString(), "$.remaining_words");

		assertEquals(total_words, remaining_words);
	}

	@Test
	void givenMissingId_whenPlayGame_thenInvalidId() throws Exception {
		/*
		 * Doing HTTP POST "/api/game/guess"
		 *
		 * Input: JSON request body
		 * a) `id` is null or missing
		 * b) `word` is null/anything or missing
		 *
		 * Expect: Assert these
		 * a) HTTP status == 404
		 * b) `result` equals "Invalid Game ID."
		 */

		mvc
				.perform(
						MockMvcRequestBuilders.post("/api/game/guess")
								.contentType(MediaType.APPLICATION_JSON)
								.content(new ObjectMapper().writeValueAsString(new GameGuessInput() {
									{
										setId(null);
										setWord(null);
									}
								})))
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result", is("Invalid Game ID")));
	}

	@Test
	void givenMissingRecord_whenPlayGame_thenRecordNotFound() throws Exception {
		/*
		 * Doing HTTP POST "/api/game/guess"
		 *
		 * Input: JSON request body
		 * a) `id` is some valid ID (but not exists in game system)
		 * b) `word` is null/anything or missing
		 *
		 * Expect: Assert these
		 * a) HTTP status == 404
		 * b) `result` equals "Game board/state not found."
		 */

		mvc
				.perform(
						MockMvcRequestBuilders.post("/api/game/guess")
								.contentType(MediaType.APPLICATION_JSON)
								.content(new ObjectMapper().writeValueAsString(new GameGuessInput() {
									{
										setId("test12345");
										setWord(null);
									}
								})))
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result", is("Game board/state not found")));
	}

	@Test
	void givenCreateNewGame_whenSubmitNullWord_thenGuessedIncorrectly() throws Exception {
		/*
		 * Doing HTTP POST "/api/game/guess"
		 *
		 * Given:
		 * a) has valid game ID from previously created game
		 *
		 * Input: JSON request body
		 * a) `id` of previously created game
		 * b) `word` is null or missing
		 *
		 * Expect: Assert these
		 * a) HTTP status == 200
		 * b) `result` equals "Guessed incorrectly."
		 * c) `id` equals to `id` of this game
		 * d) `originalWord` is equals to `originalWord` of this game
		 * e) `scrambleWord` is not null
		 * f) `guessWord` is equals to `input.word`
		 * g) `totalWords` is equals to `totalWords` of this game
		 * h) `remainingWords` is equals to `remainingWords` of previous game state (no
		 * change)
		 * i) `guessedWords` is empty list (because this is first attempt)
		 */

		MvcResult newGameResult = mvc
				.perform(MockMvcRequestBuilders.get("/api/game/new"))
				.andReturn();

		String id = JsonPath.read(newGameResult.getResponse().getContentAsString(), "$.id");

		mvc
				.perform(
						MockMvcRequestBuilders.post("/api/game/guess")
								.contentType(MediaType.APPLICATION_JSON)
								.content(new ObjectMapper().writeValueAsString(new GameGuessInput() {
									{
										setId(id);
										setWord(null);
									}
								})))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result", is("Guessed incorrectly.")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.id", is(id)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.original_word", is((String) JsonPath.read(newGameResult.getResponse().getContentAsString(), "$.original_word"))))
				.andExpect(MockMvcResultMatchers.jsonPath("$.scramble_word", notNullValue()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.guess_word", is("")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.total_words", is((Integer) JsonPath.read(newGameResult.getResponse().getContentAsString(), "$.total_words"))))
				.andExpect(MockMvcResultMatchers.jsonPath("$.remaining_words", is((Integer) JsonPath.read(newGameResult.getResponse().getContentAsString(), "$.remaining_words"))))
				.andExpect(MockMvcResultMatchers.jsonPath("$.guessed_words", hasSize(0)));
	}

	@Test
	void givenCreateNewGame_whenSubmitWrongWord_thenGuessedIncorrectly() throws Exception {
		/*
		 * Doing HTTP POST "/api/game/guess"
		 *
		 * Given:
		 * a) has valid game ID from previously created game
		 *
		 * Input: JSON request body
		 * a) `id` of previously created game
		 * b) `word` is some value (that is not correct answer)
		 *
		 * Expect: Assert these
		 * a) HTTP status == 200
		 * b) `result` equals "Guessed incorrectly."
		 * c) `id` equals to `id` of this game
		 * d) `originalWord` is equals to `originalWord` of this game
		 * e) `scrambleWord` is not null
		 * f) `guessWord` equals to input `guessWord`
		 * g) `totalWords` is equals to `totalWords` of this game
		 * h) `remainingWords` is equals to `remainingWords` of previous game state (no
		 * change)
		 * i) `guessedWords` is empty list (because this is first attempt)
		 */

		MvcResult newGameResult = mvc
				.perform(MockMvcRequestBuilders.get("/api/game/new"))
				.andReturn();

		String id = JsonPath.read(newGameResult.getResponse().getContentAsString(), "$.id");

		mvc
				.perform(
						MockMvcRequestBuilders.post("/api/game/guess")
								.contentType(MediaType.APPLICATION_JSON)
								.content(new ObjectMapper().writeValueAsString(new GameGuessInput() {
									{
										setId(id);
										setWord("aaaaaaa");
									}
								})))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result", is("Guessed incorrectly.")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.id", is(id)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.original_word", is((String) JsonPath.read(newGameResult.getResponse().getContentAsString(), "$.original_word"))))
				.andExpect(MockMvcResultMatchers.jsonPath("$.scramble_word", notNullValue()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.guess_word", is("aaaaaaa")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.total_words", is((Integer) JsonPath.read(newGameResult.getResponse().getContentAsString(), "$.total_words"))))
				.andExpect(MockMvcResultMatchers.jsonPath("$.remaining_words", is((Integer) JsonPath.read(newGameResult.getResponse().getContentAsString(), "$.remaining_words"))))
				.andExpect(MockMvcResultMatchers.jsonPath("$.guessed_words", hasSize(0)));
	}

	@Test
	void givenCreateNewGame_whenSubmitFirstCorrectWord_thenGuessedCorrectly() throws Exception {
		/*
		 * Doing HTTP POST "/api/game/guess"
		 *
		 * Given:
		 * a) has valid game ID from previously created game
		 *
		 * Input: JSON request body
		 * a) `id` of previously created game
		 * b) `word` is of correct answer
		 *
		 * Expect: Assert these
		 * a) HTTP status == 200
		 * b) `result` equals "Guessed correctly."
		 * c) `id` equals to `id` of this game
		 * d) `originalWord` is equals to `originalWord` of this game
		 * e) `scrambleWord` is not null
		 * f) `guessWord` equals to input `guessWord`
		 * g) `totalWords` is equals to `totalWords` of this game
		 * h) `remainingWords` is equals to `remainingWords - 1` of previous game state
		 * (decrement by 1)
		 * i) `guessedWords` is not empty list
		 * j) `guessWords` contains input `guessWord`
		 */

		MvcResult newGameResult = mvc
				.perform(MockMvcRequestBuilders.get("/api/game/new"))
				.andReturn();

		String id = JsonPath.read(newGameResult.getResponse().getContentAsString(), "$.id");
		String word = JsonPath.read(newGameResult.getResponse().getContentAsString(), "$.original_word");

		Collection<String> subWords = jumbleEngine.generateSubWords(word, 2);
		String guessedWord = subWords.iterator().next();

		mvc
				.perform(
						MockMvcRequestBuilders.post("/api/game/guess")
								.contentType(MediaType.APPLICATION_JSON)
								.content(new ObjectMapper().writeValueAsString(new GameGuessInput() {
									{
										setId(id);
										setWord(guessedWord);
									}
								})))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result", is("Guessed correctly.")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.id", is(id)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.original_word", is((String) JsonPath.read(newGameResult.getResponse().getContentAsString(), "$.original_word"))))
				.andExpect(MockMvcResultMatchers.jsonPath("$.scramble_word", notNullValue()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.guess_word", is(guessedWord)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.total_words", is((Integer) JsonPath.read(newGameResult.getResponse().getContentAsString(), "$.total_words"))))
				.andExpect(MockMvcResultMatchers.jsonPath("$.remaining_words", is(((Integer) JsonPath.read(newGameResult.getResponse().getContentAsString(), "$.remaining_words")) - 1)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.guessed_words", hasSize(1)));
	}

	@Test
	void givenCreateNewGame_whenSubmitAllCorrectWord_thenAllGuessed() throws Exception {
		/*
		 * Doing HTTP POST "/api/game/guess"
		 *
		 * Given:
		 * a) has valid game ID from previously created game
		 * b) has submit all correct answers, except the last answer
		 *
		 * Input: JSON request body
		 * a) `id` of previously created game
		 * b) `word` is of the last correct answer
		 *
		 * Expect: Assert these
		 * a) HTTP status == 200
		 * b) `result` equals "All words guessed."
		 * c) `id` equals to `id` of this game
		 * d) `originalWord` is equals to `originalWord` of this game
		 * e) `scrambleWord` is not null
		 * f) `guessWord` equals to input `guessWord`
		 * g) `totalWords` is equals to `totalWords` of this game
		 * h) `remainingWords` is 0 (no more remaining, game ended)
		 * i) `guessedWords` is not empty list
		 * j) `guessWords` contains input `guessWord`
		 */

		MvcResult newGameResult = mvc
				.perform(MockMvcRequestBuilders.get("/api/game/new"))
				.andReturn();

		String id = JsonPath.read(newGameResult.getResponse().getContentAsString(), "$.id");
		String word = JsonPath.read(newGameResult.getResponse().getContentAsString(), "$.original_word");
		Collection<String> subWords = jumbleEngine.generateSubWords(word, 2);

		for (String subWord : new ArrayList<String>(subWords)) {
			MvcResult result = mvc
					.perform(
							MockMvcRequestBuilders.post("/api/game/guess")
									.contentType(MediaType.APPLICATION_JSON)
									.content(new ObjectMapper().writeValueAsString(new GameGuessInput() {
										{
											setId(id);
											setWord(subWord);
										}
									})))
					.andReturn();

			String resultText = JsonPath.read(result.getResponse().getContentAsString(), "$.result");
			
			// Integer remaining = JsonPath.read(result.getResponse().getContentAsString(), "$.remaining_words");
			// System.out.println(String.format("[Remaining words : %s] Guessing '%s' against '%s' - %s", remaining, subWord, word, resultText));

			if ("All words guessed.".equalsIgnoreCase(resultText)) {
				break;
			}
		}

		mvc
				.perform(
						MockMvcRequestBuilders.post("/api/game/guess")
								.contentType(MediaType.APPLICATION_JSON)
								.content(new ObjectMapper().writeValueAsString(new GameGuessInput() {
									{
										setId(id);
										setWord(new ArrayList<String>(subWords).get(0));
									}
								})))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result", is("All words guessed.")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.id", is(id)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.original_word", is((String) JsonPath.read(newGameResult.getResponse().getContentAsString(), "$.original_word"))))
				.andExpect(MockMvcResultMatchers.jsonPath("$.scramble_word", notNullValue()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.guess_word", is(new ArrayList<String>(subWords).get(0))))
				.andExpect(MockMvcResultMatchers.jsonPath("$.total_words", is((Integer) JsonPath.read(newGameResult.getResponse().getContentAsString(), "$.total_words"))))
				.andExpect(MockMvcResultMatchers.jsonPath("$.remaining_words", is(0)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.guessed_words", hasSize((Integer) JsonPath.read(newGameResult.getResponse().getContentAsString(), "$.total_words"))));

	}

}
