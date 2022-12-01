import model.Model;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

public class Controller {

    private Model model;
    private View view;
    private Thread thread;
    private FrameClicked mouseListener;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        mouseListener = new FrameClicked();
    }

    public void start() {
        model.addPropertyChangeListener(this::modelListener);
        view.addMouseListener(mouseListener);
        view.addActionListener(this::viewActionPerformed);
        view.addSpeedListener(this::viewSpeedChanged);
        View.updateImages(model.getBoard());
        view.setVisible(true);

    }

    private void modelListener(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("movement")) {
            View.updateImages(model.getBoard());
        }
    }

    public void viewActionPerformed(ActionEvent evt) {
        switch (evt.getActionCommand()) {
            case "Comenzar" -> {
                view.start(true);
                if(thread == null) {
                    thread = new Thread(model);
                    thread.start();
                }
            }
            case "->" ->  model.setSpeed(0);
            case "Reiniciar Juego"->     {
                model.resetGame();
            }
            case "Reiniciar Casillas"->     {
                model.init();
                view.start(false);
                view.addMouseListener(mouseListener);
            }
        }
    }

    public void viewSpeedChanged(ChangeEvent evt) {
        int speed = view.getSpeed();
        model.setSpeed(speed);
    }

    private class FrameClicked extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent evt) {
            int i = evt.getY() / (View.PANEL_SIZE / Model.SIZE);
            int j = evt.getX() / (View.PANEL_SIZE / Model.SIZE);
            if(i==0 && j==0) return;
            model.changeTile(i, j, view.getSelectedType());
            View.updateImages(model.getBoard());
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
