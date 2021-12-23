package sample;

import java.util.concurrent.ThreadLocalRandom;

public class MinesweeperBS {

    int numRows;
    int numCols;
    int numBombs;
    TileValue[][] bottomBoard;
    GameState state;

    int totalNumbersFound;
    int totalNumbers;

    public MinesweeperBS(int numRows, int numCols, int numBombs){
        this.numRows=numRows;
        this.numCols=numCols;
        this.numBombs=numBombs;
        this.bottomBoard=new TileValue[this.numRows][this.numCols];
        this.generateBoard();
        this.state=GameState.PLAYING;
    }

    public void printCurrentGame(){
        for(int i = 0; i < numRows; i++){
            System.out.print("[");
            for(int j = 0; j < numCols; j++){
                if(bottomBoard[i][j]==TileValue.BOMB){
                    System.out.print("B");
                }else{
                    int numBombs = findSurroundingBombs(i,j);
                    if(numBombs>0){
                        System.out.print(numBombs);
                    }else{
                        System.out.print("_");
                    }
                }
                if(j!=numCols-1){
                    System.out.print(", ");
                }
            }
            System.out.println("]");
        }
    }

    public void generateBoard(){
        for(int i = 0; i < numBombs; i++){
            //todo: fix this algorithm. seems redundant
            int bombX = ThreadLocalRandom.current().nextInt(0,numRows);
            int bombY = ThreadLocalRandom.current().nextInt(0,numCols);
            while(bottomBoard[bombX][bombY]==TileValue.BOMB){
                bombX=ThreadLocalRandom.current().nextInt(0,numRows);
                bombY=ThreadLocalRandom.current().nextInt(0,numCols);
            }
            bottomBoard[bombX][bombY]=TileValue.BOMB;
        }
        //all bombs are put into place, fill in the numbers and blanks
        for(int i = 0; i < numRows; i++){
            for(int j = 0; j < numCols; j++){
                if(bottomBoard[i][j]!=TileValue.BOMB){
                    int numBombs = findSurroundingBombs(i,j);
                    if (numBombs > 0) {
                        bottomBoard[i][j]=TileValue.NUMBER;
                    }else{
                        bottomBoard[i][j]=TileValue.CLEAR;
                    }
                }
            }
        }

    }

    public boolean checkForWin(){
        if(this.totalNumbersFound==this.totalNumbers){
            this.state=GameState.WIN;
            return true;
        }
        return false;
    }

    //requirement: coordinates are for a tile that is a number tile
    //return: number of bombs surrounding the tile
    public int findSurroundingBombs(int x, int y){
        int numBombs = 0;

        //LEFT BOMBS
        if(x==0) {
            //no left bombs
        }else{
            if(y==0){
                //check left and bottom left
                if(bottomBoard[x-1][y+1]==TileValue.BOMB){
                    numBombs++;
                }
                if(bottomBoard[x-1][y]==TileValue.BOMB){
                    numBombs++;
                }
            }else if(y==numRows-1){
                //check left and top left
                if(bottomBoard[x-1][y]==TileValue.BOMB){
                    numBombs++;
                }
                if(bottomBoard[x-1][y-1]==TileValue.BOMB){
                    numBombs++;
                }
            }else{
                //check all left tiles
                if(bottomBoard[x-1][y+1]==TileValue.BOMB){
                    numBombs++;
                }
                if(bottomBoard[x-1][y]==TileValue.BOMB){
                    numBombs++;
                }
                if(bottomBoard[x-1][y-1]==TileValue.BOMB){
                    numBombs++;
                }
            }
        }

        //TOP/BOTTOM BOMBS
        if(y==0){
            //no top bombs
            if(bottomBoard[x][y+1]==TileValue.BOMB){
                numBombs++;
            }
        }else if(y==numRows-1){
            //no bottom bombs
            if(bottomBoard[x][y-1]==TileValue.BOMB){
                numBombs++;
            }
        }else{
            //both top and bottom bombs
            if(bottomBoard[x][y+1]==TileValue.BOMB){
                numBombs++;
            }
            if(bottomBoard[x][y-1]==TileValue.BOMB){
                numBombs++;
            }
        }

        //RIGHT BOMBS
        if(x==numCols-1){
            //no right bombs
        }else{
            if(y==0){
                //check right and bottom right
                if(bottomBoard[x+1][y]==TileValue.BOMB){
                    numBombs++;
                }
                if(bottomBoard[x+1][y+1]==TileValue.BOMB){
                    numBombs++;
                }
            }else if(y==numRows-1){
                //check right and top right
                if(bottomBoard[x+1][y]==TileValue.BOMB){
                    numBombs++;
                }
                if(bottomBoard[x+1][y-1]==TileValue.BOMB){
                    numBombs++;
                }
            }else{
                //check all right tiles
                if(bottomBoard[x+1][y]==TileValue.BOMB){
                    numBombs++;
                }
                if(bottomBoard[x+1][y-1]==TileValue.BOMB){
                    numBombs++;
                }
                if(bottomBoard[x+1][y+1]==TileValue.BOMB){
                    numBombs++;
                }
            }
        }
        return numBombs;
    }



}


//enum class that will be used to store what is on the
//tile under the cover.
enum TileValue{
    BOMB, NUMBER, CLEAR
}

//keep track of the state of the game
enum GameState{
    PLAYING, WIN, LOSE
}