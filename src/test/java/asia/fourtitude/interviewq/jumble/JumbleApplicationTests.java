package asia.fourtitude.interviewq.jumble;

import java.util.Collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JumbleApplicationTests extends TestConfig {
    @Test
    void testScramble() {
    	String scrambledWord = jumbleEngine().scramble("apple");
        Assertions.assertTrue(!"apple".equals(scrambledWord));
    }

    @Test
    void testRetrievePalindromeWords() {
    	Collection<String> palindromeWords = jumbleEngine().retrievePalindromeWords();
        Assertions.assertTrue(!palindromeWords.isEmpty());
    }
    
    @Test
    void testPickOneRandomWord() {
    	String randomWord = jumbleEngine().pickOneRandomWord(3);
        Assertions.assertTrue(randomWord != null);
    }
    
    @Test
    void testExists() {
        Assertions.assertTrue(jumbleEngine().exists("about"));
    }
    
    @Test
    void testWordsMatchingPrefix() {
        Assertions.assertTrue(!jumbleEngine().wordsMatchingPrefix("BoY").isEmpty());
    }
    
    @Test
    void testSearchWords() {
        Assertions.assertTrue(!jumbleEngine().searchWords('a', 'e', 5).isEmpty());
    }
    
    @Test
    void testGenerateSubWords() {
        Assertions.assertTrue(!jumbleEngine().generateSubWords("yellow", 3).isEmpty());
    }
}
