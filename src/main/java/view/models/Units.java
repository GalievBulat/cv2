package view.models;

public enum Units {
    SOLDIER(0),ARTILLERY(1), CAVALRY(2);
    private final int id;
    Units(int id) {
        this.id = id;
    }
}
