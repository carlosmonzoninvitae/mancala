package com.bol.mancala.persistence;

import com.bol.mancala.exception.MyResourceAlreadyExistsException;
import com.bol.mancala.exception.MyResourceNotFoundException;
import com.bol.mancala.model.Match;
import org.springframework.stereotype.Component;

import java.util.Hashtable;
import java.util.Map;

@Component
public class GameTable {
    private static GameTable single_instance = null;

    // Decl;aring a variable of type String
    private Map<Integer, Match> table;

    // Constructor
    // Here we will be creating private constructor
    // restricted to this class itself
    private GameTable()
    {
        table = new Hashtable<>();
    }

    // Static method
    // Static method to create instance of Singleton class
    public static GameTable getInstance()
    {
        if (single_instance == null)
            single_instance = new GameTable();

        return single_instance;
    }

    public Match save(Match match) {
        int matchSessionCode = match.hashCode();
        //Hash table's save returns old value or null if there wasn't a previous one.
        if (table.put(matchSessionCode, match) != null)
            throw new MyResourceAlreadyExistsException("Match already exists");

        match.setId(matchSessionCode);
        return match;
    }

    public Match update(Match match) {
        //Hash table's put returns old value or null if there wasn't a previous one.
        if (table.put(match.hashCode(), match) == null)
            throw new MyResourceNotFoundException("Match not found");

        return match;
    }

    public Match get(Integer id) {
        Match match = table.get(id);
        if (match==null)
            return null;

        return new Match(match);
    }

    public Match delete(Match match) {
        return table.remove(match.hashCode());
    }
}
