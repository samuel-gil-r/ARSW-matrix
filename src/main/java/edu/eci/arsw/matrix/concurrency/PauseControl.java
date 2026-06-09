package edu.eci.arsw.matrix.concurrency;

public class PauseControl {

    private volatile boolean paused = false;

    public synchronized void checkPause() throws InterruptedException {
        while (paused) wait();
    }

    public synchronized void pause() {
        paused = true;
    }

    public synchronized void resume() {
        paused = false;
        notifyAll();
    }

    public boolean isPaused() {
        return paused;
    }
}
