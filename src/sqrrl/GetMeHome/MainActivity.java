package sqrrl.GetMeHome;

import java.io.*;
import java.util.Date;
import java.util.Stack;
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
import android.widget.Toast;

public class MainActivity extends Activity {
	LinearLayout linearLayout;
	MapView mapView;
	
	private Location currLoc;
	private Location homeLoc;
	
	double walk,taxi,train;
	double maxPrice;
	Waypoint home;
	
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (intent != null) {
			Bundle extras = intent.getExtras();
			// successfully got home address
			home = new Waypoint();
			home.address = extras.getString("homeAddress");
			//home.latitude = extras.getString("homeLat");
			//home.longitude = extras.getString("homeLon");
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			SharedPreferences.Editor editor = pref.edit();
			editor.putString("homeAddress", home.address);
			//editor.putString("homeLat", home.longitude);
			//editor.putString("homeLon", home.latitude);
			editor.commit();
		}
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle bun) {
        super.onCreate(bun);
        setContentView(R.layout.main);
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String homeAddr = prefs.getString("homeAddress", null);
        if (homeAddr == null) {
        	// open the new settings activity
        	Intent i = new Intent(this, SettingsActivity.class);
        	startActivityForResult(i, 0);
        }
        else {
        	// assume latitude and longitude are also valid
        	home = new Waypoint();
        	home.address = homeAddr;
        	//home.latitude = prefs.getString("homeLat", "0");
        	//home.longitude = prefs.getString("homeLon", "0");
        }

        //get home loc
        //TODO PARSE STUFF FROM FILE maxPrice, homeLoc 
        
        if (home != null) {
	        //get current loc
	        currLoc = getCurrentLocation();
	        Waypoint a = new Waypoint();
	        a.setGPS(currLoc.getLatitude(), currLoc.getLongitude());
	        
	        Waypoint b = home;
	        getBing(a,b);

        }
    }
    
    private void getBing(Waypoint start, Waypoint end) {
        DirectionsRequester request = new DirectionsRequester(start,end, new Date(), "Agyyu_epFtQ_RqoTaAFljASv6f16TWTsxF8KBb06X7E5ZADQOCdXi-aj1ZxeSI8u");
        
        RoutesJSONParser routes[]= new RoutesJSONParser[3];
        routes[0] = new RoutesJSONParser(request.makeRequest("Driving"));
        routes[1] = new RoutesJSONParser(request.makeRequest("Walking"));
        routes[2] = new RoutesJSONParser(request.makeRequest("Transit"));
        
        for(int x=0; x< routes.length; x++)
        {	
        	if(!routes[x].makeJSON()){
        		Toast.makeText(this, "Problem getting data for one or more request. Check your data connection", 5);
        	}else if(!routes[x].seedData()){
        		Toast.makeText(this, "Bad data from Bing! Blame Microsoft!", 5);
        	}
        }
        taxi=routes[0].getTravelDuration();
        walk=routes[1].getTravelDuration();
        train=routes[2].getTravelDuration();
        
        
        //tv.setText(json.prettyJSON());
        	
        
        
        
        
        //TODO get walktime
        
        //TODO get taxitime
        //TODO get traintime        
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
    
    private void showMap(Waypoint from, Waypoint to)
    {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
        		Uri.parse("http://maps.google.com/maps?saddr=" + from.getLatLongString() + "&daddr=" + to.getLatLongString() + "&dirflg=w"));
        startActivity(intent);
    }
    
    private String locationToString(Location loc) {
    	return loc.getLatitude() + "," + loc.getLongitude();
    }
}