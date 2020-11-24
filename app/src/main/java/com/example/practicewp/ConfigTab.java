package com.example.practicewp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ConfigTab extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //prepopulated variables
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //my variables
    private EditText editSide, editBasicWeight, editPilots, editCrew, editSeats, editOther,
            editRRocket, editLRocket, edit762, edit50Cal, editChaff, editFlare, editSM,
            editFuel, editExFuel, editPax, editCargo, editName;
    private Spinner rDasSpinner, lDasSpinner, rGunSpinner, lGunSpinner, deleteSpinner,
            rPodSpinner, lPodSpinner, rRocketSpinner, lRocketSpinner, loadSpinner;
    private TextView textOpWeight, textRRocket, textLRocket, text762, text50Cal,
            textChaff, textFlare, textSM, textZFWeight, textGWeight, textMWeight, textTotRkts;
    private String configToLoad, configToDelete;
    private Button loadConfigButton, saveConfigButton, deleteButton;
    private FloatingActionButton calcButton;
    private ArrayAdapter<Weapon> rGunAdapter, lGunAdapter;
    private ArrayAdapter<Weapon> rPodAdapter, lPodAdapter;
    private ArrayAdapter<Weapon> rRocketAdapter, lRocketAdapter;
    private ArrayAdapter<String> loadAdapter, delAdapter;
    private Gun rGunSelected, lGunSelected;
    private Das lDasSelected, rDasSelected;
    private Pod rPodSelected, lPodSelected;
    private Rocket rRocketSelected, lRocketSelected;
    private ArrayList<Weapon> rDasList, lDasList;
    private ArrayList<Weapon> rGunList, lGunList;
    private ArrayList<Weapon> rPodList, lPodList;
    private ArrayList<Weapon> rRocketList, lRocketList;
    private Configuration current;
    private View view;
    private RadioButton openButton, closedButton;

    // TODO: Rename and change types of parameters

    public ConfigTab() {
        // Required empty public constructor
    }
    //gets called only when the program is started
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (getArguments() != null) {
        //  mParam1 = getArguments().getString(ARG_PARAM1);
        //  mParam2 = getArguments().getString(ARG_PARAM2);

        //}
        current = ((MainActivity) getActivity()).getConfiguration();

    }
    //Gets called when you come back from another tab or application
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment, needs to be here to get the view
        view = inflater.inflate(R.layout.fragment_config_tab, container, false);
        //needs to be here because it requires a view
        linkViews(view);
        calcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });
        //sets weights to default when tabs are switched, needs to only do it when originally opened
        makeWeaponSpinners();

        //maybe delete later I don't know
        setLoadedValues();
        calculate();
        lDasSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lDasSelected = (Das) lDasList.get(position);
                lPodList = lDasSelected.getAllowablePods();
                clearAdapterAndAttachList(lPodAdapter, lPodList, lPodSpinner);
                lGunList = lDasSelected.getAllowableGuns();
                clearAdapterAndAttachList(lGunAdapter, lGunList, lGunSpinner);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        rDasSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rDasSelected = (Das) rDasList.get(position);
                rPodList = rDasSelected.getAllowablePods();
                clearAdapterAndAttachList(rPodAdapter, rPodList, rPodSpinner);
                rGunList = rDasSelected.getAllowableGuns();
                clearAdapterAndAttachList(rGunAdapter, rGunList, rGunSpinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        lPodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lPodSelected = (Pod) lPodList.get(position);
                lRocketList = lPodSelected.getAllowableRockets();
                clearAdapterAndAttachList(lRocketAdapter, lRocketList, lRocketSpinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        rPodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rPodSelected = (Pod) rPodList.get(position);
                rRocketList = rPodSelected.getAllowableRockets();
                clearAdapterAndAttachList(rRocketAdapter, rRocketList, rRocketSpinner);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        rGunSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rGunSelected = (Gun) rGunList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        lGunSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lGunSelected = (Gun) lGunList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        rRocketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rRocketSelected = (Rocket) rRocketList.get(position);
                    
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        lRocketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lRocketSelected = (Rocket) lRocketList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        loadSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                configToLoad = MainActivity.wpDatabase.wpDao().getSavedConfigs().get(loadSpinner.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        deleteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                configToDelete = MainActivity.wpDatabase.wpDao().getSavedConfigs().get(deleteSpinner.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loadConfigButton.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                current = MainActivity.wpDatabase.wpDao().loadSelectedConfig(configToLoad);
                setLoadedValues();
                calculate();
            }
        });
        saveConfigButton.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
                MainActivity.wpDatabase.wpDao().saveConfig(current);
                updateConfigSpinners();
            }
        });
        deleteButton.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.wpDatabase.wpDao().deleteConfig(configToDelete);
                updateConfigSpinners();
            }
        });
        makeConfigSpinners();
        return view;
    }
    //figure out what this does
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    //updates editText values to the loaded configuration
    //if items is already selected it doesn't activate onClickListener - this might be why it is fucking up
    //called by LoadButton onClick and by onResume
    public void setLoadedValues() {
        editName.setText(current.configName);
        editSide.setText(String.valueOf(current.side));
        editBasicWeight.setText(String.valueOf(current.basicWt));
        editPilots.setText(String.valueOf(current.pilotWt));
        editCrew.setText(String.valueOf(current.crewWt));
        editOther.setText(String.valueOf(current.otherWt));
        editRRocket.setText(String.valueOf(current.rRocketNum));
        editLRocket.setText(String.valueOf(current.lRocketNum));
        editChaff.setText(String.valueOf(current.chaffNum));
        editFlare.setText(String.valueOf(current.flareNum));
        editSM.setText(String.valueOf(current.smNum));
        edit50Cal.setText(String.valueOf(current.fiftyNum));
        edit762.setText(String.valueOf(current.miniNum));
        editFuel.setText(String.valueOf(current.fuelWt));
        editExFuel.setText(String.valueOf(current.exfuelWt));
        editPax.setText(String.valueOf(current.paxWt));
        editCargo.setText(String.valueOf(current.cargoWt));
        editSeats.setText(String.valueOf(current.seatsWt));
        if (current.doorDrag == 3.0) {
            openButton.setChecked(true);
            closedButton.setChecked(false);
        }
        else {
            closedButton.setChecked(true);
            openButton.setChecked(false);
        }

        int rDasPos = spinnerLoadHelper(current.rDasWt, rDasList, 0);
        rDasSpinner.setSelection(rDasPos);
        rDasSelected = (Das) rDasList.get(rDasSpinner.getSelectedItemPosition());

        int lDasPos = spinnerLoadHelper(current.lDasWt, lDasList, 0);
        lDasSpinner.setSelection(lDasPos);
        lDasSelected = (Das) lDasList.get(lDasSpinner.getSelectedItemPosition());

        rPodList = rDasSelected.getAllowablePods();
        int rPodPos = spinnerLoadHelper(current.rPodWt, rPodList, 0);
        rPodAdapter.clear();
        rPodAdapter.addAll(rPodList);
        rPodSpinner.setSelection(rPodPos);
        rPodSelected = (Pod) rPodList.get(rPodSpinner.getSelectedItemPosition());

        lPodList = rDasSelected.getAllowablePods();
        int lPodPos = spinnerLoadHelper(current.lPodWt, lPodList, 0);
        lPodAdapter.clear();
        lPodAdapter.addAll(lPodList);
        lPodSpinner.setSelection(lPodPos);
        lPodSelected = (Pod) lPodList.get(lPodSpinner.getSelectedItemPosition());

        rGunList = rDasSelected.getAllowableGuns();
        int rGunPos = spinnerLoadHelper(current.rGunWt, rGunList, 0);
        rGunAdapter.clear();
        rGunAdapter.addAll(rGunList);
        rGunSpinner.setSelection(rGunPos);
        rGunSelected = (Gun) rGunList.get(rGunSpinner.getSelectedItemPosition());

        lGunList = rDasSelected.getAllowableGuns();
        int lGunPos = spinnerLoadHelper(current.lGunWt, lGunList, 0);
        lGunAdapter.clear();
        lGunAdapter.addAll(lGunList);
        lGunSpinner.setSelection(lGunPos);
        lGunSelected = (Gun) lGunList.get(lGunSpinner.getSelectedItemPosition());

        rRocketList = rPodSelected.getAllowableRockets();
        lRocketList = lPodSelected.getAllowableRockets();
        int lRocketPos = spinnerLoadHelper(current.lRocketWt, lRocketList, 0);
        int rRocketPos = spinnerLoadHelper(current.rRocketWt, rRocketList, 0);
        rRocketAdapter.clear();
        rRocketAdapter.addAll(rRocketList);
        lRocketAdapter.clear();
        lRocketAdapter.addAll(lRocketList);
        rRocketSpinner.setSelection(rRocketPos);
        lRocketSpinner.setSelection(lRocketPos);
        rRocketSelected = (Rocket) rRocketList.get(rRocketSpinner.getSelectedItemPosition());
        lRocketSelected = (Rocket) lRocketList.get(lRocketSpinner.getSelectedItemPosition());

    }
    //returns position of item in list where weight is equal to weapon from list at that position
    public int spinnerLoadHelper(int wt, ArrayList<Weapon> arrayList, int i) {
        if (i == arrayList.size()) {
            return 0;
        }
        else if (wt == arrayList.get(i).getWeight()){
            return i;
        }
        return spinnerLoadHelper(wt, arrayList, i + 1);
    }
    //updates the config spinners
    //called by SaveButton onClick and DeleteButton onClick
    public void updateConfigSpinners() {
        loadAdapter.clear();
        delAdapter.clear();
        loadAdapter.addAll(MainActivity.wpDatabase.wpDao().getSavedConfigs());
        delAdapter.addAll(MainActivity.wpDatabase.wpDao().getSavedConfigs());
        loadAdapter.notifyDataSetChanged();
        delAdapter.notifyDataSetChanged();
    }
    //loads the SQL database into the spinners
    //Called by CreateView
    public void makeConfigSpinners() {
        loadAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, MainActivity.wpDatabase.wpDao().getSavedConfigs());
        loadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loadSpinner.setAdapter(loadAdapter);

        delAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, MainActivity.wpDatabase.wpDao().getSavedConfigs());
        delAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deleteSpinner.setAdapter(delAdapter);
    }
    //creates all the spinner lists, needs to be updated to call the selected weapon to populate the lists,
    // called by onCreateView
    private void makeWeaponSpinners() {
        Das das179 = new Das("179 DAS", 79, 3.525);
        Das das644 = new Das("644 DAS", 78, 3.525);
        Das das179NoBru = new Das("DAS 179 - No BRU", 66, 3.525);
        Das das644NoBru = new Das("DAS 644 - No BRU", 65, 3.525);
        Das noDas = new Das("None", 0, 0);
        //create das lists
        rDasList = new ArrayList<Weapon>();
        lDasList = new ArrayList<Weapon>();
        //adds the initial Das options
        rDasList.add(noDas);
        rDasList.add(das179);
        rDasList.add(das179NoBru);
        rDasList.add(das644);
        rDasList.add(das644NoBru);
        lDasList.add(noDas);
        lDasList.add(das179);
        lDasList.add(das179NoBru);
        lDasList.add(das644);
        lDasList.add(das644NoBru);
        //creates the adapters
        ArrayAdapter<Weapon> rDasAdapter = new ArrayAdapter<Weapon>(this.getContext(), android.R.layout.simple_spinner_item, rDasList);
        ArrayAdapter<Weapon> lDasAdapter = new ArrayAdapter<Weapon>(this.getContext(), android.R.layout.simple_spinner_item, lDasList);
        rDasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lDasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //links the adapters to the spinners
        rDasSpinner.setAdapter(rDasAdapter);
        lDasSpinner.setAdapter(lDasAdapter);
        //sets the selection
        rDasSelected = (Das) rDasList.get(rDasSpinner.getSelectedItemPosition());
        lDasSelected = (Das) lDasList.get(lDasSpinner.getSelectedItemPosition());

        //create gun lists from allowable based on Das selection and link them to their adapters and spinners
        rGunList = rDasSelected.getAllowableGuns();
        lGunList = lDasSelected.getAllowableGuns();
        rGunAdapter = new ArrayAdapter<Weapon>(this.getContext(), android.R.layout.simple_spinner_item, rGunList);
        lGunAdapter = new ArrayAdapter<Weapon>(this.getContext(), android.R.layout.simple_spinner_item, lGunList);
        rGunAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lGunAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rGunSpinner.setAdapter(rGunAdapter);
        lGunSpinner.setAdapter(lGunAdapter);
        rGunSelected = (Gun) rGunList.get(rGunSpinner.getSelectedItemPosition());
        lGunSelected = (Gun) lGunList.get(lGunSpinner.getSelectedItemPosition());

        //create pod lists from allowable based on Das selection and link them to their adapters and spinners
        rPodList = rDasSelected.getAllowablePods();
        lPodList = lDasSelected.getAllowablePods();
        rPodAdapter = new ArrayAdapter<Weapon>(this.getContext(), android.R.layout.simple_spinner_item, rPodList);
        lPodAdapter = new ArrayAdapter<Weapon>(this.getContext(), android.R.layout.simple_spinner_item, lPodList);
        rPodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lPodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rPodSpinner.setAdapter(rPodAdapter);
        lPodSpinner.setAdapter(lPodAdapter);
        rPodSelected = (Pod) rPodList.get(rPodSpinner.getSelectedItemPosition());
        lPodSelected = (Pod) lPodList.get(lPodSpinner.getSelectedItemPosition());

        //create rocket lists from allowable based on Pod selection and link them to their adapters and spinners
        rRocketList = rPodSelected.getAllowableRockets();
        lRocketList = lPodSelected.getAllowableRockets();
        rRocketAdapter = new ArrayAdapter<Weapon>(this.getContext(), android.R.layout.simple_spinner_item, rRocketList);
        lRocketAdapter = new ArrayAdapter<Weapon>(this.getContext(), android.R.layout.simple_spinner_item, lRocketList);
        rRocketAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lRocketAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rRocketSpinner.setAdapter(rRocketAdapter);
        lRocketSpinner.setAdapter(lRocketAdapter);
        rRocketSelected = (Rocket) rRocketList.get(rRocketSpinner.getSelectedItemPosition());
        lRocketSelected = (Rocket) lRocketList.get(lRocketSpinner.getSelectedItemPosition());

        //ensures the adapters self-notify when their spinner's lists change
        rDasAdapter.setNotifyOnChange(true);
        lDasAdapter.setNotifyOnChange(true);
        rGunAdapter.setNotifyOnChange(true);
        lGunAdapter.setNotifyOnChange(true);
        rPodAdapter.setNotifyOnChange(true);
        lPodAdapter.setNotifyOnChange(true);
        rRocketAdapter.setNotifyOnChange(true);
        lRocketAdapter.setNotifyOnChange(true);
    }
    //clears the adapter and attaches a new list
    //called by the onItemSelectedListeners to remake lists when they change
    private void clearAdapterAndAttachList(ArrayAdapter<Weapon> arrayAdapter, ArrayList<Weapon> arrayList, Spinner spinner) {
        arrayAdapter.clear();
        arrayAdapter.addAll(arrayList);
        if (arrayList.size() < spinner.getSelectedItemPosition()) {
            spinner.setSelection(0, true);
        }
    }
    //associates all view variables with the xml file views
    //called by onCreateView
    private void linkViews(View v) {
        textTotRkts = v.findViewById(R.id.total_rocket_output);
        editName = v.findViewById(R.id.config_name);
        editSide = v.findViewById(R.id.side_number);
        editBasicWeight = v.findViewById(R.id.baw_input);
        editPilots = v.findViewById(R.id.pilot_copilot_input);
        editCrew = v.findViewById(R.id.crew_input);
        rDasSpinner = v.findViewById(R.id.r_das_spinner);
        lDasSpinner = v.findViewById(R.id.l_das_spinner);
        editSeats = v.findViewById(R.id.seats_input);
        rGunSpinner = v.findViewById(R.id.r_gun_spinner);
        lGunSpinner = v.findViewById(R.id.l_gun_spinner);
        rPodSpinner = v.findViewById(R.id.r_pod_spinner);
        lPodSpinner = v.findViewById(R.id.l_pod_spinner);
        editOther = v.findViewById(R.id.other_input);
        textOpWeight = v.findViewById(R.id.op_weight);
        rRocketSpinner = v.findViewById(R.id.r_rocket_spinner);
        editRRocket = v.findViewById(R.id.r_rocket_number);
        textRRocket = v.findViewById(R.id.r_rocket_total);
        lRocketSpinner = v.findViewById(R.id.l_rocket_spinner);
        editLRocket = v.findViewById(R.id.l_rocket_number);
        textLRocket = v.findViewById(R.id.l_rocket_total);
        edit762 = v.findViewById(R.id.seven_six_two_number);
        text762 = v.findViewById(R.id.seven_six_two_total);
        edit50Cal = v.findViewById(R.id.fifty_cal_number);
        text50Cal = v.findViewById(R.id.fifty_cal_total);
        editChaff = v.findViewById(R.id.chaff_number);
        textChaff = v.findViewById(R.id.chaff_total);
        editFlare = v.findViewById(R.id.flare_number);
        textFlare = v.findViewById(R.id.flare_total);
        editSM = v.findViewById(R.id.sm_number);
        textSM = v.findViewById(R.id.sm_total);
        editFuel = v.findViewById(R.id.fuel_input);
        editExFuel = v.findViewById(R.id.exfuel_input);
        editPax = v.findViewById(R.id.pax_input);
        editCargo = v.findViewById(R.id.cargo_input);
        textZFWeight = v.findViewById(R.id.zf_weight);
        textGWeight = v.findViewById(R.id.gross_weight);
        textMWeight = v.findViewById(R.id.mission_weight);
        calcButton = v.findViewById(R.id.calc_button);
        loadConfigButton = v.findViewById(R.id.load_button);
        saveConfigButton = v.findViewById(R.id.save_button);
        loadSpinner = v.findViewById(R.id.load_spinner);
        deleteButton = v.findViewById(R.id.del_button);
        deleteSpinner = v.findViewById(R.id.del_spinner);
        openButton = v.findViewById(R.id.open_button);
        closedButton = v.findViewById(R.id.closed_button);
    }
    //calculates all the text fields when fab is pressed
    //called by calculate fab
    @SuppressLint("DefaultLocale")
    public void calculate() {
        current.configName = editName.getText().toString();
        current.side = intFromEdit(editSide);
        current.basicWt = intFromEdit(editBasicWeight);
        current.pilotWt = intFromEdit(editPilots);
        current.crewWt = intFromEdit(editCrew);
        current.seatsWt = intFromEdit(editSeats);
        current.otherWt = intFromEdit(editOther);
        current.lDasWt = lDasSelected.getWeight();
        current.rDasWt = rDasSelected.getWeight();
        current.lPodWt = lPodSelected.getWeight();
        current.rPodWt = rPodSelected.getWeight();
        current.lGunWt = lGunSelected.getWeight();
        current.rGunWt = rGunSelected.getWeight();
        current.lRocketWt = lRocketSelected.getWeight();
        current.rRocketWt = rRocketSelected.getWeight();
        current.lRocketNum = intFromEdit(editLRocket);
        current.rRocketNum = intFromEdit(editRRocket);
        current.fiftyNum = intFromEdit(edit50Cal);
        current.miniNum = intFromEdit(edit762);
        current.chaffNum = intFromEdit(editChaff);
        current.flareNum = intFromEdit(editFlare);
        current.smNum = intFromEdit(editSM);
        current.fuelWt = intFromEdit(editFuel);
        current.exfuelWt = intFromEdit(editExFuel);
        current.paxWt = intFromEdit(editPax);
        current.cargoWt = intFromEdit(editCargo);
        current.rDasDrag = rDasSelected.getDrag();
        current.lDasDrag = lDasSelected.getDrag();
        current.rPodDrag = rPodSelected.getDrag();
        current.lPodDrag = lPodSelected.getDrag();
        current.rGunDrag = rGunSelected.getDrag();
        current.lGunDrag = lGunSelected.getDrag();

        textRRocket.setText(String.valueOf(current.getRRocketTotal()));
        textLRocket.setText(String.valueOf(current.getLRocketTotal()));
        text762.setText(String.valueOf(current.getMiniWt()));
        text50Cal.setText(String.valueOf(current.getFiftyWt()));
        textFlare.setText(String.valueOf(current.getFlareWt()));
        textChaff.setText(String.valueOf(current.getChaffWt()));
        textSM.setText(String.valueOf(current.getSmWt()));
        if (openButton.isChecked()) {
            current.doorDrag = 3;
        }
        else {
            current.doorDrag = 0;
        }
        textTotRkts.setText(String.valueOf((current.getLRocketTotal()) + (current.getRRocketTotal())));
        textOpWeight.setText(String.format("Operating Weight: %d", current.getOpWeight()));
        textZFWeight.setText(String.format("Zero Fuel Weight: %d", current.getZeroFuelWeight()));
        textGWeight.setText(String.format("Gross Weight: %d", current.getGrossWeight()));
        textMWeight.setText(String.format("Mission Weight: %d", current.getMissionGrossWeight()));
        ((MainActivity) getActivity()).setConfiguration(current);
        if (current.getMissionGrossWeight() > 18500 || current.getMissionGrossWeight() < 13000) {
            Snackbar mySnackbar = Snackbar.make(view.findViewById(R.id.myframelayout), "Gross Weights < 13000 or > 18500 may produce erroneous results", Snackbar.LENGTH_LONG);
            mySnackbar.show();
        }

    }

    //takes an EditText and returns its integer input, if no input returns zero
    //called by calculate
    private int intFromEdit(EditText et) {
        String temp;
        temp = et.getText().toString();
        if (!"".equals(temp)) {
            return (int) Integer.parseInt(temp);
        }
        else {
            return 0;
        }
    }
    public void onPause() {
        ((MainActivity) getActivity()).setConfiguration(current);
        super.onPause();
    }
}

/**
 * Use this factory method to create a new instance of
 * this fragment using the provided parameters.
 *
 * @param param1 Parameter 1.
 * @param param2 Parameter 2.
 * @return A new instance of fragment ConfigTab.
 *
// TODO: Rename and change types and number of parameters
public static ConfigTab newInstance(String param1, String param2) {
ConfigTab fragment = new ConfigTab();
Bundle args = new Bundle();
args.putString(ARG_PARAM1, param1);
args.putString(ARG_PARAM2, param2);
fragment.setArguments(args);
return fragment;

}*/