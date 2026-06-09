package edu.eci.arsw.matrix.app;

import edu.eci.arsw.matrix.concurrency.AgentRunner;
import edu.eci.arsw.matrix.concurrency.NeoRunner;
import edu.eci.arsw.matrix.concurrency.PauseControl;
import edu.eci.arsw.matrix.model.Agent;
import edu.eci.arsw.matrix.model.Matrix;
import edu.eci.arsw.matrix.ui.MatrixApp;

import javax.swing.SwingUtilities;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.print("Ingrese el tamano de la matriz (N): ");
        int n = readPositive(sc, 3);

        System.out.print("Ingrese el numero de Agentes (minimo 1): ");
        int nAgents = Math.max(1, readPositive(sc, 1));

        System.out.print("Ingrese el numero de Obstaculos: ");
        int nObstacles = readNonNegative(sc);

        System.out.print("Ingrese el numero de Telefonos (minimo 1): ");
        int nPhones = Math.max(1, readPositive(sc, 1));

        int totalNeeded = 1 + nAgents + nObstacles + nPhones;
        int available = n * n;
        if (totalNeeded > available) {
            System.out.println("Error: se necesitan " + totalNeeded + " celdas pero la matriz solo tiene " + available);
            System.out.println("Reduce los valores e intenta de nuevo.");
            return;
        }

        Matrix matrix = new Matrix(n, nAgents, nObstacles, nPhones);
        PauseControl pauseControl = new PauseControl();

        var executor = Executors.newVirtualThreadPerTaskExecutor();
        executor.submit(new NeoRunner(matrix, pauseControl));
        for (Agent agent : matrix.getAgents()) {
            executor.submit(new AgentRunner(agent, matrix, pauseControl));
        }

        SwingUtilities.invokeLater(() -> {
            MatrixApp app = new MatrixApp(matrix, pauseControl);
            app.launch();
        });
    }

    private static int readPositive(Scanner sc, int min) {
        int value;
        do {
            while (!sc.hasNextInt()) {
                System.out.print("Ingrese un numero entero: ");
                sc.next();
            }
            value = sc.nextInt();
            if (value < min) System.out.print("Debe ser al menos " + min + ": ");
        } while (value < min);
        return value;
    }

    private static int readNonNegative(Scanner sc) {
        int value;
        do {
            while (!sc.hasNextInt()) {
                System.out.print("Ingrese un numero entero: ");
                sc.next();
            }
            value = sc.nextInt();
            if (value < 0) System.out.print("Debe ser 0 o mayor: ");
        } while (value < 0);
        return value;
    }
}
