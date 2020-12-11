package view.models;

public class Unit {
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    private final int id;
    private final String name;
    Unit(int id, String name){
        this.id = id;
        this.name = name;
    }
     public static class Builder {
        private int id;
        private String name;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Unit create() {
            return new Unit(id, name);
        }
    }
}
