package edu.eci.arsw.matrix.engine;

import javax.swing.SwingUtilities;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameClock {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "game-clock");
        t.setDaemon(true);
        return t;
    });
    private final Runnable repaintTarget;

    public GameClock(Runnable repaintTarget) {
        this.repaintTarget = repaintTarget;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(
            () -> SwingUtilities.invokeLater(repaintTarget),
            0, 16, TimeUnit.MILLISECONDS
        );
    }

    public void stop() {
        scheduler.shutdownNow();
    }
}
