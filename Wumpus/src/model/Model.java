package model;

import model.Tile.Type;
import model.Tile.Knowledge;

import java.util.*;

public class Model extends AbstractModel implements Runnable {

    public static final int[][] directions = {{0, 1}, {1, 0}, {-1, 0}, {0, -1}};
    public static int SIZE, NUM_HOLES, NUM_WUMPUS, NUM_GOLD;

    public Tile[][] board;
    private ArrayList<Pos> movesBack;
    private int xPos, yPos;
    private int speed, backCtr;
    private boolean running, foundGold, finished;

    public Model() {
        init();
    }

    public void init() {
        board = new Tile[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = new Tile(Type.EMPTY);
            }
        }
        xPos = yPos = 0;
        board[xPos][yPos].visit();
        board[xPos][yPos].setTimes(0); //Reset times
        backCtr = 0;
        finished = false;
        sendMovement();
    }

    public void setSpeed(int speed) {
        this.speed = speed;
        this.running = true;
    }

    public boolean getFinished() {
        return finished;
    }

    @Override
    public void run() {
        while (true) {
            if (!running) {
                sleep(100);
            } else {
                if (Type.isType(board[xPos][yPos].getType(), Type.GOLD)) {
                    board[xPos][yPos].removeType(Type.GOLD);
                    NUM_GOLD--;
                    if (NUM_GOLD == 0) {
                        foundGold = true;
                    }
                }
                if (!foundGold) {
                    holisticThink(); // Do we have more information (globally)?
                    infer();
                    killOrCover();
                    move();
                } else if (!goBack()) return;
                if (speed == 0) running = false;
            }
        }
    }


    private void infer() {
//        if (board[xPos][yPos].getTimes() > 1) return;
        int type = board[xPos][yPos].getType();
        System.out.println("Ty" + Type.asList(type));
        for (int[] direction : directions) {
            int x = xPos + direction[0], y = yPos + direction[1];
            if (checkEdges(x, y)) {
                int kneighbourKnowledge = board[x][y].getKnowledge();
                if (Type.isType(type, Type.BREEZE)
                        && !Knowledge.isType(kneighbourKnowledge, Knowledge.NOT_HOLE)
                        && !board[x][y].isSafe()) {
                    board[x][y].addKnowledge(Knowledge.POSSIBLE_HOLE);

                } else if (Type.isType(type, Type.STENCH)
                        && !Knowledge.isType(kneighbourKnowledge, Knowledge.NOT_WUMPUS)
                        && !board[x][y].isSafe()) {
                    board[x][y].addKnowledge(Knowledge.POSSIBLE_WUMPUS);

                } else if (Type.isType(type, Type.EMPTY, Type.AGENT)) {
                    board[x][y].setSafe(true);
                    board[x][y].removeKnowledge(Knowledge.POSSIBLE_HOLE, Knowledge.POSSIBLE_WUMPUS);
                    board[x][y].addKnowledge(Knowledge.NOT_HOLE, Knowledge.NOT_WUMPUS);
                }

                if (!Type.isType(type, Type.STENCH)) {
                    board[xPos][yPos].removeKnowledge(Knowledge.STENCH);
                    board[x][y].addKnowledge(Knowledge.NOT_WUMPUS);
                    board[x][y].removeKnowledge(Knowledge.POSSIBLE_WUMPUS, Knowledge.WUMPUS);
                }
                if (!Type.isType(type, Type.BREEZE)) {
                    board[xPos][yPos].removeKnowledge(Knowledge.BREEZE);
                    board[x][y].addKnowledge(Knowledge.NOT_HOLE);
                    board[x][y].removeKnowledge(Knowledge.POSSIBLE_HOLE, Knowledge.HOLE);

                }
                System.out.println("Kn" + Knowledge.asList(board[x][y].getKnowledge()));
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
            outerloop:
            for (int j = 0; j < SIZE; j++) {
                int knowledge = board[i][j].getKnowledge();
                if (Knowledge.isOneOf(knowledge, Knowledge.BREEZE, Knowledge.STENCH)) {
                    int unknown = 0;
                    int possibleHole = 0;
                    int possibleWumpus = 0;
//                    int covered = 0;
//                    int dead = 0;
                    // TODO: REFACTOR THIS
                    for (int[] direction : directions) {
                        int x = i + direction[0];
                        int y = j + direction[1];
                        if (checkEdges(x, y)) {
                            int neighbourKnowledge = board[x][y].getKnowledge();
                            if (Knowledge.isType(neighbourKnowledge, Knowledge.UNKNOWN)) {
                                unknown++;
                            } else if (Knowledge.isType(neighbourKnowledge, Knowledge.POSSIBLE_HOLE)) {
                                possibleHole++;
                            } else if (Knowledge.isType(neighbourKnowledge, Knowledge.POSSIBLE_WUMPUS)) {
                                possibleWumpus++;
                            }
//                            else if (Knowledge.isType(neighbourKnowledge, Knowledge.COVERED_HOLE)) {
//                                covered++;
//                            } else if (Knowledge.isType(neighbourKnowledge, Knowledge.DEAD_WUMPUS)) {
//                                dead++;
//                            }
                        } else continue outerloop;
                    }

                    if (Knowledge.isType(knowledge, Knowledge.BREEZE)) {
                        if (possibleHole == 1 && unknown == 0 || possibleHole == 0 && unknown == 1) {
                            for (int[] direction : directions) {
                                int x = i + direction[0];
                                int y = j + direction[1];
                                if (checkEdges(x, y)) {
                                    int neighbourKnowledge = board[x][y].getKnowledge();
                                    if (Knowledge.isOneOf(neighbourKnowledge, Knowledge.UNKNOWN, Knowledge.POSSIBLE_HOLE)) {
                                        board[x][y].addKnowledge(Knowledge.HOLE);
                                        System.out.println("Hole at " + x + " " + y + " it has " + Knowledge.asList(board[x][y].getKnowledge()));
                                    }
                                }
                            }
                        }
                    }

                    if (Knowledge.isType(knowledge, Knowledge.STENCH)) {
                        if (possibleWumpus == 0 && unknown == 1 || possibleWumpus == 1 && unknown == 0) {
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
                }
            }
        }
    }

    private void move() {
        board[xPos][yPos].removeType(Type.AGENT);
        int min = Integer.MAX_VALUE;
        int minI = xPos, minJ = yPos;
        for (int[] direction : directions) {
            int x = xPos + direction[0];
            int y = yPos + direction[1];
            if (checkEdges(x, y)) {
                if (!board[x][y].isSafe()) continue;
                if (board[x][y].getTimes() < min) {
                    min = board[x][y].getTimes();
                    minI = x;
                    minJ = y;
                }
            }
        }
        xPos = minI;
        yPos = minJ;
        board[xPos][yPos].visit();
        sendMovement();
    }

    private boolean goBack() {
        if (movesBack == null) {
            movesBack = new ArrayList<>();
            Map<Pos, Pos> parentNodes = new HashMap<>();
            boolean[][] visitats = new boolean[SIZE][SIZE];
            ArrayDeque<Pos> oberts = new ArrayDeque();
            oberts.add(new Pos(xPos, yPos));
            while (!oberts.isEmpty()) {
                Pos pos = oberts.remove();
                if (pos.x == 0 && pos.y == 0) {
                    break;
                } else {
                    visitats[pos.x][pos.y] = true;
                    for (int[] direction : directions) {
                        Pos newPos = new Pos(pos.x + direction[0], pos.y + direction[1]);
                        if (checkEdges(newPos.x, newPos.y) && !visitats[newPos.x][newPos.y] && board[newPos.x][newPos.y].isSafe()) {
                            oberts.add(newPos);
                            visitats[newPos.x][newPos.y] = true;
                            parentNodes.put(newPos, pos);
                        }
                    }
                }
            }
            Pos node = new Pos(0, 0);
            while (node != null) {
                movesBack.add(node);
                node = parentNodes.get(node);
            }
            Collections.reverse(movesBack);
            return true;
        } else if (backCtr != movesBack.size()) {
            board[xPos][yPos].removeType(Type.AGENT);
            Pos newPos = movesBack.get(backCtr++);
            xPos = newPos.x;
            yPos = newPos.y;
            board[xPos][yPos].visit();
            sendMovement();
            return true;
        }
        return false;
    }

    private void killOrCover() {
        // COVERING
        for (int[] direction : directions) {
            int x = xPos + direction[0];
            int y = yPos + direction[1];
            if (checkEdges(x, y)) {
                int knowledge = board[x][y].getKnowledge();
                if (Knowledge.isType(knowledge, Knowledge.HOLE)) {
                    board[x][y].addKnowledge(Knowledge.NOT_HOLE);
                    cover(x, y);
                }
            }
        }

        // KILLING
        for (int i = 0; i < SIZE; i++) {
            if (Knowledge.isType(board[i][yPos].getKnowledge(), Knowledge.WUMPUS)) {
                kill(i, yPos);
            }
            if (Knowledge.isType(board[xPos][i].getKnowledge(), Knowledge.WUMPUS)) {
                kill(xPos, i);
            }
        }
    }

    private void kill(int x, int y) {
        board[x][y].removeType(Type.WUMPUS);
        board[x][y].removeKnowledge(Knowledge.WUMPUS);
        board[x][y].setSafe(true);
        board[x][y].addType(Type.DEAD_WUMPUS);
        deletePerception(false, x, y);
        sendMovement();
    }

    private void cover(int x, int y) {
        board[x][y].removeType(Type.HOLE);
        board[x][y].removeKnowledge(Knowledge.HOLE);
        board[x][y].setSafe(true);
        board[x][y].addType(Type.COVERED_HOLE);
        deletePerception(true, x, y);
        sendMovement();
    }

    private void deletePerception(Boolean breeze, int x, int y) {
        outerloop:
        for (int[] direction : directions) {
            int x1 = x + direction[0];
            int y1 = y + direction[1];
            if (checkEdges(x1, y1)) {
                for (int[] direction2 : directions) {
                    int x2 = x1 + direction2[0];
                    int y2 = y1 + direction2[1];
                    if (checkEdges(x2, y2) && !(x2 == x && y2 == y)) {
                        int type = board[x2][y2].getType();
                        if (breeze) {
                            if (Type.isType(type, Type.HOLE)) {
                                continue outerloop;
                            }
                        } else {
                            if (Type.isType(type, Type.WUMPUS)) {
                                continue outerloop;
                            }
                        }
                    }
                }
                if (breeze) {
                    board[x1][y1].removeType(Type.BREEZE);
                } else {
                    board[x1][y1].removeType(Type.STENCH);
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

    private void sendMovement() {
        firePropertyChange("movement", null, null);
        int millis = 0;
        switch (speed) {
            case 0 -> millis = 0;
            case 1 -> millis = 2750;
            case 2 -> millis = 2250;
            case 3 -> millis = 1750;
            case 4 -> millis = 1250;
            case 5 -> millis = 750;
        }
        sleep(millis);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetGame() {
        board[xPos][yPos].removeType(Type.AGENT);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                ArrayList<Type> types = Type.asList(board[i][j].getType());
                if (types.contains(Type.COVERED_HOLE)) {
                    types.remove(Type.COVERED_HOLE);
                    types.add(Type.HOLE);
                }
                if (types.contains(Type.DEAD_WUMPUS)) {
                    types.remove(Type.DEAD_WUMPUS);
                    types.add(Type.WUMPUS);
                }
                board[i][j] = new Tile(types);
            }
        }
        xPos = yPos = 0;
        board[xPos][yPos].addType(Type.AGENT);
        sendMovement();
    }


    private static class Pos {
        public int x, y;

        public Pos(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pos pos = (Pos) o;
            return x == pos.x && y == pos.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }


        @Override
        public String toString() {
            return "Pos{" + "x=" + x + ", y=" + y + '}';
        }
    }
}