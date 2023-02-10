package me.robi.wordle.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import me.robi.wordle.RepositoryHolder;
import me.robi.wordle.WordsHolder;
import me.robi.wordle.entities.UserEntity;
import me.robi.wordle.repositories.UserRepository;
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

    public static final int MAXIMUM_TRIES_VALUE = 5;

    @Autowired
    public WordsHolder wordsHolder;

    @Autowired
    public RepositoryHolder repositoryHolder;

    @GetMapping("/get")
    public ResponseEntity getWord(HttpServletResponse response, @CookieValue(value = USER_ID, required = false) String userId) {
        if(!wordsHolder.initialized) {
            return new ResponseEntity(Collections.singletonMap("error", "Words List not initialized."), HttpStatus.BAD_GATEWAY);
        }
        UUID uuid;
        if(userId == null) {
            uuid = UUID.randomUUID();
            response.addCookie(permanentCookie(USER_ID, uuid.toString()));
        } else {
            uuid = UUID.fromString(userId);
        }
        if(repositoryHolder.userRepository.findById(uuid.toString()).isEmpty()) {
            repositoryHolder.userRepository.save(new UserEntity(uuid.toString()));
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/guess")
    public ResponseEntity guessWord(@CookieValue(value = USER_ID) String userId, @RequestParam(name = "word") String guess) {
        guess = guess.toLowerCase();
        UserEntity user = repositoryHolder.userRepository.findById(userId).orElse(null);
        if(user == null)
            return new ResponseEntity(Collections.singletonMap("error", "User not found"), HttpStatus.BAD_REQUEST);
        if(user.gameOver)
            return new ResponseEntity(Collections.singletonMap("error", "User already finished the game"), HttpStatus.BAD_REQUEST);
        if(guess.length() != 5)
            return new ResponseEntity(Collections.singletonMap("error", "Word Length must be 5."), HttpStatus.BAD_REQUEST);
        if(!wordsHolder.words.contains(guess))
            return new ResponseEntity(Collections.singletonMap("error", "Word must be a valid English word."), HttpStatus.BAD_REQUEST);

        Map<String, Object> responseBody = new HashMap<>();

        Map<Character, Integer> charCount = new HashMap<>();
        LetterStatus[] statuses = new LetterStatus[5];

        int correctLetters = 0;

        for(int i = 0; i < wordsHolder.wordOfTheDay.length(); i++) {
            char c = wordsHolder.wordOfTheDay.charAt(i);
            char guessCharacter = guess.charAt(i);
            charCount.putIfAbsent(c, 0);
            if (guessCharacter == c) {
                statuses[i] = LetterStatus.CORRECT;
                correctLetters++;
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
        user.tries++;
        if(user.tries > MAXIMUM_TRIES_VALUE || correctLetters >= wordsHolder.wordOfTheDay.length()) {
            user.gameOver = true;
        }
        repositoryHolder.userRepository.save(user);

        responseBody.put("wordStatus", statuses);
        responseBody.put("gameOver", user.gameOver);
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
