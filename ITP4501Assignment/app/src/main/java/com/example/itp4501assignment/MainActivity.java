
/*


Name:Chan Chi Yip
Student ID:220134191
Class:IT114105-1A
Module:ITP4501


*/

package com.example.itp4501assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity  {

    // Called when the activity is starting
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    // Inflate the options menu
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_test,menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Handle menu item selection
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuitem_exits) {
            finishAffinity(); // This closes all activities in the task and stops
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Start the SinglePlayerModeActivity when the corresponding button is clicked
    public void openSinglePlayermModeActivity(View view) {
        Intent intent = new Intent(MainActivity.this, SinglePlayermModeActivity.class);
        startActivity(intent);
    }

    // Start the PlayWithAIModeActivity when the corresponding button is clicked
    public void openPlayWithAIActivity(View view) {
        Intent intent = new Intent(MainActivity.this, PlayWithAIModeActivity.class);
        startActivity(intent);
    }

    // Start the GameRankingActivity when the corresponding button is clicked
    public void openGameRankingActivity(View view) {
        Intent intent = new Intent(MainActivity.this, GameRankingActivity.class);
        startActivity(intent);
    }


    // Start the YourRecordsActivity when the corresponding button is clicked
    public void openYourRecordsActivity(View view) {
        Intent intent = new Intent(MainActivity.this, YourRecordsActivity.class);
        startActivity(intent);
    }



}