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

    public void start() {
        for (int iter = 0; iter < 100; iter++) {
            board[xPos][yPos].visit();
            if (board[xPos][yPos].getTimes() == 1) {
                infer();
            }
            think(); // Do we have more information (locally)?
            holisticThink(); // Do we have more information (globally)?
            //   killOrCover();
            move();
        }
    }

    private void infer() {
        int type = board[xPos][yPos].getType();
        for (int[] direction : directions) {
            int x = xPos + direction[0];
            int y = yPos + direction[1];
            if (checkEdges(x, y)) {
                int kneighbourKnowledge = board[x][y].getKnowledge();
                if (Type.isType(type, Type.BREEZE) && (Knowledge.isType(kneighbourKnowledge, Knowledge.UNKNOWN)
                        || Knowledge.isType(kneighbourKnowledge, Knowledge.POSSIBLE_WUMPUS) || Knowledge.isType(kneighbourKnowledge, Knowledge.WUMPUS))) {
                    board[x][y].addKnowledge(Knowledge.POSSIBLE_HOLE);
                } else if (Type.isType(type, Type.STENCH) && (Knowledge.isType(kneighbourKnowledge, Knowledge.UNKNOWN)
                        || Knowledge.isType(kneighbourKnowledge, Knowledge.POSSIBLE_HOLE) || Knowledge.isType(kneighbourKnowledge, Knowledge.HOLE))) {
                    board[x][y].addKnowledge(Knowledge.POSSIBLE_WUMPUS);
                }
            }
        }
    }

    public void think() {
        int knowledge = board[xPos][yPos].getKnowledge();
        boolean posIsBreeze = Knowledge.isType(knowledge, Knowledge.BREEZE);
        boolean posIsStench = Knowledge.isType(knowledge, Knowledge.STENCH);
        for (int[] direction : diagonalDirections) {
            int x = xPos + direction[0];
            int y = yPos + direction[1];
            if (checkEdges(x, y)) {
                int diagKnowledge = board[x][y].getKnowledge();
                boolean diagIsBreeze = Knowledge.isType(diagKnowledge, Knowledge.BREEZE);
                boolean diagIsStench = Knowledge.isType(diagKnowledge, Knowledge.STENCH);

                boolean verIsPossibleHole = Knowledge.isType(board[x][yPos].getKnowledge(), Knowledge.POSSIBLE_HOLE);
                boolean horIsPossibleHole = Knowledge.isType(board[xPos][y].getKnowledge(), Knowledge.POSSIBLE_HOLE);

                boolean verIsPossibleWumpus = Knowledge.isType(board[x][yPos].getKnowledge(), Knowledge.POSSIBLE_WUMPUS);
                boolean horIsPossibleWumpus = Knowledge.isType(board[xPos][y].getKnowledge(), Knowledge.POSSIBLE_WUMPUS);

                if (!(posIsBreeze && diagIsBreeze)) {
                    if (verIsPossibleHole) {
                        board[x][yPos].removeKnowledge(Knowledge.POSSIBLE_HOLE);
                    }
                    if (horIsPossibleHole) {
                        board[xPos][y].removeKnowledge(Knowledge.POSSIBLE_HOLE);
                    }
                }
                if (!(posIsStench && diagIsStench)) {
                    if (verIsPossibleWumpus) {
                        board[x][yPos].removeKnowledge(Knowledge.POSSIBLE_WUMPUS);
                    }
                    if (horIsPossibleWumpus) {
                        board[xPos][y].removeKnowledge(Knowledge.POSSIBLE_WUMPUS);
                    }
                }
            }
        }
    }

    /**
     * In holisticThink we'll try to infer from the current knowledge holistically.
     * <p>
     * If we have a possible_hole surrounded by 0 unknown tile and just 1 breeze we can infer that the possible_hole is a pit.
     * If we have a possible_wumpus surrounded by 0 unknown tile and just 1 stench we can infer that the possible_wumpus is a wumpus.
     * <p>
     * If we have a breeze surrounded by 1 unknown tile or 1 possible_hole we can infer that the other tile is a hole
     * If we have a stench surrounded by 1 unknown tile or 1 possible_wumpus we can infer that the other tile is a wumpus
     */
    private void holisticThink() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int knowledge = board[i][j].getKnowledge();
                if (Knowledge.isOneOf(knowledge, Knowledge.BREEZE, Knowledge.STENCH,
                        Knowledge.POSSIBLE_HOLE, Knowledge.POSSIBLE_WUMPUS)) {
                    int unknown = 0;
                    int possible_hole = 0;
                    int possible_wumpus = 0;
                    int breeze = 0;
                    int stench = 0;
                    // TODO: REFACTOR THIS
                    for (int[] direction : directions) {
                        int x = i + direction[0];
                        int y = j + direction[1];
                        if (checkEdges(x, y)) {
                            int neighbourKnowledge = board[x][y].getKnowledge();
                            if (Knowledge.isType(neighbourKnowledge, Knowledge.UNKNOWN)) {
                                unknown++;
                            } else if (Knowledge.isType(neighbourKnowledge, Knowledge.POSSIBLE_HOLE)) {
                                possible_hole++;
                            } else if (Knowledge.isType(neighbourKnowledge, Knowledge.POSSIBLE_WUMPUS)) {
                                possible_wumpus++;
                            } else if (Knowledge.isType(neighbourKnowledge, Knowledge.BREEZE)) {
                                breeze++;
                            } else if (Knowledge.isType(neighbourKnowledge, Knowledge.STENCH)) {
                                stench++;
                            }
                        }
                    }

                    if (Knowledge.isType(knowledge, Knowledge.BREEZE)) {
                        if (possible_hole == 1 || unknown == 1) {
                            for (int[] direction : directions) {
                                int x = i + direction[0];
                                int y = j + direction[1];
                                if (checkEdges(x, y)) {
                                    int neighbourKnowledge = board[x][y].getKnowledge();
                                    if (Knowledge.isOneOf(neighbourKnowledge, Knowledge.UNKNOWN, Knowledge.POSSIBLE_HOLE)) {
                                        board[x][y].addKnowledge(Knowledge.HOLE);
                                    }
                                }
                            }
                        }
                    }

                    if (Knowledge.isType(knowledge, Knowledge.STENCH)) {
                        if (possible_wumpus == 1 || unknown == 1) {
                            for (int[] direction : directions) {
                                int x = i + direction[0];
                                int y = j + direction[1];
                                if (checkEdges(x, y)) {
                                    int neighbourKnowledge = board[x][y].getKnowledge();
                                    if (Knowledge.isOneOf(neighbourKnowledge, Knowledge.UNKNOWN, Knowledge.POSSIBLE_WUMPUS)) {
                                        board[x][y].addKnowledge(Knowledge.WUMPUS);
                                    }
                                }
                            }
                        }
                    }

                    if (Knowledge.isType(knowledge, Knowledge.POSSIBLE_HOLE)) {
                        if (unknown == 0 && breeze == 1) {
                            board[i][j].addKnowledge(Knowledge.HOLE);
                        }
                    }

                    if (Knowledge.isType(knowledge, Knowledge.POSSIBLE_WUMPUS)) {
                        if (unknown == 0 && stench == 1) {
                            board[i][j].addKnowledge(Knowledge.WUMPUS);
                        }
                    }
                }
            }
        }
    }

    private void move() {
        int min = Integer.MAX_VALUE;
        int minI = 0, minJ = 0;
        for (int[] direction : directions) {
            int x = xPos + direction[0];
            int y = yPos + direction[1];
            if (checkEdges(x, y)) {
                int knowledge = board[x][y].getKnowledge();
                if (Knowledge.isOneOf(knowledge, Knowledge.WUMPUS, Knowledge.HOLE, Knowledge.POSSIBLE_WUMPUS, Knowledge.POSSIBLE_HOLE)) {
                    continue;
                }
                if (board[x][y].getTimes() < min) {
                    min = board[x][y].getTimes();
                    minI = x;
                    minJ = y;
                }
            }
        }
        xPos = minI;
        yPos = minJ;
    }

    private void killOrCover() {
        // if there is a hole in an adjacent tile call cover
        // if there is a wumpus in the same row or column call kill

        // COVERING
        for (int[] direction : directions) {
            int x = xPos + direction[0];
            int y = yPos + direction[1];
            if (checkEdges(x, y)) {
                int knowledge = board[x][y].getKnowledge();
                if (Knowledge.isType(knowledge, Knowledge.HOLE)) {
                    cover();
                }
            }
        }

        // KILLING
        for (int i = 0; i < SIZE; i++) {

            if (Knowledge.isType(board[i][yPos].getKnowledge(), Knowledge.WUMPUS)) {
                kill();
            }
            if (Knowledge.isType(board[xPos][i].getKnowledge(), Knowledge.WUMPUS)) {
                kill();
            }
        }


    }

    private void kill() {
        // TODO
    }

    private void cover() {
        // TODO
    }

    /*
    EMPTY,
    WUMPUS,
    HOLE,
    BREEZE,
    STENCH,
    POSSIBLE_WUMPUS,
    POSSIBLE_HOLE,
    UNKNOWN;

    A safe pair is a pair of diagonally opposite tiles that allows us to infer that the tiles in between are safe.
    P.ex Empty and breeze, it let us know that the tiles in between are safe.
    The safe pairs are:
        BREEZE && ~STENCH and ~BREEZE -> not(possible_hole/possible_wumpus)
        STENCH && ~BREEZE and ~STENCH -> not(possible_hole/possible_wumpus)
        BREEZE && STENCH and BREEZE && ~STENCH -> not(posible wumpus)
        BREEZE && STENCH and STENCH && ~BREEZE  -> not(posible hole)
     */


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

        if((Type.isType(oldType, Type.HOLE) || Type.isType(oldType, Type.WUMPUS)) && Type.isType(newType.bit, Type.GOLD)){
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