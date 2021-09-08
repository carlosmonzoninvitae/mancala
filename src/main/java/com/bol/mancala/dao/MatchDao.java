package com.bol.mancala.dao;

import com.bol.mancala.model.Match;
import com.bol.mancala.persistence.GameTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MatchDao {

    @Autowired
    private GameTable gameTable;

    public Optional<Match> get(Integer id) {
        return Optional.ofNullable(gameTable.get(id));
    }

    public Match save(Match match) {
        return gameTable.save(match);
    }

    public Match update(Match match) {
        return gameTable.update(match);
    }

    public Match delete(Match match) {
        return gameTable.delete(match);
    }
}
