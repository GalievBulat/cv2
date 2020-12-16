package server.model;
public class Unit {
    private final UnitType type;
    private byte health;
    public byte x;
    public byte y;
    private final boolean cl1;
    private Unit(UnitType type,boolean cl1){
        this.type = type;
        this.health = type.getHealth();
        this.cl1 = cl1;
    }
    public boolean is1st() {
        return cl1;
    }
    public UnitType getType() {
        return type;
    }
    public byte getHealth() {
        return health;
    }
    public byte getDamage() {
        return type.getDamage();
    }
    public void getAttacked(byte damage){
        health-=damage;
    }
    public String getName() {
        return type.getName();
    }
    public static class Builder {
        private UnitType type;
        private boolean cl1;

        public Builder type(UnitType unitType) {
            this.type = unitType;
            return this;
        }
        public Builder client1(boolean cl1) {
            this.cl1 = cl1;
            return this;
        }

        public Unit create() {
            return new Unit(type,cl1);
        }
    }
}
