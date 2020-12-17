package view.model;

public enum Units {
    SOLDIER("src/main/resources/xd.jpg"),ARTILLERY("src/main/resources/xd.jpg"), CAVALRY("src/main/resources/xd.jpg");
    private final String image;
    Units( String image) {
        this.image = image;
    }
    public String getImage() {
        return image;
    }
}
