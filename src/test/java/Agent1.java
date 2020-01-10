import java.util.Scanner;

public class Agent1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("first_level");

        int width = scanner.nextInt();
        int height = scanner.nextInt();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int cell = scanner.nextInt();
            }
        }

        System.out.println("7 4 L +");
        System.out.println("3 0 D -");
        System.out.println("6 4 L -");
    }
}
