import model.Model;
import model.Tile;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class View extends JFrame {

    /* Constants */
    public static final int PANEL_SIZE = 600;
    private static final int MIN = 4, MAX = 10;

    /* Images */
    private final static String IMAGES_PATH = "assets/images";
    private static int TILE_SIZE;
    private static HashMap<String, ImageIcon> image;

    /* Variables */
    private JPanel panel;
    private static JLabel[][] tile;

    private JButton startBtn, nextBtn;
    private JComboBox tileChooser;
    private JLabel textArea, tileLbl, speedLbl, speedValLbl;
    private JSlider speedSlider;

    public View() {
        loadImages();
        initComponents();
        addComponents();
    }

    private void initComponents() {
        setTitle("Cueva del Monstruo");
        //setIconImage(new ImageIcon("assets/images/icon.png").getImage());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        panel.setBackground(Color.gray);
        panel.setLayout(new GridLayout(Model.SIZE, Model.SIZE));
        tile = new JLabel[Model.SIZE][Model.SIZE];
        for (int i = 0; i < tile.length; i++) {
            for (int j = 0; j < tile[0].length; j++) {
                tile[i][j] = new JLabel();
                if(i == 0 && j == 0) tile[i][j].setBorder(BorderFactory.createLineBorder(Color.yellow,3));
                else tile[i][j].setBorder(BorderFactory.createLineBorder(Color.black));
                tile[i][j].setOpaque(true);
                tile[i][j].setSize(TILE_SIZE, TILE_SIZE);
                tile[i][j].setIcon(image.get("empty"));
                panel.add(tile[i][j]);
            }
        }

        textArea = new JLabel("<html>Selecione una casilla y haga click para colocarla.\nUna vez completado el tablero haga click en comenzar.</html>");

        tileLbl = new JLabel("Seleccione Casilla:");
        tileChooser = new JComboBox<>(Tile.Type.getPlacebleTypes());

        startBtn = new JButton("Comenzar");

        speedLbl = new JLabel("Velocidad");
        speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 5, 0);
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setPaintTicks(true);
        speedValLbl = new JLabel("manual");
        speedSlider.setEnabled(false);
        nextBtn = new JButton("->");
        nextBtn.setEnabled(false);
    }

    private void addComponents() {
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(tileLbl)
                                                .addGap(103, 103, 103))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(textArea, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(30, 30, 30))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(startBtn)
                                                .addGap(110, 110, 110))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(speedValLbl)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(speedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(tileChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(54, 54, 54))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(speedLbl)
                                                .addGap(125, 125, 125))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(nextBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(97, 97, 97))))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(25, 25, 25)
                                                .addComponent(textArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(tileLbl)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(tileChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(24, 24, 24)
                                                .addComponent(startBtn)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(speedLbl)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(speedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(speedValLbl))
                                                .addGap(18, 18, 18)
                                                .addComponent(nextBtn)
                                                .addGap(81, 81, 81))))
        );

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public static int getBoardSize() {
        Integer[] possibilities = new Integer[MAX - MIN + 1];
        Arrays.setAll(possibilities, i -> i + MIN);
        Object result = JOptionPane.showInputDialog(null,
                "Introduzca el tamaño del tablero:",
                "Tamaño tablero",
                JOptionPane.PLAIN_MESSAGE,
                null,
                possibilities,
                4);
        if (result == null) System.exit(0);
        return (int) result;
    }

    public static void loadImages() {
        TILE_SIZE = PANEL_SIZE / Model.SIZE;
        image = new HashMap<>();
        try {
            for (File imageFile : new File(IMAGES_PATH).listFiles()) {
                String name = imageFile.getName().replace(".png", "").toLowerCase();
                Image img = ImageIO.read(imageFile).getScaledInstance(TILE_SIZE, TILE_SIZE, Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(img);
                image.put(name, icon);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Updates the view based on the model
    public static void updateImages(Tile[][] board) {
        for (int i = 0; i < Model.SIZE; i++) {
            for (int j = 0; j < Model.SIZE; j++) {
                ImageIcon currentIcon = (ImageIcon) tile[i][j].getIcon(), newIcon;
                String imgName = "";
                ArrayList<Tile.Type> types = Tile.Type.asList(board[i][j].getType());
                if (types.contains(Tile.Type.HOLE)) imgName = "hole_";
                else if (types.contains(Tile.Type.WUMPUS)) imgName = "wumpus_";
                else {
                    for (Tile.Type type : types) {
                        imgName += type.toString().toLowerCase() + "_";
                    }
                }

                if(types.contains(Tile.Type.AGENT)) tile[i][j].setBorder(BorderFactory.createLineBorder(Color.red,2));
                else if (i==0 && j==0) tile[i][j].setBorder(BorderFactory.createLineBorder(Color.yellow,3));
                else tile[i][j].setBorder(BorderFactory.createLineBorder(Color.black));

                imgName = imgName.length() == 0 ? "empty" : (imgName.substring(0, imgName.length() - 1));
                newIcon = image.get(image.containsKey(imgName) ? imgName : "error");
                if (currentIcon.equals(newIcon)) continue;
                tile[i][j].setIcon(newIcon);
            }
        }
    }

    public String getSelectedType() {
        return tileChooser.getSelectedItem().toString().toUpperCase();
    }

    public int getSpeed() {
        int speed = speedSlider.getValue();
        if(speed == 0) {
            speedValLbl.setText("manual");
            nextBtn.setEnabled(true);
        } else {
            speedValLbl.setText(speed + "x");
            nextBtn.setEnabled(false);
        }
        return speed;
    }

    // When the startBtn gets clicked, block the tileChooser and the frame
    public void start() {
        tileChooser.setEnabled(false);
        startBtn.setEnabled(false);
        speedSlider.setEnabled(true);
        nextBtn.setEnabled(true);
        panel.removeMouseListener(panel.getMouseListeners()[0]);
    }

    public void addActionListener(ActionListener listener) {
        startBtn.addActionListener(listener);
        nextBtn.addActionListener(listener);
    }

    public void addSpeedListener(ChangeListener listener){
        speedSlider.addChangeListener(listener);
    }
    public void addMouseListener(MouseAdapter adapter) {
        panel.addMouseListener(adapter);
    }
}
