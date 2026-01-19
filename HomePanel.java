
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HomePanel extends JPanel {
    private MazeSolver mainFrame;
    private int ballX, ballY;
    private int ballSize = 40;
    private int dx = 1, dy = 1;
    private int mazeStartX, mazeStartY;
    private int cellSize = 40;
    private int[][] miniMaze;
    private int animationCounter = 0;

    // Track mode selection
    private boolean isAutoMode = false;

    // Background panel
    private JPanel backgroundPanel;
    // Button panel (on top)
    private JPanel buttonPanel;

    public HomePanel(MazeSolver mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        mazeStartX = 400;
        mazeStartY = 220;

        // Initialize mini maze grid (6x6)
        miniMaze = new int[6][6];
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 6; x++) {
                miniMaze[y][x] = (x % 2 == 0 || y % 2 == 0) && !(x == 0 && y == 0) ? 1 : 0;
            }
        }

        ballX = 1;
        ballY = 1;

        // Create layered structure
        setupLayeredPanels();

        // Ball animation
        startBallAnimation();
    }

    private void setupLayeredPanels() {
        // Background panel with custom painting
        backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintBackground(g);
            }
        };
        backgroundPanel.setLayout(null);
        backgroundPanel.setOpaque(true);
        backgroundPanel.setBackground(Color.BLACK);

        // Button panel (transparent, on top)
        buttonPanel = new JPanel();
        buttonPanel.setLayout(null);
        buttonPanel.setOpaque(false);

        // Create buttons
        createButtons();

        // Use JLayeredPane to ensure proper z-ordering
        JLayeredPane layeredPane = new JLayeredPane();
        add(layeredPane, BorderLayout.CENTER);

        // Add background to bottom layer
        backgroundPanel.setBounds(0, 0, 2000, 2000);
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        // Add button panel to top layer
        buttonPanel.setBounds(0, 0, 2000, 2000);
        layeredPane.add(buttonPanel, JLayeredPane.PALETTE_LAYER);
    }

    private void createButtons() {
        System.out.println("Creating buttons...");

        // Mode selection buttons
        JButton autoBtn = createSimpleButton("AUTO PLAY", 300, 480, 180, 60);
        JButton manualBtn = createSimpleButton("MANUAL PLAY", 520, 480, 180, 60);

        manualBtn.setBackground(new Color(0, 100, 0));
        autoBtn.setBackground(Color.BLACK);

        // Mode button listeners
        autoBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("AUTO PLAY CLICKED!");
                isAutoMode = true;
                autoBtn.setBackground(new Color(0, 100, 0));
                manualBtn.setBackground(Color.BLACK);
                repaint();
            }
        });

        manualBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("MANUAL PLAY CLICKED!");
                isAutoMode = false;
                manualBtn.setBackground(new Color(0, 100, 0));
                autoBtn.setBackground(Color.BLACK);
                repaint();
            }
        });

        // Difficulty buttons
        int diffY = 570;
        int diffSpacing = 155;
        int startX = 200;

        JButton easyBtn = createSimpleButton("EASY", startX, diffY, 130, 55);
        JButton mediumBtn = createSimpleButton("MEDIUM", startX + diffSpacing, diffY, 130, 55);
        JButton hardBtn = createSimpleButton("HARD", startX + diffSpacing * 2, diffY, 130, 55);
        JButton expertBtn = createSimpleButton("EXPERT", startX + diffSpacing * 3, diffY, 130, 55);

        easyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("EASY CLICKED!");
                startGame(Difficulty.EASY);
            }
        });

        mediumBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("MEDIUM CLICKED!");
                startGame(Difficulty.MEDIUM);
            }
        });

        hardBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("HARD CLICKED!");
                startGame(Difficulty.HARD);
            }
        });

        expertBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("EXPERT CLICKED!");
                startGame(Difficulty.EXPERT);
            }
        });

        // Add buttons to button panel
        buttonPanel.add(autoBtn);
        buttonPanel.add(manualBtn);
        buttonPanel.add(easyBtn);
        buttonPanel.add(mediumBtn);
        buttonPanel.add(hardBtn);
        buttonPanel.add(expertBtn);

        System.out.println("Buttons created and added!");
    }

    private JButton createSimpleButton(String text, int x, int y, int w, int h) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        btn.setFont(new Font("Monospaced", Font.BOLD, 18));
        btn.setForeground(Color.GREEN);
        btn.setBackground(Color.BLACK);
        btn.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        // Add hover effect
        btn.addMouseListener(new MouseAdapter() {
            Color originalBg = btn.getBackground();

            @Override
            public void mouseEntered(MouseEvent e) {
                System.out.println("Mouse entered: " + text);
                btn.setBorder(BorderFactory.createLineBorder(Color.CYAN, 3));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("Mouse PRESSED: " + text);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("Mouse RELEASED: " + text);
            }
        });

        return btn;
    }

    // ========== FIXED startGame METHOD - THIS PREVENTS FREEZING ==========
    private void startGame(Difficulty difficulty) {
        System.out.println("========================================");
        System.out.println("STARTING GAME");
        System.out.println("Difficulty: " + difficulty);
        System.out.println("Auto Mode: " + isAutoMode);
        System.out.println("========================================");

        // Create loading dialog
        JDialog loadingDialog = new JDialog(mainFrame, "Loading...", false);
        JLabel loadingLabel = new JLabel("Generating " + difficulty + " maze...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        loadingLabel.setForeground(Color.GREEN);
        loadingLabel.setBackground(Color.BLACK);
        loadingLabel.setOpaque(true);
        loadingLabel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        loadingDialog.add(loadingLabel);
        loadingDialog.pack();
        loadingDialog.setLocationRelativeTo(mainFrame);

        // Disable all buttons to prevent double-clicks
        Component[] components = buttonPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                comp.setEnabled(false);
            }
        }

        // Show loading cursor
        mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        loadingDialog.setVisible(true);

        // Create maze in background thread using SwingWorker
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Small delay to ensure UI updates are visible
                Thread.sleep(100);
                return null;
            }

            @Override
            protected void done() {
                try {
                    // This runs on EDT after background work is done
                    mainFrame.startGame("Player", difficulty, isAutoMode);
                } catch (Exception ex) {
                    System.err.println("ERROR STARTING GAME:");
                    ex.printStackTrace();

                    // Re-enable buttons on error
                    for (Component comp : components) {
                        if (comp instanceof JButton) {
                            comp.setEnabled(true);
                        }
                    }

                    JOptionPane.showMessageDialog(HomePanel.this,
                            "Error: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    // Always clean up
                    loadingDialog.dispose();
                    mainFrame.setCursor(Cursor.getDefaultCursor());
                }
            }
        };

        worker.execute();
    }
    // ======================================================================

    private void startBallAnimation() {
        javax.swing.Timer animTimer = new javax.swing.Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationCounter++;

                if (animationCounter % 2 == 0) {
                    int nextX = ballX + dx;
                    int nextY = ballY + dy;

                    if (nextX < 0 || nextX >= 6 || miniMaze[ballY][nextX] == 1) {
                        dx = -dx;
                        nextX = ballX + dx;
                    }

                    if (nextY < 0 || nextY >= 6 || miniMaze[nextY][ballX] == 1) {
                        dy = -dy;
                        nextY = ballY + dy;
                    }

                    if (nextX >= 0 && nextX < 6 && nextY >= 0 && nextY < 6 && miniMaze[nextY][nextX] == 0) {
                        ballX = nextX;
                        ballY = nextY;
                    }
                }

                backgroundPanel.repaint();
            }
        });
        animTimer.start();
    }

    private void paintBackground(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Dark gradient background
        GradientPaint bg = new GradientPaint(0, 0, new Color(10, 10, 30),
                0, height, new Color(30, 10, 50));
        g2.setPaint(bg);
        g2.fillRect(0, 0, width, height);

        // Neon grid lines
        g2.setColor(new Color(0, 255, 0, 20));
        for (int i = 0; i < width; i += 50) {
            g2.drawLine(i, 0, i, height);
        }
        for (int i = 0; i < height; i += 50) {
            g2.drawLine(0, i, width, i);
        }

        // Title with glow
        drawGlowText(g2, "MAZE RUNNER", 90, Color.GREEN, new Color(0, 255, 0));

        // Subtitle
        g2.setFont(new Font("Monospaced", Font.BOLD, 20));
        g2.setColor(Color.CYAN);
        String subtitle = ":: RETRO EDITION ::";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(subtitle, (width - fm.stringWidth(subtitle)) / 2, 140);

        // Draw mini maze
        drawMiniMaze(g2, mazeStartX, mazeStartY, 6, 6, cellSize);

        // Animated ball
        int pixelX = mazeStartX + ballX * cellSize;
        int pixelY = mazeStartY + ballY * cellSize;

        for (int i = 3; i > 0; i--) {
            g2.setColor(new Color(255, 255, 0, 50 * i));
            g2.fillOval(pixelX - i * 2, pixelY - i * 2, ballSize + i * 4, ballSize + i * 4);
        }
        g2.setColor(Color.YELLOW);
        g2.fillOval(pixelX, pixelY, ballSize, ballSize);

        // Instructions
        g2.setFont(new Font("Monospaced", Font.BOLD, 16));
        g2.setColor(new Color(100, 255, 100));
        String instr1 = "1. SELECT AUTO or MANUAL MODE";
        String instr2 = "2. CHOOSE DIFFICULTY TO START";
        int startY = 655;
        int centerX = width / 2;
        g2.drawString(instr1, centerX - g2.getFontMetrics().stringWidth(instr1) / 2, startY);
        g2.drawString(instr2, centerX - g2.getFontMetrics().stringWidth(instr2) / 2, startY + 22);
    }

    private void drawGlowText(Graphics2D g2, String text, int y, Color shadow, Color main) {
        Font font = new Font("Monospaced", Font.BOLD, 72);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;

        // Glow layers
        for (int i = 10; i > 0; i--) {
            g2.setColor(new Color(shadow.getRed(), shadow.getGreen(), shadow.getBlue(), 10));
            g2.drawString(text, x - i, y + i);
        }

        // Main text
        g2.setColor(main);
        g2.drawString(text, x, y);
    }

    private void drawMiniMaze(Graphics2D g2, int startX, int startY, int rows, int cols, int cell) {
        // Glow border
        g2.setColor(new Color(0, 255, 0, 50));
        g2.fillRoundRect(startX - 10, startY - 10, cols * cell + 20, rows * cell + 20, 15, 15);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int px = startX + x * cell;
                int py = startY + y * cell;

                boolean isWall = miniMaze[y][x] == 1;

                if (isWall) {
                    g2.setColor(new Color(0, 150, 0));
                } else {
                    g2.setColor(new Color(0, 50, 0));
                }
                g2.fillRect(px, py, cell, cell);

                g2.setColor(new Color(0, 100, 0));
                g2.drawRect(px, py, cell, cell);
            }
        }
    }
}