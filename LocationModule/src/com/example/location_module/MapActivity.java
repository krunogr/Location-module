package com.example.location_module;

import java.io.File;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

@SuppressLint("NewApi")
public class MapActivity extends FragmentActivity implements LocationListener,
		OnMarkerDragListener, OnCheckedChangeListener, Communicator,
		OnClickListener {

	GoogleMap googleMap;
	boolean checkProvider = false;
	LocationManager locationManager;
	String provider;
	double latitude, longitude, altitude;
	SharedPreferences sharedPreferences;
	LatLng latlng;
	Marker marker;
	TextView textLatLng;
	ToggleButton toggleButton;
	Button btnMinus, btnPlus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		textLatLng = (TextView) findViewById(R.id.textView1);
		toggleButton = (ToggleButton) findViewById(R.id.togglebutton);
		toggleButton.setOnCheckedChangeListener(this);
		btnMinus = (Button) findViewById(R.id.btnMinus);
		btnPlus = (Button) findViewById(R.id.btnPlus);
		btnMinus.setOnClickListener(this);
		btnPlus.setOnClickListener(this);
		setUpMapIfNeeded();
	}

	private void setUpMapIfNeeded() {

		if (googleMap == null) {
			googleMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map_fragment)).getMap();
		}
		googleMap.getUiSettings().setZoomControlsEnabled(false);
		googleMap.getUiSettings().setMyLocationButtonEnabled(false);
		marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(0,
				0)));
		marker.setVisible(false);
		detectCurrentLocation();
	}

	private void detectCurrentLocation() {
		googleMap.setMyLocationEnabled(true);

		if (checkProvider() != null) {
			providerIsEnabled();
		} else {
			providerIsNotEnabled();
		}
	}

	public void providerIsNotEnabled() {
		Log.d("myLog", "Provider is not enabled");
		showDialog();
		checkProvider = false;
		File file = new File(
				"/data/data/com.example.googlemap/shared_prefs/position_data.xml");

		if (file.exists()) {
			Log.d("myLog", "File exists");
			sharedPreferences = getSharedPreferences("position_data",
					Context.MODE_PRIVATE);
			longitude = Double.valueOf(sharedPreferences.getString("longitude",
					"0"));
			latitude = Double.valueOf(sharedPreferences.getString("latitude",
					"0"));
			latlng = new LatLng(latitude, longitude);
			googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
			googleMap.animateCamera(CameraUpdateFactory.zoomTo(5));
		} else {

			Log.d("myLog", "File doesnt exists");
			latlng = new LatLng(0, 0);
			googleMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
		}
	}

	public void providerIsEnabled() {
		String checkerGPSSignal = null;
		checkProvider = true;
		Location location;

		Log.d("myLog", "Provider is enabled");

		location = locationManager.getLastKnownLocation(provider);

		if (location == null) {
			locationManager.requestLocationUpdates(provider, 10, 1, this);
			location = locationManager.getLastKnownLocation(provider);
		}
		int counterOfChecker = 0;
		while (checkerGPSSignal == null) {
			try {
				counterOfChecker++;
				if (counterOfChecker >= 3) {
					break;
				}
				Log.d("myLog", "Dont have GPS signal");
				location = locationManager.getLastKnownLocation(provider);

				checkerGPSSignal = String.valueOf(location.getLatitude());

			} catch (Exception e) {
				checkerGPSSignal = null;
				e.printStackTrace();
				long start = new Date().getTime();
				while (new Date().getTime() - start < 3000) {
				}
			}
		}
		if (counterOfChecker < 3) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();

			latlng = new LatLng(latitude, longitude);
			googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
			googleMap.animateCamera(CameraUpdateFactory.zoomTo(5));

			marker.setPosition(latlng);
			marker.setTitle("You are here");
			marker.setVisible(true);
			textLatLng.setText(makeTextLatLng(latlng));
			savePositionInXML(latlng);
		} else {
			providerIsNotEnabled();
		}
	}

	private String checkProvider() {
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			provider = locationManager.GPS_PROVIDER;
			Log.d("myLog", "GPS_PROVIDER");

		} else if (locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			provider = locationManager.NETWORK_PROVIDER;
			Log.d("myLog", "NETWORK_PROVIDER");

		} else {
			provider = null;
			Log.d("myLog", "Provider is not enabled");

		}
		return provider;
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@SuppressLint("NewApi")
	public void showDialog() {
		FragmentManager fm = getFragmentManager();
		Dialog dialog = new Dialog();
		dialog.show(fm, "dialog");
	}

	@Override
	public void respond(Point clickedPoint) {
		marker.setVisible(true);
		if (!checkProvider) {
			googleMap.setOnMarkerDragListener(this);
			LatLng latlng1 = googleMap.getProjection().fromScreenLocation(
					clickedPoint);
			marker.setPosition(latlng1);

			marker.setDraggable(true);
			textLatLng.setText(makeTextLatLng(latlng1));
			savePositionInXML(latlng1);
		}
	}

	@Override
	public void onMarkerDrag(Marker marker) {
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		LatLng latlng1 = marker.getPosition();
		googleMap.animateCamera(CameraUpdateFactory.newLatLng(latlng1));
		textLatLng.setText(makeTextLatLng(latlng1));
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
	}

	public String makeTextLatLng(LatLng latLng) {
		String lat, lng;
		lat = String.valueOf(latLng.latitude);
		lng = String.valueOf(latLng.longitude);
		if (lat.length() > 10) {
			lat = lat.substring(0, 10);
		}
		if (lng.length() > 10) {
			lng = lng.substring(0, 10);
		}
		return " Lng: " + lng + ", Lat: " + lat + " ";
	}

	public void savePositionInXML(LatLng latlng1) {
		sharedPreferences = getSharedPreferences("position_data",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("latitude", String.valueOf(latlng1.latitude));
		editor.putString("longitude", String.valueOf(latlng1.longitude));
		editor.commit();
		Log.d("myLog", "Current position is saved");
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			googleMap.setMapType(googleMap.MAP_TYPE_SATELLITE);
		} else {
			googleMap.setMapType(googleMap.MAP_TYPE_NORMAL);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == btnMinus.getId()) {
			googleMap.animateCamera(CameraUpdateFactory.zoomOut());

		} else {
			googleMap.animateCamera(CameraUpdateFactory.zoomIn());

		}

	}

}
