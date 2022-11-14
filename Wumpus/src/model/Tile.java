package model;

import java.util.ArrayList;

public class Tile {

    private int type, knowledge, times;
    private boolean isOccupied, visited;

    public Tile(Type type) {
        this.type = type.bit;
        this.knowledge = Knowledge.UNKNOWN.bit;
        this.isOccupied = false;
        this.visited = false;
        this.times = 0;
    }

    public void visit(){
        addType(Type.AGENT);
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

    // Remove a type from the tile
    public void removeType(Type type) {
        this.type &= ~type.bit;
    }

    public int getKnowledge() {
        return knowledge;
    }

    public void addKnowledge(Knowledge knowledge) {
        this.knowledge &= ~Knowledge.EMPTY.bit;
        this.knowledge |= knowledge.bit;
    }

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
        ArrayList<Type> types = Type.asList(this.type);
        for(Type type: types) {
            Knowledge type_knowledge = Knowledge.fromType(type);
            switch(type) {
                case EMPTY, HOLE, WUMPUS, GOLD, BREEZE, STENCH -> this.knowledge |= type_knowledge.bit;
                case AGENT, COVERED_HOLE, DEAD_WUMPUS-> {}
            }
        }
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

        public static boolean isType(int tile, Type... types) {
            int sum = 0;
            for (Type type: types){
                sum |= type.bit;
            }
            return (tile & sum) != 0;
        }


        // Returns true if the tile has only the types given
        public static boolean isOnlyTypes(int tile, Type... types) {
             int sum = 0;
             for (Type type : types) {
                 sum |= type.bit;
             }
             return (tile & ~sum) == 0;
         }

        public static ArrayList<Type> asList(int type) {
            ArrayList<Type> types = new ArrayList<>();
            for(Type val: Type.values()){
                if(isType(type, val)) types.add(val);
            }
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
        public int bit;

        Knowledge() {
            this.bit = (1 << ordinal());
        }

        public static boolean isType(int tile, Knowledge knowledge) {
            return (tile & knowledge.bit) != 0;
        }

        public static ArrayList<Knowledge> asList(int type) {
            ArrayList<Knowledge> knowledges = new ArrayList<>();
            for(Knowledge val: Knowledge.values()){
                if(isType(type, val)) knowledges.add(val);
            }
            return knowledges;
        }

        public static Knowledge fromType(Type type) {
            Knowledge knowledge = Knowledge.EMPTY;
            switch(type) {
                case EMPTY -> knowledge = Knowledge.EMPTY;
                case HOLE -> knowledge = Knowledge.HOLE;
                case WUMPUS -> knowledge = Knowledge.WUMPUS;
                case BREEZE -> knowledge = Knowledge.BREEZE;
                case STENCH -> knowledge = Knowledge.STENCH;
                case AGENT, COVERED_HOLE, DEAD_WUMPUS, GOLD -> {}
            }
            return knowledge;
        }
    }
}
