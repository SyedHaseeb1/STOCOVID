package com.hsb.covid_19_predictor_fyp_v10;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hsb.covid_19_predictor_fyp_v10.databinding.ActivityMainBinding;
import com.hsb.covid_19_predictor_fyp_v10.ui.dashboard.DashboardFragment;
import com.hsb.covid_19_predictor_fyp_v10.ui.home.HomeFragment;
import com.hsb.covid_19_predictor_fyp_v10.ui.notifications.NotificationsFragment;

public class MainActivity extends AppCompatActivity {
    final Fragment fragment1 = new HomeFragment();
    final Fragment fragment2 = new DashboardFragment();
    final Fragment fragment3 = new NotificationsFragment();
    Fragment active = fragment1;
    private ActivityMainBinding binding;
    MainActivity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        activity=this;
       load();
    }

    public void load(){
        FragmentManager fm = activity.getSupportFragmentManager();

        if (checkLocationPermission()){

            binding = ActivityMainBinding.inflate(getLayoutInflater());

            try {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                boolean theme = preferences.getBoolean("theme",false);
                if (theme) {
                    setTheme(R.style.DarkTheme);
                }
            } catch (Exception e) {
                setTheme(R.style.Theme_AppCompat);

                e.printStackTrace();
            }
            setContentView(binding.getRoot());

        try {
            NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_activity_main);
            BottomNavigationView navView = findViewById(R.id.nav_view);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {


                 try {
                     fm.beginTransaction().add(R.id.nav_host_fragment_activity_main, fragment3, "3").hide(fragment3).commit();

                     fm.beginTransaction().add(R.id.nav_host_fragment_activity_main, fragment2, "2").hide(fragment2).commit();
                     fm.beginTransaction().add(R.id.nav_host_fragment_activity_main, fragment1, "1").commit();
                 } catch (Exception e) {
                     e.printStackTrace();
                 }

                    navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            switch (item.getItemId()) {
                                case (R.id.navigation_home): {
                                    fm.beginTransaction().hide(active).show(fragment1).commit();
                                    active = fragment1;
                                    return true;
                                }
                                case (R.id.navigation_dashboard): {
                                    fm.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                                    fm.beginTransaction().hide(active).show(fragment2).commit();
                                    active = fragment2;

                                    return true;
                                }
                                case (R.id.navigation_notifications): {
                                    fm.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

                                    fm.beginTransaction().hide(active).show(fragment3).commit();
                                    active = fragment3;
                                    return true;
                                }


                            }
                            return false;
                        }
                    });

                }
            }, 1500);
        } catch (Exception e) {
            e.printStackTrace();
        }

        }

    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Permission Required")
                        .setMessage("For better experience, location permission is needed")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                }).create().show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    finish();
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
}
