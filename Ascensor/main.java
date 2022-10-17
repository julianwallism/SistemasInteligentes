package practica1;

import practica1.Elevator.Direction;

public class Main {

    public static void main(String[] args) {
        Elevator.Requests ruquests = new Elevator.Requests();
        requests.in = new int[] { 9, -1, -1, 7, -1, 2, -1, -1, -1, -1, 6 };
        requests.out = new boolean[] { false, false, false, false, false, false, false, false, false, false, false };
        requests.direction = new Direction[] { Direction.UP, Direction.NONE, Direction.NONE, Direction.UP,
                Direction.NONE, Direction.DOWN, Direction.NONE, Direction.NONE, Direction.NONE, Direction.NONE,
                Direction.DOWN };
        Elevator elevator = new Elevator(requests);
        elevator.run();
    }
}
