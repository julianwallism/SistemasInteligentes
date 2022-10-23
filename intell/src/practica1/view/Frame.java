package practica1.view;

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
import javax.swing.*;

import practica1.Elevator;

public class Frame extends JFrame {

    /* Constants */
    private static final int PANEL_WIDTH = 600, PANEL_HEIGHT = 600;
    private static final int ELEVATOR_WIDTH = 60, ELEVATOR_HEIGHT = PANEL_HEIGHT / Elevator.N_FLOORS;
    private static final int BUTTON_WIDTH = ELEVATOR_HEIGHT / 2, BUTTON_HEIGHT = ELEVATOR_HEIGHT / 2;

    private Panel panel;
    private Image openImage, closedImage;
    private Image bluePerson, redperson;
    private JButton[] downButtons, upButtons;

    public Frame() {
        try {
            openImage = ImageIO.read(new File("assets/images/open.png"));
            openImage = openImage.getScaledInstance(ELEVATOR_WIDTH, ELEVATOR_HEIGHT, Image.SCALE_SMOOTH);
            closedImage = ImageIO.read(new File("assets/images/close.png"));
            closedImage = closedImage.getScaledInstance(ELEVATOR_WIDTH, ELEVATOR_HEIGHT, Image.SCALE_SMOOTH);

//            bluePerson = ImageIO.read(new File("assets/images/blue.png"));
//            bluePerson = bluePerson.getScaledInstance(ELEVATOR_HEIGHT, ELEVATOR_HEIGHT, Image.SCALE_SMOOTH);
//            redperson = ImageIO.read(new File("assets/images/red.png"));
//            redperson = redperson.getScaledInstance(ELEVATOR_HEIGHT, ELEVATOR_HEIGHT, Image.SCALE_SMOOTH);
        } catch (IOException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
            return;
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

        downButtons = new JButton[Elevator.N_FLOORS];
        upButtons = new JButton[Elevator.N_FLOORS];
        for (int i = 0; i < Elevator.N_FLOORS; i++) {
            downButtons[i] = new JButton("\u8595");
            upButtons[i] = new JButton("\u8593");
        }

        addComponents();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void addComponents() {
        for (int i = 0; i < 2; i++) {
//            add(downButtons[i]);
//            //add(upButtons[i]);
//            downButtons[i].setBounds(PANEL_WIDTH/2 - 3*ELEVATOR_HEIGHT/2, (i) * ELEVATOR_HEIGHT + ELEVATOR_HEIGHT/4, ELEVATOR_HEIGHT/2, ELEVATOR_HEIGHT/2);
//            //upButtons[i].setBounds();
//            downButtons[i].repaint();
        }
    }

    public void addListener(MouseAdapter adapter) {
        panel.addMouseListener(adapter);
    }

    public Elevator.Request getRequest(MouseEvent e) {
        int floor = -1;
        Elevator.Direction dir = Elevator.Direction.NONE;
        for (int i = 0; i < Elevator.N_FLOORS; i++) {
            if (e.getY() >= (i) * ELEVATOR_HEIGHT + ELEVATOR_HEIGHT / 4 && e.getY() < (i) * ELEVATOR_HEIGHT + ELEVATOR_HEIGHT / 4 + ELEVATOR_HEIGHT / 2) {
                if (e.getX() >= PANEL_WIDTH / 2 - 3 * ELEVATOR_HEIGHT / 2 && e.getX() < PANEL_WIDTH / 2 - 3 * ELEVATOR_HEIGHT / 2 + ELEVATOR_HEIGHT / 2) {
                    floor = Elevator.N_FLOORS - i - 1;
                    dir = Elevator.Direction.DOWN;
                } else if (e.getX() >= PANEL_WIDTH / 2 + ELEVATOR_HEIGHT && e.getX() < PANEL_WIDTH / 2 + ELEVATOR_HEIGHT + ELEVATOR_HEIGHT / 2) {
                    floor = Elevator.N_FLOORS - i - 1;
                    dir = Elevator.Direction.UP;
                }
                break;
            }
        }
        return new Elevator.Request(floor, dir);
    }

    public void moveElevator(Elevator.Direction dir) {
        panel.currentFloor++;
        panel.repaint();
    }

    public void openDoor(boolean open) {
        panel.opened = open;
        panel.repaint();
    }


    public class Panel extends JPanel {
        private int currentFloor = 0;
        private boolean opened = false;

        public Panel() {
            setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
            setBackground(new Color(240, 161, 161));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics = (Graphics2D) g;
            drawBackgrounds(graphics);
            drawFloors(graphics);
            drawElevator(graphics, opened ? openImage : closedImage);
            drawPersons(graphics);
        }

        private void drawBackgrounds(Graphics2D g) {
            g.setStroke(new BasicStroke(1.5f));
            g.drawRect(PANEL_WIDTH / 2 - ELEVATOR_WIDTH / 2, 0, ELEVATOR_WIDTH + 1, PANEL_HEIGHT);
            g.setColor(new Color(150, 75, 0));
            g.fillRect(PANEL_WIDTH / 2 - ELEVATOR_WIDTH / 2, 0, ELEVATOR_WIDTH + 1, PANEL_HEIGHT);
        }

        private void drawFloors(Graphics2D g) {
            g.setColor(Color.black);
            for (int i = 0; i < Elevator.N_FLOORS; i++) {
                g.drawLine(0, i * ELEVATOR_HEIGHT, PANEL_WIDTH, i * ELEVATOR_HEIGHT);
                g.drawRect(PANEL_WIDTH / 2 - 3 * ELEVATOR_HEIGHT / 2, (i) * ELEVATOR_HEIGHT + ELEVATOR_HEIGHT / 4, ELEVATOR_HEIGHT / 2, ELEVATOR_HEIGHT / 2);
                g.drawRect(PANEL_WIDTH / 2 + ELEVATOR_HEIGHT, (i) * ELEVATOR_HEIGHT + ELEVATOR_HEIGHT / 4, ELEVATOR_HEIGHT / 2, ELEVATOR_HEIGHT / 2);
            }
        }

        private void drawElevator(Graphics2D g, Image image) {
            g.drawImage(image, PANEL_WIDTH / 2 - ELEVATOR_WIDTH / 2, PANEL_HEIGHT - (ELEVATOR_HEIGHT * (currentFloor + 1)), ELEVATOR_WIDTH, ELEVATOR_HEIGHT, null);
        }

        private void drawPersons(Graphics2D g) {
//            for (int i = 0; i < persons.length; i++) {
//                ArrayList<Boolean> floor = persons[i];
//                for (int j = 0; j < floor.size(); j++) {
//                    g.drawImage(floor.get(j) ? redperson : bluePerson, ELEVATOR_WIDTH * (j + 1), PANEL_HEIGHT - (ELEVATOR_HEIGHT * (i + 1)), ELEVATOR_HEIGHT, ELEVATOR_HEIGHT, null);
//                }
//            }
        }
    }
}
