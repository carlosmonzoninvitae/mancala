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

    public Match saveOrUpdate(Match match) {
        return gameTable.saveOrUpdate(match.hashCode(), match);
    }

    public void delete(Match match) {
        gameTable.delete(match);
    }
}
