package com.tracker.ui.UIScreens;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.tracker.ui.MainActivity;
import com.tracker.ui.Managers.ManageDevices;
import com.tracker.ui.Models.ItemsModel;
import com.tracker.ui.R;

import java.util.ArrayList;
import java.util.List;

public class CityScreen extends AppCompatActivity implements Handler.Callback {
    public static Handler CityScreenHandler;

    String cities[] = {"Sibiu", "Brasov", "Cluj", "Bucuresti", "Timisoara", "Constanta", "Oradea", "Valcea", "Craiova", "Sebes", "Timis"};
    String description[] = {"30 active locations", "50 active locations", "140 active locations", "230 active locations", "80 active locations", "30 active locations", "50 active locations", "140 active locations", "230 active locations", "80 active locations", "80 active locations"};
    int images[] = {R.drawable.buildings, R.drawable.qwer, R.drawable.hospital, R.drawable.embassy,
            R.drawable.property, R.drawable.city, R.drawable.windmill, R.drawable.home, R.drawable.office, R.drawable.buildings, R.drawable.building1};
    int percentages[] = {(int) ManageDevices.getPercentage(MainActivity.myHomeDevices, getResources().getInteger(R.integer.myHome_maxSpots)), 45, 21, 81, 44, 77};
    String statusDesc[] = {ManageDevices.calculateCrowdedness(R.string.myHome, MainActivity.myHomeDevices), "Crowded", "Not Busy", "Busy", "Busy"};
    List<ItemsModel> itemsModelList = new ArrayList<>();
    ListView listView;
    LocationAdapter customAdapter;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selector);

        CityScreenHandler = new Handler(this);
        listView = findViewById(R.id.listview);
        for (int i = 0; i < cities.length; i++) {
            ItemsModel itemsModel = new ItemsModel(cities[i], description[i], images[i], percentages[i], statusDesc[i]);
            itemsModelList.add(itemsModel);
        }

        customAdapter = new LocationAdapter(itemsModelList, this);
        listView.setAdapter(customAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.searchView);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("Main", " data search" + newText);
                customAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.searchView) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean handleMessage(Message msg) {
        statusDesc[0] = ManageDevices.calculateCrowdedness(R.string.myHome, (int) msg.obj);
        percentages[0] = (int) ManageDevices.getPercentage((int) msg.obj, getResources().getInteger(R.integer.myHome_maxSpots));
        updateAdapter();
        customAdapter.notifyDataSetChanged();
        return true;
    }


    public class LocationAdapter extends BaseAdapter implements Filterable {

        private List<ItemsModel> itemsModelsl;
        private List<ItemsModel> itemsModelListFiltered;

        LocationAdapter(List<ItemsModel> itemsModelsl, Context context) {
            this.itemsModelsl = itemsModelsl;
            this.itemsModelListFiltered = itemsModelsl;
        }


        @Override
        public int getCount() {
            return itemsModelListFiltered.size();
        }

        @Override
        public Object getItem(int position) {
            return itemsModelListFiltered.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.row_items, null);


            TextView names = view.findViewById(R.id.name);
            TextView emails = view.findViewById(R.id.email);
            ImageView imageView = view.findViewById(R.id.images);

            names.setText(itemsModelListFiltered.get(position).getName());
            emails.setText(itemsModelListFiltered.get(position).getEmail());
            imageView.setImageResource(itemsModelListFiltered.get(position).getImages());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("main activity", "item clicked");
                    startActivity(new Intent(CityScreen.this,
                            ItemsPreviewActivity.class).putExtra("items",
                            itemsModelListFiltered.get(position)));

                }
            });

            return view;
        }


        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults filterResults = new FilterResults();
                    if (constraint == null || constraint.length() == 0) {
                        filterResults.count = itemsModelsl.size();
                        filterResults.values = itemsModelsl;
                    } else {
                        List<ItemsModel> resultsModel = new ArrayList<>();
                        String searchStr = constraint.toString().toLowerCase();

                        for (ItemsModel itemsModel : itemsModelsl) {
                            if (itemsModel.getName().contains(searchStr) || itemsModel.getEmail().contains(searchStr)) {
                                resultsModel.add(itemsModel);
                            }
                            filterResults.count = resultsModel.size();
                            filterResults.values = resultsModel;
                        }
                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    itemsModelListFiltered = (List<ItemsModel>) results.values;
                    notifyDataSetChanged();

                }
            };
            return filter;
        }
    }

    public void updateAdapter() {
        itemsModelList.clear();
        for (int i = 0; i < percentages.length; i++) {
            ItemsModel itemsModel = new ItemsModel(cities[i], description[i], images[i], percentages[i], statusDesc[i]);
            itemsModelList.add(itemsModel);
        }
        customAdapter = new LocationAdapter(itemsModelList, this);

        listView.setAdapter(customAdapter);

    }
}
