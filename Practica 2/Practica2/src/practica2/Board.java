package practica2;

import java.util.Arrays;

public class Board {

    static final int STENCH_PENALTY = 20;
    static final int BREEZE_PENALTY = 20;

    private int size;
    private int num_holes;
    private int num_wumpus;
    private int num_gold;
    private int[][] board;
    private static final int[][] MOVEMENTS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private static final int[][] DIAGONALS = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
    private static final String[] DIRECTIONS = {"UP", "DOWN", "LEFT", "RIGHT"};

    Board(int size, int num_holes, int num_wumpus, int num_gold) {
        this.size = size;
        this.num_holes = num_holes;
        this.num_wumpus = num_wumpus;
        this.num_gold = num_gold;
        this.board = new int[size][size];
    }

    Board(int size) {
        this.size = size;
        this.board = new int[size][size];
    }

    /**
     * Initializes the board EMPTY = 0 HOLE = 1 WUMPUS = 2 GOLD = 3 BREEZE = 4
     * STENCH = 5 BREEZE_STENCH = 6 GOLD_BREEZE = 7 GOLD_STENCH = 8
     * GOLD_BREEZE_STENCH = 9
     */
    public void init() {
//        // Create the positions for the holes, wumpus and gold
//        int[] positions = new int[size * size - 1];
//        for (int i = 0; i < size * size - 1; i++) {
//            positions[i] = i + 1;
//        }
//        shuffle(positions);
//        System.out.println("Positions: " + Arrays.toString(positions));
        int[] positions = {4, 8, 12, 9, 13, 14, 15};

        for (int i = 0; i < num_holes; i++) {
            int x = positions[i] / size;
            int y = positions[i] % size;
            board[x][y] = 1; // HOLE
            for (int[] movement : MOVEMENTS) {
                if (checkEdges(x, y, movement)) {
                    if (board[x + movement[0]][y + movement[1]] != 1) {
                        board[x + movement[0]][y + movement[1]] = 4; // BREEZE
                    }
                }
            }
        }

        for (int i = num_holes; i < num_holes + num_wumpus; i++) {
            int x = positions[i] / size;
            int y = positions[i] % size;
            board[x][y] = 2; // WUMPUS
            for (int[] movement : MOVEMENTS) {
                if (checkEdges(x, y, movement)) {
                    if (board[x + movement[0]][y + movement[1]] == 4) {
                        board[x + movement[0]][y + movement[1]] = 6; // BREEZE_STENCH
                    } else if (board[x + movement[0]][y + movement[1]] == 0) {
                        board[x + movement[0]][y + movement[1]] = 5; // STENCH
                    }
                }
            }
        }

        for (int i = num_holes + num_wumpus; i < num_holes + num_wumpus + num_gold; i++) {
            int x = positions[i] / size;
            int y = positions[i] % size;

            if (board[x][y] == 4) {
                board[x][y] = 7; // GOLD_BREEZE
            } else if (board[x][y] == 5) {
                board[x][y] = 8; // GOLD_STENCH
            } else if (board[x][y] == 6) {
                board[x][y] = 9; // GOLD_BREEZE_STENCH
            } else {
                board[x][y] = 3; // GOLD
            }

        }
    }

    /**
     * Returns true if we can move in the given direction without going out of
     * bounds
     *
     * @param x
     * @param y
     * @param movement
     */
    private boolean checkEdges(int x, int y, int[] movement) {
        int new_x = x + movement[0];
        int new_y = y + movement[1];
        if (new_x < 0 || new_x >= size || new_y < 0 || new_y >= size) {
            return false;
        }
        return true;
    }

    /**
     * Prints the board, so it's evenly spaced and separeted by lines
     */
    public void print() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(String.format("%03d", board[i][j]) + " | ");
            }
            System.out.println();
            for (int j = 0; j < size; j++) {
                System.out.print("--- | ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Shuffles the given array
     *
     * @param array
     */
    private void shuffle(int[] array) {
        int index, temp;
        for (int i = array.length - 1; i > 0; i--) {
            index = (int) (Math.random() * (i + 1));
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    /**
     * Fills the board with the given value. Only intended to use with
     * known_board
     *
     * @param value
     */
    public void fill(int value) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = value;
            }
        }
    }

    /**
     * Given a postion, it returns the adjacent position with the smallest value
     * in the heuristic_board
     *
     * @param heuristic_board
     * @return
     */
    public int[] getBestPosition(int[] current_position) {
        int x = current_position[0];
        int y = current_position[1];
        int[] best_position = new int[2];
        int best_value = Integer.MAX_VALUE;
        int idx = 0;
        int best_idx = 0;
        for (int[] movement : MOVEMENTS) {
            if (checkEdges(x, y, movement)) {
                if (this.board[x + movement[0]][y + movement[1]] < best_value) {
                    best_value = this.board[x + movement[0]][y + movement[1]];
                    best_position[0] = x + movement[0];
                    best_position[1] = y + movement[1];
                    best_idx = idx;
                }
            }
            idx++;
        }
        System.out.println("Moving: " + DIRECTIONS[best_idx]);
        return best_position;
    }

    /**
     * Returns the board
     *
     * @return
     */
    public int[][] getBoard() {
        return board;
    }

    /**
     * Returns specific position in the board
     *
     * @param position
     * @return
     */
    public int get(int x, int y) {
        return board[x][y];
    }

    /**
     * Sets the value of a specific position in the board
     *
     * @param position
     * @param value
     */
    public void set(int[] position, int value) {
        board[position[0]][position[1]] = value;
    }

    public void addHeuristic(int[] current_position, Board safe_board, int result) {
        int x = current_position[0];
        int y = current_position[1];
        this.board[x][y]++;
        for (int[] movement : MOVEMENTS) {
            if (checkEdges(x, y, movement)) {
                if (safe_board.get(x + movement[0], y + movement[1]) == 0) {

                    if (result == 4) {
                        this.board[x + movement[0]][y + movement[1]] += BREEZE_PENALTY;
                    } else if (result == 5) {
                        this.board[x + movement[0]][y + movement[1]] += STENCH_PENALTY;
                    } else if (result == 6) {
                        this.board[x + movement[0]][y + movement[1]] += BREEZE_PENALTY + STENCH_PENALTY;
                    }

                }
            }
        }
    }

    public void recalculate(int[] current_position, Board safe_board, Board real_board) {
        int[][] safe_pairs = {{0, 4}, {0, 5}, {0, 6}, {4, 5}, {4, 0}, {5, 0}, {6, 0}, {7, 0},
        {5, 4}};
        int x = current_position[0];
        int y = current_position[1];
        for (int[] diagonal : DIAGONALS) {
            if (checkEdges(x, y, diagonal)) {
                if (safe_board.get(x + diagonal[0], y + diagonal[1]) != 0) {
                    int[] pair = {real_board.get(x, y), real_board.get(x + diagonal[0], y + diagonal[1])};
                    for (int[] safe_pair : safe_pairs) {
                        if (Arrays.equals(pair, safe_pair)) {
                            this.board[x][y + diagonal[1]] = 0;
                            this.board[x + diagonal[0]][y] = 0;
                        }
                    }
                }
            }
        }
    }
}
