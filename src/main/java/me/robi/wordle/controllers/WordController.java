package me.robi.wordle.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import me.robi.wordle.WordsHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.util.*;

@RestController
@RequestMapping("/wordle")
public class WordController {
    public static final String USER_ID = "user-id";

    @Autowired
    public WordsHolder wordsHolder;

    @GetMapping("/get")
    public ResponseEntity getWord(HttpServletResponse response, @CookieValue(value = USER_ID, required = false) String userId) {
        if(!wordsHolder.initialized) {
            return new ResponseEntity(Collections.singletonMap("error", "Words List not initialized."), HttpStatus.BAD_GATEWAY);
        }
        if(userId == null) {
            response.addCookie(permanentCookie(USER_ID, UUID.randomUUID().toString()));
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/guess")
    public ResponseEntity guessWord(@CookieValue(value = USER_ID) String userId, @RequestParam(name = "word") String guess) {
        if(guess.length() != 5) {
            return new ResponseEntity(Collections.singletonMap("error", "Word Length must be 5."), HttpStatus.BAD_GATEWAY);
        }
        Map<String, Object> responseBody = new HashMap<>();

        Map<Character, Integer> charCount = new HashMap<>();
        LetterStatus[] statuses = new LetterStatus[5];
        // TODO check if word is valid english word
        for(int i = 0; i < wordsHolder.wordOfTheDay.length(); i++) {
            char c = wordsHolder.wordOfTheDay.charAt(i);
            char guessCharacter = guess.charAt(i);
            charCount.putIfAbsent(c, 0);
            if (guessCharacter == c) {
                statuses[i] = LetterStatus.CORRECT;
            } else {
                charCount.put(c, charCount.get(c) + 1);
            }
        }

        for(int i = 0; i < statuses.length; i++) {
            char guessCharacter = guess.charAt(i);
            int count = charCount.get(guessCharacter) != null ? charCount.get(guessCharacter) : 0;

            if(statuses[i] != null) continue;

            statuses[i] = count > 0 ? LetterStatus.CONTAINS : LetterStatus.NONE;
        }

        responseBody.put("wordStatus", statuses);

        return new ResponseEntity(responseBody, HttpStatus.OK);
    }

    private Cookie permanentCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(Integer.MAX_VALUE);
        return cookie;
    }
}

enum LetterStatus {
    NONE, CONTAINS, CORRECT
}
