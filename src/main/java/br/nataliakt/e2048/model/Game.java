package br.nataliakt.e2048.model;

import br.nataliakt.e2048.ga.Chromosome;
import br.nataliakt.e2048.ga.Generation;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents the game board with the moviments
 * @author Natalia Kelim Thiel
 * @version 1.0.0
 */
public class Game extends Chromosome {

    public static final int WIDTH = 4;
    public static final int HEIGHT = 4;
    private final SimpleIntegerProperty[][] board;
    private SimpleIntegerProperty score;
    private SimpleIntegerProperty moviments;
    private boolean running = true;

    /**
     * Constructor with the super params
     * @param generation
     * @param geneList
     */
    public Game(Generation generation, int[] geneList) {
        super(generation, geneList);
        board = new SimpleIntegerProperty[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                board[i][j] = new SimpleIntegerProperty(0);
            }
        }
        score = new SimpleIntegerProperty(0);
        moviments = new SimpleIntegerProperty(0);
        nextNumber();
    }

    /**
     * Start the moviments
     */
    public void start() {
        Timer timer = new Timer();
        final int[] i = {0, 0};
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (i[0] >= size()) {
                    running = false;
                    timer.cancel();
                    return;
                }
                if (i[0] != i[1]) {
                    return;
                }
                Platform.runLater(() -> {
                    swipe(MovimentEnum.find(get(i[0])));
                    i[0]++;
                });
                i[1]++;
            }
        }, 100, 50);
    }

    /**
     * Create a new number (2 or 4) in a random free space
     */
    protected void nextNumber() {
        int spaces = countSpaces();
        if (spaces == 0) {
            return;
        }
        int positionRandom = ThreadLocalRandom.current().nextInt(spaces);
        int value = ThreadLocalRandom.current().nextInt(1, 3) * 2;
        int position = 0;
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (board[i][j].get() != 0) {
                    continue;
                }
                if (position == positionRandom) {
                    board[i][j].set(value);
                }
                position++;
            }
        }
    }

    /**
     * Swipe to a direction
     * @param movimentEnum
     */
    public void swipe(MovimentEnum movimentEnum) {
        boolean swiped = false;
        switch (movimentEnum) {
            case LEFT:
                swiped = swipeLeft();
                break;
            case UP:
                swiped = swipeUp();
                break;
            case RIGHT:
                swiped = swipeRight();
                break;
            case DOWN:
                swiped = swipeDown();
                break;
        }
        if (swiped) {
            moviments.set(moviments.get() + 1);
            nextNumber();
        }
    }

    /**
     * Move the numbers to left and sum the equals
     * @return if do a swipe
     */
    protected boolean swipeLeft() {
        boolean swiped = false;
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (searchNextAndSecond(i, j, false, j + 1, 0, WIDTH, 1)) {
                    swiped = true;
                }
            }
        }
        return swiped;
    }

    /**
     * Move the numbers to up and sum the equals
     * @return if do a swipe
     */
    protected boolean swipeUp() {
        boolean swiped = false;
        for (int j = 0; j < WIDTH; j++) {
            for (int i = 0; i < HEIGHT; i++) {
                if (searchNextAndSecond(i, j, true, i + 1, 0, HEIGHT, 1)) {
                    swiped = true;
                }
            }
        }
        return swiped;
    }

    /**
     * Move the numbers to right and sum the equals
     * @return if do a swipe
     */
    protected boolean swipeRight() {
        boolean swiped = false;
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = WIDTH - 1; j >= 0; j--) {
                if (searchNextAndSecond(i, j, false, j - 1, -1, WIDTH, -1)) {
                    swiped = true;
                }
            }
        }
        return swiped;
    }

    /**
     * Move the numbers to down and sum the equals
     * @return if do a swipe
     */
    protected boolean swipeDown() {
        boolean swiped = false;
        for (int j = 0; j < WIDTH; j++) {
            for (int i = HEIGHT - 1; i >= 0; i--) {
                if (searchNextAndSecond(i, j, true, i - 1, -1, HEIGHT, -1)) {
                    swiped = true;
                }
            }
        }
        return swiped;
    }

    /**
     * Search the next number and the second
     * @param i x of original number
     * @param j y of original number
     * @param isI if is vertical
     * @param ijInitial initial value to for
     * @param ijBiggerThan ij > ijBiggerThan
     * @param ijLessThan ij < ijLessThan
     * @param ijIncrement ij += ijIncrement
     * @return if do a swipe
     */
    private boolean searchNextAndSecond(int i, int j, boolean isI, int ijInitial,
                                        int ijBiggerThan, int ijLessThan, int ijIncrement) {
        boolean swiped = false;
        boolean next = board[i][j].get() != 0;
        for (int ij = ijInitial; ij > ijBiggerThan && ij < ijLessThan; ij += ijIncrement) {
            int i2, j2;
            if (isI) {
                i2 = ij;
                j2 = j;
            } else {
                i2 = i;
                j2 = ij;
            }
            // Only when isn't empty
            if (board[i2][j2].get() == 0) {
                continue;
            }
            // Capture the next number
            if (!next) {
                swiped = true;
                board[i][j].set(board[i2][j2].get());
                board[i2][j2].set(0);
                next = true;
                continue;
            }
            // Compare with the second number
            if (board[i][j].get() == board[i2][j2].get()) {
                swiped = true;
                board[i][j].set(board[i][j].get() * 2);
                board[i2][j2].set(0);
                score.set(score.get() + board[i][j].get());
            }
            break;
        }
        return swiped;
    }

    public int countSpaces() {
        int count = 0;
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (board[i][j].get() == 0) {
                    count++;
                }
            }
        }
        return count;
    }

    public SimpleIntegerProperty[][] getBoard() {
        return board;
    }

    public SimpleIntegerProperty getScore() {
        return score;
    }

    public SimpleIntegerProperty getMoviments() {
        return moviments;
    }

    public int bestValue() {
        int max = 0;
        for (SimpleIntegerProperty[] line : board) {
            for (SimpleIntegerProperty element : line) {
                if (max < element.get()) {
                    max = element.get();
                }
            }
        }
        return max;
    }

    @Override
    public void updateFitness() {

    }

    @Override
    public int getFitness() {
        return score.get();
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(super.toString());
        stringBuilder.append("\nGame: (S: ");
        stringBuilder.append(score.get());
        stringBuilder.append(". M: ");
        stringBuilder.append(moviments.get());
        stringBuilder.append(")");
        for (int i = 0; i < HEIGHT; i++) {
            stringBuilder.append("\n [ ");
            for (int j = 0; j < WIDTH; j++) {
                stringBuilder.append(board[i][j].get());
                stringBuilder.append(" ");
            }
            stringBuilder.append("]");
        }
        return stringBuilder.toString();
    }

}
