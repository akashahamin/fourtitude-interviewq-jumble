package asia.fourtitude.interviewq.jumble.controller;

import java.time.ZonedDateTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import asia.fourtitude.interviewq.jumble.core.JumbleEngine;
import asia.fourtitude.interviewq.jumble.model.ExistsForm;
import asia.fourtitude.interviewq.jumble.model.PrefixForm;
import asia.fourtitude.interviewq.jumble.model.ScrambleForm;
import asia.fourtitude.interviewq.jumble.model.SearchForm;
import asia.fourtitude.interviewq.jumble.model.SubWordsForm;

@Controller
@RequestMapping(path = "/")
public class RootController {
	private final JumbleEngine jumbleEngine;

	public RootController(JumbleEngine jumbleEngine) {
		this.jumbleEngine = jumbleEngine;
	}

	@GetMapping
	public String index(Model model) {
		model.addAttribute("timeNow", ZonedDateTime.now());
		return "index";
	}

	@GetMapping("scramble")
	public String doGetScramble(Model model) {
		model.addAttribute("form", new ScrambleForm());
		return "scramble";
	}

	@PostMapping("scramble")
	public String doPostScramble(@ModelAttribute(name = "form") ScrambleForm form, BindingResult bindingResult, Model model) {
		/*
		 * TODO: a) Validate the input `form` b) To call JumbleEngine#scramble() c)
		 * Presentation page to show the result d) Must pass the corresponding unit
		 * tests
		 */
		
		try {
			form.setScramble(jumbleEngine.scramble(form.getWord()));
			model.addAttribute("isError", false);
		} catch (Exception e) {
			model.addAttribute("isError", true);
			model.addAttribute("errorMessage", e.getMessage());
		}
		
		model.addAttribute("form", form);
		return "scramble";
	}

	@GetMapping("palindrome")
	public String doGetPalindrome(Model model) {
		model.addAttribute("words", this.jumbleEngine.retrievePalindromeWords());
		return "palindrome";
	}

	@GetMapping("exists")
	public String doGetExists(Model model) {
		model.addAttribute("form", new ExistsForm());
		return "exists";
	}

	@PostMapping("exists")
	public String doPostExists(@ModelAttribute(name = "form") ExistsForm form, BindingResult bindingResult,
			Model model) {
		/*
		 * TODO: a) Validate the input `form` b) To call JumbleEngine#exists() c)
		 * Presentation page to show the result d) Must pass the corresponding unit
		 * tests
		 */
		
		form.setExists(jumbleEngine.exists(form.getWord()));
		model.addAttribute("form", form);
		return "exists";
	}

	@GetMapping("prefix")
	public String doGetPrefix(Model model) {
		model.addAttribute("form", new PrefixForm());
		return "prefix";
	}

	@PostMapping("prefix")
	public String doPostPrefix(@ModelAttribute(name = "form") PrefixForm form, BindingResult bindingResult,
			Model model) {
		/*
		 * TODO: a) Validate the input `form` b) To call
		 * JumbleEngine#wordsMatchingPrefix() c) Presentation page to show the result d)
		 * Must pass the corresponding unit tests
		 */

		form.setWords(jumbleEngine.wordsMatchingPrefix(form.getPrefix()));
		model.addAttribute("form", form);
		return "prefix";
	}

	@GetMapping("search")
	public String doGetSearch(Model model) {
		model.addAttribute("form", new SearchForm());
		return "search";
	}

	@PostMapping("search")
	public String doPostSearch(@ModelAttribute(name = "form") SearchForm form, BindingResult bindingResult,
			Model model) {
		/*
		 * TODO: a) Validate the input `form` b) Show the fields error accordingly:
		 * "Invalid startChar", "Invalid endChar", "Invalid length". c) To call
		 * JumbleEngine#searchWords() d) Presentation page to show the result e) Must
		 * pass the corresponding unit tests
		 */
		
		Character startChar = null;
		Character endChar = null;
		
		if(StringUtils.isNotBlank(form.getStartChar())) {
			startChar = form.getStartChar().trim().toCharArray()[0];
		}
		
		if(StringUtils.isNotBlank(form.getEndChar())) {
			endChar = form.getEndChar().trim().toCharArray()[0];
		}

		try {
			form.setWords(jumbleEngine.searchWords(startChar, endChar, form.getLength()));
			model.addAttribute("isError", false);
		} catch (Exception e) {
			model.addAttribute("isError", true);
			model.addAttribute("errorMessage", e.getMessage());
		}
		
		model.addAttribute("form", form);
		return "search";
	}

	@GetMapping("subWords")
	public String goGetSubWords(Model model) {
		model.addAttribute("form", new SubWordsForm());
		return "subWords";
	}

	@PostMapping("subWords")
	public String doPostSubWords(@ModelAttribute(name = "form") SubWordsForm form, BindingResult bindingResult,
			Model model) {
		/*
		 * TODO: a) Validate the input `form` b) To call JumbleEngine#generateSubWords()
		 * c) Presentation page to show the result d) Must pass the corresponding unit
		 * tests
		 */
		
		form.setWords(jumbleEngine.generateSubWords(form.getWord(), form.getMinLength()));
		model.addAttribute("form", form);
		return "subWords";
	}

}
