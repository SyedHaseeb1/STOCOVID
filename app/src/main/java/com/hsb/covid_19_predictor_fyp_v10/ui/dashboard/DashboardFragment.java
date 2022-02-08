package com.hsb.covid_19_predictor_fyp_v10.ui.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.hsb.covid_19_predictor_fyp_v10.R;
import com.hsb.covid_19_predictor_fyp_v10.Stock_Market_WebPage;
import com.hsb.covid_19_predictor_fyp_v10.databinding.FragmentDashboardBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Struct;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import lecho.lib.hellocharts.formatter.LineChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class DashboardFragment extends Fragment {
    String from_this_to_this;
    int[] yAxisData;
    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;
    //TextView countrytxt;
    LineChartView cases_graph;
    List<String> cases_array;
    String countryname = "Pakistan";
    String date = "2021-11-18T00:00:00Z";
    String Stock_date = "2022-02-01";
    String[] countries = new String[]{"Global", "Pakistan", "Afghanistan", "Albania", "Algeria",
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

    List yAxisValues;
    List yAxisValues2;
    List axisValues;
    TextView weekly_date, today_datetxt;
    TextView weekly_date_stock, today_datetxt_stock;
    ProgressBar covid_pbr, stock_pbr;
    json_data_date_wise js_covid;
    String Global_Cases_Link = "https://corona.lmao.ninja/v2/historical/all";
    //Stock
    LineChartView stock_graph;
    List<String> stock_list_open;
    List<String> stock_list_close;
    List yAxisValues_stock;
    List yAxisValues2_stock;
    List axisValues_stock;
    int[] yAxisData_stock;
    String Stock_Link = "http://api.marketstack.com/v1/eod?access_key=38aa62e0cb3db5a9179b8a48bf79e40b&symbols=AAPL&date_from=";
    String Stock_Link_New = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=psx&apikey=TYV31X78NKKL4LTDa";
    String Stock_Link_New_1 = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=";
    String Stock_Link_New_2 = "baba";
    String Stock_Link_New_3 = "&apikey=TYV31X78NKKL4LTDa";
    TextView stock_txt;
    boolean mycovidalgo = false;


    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onPause() {
        cases_array.clear();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Country", "Pakistan");
        editor.apply();
        binding = null;
    }

    int v = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        cases_graph = root.findViewById(R.id.chart);
        weekly_date = root.findViewById(R.id.week_date);
        today_datetxt = root.findViewById(R.id.today_date);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean theme_dark = preferences.getBoolean("theme_dark", false);
        mycovidalgo = preferences.getBoolean("myalgo", false);
        ScrollView main_L;
        main_L = root.findViewById(R.id.main_L);
        if (theme_dark) {
            main_L.setBackgroundColor(getContext().getResources().getColor(R.color.theme_dark));
        }
        weekly_date_stock = root.findViewById(R.id.week_date_stock);
        today_datetxt_stock = root.findViewById(R.id.today_date_stock);
        stock_txt = root.findViewById(R.id.stock_txt);

        covid_pbr = root.findViewById(R.id.covid_pbr);
        stock_pbr = root.findViewById(R.id.stock_pbr);
        New_Global_Caes_API new_global_caes_api=new New_Global_Caes_API();
        cases_array = new ArrayList<>();
        yAxisData = new int[]{7, 6, 5, 4, 3, 2, 1, 0};
        //countrytxt = root.findViewById(R.id.countrytxt);
        Date c = Calendar.getInstance().getTime();
//        Date _7daysD = new Date(c.getTime() - 604800000L);
        Date _7daysD = new Date(c.getTime() - 691200000L);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today_date = df.format(c);
        String _7days = df.format(_7daysD);
        // Log.e("date", _7days + "");
        axisValues = new ArrayList();
        yAxisValues = new ArrayList();
        yAxisValues2 = new ArrayList();
        try {

            String name = preferences.getString("Country", "Pakistan");
            countryname = name;
        } catch (Exception e) {
            countryname = "Pakistan";
            e.printStackTrace();
        }
        //2021-11-18

        //Actual Calculation CASES
        js_covid = new json_data_date_wise();
        try {


            from_this_to_this = "https://api.covid19api.com/country/" + countryname + "/status/confirmed?" +
                    "from=" + _7days + "&to=" +
                    today_date + "";
            Log.e("Country", countryname + "\n" +
                    _7days + "\n" +
                    today_date + "\n" +
                    from_this_to_this);
            weekly_date.setText(_7days + "");
            today_datetxt.setText(today_date + "");


            new json_data_date_wise().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Spinner spinner = (Spinner) root.findViewById(R.id.simpleSpinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_spinner_item,
                        countries); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        SharedPreferences.Editor editor = preferences.edit();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                try {
                    countryname = spinner.getSelectedItem().toString().trim();

                    from_this_to_this = "https://api.covid19api.com/country/" + countryname + "/status/confirmed?" +
                            "from=" + _7days + "&to=" +
                            today_date + "";

                    if (countryname.equals("Global")) {
                         new New_Global_Caes_API().execute();
                    } else {

                        v = 0;
                        js_covid.execute();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

//                            Collections.reverse(temp2);


                              //  temp2.add(final_predicted + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //Sentimental Analysis Graph


        /**Stock Data Graph*/


        stock_graph = root.findViewById(R.id.chart_stock);
        axisValues_stock = new ArrayList();
        yAxisValues_stock = new ArrayList();
        yAxisValues2_stock = new ArrayList();
        stock_list_open = new ArrayList();
        stock_list_close = new ArrayList();

        Stock_bg_process stock_bg_process = new Stock_bg_process();
        Stock_Today();
        stock_bg_process.execute();

        stock_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a = 0;
                a++;
            }
        });
        final int[] lable_reset = {0};
        stock_graph.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //Open_Stcok_Web();
                try {
                    lable_reset[0]++;

                    if (lable_reset[0] == 1) {
                        stock_bg_process.show_line_label();
                    } else {
                        lable_reset[0] = 0;
                        stock_bg_process.remove_line_label();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });


        /**Stock Data Graph*/
        yAxisData_stock = new int[]{7, 6, 5, 4, 3, 2, 1, 0};
        // stock_bg_process.execute();

        // int a2=Linear_Regression_ALlo(8,21);
        // Log.e("Linear_Algo","Linear Regression: \n Day 7: "+Linear_Regression_ALlo(7,21));

        return root;
    }


    //New Global Covid Api
    class New_Global_Caes_API extends AsyncTask<Void, String, String> {
        Line line, line2;



        @Override
        protected void onPreExecute() {
            cases_array.clear();
            dates.clear();
            temp.clear();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(Global_Cases_Link);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                JSONObject array;
                try {
                    array = new JSONObject(result);
                    JSONObject object;
                    JSONObject object1 = array.getJSONObject("cases");
                    JSONArray arr = object1.names();

                    for (int i = 0; i < 8; i++) {
                        String date_in_db = arr.getString(arr.length() - 1 - i) + "";

                        JSONObject object2 = array.getJSONObject("cases");
                        Map<String, String> map = null;
                        for (int j = 0; j < object2.length(); j++) {
                            map = new HashMap<String, String>();
                            Iterator<?> iter = object2.keys();
                            while (iter.hasNext()) {
                                String key = (String) iter.next();
                                String value = object2.getString(key);
                                map.put(key, value);

                            }


                        }
                        String [] spilt1=date_in_db.split("/");
                        String [] spilt2=spilt1[1].split("/");
                        String month=spilt1[0];
                        String day=spilt2[0];
                        if (day.length()<2){
                            day="0"+day;
                        }
                        if (month.length()<2){
                            month="0"+month;
                        }
                        Log.e("N_C date", "2022-"+ month+"-"+day+""+"\t"+map.get(date_in_db) + "");
                        cases_array.add(map.get(date_in_db) + "");
                        dates.add("2022-"+ month+"-"+day+"");


                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                } catch (JSONException e) {
                    Log.e("N_C err1", e.getLocalizedMessage() + "");

                    //No Data Exception
                    //Recalling with new Date day-1


                    e.printStackTrace();
                }
                Collections.reverse(cases_array);
            } catch (Exception e) {
                Log.e("N_C err2", e.getLocalizedMessage() + "");
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            line = new Line(yAxisValues);
            line2 = new Line(yAxisValues2);
            Collections.reverse(dates);

            try {
                weekly_date.setText(dates.get(1).toString().replace("T00:00:00Z", "") + "");

                today_datetxt.setText(dates.get(dates.size() - 1).toString().replace("T00:00:00Z", "") + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (cases_array.size() > 7 && cases_array.size() < 15) {
                Log.e("if", "ASA");
                try {
                    DateIncrementer dateIncrementer = new DateIncrementer();
                    String datess = dates.get(7).toString().replace("T00:00:00Z", "");


                    String[] axisData = {
                            dates.get(1).toString().replace("2022-01-", "").replace("T00:00:00Z", "")
                                    .replace("2022-02-", ""),
                            dates.get(2).toString().replace("2022-01-", "").replace("T00:00:00Z", "")
                                    .replace("2022-02-", ""),
                            dates.get(3).toString().replace("2022-01-", "").replace("T00:00:00Z", "")
                                    .replace("2022-02-", ""),
                            dates.get(4).toString().replace("2022-01-", "").replace("T00:00:00Z", "")
                                    .replace("2022-02-", ""),
                            dates.get(5).toString().replace("2022-01-", "").replace("T00:00:00Z", "")
                                    .replace("2022-02-", ""),
                            dates.get(6).toString().replace("2022-01-", "").replace("T00:00:00Z", "")
                                    .replace("2022-02-", ""),
                            dates.get(7).toString().replace("2022-01-", "").replace("T00:00:00Z", "")
                                    .replace("2022-02-", ""),

                            new DateIncrementer().addOneDay(datess + "").replace("2022-01-", "")
                                    .replace("2022-02-", ""),
                            new DateIncrementer().addOneDay(datess + "").replace("2022-01-", "")
                                    .replace("2022-02-", ""),
                            new DateIncrementer().addOneDay(datess + "").replace("2022-01-", "")
                                    .replace("2022-02-", ""),
                            new DateIncrementer().addOneDay(datess + "").replace("2022-01-", "")
                                    .replace("2022-02-", ""),
                            new DateIncrementer().addOneDay(datess + "").replace("2022-01-", "")
                                    .replace("2022-02-", ""),
                            new DateIncrementer().addOneDay(datess + "").replace("2022-01-", "")
                                    .replace("2022-02-", ""),
                            new DateIncrementer().addOneDay(datess + "").replace("2022-01-", "")
                                    .replace("2022-02-", ""),


//                            "2",
//                            "3",
//                            "4",
//                            "5",
//                            "6",
//                            "7",

//                            "8",
//                            "9",
//                            "10",
//                            "11",
//                            "12",
//                            "13",
//                            "14"
                    };

                    d = 0;
                    try {
                        for (int i = 0; i < axisData.length; i++) {
                            axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < yAxisData[0]; i++) {

                        if (cases_array.size() < 6 || cases_array.size() > 15) {
                            // yAxisValues.clear();
//                        cases_array.clear();
//                        axisValues.clear();
                            Date c = Calendar.getInstance().getTime();
                            Date _8daysD = new Date(c.getTime() - 691200000L - 86400000L);
                            Date _1daysD = new Date(c.getTime() - 86400000L);
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String today_date = df.format(_1daysD);
                            String _8days = df.format(_8daysD);
                            from_this_to_this = "https://api.covid19api.com/country/" + countryname + "/status/confirmed?" +
                                    "from=" + _8days + "&to=" +
                                    today_date + "";

                            Log.e("-1 day in", today_date + "\n" + _8days + "\n" + from_this_to_this);
                            weekly_date.setText(dates.get(0) + "");
                            today_datetxt.setText(dates.get(dates.size()) + "");

                        } else {
                            try {

                                yAxisValues.add(new PointValue(i, Integer.parseInt(cases_array.get(i).trim())));
                                yAxisValues2.add(new PointValue(i, Integer.parseInt(cases_array.get(i).trim())));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }


                        }

                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                List lines = new ArrayList<String>();
                List lines2 = new ArrayList<String>();

                lines.add(line);

                //Future Data
                lines.add(line2);

                //lines2.add(line2);//For Prediction

                data.setLines(lines);

                //Future Prediction
                // data2.setLines(lines2);

                int day1 = 0,
                        day2 = 0,
                        day3 = 0,
                        day4 = 0,
                        day5 = 0,
                        day6 = 0,
                        day7 = 0,
                        day8 = 0;
                try {
                    Log.e("json", js_covid.getStatus() + "");
                    if (cases_array.size() > 7) {
                        try {
                            day1 = Integer.parseInt(cases_array.get(0));
                            day2 = Integer.parseInt(cases_array.get(1));
                            day3 = Integer.parseInt(cases_array.get(2));
                            day4 = Integer.parseInt(cases_array.get(3));
                            day5 = Integer.parseInt(cases_array.get(4));
                            day6 = Integer.parseInt(cases_array.get(5));
                            day7 = Integer.parseInt(cases_array.get(6));
                            day8 = Integer.parseInt(cases_array.get(7));
                        } catch (NumberFormatException eee) {
                            eee.printStackTrace();
                        }
                        //Each day difference Week
                        int a, b, c, d, e, f, g;
                        a = day2 - day1;//1
                        b = day3 - day2;//2
                        c = day4 - day3;//3
                        d = day5 - day4;//4
                        e = day6 - day5;//5
                        f = day7 - day6;//6
                        g = day8 - day7;//7
                        if (a < 0) {
                            a = a * -1;
                        }
                        if (b < 0) {
                            b = b * -1;
                        }
                        if (c < 0) {
                            c = c * -1;
                        }
                        if (d < 0) {
                            d = d * -1;
                        }
                        if (e < 0) {
                            e = e * -1;
                        }
                        if (f < 0) {
                            f = f * -1;
                        }
                        if (g < 0) {
                            g = g * -1;
                        }
                        int average_cases = Average_Per_Day(a, b, c, d, e, f, g);
                        cases_graph.setTooltipText("Average Cases Per Day \n" + average_cases);
                        Log.e("values of ",
                                a + "\n"
                                        + b + "\n"
                                        + c + "\n"
                                        + d + "\n"
                                        + e + "\n"
                                        + f + "\n"
                                        + g + "\n"
                        );


                        //1 Week Actual Data
                        temp.add(a + "");//1
                        temp.add(b + "");//2
                        temp.add(c + "");//3
                        temp.add(d + "");//4
                        temp.add(e + "");//5
                        temp.add(f + "");//6
                        temp.add(g + "");//7


                        yAxisValues.clear();
                        for (int aa = 0; aa < temp.size(); aa++) {
                            yAxisValues.add(new PointValue(aa, Integer.parseInt(temp.get(aa) + "")));

                        }
                        yAxisValues2.clear();
                        //Future Prediction
                        List temp2 = new ArrayList();
                        int x = 0;
                        int y = 0;
                        int final_predicted = 0;
                        if (mycovidalgo) {
                            for (int i = 0; i < 7; i++) {
                                x = Integer.parseInt(temp.get(i) + "");
                                y = 2 * x;
                                final_predicted = y - average_cases;
                                if (final_predicted < 0) {
                                    final_predicted = final_predicted * (-1);
                                }

//                            Collections.reverse(temp2);


                                temp2.add(final_predicted + "");

                                Log.e("Algo", final_predicted + "");
                            }

                            Collections.reverse(temp2);
                        } else {

                            //Future graph data
                            for (int j = 1; j < 8; j++) {
                                temp2.add(Linear_Regression_ALlo(j, "Days " + j));
                            }

                        }

                        for (int bb = 0; bb < temp2.size(); bb++) {
                            yAxisValues2.add(new PointValue(bb + 7.0f, Integer.parseInt(temp2.get(bb) + "")));
                            // Log.e("temp", yAxisValues2 + "");
                        }

//a,b,c,d,e,f,g
                        if (g > f && g > e && g > d) {
                            line.setColor(getResources().getColor(R.color.red));
                            Log.e("1st", "true");


                        } else if (f > e && f > d && f > d) {
                            Log.e("2nd", "true");
                            line.setColor(getResources().getColor(R.color.red));

                        } else if (e > d && e > c && e > b) {
                            Log.e("3rd", "true");

                            line.setColor(getResources().getColor(R.color.red));

                        } else {
                            line.setColor(getResources().getColor(R.color.green));

                        }

                        //Future Prediction
                        if (g > f

                        ) {
                            line2.setColor(getResources().getColor(R.color.redish));

                        } else {
                            line2.setColor(getResources().getColor(R.color.greenish));

                        }
                    } else {
                        change_date_9days();
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                cases_graph.setLineChartData(data);
//            cases_graph.setLineChartData(data2);

                Axis axis = new Axis();
                axis.setValues(axisValues);
                axis.setMaxLabelChars(1);
                Axis yAxis = new Axis();
                //yAxis.setValues(yAxisValues);
                data.setAxisYLeft(yAxis.setAutoGenerated(false));
                data.setAxisXBottom(axis);
                data.setValueLabelTextSize(10);

                //Future Data

                data2.setAxisYLeft(yAxis.setAutoGenerated(false));
                data2.setAxisXBottom(axis);
                data2.setValueLabelTextSize(10);
                try {
                    line.setHasLabels(true);
                    line.setHasLines(true);
                    line.setFilled(true);
                    line.setHasPoints(true);
                    line.setPointColor(getContext().getResources().getColor(R.color.yellow));
                    line.setStrokeWidth(2);
                    line.setPointRadius(5);

                    //Future Graph
                    line2.setHasLabels(true);
                    line2.setHasLines(true);
                    line2.setFilled(true);
                    line2.setHasPoints(true);
                    line2.setPointColor(getContext().getResources().getColor(R.color.graysh2));
                    line2.setStrokeWidth(2);
                    line2.setPointRadius(5);
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }

            } else {
                Date c = Calendar.getInstance().getTime();
                Date _8daysD = new Date(c.getTime() - 691200000L);
                Date _1daysD = new Date(c.getTime() - 86400000L);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String today_date = df.format(_1daysD);
                String _8days = df.format(_8daysD);
                from_this_to_this = "https://api.covid19api.com/country/" + countryname + "/status/confirmed?" +
                        "from=" + _8days + "&to=" +
                        today_date + "";
//                from_this_to_this = "https://api.covid19api.com/country/" + countryname + "/status/confirmed?" +
//                        "from=" + "2022-01-15" + "&to=" +
//                        "2022-01-23" + "";
                Log.e("-1 day2", today_date + "\n" + _8days + "\n" + from_this_to_this);
                try {
                    weekly_date.setText(dates.get(1).toString().replace("T00:00:00Z", "") + "");

                    today_datetxt.setText(dates.get(dates.size() - 1).toString().replace("T00:00:00Z", "") + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("D_date", dates.size() + "");
                if (dates.size() < 8) {
                    c = Calendar.getInstance().getTime();
                    _8daysD = new Date(c.getTime() - 777600000L);
                    _1daysD = new Date(c.getTime() - 172800000L);
                    df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    today_date = df.format(_1daysD);
                    _8days = df.format(_8daysD);
                    from_this_to_this = "https://api.covid19api.com/country/" + countryname + "/status/confirmed?" +
                            "from=" + _8days + "&to=" +
                            today_date + "";
//                from_this_to_this = "https://api.covid19api.com/country/" + countryname + "/status/confirmed?" +
//                        "from=" + "2022-01-15" + "&to=" +
//                        "2022-01-23" + "";
                    Log.e("-2 day", today_date + "\n" + _8days + "\n" + from_this_to_this);
                    try {
                        weekly_date.setText(dates.get(1).toString().replace("T00:00:00Z", "") + "");

                        today_datetxt.setText(dates.get(dates.size() - 1).toString().replace("T00:00:00Z", "") + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    js_covid.execute();
                }
                if (v < 1) {
                    v++;
                    js_covid.execute();
                }


            }
            covid_pbr.setVisibility(View.GONE);
//            for (int i = 8; i < 15; i++) {
//                Linear_Regression_ALlo(i, "Day " + i);
//
//            }
            super.onPostExecute(s);
        }
        public void Show_line_label_cases() {
            line.setHasLines(true);
            line2.setHasLines(true);
        }

        public void Remove_line_label_cases() {
            line.setHasLines(false);
            line2.setHasLines(false);
        }
    }


    /**
     * Stock Data Graph
     */
    public void Open_Stcok_Web() {
        Intent intent = new Intent(getContext(), Stock_Market_WebPage.class);
        startActivity(intent);
    }

    LineChartData data_stock = new LineChartData();

    LineChartData data_stock2 = new LineChartData();

    public void Stock_Today() {
        Date c = Calendar.getInstance().getTime();
        //Date today = new Date(c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today_date = df.format(c);
        Stock_date = today_date;
    }

    public void Stock_Yesterday() {
        Date c = Calendar.getInstance().getTime();
        //7 means 6 days
        Date yesterday = new Date(c.getTime() - 86400000L);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String yesterday_date = df.format(yesterday);
        Stock_date = yesterday_date;
    }

    Date c = Calendar.getInstance().getTime();

    public String Stock_Week(long time) {
        Date today_date = new Date(c.getTime() - time);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String weekly_dates = df.format(today_date);
        Log.e("S_date", weekly_dates + "");
        return weekly_dates;
    }

    int limit_Stock_bg = 0;
    int limit_run = 0;
    List Stock_dates, Stock_dates2;

    class Stock_bg_process extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            stock_list_open.clear();
            stock_list_close.clear();
            yAxisValues_stock.clear();
            yAxisValues2_stock.clear();
            axisValues_stock.clear();
            Stock_dates = new ArrayList();
            Stock_dates2 = new ArrayList();
            stock_pbr.setVisibility(View.VISIBLE);
            Stock_dates.clear();
            Stock_dates2.clear();


            super.onPreExecute();
        }

        Line line, line2;

        public void show_line_label() {
            line.setHasLabels(true);

            line2.setHasLabels(true);

        }

        public void remove_line_label() {
            line.setHasLabels(false);

            line2.setHasLabels(false);

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(String s) {
            stock_pbr.setVisibility(View.GONE);

            //Collections.reverse(stock_list);
            //Collections.reverse(stock_list2);

            line = new Line(yAxisValues_stock); //Close
            line2 = new Line(yAxisValues2_stock); //Open

            try {
//                String[] axisData = {
//                        "1",
//                        "2",
//                        "3",
//                        "4",
//                        "5",
//                        "6",
//                        "7",
//
//                        "8",
//                        "9",
//                        "10",
//                        "11",
//                        "12",
//                        "13",
//                        "14"
//                };
                Collections.reverse(Stock_dates);
                Collections.reverse(stock_list_open);
                Collections.reverse(stock_list_close);
                String[] axisData = {
                        Stock_dates.get(0) + "",
                        Stock_dates.get(1) + "",
                        Stock_dates.get(2) + "",
                        Stock_dates.get(3) + "",
                        Stock_dates.get(4) + "",
                        Stock_dates.get(5) + "",
                        Stock_dates.get(6) + "",
                };


                //stock_list.add(i + "");

                //Collections.reverse(stock_list2);

//                stock_list.add("200");
//                stock_list.add("188");
//                stock_list.add("120");
//                stock_list.add("102");
//                stock_list.add("88");
//                stock_list.add("109");
//                stock_list.add("186");

                ArrayList<Integer> stock_int = new ArrayList<Integer>();
                stock_int.add(200);
                stock_int.add(100);
                stock_int.add(90);
                stock_int.add(220);
                stock_int.add(110);
                stock_int.add(10);
                int stock_min = Integer.MAX_VALUE;
                for (int i = 0; i < 6; i++) {
                    if (stock_int.get(i) < stock_min) {
                        stock_min = stock_int.get(i);
                    }
                }
                int stock_max = Integer.MIN_VALUE;
                for (int i = 0; i < 6; i++) {
                    if (stock_int.get(i) > stock_max) {
                        stock_max = stock_int.get(i);
                    }
                }
                //Log.e("Stock min", stock_min + "\n" + stock_max);

                final int min = stock_min;
                final int max = stock_max;
                final int random = new Random().nextInt((max - min) + 1) + min;
                int x = 0;
                int y = 0;
//                int average_stock = Average_Per_Day(
//                        Integer.parseInt(stock_list.get(0)),
//                        Integer.parseInt(stock_list.get(1)),
//                        Integer.parseInt(stock_list.get(2)),
//                        Integer.parseInt(stock_list.get(3)),
//                        Integer.parseInt(stock_list.get(4)),
//                        Integer.parseInt(stock_list.get(5)),
//                        Integer.parseInt(stock_list.get(6))
//                        );
//                int final_predicted = 0;
//
//                for (int i = 0; i < 6; i++) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        x=Integer.parseInt(stock_list.get(i));
//                        y = 2 * x;
//                        final_predicted = y - average_stock;
//                        stock_list2.add(final_predicted + "");
//                       // Log.e("Stock arrary", average_stock + "");
//                    }
//                }
                // Collections.rotate(stock_list2,4);


                try {
                    for (int i = 0; i < axisData.length; i++) {
                        axisValues_stock.add(i, new AxisValue(i).setLabel(axisData[i]));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < stock_list_close.size(); i++) {
                    yAxisValues_stock.add(new PointValue(i, Float.parseFloat(stock_list_close.get(i).trim() + "")));
                    //yAxisValues2_stock.add(new PointValue(i, Integer.parseInt(stock_list2.get(i).trim())));

                }
                for (int i = 0; i > stock_list_close.size(); i++) {
//                    yAxisValues2_stock.add(new PointValue(i + 7.0f, Float.parseFloat(stock_list2.get(i).trim())));
                    yAxisValues2_stock.add(new PointValue(i, Float.parseFloat(stock_list_open.get(i).trim())));


                }
                //  Log.e("stock", stock_list2.size() + "");
                yAxisValues2_stock.clear();
                //Future graph data
                for (int bb = 0; bb < stock_list_open.size(); bb++) {
//                    yAxisValues2_stock.add(new PointValue(bb + 7.0f, Float.parseFloat(stock_list2.get(bb))));
                    yAxisValues2_stock.add(new PointValue(bb, Float.parseFloat(stock_list_open.get(bb))));
                    //Log.e("temp", yAxisValues2 + "");
                }


                List lines = new ArrayList<String>();
                List lines2 = new ArrayList<String>();
                lines.add(line);
                lines.add(line2);
                data_stock.setLines(lines);
                //   data_stock.setLines(lines2);

                stock_graph.setLineChartData(data_stock);

                Axis axis = new Axis();
                axis.setValues(axisValues_stock);
                axis.setMaxLabelChars(2);
                Axis yAxis = new Axis();
                //yAxis.setValues(yAxisValues);
                data_stock.setAxisYLeft(yAxis.setAutoGenerated(false));
                data_stock.setAxisXBottom(axis);
                data_stock.setValueLabelTextSize(10);
                LineChartValueFormatter formatter = new SimpleLineChartValueFormatter(1);
                line.setFormatter(formatter);
                // line.setHasLabels(true);
                line.setHasLines(true);
                line.setFilled(true);
                line.setHasPoints(true);
                line.setPointColor(getContext().getResources().getColor(R.color.blue));
                line.setStrokeWidth(2);
                line.setPointRadius(5);
                line.setColor(getResources().getColor(R.color.blue2));


                line2.setFormatter(formatter);
                // line2.setHasLabels(true);
                line2.setHasLines(true);
                line2.setFilled(true);
                line2.setHasPoints(true);
                line2.setPointColor(getContext().getResources().getColor(R.color.red));
                line2.setColor(getResources().getColor(R.color.redish));

                line2.setStrokeWidth(2);
                line2.setPointRadius(5);


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }

        int a = 0, k = 0;


        @Override
        protected String doInBackground(Void... voids) {
            String dummy = "";
            if (limit_run < 1) {
                try {
                    URL url = new URL(Stock_Link_New);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    String result = sb.toString();
                    JSONObject array;
                    try {
                        array = new JSONObject(result);
                        JSONObject object;
                        JSONObject object1 = array.getJSONObject("Time Series (Daily)");
                        JSONArray arr = object1.names();


                        for (int i = 0; i < 7; i++) {
                            String date_in_db = arr.getString(arr.length() - 100 + i) + "";
                            Stock_dates.add(date_in_db.substring(8, 10));
                            Stock_dates2.add(date_in_db);
                            JSONObject object2 = object1.getJSONObject(date_in_db);
                            Map<String, String> map = null;
                            for (int j = 0; j < object2.length(); j++) {
                                map = new HashMap<String, String>();
                                Iterator<?> iter = object2.keys();
                                while (iter.hasNext()) {
                                    String key = (String) iter.next();
                                    String value = object2.getString(key);
                                    map.put(key, value);

                                }


                                a++;


                                //stock_list2.add(map.get("close").trim() + "");


                                // Log.e("Date " + count, yAxisData[count] + "");
                            }
                            stock_list_close.add(Float.parseFloat(map.get("4. close").trim()) + "");
                            stock_list_open.add(Float.parseFloat(map.get("1. open").trim()) + "");

                            Log.e("S data", "Date: " + date_in_db + "\t" + map.get("4. close") + "\t" + map.get("1. open"));
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Collections.reverse(Stock_dates2);
                                weekly_date_stock.setText(Stock_dates2.get(0) + "");
                                today_datetxt_stock.setText(Stock_dates2.get(6) + "");
                                stock_txt.setText("Stock Market " + Stock_Link_New_2.toUpperCase() + " (USD)");
                            }
                        });
                    } catch (JSONException e) {
                        Log.e("S err1", e.getLocalizedMessage() + "");

                        //No Data Exception
                        //Recalling with new Date day-1

                        Stock_Yesterday();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (limit_Stock_bg < 1) {
                                    limit_run = 0;
                                    new Stock_bg_process().execute();
                                }
                                limit_Stock_bg++;
                            }

                        });


                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    Log.e("S err2", e.getLocalizedMessage() + "");
                }

            }
            limit_run++;
            return dummy;
        }
    }


    /**
     * COVID-19 DATA AND GRAPH
     */
    LineChartData data = new LineChartData();
    LineChartData data2 = new LineChartData();
    List dates;

    int d = 0;
    List temp;


    public int Linear_Regression_ALlo(int days, String day) {
        //Formula
        //y = a + bx
        //b = n(Sum xy) - (Sum x) (Sum y)
        //a = n(Sum x^2) - (Sum x)^2
        int Sum_x = 0, Sum_y = 0, Sum_xy = 0;
        int x = 0, y = 0, a = 0, b = 0, n = 0;
        int Sum_xSq = 0, Sum_ySq = 0;

        int Sq_x = 0, Sq_y = 0, Sq_xy = 0;

        x = days; //The Day to be Predicted
        n = 7;//Total Days Given
        for (int i = 0; i < temp.size(); i++) {
            Sum_y += Integer.parseInt(temp.get(i) + "");
            Sum_xy += (i + 1) * Integer.parseInt(temp.get(i) + "");
        }

        Sum_x = 28;
        Sum_xSq = 140;
        //   Sum_y=Sum_y-37289;
        // Sum_x=Sum_x-7;


        b = ((n * Sum_xy) - (Sum_x * Sum_y)) / ((n * Sum_xSq) - (Sum_x * Sum_x));
        a = ((Sum_y) - (b * Sum_x)) / n;
        y = a + (b * x);
        //y = 5327
        //y = 7258 - b

        if (y < 0) {
            y = y * (-1);
        }
        Log.e("Linear_Regrission", day + " = " + y);

        return y;
    }

    public class DateIncrementer {
        public String addOneDay(String date) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                d++;
                Log.e("Date: ", LocalDate.parse(date).plusDays(d).toString().replace("2022-02-", "") + "");
                return LocalDate.parse(date).plusDays(d).toString().replace("2022-02-", "");
            }
            return null;
        }
    }

    class json_data_date_wise extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            dates = new ArrayList();
            dates.clear();
            cases_array.clear();
            yAxisValues.clear();
            yAxisValues2.clear();
            axisValues.clear();
            covid_pbr.setVisibility(View.VISIBLE);
            js_covid = new json_data_date_wise();
            temp = new ArrayList();
            super.onPreExecute();
        }

        int a = 0, k = 0;

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String s) {
            Line line = new Line(yAxisValues);
            Line line2 = new Line(yAxisValues2);

            Log.e("cases", cases_array.size() + "\n" + v);
            try {
                weekly_date.setText(dates.get(1).toString().replace("T00:00:00Z", "") + "");

                today_datetxt.setText(dates.get(dates.size() - 1).toString().replace("T00:00:00Z", "") + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (cases_array.size() > 7 && cases_array.size() < 15) {
                Log.e("if", "ASA");
                try {
                    DateIncrementer dateIncrementer = new DateIncrementer();
                    String datess = dates.get(7).toString().replace("T00:00:00Z", "");


                    String[] axisData = {
                            dates.get(1).toString().replace("2022-01-", "").replace("T00:00:00Z", "")
                                    .replace("2022-02-", ""),
                            dates.get(2).toString().replace("2022-01-", "").replace("T00:00:00Z", "")
                                    .replace("2022-02-", ""),
                            dates.get(3).toString().replace("2022-01-", "").replace("T00:00:00Z", "")
                                    .replace("2022-02-", ""),
                            dates.get(4).toString().replace("2022-01-", "").replace("T00:00:00Z", "")
                                    .replace("2022-02-", ""),
                            dates.get(5).toString().replace("2022-01-", "").replace("T00:00:00Z", "")
                                    .replace("2022-02-", ""),
                            dates.get(6).toString().replace("2022-01-", "").replace("T00:00:00Z", "")
                                    .replace("2022-02-", ""),
                            dates.get(7).toString().replace("2022-01-", "").replace("T00:00:00Z", "")
                                    .replace("2022-02-", ""),

                            new DateIncrementer().addOneDay(datess + "").replace("2022-01-", "")
                                    .replace("2022-02-", ""),
                            new DateIncrementer().addOneDay(datess + "").replace("2022-01-", "")
                                    .replace("2022-02-", ""),
                            new DateIncrementer().addOneDay(datess + "").replace("2022-01-", "")
                                    .replace("2022-02-", ""),
                            new DateIncrementer().addOneDay(datess + "").replace("2022-01-", "")
                                    .replace("2022-02-", ""),
                            new DateIncrementer().addOneDay(datess + "").replace("2022-01-", "")
                                    .replace("2022-02-", ""),
                            new DateIncrementer().addOneDay(datess + "").replace("2022-01-", "")
                                    .replace("2022-02-", ""),
                            new DateIncrementer().addOneDay(datess + "").replace("2022-01-", "")
                                    .replace("2022-02-", ""),


//                            "2",
//                            "3",
//                            "4",
//                            "5",
//                            "6",
//                            "7",

//                            "8",
//                            "9",
//                            "10",
//                            "11",
//                            "12",
//                            "13",
//                            "14"
                    };

                    d = 0;
                    try {
                        for (int i = 0; i < axisData.length; i++) {
                            axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < yAxisData[0]; i++) {

                        if (cases_array.size() < 6 || cases_array.size() > 15) {
                            // yAxisValues.clear();
//                        cases_array.clear();
//                        axisValues.clear();
                            Date c = Calendar.getInstance().getTime();
                            Date _8daysD = new Date(c.getTime() - 691200000L - 86400000L);
                            Date _1daysD = new Date(c.getTime() - 86400000L);
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String today_date = df.format(_1daysD);
                            String _8days = df.format(_8daysD);
                            from_this_to_this = "https://api.covid19api.com/country/" + countryname + "/status/confirmed?" +
                                    "from=" + _8days + "&to=" +
                                    today_date + "";

                            Log.e("-1 day in", today_date + "\n" + _8days + "\n" + from_this_to_this);
                            weekly_date.setText(dates.get(0) + "");
                            today_datetxt.setText(dates.get(dates.size()) + "");
                            if (k < 1) {

                                //  js_covid.execute();
                            }
                            k++;
                        } else {
                            try {

                                yAxisValues.add(new PointValue(i, Integer.parseInt(cases_array.get(i).trim())));
                                yAxisValues2.add(new PointValue(i, Integer.parseInt(cases_array.get(i).trim())));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }


                        }

                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                List lines = new ArrayList<String>();
                List lines2 = new ArrayList<String>();

                lines.add(line);

                //Future Data
                lines.add(line2);

                //lines2.add(line2);//For Prediction

                data.setLines(lines);

                //Future Prediction
                // data2.setLines(lines2);

                int day1 = 0,
                        day2 = 0,
                        day3 = 0,
                        day4 = 0,
                        day5 = 0,
                        day6 = 0,
                        day7 = 0,
                        day8 = 0;
                try {
                    Log.e("json", js_covid.getStatus() + "");
                    if (cases_array.size() > 7) {
                        try {
                            day1 = Integer.parseInt(cases_array.get(0));
                            day2 = Integer.parseInt(cases_array.get(1));
                            day3 = Integer.parseInt(cases_array.get(2));
                            day4 = Integer.parseInt(cases_array.get(3));
                            day5 = Integer.parseInt(cases_array.get(4));
                            day6 = Integer.parseInt(cases_array.get(5));
                            day7 = Integer.parseInt(cases_array.get(6));
                            day8 = Integer.parseInt(cases_array.get(7));
                        } catch (NumberFormatException eee) {
                            eee.printStackTrace();
                        }
                        //Each day difference Week
                        int a, b, c, d, e, f, g;
                        a = day2 - day1;//1
                        b = day3 - day2;//2
                        c = day4 - day3;//3
                        d = day5 - day4;//4
                        e = day6 - day5;//5
                        f = day7 - day6;//6
                        g = day8 - day7;//7
                        if (a < 0) {
                            a = a * -1;
                        }
                        if (b < 0) {
                            b = b * -1;
                        }
                        if (c < 0) {
                            c = c * -1;
                        }
                        if (d < 0) {
                            d = d * -1;
                        }
                        if (e < 0) {
                            e = e * -1;
                        }
                        if (f < 0) {
                            f = f * -1;
                        }
                        if (g < 0) {
                            g = g * -1;
                        }
                        int average_cases = Average_Per_Day(a, b, c, d, e, f, g);
                        cases_graph.setTooltipText("Average Cases Per Day \n" + average_cases);
                        Log.e("values of ",
                                a + "\n"
                                        + b + "\n"
                                        + c + "\n"
                                        + d + "\n"
                                        + e + "\n"
                                        + f + "\n"
                                        + g + "\n"
                        );


                        //1 Week Actual Data
                        temp.add(a + "");//1
                        temp.add(b + "");//2
                        temp.add(c + "");//3
                        temp.add(d + "");//4
                        temp.add(e + "");//5
                        temp.add(f + "");//6
                        temp.add(g + "");//7


                        yAxisValues.clear();
                        for (int aa = 0; aa < temp.size(); aa++) {
                            yAxisValues.add(new PointValue(aa, Integer.parseInt(temp.get(aa) + "")));

                        }
                        yAxisValues2.clear();
                        //Future Prediction
                        List temp2 = new ArrayList();
                        int x = 0;
                        int y = 0;
                        int final_predicted = 0;
                        if (mycovidalgo) {
                            for (int i = 0; i < 7; i++) {
                                x = Integer.parseInt(temp.get(i) + "");
                                y = 2 * x;
                                final_predicted = y - average_cases;
                                if (final_predicted < 0) {
                                    final_predicted = final_predicted * (-1);
                                }

//                            Collections.reverse(temp2);


                                temp2.add(final_predicted + "");

                                Log.e("Algo", final_predicted + "");
                            }

                            Collections.reverse(temp2);
                        } else {

                            //Future graph data
                            for (int j = 1; j < 8; j++) {
                                temp2.add(Linear_Regression_ALlo(j, "Days " + j));
                            }

                        }

                        for (int bb = 0; bb < temp2.size(); bb++) {
                            yAxisValues2.add(new PointValue(bb + 7.0f, Integer.parseInt(temp2.get(bb) + "")));
                            // Log.e("temp", yAxisValues2 + "");
                        }

//a,b,c,d,e,f,g
                        if (g > f && g > e && g > d) {
                            line.setColor(getResources().getColor(R.color.red));
                            Log.e("1st", "true");


                        } else if (f > e && f > d && f > d) {
                            Log.e("2nd", "true");
                            line.setColor(getResources().getColor(R.color.red));

                        } else if (e > d && e > c && e > b) {
                            Log.e("3rd", "true");

                            line.setColor(getResources().getColor(R.color.red));

                        } else {
                            line.setColor(getResources().getColor(R.color.green));

                        }

                        //Future Prediction
                        if (g > f

                        ) {
                            line2.setColor(getResources().getColor(R.color.redish));

                        } else {
                            line2.setColor(getResources().getColor(R.color.greenish));

                        }
                    } else {
                        change_date_9days();
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                cases_graph.setLineChartData(data);
//            cases_graph.setLineChartData(data2);

                Axis axis = new Axis();
                axis.setValues(axisValues);
                axis.setMaxLabelChars(1);
                Axis yAxis = new Axis();
                //yAxis.setValues(yAxisValues);
                data.setAxisYLeft(yAxis.setAutoGenerated(false));
                data.setAxisXBottom(axis);
                data.setValueLabelTextSize(10);

                //Future Data

                data2.setAxisYLeft(yAxis.setAutoGenerated(false));
                data2.setAxisXBottom(axis);
                data2.setValueLabelTextSize(10);
                try {
                    line.setHasLabels(true);
                    line.setHasLines(true);
                    line.setFilled(true);
                    line.setHasPoints(true);
                    line.setPointColor(getContext().getResources().getColor(R.color.yellow));
                    line.setStrokeWidth(2);
                    line.setPointRadius(5);

                    //Future Graph
                    line2.setHasLabels(true);
                    line2.setHasLines(true);
                    line2.setFilled(true);
                    line2.setHasPoints(true);
                    line2.setPointColor(getContext().getResources().getColor(R.color.graysh2));
                    line2.setStrokeWidth(2);
                    line2.setPointRadius(5);
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }

            } else {
                Date c = Calendar.getInstance().getTime();
                Date _8daysD = new Date(c.getTime() - 691200000L);
                Date _1daysD = new Date(c.getTime() - 86400000L);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String today_date = df.format(_1daysD);
                String _8days = df.format(_8daysD);
                from_this_to_this = "https://api.covid19api.com/country/" + countryname + "/status/confirmed?" +
                        "from=" + _8days + "&to=" +
                        today_date + "";
//                from_this_to_this = "https://api.covid19api.com/country/" + countryname + "/status/confirmed?" +
//                        "from=" + "2022-01-15" + "&to=" +
//                        "2022-01-23" + "";
                Log.e("-1 day2", today_date + "\n" + _8days + "\n" + from_this_to_this);
                try {
                    weekly_date.setText(dates.get(1).toString().replace("T00:00:00Z", "") + "");

                    today_datetxt.setText(dates.get(dates.size() - 1).toString().replace("T00:00:00Z", "") + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("D_date", dates.size() + "");
                if (dates.size() < 8) {
                    c = Calendar.getInstance().getTime();
                    _8daysD = new Date(c.getTime() - 777600000L);
                    _1daysD = new Date(c.getTime() - 172800000L);
                    df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    today_date = df.format(_1daysD);
                    _8days = df.format(_8daysD);
                    from_this_to_this = "https://api.covid19api.com/country/" + countryname + "/status/confirmed?" +
                            "from=" + _8days + "&to=" +
                            today_date + "";
//                from_this_to_this = "https://api.covid19api.com/country/" + countryname + "/status/confirmed?" +
//                        "from=" + "2022-01-15" + "&to=" +
//                        "2022-01-23" + "";
                    Log.e("-2 day", today_date + "\n" + _8days + "\n" + from_this_to_this);
                    try {
                        weekly_date.setText(dates.get(1).toString().replace("T00:00:00Z", "") + "");

                        today_datetxt.setText(dates.get(dates.size() - 1).toString().replace("T00:00:00Z", "") + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    js_covid.execute();
                }
                if (v < 1) {
                    v++;
                    js_covid.execute();
                }


            }
            covid_pbr.setVisibility(View.GONE);
//            for (int i = 8; i < 15; i++) {
//                Linear_Regression_ALlo(i, "Day " + i);
//
//            }


            super.onPostExecute(s);
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        protected String doInBackground(Void... voids) {


            try {
                URL url = new URL(from_this_to_this);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                JSONArray array;
                try {
                    array = new JSONArray(result);
                    JSONObject object;
                    int count = 0;
                    Map<String, String> map = null;
                    for (int i = 0; i < array.length(); i++) {

                        object = new JSONObject(array.getJSONObject(i).toString());
                        map = new HashMap<String, String>();
                        Iterator<?> iter = object.keys();
                        while (iter.hasNext()) {
                            String key = (String) iter.next();
                            String value = object.getString(key);
                            map.put(key, value);


                        }


                        a++;


                        cases_array.add(map.get("Cases").trim() + "");
                        // yAxisData[1] = Integer.parseInt(map.get("Cases").trim());
                        count++;
                        dates.add("" + map.get("Date"));
                        //  Log.e("bg", dates + "");

                    }
                    // Log.e("Date " + count, yAxisData[count] + "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Service is down for maintenance, Please try again later", Toast.LENGTH_SHORT).show();
                        Log.e("date err", e.getLocalizedMessage());
                    }
                });
            }


            return null;
        }
    }

    public int Average_Per_Day(int a, int b, int c, int d, int e, int f, int g) {
        int Average = 0;
        Average = (a + b + c + d + e + f + g) / 7;

        return Average;
    }

    public void change_date_9days() {
        Date c = Calendar.getInstance().getTime();
        Date _9daysD = new Date(c.getTime() - 691200000L - 86400000L - 86400000L);
        Date _1daysD = new Date(c.getTime() - 86400000L);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today_date = df.format(_1daysD);
        String _9days = df.format(_9daysD);
        from_this_to_this = "https://api.covid19api.com/country/" + countryname + "/status/confirmed?" +
                "from=" + _9days + "&to=" +
                today_date + "";
        Log.e("-2 day in", today_date + "\n" + _9days + "\n" + from_this_to_this);
        weekly_date.setText(dates.get(0).toString().replace("T00:00:00Z", "") + "");
        today_datetxt.setText(dates.get(dates.size() - 1).toString().replace("T00:00:00Z", "") + "");
        js_covid.execute();
    }
    /**COVID-19 DATA AND GRAPH*/

}