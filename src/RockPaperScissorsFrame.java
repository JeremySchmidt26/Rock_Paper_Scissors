import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class RockPaperScissorsFrame extends JFrame {

    // Stats
    private int playerWins = 0;
    private int computerWins = 0;
    private int ties = 0;

    // Player move tracking
    private int playerRockCount = 0;
    private int playerPaperCount = 0;
    private int playerScissorsCount = 0;
    private String lastPlayerMove = null;

    // GUI components
    private JTextArea resultsArea;
    private JTextField playerWinsField;
    private JTextField computerWinsField;
    private JTextField tiesField;

    // Strategies
    private final Strategy cheat = new Cheat();
    private final Strategy randomStrategy = new RandomStrategy();
    private final Strategy leastUsed = new LeastUsed();
    private final Strategy mostUsed = new MostUsed();
    private final Strategy lastUsed = new LastUsed();

    private final Random rand = new Random();

    public RockPaperScissorsFrame() {
        super("Rock Paper Scissors Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLayout(new BorderLayout());

        buildGUI();

        setVisible(true);
    }

    private void buildGUI() {

        // Title
        JLabel title = new JLabel("Rock Paper Scissors Game", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        // Results area
        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setBorder(new TitledBorder("Game Results"));
        add(scrollPane, BorderLayout.CENTER);

        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(3, 2));
        statsPanel.setBorder(new TitledBorder("Stats"));

        statsPanel.add(new JLabel("Player Wins:"));
        playerWinsField = new JTextField("0");
        playerWinsField.setEditable(false);
        statsPanel.add(playerWinsField);

        statsPanel.add(new JLabel("Computer Wins:"));
        computerWinsField = new JTextField("0");
        computerWinsField.setEditable(false);
        statsPanel.add(computerWinsField);

        statsPanel.add(new JLabel("Ties:"));
        tiesField = new JTextField("0");
        tiesField.setEditable(false);
        statsPanel.add(tiesField);

        add(statsPanel, BorderLayout.EAST);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
        buttonPanel.setBorder(new TitledBorder("Choose Your Move"));

        JButton rockBtn = new JButton("Rock");
        JButton paperBtn = new JButton("Paper");
        JButton scissorsBtn = new JButton("Scissors");
        JButton quitBtn = new JButton("Quit");

        rockBtn.addActionListener(new GameListener("R"));
        paperBtn.addActionListener(new GameListener("P"));
        scissorsBtn.addActionListener(new GameListener("S"));
        quitBtn.addActionListener(e -> System.exit(0));

        buttonPanel.add(rockBtn);
        buttonPanel.add(paperBtn);
        buttonPanel.add(scissorsBtn);
        buttonPanel.add(quitBtn);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // ============================
    // INNER STRATEGY CLASSES
    // ============================

    private class LeastUsed implements Strategy {
        @Override
        public String getMove(String playerMove) {
            int r = playerRockCount;
            int p = playerPaperCount;
            int s = playerScissorsCount;

            if (r <= p && r <= s) return "P"; // beat Rock
            if (p <= r && p <= s) return "S"; // beat Paper
            return "R"; // beat Scissors
        }
    }

    private class MostUsed implements Strategy {
        @Override
        public String getMove(String playerMove) {
            int r = playerRockCount;
            int p = playerPaperCount;
            int s = playerScissorsCount;

            if (r >= p && r >= s) return "P"; // beat Rock
            if (p >= r && p >= s) return "S"; // beat Paper
            return "R"; // beat Scissors
        }
    }

    private class LastUsed implements Strategy {
        @Override
        public String getMove(String playerMove) {
            if (lastPlayerMove == null) {
                return randomStrategy.getMove(playerMove);
            }

            return switch (lastPlayerMove) {
                case "R" -> "P";
                case "P" -> "S";
                case "S" -> "R";
                default -> randomStrategy.getMove(playerMove);
            };
        }
    }

    // ============================
    // GAME LISTENER
    // ============================

    private class GameListener implements ActionListener {

        private final String playerMove;

        public GameListener(String move) {
            this.playerMove = move;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            // Track player move counts
            switch (playerMove) {
                case "R" -> playerRockCount++;
                case "P" -> playerPaperCount++;
                case "S" -> playerScissorsCount++;
            }

            lastPlayerMove = playerMove;

            // Determine strategy by probability
            int roll = rand.nextInt(100) + 1;
            Strategy strat;
            String stratName;

            if (roll <= 10) {
                strat = cheat;
                stratName = "Cheat";
            } else if (roll <= 30) {
                strat = leastUsed;
                stratName = "Least Used";
            } else if (roll <= 50) {
                strat = mostUsed;
                stratName = "Most Used";
            } else if (roll <= 70) {
                strat = lastUsed;
                stratName = "Last Used";
            } else {
                strat = randomStrategy;
                stratName = "Random";
            }

            String computerMove = strat.getMove(playerMove);

            // Determine winner
            String result = determineWinner(playerMove, computerMove, stratName);

            resultsArea.append(result + "\n");
        }
    }

    private String determineWinner(String p, String c, String stratName) {

        String outcome;

        if (p.equals(c)) {
            ties++;
            tiesField.setText(String.valueOf(ties));
            outcome = "Tie! (" + stratName + ")";
        } else if (
                (p.equals("R") && c.equals("S")) ||
                        (p.equals("P") && c.equals("R")) ||
                        (p.equals("S") && c.equals("P"))
        ) {
            playerWins++;
            playerWinsField.setText(String.valueOf(playerWins));
            outcome = translate(p, c) + " (Player wins! Computer: " + stratName + ")";
        } else {
            computerWins++;
            computerWinsField.setText(String.valueOf(computerWins));
            outcome = translate(c, p) + " (Computer wins! Computer: " + stratName + ")";
        }

        return outcome;
    }

    private String translate(String winner, String loser) {
        if (winner.equals("R") && loser.equals("S")) return "Rock breaks scissors";
        if (winner.equals("P") && loser.equals("R")) return "Paper covers rock";
        if (winner.equals("S") && loser.equals("P")) return "Scissors cut paper";
        return "";
    }
}
