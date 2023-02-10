package me.robi.wordle.entities;

import jakarta.persistence.*;

@Entity
public class UserEntity {
    @Id
    public String uuid; // H2 doesn't seem to be working with class UUID?

    public int tries;
    public boolean gameOver;

    protected UserEntity() { }

    public UserEntity(String uuid) {
        this.uuid = uuid;
        this.tries = 0;
        this.gameOver = false;
    }

    public int getTries() {
        return tries;
    }

    public void setTries(int tries) {
        this.tries = tries;
    }
}
