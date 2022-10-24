package practica1;

public class Elevator {

    /* Constats */
    public static final int N_FLOORS = 11;
    private static final float DELTA = 0.5f; // in seconds
    private static final Boolean IN = true;
    private static final Boolean OUT = false;

    /* State Variables */
    private int currentFloor;
    private boolean doorOpened;
    private Requests requests;
    private Direction direction;

    public Elevator() {
        this.currentFloor = 0;
        this.doorOpened = false;
        this.requests = new Requests();
        this.direction = Direction.UP;
    }
    
    public void setRequests(Requests requests) {
        this.requests = requests;
    }

    /* Function that implements the elevator's behaviour */
    public void run() {
        while (true) {
            if (doorOpened) continue;
            if (requests.checkFloor(Direction.UP) || requests.checkFloor(Direction.DOWN)) {
                openDoor();
                waitDelta();
                closeDoor();
                requests.out[currentFloor] = false;
                if (requests.in[currentFloor] != -1) {
                    requests.out[requests.in[currentFloor]] = true;
                    requests.in[currentFloor] = -1;
                    requests.directions[currentFloor] = Direction.NONE;
                }
            } else if (!direction.isNone() && requests.checkAbove()) {
                goUp();
            } else if (!direction.isNone() && requests.checkBelow()) {
                goDown();
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
    private boolean nextAbove(boolean in) {
        for (int i = currentFloor; i < N_FLOORS; i++) {
            if ((in && requests.in[i] != -1) || requests.out[i]) {
                return true;
            }
        }
        return false;
    }

    // Returns the next lower floor for which there is an exit/entry request
    private boolean nextBelow(boolean in) {
        for (int i = currentFloor; i >= 0; i--) {
            if ((in && requests.in[i] != -1) || requests.out[i]) {
                return true;
            }
        }
        return false;
    }

    /* Auxiliar Structures */
    public class Requests {

        public int[] in; // -1 if no request, otherwise the number of the floor where they want to go
        public boolean[] out; // True if there is a passenger that wants to exit the elevator in that floor
        public Direction[] directions; // The direction of the passenger that wants to enter the elevator in that floor
        
        private boolean checkFloor(Direction dir){
            if(dir == Direction.UP) {
                return direction == dir && (directions[currentFloor] == dir || out[currentFloor] || (currentFloor == N_FLOORS - 1 && in[currentFloor] != -1));
            } else if(dir == Direction.DOWN) {
                return direction == dir && (directions[currentFloor] == dir || out[currentFloor] || (currentFloor == 0 && in[currentFloor] != -1));
            }
            return false;
        }
        
        private boolean checkAbove(){
            return (!out[currentFloor] && nextAbove(OUT) || directions[currentFloor].isNone() && nextAbove(IN));
        }
        
        private boolean checkBelow() {
            return !out[currentFloor] && nextBelow(OUT) || directions[currentFloor].isNone() && nextBelow(IN);
        }
        
        public void setRequest(int origin, int destination){
            in[origin] = destination;
            out[origin] = true;
            directions[origin] = destination - origin <= 0 ? Direction.DOWN : Direction.UP;
        }
    }

    public enum Direction {
        UP,
        DOWN,
        NONE;

        public boolean isNone() {
            return this == NONE;
        }
    }
}
