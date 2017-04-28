package revisednoteapp.sayan.revisednoteapp;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

/**
 * Created by banersay on 06-08-2016.
 */
public class WeatherActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView cityField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView humidity_field;
    TextView pressure_field;
    TextView weatherIcon;
    TextView updatedField;
    TextView updatedCityField;
    Typeface weatherFont;
    double latitude;
    double longitude;
    String block;
    private GoogleMap mMap;
    private ProgressDialog mSpinner;
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 2;
    private Location myLoc;

    public int countNotificationWeather = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.slide_from_up, R.anim.slide_to_down);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_weather);

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect exact location.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE_LOCATION);
                }
            });
            builder.show();
            //return;
        } else {
            mMap.setMyLocationEnabled(true);
            //showSpinner();

            Criteria criteria = new Criteria();
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    myLoc = location;

                    updateLocation(myLoc);

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };
            //Toast.makeText(getApplicationContext(), "Map done", Toast.LENGTH_SHORT).show();
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000*20,10,locationListener); // 20 secs, 10 meters
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000*20, 10, locationListener);
            }


            String bestProvider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(bestProvider);


            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

            if (!wifiManager.isWifiEnabled()) {
                buildAlertMessageNoWLAN();
            }
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();

            }
            if (location != null) {
                //onLocationChanged(location);
                //updateLocation(location);

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE_LOCATION) {
            /*if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setTrafficEnabled(true);
                    Toast.makeText(getApplicationContext(),"Permission granted",Toast.LENGTH_SHORT).show();

                }
            }*/

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(getPackageName(), "coarse location permission granted");
                //Toast.makeText(getApplicationContext(),"Permission granted",Toast.LENGTH_SHORT).show();
                onMapReady(mMap);
                //updateLocation(myLoc);
            } else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Functionality limited");
                builder.setMessage("Since location access has not been granted, this app will not be running in its full functionality.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {

                    }
                });
                builder.show();
            }
        }
    }

    private void updateLocation(Location location){
        //TextView locationTv = (TextView) findViewById(R.id.latlongLocation);

        double latitude = location.getLatitude();

        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


        try {
            /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if(!((WeatherActivity.this).isFinishing()))
                    {
                        showSpinner();
                    }
                }
            }, 1000);*/
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String street = addresses.get(0).getAddressLine(0); // If any additional address line
            // present than only,
            // check with max available address
            // lines by getMaxAddressLineIndex()
            block = addresses.get(0).getAddressLine(1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String mlatitude = getResources().getString(R.string.latitude, latitude);
            /*locationTv.setText(mlatitude);
            locationTv.append("\n");*/
            String mlongitude = getResources().getString(R.string.longitude, longitude);
            /*locationTv.append(mlongitude);
            locationTv.append("\n");*/
            String mStreet = getResources().getString(R.string.street, street);
            /*locationTv.append(mStreet);
            locationTv.append("\n");*/
            String mBlock = getResources().getString(R.string.block, block);
            /*locationTv.append(mBlock);
            locationTv.append("\n");*/
            String mCity = getResources().getString(R.string.city, city);
            /*locationTv.append(mCity);
            locationTv.append("\n");*/
            String mState = getResources().getString(R.string.state, state);
            /*locationTv.append(mState);
            locationTv.append("\n");*/
            String mCountry = getResources().getString(R.string.country, country);
            /*locationTv.append(mCountry);
            locationTv.append("\n");*/
            String mPostalCode = getResources().getString(R.string.postalCode, postalCode);
            /*locationTv.append(mPostalCode);*/

            weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/weathericonsregularwebfont.ttf");

            cityField = (TextView)findViewById(R.id.city_field);
            updatedCityField = (TextView)findViewById(R.id.updated_city_field);
            updatedField = (TextView)findViewById(R.id.updated_field);
            detailsField = (TextView)findViewById(R.id.details_field);
            currentTemperatureField = (TextView)findViewById(R.id.current_temperature_field);
            humidity_field = (TextView)findViewById(R.id.humidity_field);
            pressure_field = (TextView)findViewById(R.id.pressure_field);
            weatherIcon = (TextView)findViewById(R.id.weather_icon);
            weatherIcon.setTypeface(weatherFont);


            WeatherFunctions.placeIdTask asyncTask = new WeatherFunctions.placeIdTask(new WeatherFunctions.AsyncResponse() {
                @Override
                public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {
                    cityField.setText(weather_city);
                    updatedCityField.setText(block);
                    updatedField.setText(weather_updatedOn);
                    detailsField.setText(weather_description);
                    currentTemperatureField.setText(weather_temperature);
                    humidity_field.setText("Humidity: "+weather_humidity);
                    pressure_field.setText("Pressure: "+weather_pressure);
                    weatherIcon.setText(Html.fromHtml(weather_iconText));
                    if(countNotificationWeather == 1){
                        notifyingWeather(getApplicationContext(),weather_temperature,weather_description);
                        countNotificationWeather++;
                    }


                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("temperature",weather_temperature);
                    intent.putExtra("description",weather_description);
                    if (intent.hasExtra(weather_temperature)){
                        startActivity(intent);
                    }
                }
            });
            Intent in = getIntent();
            String strLat =  in.getStringExtra("Latitude");
            String strLong =  in.getStringExtra("Longitude");
            Log.i("Lat: ",String.valueOf(strLat));
            Log.i("Long: ",String.valueOf(strLong));

            Log.i("Lati: ",String.valueOf(latitude));
            Log.i("Longi: ",String.valueOf(longitude));
            //asyncTask.execute("25.180000", "89.530000");
            asyncTask.execute(String.valueOf(latitude),String.valueOf(longitude)); //  asyncTask.execute("Latitude", "Longitude")

        } catch (Exception e) {
            Log.d("Message", "Location exception");
        }

    }

    private void buildAlertMessageNoWLAN() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your WLAN/Wi-fi seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    public boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    /*@Override
    public void onLocationChanged(Location location) {

        //TextView locationTv = (TextView) findViewById(R.id.latlongLocation);

        latitude = location.getLatitude();

        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


        try {
            showSpinner();
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String street = addresses.get(0).getAddressLine(0); // If any additional address line
            // present than only,
            // check with max available address
            // lines by getMaxAddressLineIndex()
            block = addresses.get(0).getAddressLine(1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String mlatitude = getResources().getString(R.string.latitude, latitude);
            *//*locationTv.setText(mlatitude);
            locationTv.append("\n");*//*
            String mlongitude = getResources().getString(R.string.longitude, longitude);
            *//*locationTv.append(mlongitude);
            locationTv.append("\n");*//*
            String mStreet = getResources().getString(R.string.street, street);
            *//*locationTv.append(mStreet);
            locationTv.append("\n");*//*
            String mBlock = getResources().getString(R.string.block, block);
            *//*locationTv.append(mBlock);
            locationTv.append("\n");*//*
            String mCity = getResources().getString(R.string.city, city);
            *//*locationTv.append(mCity);
            locationTv.append("\n");*//*
            String mState = getResources().getString(R.string.state, state);
            *//*locationTv.append(mState);
            locationTv.append("\n");*//*
            String mCountry = getResources().getString(R.string.country, country);
            *//*locationTv.append(mCountry);
            locationTv.append("\n");*//*
            String mPostalCode = getResources().getString(R.string.postalCode, postalCode);
            *//*locationTv.append(mPostalCode);*//*


        } catch (Exception e) {
            Log.d("Message", "Location exception");
        }

    }*/

    @TargetApi(Build.VERSION_CODES.M)
    public void notifyingWeather(Context context, String temp, String desc) {
        Intent intent = new Intent(context, WeatherActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 2, intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(context)
                /*.setContentTitle("New mail from " + "test@gmail.com")
                .setContentText("Subject")*/
                .setContentTitle(desc)
                .setContentText(temp)
                /*.setDefaults(Notification.DEFAULT_SOUND)*/
                .setSmallIcon(R.drawable.weathericons)
                .setColor(Color.parseColor("#190564"))
                .setContentIntent(pIntent).build();
                /*.addAction(R.drawable.weathericons, "Call", pIntent)
                .addAction(R.drawable.icon, "More", pIntent)
                .addAction(R.drawable.bell3, "And more", pIntent).build();*/
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(2, noti);
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Updating Weather Condition");
        mSpinner.setMessage("Please wait...");
        mSpinner.setIcon(R.drawable.weathericons);
        mSpinner.show();
        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                mSpinner.cancel();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 1000);
    }

    /*@Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }*/

    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //askLocationPermission();

        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);

        if (!wifiManager.isWifiEnabled()) {
            buildAlertMessageNoWLAN();
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
        if (location != null) {
            onLocationChanged(location);
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);

        weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/weathericonsregularwebfont.ttf");

        cityField = (TextView)findViewById(R.id.city_field);
        updatedCityField = (TextView)findViewById(R.id.updated_city_field);
        updatedField = (TextView)findViewById(R.id.updated_field);
        detailsField = (TextView)findViewById(R.id.details_field);
        currentTemperatureField = (TextView)findViewById(R.id.current_temperature_field);
        humidity_field = (TextView)findViewById(R.id.humidity_field);
        pressure_field = (TextView)findViewById(R.id.pressure_field);
        weatherIcon = (TextView)findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);


        WeatherFunctions.placeIdTask asyncTask = new WeatherFunctions.placeIdTask(new WeatherFunctions.AsyncResponse() {
            @Override
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {
                cityField.setText(weather_city);
                updatedCityField.setText(block);
                updatedField.setText(weather_updatedOn);
                detailsField.setText(weather_description);
                currentTemperatureField.setText(weather_temperature);
                humidity_field.setText("Humidity: "+weather_humidity);
                pressure_field.setText("Pressure: "+weather_pressure);
                weatherIcon.setText(Html.fromHtml(weather_iconText));
                notifyingWeather(getApplicationContext(),weather_temperature,weather_description);

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("temperature",weather_temperature);
                intent.putExtra("description",weather_description);
                if (intent.hasExtra(weather_temperature)){
                    startActivity(intent);
                }
            }
        });
        Intent in = getIntent();
        String strLat =  in.getStringExtra("Latitude");
        String strLong =  in.getStringExtra("Longitude");
        Log.i("Lat: ",String.valueOf(strLat));
        Log.i("Long: ",String.valueOf(strLong));

        Log.i("Lati: ",String.valueOf(latitude));
        Log.i("Longi: ",String.valueOf(longitude));
        //asyncTask.execute("25.180000", "89.530000");
        asyncTask.execute(String.valueOf(latitude),String.valueOf(longitude)); //  asyncTask.execute("Latitude", "Longitude")

    }*/
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }
}