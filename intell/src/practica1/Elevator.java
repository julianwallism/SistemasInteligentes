package practica1;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;

public class Elevator extends AbstractModel implements Runnable{

    /* Constats */
    public static final int N_FLOORS = 11;
    private static final float DELTA = 1; // in seconds
    private static final Boolean IN = true;

    private static final Boolean OUT = false;

    /* State Variables */
    private int currentFloor;
    private boolean doorOpened;
    public Requests requests;
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
    @Override
    public void run() {
        while (true) {
            // print requests.out array
            if (doorOpened) {
                continue;
            }
            // If it's going up AND (someone who is also going up wants to get in
            //      OR someone wants to get out in this floor OR (we're in the last floor AND someone wants to get in))
            if (direction == Direction.UP
                    && (requests.directions[currentFloor] == Direction.UP || requests.out[currentFloor]
                    || (requests.directions[currentFloor] == Direction.DOWN && !nextAbove(OUT)))) {
                // Open and close doors
                openDoor();
                waitDelta();
                closeDoor();
                // Everyone got out, we don't have anymore out requests for this floor
                requests.out[currentFloor] = false;
                // If someone wants to get in, they'll press the corresponding out button
                // We can also can unmark our internal arrays, since everyone got in
                if (requests.directions[currentFloor] == Direction.BOTH) {
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
                } else if (requests.directions[currentFloor] != Direction.NONE) {
                    for (int idx = 0; idx < requests.in[currentFloor].size(); idx++) {
                        requests.out[requests.in[currentFloor].get(idx)] = true;
                        // Send notification to view, to show jdialogs so the user can enter the floor they want to go
                    }
                    requests.in[currentFloor].clear();
                    requests.directions[currentFloor] = Direction.NONE;
                }

                // If it's going down AND (someone who is also going down wants to get in
                //      OR someone wants to get out in this floor OR (we're in the first floor AND someone wants to get in))
            } else if (direction == Direction.DOWN
                    && (requests.directions[currentFloor] == Direction.DOWN || requests.out[currentFloor]
                    || (requests.directions[currentFloor] == Direction.UP && !nextBelow(OUT)))) {
                // Open and close doors
                openDoor();
                waitDelta();
                closeDoor();
                // Everyone got out, we don't have anymore requests for this floor
                requests.out[currentFloor] = false;
                // If someone wants to get in, they'll press the corresponding out button
                // We can also can unmark our internal arrays, since everyone got in
                if (requests.directions[currentFloor] == Direction.BOTH) {
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
                } else if (requests.directions[currentFloor] != Direction.NONE) {
                    for (int idx = 0; idx < requests.in[currentFloor].size(); idx++) {
                        requests.out[requests.in[currentFloor].get(idx)] = true;
                    }
                    requests.in[currentFloor].clear();
                    requests.directions[currentFloor] = Direction.NONE;
                }

                // If we're going up AND no one wants to get out AND there is a floor above where someone wants to get out --> GO_UP
            } else if (direction == Direction.UP && !requests.out[currentFloor] && nextAbove(OUT)) {
                goUp();
                // If we're going up AND no one wants to get in AND there is a floor above where someone wants to get in --> GO_UP
            } else if (direction == Direction.UP && requests.directions[currentFloor] == Direction.NONE && nextAbove(IN)) {
                goUp();
                // If we're going down AND no one wants to get out AND there is a floor below where someone wants to get out --> GO_DOWN
            } else if (direction == Direction.DOWN && !requests.out[currentFloor] && nextBelow(OUT)) {
                goDown();
                // If we're going down AND no one wants to get in AND there is a floor below where someone wants to get in --> GO_DOWN
            } else if (direction == Direction.DOWN && requests.directions[currentFloor] == Direction.NONE && nextBelow(IN)) {
                goDown();
                // If we're going up AND no one wants to get out AND there is a floor below where someone wants to get out --> GO_DOWN
            } else if (direction == Direction.UP && !requests.out[currentFloor] && nextBelow(OUT)) {
                goDown();
                // If we're going up AND no one wants to get in AND there is a floor below where someone wants to get in --> GO_DOWN
            } else if (direction == Direction.UP && requests.directions[currentFloor] == Direction.NONE && nextBelow(IN)) {
                goDown();
                // If we're going down AND no one wants to get out && there is a floor above where someone wants to get out --> GO_UP
            } else if (direction == Direction.DOWN && !requests.out[currentFloor] && nextAbove(OUT)) {
                goUp();
                // If we're going down AND no one wants to get in AND there is a floor above where someone wants to get in --> GO_DOWN
            } else if (direction == Direction.DOWN && requests.directions[currentFloor] == Direction.NONE && nextAbove(IN)) {
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
        firePropertyChange("moving", null, Direction.UP);
        System.out.println("Going up to floor " + this.currentFloor);
        waitDelta();
    }

    /* A2. Go to the floor below, unless it is the bottom floor */
    private void goDown() {
        this.currentFloor--;
        this.direction = Direction.DOWN;
        firePropertyChange("moving", null, Direction.DOWN);
        System.out.println("Going down to floor " + this.currentFloor);
        waitDelta();
    }

    /* A3. Open the door */
    private void openDoor() {
        this.doorOpened = true;
        firePropertyChange("door", null, doorOpened);
        System.out.println("Door opened");

    }

    /* A4. Close the door */
    private void closeDoor() {
        this.doorOpened = false;
        firePropertyChange("door", null, doorOpened);
        System.out.println("Door closed");
    }

    /*
     * A5. Wait for DELTA seconds, to simulate the time it takes for the passengers
     * to enter/exit the elevator
     */
    private void waitDelta() {
        try {
            //System.out.println("Waiting for " + DELTA + " seconds");
            Thread.sleep((long) (DELTA * 1000));
        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /* Auxiliar Functions */
    // Returns the next higher floor for which there is an exit/entry request
    private boolean nextAbove(boolean in) {
        for (int i = currentFloor; i < N_FLOORS; i++) {

            if ((in && requests.directions[i] != Direction.NONE) || requests.out[i]) {
                return true;
            }
        }
        return false;
    }

    // Returns the next lower floor for which there is an exit/entry request
    private boolean nextBelow(boolean in) {
        for (int i = currentFloor; i >= 0; i--) {
            if ((in && requests.directions[i] != Direction.NONE) || requests.out[i]) {
                return true;
            }
        }
        return false;
    }

    /* Auxiliar Structures */
    public class Requests {

        // in is an array of arraylists of ints
        public ArrayList<Integer>[] in; // -1 if no request, otherwise the number of the floor
        // where
        // they want to go
        public boolean[] out; // True if there is a passenger that wants to exit the elevator in that floor
        public Direction[] directions; // The direction of the passenger that wants to enter the elevator in that floor

        public Requests() {
            in = new ArrayList[N_FLOORS];
            out = new boolean[N_FLOORS];
            directions = new Direction[N_FLOORS];
            for (int i = 0; i < N_FLOORS; i++) {
                in[i] = new ArrayList<Integer>();
                directions[i] = Direction.NONE;
            }

        }

        public void add(Request request) {
            if (directions[request.floor] != Direction.NONE && directions[request.floor] != direction) {
                directions[request.floor] = Direction.BOTH;
            } else {
                directions[request.floor] = request.direction;
            }
        }
    }

    public static class Request {

        private int floor;
        private Direction direction;

        public Request(int floor, Direction direction) {
            this.floor = floor;
            this.direction = direction;
        }

        public boolean isValid() {
            return floor != -1;
        }

        @Override
        public String toString() {
            return "Request{%d, %s}".formatted(floor, direction);
        }
    }

    public enum Direction {
        UP,
        DOWN,
        BOTH,
        NONE;
    }
}
