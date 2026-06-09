package edu.eci.arsw.matrix.model;

import edu.eci.arsw.matrix.engine.GameState;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Matrix {

    private final int size;
    private final Neo neo;
    private final List<Agent> agents;
    private final Set<Position> obstacles;
    private final Set<Position> phones;
    private final AtomicReference<GameState> state = new AtomicReference<>(GameState.RUNNING);

    public Matrix(int size, int nAgents, int nObstacles, int nPhones) {
        this.size = size;
        this.obstacles = new HashSet<>();
        this.phones = new HashSet<>();
        this.agents = new ArrayList<>();

        Set<Position> taken = new HashSet<>();

        Position neoPos = randomFree(taken);
        taken.add(neoPos);
        this.neo = new Neo(neoPos);

        for (int i = 0; i < nAgents; i++) {
            Position p = randomFree(taken);
            taken.add(p);
            agents.add(new Agent(i, p));
        }

        for (int i = 0; i < nObstacles; i++) {
            Position p = randomFree(taken);
            taken.add(p);
            obstacles.add(p);
        }

        for (int i = 0; i < nPhones; i++) {
            Position p = randomFree(taken);
            taken.add(p);
            phones.add(p);
        }
    }

    private Position randomFree(Set<Position> taken) {
        Random rng = new Random();
        Position p;
        do {
            p = new Position(rng.nextInt(size), rng.nextInt(size));
        } while (taken.contains(p));
        return p;
    }

    public synchronized void moveNeo(Position dest) {
        if (state.get() != GameState.RUNNING) return;
        neo.setPosition(dest);

        if (phones.contains(dest)) {
            state.set(GameState.NEO_WINS);
            return;
        }
        for (Agent a : agents) {
            if (a.getPosition().equals(dest)) {
                state.set(GameState.AGENT_WINS);
                return;
            }
        }
    }

    public synchronized void moveAgent(Agent agent, Position dest) {
        if (state.get() != GameState.RUNNING) return;
        agent.setPosition(dest);

        if (dest.equals(neo.getPosition())) {
            state.set(GameState.AGENT_WINS);
        }
    }

    public int getSize() {
        return size;
    }

    public Neo getNeo() {
        return neo;
    }

    public List<Agent> getAgents() {
        return Collections.unmodifiableList(agents);
    }

    public synchronized Set<Position> getObstacles() {
        return new HashSet<>(obstacles);
    }

    public synchronized Set<Position> getPhones() {
        return new HashSet<>(phones);
    }

    public GameState getState() {
        return state.get();
    }

    public synchronized Snapshot snapshot() {
        List<Position> agentPositions = new ArrayList<>();
        for (Agent a : agents) agentPositions.add(a.getPosition());
        return new Snapshot(
            neo.getPosition(),
            Collections.unmodifiableList(agentPositions),
            new HashSet<>(obstacles),
            new HashSet<>(phones),
            state.get()
        );
    }

    public record Snapshot(
        Position neo,
        List<Position> agents,
        Set<Position> obstacles,
        Set<Position> phones,
        GameState state
    ) {}
}
