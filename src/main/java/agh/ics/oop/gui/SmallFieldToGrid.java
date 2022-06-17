package agh.ics.oop.gui;

import agh.ics.oop.IObserver;
import agh.ics.oop.SmallField;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class SmallFieldToGrid {

    private final GridPane grid=new GridPane();
    private final SmallField field;

    private final Image circleImage=new Image(new FileInputStream("src/main/resources/circle.png"));
    private final Image crossImage=new Image(new FileInputStream("src/main/resources/cross.png"));

    private IObserver observer;

    public SmallFieldToGrid(SmallField f) throws FileNotFoundException {
        field=f;
        makeGrid();
        updateGrid();

    }

    public void makeGrid(){
        grid.getColumnConstraints().add(new ColumnConstraints(70));
        grid.getColumnConstraints().add(new ColumnConstraints(70));
        grid.getColumnConstraints().add(new ColumnConstraints(70));
        grid.getRowConstraints().add(new RowConstraints(70));
        grid.getRowConstraints().add(new RowConstraints(70));
        grid.getRowConstraints().add(new RowConstraints(70));
        GridPane.setHalignment(grid, HPos.RIGHT);
        GridPane.setValignment(grid, VPos.BOTTOM);
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                Pane k=new Pane();
                k.setMinSize(65,65);
                k.setMaxSize(65,65);
                grid.add(k,i,j);
                GridPane.setHalignment(k,HPos.CENTER);
                GridPane.setValignment(k,VPos.CENTER);
            }
        }
        grid.setGridLinesVisible(true);
        grid.getChildren().forEach(item ->{
            item.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (field.getCanClickHere()){
                        int column;
                        int row;
                        if(GridPane.getColumnIndex((Node)event.getSource() )!=null && GridPane.getRowIndex((Node)event.getSource() )!=null){
                            column=GridPane.getColumnIndex((Node)event.getSource() );
                            row=GridPane.getRowIndex((Node)event.getSource() );
                            Node node= (Node) event.getSource();
                            Parent parent=node.getParent();
                            node=parent;
                            parent=node.getParent();
                            int parentRow = GridPane.getRowIndex(parent);
                            int parentColumn = GridPane.getColumnIndex(parent);
                            boolean flag=false;
                            if(field.move(row,column, observer.getCounter())){
                                flag=true;
                                observer.makeMoveRecord(row,column,parentRow,parentColumn, observer.getCounter());
                                observer.incrementCounter();
                            }
                            if(field.statusIsFull()){
                                try {
                                    observer.changeBigFields(parentRow,parentColumn,field.getWinner());
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            updateGrid();
                            try {
                                observer.positionChanged();
                                if(flag){
                                    observer.setLastMoves(row,column);
                                }
                                else{
                                    observer.lastMove();
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        });
    }


    public void updateGrid(){
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                if (field.getFields()[i][j]==1){
                    ImageView k=new ImageView(circleImage);
                    k.setFitWidth(80);
                    k.setFitHeight(80);
                    grid.add(k,j,i);
                }
                else if(field.getFields()[i][j]==2){
                    ImageView a=new ImageView(crossImage);
                    a.setFitWidth(55);
                    a.setFitHeight(55);
                    grid.add(a,j,i);
                }
            }
        }
    }

    public GridPane getGrid(){
        return grid;
    }

    public void addObserver(IObserver k){
        observer=k;
    }

}
