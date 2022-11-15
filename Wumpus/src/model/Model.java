package model;

import model.Tile.Type;
import model.Tile.Knowledge;

public class Model extends AbstractModel {

    public static final int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
    public static final int[][] diagonalDirections = {{-1, -1}, {-1, 1}, {1, 1}, {1, -1}};
    public static int SIZE, NUM_HOLES, NUM_WUMPUS, NUM_GOLD;


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
        board[xPos][yPos].setTimes(0); //Reset times
    }

    public void start() {
        for (int iter = 0; iter < 100; iter++) {
            board[xPos][yPos].visit();
            firePropertyChange("movement", null, null);
            if (board[xPos][yPos].getTimes() == 1) {
                infer();
            }
            think(); // Do we have more information (locally)?
            holisticThink(); // Do we have more information (globally)?
            //   killOrCover();
            if (Type.isType(board[xPos][yPos].getType(), Type.GOLD)) {
                board[xPos][yPos].removeType(Type.GOLD);
                NUM_GOLD--;
                if (NUM_GOLD == 0) {
                    System.out.println("Ganaste");
                    firePropertyChange("movement", null, null);
                    printVisited();
                    goBack();
                    return;
                }
            } else if (Type.isType(board[xPos][yPos].getType(), Type.HOLE) || Type.isType(board[xPos][yPos].getType(), Type.WUMPUS)) {
                System.out.println("Perdiste");
                printVisited();
                return;
            }
            board[xPos][yPos].removeType(Type.AGENT);
            move();
        }
        System.out.println("Han pasado 100 iteraciones, hay un bucle?");
        printVisited();
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
                boolean diagIsBreeze, diagIsStench;

                if (Knowledge.isType(diagKnowledge, Knowledge.UNKNOWN)) {
                    continue;
                } else {
                    diagIsBreeze = Knowledge.isType(diagKnowledge, Knowledge.BREEZE);
                    diagIsStench = Knowledge.isType(diagKnowledge, Knowledge.STENCH);
                }

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

    private void printVisited() {
        //Print knowledge board
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(board[i][j].getTimes() + " ");
            }
            System.out.println();
        }
        // TODO: IMPLEMENT THIS
    }

    private void goBack() {
        // Implement route to get back to 0,0, we can only use the tiles with visited > 0
        // Manhatan distance heuristic
        while (xPos != 0 || yPos != 0) {
            board[xPos][yPos].removeType(Type.AGENT);
            int min = Integer.MAX_VALUE;
            int minI = 0, minJ = 0;
            for (int[] direction : directions) {
                int x1 = xPos + direction[0];
                int y1 = yPos + direction[1];
                if (checkEdges(x1, y1)) {
                    if (board[x1][y1].getTimes() > 0) {
                        int dist = manhattanDistance(x1, y1, 0, 0);
                        if (dist < min) {
                            min = dist;
                            minI = x1;
                            minJ = y1;
                        }
                    }
                }
            }
            xPos = minI;
            yPos = minJ;
            board[xPos][yPos].addType(Type.AGENT);
            firePropertyChange("movement", null, null);
            System.out.println("Going back to " + xPos + " " + yPos);
        }
    }

    public int manhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
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

    public Tile[][] getBoard() {
        return board;
    }

    // Change the tile at the given coordinates
    // Assumptions: we can't have "wumpus", "hole" and "gold" on the same tile
    public void changeTile(int i, int j, String name) {
        int oldType = board[i][j].getType();
        Type newType = Type.valueOf(name);

        // "doubleclick" on the tile results on elimination of the type
        if (Type.isType(oldType, newType)) {

            if (Type.isType(oldType, Type.WUMPUS)) NUM_WUMPUS--;
            if (Type.isType(oldType, Type.HOLE)) NUM_HOLES--;
            if (Type.isType(oldType, Type.GOLD)) NUM_GOLD--;

            board[i][j].removeType(newType);
            removeNeighbours(i, j, newType.bit);
            return;
        }

        // Can't add gold on top of a wumpus or a hole
        if ((Type.isType(oldType, Type.HOLE) || Type.isType(oldType, Type.WUMPUS)) && Type.isType(newType.bit, Type.GOLD)) {
            return;
        }

        // Adding wumpus or hole on top of another type removes the underlying type
        if (Type.isType(oldType, Type.HOLE)) {
            NUM_HOLES--;
            board[i][j].removeType(Type.HOLE);
            removeNeighbours(i, j, oldType);
        } else if (Type.isType(oldType, Type.WUMPUS)) {
            NUM_WUMPUS--;
            board[i][j].removeType(Type.WUMPUS);
            removeNeighbours(i, j, oldType);
        } else if (Type.isType(oldType, Type.GOLD)) {
            NUM_GOLD--;
            board[i][j].removeType(Type.GOLD);
        }

        if (Type.isType(newType.bit, Type.WUMPUS)) NUM_WUMPUS++;
        if (Type.isType(newType.bit, Type.HOLE)) NUM_HOLES++;
        if (Type.isType(newType.bit, Type.GOLD)) NUM_GOLD++;
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