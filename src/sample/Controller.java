package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public GridPane grid;
    @FXML
    AnchorPane pane;
    @FXML
    Text gameStateText;

    @FXML
    Text bomb_text;
    @FXML
    Text flags_text;

    public MinesweeperBS game;
    GameTile[][] nodes;
    int numRows;
    int numCols;
    ArrayList<Text> numbers;
    int numFlags;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       setupGame();
    }

    public void setupGame(){
        numFlags=0;
        this.numRows = grid.getRowConstraints().size();
        this.numCols = grid.getColumnConstraints().size();
        numbers = new ArrayList<>();
        nodes = new GameTile[numRows][numCols];
        game = new MinesweeperBS(grid.getRowConstraints().size(), grid.getColumnConstraints().size(), 15);
        setUpGrid();
        bomb_text.setText(""+game.numBombs);
        game.printCurrentGame();
    }

    public void setUpGrid(){
        double cellWidth = grid.getPrefWidth()/numRows;
        double cellHeight = grid.getPrefHeight()/numCols;
        for(int i = 0; i < numRows; i++){
            for(int j = 0; j < numCols; j++){
                GameTile node;
                if(game.bottomBoard[i][j]==TileValue.BOMB){
                    node = new GameTile(true, -2, cellWidth, cellHeight);
                }else if(game.bottomBoard[i][j]==TileValue.NUMBER){
                    game.totalNumbers++;
                    node = new GameTile(false, game.findSurroundingBombs(i,j), cellWidth, cellHeight);
                }else{
                    node = new GameTile(false, -1, cellWidth, cellHeight);
                }
                nodes[i][j]=node;
                node.setFill(Color.WHITE);
                node.setStyle("-fx-stroke: black; -fx-stroke-width: 1;");
                grid.add(node, i, j, 1, 1);
            }
        }
    }

    public void clickGrid(javafx.scene.input.MouseEvent event) {
        if(game.state==GameState.WIN || game.state==GameState.LOSE){
            return;
        }
        Node clickedNode = event.getPickResult().getIntersectedNode();
        Integer colIndex = GridPane.getColumnIndex(clickedNode);
        Integer rowIndex = GridPane.getRowIndex(clickedNode);
        if(nodes[colIndex][rowIndex].shown){
            return;
        }
        //flag
        if(event.getButton()== MouseButton.SECONDARY){
            if(nodes[colIndex][rowIndex].flagged){
                numFlags--;
                nodes[colIndex][rowIndex].flagged=false;
                nodes[colIndex][rowIndex].setFill(Color.WHITE);
            }else{
                numFlags++;
                nodes[colIndex][rowIndex].flagged=true;
                nodes[colIndex][rowIndex].setFill(new ImagePattern(new Image("file:///C:/Users/storoeb1/Desktop/MinesweeperBS/src/img/flag.png")));
            }
            flags_text.setText(""+numFlags);
        //clear
        }else if(event.getButton()==MouseButton.PRIMARY && !nodes[colIndex][rowIndex].flagged){
            if (clickedNode != grid) {
                drawTile(rowIndex,colIndex);
            }
            if(game.checkForWin()){
                gameStateText.setText("You win!");
            }
        }
    }

    public void drawTile(int x, int y){
        double xPos = nodes[x][y].getParent().getLayoutX() + (nodes[x][y].getWidth()*y) + (nodes[x][y].getWidth()/2);
        double yPos = nodes[x][y].getParent().getLayoutY() + (nodes[x][y].getHeight()*x) + (nodes[x][y].getHeight()/2);
        if(game.bottomBoard[x][y]==TileValue.BOMB){
            //draw bomb into rectangle
            game.state=GameState.LOSE;
            gameStateText.setText("You Lose!");
            nodes[y][x].setFill(new ImagePattern(new Image("file:///C:/Users/storoeb1/Desktop/MinesweeperBS/src/img/bomb.png")));
        }else if(game.bottomBoard[x][y]==TileValue.NUMBER){
            nodes[y][x].setFill(Color.LIGHTGRAY);
            nodes[y][x].shown=true;
            game.totalNumbersFound++;
            int numBombs = game.findSurroundingBombs(x,y);
            Text tileVal = new Text(""+numBombs);
            tileVal.setFill(chooseColorForNumber(numBombs));
            tileVal.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/mine-sweeper.ttf"), 20));
            tileVal.setX(xPos-8);
            tileVal.setY(yPos+10);
            numbers.add(tileVal);
            pane.getChildren().add(tileVal);
        }else{
            nodes[y][x].setFill(Color.LIGHTGRAY);
            nodes[y][x].shown=true;

            //clear all other blank tiles recursively
            drawTileRecursively(x+1,y);
            drawTileRecursively(x-1,y);
            drawTileRecursively(x,y-1);
            drawTileRecursively(x,y+1);
            drawTileRecursively(x+1,y+1);
            drawTileRecursively(x-1,y-1);
            drawTileRecursively(x-1,y+1);
            drawTileRecursively(x+1,y-1);
        }
    }

    public void drawTileRecursively(int x, int y){
        if(x<0 || x>numCols-1 || y<0 || y>numRows-1){
            //out of bounds
            return;
        }
        if(nodes[y][x].shown){
            return;
        }
        if(game.bottomBoard[x][y]==TileValue.CLEAR || game.bottomBoard[x][y]==TileValue.NUMBER){
            //continue recursion.
            //in this case, if the tile is clear, it will continue the recursion by itself
            //if the tile is a number, it will end the recursion.
            drawTile(x,y);
        }
    }

    public Color chooseColorForNumber(int number){
        switch (number){
            case 1: return Color.BLUE;
            case 2: return Color.GREEN;
            case 3: return Color.RED;
            case 4: return Color.PURPLE;
            case 5: return Color.MAROON;
            case 6: return Color.TURQUOISE;
            case 7: return Color.BLACK;
            case 8: return Color.GRAY;
        }
        return Color.WHITE;
    }

    public void removeNumbers(){
        for(Text t: this.numbers){
            pane.getChildren().remove(t);
        }
    }

    public void resetGame(){
        fixScoreboard();
        editGameStateText(" ");
        removeNumbers();
        setupGame();
    }

    public void fixScoreboard(){
        this.numFlags=0;
        flags_text.setText(""+this.numFlags);
    }

    public void editGameStateText(String text){
        pane.getChildren().remove(gameStateText);
        gameStateText.setText(text);
        pane.getChildren().add(gameStateText);
    }


}
