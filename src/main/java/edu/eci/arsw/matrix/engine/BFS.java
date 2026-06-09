package edu.eci.arsw.matrix.engine;

import edu.eci.arsw.matrix.model.Position;

import java.util.*;

public final class BFS {

    private BFS() {}

    /**
     * Returns the next step from `from` toward the nearest target, avoiding obstacles.
     * Returns null if no path exists or targets is empty.
     */
    public static Position nextStep(Position from, Collection<Position> targets, Set<Position> blocked, int n) {
        if (targets == null || targets.isEmpty()) return null;

        Queue<Position> queue = new ArrayDeque<>();
        Map<Position, Position> parent = new HashMap<>();

        queue.add(from);
        parent.put(from, null);

        while (!queue.isEmpty()) {
            Position current = queue.poll();

            if (targets.contains(current) && !current.equals(from)) {
                return traceStep(parent, from, current);
            }

            for (Position neighbor : current.neighbors(n)) {
                if (!parent.containsKey(neighbor) && !blocked.contains(neighbor)) {
                    parent.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
        return null;
    }

    private static Position traceStep(Map<Position, Position> parent, Position from, Position goal) {
        Position current = goal;
        while (!parent.get(current).equals(from)) {
            current = parent.get(current);
        }
        return current;
    }
}
