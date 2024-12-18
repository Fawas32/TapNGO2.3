package com.app.tapngo.Frontend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tapngo.R;
import com.app.tapngo.Backend.HttpRequest;
import com.app.tapngo.models.StationDataModel;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity {
    List<StationDataModel> stationDataList, finalList;
    RecyclerView recyclerView;
    TextView tvFrom, tvTo, tvViewMap;
    ImageView imgLocation;
    Button btnReminder, btnBack;
    public static double latiFrom, longiFrom, latiTo, longiTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showProgressDialog();
        stationDataList = new ArrayList<>();
        finalList = new ArrayList<>();

        tvFrom = findViewById(R.id.tvFrom);
        tvTo = findViewById(R.id.tvTo);
        tvViewMap = findViewById(R.id.tvViewMap);
        imgLocation = findViewById(R.id.imgLocation);
        btnReminder = findViewById(R.id.btnReminder);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        btnBack = findViewById(R.id.btnBack);

        tvFrom.setText("From: "+SearchDestinationActivity.from);
        tvTo.setText("To: "+SearchDestinationActivity.to);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ReminderActivity.class));
            }
        });
        tvViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Get a list of Address objects
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    List<Address> addressesFrom = geocoder.getFromLocationName(SearchDestinationActivity.from, 1);
                    List<Address> addressesTo = geocoder.getFromLocationName(SearchDestinationActivity.to, 1);

                    if (addressesFrom != null && !addressesFrom.isEmpty()) {
                        // Get the first address
                        Address address = addressesFrom.get(0);
                        // Extract latitude and longitude
                        latiFrom = address.getLatitude();
                        longiFrom = address.getLongitude();

                        if (addressesTo != null && !addressesTo.isEmpty()) {
                            // Get the first address
                            Address address2 = addressesTo.get(0);
                            // Extract latitude and longitude
                            latiTo = address2.getLatitude();
                            longiTo = address2.getLongitude();

                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        } else {
                            Toast.makeText(MainActivity.this, "To location not found", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(MainActivity.this, "From location not found", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        imgLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Get a list of Address objects
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    List<Address> addressesFrom = geocoder.getFromLocationName(SearchDestinationActivity.from, 1);
                    List<Address> addressesTo = geocoder.getFromLocationName(SearchDestinationActivity.to, 1);

                    if (addressesFrom != null && !addressesFrom.isEmpty()) {
                        // Get the first address
                        Address address = addressesFrom.get(0);
                        // Extract latitude and longitude
                        latiFrom = address.getLatitude();
                        longiFrom = address.getLongitude();

                        if (addressesTo != null && !addressesTo.isEmpty()) {
                            // Get the first address
                            Address address2 = addressesTo.get(0);
                            // Extract latitude and longitude
                            latiTo = address2.getLatitude();
                            longiTo = address2.getLongitude();

                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        } else {
                            Toast.makeText(MainActivity.this, "To location not found", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(MainActivity.this, "From location not found", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        postDataOnServer();
    }

    private void postDataOnServer() {
        new postDataTask().execute();
    }

    class postDataTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {
            String response = "";
            stationDataList.clear();
            finalList.clear();
            stationDataList = HttpRequest.executeGet();
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            Log.d("responseListSize",stationDataList.size()+"");
            Log.d("responseeee",response);

            for(StationDataModel model : stationDataList){
                if(model.getOrigin().toLowerCase().contains(SearchDestinationActivity.from.toLowerCase()) && model.getDestination().toLowerCase().contains(SearchDestinationActivity.to.toLowerCase()) && model.getTraindate().equals(SearchDestinationActivity.date)){
                    finalList.add(model);
                }
            }
            hideProgressDialog();
            if(finalList.size()>0){
                recyclerView.setAdapter(null);
                ItemsListAdapter adapter = new ItemsListAdapter(MainActivity.this, finalList);
                recyclerView.setAdapter(adapter);
            }else {
                Toast.makeText(MainActivity.this, "No data found!", Toast.LENGTH_LONG).show();
            }

        }
    }

    public class ItemsListAdapter extends RecyclerView.Adapter<ItemsListAdapter.ImageViewHolder>{
        private Context mcontext ;
        private List<StationDataModel> muploadList;

        public ItemsListAdapter(Context context , List<StationDataModel> uploadList) {
            mcontext = context ;
            muploadList = uploadList ;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mcontext).inflate(R.layout.train_data_layout, parent , false);
            return (new ImageViewHolder(v));
        }

        @Override
        public void onBindViewHolder(final ImageViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            final StationDataModel item = muploadList.get(position);
            holder.tvTrainCode.setText("Train Code: "+item.getTraincode());
            holder.tvOriginTime.setText(item.getOrigintime());
            holder.tvOrigin.setText(item.getOrigin());
            holder.tvDestTime.setText(item.getDestinationtime());
            holder.tvDest.setText(item.getDestination());

            calculateTimeDifference(item.getTraindate(), item.getExparrival(), holder.tvArriving);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        private void calculateTimeDifference(String trainDate, String expectedArrivalTime, TextView resultTextView) {
            // Define formats for date and time
            SimpleDateFormat trainDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            try {
                // Trim input strings
                trainDate = trainDate.trim();
                expectedArrivalTime = expectedArrivalTime.trim();

                // Get the current date and time
                Calendar calendar = Calendar.getInstance();
                String currentDate = currentDateFormat.format(calendar.getTime());
                String currentTime = timeFormat.format(calendar.getTime());

                Log.d("TimeDebug", "Train Date: " + trainDate + ", Train Time: " + expectedArrivalTime);
                Log.d("TimeDebug", "Current Date: " + currentDate + ", Current Time: " + currentTime);

                // Parse train date using its specific format
                Date trainDateObj = trainDateFormat.parse(trainDate);

                // Parse current date using its specific format
                Date currentDateObj = currentDateFormat.parse(currentDate);

                if (trainDateObj != null && currentDateObj != null && trainDateObj.equals(currentDateObj)) {
                    // Parse expected arrival time and current time
                    Date trainTimeObj = timeFormat.parse(expectedArrivalTime);
                    Date currentTimeObj = timeFormat.parse(currentTime);

                    if (trainTimeObj != null && currentTimeObj != null) {
                        // Calculate the difference in milliseconds
                        long differenceInMillis = trainTimeObj.getTime() - currentTimeObj.getTime();

                        // Convert to minutes
                        long minutesDifference = differenceInMillis / (1000 * 60);

                        // Display result
                        if (minutesDifference > 0) {
                            resultTextView.setText("Arriving in " + minutesDifference + " minutes.");
                        } else {
                            resultTextView.setText("Train has already passed.");
                        }
                    }
                } else {
                    resultTextView.setText("Train is not scheduled for today.");
                }
            } catch (ParseException e) {
                e.printStackTrace();
                resultTextView.setText("Error: Unable to parse date or time!");
            }
        }



        @Override
        public int getItemCount() {
            return muploadList.size();
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder{
            public TextView tvTrainCode;
            public TextView tvOriginTime;
            public TextView tvOrigin;
            public TextView tvDestTime;
            public TextView tvDest;
            public TextView tvArriving;

            public ImageViewHolder(View itemView) {
                super(itemView);

                tvTrainCode = itemView.findViewById(R.id.tvTrainCode);
                tvOriginTime = itemView.findViewById(R.id.tvOriginTime);
                tvOrigin = itemView.findViewById(R.id.tvOrigin);
                tvDestTime = itemView.findViewById(R.id.tvDestTime);
                tvDest = itemView.findViewById(R.id.tvDest);
                tvArriving = itemView.findViewById(R.id.tvArriving);
            }
        }
    }
}