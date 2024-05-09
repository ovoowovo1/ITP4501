package com.example.itp4501assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SinglePlayermModeActivity extends AppCompatActivity {
    private TextView tvMovesCount;
    private Button btnRestart;
    private ImageView[] gameImages = new ImageView[8];
    private int[] buttonValues = new int[8];
    private int moveCount = 0;
    private int firstButtonIndex = -1;
    private int secondButtonIndex = -1;

    private SQLiteDatabase MemberDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_playerm_mode);
        tvMovesCount = findViewById(R.id.tvMovesCount);
        btnRestart = findViewById(R.id.btnRestart);

        gameImages[0] = findViewById(R.id.imgGame1);
        gameImages[1] = findViewById(R.id.imgGame2);
        gameImages[2] = findViewById(R.id.imgGame3);
        gameImages[3] = findViewById(R.id.imgGame4);
        gameImages[4] = findViewById(R.id.imgGame5);
        gameImages[5] = findViewById(R.id.imgGame6);
        gameImages[6] = findViewById(R.id.imgGame7);
        gameImages[7] = findViewById(R.id.imgGame8);


        // Set onClickListeners for the ImageViews
        for (int i = 0; i < gameImages.length; i++) {


            int index = i;
            gameImages[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (firstButtonIndex == -1) {
                        firstButtonIndex = index;
                        // Set the image resource for the ImageView
                        gameImages[index].setImageResource(getImageResource(buttonValues[index]));
                    } else if (secondButtonIndex == -1 && firstButtonIndex != index) {
                        secondButtonIndex = index;
                        // Set the image resource for the ImageView
                        gameImages[index].setImageResource(getImageResource(buttonValues[index]));
                        moveCount++;
                        tvMovesCount.setText("Moves: " + moveCount);
                        checkMatch();
                    }
                }
            });
        }

        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupGame();
            }
        });

        setupGame();
    }


    // Method to set up the game
    private void setupGame() {
        Random random = new Random();
        int count = 0;
        moveCount = 0;
        tvMovesCount.setText("Moves: " + moveCount);


        btnRestart.setVisibility(View.INVISIBLE);
        buttonValues = new int[8];

        // Assign random values to the buttonValues array
        while (count < 8) {
            int randomNum = random.nextInt(4) + 1;
            int foundCount = 0;
            for (int j = 0; j < count; j++) {
                if (buttonValues[j] == randomNum) {
                    foundCount++;
                }
            }

            if (foundCount < 2) {
                buttonValues[count] = randomNum;
                count++;
            }
        }


        // Set the initial state for the ImageViews
        for (int i = 0; i < gameImages.length; i++) {
            gameImages[i].setImageResource(R.drawable.gmaequestionmark); // Set the ImageView to transparent
            gameImages[i].setVisibility(View.VISIBLE);
        }
    }


    // Method to check if the selected images match
    private void checkMatch() {
        if (firstButtonIndex != -1 && secondButtonIndex != -1) {
            final boolean isMatch = buttonValues[firstButtonIndex] == buttonValues[secondButtonIndex];

            gameImages[firstButtonIndex].setImageResource(getImageResource(buttonValues[firstButtonIndex]));
            gameImages[secondButtonIndex].setImageResource(getImageResource(buttonValues[secondButtonIndex]));


            // Use a handler to delay the image change
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isMatch) {
                        gameImages[firstButtonIndex].setVisibility(View.INVISIBLE);
                        gameImages[secondButtonIndex].setVisibility(View.INVISIBLE);
                    } else {

                        gameImages[firstButtonIndex].setImageResource(R.drawable.gmaequestionmark);
                        gameImages[secondButtonIndex].setImageResource(R.drawable.gmaequestionmark);
                    }
                    // Reset the button indices
                    firstButtonIndex = -1;
                    secondButtonIndex = -1;

                    // Check if the game is over
                    checkGameOver();
                }
            }, 1000); // Delay for 1 second
        }
    }


    // Method to check if the game is over
    private void checkGameOver() {
        boolean gameOver = true;
        for (ImageView image : gameImages) {
            if (image.getVisibility() == View.VISIBLE) {
                gameOver = false;
                break;
            }
        }
        if (gameOver) {
            tvMovesCount.setText("Finish Your Moves: " + moveCount);
            btnRestart.setVisibility(View.VISIBLE);
            // Save game result to database
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
            String currentDate = dateFormatter.format(calendar.getTime());
            String currentTime = timeFormatter.format(calendar.getTime());
            try {
                // Create a database if it does not exist
                MemberDB = SQLiteDatabase.openDatabase("/data/data/com.example.itp4501assignment/MemberDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);

                String sql = "CREATE TABLE IF NOT EXISTS GamesLog  (gameID INTEGER PRIMARY KEY AUTOINCREMENT, playDate Date, playTime Time, moves int)  ;";
                MemberDB.execSQL(sql);

                // Insert game result into the table
                MemberDB.execSQL("INSERT INTO GamesLog(playDate, playTime, moves) values"
                        + "('" + currentDate + "', '" + currentTime + "', " + moveCount + "); ");

            } catch (SQLiteException e) {

            }finally{
                MemberDB.close();
            }

        }
    }


    // Helper method to get the image resource for a given number
    private int getImageResource(int number) {
        switch (number) {
            case 1:
                return R.drawable.image1;
            case 2:
                return R.drawable.image2;
            case 3:
                return R.drawable.image3;
            case 4:
                return R.drawable.image4;
            default:
                return android.R.color.transparent;
        }
    }
}
