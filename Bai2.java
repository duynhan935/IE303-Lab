import java.util.Random;

public class Bai2 {
    public static void main(String[] args) {
        Random rand = new Random();

        int n = 1000000; 
        int count = 0;

        for (int i = 0; i < n; i++) {
            double x = -1 + 2 * rand.nextDouble();
            double y = -1 + 2 * rand.nextDouble();

            if (x * x + y * y <= 1) {
                count++;
            }
        }

        double pi = 4.0 * count / n;

        System.out.println("Gia tri xap xi cua PI: " + pi);
    }
}