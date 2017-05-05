package aakarsh.androidclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static aakarsh.androidclient.PostLoginActivity.idOfTodo;
import static aakarsh.androidclient.PostLoginActivity.token;
import static aakarsh.androidclient.PostLoginActivity.url;

public class CompleteTodoActivity extends AppCompatActivity {


    Button complete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_todo);
        complete = (Button) findViewById(R.id.completeButton);
        complete.setOnClickListener(completeListener);
        //Intent intent = getIntent();
      //  token = intent.getStringExtra("token");
       // idOfTodo = intent.getStringExtra("idOfTodo");
        //nameOfTodo = intent.getStringExtra("key");
    System.out.println(token + "  " + idOfTodo + "   ");
    }

    public View.OnClickListener completeListener = new View.OnClickListener() {
        public void onClick(View view) {
            completeTodo();

        }
    };


    public void completeTodo(){
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject obj = new JSONObject();
        try{
        obj.put("a","b");} catch (Exception e){

        }

        JsonObjectRequest createGroupRequest = new JsonObjectRequest(url + "/todos/donetodos/" + idOfTodo,obj, ///JS (object goes right after url)
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //ArrayList
                        System.out.println("Todo completed");
                        System.out.println("response    " + response);
                        Intent todos = new Intent(getApplicationContext(), PostLoginActivity.class);
                        startActivity(todos);
                        finish();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
//                System.out.println("Error was   " + error.getMessage().toString());
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


}
