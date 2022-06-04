package application;

import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.control.Cell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Board extends Paren{
    private VBox rows = new VBox();
    private boolean enemy = false;
    public int ships = 5;

    public Board(boolean enemy, EventHandler<? super MouseEvent> handler){
        this.enemy = enemy;
        for(int y = 0; y < 10; y++){
            HBox row = new HBox();
            for(int x = 0; x < 10; x++){
                Cell c = new Cell(x, y, this);
                c.setOnMouseClicked(handler);
                row.getChildren().add(c);
            }
            rows.getChildren().add(row);
        }
        getChildren().add(rows);
    }

    public boolean placeShip(Ship ship, int x, int y){
        if(canPlaceShip(ship, x, y)){
            int length = ship.type;
            boolean vertical = ship.vertical;

            if(vertical){
                for(int i = y; i < y + length; i++){
                    Cell cell = getCell(x, i);
                    cell.ship = ship;
                    if(!enemy){  // ne moze brod da se postavi na protivnicku tablu
                        cell.setFill(Color.LIGHTGRAY);
                        cell.setStroke(Color.GREY);
                    }
                }
            }else{
                for(int i = x; i < x + length; i++){
                    Cell cell = getCell(i, y);
                    cell.ship = ship;
                    if(!enemy){
                        cell.setFill(Color.LIGHTGRAY);
                        cell.setStroke(Color.GREY);
                    }
                }
            }
            return true;
        }
        return false;
    }

    public Cell getCell(int x, int y){
        return (Cell) ((HBox)rows.getChildren().get(y)).getChildren().get(x);
    }

    private Cell[] getNeighbors(int x, int y){
        Point2D[] points = new Point2D[]{
                new Point2D(x - 1, y),
                new Point2D(x + 1, y),
                new Point2D(x, y - 1),
                new Point2D(x, y + 1)
        };

        List<Cell> neighbors = new ArrayList<Cell>();

        for(Point2D p: points){
            if(isValidPoint(p)){
                neighbors.add(getCell((int)p.getX(), (int)p.getY()));
            }
        }
        return neighbors.toArray(new Cell[0]);
    }

    private boolean isValidPoint(Point2D point){
        return isValidPoint(point.getX(), point.getY());
    }

    private boolean isValidPoint(double x, double y){
        return x >= 0 && x < 10 && y >= 0 && y < 10;
    }

    private boolean canPlaceShip(Ship ship, int x, int y){
        int length = ship.type;

        if(ship.vertical){
            for(int i = y; i < y + length; i++){
                if(!isValidPoint(x, i))
                    return false;

                Cell cell = getCell(x, i);
                if(cell.ship != null)
                    return false;

                for(Cell neighbor : getNeighbors(x, i)){
                    if(!isValidPoint(x, i))
                        return false;

                    if(neighbor.ship != null)
                        return false;
                }
            }
        }else{
            for(int i = x; i < x + length; i++) {
                if (!isValidPoint(i, y))
                    return false;

                Cell cell = getCell(i, y);
                if (cell.ship != null)
                    return false;

                for (Cell neighbor : getNeighbors(i, y)) {
                    if (!isValidPoint(i, y))
                        return false;

                    if (neighbor.ship != null)
                        return false;
                }
            }
        }
        return true;
    }

    public class Cell extends Rectangle{
        public int x, y;   // pozicija celije
        public Ship ship = null;
        public boolean wasShot = false;  // da li smo vec pogadjali tu celiju

        private Board board;

        public Cell(int x, int y, Board board){
            super(21, 21);
            this.x = x;
            this.y = y;
            this.board = board;
            setFill(Color.DODGERBLUE);
            setStroke(Color.DARKBLUE);
        }

        public boolean shoot(){
            wasShot = true;
            setFill(Color.BLACK);

            if(ship != null){   // da li je deo broda u toj celiji
                ship.hit();
                setFill(Color.RED);  // ako smo pogodili brod, oboj celiju u crveno
                if(!ship.isAlive()){
                    board.ships--;
                }
                return true;
            }
            return false;
        }
    }
}
