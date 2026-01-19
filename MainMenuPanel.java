import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {

    private int ballX, ballY;
    private int ballSize = 50;
    private int dx = 5, dy = 5;

    private int mazeStartX, mazeStartY;
    private int cellSize = 50;
    private int[][] maze = {
            {1,1,1,1,1,1,1,1,1,1},
            {1,0,0,1,0,0,0,1,0,1},
            {1,0,1,1,0,1,0,1,0,1},
            {1,0,1,0,0,1,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1},
    };

    public JButton autoPlayButton, manualPlayButton, leaderboardButton;

    public MainMenuPanel() {
        setPreferredSize(new Dimension(900, 600));
        setLayout(null);
        setBackground(Color.BLACK);

        mazeStartX = (900 - maze[0].length*cellSize)/2;
        mazeStartY = 120;
        ballX = mazeStartX + cellSize + 10;
        ballY = mazeStartY + cellSize + 10;

        // Buttons
        autoPlayButton = createPixelButton("AUTO PLAY", 100, 500, 180, 60);
        manualPlayButton = createPixelButton("MANUAL PLAY", 360, 500, 180, 60);
        leaderboardButton = createPixelButton("LEADERBOARD", 620, 500, 180, 60);

        add(autoPlayButton);
        add(manualPlayButton);
        add(leaderboardButton);

        // Ball animation
        Timer timer = new Timer(40, e -> {
            ballX += dx;
            ballY += dy;
            int mazeWidth = maze[0].length*cellSize;
            int mazeHeight = maze.length*cellSize;
            if(ballX < mazeStartX || ballX + ballSize > mazeStartX + mazeWidth) dx = -dx;
            if(ballY < mazeStartY || ballY + ballSize > mazeStartY + mazeHeight) dy = -dy;
            repaint();
        });
        timer.start();
    }

    private JButton createPixelButton(String text, int x, int y, int w, int h){
        JButton btn = new JButton(text){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g); // keep clickable
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0,0,new Color(0,180,255),0,getHeight(),new Color(0,70,180));
                g2.setPaint(gp);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),15,15);

                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,15,15);

                g2.setFont(new Font("Monospaced", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText()))/2;
                int textY = ((getHeight() - fm.getHeight())/2) + fm.getAscent();
                g2.setColor(Color.BLUE.darker());
                g2.drawString(getText(), textX+2, textY+2);
                g2.setColor(Color.WHITE);
                g2.drawString(getText(), textX, textY);
            }
        };
        btn.setBounds(x,y,w,h);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Background gradient
        GradientPaint bg = new GradientPaint(0,0,new Color(30,30,60),0,getHeight(),new Color(50,20,80));
        g2.setPaint(bg);
        g2.fillRect(0,0,getWidth(),getHeight());

        // Neon borders
        int blockSize = 20;
        Color[] colors = {new Color(138,43,226), new Color(255,105,180), new Color(0,255,255), new Color(255,255,0)};
        for(int i=0;i<getWidth();i+=blockSize){
            g2.setColor(colors[(i/blockSize)%4]);
            g2.fillRect(i,0,blockSize,blockSize);
            g2.fillRect(i,getHeight()-blockSize,blockSize,blockSize);
        }
        for(int i=0;i<getHeight();i+=blockSize){
            g2.setColor(colors[(i/blockSize)%4]);
            g2.fillRect(0,i,blockSize,blockSize);
            g2.fillRect(getWidth()-blockSize,i,blockSize,blockSize);
        }

        draw3DTitle(g2,"BALL MAZE",60);
        drawMazeWithNeon(g2);
        drawBallWithEyes(g2);
    }

    private void draw3DTitle(Graphics2D g2,String text,int yPos){
        Font font = new Font("Monospaced", Font.BOLD, 64);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth()-fm.stringWidth(text))/2;

        Color[] shadows = {new Color(50,0,100), new Color(100,0,150), new Color(150,50,200)};
        for(int i=shadows.length-1;i>=0;i--){
            g2.setColor(shadows[i]);
            g2.drawString(text,x+i*3,yPos+i*3);
        }

        g2.setColor(new Color(255,250,205));
        g2.drawString(text,x,yPos);
    }

    private void drawMazeWithNeon(Graphics2D g2){
        int mazeWidth = maze[0].length*cellSize;
        int mazeHeight = maze.length*cellSize;

        for(int i=0;i<mazeWidth+10;i+=15){
            g2.setColor(Color.CYAN);
            g2.fillOval(mazeStartX-10+i, mazeStartY-10, 10,10);
            g2.fillOval(mazeStartX-10+i, mazeStartY+mazeHeight, 10,10);
        }
        for(int i=0;i<mazeHeight+10;i+=15){
            g2.setColor(Color.MAGENTA);
            g2.fillOval(mazeStartX-10, mazeStartY-10+i, 10,10);
            g2.fillOval(mazeStartX+mazeWidth, mazeStartY-10+i, 10,10);
        }

        for(int i=0;i<maze.length;i++){
            for(int j=0;j<maze[i].length;j++){
                int px = mazeStartX+j*cellSize;
                int py = mazeStartY+i*cellSize;
                if(maze[i][j]==1){
                    GradientPaint gp = new GradientPaint(px,py,new Color(0,255,0),px+cellSize,py+cellSize,new Color(0,150,0));
                    g2.setPaint(gp);
                    g2.fillRect(px, py, cellSize, cellSize);
                } else {
                    g2.setColor(new Color(255,255,0));
                    g2.fillRect(px, py, cellSize, cellSize);
                }
            }
        }

        // Start and Stop labels
        g2.setFont(new Font("Monospaced",Font.BOLD,22));
        g2.setColor(Color.BLACK);
        g2.drawString("START", mazeStartX + 1*cellSize + 5, mazeStartY + 1*cellSize + cellSize/2 + 5);
        g2.setColor(Color.RED);
        g2.drawString("STOP", mazeStartX + 8*cellSize + 5, mazeStartY + 3*cellSize + cellSize/2 + 5);
    }

    private void drawBallWithEyes(Graphics2D g2){
        GradientPaint gp = new GradientPaint(ballX, ballY, new Color(0,255,255), ballX+ballSize, ballY+ballSize, new Color(0,150,200));
        g2.setPaint(gp);
        g2.fillOval(ballX, ballY, ballSize, ballSize);

        g2.setColor(new Color(255,255,255,120));
        g2.fillOval(ballX+5, ballY+5, ballSize/3, ballSize/3);

        g2.setColor(Color.CYAN.darker());
        g2.drawOval(ballX+2, ballY+2, ballSize-4, ballSize-4);

        int eyeSize = ballSize/8;
        g2.setColor(Color.BLACK);
        g2.fillOval(ballX + ballSize/3 - eyeSize/2, ballY + ballSize/3 - eyeSize/2, eyeSize, eyeSize);
        g2.fillOval(ballX + 2*ballSize/3 - eyeSize/2, ballY + ballSize/3 - eyeSize/2, eyeSize, eyeSize);
    }
}
