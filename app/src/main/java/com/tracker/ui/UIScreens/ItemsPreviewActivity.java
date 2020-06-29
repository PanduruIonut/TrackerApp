package com.tracker.ui.UIScreens;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
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
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.tracker.ui.MainActivity;
import com.tracker.ui.Managers.ManageDevices;
import com.tracker.ui.Models.ItemsModel;
import com.tracker.ui.R;

import java.util.ArrayList;
import java.util.List;

public class ItemsPreviewActivity extends AppCompatActivity implements Handler.Callback {

    ItemsModel itemsModel;
    String statusAvi;
    public static Handler CityLocationHandler;

    static String names[] = {"myHome", "Ulbs Stiinte", "Biblioteca Judeteana", "Ulbs Decanat", "Starbucks", "Posta Romana"};
    String status[] = {"Open", "Partially open", "Partially open", "Partially open", "Open", "Closed"};
    int images[] = {R.drawable.home, R.drawable.university, R.drawable.study, R.drawable.certificate, R.drawable.chat, R.drawable.message};
    static int percentages[] = {(int) ManageDevices.getPercentage(MainActivity.myHomeDevices, MainActivity.context.getResources().getInteger(R.integer.myHome_maxSpots)), 35, 45, 21, 81, 44, 77};
    String statusDesc[] = {ManageDevices.calculateCrowdedness(R.string.myHome, MainActivity.myHomeDevices), "Not Busy", "Crowded", "Not Busy", "Busy", "Busy"};


    List<ItemsModel> itemsModelList = new ArrayList<>();

    ListView listView;

    CustomAdapter customAdapter;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_preview);

        CityLocationHandler = new Handler(this);
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            itemsModel = (ItemsModel) intent.getSerializableExtra("items");
        }

        listView = findViewById(R.id.spotsList);
        for (int i = 0; i < names.length; i++) {
            ItemsModel itemsModel = new ItemsModel(names[i], status[i], images[i], percentages[i], statusDesc[i]);
            itemsModelList.add(itemsModel);
        }
        customAdapter = new CustomAdapter(itemsModelList, this);

        listView.setAdapter(customAdapter);


    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        statusDesc[0] = ManageDevices.calculateCrowdedness(R.string.myHome, (int) msg.obj);
        percentages[0] = (int) ManageDevices.getPercentage((int) msg.obj, getResources().getInteger(R.integer.myHome_maxSpots));
        updateAdapter();
        customAdapter.notifyDataSetChanged();
        return false;
    }

    public class CustomAdapter extends BaseAdapter implements Filterable {

        private List<ItemsModel> itemsModelsl;
        private List<ItemsModel> itemsModelListFiltered;
        private Context context;
        String  typeArray[] ={"Custom","Study", "Study", "PaperWork", "Fun", "PaperWork"};


        public CustomAdapter(List<ItemsModel> itemsModelsl, Context context) {
            this.itemsModelsl = itemsModelsl;
            this.itemsModelListFiltered = itemsModelsl;
            this.context = context;
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

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.spot_info_item, null);

            final TextView names = view.findViewById(R.id.name);
            TextView emails = view.findViewById(R.id.email);
            TextView type = view.findViewById(R.id.type);
            TextView crowdedness = view.findViewById(R.id.crowdedness);
            ImageView imageView = view.findViewById(R.id.images);
            ProgressBar progressBar = view.findViewById(R.id.percentageBar);

            names.setText(itemsModelListFiltered.get(position).getName());
            emails.setText(itemsModelListFiltered.get(position).getEmail());
            type.setText(typeArray[position]);
            final int percentage = itemsModelListFiltered.get(position).getPercent();
            imageView.setImageResource(itemsModelListFiltered.get(position).getImages());
            progressBar.setProgress(percentage);
            if (percentage <= 35) {
                statusAvi = ("Not Busy " + percentage + "%");
                crowdedness.setText(statusAvi);
                progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                ;

            } else if (percentage <= 75) {
                statusAvi = "Crowded - " + percentage + "%";
                crowdedness.setText(statusAvi);
                progressBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));

            } else {
                statusAvi = "Full " + percentage + "%";
                crowdedness.setText(statusAvi);
                progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
            }

            view.setOnClickListener(v -> {
                Intent intent = new Intent(ItemsPreviewActivity.this, SpotDetailsScreen.class);
                intent.putExtra("items", itemsModelListFiltered.get(position));
                intent.putExtra("placeName", itemsModelListFiltered.get(position).getName());
                intent.putExtra("percentage", String.valueOf(itemsModelListFiltered.get(position).getPercent()));
                intent.putExtra("percentageDesc", itemsModelListFiltered.get(position).getStatusDesc());
                startActivity(intent);

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

    public void updateAdapter() {
        itemsModelList.clear();
        for (int i = 0; i < names.length; i++) {
            ItemsModel itemsModel = new ItemsModel(names[i], status[i], images[i], percentages[i], statusDesc[i]);
            itemsModelList.add(itemsModel);
        }
        customAdapter = new CustomAdapter(itemsModelList, this);

        listView.setAdapter(customAdapter);
    }
}
