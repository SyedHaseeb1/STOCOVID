package com.hsb.covid_19_predictor_fyp_v10.ui.notifications;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.hsb.covid_19_predictor_fyp_v10.R;

import java.util.List;


public class CustomAdapter extends ArrayAdapter<String> {
    Context context;

    List<String> Country_array, Provinces_array, Status_array, Active_array, Totalcases_array, Totaldeaths_array, Flag_array;
    //My Stuff
    TextView top_txt, countryname_txt, provincename_txt,
            status_txt, newcases_txt, totalcases_txt, totaldeaths_txt;
    ImageView search_btn, flag_img;
    String country_name = "Pakistan";
    String province_name = "Islamabad";
    RelativeLayout list_l;

    public CustomAdapter(Context context, int resource,
                         List<String> country_array,
                         List<String> provinces_array,
                         List<String> status_array,
                         List<String> active_array,
                         List<String> totalcases_array,
                         List<String> totaldeaths_array,
                         List<String> flag_array

    ) {

        super(context, resource, country_array);
        this.context = context;
        this.Country_array = country_array;
        this.Provinces_array = provinces_array;
        this.Status_array = status_array;
        this.Active_array = active_array;
        this.Totalcases_array = totalcases_array;
        this.Totaldeaths_array = totaldeaths_array;
        this.Flag_array = flag_array;
    }

    public View getView(final int position, View root, ViewGroup parent) {

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = inflater.inflate(R.layout.custom_list, null);

        String Country_name, Province_name, Active_case, Total_cases, Total_deaths;
        String Flag_name;

        Country_name = Country_array.get(position) + "";
        Province_name = Provinces_array.get(position) + "";
        Active_case = Active_array.get(position) + "";
        Total_cases = Totalcases_array.get(position) + "";
        Total_deaths = Totaldeaths_array.get(position) + "";
        Flag_name = Flag_array.get(position) + "";

        flag_img = root.findViewById(R.id.flag_img);
        countryname_txt = root.findViewById(R.id.counrtyname_txt);
        provincename_txt = root.findViewById(R.id.provincename_txt);
        newcases_txt = root.findViewById(R.id.newcases_txt);
        totalcases_txt = root.findViewById(R.id.totalcases_txt);
        totaldeaths_txt = root.findViewById(R.id.totaldeaths_txt);
        list_l = root.findViewById(R.id.list_L);


        countryname_txt.setText(Country_name);
        provincename_txt.setText(Province_name);
        newcases_txt.setText(Active_case);
        totalcases_txt.setText(Total_cases);
        totaldeaths_txt.setText(Total_deaths);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean theme_dark=preferences.getBoolean("theme_dark",false);
        CardView main_L;
        main_L=root.findViewById(R.id.main_L);
        if (theme_dark){
            main_L.setBackgroundColor(getContext().getResources().getColor(R.color.graysh));
        }

        //Setting Flag
        String flag_str = "flag_" + Country_name.toLowerCase().replace(" ", "_");
        int resId = root.getResources().getIdentifier(flag_str, "drawable",
                getContext().getPackageName());
        Drawable d = getContext().getResources().getDrawable(resId);
        flag_img.setImageDrawable(d);

        Animation fade_anim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_anim);
        Animation get_visible_anim = AnimationUtils.loadAnimation(getContext(), R.anim.get_visibile_anim);
        list_l.setAnimation(get_visible_anim);

        list_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Will be implemented soon",Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }
}