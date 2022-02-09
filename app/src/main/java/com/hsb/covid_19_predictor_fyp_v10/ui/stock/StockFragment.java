package com.hsb.covid_19_predictor_fyp_v10.ui.stock;

import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.hsb.covid_19_predictor_fyp_v10.R;
import com.hsb.covid_19_predictor_fyp_v10.Stock_Market_WebPage;
import com.hsb.covid_19_predictor_fyp_v10.databinding.FragmentStockBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

import lecho.lib.hellocharts.formatter.LineChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class StockFragment extends Fragment {
    private StockViewModel dashboardViewModel;
    private FragmentStockBinding binding;

    //Stock
    LineChartView stock_graph, stock_graph_prediction, stock_covid_graph;
    List<String> stock_list_open;
    List<String> stock_list_close;
    List<String> stock_list_prediction;
    List yAxisValues_stock_prediction;
    List yAxisValues_stock_covid;
    List<String> Dates_prediction;
    List yAxisValues_stock;
    List yAxisValues2_stock;
    List axisValues_stock;
    List axisValues_stock_prediction;
    int[] yAxisData_stock;
    String Stock_Link = "http://api.marketstack.com/v1/eod?access_key=38aa62e0cb3db5a9179b8a48bf79e40b&symbols=AAPL&date_from=";
    String Stock_Link_New = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=psx&apikey=TYV31X78NKKL4LTDa";
    String Stock_Link_New_1 = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=";
    String Stock_Link_New_2 = "baba";
    String Stock_Link_New_3 = "&apikey=TYV31X78NKKL4LTDa";
    TextView stock_txt;
    ProgressBar stock_pbr;
    String Stock_date;
    TextView weekly_date_stock, today_datetxt_stock;
    boolean mycovidalgo;

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    int v = 0;
    SharedPreferences preferences;
    List covid_cases;
    boolean done = false;
    Stock_bg_process stock_bg_process;
    boolean pkr;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(StockViewModel.class);

        binding = FragmentStockBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean theme_dark = preferences.getBoolean("theme_dark", false);
        pkr = preferences.getBoolean("pkr", false);
        mycovidalgo = preferences.getBoolean("myalgo", false);
        ScrollView main_L;
        main_L = root.findViewById(R.id.main_L);
        if (theme_dark) {
            main_L.setBackgroundColor(getContext().getResources().getColor(R.color.theme_dark));
        }
        stock_txt = root.findViewById(R.id.stock_txt);
        today_datetxt_stock = root.findViewById(R.id.today_date_stock);
        weekly_date_stock = root.findViewById(R.id.week_date_stock);
        covid_cases = new ArrayList();

        stock_pbr = root.findViewById(R.id.stock_pbr);
        Date c = Calendar.getInstance().getTime();
//        Date _7daysD = new Date(c.getTime() - 604800000L);
        Date _7daysD = new Date(c.getTime() - 691200000L);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today_date = df.format(c);
        String _7days = df.format(_7daysD);

        /**Stock Data Graph*/

        stock_graph = root.findViewById(R.id.chart_stock);
        stock_covid_graph = root.findViewById(R.id.chart_stock_covid);
        stock_graph_prediction = root.findViewById(R.id.chart_stock_prediction);
        axisValues_stock = new ArrayList();
        axisValues_stock_prediction = new ArrayList();
        yAxisValues_stock = new ArrayList();
        yAxisValues2_stock = new ArrayList();
        stock_list_open = new ArrayList();
        stock_list_close = new ArrayList();
        stock_list_prediction = new ArrayList();
        Dates_prediction = new ArrayList();
        yAxisValues_stock_prediction = new ArrayList();
        yAxisValues_stock_covid = new ArrayList();

        stock_bg_process = new Stock_bg_process();
        Stock_Today();

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

        final int[] lable_reset_prediction = {0};
        stock_graph_prediction.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //Open_Stcok_Web();
                try {
                    lable_reset_prediction[0]++;

                    if (lable_reset_prediction[0] == 1) {
                        stock_bg_process.show_line_label_2();
                        Stock_Covid_Cases(getContext());
                    } else {
                        lable_reset_prediction[0] = 0;
                        stock_bg_process.remove_line_label_2();

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

        //Covid-19 Cases

        Handler handler = new Handler();

        final int delay = 1000; // 1000 milliseconds == 1 second

        handler.postDelayed(new Runnable() {
            public void run() {
                done = Stock_Covid_Cases(getContext());
                if (done) {
                    Log.e("Done nigga", "Kabootar\n" + covid_cases);
                    stock_bg_process.execute();

                } else {
                    Log.e("Done nigga", "Checking again in 1 sec");
                    handler.postDelayed(this, delay);
                }

            }
        }, 100);

        return root;
    }


    public boolean Stock_Covid_Cases(Context context) {
        //Covid-19 Data
        boolean done = false;
        covid_cases = new ArrayList();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        for (int i = 0; i < 7; i++) {
            String a = preferences.getString("day" + i, "0");
            covid_cases.add(a + "");
        }
        if (!covid_cases.get(0).toString().equals("0")) {
            done = true;
            Log.e("Stock_Covid_Cases", covid_cases + "");
        }
        return done;
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
    LineChartData data_stock3 = new LineChartData();

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
        // Log.e("S_date", weekly_dates + "");
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
            stock_list_prediction.clear();
            yAxisValues_stock.clear();
            yAxisValues2_stock.clear();
            yAxisValues_stock_prediction.clear();
            yAxisValues_stock_covid.clear();
            axisValues_stock.clear();
            axisValues_stock_prediction.clear();
            Stock_dates = new ArrayList();
            Stock_dates2 = new ArrayList();
            stock_pbr.setVisibility(View.VISIBLE);
            Stock_dates.clear();
            Stock_dates2.clear();


            super.onPreExecute();
        }

        Line line, line2;
        Line line_prediction;
        Line line_covid;

        public void show_line_label() {
            line.setHasLabels(true);

            line2.setHasLabels(true);

        }

        public void show_line_label_2() {
            line_prediction.setHasLabels(true);
        }

        public void remove_line_label() {
            line.setHasLabels(false);

            line2.setHasLabels(false);

        }

        public void remove_line_label_2() {
            line_prediction.setHasLabels(false);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(String s) {
            stock_pbr.setVisibility(View.GONE);

            //Collections.reverse(stock_list);
            //Collections.reverse(stock_list2);

            line = new Line(yAxisValues_stock); //Close
            line2 = new Line(yAxisValues2_stock); //Open
            line_prediction = new Line(yAxisValues_stock_prediction); //Prediction
            line_covid = new Line(yAxisValues_stock_covid); //Covid

            try {

                Collections.reverse(Stock_dates);
                Collections.reverse(stock_list_open);
                Collections.reverse(stock_list_close);
                Collections.reverse(stock_list_prediction);
                String[] axisData = {
                        Stock_dates.get(0) + "",
                        Stock_dates.get(1) + "",
                        Stock_dates.get(2) + "",
                        Stock_dates.get(3) + "",
                        Stock_dates.get(4) + "",
                        Stock_dates.get(5) + "",
                        Stock_dates.get(6) + "",
                };
                String datess = Stock_dates2.get(6) + "";
                Log.e("Stock_Date", datess + "");
                String[] axisData2 = {
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

                };


                try {
                    for (int i = 0; i < axisData.length; i++) {
                        axisValues_stock.add(i, new AxisValue(i).setLabel(axisData[i]));
                        axisValues_stock_prediction.add(i, new AxisValue(i).setLabel(axisData2[i]));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < stock_list_close.size(); i++) {
                    float close = Float.parseFloat(stock_list_close.get(i).trim() + "");
                    if (pkr) {
                        close = close * 174.55f;
                    }
                    yAxisValues_stock.add(new PointValue(i, close));
                }
                for (int i = 0; i > stock_list_close.size(); i++) {
                    float open = Float.parseFloat(stock_list_open.get(i).trim() + "");
                    if (pkr) {
                        open = open * 174.55f;
                    }
                    yAxisValues2_stock.add(new PointValue(i, open));


                }
                //  Log.e("stock", stock_list2.size() + "");
                // yAxisValues2_stock.clear();
                //Future graph data

                List lines = new ArrayList<String>();
                List lines2 = new ArrayList<String>();
                List lines3 = new ArrayList<String>();
                lines.add(line);
                lines.add(line2);

                lines3.add(line);
                lines3.add(line_covid);

                lines2.add(line_prediction);
                data_stock.setLines(lines);
                data_stock2.setLines(lines2);
                data_stock3.setLines(lines3);
                float day1 = 0,
                        day2 = 0,
                        day3 = 0,
                        day4 = 0,
                        day5 = 0,
                        day6 = 0,
                        day7 = 0,
                        day8 = 0;
                try {
                    try {
                        day1 = Float.parseFloat(stock_list_close.get(0));
                        day2 = Float.parseFloat(stock_list_close.get(1));
                        day3 = Float.parseFloat(stock_list_close.get(2));
                        day4 = Float.parseFloat(stock_list_close.get(3));
                        day5 = Float.parseFloat(stock_list_close.get(4));
                        day6 = Float.parseFloat(stock_list_close.get(5));
                        day7 = Float.parseFloat(stock_list_close.get(6));
                        day8 = Float.parseFloat(stock_list_close.get(6));
                    } catch (NumberFormatException eee) {
                        eee.printStackTrace();
                    }
                    //Each day difference Week
                    float a, b, c, d, e, f, g;
                    a = day2 - day1;//1
                    b = day3 - day2;//2
                    c = day4 - day3;//3
                    d = day5 - day4;//4
                    e = day6 - day5;//5
                    f = day7 - day6;//6
                    g = day8 - day7;//7Stock_dates2
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
                    int average_cases = Average_Per_Day((int) a, (int) b, (int) c, (int) d, (int) e, (int) f, (int) g);

                    for (int bb = 0; bb < stock_list_open.size(); bb++) {
                        float open = Float.parseFloat(stock_list_open.get(bb).trim() + "");
                        if (pkr) {
                            open = open * 174.55f;
                        }
                        yAxisValues2_stock.add(new PointValue(bb, open));
                        //Log.e("temp", yAxisValues2 + "");
                    }
                    //Future Prediction
                    List temp2 = new ArrayList();
                    float x = 0;
                    float y = 0;
                    float final_predicted = 0;
                    if (1 == 1) {
                        for (int i = 0; i < 7; i++) {
                            x = Float.parseFloat(stock_list_close.get(i) + "");
                            y = 2 * x;
                            final_predicted = y - average_cases;
                            if (final_predicted < 0) {
                                final_predicted = final_predicted * (-1);
                            }

//                            Collections.reverse(temp2);


                            temp2.add(final_predicted + "");

                            Log.e("Stock_Algo", final_predicted + "");
                        }

                        Collections.reverse(temp2);
                    } else {
                    }

                    for (int bb = 0; bb < temp2.size(); bb++) {
                        float pred = Float.parseFloat(temp2.get(bb) + "");
                        if (pkr) {
                            pred = pred * 174.55f;
                        }
                        yAxisValues_stock_prediction.add(new PointValue(bb, pred));
                        // Log.e("temp", yAxisValues2 + "");
                    }
                    for (int i = 0; i < covid_cases.size(); i++) {
                        if (pkr) {
                            float z = Float.parseFloat(covid_cases.get(i) + "") / 100;
                            yAxisValues_stock_covid.add(new PointValue(i, z/2));
                        } else {
                            float z = Float.parseFloat(covid_cases.get(i) + "") / 10000;
                            yAxisValues_stock_covid.add(new PointValue(i, z / 3));
                        }
                    }


                    stock_graph.setLineChartData(data_stock);
                    stock_graph_prediction.setLineChartData(data_stock2);

                    stock_covid_graph.setLineChartData(data_stock3);

                    Axis axis = new Axis();
                    Axis axis2 = new Axis();
                    axis.setValues(axisValues_stock);
                    axis2.setValues(axisValues_stock_prediction);
                    axis.setMaxLabelChars(2);
                    axis2.setMaxLabelChars(2);
                    Axis yAxis = new Axis();
                    //yAxis.setValues(yAxisValues);
                    data_stock.setAxisYLeft(yAxis.setAutoGenerated(false));
                    data_stock.setAxisXBottom(axis);
                    data_stock.setValueLabelTextSize(10);

                    data_stock2.setAxisYLeft(yAxis.setAutoGenerated(false));
                    data_stock2.setAxisXBottom(axis2);
                    data_stock2.setValueLabelTextSize(10);

                    data_stock3.setAxisYLeft(yAxis.setAutoGenerated(false));
                    data_stock3.setAxisXBottom(axis);
                    data_stock3.setValueLabelTextSize(10);

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

                    line_prediction.setFormatter(formatter);
                    // line2.setHasLabels(true);
                    line_prediction.setHasLines(true);
                    line_prediction.setFilled(true);
                    line_prediction.setHasPoints(true);
                    line_prediction.setPointColor(getContext().getResources().getColor(R.color.green));
                    line_prediction.setColor(getResources().getColor(R.color.greenish));
                    line_prediction.setStrokeWidth(2);
                    line_prediction.setPointRadius(5);


                    LineChartValueFormatter formatter1 = new SimpleLineChartValueFormatter(5);
                    line_covid.setFormatter(formatter1);
                    //line_covid.setHasLabels(true);
                    line_covid.setHasLines(true);
                    line_covid.setFilled(true);
                    line_covid.setHasPoints(true);
                    line_covid.setPointColor(getContext().getResources().getColor(R.color.yellow));
                    line_covid.setColor(getResources().getColor(R.color.yellowish));
                    line_covid.setStrokeWidth(2);
                    line_covid.setPointRadius(5);


                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (Resources.NotFoundException e) {
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

                            //  Log.e("S data", "Date: " + date_in_db + "\t" + map.get("4. close") + "\t" + map.get("1. open"));
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Collections.reverse(Stock_dates2);
                                weekly_date_stock.setText(Stock_dates2.get(0) + "");
                                today_datetxt_stock.setText(Stock_dates2.get(6) + "");
                                if (pkr) {
                                    stock_txt.setText("Stock Market " + Stock_Link_New_2.toUpperCase() + " (PKR)");

                                } else {
                                    stock_txt.setText("Stock Market " + Stock_Link_New_2.toUpperCase() + " (USD)");
                                }
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

    public class DateIncrementer {
        public String addOneDay(String date) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                d++;
                //Log.e("Date: ", LocalDate.parse(date).plusDays(d).toString().replace("2022-02-", "") + "");
                return LocalDate.parse(date).plusDays(d).toString().replace("2022-02-", "");
            }
            return null;
        }
    }

    public int Average_Per_Day(int a, int b, int c, int d, int e, int f, int g) {
        int Average = 0;
        Average = (a + b + c + d + e + f + g) / 7;

        return Average;
    }

    /**COVID-19 DATA AND GRAPH*/

}