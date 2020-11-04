package Pong;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.util.Random;

public class Controller {
    //pulls the FXML objects in from the Game.FXML file
    @FXML private Circle ball;
    @FXML private Rectangle p1paddle;
    @FXML private Rectangle p2paddle;
    @FXML private AnchorPane pane;
    @FXML private Label p1Score;
    @FXML private Label p2Score;
    @FXML private Button startButton;
    @FXML private Label win;
    @FXML private AnchorPane pauseScreen;
    @FXML private AnchorPane endScreen;
    @FXML private AnchorPane controlsScreen;
    private double p1paddleDY;
    private double p2paddleDY;
    private double ballDX;
    private double ballDY;
    int scoreP1 = 0;
    int scoreP2 = 0;
    int maxScore = 3;
    boolean scoreCheck = true;

    //AnimationTimer allows for a continuous input to be registered.
    AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long l) {
            double p1paddleY = p1paddle.getY(); //sets a height variable for p1 equivalent to p1paddle's Y location
            double p2paddleY = p2paddle.getY(); //sets a height variable for p2 equivalent to p2paddle's Y location
            double ballX = ball.getLayoutX();
            double ballY = ball.getLayoutY();
            double maxSpeed = 8; //the maximum pixels per frame that the ball will move

            //adds the ball x and y deltas to the ball's coordinates
            ballX += ballDX;
            ballY += ballDY;
            //makes sure the ball doesn't go out of bounds
            if (ballY > pane.getHeight()-10 || ballY < pane.getMinHeight()+10) {
                ballDY *= -1;
            }

            //if the ball hits paddle 1, its speed will be inverted and increased slightly
            if ((ballY >= p1paddleY && ballY <= p1paddleY+100) && (ballX <= pane.getMinWidth()+25 && ballX >= pane.getMinWidth()+15)) {
                if (ballDX < maxSpeed && ballDX > -maxSpeed) {
                    ballDX *= -1.3;
                } else {
                    ballDX *= -1;
                }

                //if the ball hits the paddle's lower third and its Y speed is positive (going upward), the ball's speed will be inverted
                if (ballY < p1paddleY + 34 && ballDY > 0) ballDY *= -1;
                //if the ball hits the paddle's upper third and its Y speed is negative (going downward), the ball's speed will be inverted
                if (ballY > p1paddleY + 67 && ballDY <= 0) ballDY *= -1;
            }

            //if the ball hits paddle 2, its speed will be inverted and increased slightly
            if ((ballY >= p2paddleY && ballY <= p2paddleY+100) && (ballX >= pane.getWidth()-25 && ballX <= pane.getWidth()-15)) {
                if (ballDX < maxSpeed && ballDX > -maxSpeed) {
                    ballDX *= -1.3;
                } else {
                    ballDX *= -1;
                }

                //if the ball hits the paddle's lower third and its Y speed is positive (going upward), the ball's speed will be inverted
                if (ballY < p2paddleY+34 && ballDY > 0) ballDY *= -1;
                //if the ball hits the paddle's upper third and its Y speed is negative (going downward), the ball's speed will be inverted
                if (ballY > p2paddleY+67 && ballDY <= 0) ballDY *= -1;
            }

            //calls the score method when the ball passes the edge of the pane
            if (ballX < pane.getMinWidth()-10) {
                //stops the timer to prevent more scores from being counted
                timer.stop();
                //calls the score method for player 2
                score(2);
            }
            if (ballX > pane.getWidth()+10) {
                timer.stop();
                //calls the score method for player 1
                score(1);
            }

            //sets the ball's coordinates from the xml to the local x and y coordinates
            ball.setLayoutX(ballX);
            ball.setLayoutY(ballY);

            p1paddleY += p1paddleDY;
            p2paddleY += p2paddleDY;

            //prevents paddle 1 from going higher than the pane's height
            if (p1paddleY < 0) {
                p1paddleY = 0;
            }
            //prevents paddle 2 from going higher than the pane's height
            if (p2paddleY < 0) {
                p2paddleY = 0;
            }
            //prevents paddle 1 from going lower than the pane's height
            if (p1paddleY > 500) {
                p1paddleY = 500;
            }
            //prevents paddle 2 from going lower than the pane's height
            if (p2paddleY > 500) {
                p2paddleY = 500;
            }
            p1paddle.setY(p1paddleY);
            p2paddle.setY(p2paddleY);
        }
    };

    //method called by the AnchorPane and is accessed on every keypress
    public void paddleMove(KeyEvent event) {
        //switch that takes the keyboard input and changes the outcome according to which key was pressed
        switch (event.getCode()) {
            case W:
                p1paddleDY = -5; //updates the Y delta to be -5 pixels per frame (goes up)
                break;
            case S:
                p1paddleDY = 5; //updates the Y delta to be 5 pixels per frame (goes down)
                break;
            case UP:
                p2paddleDY = -5; //updates the Y delta for paddle 2 to be -5 pixels per frame (goes up)
                break;
            case DOWN:
                p2paddleDY = 5; //updates the Y delta for paddle 2 to be 5 pixels per frame (goes down)
                break;
            case ESCAPE:
                pauseGame();
                break;
        }
    }

    public void paddleStop(KeyEvent event) {
        switch (event.getCode()) {
            case W:
            case S:
                p1paddleDY = 0; //updates the Y speed of paddle 1 to be 0 pixels per frame (stopped)
                break;
            case UP:
            case DOWN:
                p2paddleDY = 0; //updates the Y speed of paddle 2 to be 0 pixels per frame (stopped)
                break;
        }
    }

    //called everytime the mouse is clicked, but only outputs when conditions are met
    public void resetBoard(MouseEvent event) {
        //score check is true only at the beginning of the game or when a player scores
        if (scoreCheck && !endScreen.isVisible() && !pauseScreen.isVisible()) {
            if (event.getButton() == MouseButton.PRIMARY) {
                Random random = new Random();
                //ternary operator to randomize the ball's x and y speed to be either 2 or -2
                ballDX = random.nextBoolean() ? 2 : -2;
                ballDY = random.nextBoolean() ? 2 : -2;

                //sets the x and y coords for the ball to be centered
                ball.setLayoutX(400);
                ball.setLayoutY(300);
                //sets the paddles to be centered
                p1paddle.setY(250);
                p2paddle.setY(250);

                //starts the animation timer again
                timer.start();
                //sets the scoreCheck to be false so the board can't be reset mid game
                scoreCheck = false;
            }
        }
    }

    //the start of a method to pause the game and return to the menu (not working yet)
    public void pauseGame() {
        timer.stop();
        pauseScreen.setVisible(true);
    }
    //accessed when an unpause button is pressed in that pause menu
    public void unpauseGame() {
        pauseScreen.setVisible(false);
        timer.start();
        pane.requestFocus();
    }

    //sets the score for each player
    public void score(int player) {
        //converts the labels for each score into an integer and stores it in scoreP1 or scoreP2
        scoreP1 = Integer.parseInt(p1Score.getText());
        scoreP2 = Integer.parseInt(p2Score.getText());

        //iterates the score for the player who scored
        if (player == 1) scoreP1 ++;
        if (player == 2) scoreP2 ++;

        //calls method winner when a player's score reaches the max score, passing the player who won
        if (scoreP1 == maxScore) winner(1);
        if (scoreP2 == maxScore) winner(2);

        //converts the scoreP1 and scoreP2 values back to text and updates the labels to display the new score
        p1Score.setText(Integer.toString(scoreP1));
        p2Score.setText(Integer.toString(scoreP2));

        //allows the mouse to be clicked again to reset the ball and paddle's positions
        scoreCheck = true;
    }

    public void winner(int player) {
        if (player == 1) {
            win.setText("PLAYER 1 WINS!");
        }
        if (player == 2) {
            win.setText("PLAYER 2 WINS!");
        }
        endScreen.setVisible(true);
    }

    //called when the stop game buttons are pressed in the pause menu or post-game menu
    public void stopGame() throws Exception {
        //loads the start screen fxml file
        Parent root = FXMLLoader.load(getClass().getResource("StartScreen.fxml"));
        Stage primaryStage = (Stage) p1paddle.getScene().getWindow();
        primaryStage.setScene(new Scene(root));
    }

    //called when the button on StartScreen.fxml is pushed -> changes the scene to Game.fxml
    public void startGame() throws Exception {
        Parent root2 = FXMLLoader.load(getClass().getResource("Game.fxml"));
        Stage secondaryStage = (Stage) startButton.getScene().getWindow();
        secondaryStage.setScene(new Scene(root2));
        root2.requestFocus();
    }

    //called when the restart game button is pressed after the previous game is completed
    public void restartGame() {
        //clears the score and winner text
        p1Score.setText("0");
        p2Score.setText("0");
        win.setText("");
        //makes the end screen invisible
        endScreen.setVisible(false);
        //sets focus to the game pane so keyboard inputs will work
        pane.requestFocus();

        //centers the ball
        ball.setLayoutX(400);
        ball.setLayoutY(300);

        //centers the paddles
        p1paddle.setY(250);
        p2paddle.setY(250);
    }

    //methods to set the controls menu visible or invisible when their respective buttons are pushed
    public void controlsScreen() {
        controlsScreen.setVisible(true);
    }
    public void closeControls() {
        controlsScreen.setVisible(false);
    }
}