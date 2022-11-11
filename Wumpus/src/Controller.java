import model.Model;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Controller {

    private Model model;
    private View view;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    public void start() {
        //model.addPropertyChangeListener(this::modelListener);
        view.addListener(new FrameClicked());
        view.setVisible(true);
    }

    private static class FrameClicked extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent evt) {

        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Model.SIZE = View.getBoardSize();
            Controller controller = new Controller(new Model(), new View());
            controller.start();
        });
    }
}
