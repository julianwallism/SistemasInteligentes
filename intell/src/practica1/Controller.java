package practica1;

import practica1.view.Frame;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

public class Controller {

    private final Elevator model;
    private final Frame view;

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
            System.out.println("moving");
            view.moveElevator((Elevator.Direction) evt.getNewValue());
        }
    }

    private class FrameClicked extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            Elevator.Request request = view.getRequest(e);
            if(!request.isValid()) return;
            System.out.println(request);
            model.requests.add(request);
            model.run();
//            Object[] possibilities = new Integer[Elevator.N_FLOORS];
//            for(int i = 0; i < possibilities.length; i++){
//                possibilities[i] = (Integer) i;
//            }
//            Object result = JOptionPane.showInputDialog(null,
//                    "Introduzca el piso de destino:",
//                    "Destino",
//                    JOptionPane.PLAIN_MESSAGE,
//                    null,
//                    possibilities,
//                    floor);
//            if(result == null) return;
//            int destination = (Integer) result;
//            if(destination == floor) return;
//
//            persons[floor].add(destination - floor <= 0);
//            repaint();
        }
    }
}
