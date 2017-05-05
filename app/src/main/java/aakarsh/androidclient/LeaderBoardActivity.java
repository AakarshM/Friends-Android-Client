package aakarsh.androidclient;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static aakarsh.androidclient.CreateOrJoinActivity.token;
import static aakarsh.androidclient.PostLoginActivity.idOfTodo;
import static aakarsh.androidclient.PostLoginActivity.url;

public class LeaderBoardActivity extends AppCompatActivity {

    ListView lview;
    LinkedHashMap<String, String> map = new LinkedHashMap<>();
    ArrayList<String> names = new ArrayList<>();
    ArrayAdapter listAdapter;
    String token1 = Constants.token;
    private static JSONArray arrayOfScores;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        lview = (ListView) findViewById(R.id.lview);
        getBoard();
    }

    public void getBoard() {
        RequestQueue queue = Volley.newRequestQueue(this);

// Request a string response from the provided URL.
        StringRequest jRequest = new StringRequest(Request.Method.GET, url + "/groups/scores",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        try {
                            arrayOfScores = new JSONArray(response);
                            processScores(arrayOfScores);
                        } catch (JSONException e){

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString()); //Error exists


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("x-auth", token1);
                return headers;
            }
        };
// Add the request to the RequestQueue.
        queue.add(jRequest);

    }

    public void processScores(JSONArray arrayOfScores){
        for(int i = 0; i < arrayOfScores.length(); i++){
            try {
                JSONObject obj = arrayOfScores.getJSONObject(i);
                map.put(obj.getString("email"), obj.getString("victories"));
                names.add(obj.getString("email"));

            } catch (Exception e){

            }


        }
        createListView();


    }

    public void createListView(){
        listAdapter = new ArrayAdapter<String>(LeaderBoardActivity.this, android.R.layout.simple_list_item_1, names);
        lview.setAdapter(listAdapter);
        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = names.get(position);
                String victories = map.get(key);
                dialog(key,victories);

            }

        });


    }

    public void dialog(String email, String victories){
        AlertDialog alertDialog = new AlertDialog.Builder(LeaderBoardActivity.this).create();
        alertDialog.setTitle("User " + email + "'s statistics" );
        alertDialog.setMessage("Victories: " + victories);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}

