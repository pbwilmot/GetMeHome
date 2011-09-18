package sqrrl.GetMeHome;

import java.io.*;
import java.util.Date;
import java.util.prefs.Preferences;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
	LinearLayout linearLayout;
	MapView mapView;
	
	private Location currLoc;
	private Location homeLoc;
	
	double walk,taxi,train;
	double maxPrice;
	String home;
	
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Bundle extras = intent.getExtras();
		if (extras != null && (home = extras.getString("homeAddress")) != null) {
			// successfully got home address
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			SharedPreferences.Editor editor = pref.edit();
			editor.putString("homeAddress", home);
			editor.commit();
		}
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle bun) {
        super.onCreate(bun);
        setContentView(R.layout.main);
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        home = prefs.getString("homeAddress", null);
        if (home == null) {
        	// open the new settings activity
        	Intent i = new Intent(this, SettingsActivity.class);
        	startActivityForResult(i, 0);
        }
/*
        TextView tv = new TextView(this);
        tv.setVerticalScrollBarEnabled(true);

        //get home loc
        //TODO PARSE STUFF FROM FILE maxPrice, homeLoc 
        
        //get current loc
        currLoc = getCurrentLocation();
        Waypoint a = new Waypoint();
        a.setGPS(currLoc.getLatitude(), currLoc.getLongitude());
        
        Waypoint b= new Waypoint();
        b.setGPS(37.808333, -122.4155556);
        DirectionsRequester request = new DirectionsRequester(a,b, new Date(), "Agyyu_epFtQ_RqoTaAFljASv6f16TWTsxF8KBb06X7E5ZADQOCdXi-aj1ZxeSI8u");
        RoutesJSONParser json = new RoutesJSONParser(request.makeRequest("Driving"));
        json.makeJSON();
        if(json.seedData())
        	tv.setText(""+json.getTravelDuration());
        else
        {
        	tv.setText(""+json.getStatusCode());
        }
        //tv.setText(json.prettyJSON());
        	
        
        
        
        
        //TODO get walktime
        //TODO get taxitime
        //TODO get traintime
        

        setContentView(tv);
        
        Settings settings = loadSettings();
        if (settings.home == null) {
        	settings.home = "providence";
        }
        String current = locationToString(getCurrentLocation());
        showMap(current, settings.home);
        */
        
    }
    
    private Location getCurrentLocation() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the locatioin provider -> use
		// default
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
        return location;
    }
    
    private void showMap(String from, String to)
    {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
        		Uri.parse("http://maps.google.com/maps?saddr=" + from + "&daddr=" + to + "&dirflg=w"));
        startActivity(intent);
    }
    
    private String locationToString(Location loc) {
    	return loc.getLatitude() + "," + loc.getLongitude();
    }
    
    public class Settings {
    	public String home;
    }
}