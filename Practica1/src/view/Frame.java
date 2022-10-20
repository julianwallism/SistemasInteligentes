package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import practica1.Elevator;

public class Frame extends JFrame {

    /* Constants */
    private static final int PANEL_WIDTH = 600, PANEL_HEIGHT = 600;
    private static final int ELEVATOR_WIDTH = 60, ELEVATOR_HEIGHT = PANEL_HEIGHT / Elevator.N_FLOORS;

    private Panel panel;
    private Image openImage, closedImage;
    private Image bluePerson, redperson;
    private ArrayList<Boolean>[] persons;

    public Frame() {
        try {
            openImage = ImageIO.read(new File("assets/images/open.png"));
            openImage = openImage.getScaledInstance(ELEVATOR_WIDTH, ELEVATOR_HEIGHT, Image.SCALE_SMOOTH);
            closedImage = ImageIO.read(new File("assets/images/close.png"));
            closedImage = closedImage.getScaledInstance(ELEVATOR_WIDTH, ELEVATOR_HEIGHT, Image.SCALE_SMOOTH);

            bluePerson = ImageIO.read(new File("assets/images/blue.png"));
            bluePerson = bluePerson.getScaledInstance(ELEVATOR_HEIGHT, ELEVATOR_HEIGHT, Image.SCALE_SMOOTH);
            redperson = ImageIO.read(new File("assets/images/red.png"));
            redperson = redperson.getScaledInstance(ELEVATOR_HEIGHT, ELEVATOR_HEIGHT, Image.SCALE_SMOOTH);
        } catch (IOException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        persons = new ArrayList[Elevator.N_FLOORS];
        for (int i = 0; i < persons.length; i++) {
            persons[i] = new ArrayList<>();
        }
        initComponents();
    }

    private void initComponents() {
        setTitle("Elevator");
        setIconImage(new ImageIcon("assets/images/icon.png").getImage());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        panel = new Panel();
        add(panel);
        pack();

        //addComponents();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public class Panel extends JPanel {

        private int currentFloor = 0;
        private boolean opened = true;

        public Panel() {
            setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
            setBackground(new Color(240, 161, 161));
            addMouseListener(new Mouse());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics = (Graphics2D) g;
            drawBackgrounds(graphics);
            drawLines(graphics);
            drawElevator(graphics, opened ? openImage : closedImage);
            drawPersons(graphics);
        }

        private void drawBackgrounds(Graphics2D g) {
            g.setStroke(new BasicStroke(1.5f));
            g.drawRect(0, 0, ELEVATOR_WIDTH + 1, PANEL_HEIGHT);
            g.setColor(new Color(150, 75, 0));
            g.fillRect(0, 0, ELEVATOR_WIDTH + 1, PANEL_HEIGHT);
        }

        private void drawLines(Graphics2D g) {
            g.setColor(Color.black);
            for (int i = 1; i < Elevator.N_FLOORS; i++) {
                g.drawLine(0, i * ELEVATOR_HEIGHT, PANEL_WIDTH, i * ELEVATOR_HEIGHT);
            }
        }

        private void drawElevator(Graphics2D g, Image image) {
            g.drawImage(image, 0, PANEL_HEIGHT - (ELEVATOR_HEIGHT * (currentFloor + 1)), ELEVATOR_WIDTH, ELEVATOR_HEIGHT, null);
        }

        private void drawPersons(Graphics2D g) {
            for (int i = 0; i < persons.length; i++) {
                ArrayList<Boolean> floor = persons[i];
                for (int j = 0; j < floor.size(); j++) {
                    g.drawImage(floor.get(j) ? redperson : bluePerson, ELEVATOR_WIDTH * (j + 1), PANEL_HEIGHT - (ELEVATOR_HEIGHT * (i + 1)), ELEVATOR_HEIGHT, ELEVATOR_HEIGHT, null);
                }
            }
        }

        private class Mouse extends MouseAdapter {

            @Override
            public void mousePressed(MouseEvent e) {
                int floor = Elevator.N_FLOORS - e.getY() / ELEVATOR_HEIGHT - 1;
                Object[] possibilities = new Integer[Elevator.N_FLOORS];
                for(int i = 0; i < possibilities.length; i++){
                    possibilities[i] = (Integer) i;
                }
                Object result = JOptionPane.showInputDialog(null,
                        "Introduzca el piso de destino:",
                        "Destino",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        possibilities,
                        floor);
                if(result == null) return;
                int destination = (Integer) result;
                if(destination == floor) return;
                persons[floor].add(destination - floor <= 0);
                repaint();
            }

        }
    }
}
