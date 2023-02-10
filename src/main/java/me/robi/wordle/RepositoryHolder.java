package me.robi.wordle;

import me.robi.wordle.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RepositoryHolder {
    @Autowired
    public UserRepository userRepository;
}
