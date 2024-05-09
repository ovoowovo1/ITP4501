package com.example.itp4501assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PlayWithAIModeActivity extends AppCompatActivity {


    private TextView tvMovesCount, tvPLayerMark, tvAIMark, tvWhoTurnPlayerOrAI;
    private Button btnRestart;
    private ImageView[] gameImages = new ImageView[20];
    private int[] buttonValues = new int[20];
    private int moveCount = 0;
    private int firstButtonIndex = -1;
    private int secondButtonIndex = -1;
    private int playerScore = 0;
    private int aiScore = 0;
    private boolean isPlayerTurn = true;


    // AI memory to store previously flipped cards
    private HashMap<Integer, Integer> aiMemory = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_with_aimode);
        tvMovesCount = findViewById(R.id.tvMovesCount);
        tvWhoTurnPlayerOrAI = findViewById(R.id.tvWhoTurnPlayerOrAI);
        tvPLayerMark = findViewById(R.id.tvPLayerMark);
        tvAIMark = findViewById(R.id.tvAIMark);
        btnRestart = findViewById(R.id.btnRestart);


        gameImages[0] = findViewById(R.id.imgGame11);
        gameImages[1] = findViewById(R.id.imgGame12);
        gameImages[2] = findViewById(R.id.imgGame13);
        gameImages[3] = findViewById(R.id.imgGame14);
        gameImages[4] = findViewById(R.id.imgGame21);
        gameImages[5] = findViewById(R.id.imgGame22);
        gameImages[6] = findViewById(R.id.imgGame23);
        gameImages[7] = findViewById(R.id.imgGame24);
        gameImages[8] = findViewById(R.id.imgGame31);
        gameImages[9] = findViewById(R.id.imgGame32);
        gameImages[10] = findViewById(R.id.imgGame33);
        gameImages[11] = findViewById(R.id.imgGame34);
        gameImages[12] = findViewById(R.id.imgGame41);
        gameImages[13] = findViewById(R.id.imgGame42);
        gameImages[14] = findViewById(R.id.imgGame43);
        gameImages[15] = findViewById(R.id.imgGame44);
        gameImages[16] = findViewById(R.id.imgGame51);
        gameImages[17] = findViewById(R.id.imgGame52);
        gameImages[18] = findViewById(R.id.imgGame53);
        gameImages[19] = findViewById(R.id.imgGame54);


        // Set onClickListeners for game image buttons
        for (int i = 0; i < gameImages.length; i++) {
            final int index = i;
            gameImages[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPlayerTurn) {
                        if (firstButtonIndex == -1) {
                            firstButtonIndex = index;
                            gameImages[index].setImageResource(getImageResource(buttonValues[index]));
                        } else if (secondButtonIndex == -1 && firstButtonIndex != index) {
                            secondButtonIndex = index;
                            gameImages[index].setImageResource(getImageResource(buttonValues[index]));
                            moveCount++;
                            tvMovesCount.setText("Moves: " + moveCount);
                            checkMatch();
                        }
                    }
                }
            });
        }

        // Set onClickListener for restart button
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupGame();
            }
        });

        setupGame();
    }


    // Setup the game and initialize game variables
    private void setupGame() {
        Random random = new Random();
        int count = 0;
        moveCount = 0;
        playerScore = 0;
        aiScore = 0;
        aiMemory.clear();

        tvPLayerMark.setText("PLayer Mark: 0");
        tvAIMark.setText("AI Mark: 0");

        tvMovesCount.setText("Moves: " + moveCount);
        btnRestart.setVisibility(View.INVISIBLE);
        buttonValues = new int[20];

        // Randomly assign image numbers to buttons
        while (count < 20) {
            int randomNum = random.nextInt(10) + 1;
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

        // Set all game image buttons to question mark
        for (int i = 0; i < gameImages.length; i++) {
            gameImages[i].setImageResource(R.drawable.gmaequestionmark); // Set the ImageView to transparent
            gameImages[i].setVisibility(View.VISIBLE);
        }

        isPlayerTurn = true;
        updateTurnIndicator();
        if (!isPlayerTurn) {
            aiTurn();
        }
    }

    // Check if the two selected cards match
    private void checkMatch() {
        if (firstButtonIndex != -1 && secondButtonIndex != -1) {
            final boolean isMatch = buttonValues[firstButtonIndex] == buttonValues[secondButtonIndex];

            gameImages[firstButtonIndex].setImageResource(getImageResource(buttonValues[firstButtonIndex]));
            gameImages[secondButtonIndex].setImageResource(getImageResource(buttonValues[secondButtonIndex]));

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isMatch) {
                        gameImages[firstButtonIndex].setVisibility(View.INVISIBLE);
                        gameImages[secondButtonIndex].setVisibility(View.INVISIBLE);
                        if (isPlayerTurn) {
                            playerScore++;
                            tvPLayerMark.setText("Player Score: " + playerScore);
                        } else {
                            aiScore++;
                            tvAIMark.setText("AI Score: " + aiScore);
                        }
                    } else {
                        gameImages[firstButtonIndex].setImageResource(R.drawable.gmaequestionmark);
                        gameImages[secondButtonIndex].setImageResource(R.drawable.gmaequestionmark);
                    }
                    // Update AI memory with player's flipped cards
                    if (isPlayerTurn) {
                        aiMemory.put(firstButtonIndex, buttonValues[firstButtonIndex]);
                        aiMemory.put(secondButtonIndex, buttonValues[secondButtonIndex]);
                    }
                    firstButtonIndex = -1;
                    secondButtonIndex = -1;

                    // Switch turns
                    isPlayerTurn = !isPlayerTurn;
                    updateTurnIndicator();

                    if (!isPlayerTurn) {
                        aiTurn();
                    }

                    // Check for game over after the delay
                    checkGameOver();
                }
            }, 1000);
        }
    }

    // Update UI to indicate whose turn it is
    private void updateTurnIndicator() {
        if (isPlayerTurn) {
            tvWhoTurnPlayerOrAI.setText("Player's Turn");
        } else {
            tvWhoTurnPlayerOrAI.setText("AI's Turn");
        }
    }

    // AI takes its turn
    private void aiTurn() {
        // Check if the game is over, if so, return
        if (checkGameOver()) {
            return;
        }
        // Handler to run code with a delay
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                // Set the probability of using memory, for example, 90%
                double memoryProbability = 0.9;
                boolean useMemory = random.nextDouble() < memoryProbability;


                // If AI decides to use memory and has at least two cards in memory
                if (useMemory && aiMemory.size() > 1) {
                    List<Integer> keys = new ArrayList<>(aiMemory.keySet());
                    do {
                        // Pick a random card index from AI memory
                        firstButtonIndex = keys.get(random.nextInt(keys.size()));
                    } while (gameImages[firstButtonIndex].getVisibility() == View.INVISIBLE);
                } else {
                    do {
                        // If AI doesn't use memory, pick a random card index
                        firstButtonIndex = random.nextInt(gameImages.length);
                    } while (gameImages[firstButtonIndex].getVisibility() == View.INVISIBLE);
                }
                // Set the image of the first card
                gameImages[firstButtonIndex].setImageResource(getImageResource(buttonValues[firstButtonIndex]));

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // If AI used memory, try to find a match in memory
                        if (useMemory) {
                            for (Integer key : aiMemory.keySet()) {
                                if (key != firstButtonIndex && aiMemory.get(key).equals(buttonValues[firstButtonIndex])) {
                                    secondButtonIndex = key;
                                    break;
                                }
                            }
                        }
                        // If AI didn't find a match in memory, pick a random second card index
                        if (secondButtonIndex == -1) {
                            do {
                                secondButtonIndex = random.nextInt(gameImages.length);
                            } while (gameImages[secondButtonIndex].getVisibility() == View.INVISIBLE || firstButtonIndex == secondButtonIndex);
                        }
                        // Set the image of the second card
                        gameImages[secondButtonIndex].setImageResource(getImageResource(buttonValues[secondButtonIndex]));
                        // Increment move count and check for a match
                        moveCount++;
                        tvMovesCount.setText("Moves: " + moveCount);
                        checkMatch();

                        /// Store the viewed cards in AI memory
                        aiMemory.put(firstButtonIndex, buttonValues[firstButtonIndex]);
                        aiMemory.put(secondButtonIndex, buttonValues[secondButtonIndex]);
                    }
                }, 1000);
            }
        }, 1000);
    }



    // Check if the game is over and display result
    private boolean checkGameOver() {
        boolean isGameOver = true;
        for (ImageView gameImage : gameImages) {
            if (gameImage.getVisibility() == View.VISIBLE) {
                isGameOver = false;
                break;
            }
        }
        if (isGameOver) {
            String winner;
            if (playerScore > aiScore) {
                winner = "Player";
                tvMovesCount.setText("Player won" );
                btnRestart.setVisibility(View.VISIBLE);
            } else if (aiScore > playerScore) {
                winner = "AI";
                tvMovesCount.setText("AI won" );
                btnRestart.setVisibility(View.VISIBLE);
            } else {
                tvMovesCount.setText("No one won" );
                btnRestart.setVisibility(View.VISIBLE);
                winner = "Draw";
            }
        }
        return isGameOver;
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
            case 5:
                return R.drawable.image5;
            case 6:
                return R.drawable.image6;
            case 7:
                return R.drawable.image7;
            case 8:
                return R.drawable.image8;
            case 9:
                return R.drawable.image9;
            case 10:
                return R.drawable.image10;


            default:
                return  R.drawable.medal1;
        }
    }

}