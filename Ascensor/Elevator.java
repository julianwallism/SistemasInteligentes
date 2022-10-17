package practica1;

import java.security.DigestInputStream;
import java.util.Arrays;

public class Elevator {

    /* Constats */
    private static final int N_FLOORS = 11;
    private static final float DELTA = 0.5f; // in seconds
    private static final Boolean IN = true;
    private static final Boolean OUT = false;

    /* State Variables */
    private int currentFloor;
    private boolean doorOpened;
    private Requests requests;
    private Direction direction;

    public Elevator(Requests requests) {
        this.currentFloor = 0;
        this.doorOpened = false;
        this.requests = requests;
        this.direction = Direction.NONE;
    }

    /* Function that implements the elevator's behaviour */
    public void run() {
        while (true) {
            System.out.println("Current floor: " + currentFloor);
            System.out.println("Direction: " + direction);
            System.out.println("In: " + Arrays.toString(requests.in));
            System.out.println("Out: " + Arrays.toString(requests.out));
            System.out.println("Direction: " + Arrays.toString(requests.direction));
            if (!doorOpened && direction == Direction.UP
                    && (requests.direction[currentFloor] == Direction.UP || requests.out[currentFloor]
                            || (currentFloor == N_FLOORS - 1 && requests.in[currentFloor] != -1))) {
                openDoor();
                waitDelta();
                closeDoor();
                requests.out[currentFloor] = false;
                if (requests.in[currentFloor] != -1) {
                    requests.out[requests.in[currentFloor]] = true;
                }
                requests.in[currentFloor] = -1;
                requests.direction[currentFloor] = Direction.NONE;
            } else if (!doorOpened && direction == Direction.DOWN && (requests.direction[currentFloor] == Direction.DOWN
                    || requests.out[currentFloor] || (currentFloor == 0 && requests.in[currentFloor] != -1))) {
                openDoor();
                waitDelta();
                closeDoor();
                requests.out[currentFloor] = false;
                if (requests.in[currentFloor] != -1) {
                    requests.out[requests.in[currentFloor]] = true;
                }
                requests.in[currentFloor] = -1;
                requests.direction[currentFloor] = Direction.NONE;
            } else if (!doorOpened && direction == Direction.UP && !requests.out[currentFloor]
                    && nextAbove(OUT) != -1) {
                goUp();
            } else if (!doorOpened && direction == Direction.UP && requests.direction[currentFloor] == Direction.NONE
                    && nextAbove(IN) == -1) {
                goUp();

            } else if (!doorOpened && direction == Direction.DOWN && !requests.out[currentFloor]
                    && nextBelow(OUT) != -1) {
                goDown();
            } else if (!doorOpened && direction == Direction.DOWN && requests.direction[currentFloor] == Direction.NONE
                    && nextBelow(IN) == -1) {
                goDown();
            } else if (!doorOpened && direction == Direction.UP && !requests.out[currentFloor]
                    && nextBelow(OUT) != -1) {
                goDown();
            } else if (!doorOpened && direction == Direction.UP && requests.direction[currentFloor] == Direction.DOWN
                    && nextBelow(IN) != -1) {
                goDown();
            } else if (!doorOpened && direction == Direction.DOWN && !requests.out[currentFloor]
                    && nextAbove(OUT) != -1) {
                goUp();
            } else if (!doorOpened && direction == Direction.DOWN && requests.direction[currentFloor] == Direction.NONE
                    && nextAbove(IN) != -1) {
                goUp();
            } else {
                break;
            }
        }
    }

    /* A1. Go to the floor above, unless it is the top floor */
    private void goUp() {
        this.currentFloor--;
        this.direction = Direction.UP;
        System.out.println("Going up to floor " + this.currentFloor);
    }

    /* A2. Go to the floor below, unless it is the bottom floor */
    private void goDown() {
        this.currentFloor++;
        this.direction = Direction.DOWN;
        System.out.println("Going down to floor " + this.currentFloor);
    }

    /* A3. Open the door */
    private void openDoor() {
        this.doorOpened = true;
        System.out.println("Door opened");

    }

    /* A4. Close the door */
    private void closeDoor() {
        this.doorOpened = false;
        System.out.println("Door closed");
    }

    /*
     * A5. Wait for DELTA seconds, to simulate the time it takes for the passengers
     * to enter/exit the elevator
     */
    private void waitDelta() {
        try {
            System.out.println("Waiting for " + DELTA + " seconds");
            Thread.sleep((long) (DELTA * 1000));
        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /* Auxiliar Functions */

    // Returns the next higher floor for which there is an exit/entry request
    private int nextAbove(boolean in) {
        for (int i = currentFloor; i < N_FLOORS; i++) {
            if ((in && requests.in[i] != -1) || requests.out[i]) {
                return i;
            }
        }
        return -1;
    }

    // Returns the next lower floor for which there is an exit/entry request
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
        public int[] in; // -1 if no request, otherwise the number of the floor where they want to go
        public boolean[] out; // True if there is a passenger that wants to exit the elevator in that floor
        public Direction[] direction; // The direction of the passenger that wants to enter the elevator in that floor
    }

    public enum Direction {
        UP,
        DOWN,
        NONE
    }
}
