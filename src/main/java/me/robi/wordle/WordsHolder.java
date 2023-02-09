package me.robi.wordle;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class WordsHolder {
    public List<String> words = new ArrayList<>();
    public boolean initialized = false;
    public String wordOfTheDay;

    public WordsHolder() {
        super();
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            is = new ClassPathResource("words.txt").getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            words.addAll(br.lines().toList());
            wordOfTheDay = words.get(ThreadLocalRandom.current().nextInt(words.size())); // TODO make it different everyday rather than every start
            initialized = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (is != null) is.close();
                if (isr != null) isr.close();
                if (br != null) br.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
