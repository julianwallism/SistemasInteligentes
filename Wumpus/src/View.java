import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.Arrays;

public class View extends JFrame {

    private static final int PANEL_WIDTH = 600, PANEL_HEIGHT = 600;
    private static final int MIN = 4, MAX = 10;
    private Panel panel;

    public View() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Cueva del Monstruo");
        //setIconImage(new ImageIcon("assets/images/icon.png").getImage());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        panel = new Panel();
        add(panel);
        pack();

        setResizable(false);
        setLocationRelativeTo(null);
    }

    public int getBoardSize() {
        Integer[] possibilities = new Integer[MAX - MIN + 1];
        Arrays.setAll(possibilities, i -> i + MIN);
        Object result = JOptionPane.showInputDialog(null,
                "Introduzca el tamaño del tablero:",
                "Tamaño tablero",
                JOptionPane.PLAIN_MESSAGE,
                null,
                possibilities,
                4);
        return result == null ? Model.SIZE : (int) result;
    }

    public void addListener(MouseAdapter adapter) {
        panel.addMouseListener(adapter);
    }

    public class Panel extends JPanel {
        public Panel() {
            setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
            setBackground(Color.gray);
        }
    }
}
