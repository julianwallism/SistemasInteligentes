package model;

import java.util.ArrayList;

public class Tile {

    private int type, knowledge, times;

    public Tile(Type type) {
        this.type = type.bit;
        this.knowledge = Knowledge.UNKNOWN.bit;
        this.times = 0;
    }

    public void visit(){
        addType(Type.AGENT);
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

    public void removeKnowledge(Knowledge knowledge) {
        this.knowledge &= ~knowledge.bit;
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
        UNKNOWN,
        EMPTY,
        WUMPUS,
        HOLE,
        BREEZE,
        STENCH,
        POSSIBLE_WUMPUS,
        POSSIBLE_HOLE;
        public int bit;

        Knowledge() {
            this.bit = (1 << ordinal());
        }

        public static boolean isType(int tile, Knowledge knowledge) {
            return (tile & knowledge.bit) != 0;
        }

        public static boolean isOneOf(int tile, Knowledge... knowledges) {
            for (Knowledge knowledge : knowledges) {
                if(isType(tile, knowledge)) {
                    return true;
                }
            }
            return false;
        }
        public static ArrayList<Knowledge> asList(int type) {
            ArrayList<Knowledge> knowledges = new ArrayList<>();
            for(Knowledge val: Knowledge.values()){
                if(isType(type, val)) knowledges.add(val);
            }
            return knowledges;
        }

        public static Knowledge fromType(Type type) {
             return switch(type) {
                case HOLE       -> Knowledge.HOLE;
                case WUMPUS     -> Knowledge.WUMPUS;
                case BREEZE     -> Knowledge.BREEZE;
                case STENCH     -> Knowledge.STENCH;
                default         -> Knowledge.EMPTY;
            };
        }
    }
}
