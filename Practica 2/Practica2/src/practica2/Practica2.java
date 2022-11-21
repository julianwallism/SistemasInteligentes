// wumpus world
package practica2;

/**
 *
 * @author walli
 */
public class Practica2 {

    static final int ITERS = 500; // Temporal

    static final int SIZE = 4;
    static final int NUM_HOLES = 5;
    static final int NUM_WUMPUS = 1;
    static final int NUM_GOLD = 1;

    static int[] current_position = new int[2];
    static Board board = new Board(SIZE, NUM_HOLES, NUM_WUMPUS, NUM_GOLD);
    static Board heuristic_board = new Board(SIZE);
    static Board safe_board = new Board(SIZE);
    static Board known_board = new Board(SIZE);

    public static void init() {
        current_position[0] = 0;
        current_position[1] = 0;
        board.init();
        known_board.fill(-1);
    }

    public static void start() {
        int iter;
        for(iter=0; iter<ITERS;iter++){
            int result = think();
            if (result == 1) {
                System.out.println("GOLD FOUND");
                break;
            } else if (result == 2) {
                System.out.println("WUMPUS FOUND");
                break;
            } else if (result == 3) {
                System.out.println("HOLE FOUND");
                break;
            } else {
                move();
            }
        }
        System.out.println("Iter"+iter);
        safe_board.print();
        heuristic_board.print();
    }

    public static int think() {
        int result = board.get(current_position[0], current_position[1]);
        if (result == 3 || result == 7 || result == 8 || result == 9) {
            safe_board.set(current_position, 1);
            return 1; // GOLD FOUND
        } else if (result == 2) {
            return 2; // WUMPUS FOUND
        } else if (result == 1) {
            return 3; // HOLE FOUND
        } else {
            safe_board.set(current_position, 1);
            heuristic_board.addHeuristic(current_position, safe_board, result);
            heuristic_board.recalculate(current_position, safe_board, board);
            return 0;
        }
    }

    public static void move() {
        current_position = heuristic_board.getBestPosition(current_position);
    }

    public static void main(String[] args) {
        init();
        board.print();
        start();
    }

}


