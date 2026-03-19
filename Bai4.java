import java.util.*;

public class Bai4 {

    static int n, k;
    static int[] a;
    static List<Integer> best = new ArrayList<>();

    static void backtrack(int index, int sum, List<Integer> current) {

        if (sum == k) {
            if (current.size() > best.size()) {
                best = new ArrayList<>(current);
            }
            return;
        }

        if (index == n || sum > k) return;

        current.add(a[index]);
        backtrack(index + 1, sum + a[index], current);
        current.remove(current.size() - 1);

        backtrack(index + 1, sum, current);
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        n = sc.nextInt();
        k = sc.nextInt();

        a = new int[n];

        for (int i = 0; i < n; i++)
            a[i] = sc.nextInt();

        backtrack(0, 0, new ArrayList<>());

        for (int x : best)
            System.out.print(x + " ");
    }
}