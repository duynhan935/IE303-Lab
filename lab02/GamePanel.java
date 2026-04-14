import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    public static final int WIDTH = 360;
    public static final int HEIGHT = 640;

    private static final int BIRD_WIDTH = 34;
    private static final int BIRD_HEIGHT = 24;

    private static final int PIPE_WIDTH = 64;
    private static final int PIPE_HEIGHT = 512;
    private static final int PIPE_GAP = 150;
    private static final int PIPE_SPEED = 3;

    private final Image backgroundImage = new ImageIcon("./data/flappybirdbg.png").getImage();
    private final ArrayList<Pipe> topPipes = new ArrayList<>();
    private final ArrayList<Pipe> bottomPipes = new ArrayList<>();
    private final Random random = new Random();

    private final Timer gameTimer = new Timer(1000 / 60, this);
    private final Timer pipeSpawner = new Timer(1500, e -> spawnPipes());

    private Bird bird;
    private boolean gameOver;
    private int score;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleInput();
            }
        });

        initGame();
    }

    private void initGame() {
        bird = new Bird(50, HEIGHT / 2, BIRD_WIDTH, BIRD_HEIGHT, "./data/flappybird.png");
        topPipes.clear();
        bottomPipes.clear();
        score = 0;
        gameOver = false;
    }

    public void startGame() {
        requestFocusInWindow();
        gameTimer.start();
        pipeSpawner.start();
    }

    private void handleInput() {
        requestFocusInWindow();

        if (gameOver) {
            initGame();
            pipeSpawner.start();
        } else {
            bird.jump();
        }
    }

    private void spawnPipes() {
        if (gameOver) return;

        int topPipeY = random.nextInt(251) - 400; // từ -400 đến -150

        topPipes.add(new Pipe(WIDTH, topPipeY, PIPE_WIDTH, PIPE_HEIGHT, PIPE_SPEED, "./data/toppipe.png"));
        bottomPipes.add(new Pipe(WIDTH, topPipeY + PIPE_HEIGHT + PIPE_GAP, PIPE_WIDTH, PIPE_HEIGHT, PIPE_SPEED, "./data/bottompipe.png"));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT, null);

        for (Pipe pipe : topPipes) pipe.draw(g);
        for (Pipe pipe : bottomPipes) pipe.draw(g);

        bird.draw(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString("Score: " + score, 20, 40);

        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("GAME OVER", 70, HEIGHT / 2 - 20);

            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawString("Press SPACE/ENTER to play again", 20, HEIGHT / 2 + 20);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) updateGame();
        repaint();
    }

    private void updateGame() {
        bird.update();

        movePipes(topPipes, true);
        movePipes(bottomPipes, false);

        if (bird.getY() < 0) {
            bird.setY(0);
        }

        if (bird.getY() + bird.getHeight() >= HEIGHT) {
            bird.setY(HEIGHT - bird.getHeight());
            endGame();
        }
    }

    private void movePipes(ArrayList<Pipe> pipes, boolean countScore) {
        Iterator<Pipe> iterator = pipes.iterator();

        while (iterator.hasNext()) {
            Pipe pipe = iterator.next();
            pipe.update();

            if (pipe.collidesWith(bird)) {
                endGame();
            }

            if (countScore && !pipe.isScored() && pipe.getX() + pipe.getWidth() < bird.getX()) {
                pipe.setScored(true);
                score++;
            }

            if (pipe.isOffScreen()) {
                iterator.remove();
            }
        }
    }

    private void endGame() {
        gameOver = true;
        pipeSpawner.stop();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_ENTER) {
            handleInput();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    } 
}