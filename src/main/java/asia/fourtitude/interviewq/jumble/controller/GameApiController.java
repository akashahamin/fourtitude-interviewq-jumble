package asia.fourtitude.interviewq.jumble.controller;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import asia.fourtitude.interviewq.jumble.core.GameState;
import asia.fourtitude.interviewq.jumble.core.JumbleEngine;
import asia.fourtitude.interviewq.jumble.model.GameGuessInput;
import asia.fourtitude.interviewq.jumble.model.GameGuessModel;
import asia.fourtitude.interviewq.jumble.model.GameGuessOutput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Game API", description = "Guessing words game REST API endpoint.")
@RequestMapping(path = "/api/game")
public class GameApiController {

	private final JumbleEngine jumbleEngine;

	/*
	 * In-memory database/repository for all the game boards/states.
	 */
	private final Map<String, GameGuessModel> gameBoards;

	public GameApiController(JumbleEngine jumbleEngine) {
		this.jumbleEngine = jumbleEngine;
		this.gameBoards = new ConcurrentHashMap<>();
	}

	@Operation(summary = "Creates new game board/state", description = "Creates a new game board/state and registered into game engine referenced by `id`. All subsequent operation/play is tied to `id`.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GameGuessOutput.class), examples = {
					@ExampleObject(name = "Success", description = "Created a new game/board and registered into system.", value = "{\n"
							+ "  \"result\": \"Created new game.\",\n"
							+ "  \"id\": \"65e0d7a4-59bf-4065-beb1-3c2220d87e1e\",\n"
							+ "  \"original_word\": \"titans\",\n" + "  \"scramble_word\": \"nisatt\",\n"
							+ "  \"total_words\": 29,\n" + "  \"remaining_words\": 29,\n" + "  \"guessed_words\": []\n"
							+ "}") })) })
	@GetMapping(value = "/new", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GameGuessOutput> newGame() {
		GameGuessOutput output = new GameGuessOutput();
		GameState gameState = this.jumbleEngine.createGameState(6, 3);
		
		GameGuessModel model = new GameGuessModel();
		model.setGameState(gameState);
		model.setId(UUID.randomUUID().toString());

		gameBoards.put(model.getId(), model);
		
		output.setId(model.getId());
		output.setResult("Created new game.");
		output.setOriginalWord(gameState.getOriginal());
		output.setScrambleWord(gameState.getScramble());
		output.setTotalWords(gameState.getSubWords().size());
		output.setRemainingWords(gameState.getSubWords().size() - gameState.getGuessedWords().size());
		output.setGuessedWords(gameState.getGuessedWords());

		return new ResponseEntity<>(output, HttpStatus.OK);
	}

	@Operation(summary = "Submits word to play the game", description = "Submits a guessed `word`, along with `id` to play the game.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GameGuessOutput.class), examples = {
					@ExampleObject(name = "Guessed Correctly First Time", description = "Guessed correctly the first time.", value = "{\n"
							+ "  \"result\": \"Guessed correctly.\",\n"
							+ "  \"id\": \"88b4278c-5141-42af-86e6-2a1d4cfa5f3b\",\n"
							+ "  \"original_word\": \"ranker\",\n" + "  \"scramble_word\": \"nekarr\",\n"
							+ "  \"guess_word\": \"rank\",\n" + "  \"total_words\": 15,\n"
							+ "  \"remaining_words\": 14,\n" + "  \"guessed_words\": [\n" + "    \"rank\"\n" + "  ]\n"
							+ "}"),
					@ExampleObject(name = "Guessed Correctly Subsequent", description = "Guessed correctly with subsequent word.", value = "{\n"
							+ "  \"result\": \"Guessed correctly.\",\n"
							+ "  \"id\": \"e23a268c-e4af-4123-a610-755e34ac201c\",\n"
							+ "  \"original_word\": \"burger\",\n" + "  \"scramble_word\": \"rerugb\",\n"
							+ "  \"guess_word\": \"rug\",\n" + "  \"total_words\": 15,\n"
							+ "  \"remaining_words\": 7,\n" + "  \"guessed_words\": [\n" + "    \"bug\",\n"
							+ "    \"bur\",\n" + "    \"err\",\n" + "    \"rug\",\n" + "    \"burr\",\n"
							+ "    \"grub\",\n" + "    \"rube\",\n" + "    \"urge\"\n" + "  ]\n" + "}"),
					@ExampleObject(name = "Guessed Incorrectly", description = "Guessed with incorrect word.", value = "{\n"
							+ "  \"result\": \"Guessed incorrectly.\",\n"
							+ "  \"id\": \"88b4278c-5141-42af-86e6-2a1d4cfa5f3b\",\n"
							+ "  \"original_word\": \"ranker\",\n" + "  \"scramble_word\": \"rnraek\",\n"
							+ "  \"guess_word\": \"answer\",\n" + "  \"total_words\": 15,\n"
							+ "  \"remaining_words\": 15,\n" + "  \"guessed_words\": []\n" + "}"),
					@ExampleObject(name = "All Guessed", description = "All words guessed.", value = "{\n"
							+ "  \"result\": \"All words guessed.\",\n"
							+ "  \"id\": \"353ee769-a472-4704-a5f2-d525f181a03e\",\n"
							+ "  \"original_word\": \"gloomy\",\n" + "  \"scramble_word\": \"gomlyo\",\n"
							+ "  \"guess_word\": \"moo\",\n" + "  \"total_words\": 9,\n" + "  \"remaining_words\": 0,\n"
							+ "  \"guessed_words\": [\n" + "    \"goo\",\n" + "    \"gym\",\n" + "    \"log\",\n"
							+ "    \"loo\",\n" + "    \"moo\",\n" + "    \"glom\",\n" + "    \"logo\",\n"
							+ "    \"loom\",\n" + "    \"gloom\"\n" + "  ]\n" + "}") })),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GameGuessOutput.class), examples = {
					@ExampleObject(name = "Invalid ID", description = "The input `ID` is invalid.", value = "{\n"
							+ "  \"result\": \"Invalid Game ID.\"\n" + "}"),
					@ExampleObject(name = "Record not found", description = "The `ID` is correct format, but game board/state is not found in system.", value = "{\n"
							+ "  \"result\": \"Game board/state not found.\"\n" + "}") })) })
	@PostMapping(value = "/guess", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GameGuessOutput> playGame(
			@Parameter(description = "Submits the `word` to guess.", required = true, schema = @Schema(implementation = GameGuessInput.class), example = "{\n"
					+ "  \"id\": \"4579256c-326f-4169-9b56-6d1d1a2c11f0\",\n" + "  \"word\": \"answer\"\n"
					+ "}") @RequestBody GameGuessInput input) {
		
		GameGuessOutput output = new GameGuessOutput();
		
		if(StringUtils.isBlank(input.getId())) {
			output.setResult("Invalid Game ID");
			return new ResponseEntity<>(output, HttpStatus.NOT_FOUND);
		}
		
		if(StringUtils.isNotBlank(input.getId()) && gameBoards.get(input.getId()) == null) {
			output.setResult("Game board/state not found");
			return new ResponseEntity<>(output, HttpStatus.NOT_FOUND);
		}

		GameGuessModel model = gameBoards.get(input.getId());
		
		boolean isCorrect = model.getGameState().updateGuessWord(input.getWord());
		
		String result = "";
		
		if(isCorrect && (model.getGameState().getSubWords().size() - model.getGameState().getGuessedWords().size()) == 0) {
			result = "All words guessed.";
		}
		
		if(isCorrect && (model.getGameState().getSubWords().size() - model.getGameState().getGuessedWords().size()) > 0) {
			result = "Guessed correctly.";
		}
		
		if(!isCorrect) {
			result = "Guessed incorrectly.";
		}
		
		
		output.setId(model.getId());
		output.setResult(result);
		output.setOriginalWord(model.getGameState().getOriginal());
		output.setScrambleWord(model.getGameState().getScramble());
		output.setTotalWords(model.getGameState().getSubWords().size());
		output.setRemainingWords(model.getGameState().getSubWords().size() - model.getGameState().getGuessedWords().size());
		output.setGuessedWords(model.getGameState().getGuessedWords());
		output.setGuessWord(input.getWord() == null ? "" : input.getWord());
		
		return new ResponseEntity<>(output, HttpStatus.OK);
	}
}
