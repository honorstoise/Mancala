package com.jonesclass.mancala;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class Game extends AppCompatActivity {

    Button[][] buttons;
    ImageView[][] imageViews;
    ImageView topBowl;
    ImageView bottomBowl;

    TextView otherTextView, playerTextView, turnTextView;

    boolean playerTurn = true;
    int[][] values = {
            {
                4, 4,
                4, 4,
                4, 4
            },
            {
                4, 4,
                4, 4,
                4, 4
            },
            {
                0, 0
            }
    };

    Random randObj = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        otherTextView = findViewById(R.id.other_textView);
        playerTextView = findViewById(R.id.player_textView);
        turnTextView = findViewById(R.id.turn_textView);
        topBowl = findViewById(R.id.imageView_topBowl);
        bottomBowl = findViewById(R.id.imageView_bottomBowl);

        buttons = new Button[][]{
                {
                        findViewById(R.id.button00), findViewById(R.id.button01),
                        findViewById(R.id.button02), findViewById(R.id.button03),
                        findViewById(R.id.button04), findViewById(R.id.button05)
                },
                {
                        findViewById(R.id.button10), findViewById(R.id.button11),
                        findViewById(R.id.button12), findViewById(R.id.button13),
                        findViewById(R.id.button14), findViewById(R.id.button15)
                }
        };

        imageViews = new ImageView[][]{
                {
                        findViewById(R.id.imageView_00), findViewById(R.id.imageView_01),
                        findViewById(R.id.imageView_02), findViewById(R.id.imageView_03),
                        findViewById(R.id.imageView_04), findViewById(R.id.imageView_05)
                },
                {
                        findViewById(R.id.imageView_10), findViewById(R.id.imageView_11),
                        findViewById(R.id.imageView_12), findViewById(R.id.imageView_13),
                        findViewById(R.id.imageView_14), findViewById(R.id.imageView_15)
                },
                {
                    findViewById(R.id.imageView_topBowl), findViewById(R.id.imageView_bottomBowl)
                }
        };

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                int finalI = i;
                int finalJ = j;
                buttons[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonClicked(finalI * 10 + finalJ);
                    }
                });
            }
        }

    }

    protected void buttonClicked(int num) {
        int value = values[num / 10][num % 10];
        if ((playerTurn && num / 10 == 1) || (!playerTurn && num / 10 == 0)) {
            return;
        }
        if (value != 0) {
            values[num / 10][num % 10] = 0;
            move(num, value);
            updateImages();
        }
        int win = checkWin();
        if (win != -1) {
            for (Button[] buttonArr : buttons) {
                for (Button button : buttonArr) {
                    button.setEnabled(false);
                }
            }
            turnTextView.setText(win == 0 ? "You Win!!" : "You Lose :(");
            return;
        }
        // ai
        while (!playerTurn) {
            for (Button[] buttonArr : buttons) {
                for (Button button : buttonArr) {
                    button.setEnabled(false);
                }
            }
            turnTextView.setText("Opponent's Turn");
            int r, rVal;
            do {
                r = randObj.nextInt(6);
                rVal = values[1][r];
            } while (rVal == 0);
            values[1][r] = 0;
            move(10 + r, rVal);
            new CountDownTimer(1500, 1000) {
                public void onFinish() {
                    updateImages();
                    for (Button[] buttonArr : buttons) {
                        for (Button button : buttonArr) {
                            button.setEnabled(true);
                        }
                    }
                }
                public void onTick(long millisUntilFinished) {

                }
            }.start();
            win = checkWin();
            if (win != -1) {
                for (Button[] buttonArr : buttons) {
                    for (Button button : buttonArr) {
                        button.setEnabled(false);
                    }
                }
                turnTextView.setText(win == 0 ? "You Win!!" : "You Lose :(");
                return;
            }
        }
        //
        playerTextView.setText(String.valueOf(values[2][0]));
        otherTextView.setText(String.valueOf(values[2][1]));
        new CountDownTimer(1500, 1000) {
            public void onFinish() {
                int win = checkWin();
                if(win == 1){
                    turnTextView.setText("You Win!!");
                }else if(win == 0){
                    turnTextView.setText("You Lose:(");
                }else{
                    turnTextView.setText(playerTurn ? "Your Turn" : "Opponent's Turn");
                }
            }
            public void onTick(long millisUntilFinished) {

            }
        }.start();
    }

    protected void move(int num, int value) { // animation goes somewhere in this function Andrew
        if (value == 0) {
            if (num == 20 || num == 21) {
                return;
            }
            if (values[playerTurn ? 1 : 0][num % 10] != 0
                    && values[playerTurn ? 0 : 1][num % 10] == 1) {
                // captures
                int total = values[1][num % 10] + values[0][num % 10];
                values[1][num % 10] = 0; values[0][num % 10] = 0;
                values[2][playerTurn ? 0 : 1] += total;
            }
            playerTurn = !playerTurn;
            return;
        }

        int newVal;
        if (num == 20) { // player side pot
            newVal = 15;
        } else if (num == 21) { // opponent side pot
            newVal = 00;
        } else if (num / 10 == 0) { // player side
            if (num % 10 < 5) { // not changing
                newVal = num + 1;
            } else { // changing
                newVal = playerTurn ? 20 : 15;
            }
        } else { // opponent side
            if (num % 10 > 0) { // not changing
                newVal = num - 1;
            } else { // changing
                newVal = playerTurn ? 00 : 21;
            }
        }
        values[newVal / 10][newVal % 10]++;
        move(newVal, value - 1);
    }

    int checkWin() {
        int total1 = 0, total2 = 0;
        for (int i = 0; i < values[0].length; i++) {
            total1 += values[0][i];
            total2 += values[1][i];
        }
        if (total1 == 0 || total2 == 0) {
            return (total1 + values[2][0] > total2 + values[2][1]) ? 0 : 1;
        }
        return -1;
    }

    public void updateImages(){
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                buttons[i][j].setText(String.valueOf(values[i][j]));
                if(values[i][j] == 0){
                    imageViews[i][j].setImageResource(R.drawable.ball);
                }else if(values[i][j] == 1){
                    imageViews[i][j].setImageResource(R.drawable.ball1);
                }else if(values[i][j] == 2){
                    imageViews[i][j].setImageResource(R.drawable.ball2);
                }else if(values[i][j] == 3){
                    imageViews[i][j].setImageResource(R.drawable.ball3);
                }else if(values[i][j] == 4){
                    imageViews[i][j].setImageResource(R.drawable.ball4);
                }else{
                    imageViews[i][j].setImageResource(R.drawable.ball5);
                }
            }
        }
        if(values[2][1] == 0){
            topBowl.setImageResource(R.drawable.bowl);
        }else if(values[2][1] == 1){
            topBowl.setImageResource(R.drawable.bowl1);
        }else if(values[2][1] == 2){
            topBowl.setImageResource(R.drawable.bowl2);
        }else if(values[2][1] == 3){
            topBowl.setImageResource(R.drawable.bowl3);
        }else if(values[2][1] == 4){
            topBowl.setImageResource(R.drawable.bowl4);
        }else{
            topBowl.setImageResource(R.drawable.bowl5);
        }

        if(values[2][0] == 0){
            bottomBowl.setImageResource(R.drawable.bowl);
        }else if(values[2][0] == 1){
            bottomBowl.setImageResource(R.drawable.bowl1);
        }else if(values[2][0] == 2){
            bottomBowl.setImageResource(R.drawable.bowl2);
        }else if(values[2][0] == 3){
            bottomBowl.setImageResource(R.drawable.bowl3);
        }else if(values[2][0] == 4){
            bottomBowl.setImageResource(R.drawable.bowl4);
        }else{
            bottomBowl.setImageResource(R.drawable.bowl5);
        }
    }
}