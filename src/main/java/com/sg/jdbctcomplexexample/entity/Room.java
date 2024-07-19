package com.sg.jdbctcomplexexample.entity;

import java.util.Objects;

/**
 *
 * @author kylerudy
 */
public class Room {
    int id;
    String name;
    String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return id == room.id && Objects.equals(name, room.name) && Objects.equals(description, room.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }
}
