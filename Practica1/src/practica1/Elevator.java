package practica1;

public class Elevator {

    /* Constats */
    public static final int N_FLOORS = 8;
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
        this.direction = Direction.UP;
    }

    /* Function that implements the elevator's behaviour */
    public void run() {
        while (true) {
            // If it's going up AND (someone who is also going up wants to get in 
            //      OR someone wants to get out in this floor OR (we're in the last floor AND someone wants to get in))
            if (!doorOpened && direction == Direction.UP
                    && (requests.direction[currentFloor] == Direction.UP || requests.out[currentFloor]
                        || (currentFloor == N_FLOORS - 1 && requests.in[currentFloor] != -1))) {
                // Open and close doors
                openDoor();
                waitDelta();
                closeDoor();
                // Everyone got out, we don't have anymore out requests for this floor
                requests.out[currentFloor] = false;
                // If someone wants to get in, they'll press the corresponding out button
                // We can also can unmark our internal arrays, since everyone got in
                if (requests.in[currentFloor] != -1) {
                    requests.out[requests.in[currentFloor]] = true;
                    requests.in[currentFloor] = -1;
                    requests.direction[currentFloor] = Direction.NONE;

                }
                // If it's going down AND (someone who is also going down wants to get in
                //      OR someone wants to get out in this floor OR (we're in the first floor AND someone wants to get in))
            } else if (!doorOpened && direction == Direction.DOWN 
                    && (requests.direction[currentFloor] == Direction.DOWN || requests.out[currentFloor] 
                        || (currentFloor == 0 && requests.in[currentFloor] != -1))) {
                // Open and close doors
                openDoor();
                waitDelta();
                closeDoor();
                // Everyone got out, we don't have anymore requests for this floor
                requests.out[currentFloor] = false;
                // If someone wants to get in, they'll press the corresponding out button
                // We can also can unmark our internal arrays, since everyone got in
                if (requests.in[currentFloor] != -1) {
                    requests.out[requests.in[currentFloor]] = true;
                    requests.in[currentFloor] = -1;
                    requests.direction[currentFloor] = Direction.NONE;
                }
                // If we're going up AND no one wants to get out AND there is a floor above where someone wants to get out --> GO_UP
            } else if (!doorOpened && direction == Direction.UP && !requests.out[currentFloor] && nextAbove(OUT) != -1) {
                goUp();
                // If we're going up AND no one wants to get in AND there is a floor above where someone wants to get in --> GO_UP
            } else if (!doorOpened && direction == Direction.UP && requests.direction[currentFloor] == Direction.NONE && nextAbove(IN) != -1) {
                goUp();
                // If we're going down AND no one wants to get out AND there is a floor below where someone wants to get out --> GO_DOWN
            } else if (!doorOpened && direction == Direction.DOWN && !requests.out[currentFloor] && nextBelow(OUT) != -1) {
                goDown();
                // If we're going down AND no one wants to get in AND there is a floor below where someone wants to get in --> GO_DOWN
            } else if (!doorOpened && direction == Direction.DOWN && requests.direction[currentFloor] == Direction.NONE && nextBelow(IN) != -1) {
                goDown();
                // If we're going up AND no one wants to get out AND there is a floor below where someone wants to get out --> GO_DOWN
            } else if (!doorOpened && direction == Direction.UP && !requests.out[currentFloor] && nextBelow(OUT) != -1) {
                goDown();
                // If we're going up AND no one wants to get in AND there is a floor below where someone wants to get in --> GO_DOWN
            } else if (!doorOpened && direction == Direction.UP && requests.direction[currentFloor] == Direction.NONE && nextBelow(IN) != -1) {
                goDown();
                // If we're going down AND no one wants to get out && there is a floor above where someone wants to get out --> GO_UP
            } else if (!doorOpened && direction == Direction.DOWN && !requests.out[currentFloor] && nextAbove(OUT) != -1) {
                goUp();
                // If we're going down AND no one wants to get in AND there is a floor above where someone wants to get in --> GO_DOWN                
            } else if (!doorOpened && direction == Direction.DOWN && requests.direction[currentFloor] == Direction.NONE && nextAbove(IN) != -1) {
                goUp();
            } else {
                break;
            }
        }
    }

    /* A1. Go to the floor above, unless it is the top floor */
    private void goUp() {
        this.currentFloor++;
        this.direction = Direction.UP;
        System.out.println("Going up to floor " + this.currentFloor);
    }

    /* A2. Go to the floor below, unless it is the bottom floor */
    private void goDown() {
        this.currentFloor--;
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
