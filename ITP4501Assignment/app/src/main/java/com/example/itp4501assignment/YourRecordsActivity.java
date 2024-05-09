package com.example.itp4501assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class YourRecordsActivity extends AppCompatActivity implements OnItemSelectedListener {
    Cursor cursor = null;
    SQLiteDatabase MemberDB;
    ListView listViewYourRecords;
    Spinner spinnerType, spinnerOrder;
    int firsttime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_records);

        spinnerType = findViewById(R.id.psType);
        spinnerOrder = findViewById(R.id.psOrder);

        // Set up the Spinner listeners
        spinnerType.setOnItemSelectedListener(this);
        spinnerOrder.setOnItemSelectedListener(this);

        listViewYourRecords = findViewById(R.id.listViewYourRecords);
        try {
        // Create a database if it does not exist
            MemberDB = SQLiteDatabase.openDatabase("/data/data/com.example.itp4501assignment/MemberDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);

            String sql = "CREATE TABLE IF NOT EXISTS GamesLog  (gameID INTEGER PRIMARY KEY AUTOINCREMENT, playDate Date, playTime Time, moves int)  ;";
            MemberDB.execSQL(sql);

        } catch (SQLiteException e) {

        }finally{
            MemberDB.close();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_test, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuitem_exits) {
            finishAffinity(); // This closes all activities in the task and stops
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Handle Spinner item selections
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedType = spinnerType.getSelectedItem().toString().toLowerCase();

        String selectedOrder = spinnerOrder.getSelectedItem().toString().toUpperCase();
        if(selectedType.equals("time")){
            selectedType = "gameID";
        }
        System.out.println(selectedType);
        System.out.println(selectedOrder);
        if(selectedType.equals("Moves")){
            selectedType = "moves";
        }

        if(firsttime== 1){
            selectedOrder = "DESC";
        }
        firsttime++;
        System.out.println(selectedOrder);

        System.out.println(selectedType);

        // Display the records based on the selected type and order
        showYourRecords(selectedType, selectedOrder);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }



    // Method to display records in the ListView based on the selected type and order
    private void showYourRecords(String type, String order) {
        ArrayList<String> recordsList = new ArrayList<>();
        try {
            // Reopen the database if it's not open
            if (MemberDB == null || !MemberDB.isOpen()) {
                MemberDB = SQLiteDatabase.openDatabase("/data/data/com.example.itp4501assignment/MemberDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
            }
            String query = "SELECT * FROM GamesLog ORDER BY " + type + " " + order;
            cursor = MemberDB.rawQuery(query, null);

// Date and time formatters
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat inputTimeFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat outputTimeFormat = new SimpleDateFormat("HH:mm:ss");

            // Iterate through the cursor to get records and add them to the records list
            while (cursor.moveToNext()) {
                int gameIDIndex = cursor.getColumnIndex("gameID");
                int playDateIndex = cursor.getColumnIndex("playDate");
                int playTimeIndex = cursor.getColumnIndex("playTime");
                int movesIndex = cursor.getColumnIndex("moves");

                if (gameIDIndex >= 0 && playDateIndex >= 0 && playTimeIndex >= 0 && movesIndex >= 0) {
                    int gameID = cursor.getInt(gameIDIndex);
                    String playDate = cursor.getString(playDateIndex);
                    String playTime = cursor.getString(playTimeIndex);
                    int moves = cursor.getInt(movesIndex);
                    Date date = inputDateFormat.parse(playDate);
                    Date time = inputTimeFormat.parse(playTime);
                    String formattedDateTime = outputDateFormat.format(date) + ", " + outputTimeFormat.format(time);

                    recordsList.add(formattedDateTime + ",  " + moves + " moves");
                }
            }
        } catch (SQLiteException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (MemberDB != null && MemberDB.isOpen()) {
                MemberDB.close();
            }
        }

        // Set the ArrayAdapter to the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recordsList);
        listViewYourRecords.setAdapter(adapter);
    }
}