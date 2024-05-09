package com.example.itp4501assignment;

import androidx.appcompat.app.AppCompatActivity;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GameRankingActivity extends AppCompatActivity {
    TextView textViewUrl, textViewResult;
    ListView lvGameRanking;
    ListView lvGameRanking2;
    ListView lvGameRanking3;

    ListView lvGameRankingOther;

    DownloadTask task = null;
    String[] items;
    String[] itemsFirstPlace;
    String[] itemsSecondPlace;
    String[] itemsThirdPlace;
    String[]itemsOtherPlaces;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_ranking);
        lvGameRanking = findViewById(R.id.lvGameRanking);
        lvGameRanking2 = findViewById(R.id.lvGameRanking2);
        lvGameRanking3 = findViewById(R.id.lvGameRanking3);
        lvGameRankingOther = findViewById(R.id.lvGameRankingOther);
        task = new DownloadTask();
        String url = "https://ranking-mobileasignment-wlicpnigvf.cn-hongkong.fcapp.run";
        task.execute(url);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_test,menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Handle menu item selections
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuitem_exits) {
            finishAffinity(); // This closes all activities in the task and stops
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    // AsyncTask to download game ranking data from a remote server
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... values) {
            InputStream inputStream = null;
            String result = "";
            URL url = null;
            try {
                url = new URL(values[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();

                inputStream = con.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while ((line = bufferedReader.readLine()) != null)
                    result += line;
                inputStream.close();

                JSONArray gameRankingData = new JSONArray(result);

                List<JSONObject> gotJsonData = new ArrayList<>();
                for (int i = 0; i < gameRankingData.length(); i++) {
                    gotJsonData.add(gameRankingData.getJSONObject(i));
                }

                // Sort the gotJsonData by "Moves" key in ascending order
                Collections.sort(gotJsonData, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject a, JSONObject b) {
                        int valA = 0;
                        try {
                            valA = a.getInt("Moves");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        int valB = 0;
                        try {
                            valB = b.getInt("Moves");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        return Integer.compare(valA, valB);
                    }
                });

                // Populate the items arrays
                itemsFirstPlace = new String[1];
                itemsSecondPlace = new String[1];
                itemsThirdPlace = new String[1];
                itemsOtherPlaces = new String[gotJsonData.size() - 3];

                for (int i = 0; i < gotJsonData.size(); i++) {
                    JSONObject jsonObject = gotJsonData.get(i);
                    String name = jsonObject.getString("Name");
                    int moves = jsonObject.getInt("Moves");

                    if (i == 0) {
                        itemsFirstPlace[0] = "Rank " + (i + 1) + ", " + name + ", " + moves + " moves";
                    } else if (i == 1) {
                        itemsSecondPlace[0] = "Rank " + (i + 1) + ", " + name + ", " + moves + " moves";
                    } else if (i == 2) {
                        itemsThirdPlace[0] = "Rank " + (i + 1) + ", " + name + ", " + moves + " moves";
                    } else {
                        itemsOtherPlaces[i - 3] = "Rank " + (i + 1) + ", " + name + ", " + moves + " moves";
                    }
                }
            } catch (Exception ex) {
                result = ex.getMessage();
            }
            return result;
        }

        // Update the UI with the downloaded data
        @Override
        protected void onPostExecute(String result) {
            ListView lvGameRanking2 = findViewById(R.id.lvGameRanking2);
            ListView lvGameRanking3 = findViewById(R.id.lvGameRanking3);
            ListView lvGameRankingOther = findViewById(R.id.lvGameRankingOther);
            // If the data is available, set up the ArrayAdapter for each ListView
            if (itemsFirstPlace != null && itemsSecondPlace != null && itemsThirdPlace != null && itemsOtherPlaces != null) {
                ArrayAdapter<String> adapterFirstPlace = new ArrayAdapter<>(GameRankingActivity.this, android.R.layout.simple_list_item_1, itemsFirstPlace);
                ArrayAdapter<String> adapterSecondPlace = new ArrayAdapter<>(GameRankingActivity.this, android.R.layout.simple_list_item_1, itemsSecondPlace);
                ArrayAdapter<String> adapterThirdPlace = new ArrayAdapter<>(GameRankingActivity.this, android.R.layout.simple_list_item_1, itemsThirdPlace);
                ArrayAdapter<String> adapterOtherPlaces = new ArrayAdapter<>(GameRankingActivity.this, android.R.layout.simple_list_item_1, itemsOtherPlaces);

                lvGameRanking.setAdapter(adapterFirstPlace);
                lvGameRanking2.setAdapter(adapterSecondPlace);
                lvGameRanking3.setAdapter(adapterThirdPlace);
                lvGameRankingOther.setAdapter(adapterOtherPlaces);
            }
        }
    }


}


