package edu.eci.arsw.matrix.model;

public class Agent extends Entity {

    private final int id;

    public Agent(int id, Position position) {
        super(position, EntityType.AGENT);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
