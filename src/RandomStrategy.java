import java.util.Random;

public class RandomStrategy implements Strategy {

    private final Random rand = new Random();

    @Override
    public String getMove(String playerMove) {
        int r = rand.nextInt(3);
        return switch (r) {
            case 0 -> "R";
            case 1 -> "P";
            default -> "S";
        };
    }
}
