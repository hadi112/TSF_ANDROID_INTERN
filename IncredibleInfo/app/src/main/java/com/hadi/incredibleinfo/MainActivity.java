package com.hadi.incredibleinfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hadi.incredibleinfo.EduDetailsClasses.EducationDetails;
import com.hadi.incredibleinfo.LoginClasses.LoginSignupData;
import com.hadi.incredibleinfo.LoginClasses.ServerTest;
import com.hadi.incredibleinfo.LoginClasses.User;
import com.hadi.incredibleinfo.UserDetails.EducationDetailsActivity;
import com.hadi.incredibleinfo.UserDetails.PersonalDetailsActivity;
import com.hadi.incredibleinfo.remote.APIUTils;
import com.hadi.incredibleinfo.remote.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
   private String userEmail;
   private int userId;
   private String email,password;
   UserService userService;
   Button loginButton, signupButton;
   public static final String MY_PREF = "MyPreference";
   EditText emailEditText,passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //permissionManager = new PermissionManager() {};
        //permissionManager.checkAndRequestPermissions(this);

        loginButton = findViewById(R.id.buttonLogin);
        signupButton = findViewById(R.id.buttonSign);

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);

        userService = APIUTils.getUserService();

        serverTest();

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                email = emailEditText.getText().toString().trim();
                password = passwordEditText.getText().toString().trim();

                if(email != null && password != null){
                    User user =new User(email,password);
                    loginUser(user);
                }
                else{
                    Toast.makeText(MainActivity.this, "Empty Fields!!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = emailEditText.getText().toString().trim();
                password = passwordEditText.getText().toString().trim();


                if(email != null && password != null)
                {
                    User user = new User(email, password);
                    signUpUser(user, email);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Empty Fields!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        //permissionManager.checkResult(requestCode,permissions, grantResults);
    }

        private void loginUser(User user){
                Call<LoginSignupData> call = userService.getUser(user);
                call.enqueue(new Callback<LoginSignupData>() {
                    @Override
                    public void onResponse(Call<LoginSignupData> call, Response<LoginSignupData> response) {
                        if(response.body().getData() == null){
                            Toast.makeText(MainActivity.this, "No such user exists!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        userId = Integer.parseInt(response.body().getData().getId());
                        userEmail = response.body().getData().getEmail();

                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREF, MODE_PRIVATE).edit();
                        editor.putString("email", userEmail);
                        editor.putInt("id", userId);
                        editor.apply();

                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.putExtra("id", userId);

                        startActivity(intent);


                    }

                    @Override
                    public void onFailure(Call<LoginSignupData> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Login Failure!!: "+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        }

    public void signUpUser(final User user, final String email)
    {
        Call<LoginSignupData> call = userService.addUser(user);
        call.enqueue(new Callback<LoginSignupData>() {
            @Override
            public void onResponse(Call<LoginSignupData> call, Response<LoginSignupData> response) {
                userId = Integer.parseInt(response.body().getData().getId());
                userEmail = response.body().getData().getEmail();

                Toast.makeText(MainActivity.this, "Thanks for joining us.\nYour unique ID is: " + userId + ".\nPlease fill the details to continue", Toast.LENGTH_LONG).show();

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREF, MODE_PRIVATE).edit();
                editor.putString("email", userEmail);
                editor.putInt("id", userId);
                editor.apply();

                Intent intent = new Intent(MainActivity.this, PersonalDetailsActivity.class);
                intent.putExtra("id", userId);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<LoginSignupData> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Signup Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void serverTest() {
        Call<ServerTest> call = userService.getServerStatus();
        call.enqueue(new Callback<ServerTest>() {
            @Override
            public void onResponse(Call<ServerTest> call, Response<ServerTest> response) {

                Toast.makeText(MainActivity.this, "ServerStatus : " + response.body().getStatus(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ServerTest> call, Throwable t) {
                Toast.makeText(MainActivity.this, "ServerStatus : Down : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
