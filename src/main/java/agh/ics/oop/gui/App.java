package agh.ics.oop.gui;

import agh.ics.oop.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;


public class App extends Application implements IObserver{

    private GridPane mainGrid;
    private int counter;
    private ImageView imageView;
    private VBox vbox;
    private VBox inScrollPane;
    private boolean isFull;

    private int[][] bigFields;

    private int mainWinner; //1-circle,2-cross,3-draw


    private final Image circleImage=new Image(new FileInputStream("src/main/resources/circle.png"));
    private final Image crossImage=new Image(new FileInputStream("src/main/resources/cross.png"));
    private final Image drawImage=new Image(new FileInputStream("src/main/resources/draw.png"));
    private final Image icon=new Image(new FileInputStream("src/main/resources/icon.png"));

    public App() throws FileNotFoundException {
    }

    private HBox announcement;
    private Stage stg;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stg=primaryStage;
        initEntireProgram(stg,0);
        primaryStage.setTitle("Ultimate TicTacToe");
        primaryStage.getIcons().add(icon);
        primaryStage.show();
    }


    @Override
    public void stop(){
        if(timer!=null){
            timer.cancel();
            timer.purge();
        }
    }

    private void initEntireProgram(Stage primaryStage, int whichButton) throws FileNotFoundException {
        announcement=new HBox();
        isFull=false;
        HBox hbox = new HBox();
        counter=0;
        mainWinner=0;
        bigFields=new int[3][3];

        mainGrid=new GridPane();
        mainGrid.getColumnConstraints().add(new ColumnConstraints(210));
        mainGrid.getColumnConstraints().add(new ColumnConstraints(210));
        mainGrid.getColumnConstraints().add(new ColumnConstraints(210));
        mainGrid.getRowConstraints().add(new RowConstraints(210));
        mainGrid.getRowConstraints().add(new RowConstraints(210));
        mainGrid.getRowConstraints().add(new RowConstraints(210));

        vbox=new VBox();

        Button btn=new Button("Jak grac?");
        btn.setOnAction(e->{
            String url = "https://ultimate-t3.herokuapp.com/rules";
            String os = System.getProperty("os.name").toLowerCase();
//            System.out.println(os);
            if (os.contains("win")){
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://ultimate-t3.herokuapp.com/rules"));
                    } catch (IOException | URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            else if (os.contains("nix") || os.contains("nux")){
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("xdg-open " + url);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        vbox.getChildren().add(btn);

        Label whoseMove=new Label("Czyj ruch: ");
        whoseMove.setFont(Font.font(35));
        makeImageView();
        vbox.getChildren().addAll(whoseMove,imageView);

        RadioButton unlimitedTime=new RadioButton("Nielimitowany czas");
        unlimitedTime.setOnAction(d->{
            if(timer !=null){
                timer.cancel();
            }
            if (whichButton!=0){
                try {
                    initEntireProgram(primaryStage,0);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                seconds=0;
            }
        });
        RadioButton tenSecTime=new RadioButton("10 sekund");
        tenSecTime.setOnAction(a->{
            if(timer !=null){
                timer.cancel();
            }
            if(whichButton!=1){
                try {
                    initEntireProgram(primaryStage,1);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                time(10);
                Label lab=new Label("10");
                vbox.getChildren().add(7,lab);
            }
        });

        RadioButton halfMinuteTime=new RadioButton("30 sekund");
        halfMinuteTime.setOnAction(b->{
            if(timer !=null){
                timer.cancel();
            }
            if (whichButton!=2){
                try {
                    initEntireProgram(primaryStage,2);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                time(30);
                Label lab=new Label("30");
                vbox.getChildren().add(7,lab);
            }
        });
        RadioButton oneMinuteTime=new RadioButton("1 minuta");
        oneMinuteTime.setOnAction(c->{
            if(timer !=null){
                timer.cancel();
            }
            if (whichButton!=3){
                try {
                    initEntireProgram(primaryStage,3);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                time(60);
                Label lab=new Label("60");
                vbox.getChildren().add(7,lab);
            }
        });
        vbox.getChildren().addAll(unlimitedTime,tenSecTime,halfMinuteTime,oneMinuteTime);
        ToggleGroup buttonGroup=new ToggleGroup();
        unlimitedTime.setToggleGroup(buttonGroup);
        tenSecTime.setToggleGroup(buttonGroup);
        halfMinuteTime.setToggleGroup(buttonGroup);
        oneMinuteTime.setToggleGroup(buttonGroup);

        if(whichButton==0){
            unlimitedTime.fire();
        }
        else if(whichButton==1){
            tenSecTime.fire();
        }
        else if(whichButton==2){
            halfMinuteTime.fire();
        }
        else if(whichButton==3){
            oneMinuteTime.fire();
        }


        ScrollPane moves = new ScrollPane();
        moves.setHmax(50);
        moves.setMaxHeight(300);
        moves.setMinHeight(300);
        moves.setPannable(true);
        moves.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        moves.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        Label turn=new Label("Ruch");
        turn.setFont(Font.font(null,FontWeight.BOLD, FontPosture.REGULAR,25));
        Label player=new Label("Gracz");
        player.setFont(Font.font(null,FontWeight.BOLD, FontPosture.REGULAR,25));
        Label location=new Label("Miejsce");
        location.setFont(Font.font(null,FontWeight.BOLD, FontPosture.REGULAR,25));
        HBox record=new HBox();
        record.setSpacing(15);
        record.getChildren().addAll(turn,player,location);
        inScrollPane=new VBox();
        inScrollPane.getChildren().add(record);
        moves.setContent(inScrollPane);
        announcement.setAlignment(Pos.CENTER);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.getChildren().addAll(moves,announcement);

        hbox.setSpacing(5);
        vbox.setSpacing(15);
        hbox.getChildren().addAll(mainGrid,vbox);
        makeSmallFields();
        allPlacesCanMove();
        Scene scene = new Scene(hbox);
        primaryStage.setScene(scene);
    }


    private Timer timer;
    private TimerTask task;
    private int seconds;

    private void time(int sec){
        seconds=sec;
        final int[] totalSec = {sec};
        timer = new Timer();
        task=new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Label lb=new Label(String.valueOf(totalSec[0]));
                        lb.setFont(Font.font(35));
                        vbox.getChildren().set(7,lb);
                    }
                });
                totalSec[0]--;
                if (totalSec[0]<0){
                    timer.cancel();
                    counter++;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if(!isFull){
                                try {
                                    makeEmptyMoveRecord(counter-1);
                                    positionChanged();
                                    allPlacesCanMove();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                }
            }
        };
        timer.schedule(task,0,1000);
    }

    private SmallField[][] fields;
    private void makeSmallFields() throws FileNotFoundException {
        fields=new SmallField[3][3];
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                fields[i][j]=new SmallField();
                SmallFieldToGrid smallFieldToGrid=new SmallFieldToGrid(fields[i][j]);
                Pane pane=new Pane();
                pane.setStyle("-fx-border-color: black;-fx-border-width: 2");
                smallFieldToGrid.addObserver(this);
                GridPane k= smallFieldToGrid.getGrid();
                pane.getChildren().add(k);
                GridPane.setHalignment(k, HPos.CENTER);
                GridPane.setValignment(k, VPos.CENTER);
                GridPane.setColumnIndex(pane,j);
                GridPane.setRowIndex(pane,i);
                mainGrid.getChildren().add(pane);
            }
        }
    }




    private void changeBackgrounds(int col, int row){
        if(bigFields[row][col]==0 && flag){
            mainGrid.getChildren().forEach(item3->{
                if (item3 instanceof Pane){
                    int r=GridPane.getRowIndex(item3);
                    int c=GridPane.getColumnIndex(item3);
                    if(c== col && r== row){
                        Pane k=(Pane) item3;
                        k.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, null, null)));
                        fields[row][col].setCanClickHere(true);
                    }
                }
            });
        }
        else{
            allPlacesCanMove();
        }
    }

    private void allPlacesCanMove(){
        flag=true;
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                if(bigFields[i][j]==0){
                    int finalJ = j;
                    int finalI = i;
                    mainGrid.getChildren().forEach(item2->{
                        if(item2 instanceof Pane){
                            if(GridPane.getColumnIndex(item2)== finalJ && GridPane.getRowIndex(item2)== finalI){
                                Pane k=(Pane) item2;
                                k.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK,null,null)));
                                fields[finalI][finalJ].setCanClickHere(true);
                            }
                        }
                    });
                }
            }
        }
    }

    private void makeImageView() throws FileNotFoundException {
        if (counter%2==0){
            Image img=new Image(new FileInputStream("src/main/resources/circle.png"));
            imageView=new ImageView(img);
        }
        else{
            Image image=new Image(new FileInputStream("src/main/resources/cross.png"));
            imageView=new ImageView(image);
        }
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);
    }



    private void makeAnnouncement(){
        if(seconds!=0){
            timer.cancel();
            timer.purge();
            task.cancel();
        }
        if (mainWinner!=0){
            if(mainWinner==1){
                ImageView view=new ImageView(circleImage);
                Label label=new Label("Win!");
                label.setStyle("-fx-text-fill: Red ;");
                label.setAlignment(Pos.CENTER);
                label.setFont(Font.font(30));
                view.setFitHeight(50);
                view.setFitWidth(50);
                announcement.getChildren().addAll(view,label);
            }
            else if(mainWinner==2){
                ImageView view=new ImageView(crossImage);
                Label label=new Label("Win!");
                label.setStyle("-fx-text-fill: blue ;");
                label.setAlignment(Pos.CENTER);
                label.setFont(Font.font(30));
                view.setFitHeight(40);
                view.setFitWidth(40);
                announcement.getChildren().addAll(view,label);
            }
            else if (mainWinner==3){
                Label label=new Label("It's a draw");
                label.setAlignment(Pos.CENTER);
                label.setFont(Font.font(30));
                announcement.getChildren().add(label);
            }
            Button playAgain=new Button("Zagraj jeszcze raz");
            playAgain.setOnAction(k->{
                seconds=0;
                if(timer !=null){
                    timer.cancel();
                }
                try {
                    initEntireProgram(stg,0);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
            vbox.getChildren().add(playAgain);
            for (int i=0;i<3;i++){
                for (int j=0;j<3;j++){
                    fields[i][j].setCanClickHere(false);
                }
            }
            mainGrid.getChildren().forEach(item5->{
                if(item5 instanceof Pane){
                    Pane k=(Pane) item5;
                    k.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,null,null)));
                }
            });
        }
    }



    private void checkField(){
        if (isFull){
            return;
        }
        for (int i=0;i<3;i++){
            if (bigFields[i][0]==bigFields[i][1] && bigFields[i][1]==bigFields[i][2] && bigFields[i][2]!=0 && bigFields[i][2]!=3){
                mainWinner=bigFields[i][0];
                if(seconds!=0){
                    timer.cancel();
                }
                isFull=true;
                return;
            }
            if (bigFields[0][i]==bigFields[1][i] && bigFields[1][i]==bigFields[2][i] && bigFields[0][i]!=0 && bigFields[0][i]!=3){
                mainWinner=bigFields[0][i];
                if(seconds!=0){
                    timer.cancel();
                }
                isFull=true;
                return;
            }
        }
        if (bigFields[0][0]==bigFields[1][1] && bigFields[1][1]==bigFields[2][2] && bigFields[0][0]!=0 && bigFields[0][0]!=3){
            mainWinner=bigFields[0][0];
            if(seconds!=0){
                timer.cancel();
            }
            isFull=true;
            return;
        }
        if (bigFields[0][2]==bigFields[1][1] && bigFields[1][1]==bigFields[2][0] && bigFields[1][1]!=0 && bigFields[1][1]!=3){
            mainWinner=bigFields[1][1];
            if(seconds!=0){
                timer.cancel();
            }
            isFull=true;
            return;
        }
        boolean flag=true;
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                if(bigFields[i][j]==0){
                    flag=false;
                }
            }
        }
        if (flag){
            isFull=true;
            mainWinner=3;
        }
    }



    @Override
    public void positionChanged() throws FileNotFoundException {
        if (seconds!=0 && mainWinner==0){
            timer.cancel();
            time(seconds);
        }
        makeImageView();
        mainGrid.getChildren().forEach(item->{
            if (item instanceof Pane) {
                Pane k = (Pane) item;
                k.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
                fields[GridPane.getRowIndex(item)][GridPane.getColumnIndex(item)].setCanClickHere(false);
            }
        });
        vbox.getChildren().set(2,imageView);
    }

    @Override
    public int getCounter() {
        return counter;
    }

    @Override
    public void incrementCounter() {
        counter+=1;
    }

    private boolean flag=true;

    @Override
    public void changeBigFields(int row, int col, int winner) throws FileNotFoundException {
        bigFields[row][col]=winner;
        if (winner!=3){
            flag=false;
        }
        mainGrid.getChildren().forEach(itm->{
            if (itm instanceof Pane){
                if (GridPane.getColumnIndex(itm)==col && GridPane.getRowIndex(itm)==row){
                    Pane k=(Pane) itm;
                    k.getChildren().add(getBigImageView(winner));
                }
            }
        });
        checkField();
        makeAnnouncement();
    }

    private ImageView getBigImageView(int winner){
        if(winner==1){
            ImageView k=new ImageView(circleImage);
            k.setFitWidth(210);
            k.setFitHeight(210);
            return k;
        }
        if(winner==2){
            ImageView k=new ImageView(crossImage);
            k.setFitWidth(210);
            k.setFitHeight(210);
            return k;
        }
        else{
            ImageView k=new ImageView(drawImage);
            k.setFitWidth(210);
            k.setFitHeight(210);
            return k;
        }
    }

    private int lastMoveCol;
    private int lastMoveRow;

    @Override
    public void setLastMoves(int row, int col) {
        lastMoveCol=col;
        lastMoveRow=row;
        if (mainWinner==0){
            changeBackgrounds(col,row);
        }
    }

    @Override
    public void lastMove() {
        if (mainWinner==0){
            changeBackgrounds(lastMoveCol,lastMoveRow);
        }
    }

    @Override
    public void makeMoveRecord(int row, int col, int parRow, int parCol, int counter) {
        String par=getPosName(parRow,parCol);
        String child=getPosName(row,col);
        Label move=new Label(String.valueOf(counter+1));
        ImageView vw;
        if((counter%2)+1==1){
            vw=new ImageView(circleImage);
        }
        else{
            vw=new ImageView(crossImage);
        }
        vw.setFitWidth(20);
        vw.setFitHeight(20);
        Label location=new Label(par+"/"+child);
        HBox hbox=new HBox();
        hbox.setSpacing(45);
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(move,vw,location);
        inScrollPane.getChildren().add(hbox);
    }

    public void makeEmptyMoveRecord(int cr){
        Label move=new Label(String.valueOf(cr+1));
        ImageView vw;
        if((cr%2)+1==1){
            vw=new ImageView(circleImage);
        }
        else{
            vw=new ImageView(crossImage);
        }
        vw.setFitWidth(20);
        vw.setFitHeight(20);
        Label location=new Label("Czas minal!");
        HBox hbox=new HBox();
        hbox.setSpacing(35);
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(move,vw,location);
        inScrollPane.getChildren().add(hbox);
    }

    private String getPosName(int row, int col){
        switch (row){
            case 0:
                switch (col){
                    case 0:
                        return "NW";
                    case 1:
                        return "N";
                    case 2:
                        return "NE";
                }
                break;
            case 1:
                switch (col){
                    case 0:
                        return "W";
                    case 1:
                        return "C";
                    case 2:
                        return "E";
                }
                break;
            case 2:
                switch (col){
                    case 0:
                        return "SW";
                    case 1:
                        return "S";
                    case 2:
                        return "SE";
                }
                break;
            default:
                return "";
        }
        return "";
    };
}
