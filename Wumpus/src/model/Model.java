package model;

import model.Tile.Type;
import model.Tile.Knowledge;

public class Model {

    public static final int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
    public static final int[][] diagonalDirections = {{-1, -1}, {-1, 1}, {1, 1}, {1, -1}};

    public static int SIZE;
    public Tile[][] board;
    private int xPos, yPos;

    public Model() {
        board = new Tile[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = new Tile(Type.EMPTY);
            }
        }
        xPos = yPos = 0;
        board[xPos][yPos].visit();
    }

    public void start(){
        board[xPos][yPos].visit();
        infer(xPos, yPos);
        think(xPos, yPos);


    }

    public void think(int i, int j){
        int knowledge = board[i][j].getKnowledge();
        for(int[] direction: diagonalDirections){
            int x = i + direction[0];
            int y = j + direction[1];
            if(checkEdges(x, y)){
                int newKnowledge = board[x][y].getKnowledge();

                // safe
            }
        }
    }


    private void infer(int i, int j) {
        int type = board[i][j].getType();

        for (int[] direction : directions) {
            int x = i + direction[0];
            int y = j + direction[1];
            if (checkEdges(x, y)) {
                int knowledge = board[x][y].getKnowledge();
                if (Type.isType(type, Type.BREEZE) && (Knowledge.isType(knowledge, Knowledge.UNKNOWN) || Knowledge.isType(knowledge, Knowledge.POSSIBLE_WUMPUS))) {
                    board[x][y].addKnowledge(Knowledge.POSSIBLE_HOLE);
                } else if (Type.isType(type, Type.STENCH) && (Knowledge.isType(knowledge, Knowledge.UNKNOWN) || Knowledge.isType(knowledge, Knowledge.POSSIBLE_WUMPUS))) {
                    board[x][y].addKnowledge(Knowledge.POSSIBLE_WUMPUS);
                }
            }
        }
    }

    public Tile[][] getBoard() {
        return board;
    }

    // Change the tile at the given coordinates
    // Assumptions: we can't have "wumpus", "hole" and "gold" on the same tile
    public void changeTile(int i, int j, String name) {
        int oldType = board[i][j].getType();
        Type newType = Type.valueOf(name);

        /* If oldType has newType, we remove it and its neighbours and return.  */
        if (Type.isType(oldType, newType)) {
            board[i][j].removeType(newType);
            removeNeighbours(i, j, newType.bit);
            return;
        }

        if (Type.isType(oldType, Type.HOLE)) {
            board[i][j].removeType(Type.HOLE);
            removeNeighbours(i, j, oldType);
        } else if (Type.isType(oldType, Type.WUMPUS)) {
            board[i][j].removeType(Type.WUMPUS);
            removeNeighbours(i, j, oldType);
        } else if (Type.isType(oldType, Type.GOLD)) {
            board[i][j].removeType(Type.GOLD);
        }
        /* We add the new type and calculate its neighbours */
        board[i][j].addType(newType);
        calculateNeighbours(i, j);
    }

    // Given a tile, calculate the neighbours and add the "breeze" and "stench" types
    private void calculateNeighbours(int i, int j) {
        int type = board[i][j].getType();
        for (int[] direction : directions) {
            int x = i + direction[0];
            int y = j + direction[1];
            if (checkEdges(x, y)) {
                if (Type.isType(type, Type.HOLE)) {
                    board[x][y].addType(Type.BREEZE);
                } else if (Type.isType(type, Type.WUMPUS)) {
                    board[x][y].addType(Type.STENCH);
                }
            }
        }
    }

    // Given a tile, remove the neighbours, if it's the only "hole/wumpus" neighbouring them
    // If a breez has two neighbour holes and we remove one of them, the other hole will still be a neighbour
    // and the breeze will stay
    private void removeNeighbours(int i, int j, int type) {
        for (int[] direction : directions) {
            int x = i + direction[0];
            int y = j + direction[1];
            if (checkEdges(x, y)) {
                if (Type.isType(type, Type.HOLE) && !hasTypeNeighbour(x, y, Type.HOLE)) {
                    board[x][y].removeType(Type.BREEZE);
                } else if (Type.isType(type, Type.WUMPUS) && !hasTypeNeighbour(x, y, Type.WUMPUS)) {
                    board[x][y].removeType(Type.STENCH);
                }
            }
        }
    }

    // Returns true if the tile has a neighbour of the given type
    private boolean hasTypeNeighbour(int i, int j, Type type) {
        for (int[] direction : directions) {
            int x = i + direction[0];
            int y = j + direction[1];
            if (checkEdges(x, y)) {
                if (Type.isType(board[x][y].getType(), type)) return true;
            }
        }
        return false;
    }

    // Returns true if the given coordinates are inside the board
    private static boolean checkEdges(int i, int j) {
        return i >= 0 && i < SIZE && j >= 0 && j < SIZE;
    }
}
