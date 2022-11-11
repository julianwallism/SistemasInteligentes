import model.Model;
import model.Tile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class View extends JFrame {

    /* Constants */
    private static final int PANEL_SIZE = 600;
    private static final int MIN = 4, MAX = 10;

    /* Images */
    private final static String IMAGES_PATH = "assets/images";
    private static int TILE_SIZE;
    private static HashMap<String, Image> image;

    /* Variables */
    private JPanel panel;
    private JLabel[][] tile;

    private JButton startBtn, nextBtn;
    private JComboBox tileChooser;
    private JLabel tileLbl, speedLbl, textArea;
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
                tile[i][j].setOpaque(true);
                tile[i][j].setSize(TILE_SIZE, TILE_SIZE);
                tile[i][j].setIcon(new ImageIcon(image.get("agent_stench_wind_gold")));
                panel.add(tile[i][j]);
            }
        }

        textArea = new JLabel("Selecione una casilla y haga click para colocarla.\nUna vez completado el tablero haga click en comenzar.");

        tileLbl = new JLabel("Seleccione Casilla:");
        tileChooser = new JComboBox<>(Tile.Type.getPlacebleTypes());
        //tileChooser.addActionListener((e) -> selectChesspiece());

        startBtn = new JButton("Comenzar");

        speedLbl = new JLabel("Velocidad");
        speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 5, 0);
        nextBtn = new JButton("->");
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
                                                .addComponent(tileChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(87, 87, 87))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(58, 58, 58)
                                                                .addComponent(speedLbl))
                                                        .addComponent(speedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(63, 63, 63))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(nextBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(96, 96, 96))))
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
                                                .addComponent(speedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        if(result == null) System.exit(0);
        return (int) result;
    }
    public static void loadImages(){
        TILE_SIZE = PANEL_SIZE / Model.SIZE;
        image = new HashMap<>();
        try {
            for(File imageFile: new File(IMAGES_PATH).listFiles()) {
                String name = imageFile.getName().replace(".png", "").toLowerCase();
                Image img = ImageIO.read(imageFile).getScaledInstance(TILE_SIZE, TILE_SIZE, Image.SCALE_SMOOTH);
                image.put(name, img);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addListener(MouseAdapter adapter) {
        panel.addMouseListener(adapter);
    }
}
