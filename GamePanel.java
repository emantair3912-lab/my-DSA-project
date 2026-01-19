// GamePanel.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class GamePanel extends JPanel {
    private MazeSolver mainFrame;
    private MazeGrid mazeGrid;
    private Difficulty difficulty;
    private boolean autoMode = false;
    private javax.swing.Timer autoTimer;
    private int autoIndex = 0;
    private List<int[]> autoPath = new ArrayList<>();
    private JLabel stepsLabel, timerLabel, difficultyLabel;
    private javax.swing.Timer gameTimer;
    private int elapsedSeconds = 0;

    public GamePanel(MazeSolver mainFrame, MazeGrid mazeGrid, boolean autoMode) {
        this.mainFrame = mainFrame;
        this.mazeGrid = mazeGrid;
        this.difficulty = mazeGrid.getDifficulty();
        this.autoMode = autoMode;
        this.elapsedSeconds = 0;

        setLayout(null);
        setFocusable(true);
        setBackground(Color.BLACK);

        // Control panel at top
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(new Color(20, 20, 40));
        controlPanel.setBounds(0, 0, 1000, 60);

        // Difficulty label
        difficultyLabel = new JLabel("DIFFICULTY: " + difficulty.name());
        difficultyLabel.setForeground(Color.MAGENTA);
        difficultyLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        controlPanel.add(difficultyLabel);

        // Timer label
        timerLabel = new JLabel("TIME: 00:00");
        timerLabel.setForeground(Color.CYAN);
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        controlPanel.add(timerLabel);

        // Steps label
        stepsLabel = new JLabel("MOVES: 0");
        stepsLabel.setForeground(Color.GREEN);
        stepsLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        controlPanel.add(stepsLabel);

        // Auto solve button
        JButton autoBtn = createControlButton("AUTO SOLVE");
        autoBtn.addActionListener(e -> {
            if (!this.autoMode) {
                startAutoMode();
                autoBtn.setEnabled(false);
            }
        });
        controlPanel.add(autoBtn);

        // Home button
        JButton homeBtn = createControlButton("HOME");
        homeBtn.addActionListener(e -> {
            stopTimers();
            mainFrame.returnToHome();
        });
        controlPanel.add(homeBtn);

        add(controlPanel);

        // Start game timer
        gameTimer = new javax.swing.Timer(1000, e -> {
            elapsedSeconds++;
            updateTimerLabel();
        });
        gameTimer.start();

        // If auto mode selected from home, start it
        if (this.autoMode) {
            startAutoMode();
        }

        // Key listener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!GamePanel.this.autoMode) handleKeyPress(e);
            }
        });
    }

    private JButton createControlButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Monospaced", Font.BOLD, 16));
        btn.setForeground(Color.GREEN);
        btn.setBackground(new Color(0, 0, 0));
        btn.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void updateTimerLabel() {
        int mins = elapsedSeconds / 60;
        int secs = elapsedSeconds % 60;
        timerLabel.setText(String.format("TIME: %02d:%02d", mins, secs));
    }

    private void stopTimers() {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
        if (autoTimer != null && autoTimer.isRunning()) {
            autoTimer.stop();
        }
    }

    private void startAutoMode() {
        this.autoMode = true;

        // Get the shortest path from the maze grid
        autoPath = mazeGrid.getShortestPath();

        if (autoPath == null || autoPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No path found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        autoIndex = 0;

        // Timer to animate through the path
        autoTimer = new javax.swing.Timer(150, e -> {
            if (autoIndex < autoPath.size()) {
                int[] pos = autoPath.get(autoIndex);
                // pos[0] is x, pos[1] is y
                mazeGrid.setBallPosition(pos[0], pos[1]);

                // Update steps display
                stepsLabel.setText("MOVES: " + mazeGrid.getSteps());

                autoIndex++;
                repaint();

                // Check if we've reached the end
                if (mazeGrid.isFinished()) {
                    autoTimer.stop();
                    stopTimers();
                    // Delay slightly before showing finish dialog
                    javax.swing.Timer delayTimer = new javax.swing.Timer(300, evt -> {
                        showFinishMessage();
                    });
                    delayTimer.setRepeats(false);
                    delayTimer.start();
                }
            } else {
                // Path completed
                autoTimer.stop();
                if (mazeGrid.isFinished()) {
                    stopTimers();
                    showFinishMessage();
                }
            }
        });
        autoTimer.start();
    }

    private void handleKeyPress(KeyEvent e) {
        boolean moved = false;
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) moved = mazeGrid.moveUp();
        else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) moved = mazeGrid.moveDown();
        else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) moved = mazeGrid.moveLeft();
        else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) moved = mazeGrid.moveRight();

        if (moved) {
            stepsLabel.setText("MOVES: " + mazeGrid.getSteps());
            repaint();
        }

        if (mazeGrid.isFinished()) {
            stopTimers();
            showFinishMessage();
        }
    }

    private void showFinishMessage() {
        stopTimers();

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "VICTORY!", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(10, 10, 30));
        panel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));

        JLabel titleLabel = new JLabel("MAZE COMPLETED!");
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 28));
        titleLabel.setForeground(Color.GREEN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel diffLabel = new JLabel("DIFFICULTY: " + difficulty.name());
        diffLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        diffLabel.setForeground(Color.MAGENTA);
        diffLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel timeLabel = new JLabel(timerLabel.getText());
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 22));
        timeLabel.setForeground(Color.CYAN);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel movesLabel = new JLabel(stepsLabel.getText());
        movesLabel.setFont(new Font("Monospaced", Font.BOLD, 22));
        movesLabel.setForeground(Color.YELLOW);
        movesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton homeBtn = new JButton("RETURN HOME");
        homeBtn.setFont(new Font("Monospaced", Font.BOLD, 18));
        homeBtn.setForeground(Color.GREEN);
        homeBtn.setBackground(Color.BLACK);
        homeBtn.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
        homeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        homeBtn.addActionListener(e -> {
            dialog.dispose();
            mainFrame.returnToHome();
        });

        panel.add(Box.createVerticalStrut(40));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(diffLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(timeLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(movesLabel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(homeBtn);
        panel.add(Box.createVerticalStrut(20));

        dialog.add(panel);
        dialog.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dark gradient background
        GradientPaint bg = new GradientPaint(0, 0, new Color(10, 10, 30),
                0, getHeight(), new Color(30, 10, 50));
        g2.setPaint(bg);
        g2.fillRect(0, 0, getWidth(), getHeight());

        drawMaze(g2);
        drawBall(g2);
    }

    private void drawMaze(Graphics2D g2) {
        int[][] maze = mazeGrid.getMaze();
        int rows = maze.length;
        int cols = maze[0].length;

        // Calculate cell size to fit on screen
        int maxWidth = 950;
        int maxHeight = 580;
        int cellSize = Math.min(maxWidth / cols, maxHeight / rows);
        cellSize = Math.max(cellSize, 15); // Minimum 15px
        cellSize = Math.min(cellSize, 40); // Maximum 40px

        int offsetX = (getWidth() - cols * cellSize) / 2;
        int offsetY = 100;

        // Draw glow border
        g2.setColor(new Color(0, 255, 0, 30));
        g2.fillRoundRect(offsetX - 10, offsetY - 10,
                cols * cellSize + 20, rows * cellSize + 20, 15, 15);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int px = x * cellSize + offsetX;
                int py = y * cellSize + offsetY;

                if (maze[y][x] == 1) {
                    // Wall (value = 1)
                    g2.setColor(new Color(0, 180, 0));
                    g2.fillRect(px, py, cellSize, cellSize);
                    g2.setColor(new Color(0, 100, 0));
                    g2.drawRect(px, py, cellSize, cellSize);
                } else {
                    // Path (value = 0, 2, or 3)
                    g2.setColor(new Color(0, 30, 0));
                    g2.fillRect(px, py, cellSize, cellSize);
                    g2.setColor(new Color(0, 50, 0));
                    g2.drawRect(px, py, cellSize, cellSize);
                }

                // Start marker
                if (maze[y][x] == 2) {
                    g2.setColor(Color.CYAN);
                    g2.fillOval(px + 3, py + 3, cellSize - 6, cellSize - 6);
                }

                // End marker
                if (maze[y][x] == 3) {
                    g2.setColor(Color.RED);
                    g2.fillOval(px + 3, py + 3, cellSize - 6, cellSize - 6);
                    g2.setColor(Color.YELLOW);
                    g2.drawOval(px + 2, py + 2, cellSize - 4, cellSize - 4);
                }
            }
        }

        // Draw auto path if active
        if (autoMode && autoPath != null && autoPath.size() > 1) {
            g2.setColor(new Color(255, 255, 0, 100));
            g2.setStroke(new BasicStroke(Math.max(2, cellSize / 8)));

            // Draw the path up to current position
            for (int i = 0; i < Math.min(autoIndex, autoPath.size() - 1); i++) {
                int[] p1 = autoPath.get(i);
                int[] p2 = autoPath.get(i + 1);

                // Calculate pixel positions for the center of each cell
                int x1 = p1[0] * cellSize + offsetX + cellSize / 2;
                int y1 = p1[1] * cellSize + offsetY + cellSize / 2;
                int x2 = p2[0] * cellSize + offsetX + cellSize / 2;
                int y2 = p2[1] * cellSize + offsetY + cellSize / 2;

                g2.drawLine(x1, y1, x2, y2);
            }
        }
    }

    private void drawBall(Graphics2D g2) {
        int[][] maze = mazeGrid.getMaze();
        int rows = maze.length;
        int cols = maze[0].length;

        // Calculate cell size same as in drawMaze
        int maxWidth = 950;
        int maxHeight = 580;
        int cellSize = Math.min(maxWidth / cols, maxHeight / rows);
        cellSize = Math.max(cellSize, 15);
        cellSize = Math.min(cellSize, 40);

        int offsetX = (getWidth() - cols * cellSize) / 2;
        int offsetY = 100;

        // Get ball position in grid coordinates
        int ballGridX = mazeGrid.getBallX();
        int ballGridY = mazeGrid.getBallY();

        // Convert to pixel coordinates (center of cell)
        int ballX = ballGridX * cellSize + offsetX + cellSize / 2;
        int ballY = ballGridY * cellSize + offsetY + cellSize / 2;
        int ballSize = Math.max(cellSize - 10, 12);

        // Glow effect
        for (int i = 3; i > 0; i--) {
            g2.setColor(new Color(255, 255, 0, 30 * i));
            g2.fillOval(ballX - ballSize / 2 - i * 2, ballY - ballSize / 2 - i * 2,
                    ballSize + i * 4, ballSize + i * 4);
        }

        // Ball with gradient
        GradientPaint gp = new GradientPaint(ballX - ballSize / 2, ballY - ballSize / 2,
                Color.YELLOW,
                ballX + ballSize / 2, ballY + ballSize / 2,
                Color.YELLOW);
        g2.setPaint(gp);
        g2.fillOval(ballX - ballSize / 2, ballY - ballSize / 2, ballSize, ballSize);

        // Eyes
        if (cellSize > 20) {
            g2.setColor(Color.BLACK);
            int eyeSize = Math.max(ballSize / 6, 3);
            g2.fillOval(ballX - ballSize / 4, ballY - ballSize / 6, eyeSize, eyeSize);
            g2.fillOval(ballX + ballSize / 6, ballY - ballSize / 6, eyeSize, eyeSize);
        }
    }
}