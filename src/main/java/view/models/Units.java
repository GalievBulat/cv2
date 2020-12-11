package view.models;

public enum Units {
    SOLDIER(1),ARTILLERY(2), CAVALRY(3);
    private final int id;
    Units(int id) {
        this.id = id;
    }
}
