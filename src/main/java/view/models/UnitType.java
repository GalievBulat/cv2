package view.models;

public class UnitType {
    private final int id;
    private final String name;
    private final byte damage;
    private final byte health;

    @Deprecated
    private UnitType(int id, String name,byte damage,byte health){
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.health = health;
    }
    public int getId() {
        return id;
    }

    public byte getDamage() {
        return damage;
    }

    public byte getHealth() {
        return health;
    }

    public String getName() {
        return name;
    }

    public static class Builder {
        private int id;
        private String name;
        private byte damage;
        private byte health;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }
        public Builder damage(byte damage) {
            this.damage = damage;
            return this;
        }
        public Builder health(byte health) {
            this.health = health;
            return this;
        }

        public UnitType create() {
            return new UnitType(id, name,damage,health);
        }
    }
}
