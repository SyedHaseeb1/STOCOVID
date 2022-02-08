package com.hsb.covid_19_predictor_fyp_v10.ui.home;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.DatabaseErrorHandler;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.textclassifier.ConversationActions;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.hsb.covid_19_predictor_fyp_v10.R;
import com.hsb.covid_19_predictor_fyp_v10.SettingsActivity;
import com.hsb.covid_19_predictor_fyp_v10.databinding.FragmentHomeBinding;
import com.hsb.covid_19_predictor_fyp_v10.ui.dashboard.DashboardFragment;
import com.hsb.covid_19_predictor_fyp_v10.ui.notifications.NotificationsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.ext.DeclHandler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {


    List countries_array;
    LinearLayout timeL;
    String api_json = "https://api.covid19api.com/live/country/pakistan/status/confirmed.json";
    String covid_global = "https://api.covid19api.com/summary";
    String from_this_to_this =
            "https://api.covid19api.com/country/Pakistan/status/confirmed?from=2021-11-18T00:00:00Z&to=2021-11-24T00:00:00Z";
    String country_name, statnamee_name;
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    String current_country;
    String[] countries = new String[]{"Global", "Afghanistan", "Albania", "Algeria",
            "Andorra", "Angola",
            "Argentina", "Armenia", "Australia", "Austria", "Azerbaijan",
            "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize",
            "Benin", "Bhutan", "Bolivia", "Botswana",
            "Brazil", "Brunei Darussalam", "Bulgaria", "Burkina Faso",
            "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Chile",
            "China", "Colombia", "Comoros",
            "Costa Rica", "Cuba", "Cyprus", "Czech Republic", "Denmark",
            "Djibouti", "Dominica", "Dominican Republic", "Ecuador",
            "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia",
            "Fiji", "Finland", "France", "Gabon", "Gambia", "Georgia",
            "Germany", "Ghana", "Greece", "Grenada", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Hungary", "Iceland", "India", "Indonesia",
            "Iran", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan",
            "Kazakhstan", "Kenya", "Kuwait", "Kyrgyzstan",
            "Latvia", "Lebanon", "Lesotho", "Liechtenstein", "Lithuania", "Luxembourg", "Macau",
            "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands",
            "Martinique", "Mauritania", "Mauritius", "Mexico", "Micronesia",
            "Moldova", "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar",
            "Namibia", "Nauru", "Nepal", "Netherlands", "New Caledonia", "New Zealand", "Nicaragua",
            "Niger", "Nigeria", "Niue", "Norway", "Oman", "Pakistan", "Palau",
            "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn", "Poland",
            "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russian Federation",
            "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Samoa", "San Marino", "Sao Tome and Principe",
            "Saudi Arabia", "Senegal", "Seychelles", "Sierra Leone", "Singapore", "Slovenia",
            "Solomon Islands", "Somalia", "South Africa", "South Korea", "Spain", "Sri Lanka", "Sudan", "Suriname",
            "Swaziland", "Sweden", "Switzerland", "Tajikistan", "Tanzania", "Thailand", "Togo",
            "Trinidad and Tobago", "Tunisia", "Turkey",
            "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom",
            "United States of America", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam",
            "Yemen", "Zambia", "Zimbabwe", "Palestine"};
    List<String> array1;
    //UI
    TextView activetxt, deathstxt, recoveredtxt, affectedtxt, critical_num,
            totaltxt,today_tests_txt;
    String activeS, deathS, recoverS, affectS, seriousS;
    Button learnmore;
    ImageView settings;
    ProgressDialog progressDialog;
    TextView datetxt;
    Context context;
    Spinner spinner;
    ArrayAdapter<String> spinnerArrayAdapter;
    ScrollView main_L;
    SwipeRefreshLayout swipe_L;

    //New API
    TextView today_recovered_num, critical_txt, todaydeaths, totalcurrent_txt;
    String New_Api_Link_Global = "https://corona.lmao.ninja/v2/countries/";
    String New_Api_Link_Global_Yesterday1 = "https://corona.lmao.ninja/v2/countries/";
//    String New_Api_Link_Global = "https://corona.lmao.ninja/v2/all?";
    TextView positivity_ratio_txt;


    @SuppressLint("ResourceType")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        countries_array = new ArrayList<String>();
        //hooks
        activetxt = root.findViewById(R.id.activenum); //available
        deathstxt = root.findViewById(R.id.deathsnum); //available
        recoveredtxt = root.findViewById(R.id.recoverdnum);
        affectedtxt = root.findViewById(R.id.affectednum); //available
        critical_num = root.findViewById(R.id.critical_num);
        timeL = root.findViewById(R.id.timeL);
        totaltxt = root.findViewById(R.id.totaltxt);
        settings = root.findViewById(R.id.settings);
        learnmore = root.findViewById(R.id.learnmorebtn);
        datetxt = root.findViewById(R.id.date);
        main_L = root.findViewById(R.id.main_L);
        today_recovered_num = root.findViewById(R.id.today_recovered_num);
        totalcurrent_txt = root.findViewById(R.id.totalcurrent_txt);
        todaydeaths = root.findViewById(R.id.todaydeaths_num);
        critical_txt = root.findViewById(R.id.critical_num);
        swipe_L = root.findViewById(R.id.swipe_L);
        positivity_ratio_txt = root.findViewById(R.id.positvity_ratio_num);
        today_tests_txt = root.findViewById(R.id.today_tests_num);


        TextView prevent_txt;
        prevent_txt = root.findViewById(R.id.preventiontxt);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean theme_dark = preferences.getBoolean("theme_dark", false);
        if (theme_dark) {
            main_L.setBackgroundColor(getContext().getResources().getColor(R.color.theme_dark));

            prevent_txt.setTextColor(getContext().getResources().getColor(R.color.white));
        }
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Please wait..");
        progressDialog.setCancelable(false);
        array1 = new ArrayList<>();
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });
        context = getContext();

        learnmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Learn More", Toast.LENGTH_SHORT).show();
            }
        });


        //  current_country=getUserCountry(getContext());

        spinner = (Spinner) root.findViewById(R.id.simpleSpinner);
        spinnerArrayAdapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_spinner_item,
                        countries); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);


        //Live Location
//        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        String countryCode = tm.getSimCountryIso();
        new GetLocation().execute();


        //NEW COVID API
        //  Json_NEW_API json_new_api = new Json_NEW_API();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                json_new_api.execute();
//            }
//        }, 3000);


        Animation get_visible_anim = AnimationUtils.loadAnimation(getContext(), R.anim.get_visibile_anim);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                Animation anim = new AlphaAnimation(0.0f, 1.0f);
//                anim.setDuration(2000); //You can manage the blinking time with this parameter
//                anim.setStartOffset(10);
//                anim.setRepeatMode(Animation.ABSOLUTE);
//                anim.setRepeatCount(Animation.ABSOLUTE);

                // main_L.setAnimation(get_visible_anim);
            }
        }, 1000);

        swipe_L.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                new Json_NEW_API().execute();
                new Json_NEW_API_Positvity().execute();
            }


        });


        return root;
    }


    int cc = 0;

    private class GetLocation extends AsyncTask {

        @Override
        protected void onPostExecute(Object o) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = preferences.edit();

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getContext(), "You have selected " + spinner.getSelectedItem().toString()
                            + " as your current place.", Toast.LENGTH_SHORT).show();
                   new Handler().postDelayed(new Runnable() {
                       @Override
                       public void run() {

                   country_name = spinner.getSelectedItem().toString().trim();
                    progressDialog.setMessage("Loading..");
                    //progressDialog.show();
//                    Log.e("page1", "spinner");


                    if (country_name.equals("Global")) {
                        New_Api_Link_Global = "https://corona.lmao.ninja/v2/all?";
                        new Json_NEW_API().execute();
                        new Json_NEW_API_Positvity().execute();
                    }

                    else {
                        cc++;
                        if (cc < 2) {
                            activetxt.setText("");
                            deathstxt.setText("");
                            recoveredtxt.setText("");
                            affectedtxt.setText("");
                            todaydeaths.setText("");
                            today_recovered_num.setText("");
                            totalcurrent_txt.setText("");
                            critical_txt.setText("");
                            positivity_ratio_txt.setText("");
                            datetxt.setText("As of ");
                            New_Api_Link_Global = "https://corona.lmao.ninja/v2/countries/" + country_name + "?";
                            //new json_data().execute();
                            new Json_NEW_API().execute();
                            new Json_NEW_API_Positvity().execute();


                        }
                        //new json_data().execute();
                        New_Api_Link_Global = "https://corona.lmao.ninja/v2/countries/" + country_name + "?";

                        new Json_NEW_API().execute();
                        new Json_NEW_API_Positvity().execute();
                        Log.e("New_Link", New_Api_Link_Global);
                        // new json_data_country().execute();
                        editor.putString("Country", country_name);
                        editor.apply();

                    }

                       }
                   },0);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            // progressDialog.dismiss();
            super.onPostExecute(o);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Getting Current Location..");
            //progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                current_country = getLocation(context) + "";


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int spinnerPosition = spinnerArrayAdapter.getPosition(current_country);
                            spinner.setSelection(spinnerPosition);

                        }
                    });

            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Please Check Your Internet Connection", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }
                });

            }
            Log.e("C_Country", current_country + "");

            return null;
        }
    }


    public String getLocation(Context context) {
        String country_name = null;
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Geocoder geocoder = new Geocoder(context);
        for (String provider : lm.getAllProviders()) {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            @SuppressWarnings("ResourceType") Location location = lm.getLastKnownLocation(provider);
            if (location != null) {
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),
                            1);
                    if (addresses != null && addresses.size() > 0) {
                        country_name = addresses.get(0).getCountryName() + "";
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (country_name==null){
            country_name="Global";
        }
        return country_name + "";
    }

    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry;
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toUpperCase(Locale.ENGLISH);
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //COVID OLD
    class json_data extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            // progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            progressDialog.dismiss();

            super.onPostExecute(s);
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        protected String doInBackground(Void... voids) {


            try {
                URL url = new URL(covid_global);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                //Log.e("aaaa", result + "");
                JSONObject json = new JSONObject(result);
                JSONArray contacts = new JSONArray();
                contacts.put(json.get("Global"));
                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(i);
                    String newConfirmed = c.getString("NewConfirmed");
                    String totalConfirmed = c.getString("TotalConfirmed");
                    String totaldeaths = c.getString("TotalDeaths");
                    String totalRecovered = c.getString("TotalRecovered");
                    String newDeaths = c.getString("NewDeaths");
                    String date = c.getString("Date");

                    activeS = totalConfirmed;
                    deathS = totaldeaths;
                    recoverS = totalRecovered;
                    getFormatedAmount(Integer.parseInt(newConfirmed));
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            activetxt.setText(getFormatedAmount(Integer.parseInt(activeS)) + "");
                            deathstxt.setText(getFormatedAmount(Integer.parseInt(deathS)) + "");
                            recoveredtxt.setText(getFormatedAmount(Integer.parseInt(recoverS)) + "");
                            affectedtxt.setText(getFormatedAmount(Integer.parseInt(newConfirmed)) + "");
                            todaydeaths.setText(getFormatedAmount(Integer.parseInt(newDeaths)) + "");
                            datetxt.setText("As of " + date.substring(0, date.indexOf("T")));

                        }
                    });
//                    Log.e("aa",
//                            "newConfirmed: " +
//                                    newConfirmed
//                                    + "\n" +
//                                    "totalConfirmed: " +
//                                    totalConfirmed
//                                    + "\n" +
//                                    "totaldeaths: " +
//                                    totaldeaths
//                                    + "\n" +
//                                    "totalRecovered: " +
//                                    totalRecovered
//                    );

                }
            } catch (Exception e) {
                // Log.e("aaaa err", e.getLocalizedMessage());
            }


            return null;
        }
    }

    class json_data_country extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            progressDialog.dismiss();
            super.onPostExecute(s);
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        protected String doInBackground(Void... voids) {


            try {
                URL url = new URL(covid_global);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                JSONObject json = new JSONObject(result);

                JSONArray contacts = new JSONArray();

                JSONArray data = json.getJSONArray("Countries");
                //  Log.e("aaar", data + "");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject object = data.getJSONObject(i);
                    String cname = object.getString("Country");
                    String newcases = object.getString("NewConfirmed");
                    String TotalConfirmed = object.getString("TotalConfirmed");
                    String NewDeaths = object.getString("NewDeaths");
                    String TotalDeaths = object.getString("TotalDeaths");
                    String NewRecovered = object.getString("NewRecovered");
                    String TotalRecovered = object.getString("TotalRecovered");
                    String date = object.getString("Date");
                    //New Deaths
                    int oldD, newD;
                    newD = Integer.parseInt(deathS);


                    if (cname.equals(country_name)) {
                        //   Log.e("aaac", "Country: " + cname + "\nNew Cases: " + newcases + "");
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                activetxt.setText(getFormatedAmount(Integer.parseInt(TotalConfirmed)) + "");
                                deathstxt.setText(getFormatedAmount(Integer.parseInt(TotalDeaths)) + "");
                                recoveredtxt.setText(getFormatedAmount(Integer.parseInt(TotalRecovered)) + "");
                                affectedtxt.setText(getFormatedAmount(Integer.parseInt(newcases)) + "");
                                todaydeaths.setText(getFormatedAmount(Integer.parseInt(NewDeaths)) + "");
                                datetxt.setText("As of " + date.substring(0, date.indexOf("T")));

                            }
                        });

                    }
                    //Log.e("aaac", "Country: "+cname+"\nNew Cases: "+newcases + "");


                }
            } catch (Exception e) {
                //  Log.e("aaaa err", e.getLocalizedMessage());
            }


            return null;
        }
    }

    long P_Ratio=0;
    long Today_Cases=0;
    //COVID NEW
    class Json_NEW_API_Positvity extends AsyncTask<Void, String, String> {
        @Override
        protected void onPostExecute(String s) {
           // new Json_NEW_API_Positvity().execute();
            try {    P_Ratio=P_Ratio/Today_Cases;
            Log.e("N_C_P",P_Ratio+"");



                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        positivity_ratio_txt.setTextSize(14f);
                        positivity_ratio_txt.setText(P_Ratio+" %");

                    }
                });
            } catch (Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        positivity_ratio_txt.setText("Not Available");
                        positivity_ratio_txt.setTextSize(13f);

                    }
                });
                e.printStackTrace();
            }

            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                URL url = new URL(New_Api_Link_Global+"yesterday=true&strict=true&query");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                JSONObject jobj;
                try {
                    jobj = new JSONObject(result);
                    //  JSONObject object;
                    int count = 0;
                    Map<String, String> map = null;
                    for (int i = 0; i < jobj.length(); i++) {

                        // object = new JSONObject();
                        map = new HashMap<String, String>();
                        Iterator<?> iter = jobj.keys();
                        while (iter.hasNext()) {
                            String key = (String) iter.next();
                            String value = jobj.getString(key);
                            map.put(key, value);


                        }

                    }
                    long Yesterday_Tests = Long.parseLong(map.get("tests"));
                    P_Ratio-=Yesterday_Tests;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            today_tests_txt.setText(getFormatedAmount(P_Ratio)+"");

                        }
                    });



                } catch (JSONException e) {
                    e.printStackTrace();

                }


            } catch (Exception e) {
                e.printStackTrace();


            }
            return null;
        }
    }

        class Json_NEW_API extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            try {
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                progressDialog.dismiss();
                swipe_L.setRefreshing(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        protected String doInBackground(Void... voids) {


            try {
                URL url = new URL(New_Api_Link_Global);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                JSONObject jobj;
                try {
                    jobj = new JSONObject(result);
                    //  JSONObject object;
                    int count = 0;
                    Map<String, String> map = null;
                    for (int i = 0; i < jobj.length(); i++) {

                        // object = new JSONObject();
                        map = new HashMap<String, String>();
                        Iterator<?> iter = jobj.keys();
                        while (iter.hasNext()) {
                            String key = (String) iter.next();
                            String value = jobj.getString(key);
                            map.put(key, value);


                        }

                    }
                    Log.e("NEWAPI_Date ", convertDate(map.get("updated") + "", "dd/MM/yyyy \n hh:mm:ss") + "");
                    String TotalCases = getFormatedAmount(Long.parseLong(map.get("cases") + "")) + "";
                    String TodayCases = getFormatedAmount(Integer.parseInt(map.get("todayCases"))) + "";
                    String TotalDeaths = getFormatedAmount(Long.parseLong(map.get("deaths"))) + "";
                    String TodayDeaths = getFormatedAmount(Integer.parseInt(map.get("todayDeaths"))) + "";
                    P_Ratio = Long.parseLong(map.get("tests"));
                    Today_Cases = Long.parseLong(map.get("todayCases"));
                    String TotalRecovered = getFormatedAmount(Integer.parseInt(map.get("recovered"))) + "";
                    String TodayRecovered = getFormatedAmount(Integer.parseInt(map.get("todayRecovered"))) + "";
                    String TotalActive = getFormatedAmount(Integer.parseInt(map.get("active"))) + "";
                    String TotalCritical = getFormatedAmount(Integer.parseInt(map.get("critical"))) + "";
                    String CasesPerOneMillion = getFormatedAmount(Integer.parseInt(map.get("casesPerOneMillion"))) + "";
                    float deathsPerMillion = Float.parseFloat(map.get("deathsPerOneMillion"));
                    String DeathsPerOneMillion = getFormatedAmount((long) deathsPerMillion);

                    String TotalTests = getFormatedAmount(Long.parseLong(map.get("tests") + "")) + "";
                    float testsPerOneMillion = Float.parseFloat(map.get("testsPerOneMillion"));

                    String TestsPerOneMillion = getFormatedAmount((long) testsPerOneMillion);
                    String Population = getFormatedAmount(Long.parseLong(map.get("population") + "")) + "";
//                    String AffectedCountries = getFormatedAmount(Integer.parseInt(map.get("affectedCountries") + "")) + "";
                    String Date = convertDate(map.get("updated") + "", "dd/MM/yyyy hh:mm:ss");
                    Log.e("NEWAPI_Cases ", TotalCases + "");


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activetxt.setText(TotalCases);
                            deathstxt.setText(TotalDeaths);
                            recoveredtxt.setText(TotalRecovered);
                            affectedtxt.setText(TodayCases);
                            todaydeaths.setText(TodayDeaths);
                            today_recovered_num.setText(TodayRecovered);
                            totalcurrent_txt.setText(TotalActive);
                            critical_txt.setText(TotalCritical);

                            datetxt.setText("As of " + Date);


//                            activetxt.setText(getFormatedAmount(Integer.parseInt(activeS)) + "");
//                            deathstxt.setText(getFormatedAmount(Integer.parseInt(deathS)) + "");
//                            recoveredtxt.setText(getFormatedAmount(Integer.parseInt(recoverS)) + "");
//                            affectedtxt.setText(getFormatedAmount(Integer.parseInt(newConfirmed)) + "");
//                            serioustxt.setText(getFormatedAmount(Integer.parseInt(newDeaths)) + "");
//                            datetxt.setText("As of " + date.substring(0, date.indexOf("T")));
                        }
                    });
                } catch (JSONException e) {
                    Log.e("NEWAPI_Err1", e.getLocalizedMessage());

                    e.printStackTrace();
                }


                //JSONObject json = new JSONObject(result);

                // JSONArray contacts = new JSONArray();

                //  JSONArray data = json.getJSONArray("");
                //  Log.e("date", data + "");

//                for (int i = 0; i < data.length(); i++) {
//                    JSONObject object = data.getJSONObject(i);
//                    String cname = object.getString("Country");
//                    String newcases = object.getString("NewConfirmed");
//                    String TotalConfirmed = object.getString("TotalConfirmed");
//                    String TotalDeaths = object.getString("TotalDeaths");
//                    String NewRecovered = object.getString("NewRecovered");
//                    String TotalRecovered = object.getString("TotalRecovered");
//                    String Date = object.getString("Date");
//                    //New Deaths
//                    int oldD, newD;
//                    newD = Integer.parseInt(deathS);
//
//
//                    if (cname.equals(country_name)) {
//                        Log.e("date", "Country: " + cname + "\nNew Cases: " + newcases + "");
//                        getActivity().runOnUiThread(new Runnable() {
//                            public void run() {
////                                activetxt.setText(getFormatedAmount(Integer.parseInt(TotalConfirmed)) + "");
////                                deathstxt.setText(getFormatedAmount(Integer.parseInt(TotalDeaths)) + "");
////                                recoveredtxt.setText(getFormatedAmount(Integer.parseInt(TotalRecovered)) + "");
////                                affectedtxt.setText(getFormatedAmount(Integer.parseInt(newcases)) + "");
////                                serioustxt.setText(getFormatedAmount(Integer.parseInt(NewRecovered)) + "");
//                            }
//                        });
//
//                    }
//                    //Log.e("aaac", "Country: "+cname+"\nNew Cases: "+newcases + "");
//
//
//                }
            } catch (Exception e) {
                Log.e("NEWAPI_Err2", e.getLocalizedMessage() + "");
                e.printStackTrace();

            }


            return null;
        }
    }

    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    private String getFormatedAmount(long amount) {
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }


    public void split_data(String data) {

        String[] str1 = data.split("");
        String datasplited = str1[0];
        //  Log.e("aaa", datasplited);

    }


    class http_req extends AsyncTask<Void, String, String> {

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder content = new StringBuilder();
            try {
                URL u1 = new URL(api_json);
                HttpURLConnection uc1 = (HttpURLConnection) u1.openConnection();
                if (uc1.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    InputStream is = uc1.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                    String line;
                    String[] str;
                    while ((line = br.readLine()) != null) {
                        content.append(line).append("\n");
                        //  Log.e("aaaa", content + "");
                        JSONArray js = new JSONArray(content.toString());
                        JSONObject obj2 = new JSONObject();
                        obj2.put("Country", js.toString());
                        //   Log.e("aaa", obj2.getString("Counrty"));

                    }

                }//other codes
                return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}