package com.bol.mancala.model;

import java.util.Objects;

public class Player {
    private final Integer id;
    private final String name;
    private final Boolean goesFirst;

    public Player(Integer id, String name) {
        this.id = id;
        this.name = name;
        this.goesFirst = false;
    }

    public Player(Integer id, String name, Boolean goesFirst) {
        this.id = id;
        this.name = name;
        this.goesFirst = goesFirst;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getGoesFirst() {
        return goesFirst;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id.equals(player.id) && name.equals(player.name) && goesFirst.equals(player.goesFirst);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, goesFirst);
    }
}
