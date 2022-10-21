package practica1;

import java.util.ArrayList;
import java.util.Arrays;

public class Elevator {

    /* Constats */
    public static final int N_FLOORS = 11;
    private static final float DELTA = 0.1f; // in seconds
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
            // print requests.out array
            if (doorOpened) {
                continue;
            }
            if (requests.checkFloor(Direction.UP) || requests.checkFloor(Direction.DOWN)) {
                openDoor();
                waitDelta();
                closeDoor();
                requests.out[currentFloor] = false;
//                System.out.println(currentFloor);
//                System.out.println(requests.in[currentFloor]);
                if (!requests.in[currentFloor].isEmpty()) {
//                    System.out.println(currentFloor);
//                    System.out.println(requests.directions[currentFloor]);
                    if (requests.directions[currentFloor] == Direction.BOTH) {
                        if (direction == Direction.UP) {
                            for (int idx = 0; idx < requests.in[currentFloor].size(); idx++) {
                                if (requests.in[currentFloor].get(idx) > currentFloor) {
                                    requests.out[requests.in[currentFloor].get(idx)] = true;
                                    requests.in[currentFloor].set(idx, -1);
                                }
                                requests.in[currentFloor].removeAll(new ArrayList<Integer>() {
                                    {
                                        add(-1);
                                    }
                                });
                            }
                            requests.directions[currentFloor] = Direction.DOWN;
                        } else {
                            for (int idx = 0; idx < requests.in[currentFloor].size(); idx++) {
                                if (requests.in[currentFloor].get(idx) < currentFloor) {
                                    requests.out[requests.in[currentFloor].get(idx)] = true;
                                    requests.in[currentFloor].set(idx, -1);
                                }
                                requests.in[currentFloor].removeAll(new ArrayList<Integer>() {
                                    {
                                        add(-1);
                                    }
                                });
                            }

                            requests.directions[currentFloor] = Direction.UP;
                        }
                    } else {
//                        System.out.println(currentFloor);
//                        System.out.println(requests.in[currentFloor]);
//                        System.out.println(requests.in[currentFloor].size());
                        for (int idx = 0; idx < requests.in[currentFloor].size(); idx++) {
//                            System.out.println(idx);
//                            System.out.println(currentFloor);
//                            System.out.println(requests.in[currentFloor].get(idx));
                            requests.out[requests.in[currentFloor].get(idx)] = true;
                        }
                        requests.in[currentFloor].clear();
                        requests.directions[currentFloor] = Direction.NONE;
                    }
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
            if ((in && !requests.in[i].isEmpty()) || requests.out[i]) {
                return true;
            }
        }
        return false;
    }

    // Returns the next lower floor for which there is an exit/entry request
    private boolean nextBelow(boolean in) {
        for (int i = currentFloor; i >= 0; i--) {
            if ((in && !requests.in[i].isEmpty()) || requests.out[i]) {
                return true;
            }
        }
        return false;
    }

    /* Auxiliar Structures */
    public class Requests {

        // in is an array of arraylists of ints
        public ArrayList<Integer>[] in = new ArrayList[N_FLOORS]; // -1 if no request, otherwise the number of the floor
        // where
        // they want to go
        public boolean[] out; // True if there is a passenger that wants to exit the elevator in that floor
        public Direction[] directions; // The direction of the passenger that wants to enter the elevator in that floor

        private boolean checkFloor(Direction dir) {
            if (dir == Direction.UP) {
                return direction == dir
                        && (directions[currentFloor] == dir 
                                || directions[currentFloor] == Direction.BOTH
                                || out[currentFloor] || (currentFloor == N_FLOORS - 1 && !in[currentFloor].isEmpty()));
            } else if (dir == Direction.DOWN) {
                return direction == dir
                        && (directions[currentFloor] == dir
                                || directions[currentFloor] == Direction.BOTH
                                || out[currentFloor] || (currentFloor == 0 && !in[currentFloor].isEmpty()));
            }
            return false;
        }

        public Requests() {
            for (int i = 0; i < N_FLOORS; i++) {
                in[i] = new ArrayList<Integer>();
            }
        }

        private boolean checkAbove() {
            return (!out[currentFloor] && nextAbove(OUT) || directions[currentFloor].isNone() && nextAbove(IN));
        }

        private boolean checkBelow() {
            return !out[currentFloor] && nextBelow(OUT) || directions[currentFloor].isNone() && nextBelow(IN);
        }

        public void setRequest(int origin, int destination) {
            in[origin].add(destination);
            out[origin] = true;
            directions[origin] = destination - origin <= 0 ? Direction.DOWN : Direction.UP;
        }
    }

    public enum Direction {
        UP,
        DOWN,
        BOTH,
        NONE;

        public boolean isNone() {
            return this == NONE;
        }
    }
}
