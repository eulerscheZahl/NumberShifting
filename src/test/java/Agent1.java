import java.util.Scanner;

public class Agent1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("first_level");

        int width = scanner.nextInt();
        int height = scanner.nextInt();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int val = scanner.nextInt();
            }
        }

        System.out.println("4 1 D -");
        System.out.println("0 3 R +");
        System.out.println("1 3 R -");
    }
}
