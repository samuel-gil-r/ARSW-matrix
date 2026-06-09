package edu.eci.arsw.matrix.model;

public record Position(int row, int col) {

    public boolean isValid(int n) {
        return row >= 0 && row < n && col >= 0 && col < n;
    }

    public Position[] neighbors(int n) {
        Position[] candidates = {
            new Position(row - 1, col),
            new Position(row + 1, col),
            new Position(row, col - 1),
            new Position(row, col + 1)
        };
        int count = 0;
        for (Position p : candidates) {
            if (p.isValid(n)) count++;
        }
        Position[] result = new Position[count];
        int i = 0;
        for (Position p : candidates) {
            if (p.isValid(n)) result[i++] = p;
        }
        return result;
    }
}
