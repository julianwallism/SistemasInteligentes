package practica1;

public class Elevator {

    /* Constats */
    private static final int N_FLOORS = 11;
    private static final float DELTA = 0.5f; // in seconds

    /* State Variables */
    private int currentFloor;
    private boolean doorOpened;
    private Requests requests;
    private Direction direction;

    public Elevator() {

    }

    
    /* Function that implements the elevator's behaviour */
    public void run() {
        while (true) {
        }
    }
    

    /* A1. Go to the floor above, unless it is the top floor */
    private void goUp() {
        this.currentFloor = Math.max(currentFloor + 1, N_FLOORS);
        this.direction = Direction.UP;
    }

    /* A2. Go to the floor below, unless it is the bottom floor */
    private void goDown() {
        this.currentFloor = Math.min(currentFloor - 1, 0);
        this.direction = Direction.DOWN;
    }

    /* A3. Open the door */
    private void openDoor() {
        this.doorOpened = true;
    }

    /* A4. Close the door */
    private void closeDoor() {
        this.doorOpened = false;
    }

    /* A5. Wait for DELTA seconds, to simulate the time it takes for the passengers to enter/exit the elevator */
    private void waitDelta() {
        try {
            Thread.sleep((long) (DELTA * 1000));
        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
        }
    }

    
    /* Auxiliar Functions */
    private int nextAbove(boolean in) {
        for (int i = currentFloor; i < N_FLOORS; i++) {
            if ((in && requests.in[i] != -1) || requests.out[i]) {
                return i;
            }
        }
        return -1;
    }

    private int nextBelow(boolean in) {
        for (int i = currentFloor; i >= 0; i--) {
            if ((in && requests.in[i] != -1) || requests.out[i]) {
                return i;
            }
        }
        return -1;
    }

    
    /* Auxiliar Structures */
    public static class Requests {

        public int[] in; // true or false for each floor
        public boolean[] out; // 
        public Direction[] direction;
    }

    public enum Direction {
        UP,
        DOWN
    }
}
