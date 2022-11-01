package practica1.view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.Timer;

import practica1.Elevator;

public class Frame extends JFrame {

    /* Constants */
    private static final int PANEL_WIDTH = 600, PANEL_HEIGHT = 600;
    private static final int ELEVATOR_WIDTH = 60, ELEVATOR_HEIGHT = PANEL_HEIGHT / Elevator.N_FLOORS;
    private static final int BUTTON_WIDTH = ELEVATOR_HEIGHT / 2, BUTTON_HEIGHT = ELEVATOR_HEIGHT / 2;

    private Panel panel;
    private Image openImage, closedImage;
    private Image bluePerson, redPerson, blueArrow, redArrow;
    private JButton[] downButtons, upButtons;

    public Frame() {
        try {
            openImage = ImageIO.read(new File("assets/images/open_elevator.png"));
            openImage = openImage.getScaledInstance(ELEVATOR_WIDTH, ELEVATOR_HEIGHT, Image.SCALE_SMOOTH);
            closedImage = ImageIO.read(new File("assets/images/closed_elevator.png"));
            closedImage = closedImage.getScaledInstance(ELEVATOR_WIDTH, ELEVATOR_HEIGHT, Image.SCALE_SMOOTH);

            redPerson = ImageIO.read(new File("assets/images/red.png"));
            redPerson = redPerson.getScaledInstance(ELEVATOR_WIDTH / 2, ELEVATOR_HEIGHT, Image.SCALE_SMOOTH);
            bluePerson = ImageIO.read(new File("assets/images/blue.png"));
            bluePerson = bluePerson.getScaledInstance(ELEVATOR_WIDTH / 2, ELEVATOR_HEIGHT, Image.SCALE_SMOOTH);

            blueArrow = ImageIO.read(new File("assets/images/subir2.png"));
            blueArrow = blueArrow.getScaledInstance(ELEVATOR_WIDTH / 2, ELEVATOR_HEIGHT, Image.SCALE_SMOOTH);
            redArrow = ImageIO.read(new File("assets/images/bajar2.png"));
            redArrow = redArrow.getScaledInstance(ELEVATOR_WIDTH / 2, ELEVATOR_HEIGHT, Image.SCALE_SMOOTH);
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

        setResizable(false);
        setLocationRelativeTo(null);
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
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(panel.new Animator(dir), 0, 25);
    }

    public void openDoor(boolean open) {
        panel.opened = open;
        panel.repaint();
    }

    public void drawPersons(int[][] count) {
        panel.personCount = count;
        panel.repaint();
    }

    public int[] askFloors(Elevator.Direction dir, int floor, int count) {
        panel.personCount[floor][dir.getInt()] = 0;
        Object[] possibilities = null;
        if (dir == Elevator.Direction.UP) {
            possibilities = new Integer[Elevator.N_FLOORS - floor];
            for (int i = 0; i < possibilities.length; i++) {
                possibilities[i] = floor + i;
            }
        } else if (dir == Elevator.Direction.DOWN) {
            possibilities = new Integer[floor];
            for (int i = 0; i < possibilities.length; i++) {
                possibilities[i] = i;
            }
        }
        int[] floors = new int[count];
        for (int i = 0; i < count; i++) {
            Object result = JOptionPane.showInputDialog(null,
                    "Introduzca el piso de destino:",
                    "Destino",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    possibilities,
                    floor);
            if (result != null && result instanceof Integer value) {
                floors[i] = value;
            }
        }
        return floors;
    }

    public class Panel extends JPanel {
        private int elevatorY = 0, nextFloor = 0;
        private boolean opened = false;
        private int[][] personCount = new int[Elevator.N_FLOORS][2];

        public Panel() {
            setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
            setBackground(new Color(240, 161, 161));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics = (Graphics2D) g;
            drawBackgrounds(graphics);
            drawFloors(graphics, redArrow, blueArrow);
            drawElevator(graphics, opened ? openImage : closedImage);
            drawPersons(graphics);
        }

        private void drawBackgrounds(Graphics2D g) {
            g.setStroke(new BasicStroke(1.5f));
            g.drawRect(PANEL_WIDTH / 2 - ELEVATOR_WIDTH / 2, 0, ELEVATOR_WIDTH + 1, PANEL_HEIGHT);
            g.setColor(new Color(150, 75, 0));
            g.fillRect(PANEL_WIDTH / 2 - ELEVATOR_WIDTH / 2, 0, ELEVATOR_WIDTH + 1, PANEL_HEIGHT);
        }

        private void drawFloors(Graphics2D g, Image redArrow, Image blueArrow) {
            g.setColor(Color.black);
            g.setFont(new Font("TimesRoman", Font.BOLD, 15));

            for (int i = 0; i < Elevator.N_FLOORS; i++) {
                g.drawLine(0, i * ELEVATOR_HEIGHT, PANEL_WIDTH, i * ELEVATOR_HEIGHT);
                g.drawRect(PANEL_WIDTH / 2 - 3 * ELEVATOR_HEIGHT / 2, (i) * ELEVATOR_HEIGHT + ELEVATOR_HEIGHT / 4, ELEVATOR_HEIGHT / 2, ELEVATOR_HEIGHT / 2);
                g.drawRect(PANEL_WIDTH / 2 + ELEVATOR_HEIGHT, (i) * ELEVATOR_HEIGHT + ELEVATOR_HEIGHT / 4, ELEVATOR_HEIGHT / 2, ELEVATOR_HEIGHT / 2);
                g.drawImage(redArrow,PANEL_WIDTH / 2 - 3 * ELEVATOR_HEIGHT / 2, (i) * ELEVATOR_HEIGHT + ELEVATOR_HEIGHT / 4, ELEVATOR_HEIGHT / 2, ELEVATOR_HEIGHT / 2, null);
                g.drawImage(blueArrow,PANEL_WIDTH / 2 + ELEVATOR_HEIGHT, (i) * ELEVATOR_HEIGHT + ELEVATOR_HEIGHT / 4, ELEVATOR_HEIGHT / 2, ELEVATOR_HEIGHT / 2, null);
                g.drawString(String.valueOf(Elevator.N_FLOORS - i - 1), 10, (i) * ELEVATOR_HEIGHT + ELEVATOR_HEIGHT / 2 + ELEVATOR_HEIGHT / 8);
            }
        }

        private void drawElevator(Graphics2D g, Image image) {
            g.drawImage(image, PANEL_WIDTH / 2 - ELEVATOR_WIDTH / 2, PANEL_HEIGHT - (ELEVATOR_HEIGHT + elevatorY), ELEVATOR_WIDTH, ELEVATOR_HEIGHT, null);
        }

        private void drawPersons(Graphics2D g) {
            for (int i = 0; i < personCount.length; i++) {
                int down = personCount[i][0];
                int up = personCount[i][1];
                for (int j = 0; j < down; j++) {
                    g.drawImage(redPerson, PANEL_WIDTH / 2 - 3 * ELEVATOR_WIDTH / 2 - (ELEVATOR_WIDTH * (j + 1) / 2), PANEL_HEIGHT - (ELEVATOR_HEIGHT * (i + 1)), ELEVATOR_HEIGHT / 2, ELEVATOR_HEIGHT, null);
                }
                for (int j = 0; j < up; j++) {
                    g.drawImage(bluePerson, PANEL_WIDTH / 2 + ELEVATOR_WIDTH + (ELEVATOR_WIDTH * (j + 1) / 2), PANEL_HEIGHT - (ELEVATOR_HEIGHT * (i + 1)), ELEVATOR_HEIGHT / 2, ELEVATOR_HEIGHT, null);
                }
            }
        }

        private class Animator extends TimerTask {

            private Elevator.Direction dir;

            public Animator(Elevator.Direction dir) {
                this.dir = dir;
                if (dir == Elevator.Direction.UP) {
                    panel.nextFloor = Math.min(Elevator.N_FLOORS, panel.nextFloor + 1);
                } else if (dir == Elevator.Direction.DOWN) {
                    panel.nextFloor = Math.max(0, panel.nextFloor - 1);
                }
            }

            @Override
            public void run() {
                if (dir == Elevator.Direction.UP && elevatorY / ELEVATOR_HEIGHT < nextFloor) {
                    elevatorY += 2;
                } else if(dir == Elevator.Direction.DOWN && elevatorY/ELEVATOR_HEIGHT >= nextFloor && elevatorY > 0) {
                    elevatorY -= 2;
                }else {
                        cancel();
                    }
                    repaint();
                }
            }
        }
    }
