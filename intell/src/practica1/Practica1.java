package practica1;

import practica1.view.Frame;

import javax.swing.*;

public class Practica1  {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Controller controller = new Controller(new Elevator(), new Frame());
            controller.start();
        });
    }
}
