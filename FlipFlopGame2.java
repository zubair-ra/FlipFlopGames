/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flipflopgame;

/**
 *
 * @author bsef2
 */     
/*

/**
 *
 * @author bsef2
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

    public class FlipFlopGame2 {
    private JButton[] btn;
    private int[] numbers;
    private int flips;
    private JButton pre_btn;
    private int score;
    private int highScore;
    private String playerName;
    private List<Integer> scoreRecords;
    private long s_Time;
    private long e_Time;
    private long t_Time;

    private JLabel scoreLabel;
    private JLabel timerLabel;
    private int timeRemaining;
    private Timer timer;

    public FlipFlopGame2()                  //Constructors
    {
        JFrame frame = new JFrame("Flip-Flop Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 340);
        frame.setLayout(new BorderLayout());

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(4, 4));

        btn = new JButton[16];        //array of btns
        numbers = new int[16];       //array of numbers which is show on the cards
        flips = 0;
        score = 0; // Initial score set to 0
        highScore = loadHighScore();       //fun to show high score
        scoreRecords = loadScoreRecords();   //fun to store records in csv files

        for (int i = 0; i < 16; i++) {     
            btn[i] = new JButton();
            btn[i].addActionListener(new ButtonListener());
            gamePanel.add(btn[i]);
        }

        frame.add(gamePanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        scoreLabel = new JLabel("Score: " + score);
        topPanel.add(scoreLabel);

        timerLabel = new JLabel("Time Remaining: 60");
        topPanel.add(timerLabel);

        frame.add(topPanel, BorderLayout.NORTH);

        playerName = JOptionPane.showInputDialog(frame, "Enter your name:", "Flip-Flop Game", JOptionPane.PLAIN_MESSAGE);

        resetGame();

        frame.setVisible(true);

        JOptionPane.showMessageDialog(null, "Welcome to Flip-Flop Game, " + playerName + "!\nHigh Score: " + highScore, "Flip-Flop Game", JOptionPane.INFORMATION_MESSAGE);

        showNumbersForThreeSeconds();
    }

    private int loadHighScore() {
        int score = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\bsef2\\OneDrive\\Desktop\\JAVA\\file.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                score = Integer.parseInt(line);
            }
        } catch (FileNotFoundException e) {
             //No high score file found, default score is 0
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error loading high score.", "File Error", JOptionPane.ERROR_MESSAGE);
        }
        return score;
    }

    private List<Integer> loadScoreRecords() {
        List<Integer> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\bsef2\\OneDrive\\Desktop\\JAVA\\score_records.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 1) {
                    int score = Integer.parseInt(parts[1]);
                    records.add(score);
                }
            }
        } catch (FileNotFoundException e) {
             //No score records file found
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error loading score records.", "File Error", JOptionPane.ERROR_MESSAGE);
        }
        return records;
    }

    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\bsef2\\OneDrive\\Desktop\\JAVA\\file.txt"))) {
            writer.write(Integer.toString(highScore));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving high score.", "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveScoreRecord() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\bsef2\\OneDrive\\Desktop\\JAVA\\score_records.csv", true))) {
            writer.write(playerName + "," + score + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving score record.", "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetGame() {
        generateRandomNumbers();
        flips = 0;
        score = 0;
        s_Time = System.currentTimeMillis();
        e_Time = 0;
        t_Time = 0;

        if (timer != null) {
            timer.stop();
        }

        timeRemaining = 60;
        timerLabel.setText("Time Remaining: " + timeRemaining);

        updateScoreLabel();

        for (int i = 0; i < 16; i++) {
            btn[i].setText("");
            btn[i].setEnabled(true);
        }
    }

    private void generateRandomNumbers() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            list.add(i + 1);
            list.add(i + 1);
        }
        java.util.Collections.shuffle(list);
        for (int i = 0; i < 16; i++) {
            numbers[i] = list.get(i);
        }
    }

    private void showNumbersForThreeSeconds() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i < 16; i++) {
                    btn[i].setText(Integer.toString(numbers[i]));
                }

                Thread.sleep(3000);

                for (int i = 0; i < 16; i++) {
                    btn[i].setText("");
                }

                return null;
            }

            @Override
            protected void done() {
                // Start the timer after numbers are shown
                startTimer();
            }
        };

        worker.execute();
    }

    private void startTimer() {
        timer = new Timer(1000, new TimerListener());
        timer.start();
    }

    private int indexOfButton(JButton button) {
        for (int i = 0; i < 16; i++) {
            if (btn[i] == button) {
                return i;
            }
        }
        return -1;
    }

    private void flipCard(JButton button, int index) {
        button.setText(Integer.toString(numbers[index]));
        button.setEnabled(false);

        if (flips == 0) {
            flips++;
            pre_btn = button;
        } else {
            if (numbers[index] != numbers[indexOfButton(pre_btn)]) {
                 //Incorrect flip
                JOptionPane.showMessageDialog(null, "Sorry, wrong match!", "Flip-Flop Game", JOptionPane.INFORMATION_MESSAGE);
                flips = 0;
                button.setText("");
                button.setEnabled(true);
                pre_btn.setText("");
                pre_btn.setEnabled(true);

                // Deduct points based on the number of incorrect flips
                int incorrectFlips = 1;
                score -= incorrectFlips * 10;
            } else {
               //  Correct match
                flips = 0;
                button.setEnabled(false);
                pre_btn.setEnabled(false);

                 //Add points for correct match
                int matchPoints = 20;
                score += matchPoints;
                updateScoreLabel();

                // Check if the game is completed
                if (isGameCompleted()) {
                    e_Time = System.currentTimeMillis();
                    t_Time = (e_Time - s_Time) / 1000;

                    // Update high score if the current score is higher
                    if (score > highScore) {
                        highScore = score;
                        saveHighScore();
                    }

                     //Save the score record
                    saveScoreRecord();

                    // Show game over message
                    String message = "Congratulations, you completed the game!\n";
                    message += "Score: " + score + "\n";
                    message += "Total Time: " + t_Time + " seconds\n";
                    message += "High Score: " + highScore + "\n";
                    JOptionPane.showMessageDialog(null, message, "Flip-Flop Game", JOptionPane.INFORMATION_MESSAGE);

                    // Reset the game
                    resetGame();
                }
            }
        }
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Score: " + score);
    }

    private boolean isGameCompleted() {
        for (int i = 0; i < 16; i++) {
            if (btn[i].isEnabled()) {
                return false;
            }
        }
        return true;
    }

    private class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            int index = indexOfButton(button);
            if (index != -1) {
                flipCard(button, index);
            }
        }
    }

    private class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            timeRemaining--;
            timerLabel.setText("Time Remaining: " + timeRemaining);

            if (timeRemaining <= 0) {
                timer.stop();

                 //Game over when time runs out
                e_Time = System.currentTimeMillis();
                t_Time = (e_Time - s_Time) / 1000;

                // Update high score if the current score is higher
                if (score > highScore) {
                    highScore = score;
                    saveHighScore();
                }

                // Save the score record
                saveScoreRecord();

                // Show game over message
                String message = "Time's up!\n";
                message += "Score: " + score + "\n";
                message += "Total Time: " + t_Time + " seconds\n";
                message += "High Score: " + highScore + "\n";
                JOptionPane.showMessageDialog(null, message, "Flip-Flop Game", JOptionPane.INFORMATION_MESSAGE);

                // Reset the game
                resetGame();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FlipFlopGame2();
            }
        });
    }
}
