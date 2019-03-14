package com.hadi.incredibleinfo.UserDetails;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hadi.incredibleinfo.EduDetailsClasses.EducationDetails;
import com.hadi.incredibleinfo.EduDetailsClasses.EducationDetailsData;
import com.hadi.incredibleinfo.HomeActivity;
import com.hadi.incredibleinfo.R;
import com.hadi.incredibleinfo.remote.APIUTils;
import com.hadi.incredibleinfo.remote.UserService;

import java.io.File;
import java.security.PrivateKey;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hadi.incredibleinfo.MainActivity.MY_PREF;

public class EducationDetailsActivity extends AppCompatActivity {

    final private String imageUri = "content://media/internal/images/media";
    final private String imageUrl = "http://139.59.65.145:9090/user/educationdetail/certificate/";
    private Button savebutton;
    private String organization,degree,location,startyear,endyear,picturepath;
    UserService userService;
    private int userId;
    EditText orgedittext,degreeedittext,locationedittext,startyearedittext,endyearedittext;
    private ImageView certiPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_details);

        orgedittext=findViewById(R.id.organization_edit_text);
        degreeedittext=findViewById(R.id.degree_edit_text);
        locationedittext=findViewById(R.id.location_edit_text);
        startyearedittext=findViewById(R.id.start_year_edit);
        endyearedittext=findViewById(R.id.end_year_edit);
        savebutton=findViewById(R.id.save_button);


        certiPic =findViewById(R.id.certi);
        certiPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse(imageUri));
                startActivityForResult(intent, 1);
            }
        });

        SharedPreferences prefs = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        userId = prefs.getInt("id",0);

        userService = APIUTils.getUserService();

        final String isUpdate = getIntent().getStringExtra("update");

        if (isUpdate == null)
            getSupportActionBar().setTitle("Set Education Details");
        else {
            getSupportActionBar().setTitle("Edit Education Details");
            getEducationDetails();
        }

        savebutton=findViewById(R.id.save_button);
        try{
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                organization = orgedittext.getText().toString().trim();
                degree = degreeedittext.getText().toString().trim();
                location = locationedittext.getText().toString().trim();
                startyear = startyearedittext.getText().toString().trim();
                endyear = endyearedittext.getText().toString().trim();

                EducationDetails educationDetails = new EducationDetails(startyear, degree, organization, location, endyear);

                if (isUpdate == null)
                    setEducationDetails(educationDetails);
                else {
                    updateEducationDetails(educationDetails);
                }
                setCertiImg();
            }
        });
    }
        catch(Exception e){
            e.printStackTrace();
        }
    }
        private void setCertiImg(){

            File file = new File(picturepath);
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("photo",file.getName(),requestBody);
            RequestBody userid = RequestBody.create(MultipartBody.FORM, String.valueOf(userId));

            Call<ResponseBody> call = userService.setCertificates(part,userid);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(EducationDetailsActivity.this, "Certificate set successfully", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(EducationDetailsActivity.this, "Update Personal details Failed: "+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    public void getEducationDetails()
    {
        Call<EducationDetailsData> call = userService.getEducationalDetails(userId);
        call.enqueue(new Callback<EducationDetailsData>() {
            @Override
            public void onResponse(Call<EducationDetailsData> call, Response<EducationDetailsData> response) {
                if(response.body() != null) {
                    orgedittext.setText(response.body().getData().getOrganisation());
                    degreeedittext.setText(response.body().getData().getDegree());
                    locationedittext.setText(response.body().getData().getLocation());
                    startyearedittext.setText(response.body().getData().getStart_year());
                    endyearedittext.setText(response.body().getData().getEnd_year());
                } else {
                    Toast.makeText(EducationDetailsActivity.this, "Professional Details Response Empty", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<EducationDetailsData> call, Throwable t) {
                Toast.makeText(EducationDetailsActivity.this, "Response Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setEducationDetails(EducationDetails educationDetails)
    {
        Call<EducationDetailsData> call = userService.setEducationalDetails(userId, educationDetails);
        call.enqueue(new Callback<EducationDetailsData>() {
            @Override
            public void onResponse(Call<EducationDetailsData> call, Response<EducationDetailsData> response) {

                Intent intent = new Intent(EducationDetailsActivity.this, HomeActivity.class);
                intent.putExtra("id", userId);
                finish();
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<EducationDetailsData> call, Throwable t) {
                Toast.makeText(EducationDetailsActivity.this, "Set Education details Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateEducationDetails(final EducationDetails educationDetails)
    {
        Call<EducationDetailsData> call = userService.updateEducationalDetails(userId, educationDetails);
        call.enqueue(new Callback<EducationDetailsData>() {
            @Override
            public void onResponse(Call<EducationDetailsData> call, Response<EducationDetailsData> response) {
                Toast.makeText(EducationDetailsActivity.this, "Education Details Updated", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EducationDetailsActivity.this, HomeActivity.class);
                intent.putExtra("id", userId);
                finish();
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<EducationDetailsData> call, Throwable t) {
                Toast.makeText(EducationDetailsActivity.this, "Update Education details Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getRealPathFromURIPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(),uri,projection,null,null,null);
        Cursor cursor = loader.loadInBackground();
        int column_idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_idx);
        cursor.close();
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 1)
        {
            Uri uri = data.getData();
            certiPic.setImageURI(uri);

            picturepath = getRealPathFromURIPath(uri);
        }
    }
}
