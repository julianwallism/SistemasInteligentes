package model;
import model.Tile.Type;

import java.sql.SQLOutput;

public class Model {
    public static int SIZE;
    public static Tile[][] board;
    public static final int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    public Model() {
        board = new Tile[SIZE][SIZE];
        // Initialize the board
       init();
    }

    // Change the tile at the given coordinates
    // Assumptions: we can't have "wumpus", "hole" and "gold" on the same tile
    public static void changeTile(int i, int j, String name) {
        int oldType = board[i][j].getType();
        Type newType = Type.valueOf(name);

        if(!Type.isType(oldType, newType)) { // The tile is not of the same type
            if(Type.isType(oldType, Type.HOLE)){ // The tile has "hole" type
                board[i][j].removeType(Type.HOLE); // Remove the "hole" type
                removeNeighbours(i, j, oldType);  // Remove the "breeze" type from the neighbours, if it's the only "hole" neighbouring them
            }else if(Type.isType(oldType, Type.WUMPUS)){ // Same as above, but for "wumpus"
                board[i][j].removeType(Type.WUMPUS);
                removeNeighbours(i, j, oldType);
            } else if(Type.isType(oldType, Type.GOLD)){ // Same as above, but for "gold", without removing the neighbours
                board[i][j].removeType(Type.GOLD);
            }
            board[i][j].addType(newType); // Now that we've removed the old type, add the new one
            calculateNeighbours(i, j); // Calculate the neighbours of the tile, to add the "breeze" and "stench" types
        }else{ // The tile is of the same type
            board[i][j].removeType(newType); // Remove the type
            removeNeighbours(i, j, newType.bit); // Remove the neighbours, if it's the only "hole/wumpus" neighbouring them
        }
    }

    // Given a tile, calculate the neighbours and add the "breeze" and "stench" types
    public static void calculateNeighbours(int i, int j){
        int type = board[i][j].getType();
        for (int[] direction : directions) {
            int x = i + direction[0];
            int y = j + direction[1];
            if(checkEdges(x, y)){
                if(Type.isType(type, Type.HOLE)){
                    board[x][y].addType(Type.BREEZE);
                }else if(Type.isType(type, Type.WUMPUS)){
                    board[x][y].addType(Type.STENCH);
                }
           }
        }
    }

    // Given a tile, remove the neighbours, if it's the only "hole/wumpus" neighbouring them
    // If a breez has two neighbour holes and we remove one of them, the other hole will still be a neighbour
    // and the breeze will stay
    public static void removeNeighbours(int i, int j, int type){
        for (int[] direction : directions) {
            int x = i + direction[0];
            int y = j + direction[1];
            if(checkEdges(x, y)){
                if(Type.isType(type, Type.HOLE) && !checkNeighbours(x, y, Type.HOLE)){
                    board[x][y].removeType(Type.BREEZE);
                }else if(Type.isType(type, Type.WUMPUS) && !checkNeighbours(x, y, Type.WUMPUS)){
                    board[x][y].removeType(Type.STENCH);
                }
           }
        }
    }

    // Returns true if the tile has a neighbour of the given type
    public static boolean checkNeighbours(int i, int j, Type type){
        for (int[] direction : directions) {
            int x = i + direction[0];
            int y = j + direction[1];
            if(checkEdges(x, y)){
                if(Type.isType(board[x][y].getType(), type)) return true;
           }
        }
        return false;
    }

    // Returns true if the given coordinates are inside the board
    public static boolean checkEdges(int i, int j){
        return i >= 0 && i < SIZE && j >= 0 && j < SIZE;
    }

    public void init(){
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = new Tile(Type.EMPTY);
            }
        }
        board[0][0].visit();    }

    public static Tile[][] getBoard(){
        return board;
    }
}
