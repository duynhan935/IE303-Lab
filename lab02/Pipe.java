import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Pipe {
    private int x;
    private int y;
    private final int width;
    private final int height;
    private final int speed;
    private final Image image;
    private boolean scored;

    public Pipe(int x, int y, int width, int height, int speed, String imagePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.image = new ImageIcon(imagePath).getImage();
        this.scored = false;
    }

    public void update() {
        x -= speed;
    }

    public void draw(Graphics g) {
        g.drawImage(image, x, y, width, height, null);
    }

    public boolean isOffScreen() {
        return x + width < 0;
    }

    public boolean collidesWith(Bird bird) {
        return bird.getX() < x + width &&
               bird.getX() + bird.getWidth() > x &&
               bird.getY() < y + height &&
               bird.getY() + bird.getHeight() > y;
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return width;
    }

    public boolean isScored() {
        return scored;
    }

    public void setScored(boolean scored) {
        this.scored = scored;
    }
}