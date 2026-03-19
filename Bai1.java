import java.util.Random;
import java.util.Scanner;

public class Bai1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Random rand = new Random();

        System.out.print("Nhap ban kinh r: ");
        double r = sc.nextDouble();

        int n = 1000000; 
        int count = 0;

        for (int i = 0; i < n; i++) {
            double x = -r + 2 * r * rand.nextDouble();
            double y = -r + 2 * r * rand.nextDouble();

            if (x * x + y * y <= r * r) {
                count++;
            }
        }

        double area = ((double) count / n) * (2 * r) * (2 * r);

        System.out.println("Dien tich xap xi cua hinh tron: " + area);
    }
}