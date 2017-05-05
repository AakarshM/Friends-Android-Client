package aakarsh.androidclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button login, signup;
    EditText email, pass, displayField;
    ProgressBar bar;
    String emailID, passID, name;
    String url = "http://80b15a94.ngrok.io"; //base url
    public static String authToken;
    public static String emailOfUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = (EditText)findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        signup = (Button)findViewById(R.id.signup);
        login.setOnClickListener(loginListener);
        displayField = (EditText)findViewById(R.id.displayField);
        signup.setOnClickListener(signupListener);
        bar = (ProgressBar) findViewById(R.id.bar);
        bar.setVisibility(View.INVISIBLE);
    }

    public View.OnClickListener loginListener = new View.OnClickListener() {
        public void onClick (View view){

            logIn(email.getText().toString(), pass.getText().toString());

        }};
    public View.OnClickListener signupListener = new View.OnClickListener() {
        public void onClick (View view){
            emailID = email.getText().toString();
            passID = pass.getText().toString();
            name = displayField.getText().toString();
            System.out.println(emailID + passID + name);
            signUp(emailID, passID);



        }};

    String x;

    public void signUp(String email, String pass){
        JSONObject signUpInfo = new JSONObject();
        try {
            signUpInfo.put("email", emailID);
            signUpInfo.put("password", passID);
        }catch (Exception e){

        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest signUpReq = new JsonObjectRequest(url + "/users", signUpInfo, ///JS (object goes right after url)
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        try {
                            emailOfUser = response.getString("email").toString();
                            onPost();
                        } catch (Exception ex){
                            System.out.println(ex.toString());
                        }
                        //SUCCESS

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                authToken = response.headers.get("x-auth");
                System.out.println(authToken.toString());


                return super.parseNetworkResponse(response);
            }};

        queue.add(signUpReq); //SIGN UP


    }

    public void logIn(String email, String pass){

        JSONObject logInInfo = new JSONObject();
        try {
            logInInfo.put("email", email);
            logInInfo.put("password", pass);
        }catch (Exception e){

        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest logInReq = new JsonObjectRequest(url + "/users/login", logInInfo, ///JS (object goes right after url)
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        try {
                            emailOfUser = response.getString("email").toString();
                            onPost();
                        } catch (Exception ex){
                            System.out.println(ex.toString());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                authToken = response.headers.get("x-auth");
                Constants.token = authToken;
                System.out.println(authToken.toString());
                return super.parseNetworkResponse(response);
            }};

        queue.add(logInReq); //LOG IN


    }

    public void onPost(){
        Intent PostLogin = new Intent(getApplicationContext(), PostLoginActivity.class);
        PostLogin.putExtra("email",emailOfUser);
        PostLogin.putExtra("token", authToken);
        PostLogin.putExtra("url", url);
        startActivity(PostLogin);

    }


}
