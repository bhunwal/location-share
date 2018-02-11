package com.locationupdates;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class StartScreenActivity extends Activity implements AdapterView.OnItemSelectedListener {

    Button getLocationButton, shareLocationButton;
    Intent intent;
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public static final String SHARE_LOCATION = "com.example.myfirstapp.SHARE";
    public static final String GET_LOCATION = "com.example.myfirstapp.GET";
    public static final String CAB_ID = "CAB_ID";
    Spinner spinner;
    private String currentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        getLocationButton = (Button)findViewById(R.id.getLocationButton);
        shareLocationButton = (Button)findViewById(R.id.shareLocationButton);
        getLocationButton.setOnClickListener(onClickListener);
        shareLocationButton.setOnClickListener(onClickListener);

        intent = new Intent(this, Map.class);

        List<String> cabIds = new ArrayList<String>();//TODO make an api call to get this?
        cabIds.add("cab1");
        cabIds.add("maneeesh");
        cabIds.add("maneesh123");

        spinner = (Spinner) findViewById(R.id.selectCabSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cabIds);
       // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch(v.getId()){
                case R.id.getLocationButton:
                    StartMapsToGetLocation();
                    break;
                case R.id.shareLocationButton:
                    //DO something
                    StartMapsToSendLocation();
                    break;
            }

        }
    };

    private void StartMapsToSendLocation() {
        if(this.currentId != null)
        {
            intent.putExtra(EXTRA_MESSAGE, SHARE_LOCATION);
            intent.putExtra(CAB_ID, this.currentId);
            startActivity(intent);
        }
    }
    private void StartMapsToGetLocation() {
        if(this.currentId != null)
        {
            intent.putExtra(EXTRA_MESSAGE, GET_LOCATION);
            intent.putExtra(CAB_ID, this.currentId);
            startActivity(intent);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();
        this.currentId = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(this,
                "onNothingSelected : ",
                Toast.LENGTH_SHORT).show();
    }
}
