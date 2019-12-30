import com.codingame.gameengine.runner.SoloGameRunner;

public class SkeletonMain {
    public static void main(String[] args) {

        SoloGameRunner gameRunner = new SoloGameRunner();
        gameRunner.setTestCase("test1.json");
        gameRunner.setAgent(Agent1.class);

        // Another way to add a player
        // gameRunner.addAgent("python3 /home/user/player.py");

        gameRunner.start();
    }
}
