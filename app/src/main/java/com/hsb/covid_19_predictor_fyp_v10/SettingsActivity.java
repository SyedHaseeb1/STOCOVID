package com.hsb.covid_19_predictor_fyp_v10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    Switch darkthemeswitch,myalgoswitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        darkthemeswitch = findViewById(R.id.darkthemeswitch);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ConstraintLayout main_L;
        main_L=findViewById(R.id.main_L);
        myalgoswitch=findViewById(R.id.myalgoswitch);
        try {
            boolean theme = preferences.getBoolean("theme_dark", false);
            boolean myalgo = preferences.getBoolean("myalgo", false);
            Log.e("Theme",theme+"");
            if (theme) {
                boolean theme_dark=preferences.getBoolean("theme_dark",false);
                if (theme_dark){
                    main_L.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.theme_dark));

                }
                setTheme(R.style.DarkTheme);
                darkthemeswitch.setChecked(true);
            }
            if (myalgo){
                myalgoswitch.setChecked(true);
            }
        } catch (Exception e) {
            setTheme(R.style.Theme_AppCompat);

            e.printStackTrace();
        }

        myalgoswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                if (isChecked){
                    editor.putBoolean("myalgo", isChecked);
                    Toast.makeText(getApplicationContext(),"My Algo Enabled",Toast.LENGTH_SHORT).show();
                    editor.apply();
                }
                else {
                    editor.putBoolean("myalgo", isChecked);
                    Toast.makeText(getApplicationContext(),"My Algo Disabled",Toast.LENGTH_SHORT).show();
                    editor.apply();
                }
                finishAffinity();
                Intent intent=new Intent(SettingsActivity.this,MainActivity.class);
                startActivity(intent);

            }
        });
        darkthemeswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();

                if (isChecked) {
                    editor.putBoolean("theme_dark", isChecked);
                    Toast.makeText(getApplicationContext(),"Dark Theme Enabled",Toast.LENGTH_SHORT).show();
                    editor.apply();

                } else {
                    editor.putBoolean("theme_dark",isChecked);
                    Toast.makeText(getApplicationContext(),"Dark Theme Disabled",Toast.LENGTH_SHORT).show();
                    editor.apply();

                }
                finishAffinity();
                Intent intent=new Intent(SettingsActivity.this,MainActivity.class);
                startActivity(intent);

            }
        });
    }
}