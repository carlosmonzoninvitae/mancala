package com.bol.mancala.persistence;

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

    public Match saveOrUpdate(Integer id, Match match) {
        return table.put(id, match);
    }

    public Match get(Integer id) {
        return table.get(id);
    }

    public Match delete(Match match) {
        return table.remove(match);
    }
}
