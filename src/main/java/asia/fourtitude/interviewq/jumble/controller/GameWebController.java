package asia.fourtitude.interviewq.jumble.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import asia.fourtitude.interviewq.jumble.core.GameState;
import asia.fourtitude.interviewq.jumble.core.JumbleEngine;
import asia.fourtitude.interviewq.jumble.model.GameBoard;

@Controller
@RequestMapping(path = "/game")
@SessionAttributes("board")
public class GameWebController {
	private final JumbleEngine jumbleEngine;

	public GameWebController(JumbleEngine jumbleEngine) {
		this.jumbleEngine = jumbleEngine;
	}

	@ModelAttribute("board")
	public GameBoard gameBoard() {
		/*
		 * This method with "@ModelAttribute" annotation, is so that Spring can
		 * create/initialize an attribute into session scope.
		 */
		return new GameBoard();
	}

	private void scrambleWord(GameBoard board) {
		if (board.getState() != null) {
			String oldScramble = board.getState().getScramble();
			int num = 0;
			do {
				String scramble = this.jumbleEngine.scramble(board.getState().getOriginal());
				board.getState().setScramble(scramble);
				num += 1;
			} while (oldScramble.equals(board.getState().getScramble()) && num <= 10);
		}
	}

	@GetMapping(path = "/goodbye")
	public String goodbye(SessionStatus status) {
		status.setComplete();
		return "game/board";
	}

	@GetMapping("/help")
	public String doGetHelp() {
		return "game/help";
	}

	@GetMapping("/new")
	public String doGetNew(@ModelAttribute(name = "board") GameBoard board, Model model) {
		GameState state = this.jumbleEngine.createGameState(6, 3);

		/*
		 * TODO: a) Assign the created game `state` (with randomly picked word) into
		 * game `board` (session attribute) b) Presentation page to show the information
		 * of game board/state c) Must pass the corresponding unit tests
		 */

		board.setState(state);
		model.addAttribute("board", board);
		return "game/board";
	}

	@GetMapping("/play")
	public String doGetPlay(@ModelAttribute(name = "board") GameBoard board) {
		scrambleWord(board);

		return "game/board";
	}

	@PostMapping("/play")
	public String doPostPlay(@ModelAttribute(name = "board") GameBoard board, BindingResult bindingResult,
			Model model) {
		if (board == null || board.getState() == null) {
			// session expired
			return "game/board";
		}
		
		model.addAttribute("isError", false);
		
		if(StringUtils.isBlank(board.getWord()) || (StringUtils.isNotBlank(board.getWord()) && board.getWord().trim().length() < 3)) {
			model.addAttribute("isError", true);
			model.addAttribute("message", "Enter word (min 3 letters).");
		}
		
		if (board.getState().updateGuessWord(board.getWord())) {
			model.addAttribute("isError", false);
			model.addAttribute("message", String.format("Result for \"%s\" => You guessed correctly !%n", board.getWord()));
        } else {
        	model.addAttribute("isError", true);
        	model.addAttribute("message", String.format("Result for \"%s\" => You guessed incorrectly.%n", board.getWord()));
        }
		
		/*
		 * TODO: a) Validate the input `word` b) From the input guessing `word`,
		 * implement the game logic c) Update the game `board` (session attribute) d)
		 * Show the error: "Guessed incorrectly", when word is guessed incorrectly. e)
		 * Presentation page to show the information of game board/state f) Must pass
		 * the corresponding unit tests
		 */

		model.addAttribute("board", board);
		return "game/board";
	}

}
