package aakarsh.androidclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class CreateOrJoinActivity extends AppCompatActivity {

    EditText infoField;
    Button join, create;
    public static String detailOfGroup;
    public static String url;
    public static String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_join);
        join = (Button) findViewById(R.id.join);
        create = (Button) findViewById(R.id.create);
        infoField = (EditText) findViewById(R.id.infoField);
        Intent postLogin = getIntent();
        url = postLogin.getStringExtra("url");
        token = postLogin.getStringExtra("token");
        create.setOnClickListener(createListener);
        join.setOnClickListener(joinListener);
    }

    public View.OnClickListener createListener = new View.OnClickListener() {
        public void onClick(View view) {
            detailOfGroup = infoField.getText().toString();
            createGroup();

        }
    };

    public View.OnClickListener joinListener = new View.OnClickListener() {
        public void onClick(View view) {
            joinGroup();

        }
    };


    public void createGroup(){

        JSONObject groupInfo = new JSONObject();
        try {
            groupInfo.put("name",detailOfGroup);
        }catch (Exception e){

        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest createGroupRequest = new JsonObjectRequest(url + "/groups", groupInfo, ///JS (object goes right after url)
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        Intent backtoPost = new Intent(getApplicationContext(), PostLoginActivity.class);
                        backtoPost.putExtra("url", url);
                        backtoPost.putExtra("token", token);
                        startActivity(backtoPost);
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                System.out.println("Error was   " + error.getMessage().toString());
            }
        }){ @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("x-auth", token);
            return headers;
        }};

        queue.add(createGroupRequest);

    }

    public void joinGroup(){

        JSONObject groupInfo = new JSONObject();
        try {
            groupInfo.put("name", infoField.toString());
        }catch (Exception e){

        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest createGroupRequest = new JsonObjectRequest(url + "/groups/join" + infoField.toString(), groupInfo, ///JS (object goes right after url)
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        Intent backtoPost = new Intent(getApplicationContext(), PostLoginActivity.class);
                        startActivity(backtoPost);
                        //SUCCESS
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                System.out.println("Error was   " + error.getMessage().toString());
            }
        }){
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
