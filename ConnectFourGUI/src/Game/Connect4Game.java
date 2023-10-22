package Game;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.JOptionPane;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

public class Connect4Game extends JFrame {
    private final int ROWS = 6;
    private final int COLS = 7;
    private final int CELL_SIZE = 100;

    private JPanel boardPanel;
    private JLabel statusLabel;

    private boolean isPlayer1Turn;
    private boolean isGameStarted;
    private int[][] board;
    private Timer turnTimer;
    private int currentPlayerTimeLimit;
    private int currentPlayerTimeRemaining;

    public Connect4Game() {
        setTitle("Connect Four");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(ROWS, COLS));
        boardPanel.setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
        boardPanel.setBackground(Color.WHITE);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isGameStarted) {
                    int col = e.getX() / CELL_SIZE;
                    dropDisc(col);
                }
            }
        };
        boardPanel.addMouseListener(mouseAdapter);

        statusLabel = new JLabel();
        statusLabel.setPreferredSize(new Dimension(COLS * CELL_SIZE, 30));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(boardPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);

        showMenu();
    }

    private void startGame(int timeLimit) {
    	 	isGameStarted = true;
    	    board = new int[ROWS][COLS]; // Initialize the board array
    	    clearBoard();
    	    updateStatusLabelText();
        if (timeLimit > 0) {
            currentPlayerTimeLimit = timeLimit;
            currentPlayerTimeRemaining = currentPlayerTimeLimit;
            startTurnTimer();
        }
    }

    private void startTurnTimer() {
        turnTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentPlayerTimeRemaining--;
                if (currentPlayerTimeRemaining == 0) {
                    endTurnDueToTimeout();
                } else {
                    updateStatusLabelText();
                }
            }
        });
        turnTimer.setInitialDelay(0);
        turnTimer.start();
    }

    private void stopTurnTimer() {
        if (turnTimer != null) {
            turnTimer.stop();
        }
    }

    private void resetTurnTimer() {
        currentPlayerTimeRemaining = currentPlayerTimeLimit;
    }

    private void endTurnDueToTimeout() {
        stopTurnTimer();
        showTimeoutDialog();
        isPlayer1Turn = !isPlayer1Turn;
        updateStatusLabelText();
        if (currentPlayerTimeLimit > 0) {
            resetTurnTimer();
            startTurnTimer();
        }
    }

    private void initializeGame() {
        isPlayer1Turn = true;
        isGameStarted = false;
        board = new int[ROWS][COLS]; // Initialize the board array
        clearBoard();
        updateStatusLabelText();
        showMenu();
    }

    private void showMenu() {
        Object[] options = {"Play", "Exit"};
        int choice = JOptionPane.showOptionDialog(this, "Choose an option:", "Connect Four", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            showTimeLimitOptions();
        } else {
            System.exit(0);
        }
    }

    private void showTimeLimitOptions() {
        Object[] options = {"10 seconds per turn", "30 seconds per turn", "Infinite time"};
        int choice = JOptionPane.showOptionDialog(this, "Choose a time limit:", "Connect Four", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            startGame(10);
        } else if (choice == 1) {
            startGame(30);
        } else if (choice == 2) {
            startGame(-1);
        } else {
            System.exit(0);
        }
    }


    private void clearBoard() {
        boardPanel.removeAll(); // Remove all components from the board panel
        boardPanel.setLayout(new GridLayout(ROWS, COLS)); // Set the layout to GridLayout
        boardPanel.setBackground(Color.WHITE);

        board = new int[ROWS][COLS]; // Initialize the board array

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                JPanel discPanel = createDiscPanel(Color.WHITE); // Create a panel for each cell
                boardPanel.add(discPanel); // Add the panel to the board panel
            }
        }

        boardPanel.revalidate(); // Revalidate the board panel
        boardPanel.repaint(); // Repaint the board panel

        updateStatusLabelText(); // Update the status label
    }


    private void dropDisc(int col) {
        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][col] == 0) {
                board[row][col] = isPlayer1Turn ? 1 : 2;
                Color discColor = isPlayer1Turn ? Color.RED : Color.YELLOW;
                JPanel discPanel = createDiscPanel(discColor);
                int index = row * COLS + col; // Calculate the index of the panel to replace
                boardPanel.remove(index); // Remove the existing panel at the index
                boardPanel.add(discPanel, index); // Add the new disc panel at the index
                boardPanel.revalidate(); // Revalidate the board panel
                boardPanel.repaint(); // Repaint the board panel
                if (checkGameOver()) {
                    return;
                }
                isPlayer1Turn = !isPlayer1Turn;
                updateStatusLabelText();
                if (currentPlayerTimeLimit > 0) {
                    resetTurnTimer();
                }
                return;
            }
        }
    }


    private JPanel createDiscPanel(Color color) {
        JPanel discPanel = new JPanel();
        discPanel.setBackground(color);
        discPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        discPanel.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        return discPanel;
    }

    private boolean checkGameOver() {
        if (checkWin()) {
            showPlayAgainDialog();
            return true;
        }
        if (isBoardFull()) {
            showDrawDialog();
            return true;
        }
        return false;
    }

    private boolean checkWin() {
        // Check horizontally
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col <= COLS - 4; col++) {
                if (board[row][col] != 0 &&
                        board[row][col] == board[row][col + 1] &&
                        board[row][col] == board[row][col + 2] &&
                        board[row][col] == board[row][col + 3]) {
                    return true;
                }
            }
        }

        // Check vertically
        for (int col = 0; col < COLS; col++) {
            for (int row = 0; row <= ROWS - 4; row++) {
                if (board[row][col] != 0 &&
                        board[row][col] == board[row + 1][col] &&
                        board[row][col] == board[row + 2][col] &&
                        board[row][col] == board[row + 3][col]) {
                    return true;
                }
            }
        }

        // Check diagonally (top-left to bottom-right)
        for (int row = 0; row <= ROWS - 4; row++) {
            for (int col = 0; col <= COLS - 4; col++) {
                if (board[row][col] != 0 &&
                        board[row][col] == board[row + 1][col + 1] &&
                        board[row][col] == board[row + 2][col + 2] &&
                        board[row][col] == board[row + 3][col + 3]) {
                    return true;
                }
            }
        }

        // Check diagonally (bottom-left to top-right)
        for (int row = ROWS - 1; row >= 3; row--) {
            for (int col = 0; col <= COLS - 4; col++) {
                if (board[row][col] != 0 &&
                        board[row][col] == board[row - 1][col + 1] &&
                        board[row][col] == board[row - 2][col + 2] &&
                        board[row][col] == board[row - 3][col + 3]) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isBoardFull() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateStatusLabelText() {
        String player = isPlayer1Turn ? "Player 1" : "Player 2";
        String timeRemaining = (currentPlayerTimeLimit > 0) ? "Time Remaining: " + currentPlayerTimeRemaining : "";
        statusLabel.setText(player + "'s Turn. " + timeRemaining);
    }
    //Dialog Options
    private void showOptionsDialog() {
        Object[] options = {"Play with 10 seconds per turn", "Play with 30 seconds per turn", "Play with infinite time"};
        int choice = JOptionPane.showOptionDialog(this, "Choose an option:", "Connect Four", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            startGame(10);
        } else if (choice == 1) {
            startGame(30);
        } else if (choice == 2) {
            startGame(-1);
        } else {
            System.exit(0);
        }
    }

    private void showPlayAgainDialog() {
        int option = JOptionPane.showConfirmDialog(this, "Player " + (isPlayer1Turn ? "1" : "2") + " wins! Play again?", "Connect Four", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            initializeGame();
        } else {
            System.exit(0);
        }
    }

    private void showDrawDialog() {
        int option = JOptionPane.showConfirmDialog(this, "It's a draw! Play again?", "Connect Four", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            initializeGame();
        } else {
            System.exit(0);
        }
    }

    private void showTimeoutDialog() {
        JOptionPane.showMessageDialog(this, "Time's up! Player " + (isPlayer1Turn ? "1" : "2") + " loses the turn.", "Connect Four", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Connect4Game game = new Connect4Game();
            game.setVisible(true);
        });
    }
}
