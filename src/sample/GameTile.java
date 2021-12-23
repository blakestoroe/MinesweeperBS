package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameTile extends Rectangle {

    public boolean shown;
    public boolean isBomb;
    public int numVal;
    public boolean flagged;

    public GameTile(boolean isBomb, int numVal, double width, double height){
        this.setWidth(width);
        this.setHeight(height);
        this.setFill(Color.WHITE);
        this.flagged=false;
        this.shown=false;
        if(isBomb){
            this.isBomb=true;
            this.numVal=-2;
        }else{
            this.isBomb=false;
            this.numVal=numVal;
        }
    }

}
