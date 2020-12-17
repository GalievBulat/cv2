package view.model;

public class UnitType {
    private final int id;
    private final String name;
    private final String imagePath;

    private UnitType(int id, String name,  String imagePath){
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
    }
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public static class Builder {
        private int id;
        private String name;
        private String imagePath;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }
        public Builder image( String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public UnitType create() {
            return new UnitType(id, name,imagePath);
        }
    }
}
