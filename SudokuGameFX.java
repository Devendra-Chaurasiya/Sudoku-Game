import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class SudokuGameFX extends JFrame {
    private JTextField[][] cells;
    private int currentRow = 0;
    private int currentCol = 0;

    public SudokuGameFX() {
        setTitle("Sudoku Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel sudokuPanel = new JPanel(new GridBagLayout());
        cells = new JTextField[9][9];

        // Generate a valid random Sudoku puzzle.
        int[][] puzzle = generateValidRandomSudoku();

        GridBagConstraints gbc = new GridBagConstraints();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col] = createNumberTextField();

                // Display the initial puzzle values.
                if (puzzle[row][col] != 0) {
                    cells[row][col].setText(String.valueOf(puzzle[row][col]));
                    cells[row][col].setEditable(false);
                }

                gbc.gridx = col;
                gbc.gridy = row;
                gbc.fill = GridBagConstraints.BOTH;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                sudokuPanel.add(cells[row][col], gbc);
            }
        }

        add(sudokuPanel, BorderLayout.CENTER);

        JButton checkButton = new JButton("Check");
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkSudoku();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(checkButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleArrowKeys(e, currentRow, currentCol);
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private int[][] generateValidRandomSudoku() {
        int[][] puzzle = new int[9][9];
        Random rand = new Random();
        fillSudoku(puzzle, rand);
        return puzzle;
    }

    private boolean fillSudoku(int[][] puzzle, Random rand) {
        int row, col;
        int[] nums = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                do {
                    row = rand.nextInt(9);
                    col = rand.nextInt(9);
                } while (puzzle[row][col] != 0);
                int numIdx = rand.nextInt(9);
                if (isValid(puzzle, row, col, nums[numIdx])) {
                    puzzle[row][col] = nums[numIdx];
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValid(int[][] puzzle, int row, int col, int num) {
        // Check if num is not in the same row or column
        for (int i = 0; i < 9; i++) {
            if (puzzle[row][i] == num || puzzle[i][col] == num) {
                return false;
            }
        }
        // Check if num is not in the same 3x3 box
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (puzzle[i][j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    private void handleArrowKeys(KeyEvent e, int row, int col) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                col = (col - 1 + 9) % 9;
                break;
            case KeyEvent.VK_RIGHT:
                col = (col + 1) % 9;
                break;
            case KeyEvent.VK_UP:
                row = (row - 1 + 9) % 9;
                break;
            case KeyEvent.VK_DOWN:
                row = (row + 1) % 9;
                break;
        }

        cells[row][col].requestFocus();
        currentRow = row;
        currentCol = col;
    }

    private JTextField createNumberTextField() {
        JTextField textField = new JTextField(2) {
            @Override
            protected Document createDefaultModel() {
                return new PlainDocument() {
                    @Override
                    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                        if (str.matches("[1-9]") || str.isEmpty()) {
                            super.remove(0, getLength()); // Clear the field
                            super.insertString(0, str, a);
                        }
                    }
                };
            }
        };
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setFont(new Font("Arial", Font.PLAIN, 24)); // Increase font size
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveFocusToNextCell(currentRow, currentCol);
            }
        });
        return textField;
    }

    private void checkSudoku() {
        // Create a 2D array to store the Sudoku board.
        int[][] board = new int[9][9];

        // Populate the board with the user's input.
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                try {
                    int value = Integer.parseInt(cells[row][col].getText());
                    board[row][col] = value;
                } catch (NumberFormatException e) {
                    board[row][col] = 0;
                }
            }
        }

        // Check for rule violations.
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = board[row][col];
                if (value == 0) {
                    continue;
                }

                // Check row and column for duplicates.
                for (int i = 0; i < 9; i++) {
                    if (i != col && board[row][i] == value) {
                        cells[row][col].setBackground(Color.RED);
                    }
                    if (i != row && board[i][col] == value) {
                        cells[row][col].setBackground(Color.RED);
                    }
                }
            }
        }
    }

    private void moveFocusToNextCell(int row, int col) {
        col = (col + 1) % 9;
        if (col == 0) {
            row = (row + 1) % 9;
        }
        cells[row][col].requestFocus();
        currentRow = row;
        currentCol = col;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SudokuGameFX();
        });
    }
}
