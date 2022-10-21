package practica1;

import java.util.ArrayList;

import practica1.Elevator.Direction;
import practica1.Elevator.Requests;
import view.Frame;

public class Practica1 {

    public static void main(String[] args) {
        Elevator elevator = new Elevator();
        Requests requests = elevator.new Requests();
        requests.in[7].add(1);
        requests.out = new boolean[] { false, false, false, false, false, false, false, false, false, false, false };
        requests.directions = new Direction[] {Direction.NONE, Direction.NONE, Direction.NONE, Direction.NONE,
                Direction.NONE, Direction.NONE, Direction.NONE, Direction.DOWN, Direction.NONE, Direction.NONE,
                Direction.NONE };
        elevator.setRequests(requests);
        elevator.run();

//        Frame frame = new Frame();
//        frame.setVisible(true);
    }
}
