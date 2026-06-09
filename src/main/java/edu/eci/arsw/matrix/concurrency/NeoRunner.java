package edu.eci.arsw.matrix.concurrency;

import edu.eci.arsw.matrix.engine.BFS;
import edu.eci.arsw.matrix.engine.GameState;
import edu.eci.arsw.matrix.model.Matrix;
import edu.eci.arsw.matrix.model.Position;

import java.util.HashSet;
import java.util.Set;

public class NeoRunner implements Runnable {

    private static final int TICK_MS = 600;

    private final Matrix matrix;
    private final PauseControl pauseControl;

    public NeoRunner(Matrix matrix, PauseControl pauseControl) {
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
                    matrix.getNeo().getPosition(),
                    matrix.getPhones(),
                    blocked,
                    matrix.getSize()
                );

                if (next != null) {
                    matrix.moveNeo(next);
                }

                Thread.sleep(TICK_MS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
