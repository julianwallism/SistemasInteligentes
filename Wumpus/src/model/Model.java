package model;
import model.Tile.Type;

public class Model {
    public static int SIZE;
    public Tile[][] board;

    public Model() {
        board = new Tile[SIZE][SIZE];

        Tile tile = new Tile(Type.EMPTY);
        tile.addType(Type.GOLD);
        tile.addType(Type.EMPTY);

        tile.visit();
    }

    public void init(){
        board[0][0].visit();
    }
}
