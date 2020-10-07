package sample;

import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;


public class Controller {
    @FXML private Circle ball;
    @FXML private Rectangle p1paddle;
    @FXML private Rectangle p2paddle;

    @FXML
    public void paddleMove(KeyEvent event) {
        double heightP1 = p1paddle.getY();
        double heightP2 = p2paddle.getY();

        switch (event.getCode()) {
            case W:
                p1paddle.setY(heightP1-10);
                break;
            case S:
                p1paddle.setY(heightP1+10);
                break;
            case UP:
                p2paddle.setY(heightP2-10);
                break;
            case DOWN:
                p2paddle.setY(heightP2+10);
                break;
            default:
                System.out.println("Invalid key");
                break;
        }

    }
}
