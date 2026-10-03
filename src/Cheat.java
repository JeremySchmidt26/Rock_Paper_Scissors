public class Cheat implements Strategy {

    @Override
    public String getMove(String playerMove) {
        return switch (playerMove) {
            case "R" -> "P";
            case "P" -> "S";
            case "S" -> "R";
            default -> "X";
        };
    }
}
