package practica1;

import practica1.view.Frame;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

public class Controller {

    private final Elevator model;
    private final Frame view;
    private Thread thread;

    public Controller(Elevator model, Frame view) {
        this.model = model;
        this.view = view;
    }

    public void start() {
        model.addPropertyChangeListener(this::modelListener);
        view.addListener(new FrameClicked());
        view.setVisible(true);
    }

    private void modelListener(PropertyChangeEvent evt) {
        if(evt.getPropertyName() == "moving") {
            view.moveElevator((Elevator.Direction) evt.getNewValue());
        } else if(evt.getPropertyName() == "door"){
            view.openDoor((boolean) evt.getNewValue());
        } else if(evt.getPropertyName() == "floor") {
            int[] floors = view.askFloors((Elevator.Direction) evt.getOldValue(),  model.getCurrentFloor(), (int) evt.getNewValue());
            model.addFloorDestinations(floors);
        }
    }

    private class FrameClicked extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            Elevator.Request request = view.getRequest(e);
            if(!request.isValid()) return;
            model.requests.add(request);
            view.drawPersons(model.getRequestCount());
            if(thread == null || !thread.isAlive()){
                thread = new Thread(model);
                thread.start();
            }
        }
    }
}
