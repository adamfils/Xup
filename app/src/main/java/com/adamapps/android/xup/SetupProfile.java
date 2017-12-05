package com.adamapps.android.xup;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SetupProfile extends AppCompatActivity {

    EditText userNameText;
    TextView tool_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        userNameText = (EditText) findViewById(R.id.profile_name);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Login");
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        toolbar.showOverflowMenu();
        tool_text = (TextView)findViewById(R.id.toolbar_text);
        tool_text.setText(R.string.app_name);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "font/Vincentia.otf");
        tool_text.setTypeface(custom_font);

    }

    //USED TO CHECK FOR INTERNET CONNECTIVITY BY PINGING GOOGLE SERVERS
    public boolean isConnected() throws InterruptedException, IOException
    {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec (command).waitFor() == 0);
    }

    //USED TO SAVE USER INFO THE THE USERINFO NODE IN THE DATABASE
    public void SaveName(View v) {
        final String name = userNameText.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please Enter A Display Name", Toast.LENGTH_SHORT).show();
            YoYo.with(Techniques.Shake).duration(500).playOn(v);
            return;
        }
        //COLLECTING DATE FROM DEVICE
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy/MM/dd ");
        String strDate = mdformat.format(calendar.getTime());

        //SAVING DATA IN A HASHMAP
        HashMap<String, Object> info = new HashMap<>();
        info.put("name", name);
        info.put("last_sign_in", strDate);

        try {
            if(!isConnected()){
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                YoYo.with(Techniques.Shake).duration(500).playOn(v);
                return;
            }
            if(isConnected()){
                YoYo.with(Techniques.RubberBand).duration(500).playOn(v);
            FirebaseDatabase.getInstance().getReference().child("UserData")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(info).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Intent intent = new Intent(SetupProfile.this,Home.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(SetupProfile.this, "Welcome "+name.toUpperCase(), Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SetupProfile.this, "Could Not Complete", Toast.LENGTH_SHORT).show();
                }
            });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
