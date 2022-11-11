package model;

import java.util.ArrayList;

public class Tile {

    private int type, knowledge, times;
    //private Knowledge knowledge;
    private boolean isOccupied, visited;

    public Tile(Type type) {
        this.type = type.bit;
        this.knowledge = 0;
        this.isOccupied = false;
        this.visited = false;
        this.times = 0;
    }

    public void visit(){
        this.type |= Type.AGENT.bit; // Cambiar imagen
        this.isOccupied = true;
        this.visited = true;
        this.times++;
        calculateKnowledge();
    }

    public int getType() {
        return type;
    }

    public void addType(Type type) {
        this.type &= ~Type.EMPTY.bit;
        this.type |= type.bit;
    }

    /*
    public Knowledge getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(Knowledge knowledge) {
        this.knowledge = knowledge;
    } */

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    private void calculateKnowledge() {
        this.knowledge = 0;
        ArrayList<Type> types = Type.getTypes(this.type);
        for(Type type: types) {
            switch(type) {
                case BREEZE, STENCH -> this.knowledge |= type.bit;
            }
        }
        Type.getTypes(this.knowledge);
    }

    public enum Type {
        EMPTY,
        AGENT,
        HOLE,
        COVERED_HOLE,
        BREEZE,
        WUMPUS,
        DEAD_WUMPUS,
        STENCH,
        GOLD;

        public int bit;

        Type() {
            this.bit = (1 << ordinal());
        }

        public static boolean isType(int tile, Type type) {
            return (tile & type.bit) != 0;
        }

        public static ArrayList<Type> getTypes(int type) {
            ArrayList<Type> types = new ArrayList<>();
            for(Type val: Type.values()){
                if(isType(type, val)) types.add(val);
            }
            System.out.println(types);
            return types;
        }

        public static String[] getPlacebleTypes() {
            return new String[]{HOLE.name().toLowerCase(), WUMPUS.name().toLowerCase(), GOLD.name().toLowerCase()};
        }
    }

    public enum Knowledge {
        EMPTY,
        WUMPUS,
        HOLE,
        BREEZE,
        STENCH,
        BREEZE_AND_STENCH,
        POSSIBLE_WUMPUS,
        POSSIBLE_HOLE,
        POSSIBLE_WUMPUS_AND_HOLE,
        UNKNOWN;
    }
}
