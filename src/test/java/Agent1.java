import java.util.Scanner;

public class Agent1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("ssxvnjhpdqdxvcrastvybcwvmgnykrxv");

        int width = scanner.nextInt();
        int height = scanner.nextInt();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int val = scanner.nextInt();
            }
        }
        System.out.println("3 3 L -");
        System.out.println("4 4 U +");
        System.out.println("0 1 U +");
        System.out.println("0 2 R -");
        System.out.println("0 0 D -");
    }
}
