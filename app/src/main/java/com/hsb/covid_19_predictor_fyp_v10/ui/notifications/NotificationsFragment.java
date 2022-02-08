package com.hsb.covid_19_predictor_fyp_v10.ui.notifications;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.hsb.covid_19_predictor_fyp_v10.R;
import com.hsb.covid_19_predictor_fyp_v10.databinding.FragmentNotificationsBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //My Stuff
    TextView top_txt, countryname_txt, provincename_txt, status_txt, newcases_txt,
            totalcases_txt, totaldeaths_txt;
    EditText search_edit;
    ImageView search_btn, flag_img;
    ProgressDialog progressDialog;
    ListView listView;
    String country_name = "Pakistan";
    String province_name = "Islamabad";


    String api_link_demo = "https://api.covid19api.com/live/country/pk/status/confirmed/date/2022-01-29";
    String api_link_demo2 = "https://corona.lmao.ninja/v2/jhucsse";

    String api_link_complete;
    String api_link_p1 = "https://api.covid19api.com/live/country/";
    String api_link_p3 = "/status/confirmed/date/";

    Json_data_provinces json_data_provinces;


    //COVID API USECASES

    //COUNTRY
    //PROVINCE
    //LAT, LONG
    //CONFIRMED
    //DEATHS
    //RECOVERED = 0
    //ACTIVE
    //DATE
    List Country_array, Provinces_array, Status_array, Active_array, Totalcases_array, Totaldeaths_array, Flag_array;
    CustomAdapter customAdapter;

    int i = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        Country_array = new ArrayList();
        Provinces_array = new ArrayList();
        Status_array = new ArrayList();
        Active_array = new ArrayList();
        Totalcases_array = new ArrayList();
        Totaldeaths_array = new ArrayList();
        Flag_array = new ArrayList();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean theme_dark=preferences.getBoolean("theme_dark",false);
        ScrollView main_L;
        main_L=root.findViewById(R.id.main_L);
        if (theme_dark){
            main_L.setBackgroundColor(getContext().getResources().getColor(R.color.theme_dark));
        }

        listView = root.findViewById(R.id.listview);
        customAdapter = new CustomAdapter(getContext(), R.layout.custom_list,
                Country_array, Provinces_array, Status_array,
                Active_array, Totalcases_array, Totaldeaths_array, Flag_array);
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        progressDialog = new ProgressDialog(getContext());
        top_txt = root.findViewById(R.id.txt_top);
        search_btn = root.findViewById(R.id.search_btn);
        search_edit = root.findViewById(R.id.search_edit);
//        flag_img = root.findViewById(R.id.flag_img);
//
//        countryname_txt = root.findViewById(R.id.counrtyname_txt);
//        provincename_txt = root.findViewById(R.id.provincename_txt);
//        newcases_txt = root.findViewById(R.id.newcases_txt);
//        totalcases_txt = root.findViewById(R.id.totalcases_txt);
//        totaldeaths_txt = root.findViewById(R.id.totaldeaths_txt);

        json_data_provinces = new Json_data_provinces();


        country_name = preferences.getString("Country", "Germany");
        api_function(country_name);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = 0;
                Animation fade_anim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_anim);
                Animation get_visible_anim = AnimationUtils.loadAnimation(getContext(), R.anim.get_visibile_anim);

                if (search_edit.getVisibility() == View.GONE) {

                    search_edit.setVisibility(View.VISIBLE);
                    search_edit.setAnimation(get_visible_anim);
                    search_edit.setEnabled(true);

                    top_txt.setVisibility(View.GONE);
                    top_txt.setAnimation(fade_anim);


                } else {
                    search_edit.setVisibility(View.GONE);
                    search_edit.setAnimation(fade_anim);
                    top_txt.setVisibility(View.VISIBLE);
                    String search_txt = search_edit.getText().toString();
                    top_txt.setAnimation(get_visible_anim);
                    country_name = search_txt;

                    if (!search_txt.isEmpty()) {
                        Provinces_array.clear();
                        Country_array.clear();
                        Status_array.clear();
                        Totalcases_array.clear();
                        Totaldeaths_array.clear();
                        Active_array.clear();
                        Flag_array.clear();
                        top_txt.setText("Tody Cases\n" +
                                country_name);
                        api_function(country_name);
                        search_edit.setText("");
                        search_edit.setEnabled(false);
                        search_edit.clearFocus();

                    } else {
                        search_edit.clearFocus();
                        search_edit.setEnabled(false);
                        // top_txt.setText("Tody Cases");
                    }


                }

            }
        });
        search_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Animation fade_anim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_anim);
                    Animation get_visible_anim = AnimationUtils.loadAnimation(getContext(), R.anim.get_visibile_anim);

                    if (search_edit.getVisibility() == View.GONE) {

                        search_edit.setVisibility(View.VISIBLE);
                        search_edit.setAnimation(get_visible_anim);
                        search_edit.setEnabled(true);

                        top_txt.setVisibility(View.GONE);
                        top_txt.setAnimation(fade_anim);


                    } else {
                        search_edit.setVisibility(View.GONE);
                        search_edit.setAnimation(fade_anim);
                        top_txt.setVisibility(View.VISIBLE);
                        String search_txt = search_edit.getText().toString();
                        top_txt.setAnimation(get_visible_anim);
                        country_name = search_txt;

                        if (!search_txt.isEmpty()) {
                            Provinces_array.clear();
                            Country_array.clear();
                            Status_array.clear();
                            Totalcases_array.clear();
                            Totaldeaths_array.clear();
                            Active_array.clear();
                            Flag_array.clear();
                            top_txt.setText("Tody Cases\n" +
                                    country_name);
                            api_function(country_name);
                            search_edit.setText("");
                            search_edit.setEnabled(false);

                        } else {
                            // top_txt.setText("Tody Cases");
                        }


                    }
                    return true;
                }
                return false;
            }
        });
        return root;
    }

    public String Date_fun() {
        Date c = Calendar.getInstance().getTime();
        Date _1day = new Date(c.getTime() - 86400000L);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String yesterday = df.format(_1day);
        return yesterday + "";
    }

    public String Date_fun_revised() {
        Date c = Calendar.getInstance().getTime();
        Date _1day = new Date(c.getTime() - 86400000L - 86400000L);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String yesterday = df.format(_1day);
        return yesterday + "";
    }

    public void api_function(String country) {
        String date = Date_fun();
        if (country.length() < 4) {
            country = country.toLowerCase();
        }
        api_link_complete = api_link_p1 + country + api_link_p3 + date;
        new Json_data_provinces().execute();

    }

    public void api_function_revised(String country) {
        String date = Date_fun_revised();
        if (country.length() < 4) {
            country = country.toLowerCase();
        }
        api_link_complete = api_link_p1 + country + api_link_p3 + date;
        new Json_data_provinces().execute();


    }

    class Json_data_provinces extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading..");
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("P_link_revised", api_link_complete);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Active_array.size() < 1) {
                                progressDialog.dismiss();

                                if (i < 1) {
                                    if (Totalcases_array.size() < 1) {
                                        Toast.makeText(getContext(), "No data found, Rechecking..", Toast.LENGTH_SHORT).show();
                                    }
                                    api_function_revised(country_name);
                                    i++;
                                }
                            }
                        }
                    }, 500);
                }
            });

            super.onPostExecute(s);
        }

        int el = 0;

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        protected String doInBackground(Void... voids) {

            try {
                URL url = new URL(api_link_complete);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                //    JSONObject json = new JSONObject(result);


                JSONArray data = new JSONArray(result);

                for (int i = 0; i < data.length(); i++) {
                    JSONObject object = data.getJSONObject(i);
                    String cname = object.getString("Country");
                    String ccode = object.getString("CountryCode");
                    String pname = object.getString("Province");
                    String TotalConfirmed = object.getString("Confirmed");
                    String TotalDeaths = object.getString("Deaths");
                    String TotalRecovered = object.getString("Recovered");
                    String TotalActive = object.getString("Active");
                    String date = object.getString("Date");
                    //New Deaths
                    int oldD, newD;
                    // newD = Integer.parseInt(deathS);
                    Log.e("P_ccode", ccode
                            + "\n" + pname);


                    if (cname.contains(country_name) || ccode.contains(country_name.toUpperCase())) {
                        //   Log.e("aaac", "Country: " + cname + "\nNew Cases: " + newcases + "");
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {

                                //Setting Values
//                                countryname_txt.setText(cname + "");
//                                provincename_txt.setText(pname + "");
//                                totalcases_txt.setText(getFormatedAmount(Integer.parseInt(TotalConfirmed)) + "");
//                                totaldeaths_txt.setText(getFormatedAmount(Integer.parseInt(TotalDeaths)) + "");
                                String sourceString = getFormatedAmount(Integer.parseInt(TotalActive)) + "";
                                String boldText = sourceString;
                                String normalText = "\nActive";
                                SpannableString str = new SpannableString(boldText + normalText);
                                ListView listView;
                                str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                // newcases_txt.setText(str);

                                //Setting Flag
                                try {
                                    String flag_str = "flag_" + cname.toLowerCase()
                                            .replace("Lao PDR", "laos").replace(" ", "_");

                                    int resId = getResources().getIdentifier(flag_str, "drawable",
                                            getContext().getPackageName());
                                    Drawable d = getContext().getResources().getDrawable(resId);

                                    //Setting Provinces in Array For Swipe Gesture
                                    Provinces_array.add(pname + "");
                                    Country_array.add(cname + "");
                                    Active_array.add(str + "");
                                    Totalcases_array.add(getFormatedAmount(Integer.parseInt(TotalConfirmed)) + "");
                                    Totaldeaths_array.add(getFormatedAmount(Integer.parseInt(TotalDeaths)) + "");
                                    Flag_array.add(d + "");
                                    top_txt.setText("Current Cases\n" + cname);
                                } catch (Resources.NotFoundException e) {
                                    e.printStackTrace();
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }


                            }
                        });

                    } else {

                        if (el < 1) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "Country Code for " + country_name + " is " + ccode, Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                    el++;
                    //Log.e("P_json",cname+"\n"+pname+"\n"+api_link_complete);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < Provinces_array.size(); i++) {
                                    Log.e("P_array", Provinces_array.get(i) + "");

                                    customAdapter.notifyDataSetChanged();
                                    progressDialog.dismiss();
                                }


                            }
                        }, 1000);
                    }
                });

            } catch (Exception e) {
                Log.e("P_json err", e.getLocalizedMessage());
            }


            return null;
        }
    }

    private String getFormatedAmount(int amount) {
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }
}