package edu.eci.arsw.matrix.model;

public abstract class Entity {

    private volatile Position position;
    private final EntityType type;

    protected Entity(Position position, EntityType type) {
        this.position = position;
        this.type = type;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public EntityType getType() {
        return type;
    }
}
