package agh.ics.oop;

import java.io.FileNotFoundException;

public interface IObserver {
    void positionChanged() throws FileNotFoundException;

    int getCounter();

    void incrementCounter();

    void changeBigFields(int row,int col,int winner) throws FileNotFoundException;

    void setLastMoves(int row, int col);

    void lastMove();

    void makeMoveRecord(int row,int col,int parRow,int parCol,int counter);

}
