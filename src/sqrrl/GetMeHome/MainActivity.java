package sqrrl.GetMeHome;

import java.io.*;
import java.util.Date;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	LinearLayout linearLayout;
	MapView mapView;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle bun) {
        super.onCreate(bun);
        setContentView(R.layout.main);
        TextView tv = new TextView(this);
        
        
        Waypoint a = new Waypoint();
        a.setGPS(37.81997, -122.47859);
        Waypoint b= new Waypoint();
        b.setGPS(37.808333, -122.4155556);
        DirectionsRequester request = new DirectionsRequester(a,b, new Date(), "Agyyu_epFtQ_RqoTaAFljASv6f16TWTsxF8KBb06X7E5ZADQOCdXi-aj1ZxeSI8u");
        tv.setText("begin"+request.makeRequest("Driving")+"end");
        
        
       // tv.setText("Hello, Android");
        setContentView(tv);
        
        /*
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
    
    private void savePreferences(Settings set) {
    	FileOutputStream stream = null;
    	try {
			stream = openFileOutput("getmehome.txt", MODE_PRIVATE);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
			writer.write(set.home);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private Settings loadSettings() {
    	FileInputStream stream = null;
		Settings set = new Settings();
    	try {
    		stream = openFileInput("getmehome.txt");
    		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    		set.home = reader.readLine();
    	}
    	catch (IOException ex) {
    		//ex.printStackTrace();
    	}
    	return set;
    }
    
    public class Settings {
    	public String home;
    }
}