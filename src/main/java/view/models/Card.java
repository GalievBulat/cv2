package view.models;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import view.interfaces.OnClickListener;


public class Card {
    private final String imageUrl;
    private final UnitType unitType;
    private VBox vBox;
    public Card(String imageUrl, int unitId){
        this.imageUrl = imageUrl;
        this.unitType = new UnitType.Builder().id(unitId).name(Units.values()[unitId].name()).create();;
        vBox = new VBox();
        vBox.setMaxWidth(60);
        vBox.setStyle("-fx-background-color: #FFFFFF;");
        ImageView image = new ImageView(imageUrl);
        image.setFitHeight(100);
        image.setFitWidth(100);
        image.setPreserveRatio(true);
        vBox.getChildren().add(image);
        Label text = new Label(unitType.getName());
        text.setFont(Font.font("Roboto Light",12));
        vBox.getChildren().add(text);
    }

    public VBox getView() {
        return vBox;
    }

    public void setOnClickListener(OnClickListener listener){
        vBox.setOnMouseClicked(event->{
            listener.onClick(this);
        });
    }

    public UnitType getUnit() {
        return unitType;
    }
}
