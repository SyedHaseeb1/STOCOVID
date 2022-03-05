package com.hsb.covid_19_predictor_fyp_v10.ui.cryptos;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import com.hsb.covid_19_predictor_fyp_v10.databinding.FragmentCryptoBinding;

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

public class Crypto extends Fragment {
    private CryptoViewModel dashboardViewModel;
    private FragmentCryptoBinding binding;

    //BTC
    LineChartView crypto_graph;
    List<String> crypto_list_open;
    List<String> crypto_list_close;
    List yAxisValues_crypto;
    List yAxisValues2_crypto;
    List axisValues_crypto;
    int[] yAxisData_crypto;
    String Crypto_Link = "http://api.marketstack.com/v1/eod?access_key=38aa62e0cb3db5a9179b8a48bf79e40b&symbols=AAPL&date_from=";
    String Crypto_Link_New = "https://www.alphavantage.co/query?function=DIGITAL_CURRENCY_DAILY&symbol=btc&market=pkr&apikey=TYV31X78NKKL4LTD";


    String Crypto_Link_New_1 = "https://www.alphavantage.co/query?function=DIGITAL_CURRENCY_DAILY&symbol=";
    String BTC = "btc";
    String ETH = "eth";
    String SHIB = "shib";
    String BNB = "bnb";
    String ADA = "ada";
    String Crypto_Link_New_3 = "&market=pkr&apikey=8ST8QEFMEETC54NQ";


    TextView crypto_txt;
    TextView eth_txt;
    TextView shib_txt;
    TextView bnb_txt;
    ProgressBar crypto_pbr;
    ProgressBar eth_pbr;
    ProgressBar shib_pbr;
    ProgressBar bnb_pbr;
    String Crypto_date;
    TextView weekly_date_crypto, today_datetxt_crypto;
    TextView weekly_date_eth, today_datetxt_eth;
    TextView weekly_date_shib, today_datetxt_shib;
    TextView weekly_date_bnb, today_datetxt_bnb;

    //ETH
    LineChartView eth_graph;
    List<String> eth_list_open;
    List<String> eth_list_close;
    ETH_bg_process eth_bg_process;

    //SHIB
    LineChartView shib_graph;
    List<String> shib_list_open;
    List<String> shib_list_close;
    SHIB_bg_process shib_bg_process;

    //BNB
    LineChartView bnb_graph;
    List<String> bnb_list_open;
    List<String> bnb_list_close;


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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(CryptoViewModel.class);

        binding = FragmentCryptoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean theme_dark = preferences.getBoolean("theme_dark", false);
        ScrollView main_L;
        main_L = root.findViewById(R.id.main_L);
        if (theme_dark) {
            main_L.setBackgroundColor(getContext().getResources().getColor(R.color.theme_dark));
        }
        crypto_txt = root.findViewById(R.id.crypto_txt);
        today_datetxt_crypto = root.findViewById(R.id.today_date_crypto);
        weekly_date_crypto = root.findViewById(R.id.week_date_crypto);

        //ETH
        eth_txt = root.findViewById(R.id.eth_txt);
        today_datetxt_eth = root.findViewById(R.id.today_date_eth);
        weekly_date_eth = root.findViewById(R.id.week_date_eth);
        eth_pbr = root.findViewById(R.id.eth_pbr);
        eth_graph = root.findViewById(R.id.eth_chart);
        eth_list_open = new ArrayList();
        eth_list_close = new ArrayList();

        //ETH
        shib_txt = root.findViewById(R.id.shib_txt);
        today_datetxt_shib = root.findViewById(R.id.today_date_shib);
        weekly_date_shib = root.findViewById(R.id.week_date_shib);
        shib_pbr = root.findViewById(R.id.shib_pbr);
        shib_graph = root.findViewById(R.id.shib_chart);
        shib_list_open = new ArrayList();
        shib_list_close = new ArrayList();


        crypto_pbr = root.findViewById(R.id.crypto_pbr);
        Date c = Calendar.getInstance().getTime();
//        Date _7daysD = new Date(c.getTime() - 604800000L);
        Date _7daysD = new Date(c.getTime() - 691200000L);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today_date = df.format(c);
        String _7days = df.format(_7daysD);

        /**BTC Data Graph*/


        crypto_graph = root.findViewById(R.id.chart_crypto);
        axisValues_crypto = new ArrayList();
        yAxisValues_crypto = new ArrayList();
        yAxisValues2_crypto = new ArrayList();
        crypto_list_open = new ArrayList();
        crypto_list_close = new ArrayList();
        Crypto_bg_process crypto_bg_process = new Crypto_bg_process();
        Crypto_Today();
        crypto_bg_process.execute();

        crypto_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a = 0;
                a++;
            }
        });
        final int[] lable_reset = {0};
        crypto_graph.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //Open_Stcok_Web();
                try {
                    lable_reset[0]++;

                    if (lable_reset[0] == 1) {
                        crypto_bg_process.show_line_label();
                    } else {
                        lable_reset[0] = 0;
                        crypto_bg_process.remove_line_label();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });


        /**Crypto Data Graph*/
        yAxisData_crypto = new int[]{7, 6, 5, 4, 3, 2, 1, 0};
        // stock_bg_process.execute();

        // int a2=Linear_Regression_ALlo(8,21);
        // Log.e("Linear_Algo","Linear Regression: \n Day 7: "+Linear_Regression_ALlo(7,21));

        //ETH Graph
        eth_bg_process = new ETH_bg_process();
        final int[] lable_reset_eth = {0};
        eth_graph.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //Open_Stcok_Web();
                try {
                    lable_reset_eth[0]++;

                    if (lable_reset_eth[0] == 1) {
                        eth_bg_process.show_line_label();
                    } else {
                        lable_reset_eth[0] = 0;
                        eth_bg_process.remove_line_label();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        //SHIB Graph
        shib_bg_process = new SHIB_bg_process();
        final int[] lable_reset_shib = {0};
        shib_graph.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //Open_Stcok_Web();
                try {
                    lable_reset_shib[0]++;

                    if (lable_reset_shib[0] == 1) {
                        shib_bg_process.show_line_label();
                    } else {
                        lable_reset_shib[0] = 0;
                        shib_bg_process.remove_line_label();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });


        return root;
    }


    /**
     * BTC Data Graph
     */


    LineChartData data_crypto = new LineChartData();

    LineChartData data_crypto2 = new LineChartData();

    public void Crypto_Today() {
        Date c = Calendar.getInstance().getTime();
        //Date today = new Date(c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today_date = df.format(c);
        Crypto_date = today_date;
    }

    public void Crypto_Yesterday() {
        Date c = Calendar.getInstance().getTime();
        //7 means 6 days
        Date yesterday = new Date(c.getTime() - 86400000L);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String yesterday_date = df.format(yesterday);
        Crypto_date = yesterday_date;
    }

    Date c = Calendar.getInstance().getTime();

    public String Crypto_Week(long time) {
        Date today_date = new Date(c.getTime() - time);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String weekly_dates = df.format(today_date);
        Log.e("S_date", weekly_dates + "");
        return weekly_dates;
    }

    int limit_Crypto_bg = 0;
    int limit_run = 0;
    List Crypto_dates, Crypto_dates2;

    class Crypto_bg_process extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            crypto_list_open.clear();
            crypto_list_close.clear();
            yAxisValues_crypto.clear();
            yAxisValues2_crypto.clear();
            axisValues_crypto.clear();
            Crypto_dates = new ArrayList();
            Crypto_dates2 = new ArrayList();
            crypto_pbr.setVisibility(View.VISIBLE);
            Crypto_dates.clear();
            Crypto_dates2.clear();


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
            crypto_pbr.setVisibility(View.GONE);

            //Collections.reverse(stock_list);
            //Collections.reverse(stock_list2);

            line = new Line(yAxisValues_crypto); //Close
            line2 = new Line(yAxisValues2_crypto); //Open

            try {

                Collections.reverse(Crypto_dates);
                Collections.reverse(crypto_list_open);
                Collections.reverse(crypto_list_close);
                String[] axisData = {
                        Crypto_dates.get(0) + "",
                        Crypto_dates.get(1) + "",
                        Crypto_dates.get(2) + "",
                        Crypto_dates.get(3) + "",
                        Crypto_dates.get(4) + "",
                        Crypto_dates.get(5) + "",
                        Crypto_dates.get(6) + "",
                };


                try {
                    for (int i = 0; i < axisData.length; i++) {
                        axisValues_crypto.add(i, new AxisValue(i).setLabel(axisData[i]));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < crypto_list_close.size(); i++) {
                    yAxisValues_crypto.add(new PointValue(i, Float.parseFloat(crypto_list_close.get(i).trim() + "")));

                }
                for (int i = 0; i > crypto_list_close.size(); i++) {
                    yAxisValues2_crypto.add(new PointValue(i, Float.parseFloat(crypto_list_open.get(i).trim())));


                }
                yAxisValues2_crypto.clear();
                //Future graph data
                for (int bb = 0; bb < crypto_list_open.size(); bb++) {
                    yAxisValues2_crypto.add(new PointValue(bb, Float.parseFloat(crypto_list_open.get(bb))));
                    //Log.e("temp", yAxisValues2 + "");
                }


                List lines = new ArrayList<String>();
                List lines2 = new ArrayList<String>();
                lines.add(line);
                lines.add(line2);
                data_crypto.setLines(lines);
                //   data_stock.setLines(lines2);

                crypto_graph.setLineChartData(data_crypto);

                Axis axis = new Axis();
                axis.setValues(axisValues_crypto);
                axis.setMaxLabelChars(2);
                Axis yAxis = new Axis();
                //yAxis.setValues(yAxisValues);
                data_crypto.setAxisYLeft(yAxis.setAutoGenerated(false));
                data_crypto.setAxisXBottom(axis);
                data_crypto.setValueLabelTextSize(10);
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

                eth_bg_process.execute();
                shib_bg_process.execute();

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
                    URL url = new URL(Crypto_Link_New);
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
                        JSONObject object1 = array.getJSONObject("Time Series (Digital Currency Daily)");
                        JSONArray arr = object1.names();


                        for (int i = 0; i < 7; i++) {
                            String date_in_db = arr.getString(i) + "";
                            Crypto_dates.add(date_in_db.substring(8, 10));
                            Crypto_dates2.add(date_in_db);
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


                            }
                            crypto_list_close.add(Float.parseFloat(map.get("4b. close (USD)").trim()) + "");
                            crypto_list_open.add(Float.parseFloat(map.get("1b. open (USD)").trim()) + "");

                            Log.e("Crypto data", "Date: " + date_in_db + "\t" + map.get("4b. close (USD)")
                                    + "\t" + map.get("1b. open (USD)"));
                        }
                        Log.e("Crypto Dates: ", Crypto_dates + "");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Collections.reverse(Crypto_dates2);
                                weekly_date_crypto.setText(Crypto_dates2.get(0) + "");
                                today_datetxt_crypto.setText(Crypto_dates2.get(6) + "");
                                crypto_txt.setText("Crypto Market " + BTC.toUpperCase() + " (USD)");
                            }
                        });
                    } catch (JSONException e) {
                        Log.e("Crypto err1", e.getLocalizedMessage() + "");

                        //No Data Exception
                        //Recalling with new Date day-1

                        Crypto_Yesterday();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (limit_Crypto_bg < 1) {
                                    limit_run = 0;
                                    new Crypto_bg_process().execute();
                                }
                                limit_Crypto_bg++;
                            }

                        });


                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    Log.e("Crypto err2", e.getLocalizedMessage() + "");
                }

            }
            limit_run++;
            return dummy;
        }
    }


    /**
     * ETH DATA AND GRAPH
     */
    int limit_Eth_bg = 0;
    int limit_run_eth = 0;
    List ETH_dates, ETH_dates2;

    LineChartData data_eth = new LineChartData();
    LineChartData data_eth2 = new LineChartData();
    List yAxisValues_eth;
    List yAxisValues2_eth;
    List axisValues_eth;
    int[] yAxisData_eth;

    class ETH_bg_process extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            yAxisValues2_eth = new ArrayList();
            yAxisValues_eth = new ArrayList();
            axisValues_eth = new ArrayList();
            eth_list_open.clear();
            eth_list_close.clear();
            eth_pbr.setVisibility(View.VISIBLE);
            yAxisValues_eth.clear();
            yAxisValues2_eth.clear();
            axisValues_eth.clear();

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
            eth_pbr.setVisibility(View.GONE);

            line = new Line(yAxisValues_eth); //Close
            line2 = new Line(yAxisValues2_eth); //Open

            try {

                // Collections.reverse(Crypto_dates);
                Collections.reverse(eth_list_open);
                Collections.reverse(eth_list_close);
                String[] axisData = {
                        Crypto_dates.get(0) + "",
                        Crypto_dates.get(1) + "",
                        Crypto_dates.get(2) + "",
                        Crypto_dates.get(3) + "",
                        Crypto_dates.get(4) + "",
                        Crypto_dates.get(5) + "",
                        Crypto_dates.get(6) + "",
                };


                try {
                    for (int i = 0; i < axisData.length; i++) {
                        axisValues_eth.add(i, new AxisValue(i).setLabel(axisData[i]));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < eth_list_close.size(); i++) {
                    yAxisValues_eth.add(new PointValue(i, Float.parseFloat(eth_list_close.get(i).trim() + "")));

                }
                for (int i = 0; i > eth_list_close.size(); i++) {
                    yAxisValues2_eth.add(new PointValue(i, Float.parseFloat(eth_list_open.get(i).trim())));


                }
                yAxisValues2_eth.clear();
                //Future graph data
                for (int bb = 0; bb < eth_list_open.size(); bb++) {
                    yAxisValues2_eth.add(new PointValue(bb, Float.parseFloat(eth_list_open.get(bb))));
                    //Log.e("temp", yAxisValues2 + "");
                }


                List lines = new ArrayList<String>();
                List lines2 = new ArrayList<String>();
                lines.add(line);
                lines.add(line2);
                data_eth.setLines(lines);
                //   data_stock.setLines(lines2);

                eth_graph.setLineChartData(data_eth);

                Axis axis = new Axis();
                axis.setValues(axisValues_eth);
                axis.setMaxLabelChars(2);
                Axis yAxis = new Axis();
                //yAxis.setValues(yAxisValues);
                data_eth.setAxisYLeft(yAxis.setAutoGenerated(false));
                data_eth.setAxisXBottom(axis);
                data_eth.setValueLabelTextSize(10);
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
                //line2.setHasLabels(true);
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
            if (limit_run_eth < 1) {
                try {
                    URL url = new URL(Crypto_Link_New_1 + ETH + Crypto_Link_New_3);
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
                        JSONObject object1 = array.getJSONObject("Time Series (Digital Currency Daily)");
                        JSONArray arr = object1.names();


                        for (int i = 0; i < 7; i++) {
                            String date_in_db = arr.getString(i) + "";
//                             Crypto_dates.add(date_in_db.substring(8, 10));
//                             Crypto_dates2.add(date_in_db);
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


                            }
                            eth_list_close.add(Float.parseFloat(map.get("4b. close (USD)").trim()) + "");
                            eth_list_open.add(Float.parseFloat(map.get("1b. open (USD)").trim()) + "");

                            Log.e("ETH data", "Date: " + date_in_db + "\t" + map.get("4b. close (USD)")
                                    + "\t" + map.get("1b. open (USD)"));
                        }
                        Log.e("ETH Dates: ", Crypto_dates + "");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                weekly_date_eth.setText(Crypto_dates2.get(0) + "");
                                today_datetxt_eth.setText(Crypto_dates2.get(6) + "");
                                eth_txt.setText("Crypto Market " + ETH.toUpperCase() + " (USD)");
                            }
                        });
                    } catch (JSONException e) {
                        Log.e("ETH err1", e.getLocalizedMessage() + "");

                        //No Data Exception
                        //Recalling with new Date day-1

                        //Crypto_Yesterday();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (limit_Crypto_bg < 1) {
                                    limit_run_eth = 0;
                                    new ETH_bg_process().execute();
                                }
                                limit_Eth_bg++;
                            }

                        });


                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    Log.e("ETH err2", e.getLocalizedMessage() + "");
                }

            }
            limit_run_eth++;
            return dummy;
        }
    }


    /**
     * SHIB DATA AND GRAPH
     */
    int limit_Shib_bg = 0;
    int limit_run_shib = 0;
    List SHIB_dates, SHIB_dates2;

    LineChartData data_shib = new LineChartData();
    LineChartData data_shib2 = new LineChartData();
    List yAxisValues_shib;
    List yAxisValues2_shib;
    List axisValues_shib;
    int[] yAxisData_shib;

    class SHIB_bg_process extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            yAxisValues2_shib = new ArrayList();
            yAxisValues_shib = new ArrayList();
            axisValues_shib = new ArrayList();
            shib_list_open.clear();
            shib_list_close.clear();
            shib_pbr.setVisibility(View.VISIBLE);
            yAxisValues_shib.clear();
            yAxisValues2_shib.clear();
            axisValues_shib.clear();

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
            shib_pbr.setVisibility(View.GONE);

            line = new Line(yAxisValues_shib); //Close
            line2 = new Line(yAxisValues2_shib); //Open

            try {

                // Collections.reverse(Crypto_dates);
                Collections.reverse(shib_list_open);
                Collections.reverse(shib_list_close);
                String[] axisData = {
                        Crypto_dates.get(0) + "",
                        Crypto_dates.get(1) + "",
                        Crypto_dates.get(2) + "",
                        Crypto_dates.get(3) + "",
                        Crypto_dates.get(4) + "",
                        Crypto_dates.get(5) + "",
                        Crypto_dates.get(6) + "",
                };


                try {
                    for (int i = 0; i < axisData.length; i++) {
                        axisValues_shib.add(i, new AxisValue(i).setLabel(axisData[i]));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < shib_list_close.size(); i++) {
                    yAxisValues_shib.add(new PointValue(i, Float.parseFloat(shib_list_close.get(i).trim() + "")));

                }
                for (int i = 0; i > shib_list_close.size(); i++) {
                    yAxisValues2_shib.add(new PointValue(i, Float.parseFloat(shib_list_open.get(i).trim())));


                }
                yAxisValues2_shib.clear();
                //Future graph data
                for (int bb = 0; bb < shib_list_open.size(); bb++) {
                    yAxisValues2_shib.add(new PointValue(bb, Float.parseFloat(shib_list_open.get(bb))));
                    //Log.e("temp", yAxisValues2 + "");
                }


                List lines = new ArrayList<String>();
                List lines2 = new ArrayList<String>();
                lines.add(line);
                lines.add(line2);
                data_shib.setLines(lines);
                //   data_stock.setLines(lines2);

                shib_graph.setLineChartData(data_shib);

                Axis axis = new Axis();
                axis.setValues(axisValues_shib);
                axis.setMaxLabelChars(2);
                Axis yAxis = new Axis();
                //yAxis.setValues(yAxisValues);
                data_shib.setAxisYLeft(yAxis.setAutoGenerated(false));
                data_shib.setAxisXBottom(axis);
                data_shib.setValueLabelTextSize(10);
                LineChartValueFormatter formatter = new SimpleLineChartValueFormatter(6);
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
            if (limit_run_shib < 1) {
                try {
                    URL url = new URL(Crypto_Link_New_1 + SHIB + Crypto_Link_New_3);
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
                        JSONObject object1 = array.getJSONObject("Time Series (Digital Currency Daily)");
                        JSONArray arr = object1.names();


                        for (int i = 0; i < 7; i++) {
                            String date_in_db = arr.getString(i) + "";
//                             Crypto_dates.add(date_in_db.substring(8, 10));
//                             Crypto_dates2.add(date_in_db);
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


                            }
                            shib_list_close.add(Float.parseFloat(map.get("4b. close (USD)").trim()) + "");
                            shib_list_open.add(Float.parseFloat(map.get("1b. open (USD)").trim()) + "");

                            Log.e("SHIB data", "Date: " + date_in_db + "\t" + map.get("4b. close (USD)")
                                    + "\t" + map.get("1b. open (USD)"));
                        }
                        Log.e("SHIB Dates: ", Crypto_dates + "");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                weekly_date_shib.setText(Crypto_dates2.get(0) + "");
                                today_datetxt_shib.setText(Crypto_dates2.get(6) + "");
                                shib_txt.setText("Crypto Market " + SHIB.toUpperCase() + " (USD)");
                            }
                        });
                    } catch (JSONException e) {
                        Log.e("SHIB err1", e.getLocalizedMessage() + "");

                        //No Data Exception
                        //Recalling with new Date day-1

                        //Crypto_Yesterday();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (limit_Shib_bg < 1) {
                                    limit_run_shib = 0;
                                    new ETH_bg_process().execute();
                                }
                                limit_Shib_bg++;
                            }

                        });


                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    Log.e("SHIB err2", e.getLocalizedMessage() + "");
                }

            }
            limit_run_shib++;
            return dummy;
        }
    }


    public int Average_Per_Day(int a, int b, int c, int d, int e, int f, int g) {
        int Average = 0;
        Average = (a + b + c + d + e + f + g) / 7;

        return Average;
    }

    /**COVID-19 DATA AND GRAPH*/

}