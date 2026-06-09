package edu.eci.arsw.matrix.concurrency;

import edu.eci.arsw.matrix.engine.BFS;
import edu.eci.arsw.matrix.engine.GameState;
import edu.eci.arsw.matrix.model.Agent;
import edu.eci.arsw.matrix.model.Matrix;
import edu.eci.arsw.matrix.model.Position;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AgentRunner implements Runnable {

    private static final int TICK_MS = 800;

    private final Agent agent;
    private final Matrix matrix;
    private final PauseControl pauseControl;

    public AgentRunner(Agent agent, Matrix matrix, PauseControl pauseControl) {
        this.agent = agent;
        this.matrix = matrix;
        this.pauseControl = pauseControl;
    }

    @Override
    public void run() {
        try {
            while (matrix.getState() == GameState.RUNNING) {
                pauseControl.checkPause();

                Set<Position> blocked = new HashSet<>(matrix.getObstacles());

                Position next = BFS.nextStep(
                    agent.getPosition(),
                    List.of(matrix.getNeo().getPosition()),
                    blocked,
                    matrix.getSize()
                );

                if (next != null) {
                    matrix.moveAgent(agent, next);
                }

                Thread.sleep(TICK_MS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
