package com.example.coogan.googlemapsfunctionality;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private Intent intent = null, chooser = null;
    private SharedPreferences savedInfo;
    private EditText enter_coordinatesET, tag_locationET;
    private TextView current_LocationsTV;
    private Button add_locationBT, clear_locationsBT, current_locationBT;
    private TableLayout myTL;
    private static final String SAVED_INFO_MARKERS = "TaggedMarkers";

    private boolean hasMapsOpened = false;
    private double currentLatitude, currentLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        savedInfo = getSharedPreferences(SAVED_INFO_MARKERS, MODE_PRIVATE);

        // Edit Texts
        enter_coordinatesET = findViewById(R.id.enter_coordinatesET);
        tag_locationET = findViewById(R.id.tag_locationET);

        // TextViews
        current_LocationsTV = findViewById(R.id.current_locationsTV);

        // Buttons
        add_locationBT = findViewById(R.id.add_locationBT);
        current_locationBT = findViewById(R.id.add_current_locationBT);
        clear_locationsBT = findViewById(R.id.clear_locationsBT);

        // Table Layout
        myTL = findViewById(R.id.tableLayout);

        refreshButton("", true);

        requestPermission();

        /*Bundle bundle = getIntent().getExtras();
        try {
            currentLatitude = bundle.getDouble("latitude");
        } catch (NullPointerException e) { }

        try {
            currentLongitude = bundle.getDouble("longitude");
        } catch (NullPointerException e) { } */
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_locationBT:
                if(enter_coordinatesET.getText().length() > 0 && tag_locationET.getText().length() > 0) {
                    String coordinates = enter_coordinatesET.getText().toString();
                    String tag = tag_locationET.getText().toString();
                    boolean tagAlreadySaved;
                    tagAlreadySaved = savedInfo.contains(tag);
                    SharedPreferences.Editor myEditor = savedInfo.edit();
                    myEditor.putString(tag, coordinates);
                    myEditor.apply();
                    if(!tagAlreadySaved) {
                        refreshButton(tag, false);
                    }
                    enter_coordinatesET.setText("");
                    tag_locationET.setText("");
                } else {
                    AlertDialog.Builder bld = new AlertDialog.Builder(MainActivity.this);
                    bld.setTitle("Missing Text");
                    bld.setMessage("Please enter Coordinates.");
                    bld.setPositiveButton("OK", null);
                    AlertDialog missingDialog = bld.create();
                    missingDialog.show();
                }
                break;
            case R.id.open_mapsBT:
                intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
                break;
            case R.id.add_current_locationBT:
                if (hasMapsOpened) {
                    Bundle bundle = getIntent().getExtras();
                    currentLongitude = bundle.getDouble("longitude");
                    currentLatitude = bundle.getDouble("latitude");
                } else {
                    currentLatitude = (33.939167526582196);
                    currentLongitude = (-84.520017204843882);
                }

                String coordinatesCurrent = Double.toString(currentLatitude) + "," + Double.toString(currentLongitude);
                String tagCurrent = "Current Location";

                boolean currentAlreadySaved = savedInfo.contains(tagCurrent);
                SharedPreferences.Editor myEditorCurrent = savedInfo.edit();
                myEditorCurrent.putString(coordinatesCurrent, tagCurrent);
                myEditorCurrent.apply();
                if (!currentAlreadySaved) {
                    refreshButton(tagCurrent, false);
                }
                break;
            case R.id.clear_locationsBT:
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);

                adb.setTitle("Are you sure?");
                adb.setMessage("This will delete all saved Locations.");
                adb.setCancelable(true);
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Erase", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myTL.removeAllViews();
                        SharedPreferences.Editor myEditor = savedInfo.edit();
                        myEditor.clear();
                        myEditor.apply();
                    }
                });

                AlertDialog confirmDialog = adb.create();
                confirmDialog.show();
                break;
            case R.id.markerBTN:
                String tag1 = ((Button)view).getText().toString();
                String marker1 = savedInfo.getString(tag1, "");
                String coordinates = "geo:" + marker1;
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(coordinates));
                chooser = Intent.createChooser(intent, "Launch Maps");
                startActivity(chooser);
                break;
            case R.id.newEditBTN:
                TableRow row = (TableRow)view.getParent();
                Button tagBTN = (Button)row.findViewById(R.id.markerBTN);
                String tag2 = tagBTN.getText().toString();
                String marker2 = savedInfo.getString(tag2, "");
                tag_locationET.setText(tag2);
                enter_coordinatesET.setText(marker2);
                break;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    private void refreshButton(String tag, boolean applyToAll) {
        Map<String,?> queryMap = savedInfo.getAll();
        Set<String> tagSet = queryMap.keySet();
        String[] tags = tagSet.toArray(new String[0]);
        Arrays.sort(tags, String.CASE_INSENSITIVE_ORDER);

        int index = Arrays.binarySearch(tags, tag);
        if(applyToAll){
            for (int i = 0; i<tags.length; i++){
                makeTagGUI(tags[i], i);
            }
        } else { makeTagGUI(tag, index); }
    }

    private void makeTagGUI(String tag, int index) {
        LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = li.inflate(R.layout.new_marker_view, null);

        Button markerBTN = (Button)row.findViewById(R.id.markerBTN);
        markerBTN.setText(tag);

        Button editBTN = (Button)row.findViewById(R.id.newEditBTN);
        myTL.addView(row, index);
    }
}
