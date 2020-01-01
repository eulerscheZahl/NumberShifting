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

        System.out.println("2 2 U +");
        System.out.println("7 3 L -");
        System.out.println("2 4 U -");
    }
}
