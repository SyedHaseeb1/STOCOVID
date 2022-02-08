package com.hsb.covid_19_predictor_fyp_v10.ui.stock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import java.util.Random;

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
    ProgressBar stock_pbr;
    String Stock_date;
    TextView weekly_date_stock,today_datetxt_stock;


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
                new ViewModelProvider(this).get(StockViewModel.class);

        binding = FragmentStockBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean theme_dark = preferences.getBoolean("theme_dark", false);
        ScrollView main_L;
        main_L = root.findViewById(R.id.main_L);
        if (theme_dark) {
            main_L.setBackgroundColor(getContext().getResources().getColor(R.color.theme_dark));
        }
        stock_txt = root.findViewById(R.id.stock_txt);
        today_datetxt_stock = root.findViewById(R.id.today_date_stock);
        weekly_date_stock = root.findViewById(R.id.week_date_stock);


        stock_pbr = root.findViewById(R.id.stock_pbr);
        Date c = Calendar.getInstance().getTime();
//        Date _7daysD = new Date(c.getTime() - 604800000L);
        Date _7daysD = new Date(c.getTime() - 691200000L);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today_date = df.format(c);
        String _7days = df.format(_7daysD);

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



    public int Average_Per_Day(int a, int b, int c, int d, int e, int f, int g) {
        int Average = 0;
        Average = (a + b + c + d + e + f + g) / 7;

        return Average;
    }

    /**COVID-19 DATA AND GRAPH*/

}