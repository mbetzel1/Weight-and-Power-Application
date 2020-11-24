package com.example.practicewp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WPTab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WPTab extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int grossWt, pa, densAlt, fieldElev, temp;
    private Configuration current;
    private double drag;
    private Spinner spinnerIcao;
    private EditText editTemp, editPA, editDA;
    private TextView textMGW, textDrag, textHige, textHoge, textMaxCont, textIntermed, textTenPerc,
            textMaxEnd, textMaxRange, textSingleEng, textSEMin, textSEMax, textSEHige, textSEHoge,
            textNz, textPs, textToBurn;
    private Button loadMaxesButton;
    private FloatingActionButton calcButton;
    private String urlStatic, icao;
    private Hashtable<String, Integer> elevations;
    private ArrayAdapter<String> icaoAdapter;
    private View view;

    public WPTab() {
        // Required empty public constructor
    }
    //should first implement the algorithm and then work on grabbing info from ADDS
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WPTab.
     */
    // TODO: Rename and change types and number of parameters
    public static WPTab newInstance(String param1, String param2) {
        WPTab fragment = new WPTab();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        urlStatic = "https://www.aviationweather.gov/adds/dataserver_current/httpparam?datasource=tafs&requestType=retrieve&format=xml&mostRecentForEachStation=true&hoursBeforeNow=24&stationString=";
        elevations = new Hashtable<String, Integer>();
        buildElevations();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_w_p_tab, container, false);
        linkViews(view);
        loadMaxesButton.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                icao = String.valueOf(spinnerIcao.getSelectedItem()).toUpperCase();
                if (getFieldElev(icao) < 20000) {
                    fieldElev = getFieldElev(icao);
                }
                else {
                    fieldElev = 0;
                }
                new DownloadEnvironmentals().execute(urlStatic + icao);
            }
        });
        calcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });
        ArrayList<String> icaoList = new ArrayList<String>(elevations.keySet());
        Collections.sort(icaoList);
        icaoAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, icaoList);
        icaoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIcao.setAdapter(icaoAdapter);
        spinnerIcao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                icao = String.valueOf(spinnerIcao.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        textMGW.setText(String.format("Total Wt: %d", grossWt));
        textDrag.setText(String.format("Total Drag: %.2f", drag));
        return view;
    }

    public void onResume() {
        super.onResume();
        current = ((MainActivity) getActivity()).getConfiguration();
        grossWt = current.getMissionGrossWeight();
        drag = current.getTotalDrag();
        //replaces following two lines with calculate once it is up and running
        textMGW.setText(String.format("Total Wt: %d", grossWt));
        textDrag.setText(String.format("Total Drag: %.2f", drag));
    }
    //get integer from environmental edit texts
    private int intFromEdit(EditText et) {
        String s;
        s = et.getText().toString();
        if (!"".equals(s)) {
            return (int) Integer.parseInt(s);
        }
        else {
            et.setText("0");
            return 0;
        }
    }
    @SuppressLint("DefaultLocale")
    public void calculate()
    {
        String s1 = editTemp.getText().toString();
        String s2 = editPA.getText().toString();
        if ("".equals(s1)) {
            Snackbar tempSnackbar = Snackbar.make(view.findViewById(R.id.myFrameLayout2), "No value entered for temp.", Snackbar.LENGTH_SHORT);
            tempSnackbar.show();
        }
        else if ("".equals(s2)) {
        Snackbar tempSnackbar = Snackbar.make(view.findViewById(R.id.myFrameLayout2), "No value entered for pressure altitude.", Snackbar.LENGTH_SHORT);
        tempSnackbar.show();
        }
        else {
            pa = Integer.parseInt(s2);
            temp = Integer.parseInt(s1);

            densAlt = (int) (pa + (120 * (temp - (15 - (0.002 * fieldElev)))) + 1);
            editDA.setText(String.valueOf((int) (pa + (120 * (temp - (15 - (0.002 * fieldElev)))) + 1)));
            textNz.setText(String.format("Max Acceleration: %.1f", getNz()));
            double gwHoge = getGwHoge();
            double toBurn = grossWt - gwHoge;
            textTenPerc.setText(String.format("Ten Percent Margin: %.0f", gwHoge));
            textMaxCont.setText(String.format("Max Cont Tq: %.0f", getContQ()));
            textIntermed.setText(String.format("Intermediate Tq: %.0f", (double) Math.round(getIntQ())));
            if (toBurn > 0) {
                textToBurn.setText(String.format("Fuel to Burn: %.0f", toBurn));
            } else {
                textToBurn.setText("Fuel to Burn: N/A");
            }
            textSingleEng.setText(String.format("SE Max Tq: %.0f", getSeQ()));
            double hoge = getHoge();
            textHige.setText(String.format("HIGE Tq: %.0f", getHige(hoge)));
            textHoge.setText(String.format("HOGE Tq: %.0f", (double) Math.round(hoge)));
            textMaxEnd.setText(String.format("Max End Tq/Fuel Flow: %.0f / %.0f", getEndTorque(), getEndFuelFlow()));
            textMaxRange.setText(String.format("Max Rng Tq/Fuel Flow: %.0f / %.0f", getRngTorque(), getRngFuelFlow()));
            String s = "\u209B";
            textPs.setText(String.format("P%s: %.0f", s, getPs()));
            double seHoge = getSingleHoge();
            double seHige = seHoge + 1600;
            if (seHoge <= 12000) {
                textSEHoge.setText(String.format("SE HOGE Wt: N/A"));
            } else {
                textSEHoge.setText(String.format("SE HOGE Wt: %.0f", seHoge));
            }
            if (seHige <= 12000) {
                textSEHige.setText(String.format("SE HIGE Wt: N/A"));
            } else {
                textSEHige.setText(String.format("SE HIGE Wt: %.0f", seHige));
            }
            double seMin = getSEMin();
            double seMax = getSEMax();
            if (seMin > seMax) {
                textSEMax.setText(String.format("SE Max Airspeed: N/A"));
                textSEMin.setText(String.format("SE Min Airspeed: N/A"));
            } else {
                textSEMax.setText(String.format("SE Max Airspeed: %.0f", seMax));
                textSEMin.setText(String.format("SE Min Airspeed: %.0f", seMin));
            }
            if (temp > 50 || temp < -30) {
                Snackbar tSnackbar = Snackbar.make(view.findViewById(R.id.myFrameLayout2), "Temperatures > 50 or < -30 may produce erroneous results", Snackbar.LENGTH_LONG);
                tSnackbar.show();
            }
            if (pa > 10000 || pa < -500) {
                Snackbar paSnackbar = Snackbar.make(view.findViewById(R.id.myFrameLayout2), "Pressure Altitudes > 10000 or < -500 may produce erroneous results", Snackbar.LENGTH_LONG);
                paSnackbar.show();
            }
            if (grossWt > 18500 || grossWt < 13000) {
                Snackbar paSnackbar = Snackbar.make(view.findViewById(R.id.myFrameLayout2), "Gross weights > 18500 or < 13000 may produce erroneous results", Snackbar.LENGTH_LONG);
                paSnackbar.show();
            }
        }
    }
    //links all the textViews, buttons, and editTexts to the activity
    public void linkViews(View v) {
        spinnerIcao = v.findViewById(R.id.icao_input);
        calcButton = v.findViewById(R.id.power_calc_button);
        loadMaxesButton = v.findViewById(R.id.icao_button);
        editTemp = v.findViewById(R.id.temp_input);
        editPA = v.findViewById(R.id.pressalt_input);
        editDA = v.findViewById(R.id.densalt_input);
        textMGW = v.findViewById(R.id.weight_for_powertab);
        textDrag = v.findViewById(R.id.drag_for_powertab);
        textHige = v.findViewById(R.id.hige);
        textHoge = v.findViewById(R.id.hoge);
        textMaxCont = v.findViewById(R.id.max_cont);
        textIntermed = v.findViewById(R.id.intermedidate);
        textTenPerc = v.findViewById(R.id.ten_percent);
        textMaxEnd = v.findViewById(R.id.max_end);
        textMaxRange = v.findViewById(R.id.max_range);
        textSingleEng = v.findViewById(R.id.single_eng);
        textSEHige = v.findViewById(R.id.single_hige);
        textSEHoge = v.findViewById(R.id.single_eng_hoge);
        textSEMin = v.findViewById(R.id.single_min_kias);
        textSEMax = v.findViewById(R.id.single_eng_max_kias);
        textNz = v.findViewById(R.id.max_accel);
        textPs = v.findViewById(R.id.ps);
        textToBurn = v.findViewById(R.id.fuel_to_burn);
    }
    //calculates the max acceleration
    //no error
    public double getNz() {
        if (grossWt < 17000) {
            return 2.3;
        }
        else if (grossWt < 17500) {
            return 2.0;
        }
        else if (densAlt < 6001){
            return 1.9;
        }
        else {
            return 1.7;
        }
    }
    //gets max intermediate torque
    //largest error is 0.5899 which rounds out to 1 percent, mean error is 0.0662787, std of error is 0.11469547
    //errors may get doubled due to algorithm, but never more than 1 percent total off
    //all errors > 0.5 are for negative temps < 15 degrees, these get washed out when limiting max value to 100
    public double getIntQ() {
        double intQ;
        double p = ((double) pa) / 10000.0;
        double t = ((double) temp) / 50.10205797;
        intQ = 66.71812 + (-9.26612 * t) + (-24.45488791 * p) + (-10.1636191288 * t * t) + (2.370722734886 * t * p)
        + (4.173660510777 * p * p) + (0.378826419682 * t * t * t) + (2.49668255878583 * t * t * p)
        + (-0.9812183969747696 * t * p * p) + (-0.985775826803999555 * p * p * p);
        if (intQ > 50.0) {
            return 100;
        }
        else {
            return intQ * 2;
        }
    }
    //gets max continuous torque
    //who cares?
    public double getContQ() {
        double contQ;
        if (pa >= 7000) {
            contQ = 118.80103 - (0.57401293 * temp) - (0.0034385114 * pa);
        }
        else if (pa <= 3000) {
            contQ = 128.39921 - (0.98476312 * temp) - (0.003491846 * pa);
        }
        else if (pa <= 5000) {
            contQ = 126.20767 - (0.8918376 * temp) - (0.003710316 * pa);
        }
        else {
            contQ = 124.536215 - (0.7464613 * temp) - (0.003978033 * pa);
        }
        if (contQ > 100.0) {
            return 100;
        }
        else {
            return Math.round(contQ);
        }
    }
    //gets max gross weight for a 10% margin hoge
    //max error is 31.846, mean error is 7.02368, std is 5.75405
    public double getGwHoge() {
        double p = (double) pa;
        double t = (double) temp;
        double q_in = getIntQ() - 10.0;
        double q;
        double w;
        q = -57.650259448125553 + (3.533297449604127 * q_in) + (-0.381407637019191 * t) + (-0.041241411259062 * q_in * q_in)
        + (0.013835422033203 * q_in * t) + (-0.000966161945296 * t * t) + (0.000295629958774 * q_in * q_in * q_in)
        + (-0.000170096460108 * q_in * q_in * t) + (0.000006130956115 * q_in * t * t) + (0.000023500117040 * t * t * t)
        + (-0.000000790073819 * q_in * q_in * q_in * q_in) + (0.000000540934021 * q_in * q_in * q_in * t)
        + (0.000000056663933 * q_in * q_in * t * t)+ (-0.000000245115004 * q_in * t * t * t) + (0.000000003119823 * t * t * t * t);

        w = -4653.861084779618977 + (394.239453197965986 * q) + (0.337057413349404 * p) + (-2.108109839588026 * q * q)
        + (-0.006779464740002 * q * p) + (-0.000007672434630 * p * p) + (0.005359456459435 * q * q * q)
        + (0.000013337489830 * q * q * p) + (0.000000038915477 * q * p * p) + (0.000000000027796 * p * p * p);
        if (w > 18500) {
            return 18500;
        }
        else {
            return Math.round((w) / 100) * 100;
        }
    }
    //gets single engine hoge wight
    //largest error is 60 lbs, mean error is 29.816, std is 18.47326
    public double getSingleHoge() {
        double q = getSeQ();
        double se_hoge;
        double p = (double) pa;
        double t = (double) temp;
        se_hoge = 14702.90876 + (-17.11604 * t) + (-0.63347585 * p) + (-1.0491974830 * t * t) + (0.000745012102 * t * p)
        + (0.000034837618 * p * p) + (0.009000271642 * t * t * t) + (0.00005474039120 * t * t * p)
        + (-0.0000001159795125 * t * p * p) + (-0.000000002137184651 * p * p * p);
        return Math.round(se_hoge/100) * 100;

    }
    //gets single engine min airspeed
    //max error is 8.157816, mean error is 1.98622, std is 1.647497
    //the max value is an extreme outlier at p = 7065, t = 20, w = 17000. next nearest error is 5.72
    //all errors > 3 are at p > 6000, w >= 17000 and generally unrealistic conditions regarding temp and pa (t = 40, p > 5000 for example)
    public double getSEMin() {
        double q = getSeQ();
        double se_min;
        double p = (double) pa;
        double t = (double) temp;
        double w = (double) grossWt;
        if (q >= 65) {
            se_min = -241.037924174138226 + (-0.038219802446115 * t) + (0.000038413927168 * p) + (0.026915100022911 * w)
            + (0.001726906032342 * t * t) + (0.000015678309246 * t * p) + (0.000006384680689 * t * w)
            + (0.000000170023625 * p * p) + (0.000000036292034 * p * w) + (-0.000000644651682 * w * w);
        }
        else {
            se_min = 23.524240606255567 + (-0.528985545888176 * t) + (-0.009297385853680 * p) + (-0.004384535658583 * w)
            + (0.006414171888501 * t * t) + (0.000032209859151 * t * p) + (0.000034109814592 * t * w)
            + (0.000000237968695 * p * p) + (0.000000718493322 * p * w) + (0.000000258453679 * w * w);
        }
        if (se_min > 60) {
            se_min = -2516.779935217150523 + (3.765574584888622 * t) + (0.121256798535731 * p) + (0.207724327469539 * w)
            + (0.022601825279916 * t * t) + (-0.000063365927450 * t * p) + (-0.000143926405057 * t * w)
            + (-0.000001489449936 * p * p) + (-0.000004597721177 * p * w) + (-0.000004153319940 * w * w);
        }
        return Math.round(se_min);
    }
    //gets single engine max airspeed
    //max error is 4.24923, mean error is 1.088608, std is 0.997788
    public double getSEMax() {
        double q = getSeQ();
        double se_max;
        double p = (double) pa;
        double t = (double) temp;
        double w = (double) grossWt;
        if (p < 2000) {
            if (q < 65) {
                se_max = -277.723653826089901 + (5.289348161258794 * t) + (0.030647535380584 * p) + (0.046656204811891 * w)
                + (-0.022602231116480 * t * t) + (-0.000139402026458 * t * p) + (-0.000308333334981 * t * w)
                + (-0.000000307518622 * p * p) + (-0.000001956922402 * p * w) + (-0.000001330556191 * w * w);
            } else {
                se_max = 32.148730249510578 + (0.855511570053495 * t) + (0.005883306555017 * p) + (0.014134930804940 * w)
                + (-0.006237802888333 * t * t) + (-0.000071343279598 * t * p) + (-0.000036725548957 * t * w)
                + (0.000000782464637 * p * p) + (-0.000000392936585 * p * w) + (-0.000000542383641 * w * w);
            }
        }
        else {
            se_max = -100.864531413105482 + (2.142048061050037 * t) + (0.020992872434586 * p) + (0.029557018906827 * w)
                    + (-0.016014357880962 * t * t) + (-0.000085659491546 * t * p) + (-0.000127206775354 * t * w)
                    + (-0.000000418544387 * p * p) + (-0.000001341234230 * p * w) + (-0.000000960713339 * w * w);
        }
        return Math.round(se_max);
    }
    //max error is 0.867, mean error is 0.260, std error is 0.187
    public double getEndTorque(){
        double p = (double) pa;
        double t = (double) temp;
        double w = (double) grossWt;
        double q = -30.634528459511372 + (0.250708688019986 * t) + (0.003530509900710 * p) + (0.010315710083028 * w)
        + (0.001373859383606 * t * t) + (0.000010705852146 * t * p) + (-0.000049384674840 * t * w)
        + (-0.000000086812033 * p * p) + (-0.000000575536973 * p * w) + (-0.000000528693137 * w * w)
        + (-0.000009258195865 * t * t * t) + (-0.000000272824977 * t * t * p) + (0.000000014507404 * t * t * w)
        + (-0.000000001722277 * t * p * p)+ (0.000000001048787 * t * p * w) + (0.000000001809157 * t * w * w)
        + (-0.000000000002006 * p * p * p) + (0.000000000010308 * p * p * w) + (0.000000000020349 * p * w * w)
        + (0.000000000011694 * w * w * w);
        return Math.round(q);
    }
    //max error is 9.65, mean error is 3.29, std error is 2.23
    public double getEndFuelFlow() {
        double p = (double) pa;
        double t = (double) temp;
        double w = (double) grossWt;
        double ff = 156.859605155446388 + (3.826891410538655 * t) + (0.018405688101183 * p) + (0.085842895898111 * w)
        + (-0.015863454061604 * t * t) + (-0.000070655450791 * t * p) + (-0.000558195321709 * t * w)
        + (0.000000964025292 * p * p) + (-0.000006185070557 * p * w) + (-0.000004181101418 * w * w)
        + (0.000143795920747 * t * t * t) + (0.000001400515853 * t * t * p) + (0.000001039405153 * t * t * w)
        + (-0.000000017677348 * t * p * p)+ (0.000000013133935 * t * p * w) + (0.000000019244498 * t * w * w)
        + (-0.000000000185665 * p * p * p) + (0.000000000144856 * p * p * w) + (0.000000000213454 * p * w * w)
        + (0.000000000091468 * w * w * w);
        return Math.round(ff/10) * 10;
    }
    //max error is 3.0, mean error is 0.63, std error is 0.53
    //does worse at high altitudes and on both edges of max and min gross weight
    public double getRngTorque() {
        double p = (double) pa;
        double t = (double) temp;
        double w = (double) grossWt;
        double d = drag;
        double q =  -113.346205149442383 + (1.105480547344611 * t) + (0.001774299146688 * p)
                + (0.030236059012577 * w) + (0.007289176106869 * d) + (-0.008028110626462 * t * t)
                + (-0.000062824783484 * t * p) + (-0.000084630544434 * t * w) + (-0.035427782965078 * t * d)
                + (-0.000000353020700 * p * p) + (-0.000000082720641 * p * w) + (-0.000059575391682 * p * d)
                + (-0.000001682607083 * w * w) + (-0.000132825677012 * w * d) + (0.043728466340661 * d * d)
                + (0.000019272835944 * t * t * t) + (-0.000000179883775 * t * t * p) + (0.000000437904410 * t * t * w)
                + (0.000207798502993 * t * t * d)+ (-0.000000008903132 * t * p * p) + (0.000000008080447 * t * p * w)
                + (0.000001143994999 * t * p * d) + (0.000000000506222 * t * w * w) + (0.000002076685941 * t * w * d)
                + (-0.000263281661793 * t * d * d) + (-0.000000000025706 * p * p * p) + (0.000000000049315 * p * p * w)
                + (0.000000000430273 * p * p * d) + (-0.000000000014922 * p * w * w) + (0.000000006834352 * p * w * d)
                + (-0.000000912552414 * p * d * d)+ (0.000000000035195 * w * w * w) + (0.000000004160373 * w * w * d)
                + (-0.000000435836516 * w * d * d) + (-0.000921936221953 * d * d * d);

        return Math.round(q);
    }
    //max error is 38.193, mean error is 9.385, std error is 7.29
    //does worse at high altitudes and on both edges of max and min gross weight
    public double getRngFuelFlow() {
        double p = ((double) pa) / 10000.0;
        double t = ((double) temp) / 50.0;
        double w = ((double) grossWt) / 18000.0;
        double d = drag / 15.0;
        double ff = -1292.75 + (468.0602234070 * t) + (17.1114419024 * p) + (7087.7269083139 * w)
        + (33.9150596705 * d) + (-327.1015715837 * t * t) + (-422.8375423535 * t * p) + (-486.2848751761 * t * w)
        + (-434.4924331884 * t * d) + (-374.9237878642 * p * p) + (63.0908619138 * p * w) + (-299.9589200285 * p * d)
        + (-7255.9383578370 * w * w) + (-241.3085535837 * w * d) + (56.7547168410 * d * d) + (65.4167589354 * t * t * t)
        + (57.9515864747 * t * t * p) + (298.3914485668 * t * t * w) + (48.6634579472 * t * t * d)
        + (-226.9886162865 * t * p * p) + (558.6705999507 * t * p * w) + (128.5267705283 * t * p * d)
        + (-44.6106677582 * t * w * w) + (382.8749372046 * t * w * d) + (47.9174857325 * t * d * d)
        + (-167.9669791223 * p * p * p) + (696.0750452943 * p * p * w) +(172.1712930186 * p * p * d)
        + (-487.2970394539 * p * w * w) + (327.5345051532 * p * w * d) + (-108.3616351839 * p * d * d)
        + (2683.7093468452 * w * w * w) + (145.4368306602 * w * w * d) + (-156.1194307072 * w * d * d)
        + (71.9811549540 * d * d * d);
        return Math.round(ff/10) * 10;
    }
    //gets single engine max torque
    //on data from 0-10000' pa and -25 to 50 degrees average off is 0.156, std 0.151, max error is 0.547275
    public double getSeQ() {
        double se;
        double p = (double) pa;
        double t = (double) temp;
        if (t > 45) {
            se = -416.97259 + (27.45926 * t) + (0.01776934 * p) + (-0.5074951363 * t * t) + (-0.000924196173 * t * p)
            + (0.000000204808 * p * p) + (0.002961184715 * t * t * t) + (0.00001055400702 * t * t * p)
            + (-0.0000000032987924 * t * p * p) + (-0.000000000002533085 * p * p * p);
        }
        else {
            se = 70.34867 + (-0.05553 * t) + (-0.00248733 * p) + (-0.0047647906 * t * t) + (-0.000001091418 * t * p)
            + (0.000000033803 * p * p) + (0.000025163052 * t * t * t) + (0.00000012488353 * t * t * p)
            + (-0.0000000001385653 * t * p * p) + (-0.000000000000649786 * p * p * p);
        }
        if (se > 65) {
            return 65;
        }
        else {
            return Math.round(se);
        }
    }
    //gets power required to hige at seven feet
    //error is hoge error + max error 0.19, mean error 0.077313, std error 0.776606
    public double getHige(double q) {
        return Math.round(165.399363755426691 + (-6.907518472701479 * q) + (0.135720147574218 * q * q)
        + (-0.001032525491464 * q * q * q ) + (0.000002898797755 * q * q * q * q))- 1;
    }
    //gets power required to hoge
    //needs to be fixed for temperatures < 10
    //max error is < 1, mean error is .2077212, std error is 0.2409
    public double getHoge() {
        double hoge;
        double p = (double) pa;
        double t = (double) temp;
        double w = (double) grossWt;
        double q_in = (27.951022698195644 + (0.000483965589654 * w) + (0.001711261068578 * p) + (0.000000153175272 * w * w)
        + (-0.000000358709340 * w * p) + (-0.000000149988465 * p * p) + (0.000000000001041 * w * w * w)
        + (0.000000000018433 * w * w * p) + (0.000000000012575 * w * p * p) + (0.000000000002570 * p * p * p));
        //if (q_in > 60) {
            hoge = -3.006208420249493 + (1.156630064907341 * q_in) + (-0.343141411271825 * t) + (-0.003029781848653 * q_in * q_in)
        + (0.007800621403682 * q_in * t) + (0.000955144859352 * t * t) + (0.000025133955201 * q_in * q_in * q_in)
        + (-0.000046400420640 * q_in * q_in * t) + (-0.000013724739091 * q_in * t * t) + (-0.000005494269758 * t * t * t)
        + (-0.000000073943596 * q_in * q_in * q_in * q_in) + (0.000000187678773 * q_in * q_in * q_in * t)
        + (0.000000087479898 * q_in * q_in * t * t)+ (0.000000045315958 * q_in * t * t * t) + (0.000000024613376 * t * t * t * t);
        //}
        //else {
            //hoge = (8.344033810918674 + (0.612347382076624 * t) + (-0.342700638678744 * t * t) + (0.006580577522463 * t * t * t )
            //+ (0.007910302380988 * t * t * t * t));
        //}
        return Math.round(hoge);
    }
    //gets best maneuverability airspeed
    //no error
    public double getPs() {
        double p = (double) pa;
        return Math.round(60.28571 + (-0.00321429 * p) + (0.000000185714 * p * p));
    }
    //starts a thread to connect to the aviation weather API and pull the Metar and convert it to environmentals
    private class DownloadEnvironmentals extends AsyncTask<String, Void, String> {
        private Exception exception;
        String text;
        @Override
        protected String doInBackground(String... strings) {
            text = "";
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection http=(HttpURLConnection)url.openConnection();
                http.setDoInput(true);
                http.connect();
                InputStream is = http.getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(is, null);
                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if(eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("raw_text")) {
                            text = xpp.nextText();
                        }
                    }
                    eventType = xpp.next();
                }
                is.close();

            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }
        @Override
        protected void onPostExecute(String text2) {
            editTemp.setText(getTempFromTaf(text));
            editPA.setText(getPaFromTaf(text));
            densAlt = (int) (pa + (120 * (temp - (15 - (0.002 * fieldElev)))) + 1);
            if (densAlt > -1000) {
                editDA.setText(String.valueOf(densAlt));
            }
            else {
                editDA.setText("");
            }
            super.onPostExecute(text2);
        }
    }
    //goes through raw taf and finds highest temp using regex, sets highest temp and editTemp
    //sets temperature to highest temp or -100 if nothing found, sets editTemp to highest temp or ""
    //should create a dialogue if no temp is present
    private String getTempFromTaf(String taf) {
        Pattern pattern = Pattern.compile(" T([0-9]{2})");
        Matcher m = pattern.matcher(taf);
        temp = -100;
        while (m.find()) {
            if (temp < Integer.parseInt(m.group(1))) {
                temp = Integer.parseInt(m.group(1));
            }
        }
        if (temp == -100) {
            return "";
        }
        else {
            return String.valueOf(temp);
        }
    }
    //goes through raw taf and finds lowest altimeter using regex, sets highest pressAlt and editPA
    //sets pressAlt to highest elev or leaves it, sets editPA to highest PA or ""
    //should create a dialogue box if no altimeter is present
    private String getPaFromTaf(String taf) {
        Pattern pattern = Pattern.compile(" QNH([0-9]{4})INS");
        Matcher m = pattern.matcher(taf);
        int smallest = 100000;
        while (m.find()) {
            if (smallest > Integer.parseInt(m.group(1))) {
                smallest = Integer.parseInt(m.group(1));
            }
        }
        if (smallest == 100000) {
            return "";
        }
        else {
            pa = fieldElev + (10 * (2992 - smallest));
            return String.valueOf(pa);
        }
    }
    //looks up field elev from icao, if it exists returns it, if not returns 10000
    //needs to throw error if icao is not found
    private int getFieldElev(String icao) {
        if (elevations.containsKey(icao)) {
            return elevations.get(icao);
        }
        //this should be something else, should throw an error
        else {
            return 100000;
        }
    }
    //builds elevations dictionary, made its own method to keep onCreate decluttered
    private void buildElevations() {
        elevations.put("KNYL", 216);
        elevations.put("KNKX", 477);
        elevations.put("KNFG", 78);
        elevations.put("KNYG", 11);
        elevations.put("PHNG", 23);
        elevations.put("KNBC", 37);
        elevations.put("RJOI", 23);
        elevations.put("ROTM", 810);
        elevations.put("KNKT", 29);
        elevations.put("KNCA", 26);
        elevations.put("KNFL", 3929);
        elevations.put("KNPA", 28);
        elevations.put("KNZY", 50);
        elevations.put("KNXP", 2051);
    }
}