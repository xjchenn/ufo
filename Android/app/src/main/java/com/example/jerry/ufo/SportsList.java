package com.example.jerry.ufo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class SportsList extends AppCompatActivity implements com.example.jerry.ufo.View{
    List<Sport> sports;
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Model.getInstance().setView(this);
        setContentView(R.layout.activity_sports_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Sports");
        setSupportActionBar(toolbar);

        list = (ListView) findViewById(R.id.sportsList);
        update();
    }

    public void update() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sports = Model.getInstance().getSports();

                list.setAdapter(new ArrayAdapter<Sport>(SportsList.this, R.layout.sport_item, sports) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View itemView = convertView;
                        if(itemView == null) {
                            itemView = getLayoutInflater().inflate(R.layout.sport_item, parent, false);
                        }

                        Sport curSport = sports.get(position);

                        TextView textView = (TextView) itemView.findViewById(R.id.sportTextView);
                        textView.setText(curSport.getName());

                        return itemView;
                    }
                });

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                        Intent queryFacilities = new Intent(SportsList.this, FacilityList.class);
                        queryFacilities.putExtra("query",  sports.get(position).getName());
                        queryFacilities.putExtra("type", 0);
                        startActivity(queryFacilities);
                    }
                });
            }
        });

    }
}
