package edu.eci.arsw.matrix.ui;

import edu.eci.arsw.matrix.concurrency.PauseControl;
import edu.eci.arsw.matrix.engine.GameClock;
import edu.eci.arsw.matrix.engine.GameState;
import edu.eci.arsw.matrix.model.Matrix;
import edu.eci.arsw.matrix.model.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.List;
import java.util.Set;

public class MatrixApp extends JFrame {

    private final Matrix matrix;
    private final PauseControl pauseControl;
    private final int cell;
    private final GamePanel gamePanel;
    private final GameClock clock;
    private final JButton pauseBtn;
    private final JLabel statusLabel;
    private boolean gameOverShown = false;

    public MatrixApp(Matrix matrix, PauseControl pauseControl) {
        super("Neo vs Agentes — Matriz " + matrix.getSize() + "x" + matrix.getSize());
        this.matrix = matrix;
        this.pauseControl = pauseControl;
        this.cell = Math.max(8, Math.min(60, 800 / matrix.getSize()));
        this.gamePanel = new GamePanel();
        this.clock = new GameClock(this::tick);

        this.pauseBtn = new JButton("⏸  Pausar");
        pauseBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        pauseBtn.setFocusPainted(false);
        pauseBtn.addActionListener(e -> togglePause());

        this.statusLabel = new JLabel("En ejecucion", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        statusLabel.setForeground(new Color(40, 140, 40));

        JPanel south = new JPanel(new BorderLayout(10, 0));
        south.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        south.add(pauseBtn, BorderLayout.WEST);
        south.add(statusLabel, BorderLayout.CENTER);

        add(gamePanel, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void togglePause() {
        if (matrix.getState() != GameState.RUNNING) return;
        if (pauseControl.isPaused()) {
            pauseControl.resume();
            pauseBtn.setText("⏸  Pausar");
            statusLabel.setText("En ejecucion");
            statusLabel.setForeground(new Color(40, 140, 40));
        } else {
            pauseControl.pause();
            pauseBtn.setText("▶  Reanudar");
            statusLabel.setText("PAUSADO");
            statusLabel.setForeground(new Color(180, 120, 0));
        }
    }

    private void tick() {
        gamePanel.repaint();
        GameState state = matrix.getState();
        if (!gameOverShown && state != GameState.RUNNING) {
            gameOverShown = true;
            clock.stop();
            pauseBtn.setEnabled(false);
            if (state == GameState.NEO_WINS) {
                statusLabel.setText("Neo llego al telefono — NEO GANA!");
                statusLabel.setForeground(new Color(0, 100, 200));
            } else {
                statusLabel.setText("Un Agente capturo a Neo — AGENTES GANAN!");
                statusLabel.setForeground(new Color(180, 0, 0));
            }
            String msg = state == GameState.NEO_WINS
                ? "Neo llego al telefono.\nNEO GANA!"
                : "Un Agente capturo a Neo.\nAGENTES GANAN!";
            JOptionPane.showMessageDialog(this, msg, "Fin del juego", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void launch() {
        setVisible(true);
        clock.start();
    }

    private class GamePanel extends JPanel {

        GamePanel() {
            int px = matrix.getSize() * cell + 1;
            setPreferredSize(new Dimension(px, px));
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Matrix.Snapshot snap = matrix.snapshot();
            int n = matrix.getSize();

            drawGrid(g2, n);
            drawObstacles(g2, snap.obstacles());
            drawPhones(g2, snap.phones());
            drawAgents(g2, snap.agents());
            drawNeo(g2, snap.neo());

            if (pauseControl.isPaused() && snap.state() == GameState.RUNNING) {
                drawPauseOverlay(g2, n);
            }
        }

        private void drawGrid(Graphics2D g2, int n) {
            g2.setColor(new Color(210, 210, 210));
            for (int i = 0; i <= n; i++) {
                g2.drawLine(i * cell, 0, i * cell, n * cell);
                g2.drawLine(0, i * cell, n * cell, i * cell);
            }
        }

        private void drawObstacles(Graphics2D g2, Set<Position> obstacles) {
            int pad = Math.max(1, cell / 12);
            g2.setColor(new Color(80, 80, 80));
            for (Position p : obstacles) {
                g2.fillRect(
                    p.col() * cell + pad,
                    p.row() * cell + pad,
                    cell - pad * 2,
                    cell - pad * 2
                );
            }
        }

        private void drawPhones(Graphics2D g2, Set<Position> phones) {
            int pad = Math.max(1, cell / 12);
            int d = cell - pad * 2;
            for (Position p : phones) {
                int x = p.col() * cell + pad;
                int y = p.row() * cell + pad;

                g2.setColor(new Color(0, 180, 60));
                g2.fill(new Ellipse2D.Double(x, y, d, d));
                g2.setColor(new Color(0, 120, 40));
                g2.setStroke(new BasicStroke(Math.max(1f, cell / 20f)));
                g2.draw(new Ellipse2D.Double(x, y, d, d));
                g2.setStroke(new BasicStroke(1));

                if (cell >= 20) {
                    g2.setColor(Color.WHITE);
                    int cx = p.col() * cell + cell / 2;
                    int cy = p.row() * cell + cell / 2;
                    int arm = Math.max(3, cell / 6);
                    g2.setStroke(new BasicStroke(Math.max(1f, cell / 15f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(cx, cy - arm, cx, cy + arm);
                    g2.drawLine(cx - arm, cy + arm / 2, cx + arm, cy + arm / 2);
                    g2.setStroke(new BasicStroke(1));
                }
            }
        }

        private void drawAgents(Graphics2D g2, List<Position> agents) {
            int pad = Math.max(1, cell / 12);
            int half = cell / 2 - pad - Math.max(1, cell / 15);
            for (Position p : agents) {
                int cx = p.col() * cell + cell / 2;
                int cy = p.row() * cell + cell / 2;

                Polygon triangle = new Polygon(
                    new int[]{cx, cx - half, cx + half},
                    new int[]{cy - half, cy + half, cy + half},
                    3
                );
                g2.setColor(new Color(200, 30, 30));
                g2.fillPolygon(triangle);
                g2.setColor(new Color(120, 0, 0));
                g2.setStroke(new BasicStroke(Math.max(1f, cell / 20f)));
                g2.drawPolygon(triangle);
                g2.setStroke(new BasicStroke(1));
            }
        }

        private void drawNeo(Graphics2D g2, Position neo) {
            int pad = Math.max(1, cell / 12);
            int d = cell - pad * 2;
            int x = neo.col() * cell + pad;
            int y = neo.row() * cell + pad;

            g2.setColor(new Color(30, 100, 220));
            g2.fill(new Ellipse2D.Double(x, y, d, d));
            g2.setColor(new Color(10, 50, 150));
            g2.setStroke(new BasicStroke(Math.max(1f, cell / 20f)));
            g2.draw(new Ellipse2D.Double(x, y, d, d));
            g2.setStroke(new BasicStroke(1));

            if (cell >= 20) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(8, cell / 3)));
                FontMetrics fm = g2.getFontMetrics();
                String label = "N";
                g2.drawString(label,
                    x + (d - fm.stringWidth(label)) / 2,
                    y + (d + fm.getAscent() - fm.getDescent()) / 2
                );
            }
        }

        private void drawPauseOverlay(Graphics2D g2, int n) {
            int w = n * cell;
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillRect(0, 0, w, w);
            g2.setColor(new Color(255, 255, 255, 200));
            int fontSize = Math.max(14, Math.min(36, cell * 2));
            g2.setFont(new Font("SansSerif", Font.BOLD, fontSize));
            FontMetrics fm = g2.getFontMetrics();
            String text = "PAUSADO";
            g2.drawString(text, (w - fm.stringWidth(text)) / 2, w / 2);
        }
    }
}
