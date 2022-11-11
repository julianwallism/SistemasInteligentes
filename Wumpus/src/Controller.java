import model.Model;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Controller {

    private static Model model;
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
            // Get the coordinates of the click and call the model to change the tile then update the view
            int i = evt.getY() / (View.PANEL_SIZE / Model.SIZE);
            int j = evt.getX() / (View.PANEL_SIZE / Model.SIZE);
            if(i==0 && j==0){
                return;
            }
            String name = View.tileChooser.getSelectedItem().toString().toUpperCase();
            model.changeTile(i, j, name);
            View.updateImages(model.getBoard());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Model.SIZE = View.getBoardSize();
            Controller controller = new Controller(new Model(), new View());
            controller.start();
            View.updateImages(model.getBoard());
        });
    }
}
