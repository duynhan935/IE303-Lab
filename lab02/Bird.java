import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Bird {
    private int x;
    private int y;
    private final int width;
    private final int height;
    private double velocityY;
    private final double gravity;
    private final double jumpStrength;
    private final Image image;

    public Bird(int x, int y, int width, int height, String imagePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocityY = 0;
        this.gravity = 0.5;
        this.jumpStrength = -8.0;
        this.image = new ImageIcon(imagePath).getImage();
    }

    public void update() {
        velocityY += gravity;
        y += (int) velocityY;
    }

    public void jump() {
        velocityY = jumpStrength;
    }

    public void reset(int x, int y) {
        this.x = x;
        this.y = y;
        this.velocityY = 0;
    }

    public void draw(Graphics g) {
        g.drawImage(image, x, y, width, height, null);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}