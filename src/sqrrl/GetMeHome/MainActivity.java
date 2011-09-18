package sqrrl.GetMeHome;

import java.io.*;
import java.util.Date;
import java.util.Stack;
import java.util.prefs.Preferences;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import android.R.drawable;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;
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
	
	final int TAXI = 0;
	final int TRAIN = 1;
	final int WALK = 2;
	final int TREE = 3;
	
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
	
	private void setModeDisplay(int mode) {
		int imageId =0;
		int stringId = 0;
		switch (mode) {
		case TAXI:
			imageId = R.drawable.cab;
			stringId = R.string.cab;
			break;
		case WALK:
			imageId = R.drawable.walking;
			stringId = R.drawable.walking;
			break;
		case TRAIN:
			imageId = R.drawable.train;
			stringId = R.string.train;
			break;
		case TREE:
			imageId = R.drawable.tree;
			stringId = R.string.tree;
			break;
		default:
			// WTF
			break;
		}
        
        ImageView image = (ImageView)findViewById(R.id.Photo);
        TextView imageCaption = (TextView)findViewById(R.id.PhotoLabel);
        Resources res = getResources();
        image.setImageDrawable(res.getDrawable(imageId));
        imageCaption.setText(res.getString(stringId));
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
        if (home != null) {
	        //get current loc
	        currLoc = getCurrentLocation();
	        Waypoint a = new Waypoint();
	        a.setGPS(currLoc.getLatitude(), currLoc.getLongitude());
	        
	        Waypoint b = home;
	        ProgressDialog pd = ProgressDialog.show(this, "Thinking", "Calculating route...");
	        getBing(a,b);
	        pd.dismiss();
	        //showMap(a,b, TAXI);
	        compareTimes();
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
        		Toast.makeText(this, "Problem all potential ways home. Check your data connection.", 5).show();
        	}else if(!routes[x].seedData()){
        		Toast.makeText(this, "Bad data from Bing! Blame Microsoft!", 5).show();
        	}
        }
        taxi=routes[0].getTravelDuration();
        walk=routes[1].getTravelDuration();
        train=routes[2].getTravelDuration();
    }
    
	public void getCab(String addr)
	{
		sendSMS(addr, "6177715184");  //862442
	}
	
	public void cancelCab()
	{
		sendSMS("STOP", "6177715184");  //862442
	}
	
	private String getOwnNumber()
	{
		TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		  String phonenum = tMgr.getLine1Number();
		  return phonenum;
	}
	
    //---sends an SMS message to another device---
    private void sendSMS(String phoneNumber, String message)
    {        
        PendingIntent pi = PendingIntent.getActivity(this, 0,
            new Intent(this, MainActivity.class), 0);                
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);        
    } 
    private void call(String pnumber) {
    	   try {
    	       Intent callIntent = new Intent(Intent.ACTION_CALL);
    	       callIntent.setData(Uri.parse("tel:"+pnumber));
    	       startActivity(callIntent);
    	   } catch (ActivityNotFoundException e) {
    	       Log.e("GetMeHome call failed", "Failed Call", e);
    	   }
    }
    private void compareTimes() {
    	double min = Double.MAX_VALUE;
    	int mode = TREE;
    	if (taxi != -1 && taxi < min) {
    		mode = TAXI;
    		min = taxi;
    	}
    	if (walk != -1 && walk < min) {
    		mode = WALK;
    		min = walk;
    	}
    	if (train != -1 && train < min) {
    		mode = TRAIN;
    		min = train;
    	}
    	setModeDisplay(mode);
    	TextView timeView = (TextView)findViewById(R.id.travelTime);
    	int minutes = (int)(min / 60);
    	int seconds = (int)(min % 60);
    	String timeStr = minutes + "m " + seconds + "s";
    	timeView.setText(timeStr);
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
    
    
    private void showMap(Waypoint from, Waypoint to, int mode)
    {
    	String c = null;
    	switch (mode)
    	{
    	case TAXI:
    		c = "d";
    		break;
    	case TRAIN:
    		c = "r";
    		break;
    	case WALK:
    		c = "w";
    		break;
    	default:
    		// sad tree :'(--------
    		break;
    	}
    	Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
        		Uri.parse("http://maps.google.com/maps?saddr=" + from.getBest() + "&daddr=" + to.getBest() + (c != null ? "&dirflg=" + c : null)));
        startActivity(intent);
    }
}