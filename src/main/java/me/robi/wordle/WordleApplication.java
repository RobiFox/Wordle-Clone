package me.robi.wordle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WordleApplication {
	@Autowired
	public WordsHolder wordsHolder;

	public static void main(String[] args) {
		//System.out.println("WordsHolder: " + wordsHolder);
		SpringApplication.run(WordleApplication.class, args);
	}
}
