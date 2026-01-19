import javax.swing.*;

public class MazeSolver extends JFrame {
    private HomePanel homePanel;

    public MazeSolver() {
        setTitle("MAZE RUNNER - Retro Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Maximize the window
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Optional: make it resizable or keep it fixed
        setResizable(true);

        homePanel = new HomePanel(this);
        setContentPane(homePanel);
    }

    public void startGame(String playerName, Difficulty difficulty, boolean autoMode) {
        MazeGrid mazeGrid = new MazeGrid(difficulty);
        GamePanel gamePanel = new GamePanel(this, mazeGrid, autoMode);

        setContentPane(gamePanel);
        revalidate();
        repaint();
        gamePanel.requestFocusInWindow();
    }

    public void returnToHome() {
        homePanel = new HomePanel(this);
        setContentPane(homePanel);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MazeSolver frame = new MazeSolver();
            frame.setVisible(true);
        });
    }
}
