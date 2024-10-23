package com.example.jerry.ufo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class FacilityList extends AppCompatActivity implements com.example.jerry.ufo.View {
    List<Facility> facilities;
    ListView list;
    int mode = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Model.getInstance().setView(this);

        setContentView(R.layout.activity_facility_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Facilities");
        setSupportActionBar(toolbar);
        mode = getIntent().getIntExtra("type", 0);

        API api = new API(this);
        if (mode == 0) {
            String query = getIntent().getStringExtra("query");
            api.loadFacilitiesBySport(query);
        } else if (mode == 1){
            String query = getIntent().getStringExtra("query");
            api.loadFacilitiesByBuilding(query);
        } else{
        }
        Model.getInstance().setMode(mode);
        list = (ListView) findViewById(R.id.facilitisList);
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Model.getInstance().getFacilities()!=null && mode!=2 )||
            (Model.getInstance().getSubscriptionList()!=null && mode ==2)) {
                update();
        }
    }

    public void update() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mode == 2){
                    facilities = Model.getInstance().getSubscriptionList();
                }else {
                    facilities = Model.getInstance().getFacilities();
                }
                list.setAdapter(new ArrayAdapter<Facility>(FacilityList.this, R.layout.facility_item, facilities) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View itemView = convertView;
                        if (itemView == null) {
                            itemView = getLayoutInflater().inflate(R.layout.facility_item, parent, false);
                        }

                        Facility curFacility = facilities.get(position);

                        TextView facilityTextView = (TextView) itemView.findViewById(R.id.facilityTextView);
                        facilityTextView.setText(curFacility.getName());

                        TextView availabilityTextView = (TextView) itemView.findViewById(R.id.availabilityTextView);
                        availabilityTextView.setText(curFacility.getAvailability().text());
                        availabilityTextView.setTextColor(Color.rgb((int)Math.round(255*((5-curFacility.getAvailability().ordinal())/5.0)),(int)Math.round(255*(curFacility.getAvailability().ordinal()/5.0)),0));

                        return itemView;
                    }
                });

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                        Intent queryFacilities = new Intent(FacilityList.this, FacilityInfo.class);
                        queryFacilities.putExtra("curfacility",facilities.get(position).getName());
                        queryFacilities.putExtra("curbuilding",facilities.get(position).getBuilding().getName());
                        queryFacilities.putExtra("isSubscribed",Model.getInstance().isSubscribed(facilities.get(position)));
                        startActivity(queryFacilities);
                    }
                });
            }
        });

    }
}
