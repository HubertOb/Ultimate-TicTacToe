package agh.ics.oop;


public class SmallField {
    private final int[][] fields=new int[3][3];  //0-empty,1-circle,2-cross
    private boolean isFull=false;
    private int winner=0;  //1- circle, 2-cross, 3-draw
    private boolean canClickHere;



    public int[][] getFields(){
        return fields;
    }

    public boolean move(int row, int col, int counter){
        if(isFull){
            return false;
        }
        if (fields[row][col]==0){
            fields[row][col]=(counter%2)+1;
            checkField();
            return true;
        }
        else{
            return false;
        }
    }


    private void checkField(){
        if (isFull){
            return;
        }
        for (int i=0;i<3;i++){
            if (fields[i][0]==fields[i][1] && fields[i][1]==fields[i][2] && fields[i][2]!=0){
                winner=fields[i][0];
                isFull=true;
                return;
            }
            if (fields[0][i]==fields[1][i] && fields[1][i]==fields[2][i] && fields[0][i]!=0){
                winner=fields[0][i];
                isFull=true;
                return;
            }
        }
        if (fields[0][0]==fields[1][1] && fields[1][1]==fields[2][2] && fields[0][0]!=0){
            winner=fields[0][0];
            isFull=true;
            return;
        }
        if (fields[0][2]==fields[1][1] && fields[1][1]==fields[2][0] && fields[1][1]!=0){
            winner=fields[1][1];
            isFull=true;
            return;
        }
        boolean flag=true;
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                if (fields[i][j] == 0) {
                    flag = false;
                    break;
                }
            }
        }
        if (flag){
            isFull=true;
            winner=3;
        }
    }

    public boolean statusIsFull(){
        return isFull;
    }

    public int getWinner(){
        return winner;
    }

    public void setCanClickHere(boolean t){
        canClickHere=t;
    }

    public boolean getCanClickHere(){
        return canClickHere;
    }
}
