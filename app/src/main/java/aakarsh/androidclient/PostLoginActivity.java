package aakarsh.androidclient;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.INVISIBLE;

public class PostLoginActivity extends AppCompatActivity {

    public static String idOfTodo;
    public static String token;
    public static String email;
    public static String url;
    FloatingActionButton fab;
    FloatingActionButton leader;
    FloatingActionButton addTodosButton;
    public static String groupID;
    ArrayList<String> todosList = new ArrayList<>();
    ListView lview;
    //ArrayAdapter listAdapter;
    ArrayAdapter<String> listAdapter;
    HashMap<String, String> todoDetail = new HashMap<>(); //(title, id)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login);
        Intent loginActivity = getIntent();
        leader= (FloatingActionButton) findViewById(R.id.leader);
        token = loginActivity.getStringExtra("token");
        email = loginActivity.getStringExtra("email");
        url = loginActivity.getStringExtra("url");
        lview = (ListView) findViewById(R.id.lview);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        addTodosButton = (FloatingActionButton) findViewById(R.id.fabForTodos);
        addTodosButton.setVisibility(INVISIBLE);
        addTodosButton.setOnClickListener(addTodosFabListener);
        leader.setOnClickListener(leaderListener);
        fab.setOnClickListener(fabListener);
        fab.setVisibility(INVISIBLE);
        checkIfUserIsInAGroup();

    }

    public void checkIfUserIsInAGroup() {
        RequestQueue queue = Volley.newRequestQueue(this);

// Request a string response from the provided URL.
        StringRequest jRequest = new StringRequest(Request.Method.GET, url + "/groups",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);

                        if (response.equals("null")) {
                            System.out.println("null values in groups array   " + response);
                            fab.setVisibility(View.VISIBLE);
                        } else {
                            addTodosButton.setVisibility(View.VISIBLE);
                            setInfo(response);

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString()); //Error exists
                fab.setVisibility(View.VISIBLE);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("x-auth", token);
                return headers;
            }
        };
// Add the request to the RequestQueue.
        queue.add(jRequest);

    }

    public View.OnClickListener fabListener = new View.OnClickListener() {
        public void onClick(View view) {
            Intent createOrJoin = new Intent(getApplicationContext(), CreateOrJoinActivity.class);
            createOrJoin.putExtra("token", token);
            createOrJoin.putExtra("url", url);
            // createOrJoin.putExtra("token", );
            startActivity(createOrJoin);


        }
    };

    public View.OnClickListener leaderListener = new View.OnClickListener() {
        public void onClick(View view) {
            Intent leaderboard = new Intent(getApplicationContext(), LeaderBoardActivity.class);
            startActivity(leaderboard);


        }
    };


    public View.OnClickListener addTodosFabListener = new View.OnClickListener() {
        public void onClick(View view) {
            createDialog();
            //createTodos("from and");

        }
    };

    public void setInfo(String response) {
        try {
            JSONObject responseObject = new JSONObject(response);
            groupID = responseObject.getString("_id").toString();
            getTodos();


        } catch (Exception e) {

        }

    }

    private void createDialog(){
        LayoutInflater inflater = LayoutInflater.from(PostLoginActivity.this);
        View subView = inflater.inflate(R.layout.dialog_layout, null);
        final EditText subEditText = (EditText)subView.findViewById(R.id.dialogEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Todo");
        builder.setMessage("Name the todo/short desc.");
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String todo = subEditText.getText().toString();
                createTodos(todo);


            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }
    public void getTodos() {
        RequestQueue queue = Volley.newRequestQueue(this);

// Request a string response from the provided URL.
        StringRequest jRequest = new StringRequest(Request.Method.GET, url + "/todos",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        //check if null
                        if (response.equals("[]")) {
                            System.out.println("Empty array of todos");
                        } else {
                            try {
                                JSONArray arrayOfTodos = new JSONArray(response);
                                processTodos(arrayOfTodos);
                            } catch (Exception e) {

                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString()); //Error exists
                fab.setVisibility(View.VISIBLE);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("x-auth", token);
                return headers;
            }
        };
// Add the request to the RequestQueue.
        queue.add(jRequest);


    }

    public void processTodos(JSONArray todosArray) {
        for (int i = 0; i < todosArray.length(); i++) {
            try {
                JSONObject objectAtIndex = todosArray.getJSONObject(i);
              //  System.out.println(objectAtIndex);
               // System.out.println(objectAtIndex.get("text"));  WORKS
                todoDetail.put(objectAtIndex.get("text").toString(), objectAtIndex.get("_id").toString());
                todosList.add(objectAtIndex.get("text").toString());
                System.out.println(todosList.get(0));

            } catch (JSONException e) {

            }


        }

        createList(todosList, todoDetail);


    }

    public void createTodos(String textForTodo) {

        JSONObject todoInfo = new JSONObject();
        try {
            todoInfo.put("text", textForTodo);
        } catch (Exception e) {

        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest createGroupRequest = new JsonObjectRequest(url + "/todos", todoInfo, ///JS (object goes right after url)
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Successfully created
                        finish();
                        startActivity(getIntent());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                System.out.println("Error was   " + error.getMessage().toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("x-auth", token);
                return headers;
            }
        };

        queue.add(createGroupRequest);


    }

    public void createList(final ArrayList<String> listTodos, final HashMap<String, String> todoDetail) {
       System.out.println(listTodos);
        listAdapter = new ArrayAdapter<String>(PostLoginActivity.this, android.R.layout.simple_list_item_1, listTodos);
        lview.setAdapter(listAdapter);
        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = listTodos.get(position);
                 idOfTodo = todoDetail.get(key);

                CompleteTodoActivity CompleteTodoAct = new CompleteTodoActivity();
                /*
                    Intent completeTodoIntent = new Intent(getApplicationContext(), CompleteTodoActivity.class);
                    completeTodoIntent.putExtra("key", key);
                    completeTodoIntent.putExtra("idOfTodo", idOfTodo);
                    completeTodoIntent.putExtra("token", token);
*/

                //CompleteTodoAct.token = token;

                Intent completeTodoIntent = new Intent(getApplicationContext(), CompleteTodoActivity.class);
                System.out.println(idOfTodo + "      " + key);
                startActivity(completeTodoIntent);
                finish();


            }

        });


    }


}
