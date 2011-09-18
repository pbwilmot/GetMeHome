package sqrrl.GetMeHome;

import java.io.*;
import java.util.Date;
import java.util.Stack;
import java.util.prefs.Preferences;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import android.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {
	LinearLayout linearLayout;
	MapView mapView;
	
	double times[];
	double distances[];
	double maxPrice;
	Waypoint home;
	Waypoint currentLocation;
	int timeValue;
	int currentMode = 3;
	
	final int TAXI = 0;
	final int TRAIN = 1;
	final int WALK = 2;
	final int TREE = 3;
	private ProgressDialog progress;
	private String[] modes = new String[] { "Driving", "Transit", "Walking" };
	private RoutesJSONParser routes[]= new RoutesJSONParser[3];
	private String timeStr = "";
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			home = new Waypoint();
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	    	home.address = prefs.getString("homeAddress", null);
	    	timeValue = prefs.getInt("timeValue", 50);
			beginCalcRoute();
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
			stringId = R.string.walking;
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
        String costStr;
        if (mode != 3) {
    	int hours = (int) (times[currentMode] / 3600);
    	int minutes = (int)((times[currentMode] - (hours * 3600)) / 60);
    	int seconds = (int)(times[currentMode] % 60);
    	timeStr="";
    	if( hours > 0)
    		timeStr += hours + "h ";
    	timeStr += minutes + "m " + seconds + "s";
    	costStr = "$" + String.valueOf((int)getCost(mode, distances[mode]));
        }
        else {
        	timeStr = ":(";
        	costStr = "One beer";
        }
    	((TextView)findViewById(R.id.travelTime)).setText(timeStr);
    	((TextView)findViewById(R.id.travelCost)).setText(costStr);
	}
	Handler h = new Handler() {
		public void run(Message msg) {
			compareTimes();
		}
	};

	private void getBingPart1() {
        DirectionsRequester request = new DirectionsRequester(currentLocation,home, new Date(), "Agyyu_epFtQ_RqoTaAFljASv6f16TWTsxF8KBb06X7E5ZADQOCdXi-aj1ZxeSI8u");
        
        
        
        for (int i = 0; i < 3; i++) {
        	routes[i] = new RoutesJSONParser(request.makeRequest(modes[i]));
        }
	}
    String error = null;
	private String getBingPart2(){

        
        for(int x=0; x< routes.length; x++)
        {	
        	if(!routes[x].makeJSON()) {
        		error = "Could not get a " + modes[x] + " route. Check your data connection.";
        	}else if(!routes[x].seedData()) {
        		error = "Bad data from Bing on " + modes[x] + " route! Blame Microsoft!";
        	}
        }
        
        times = new double[3];
        distances = new double[3];
        
        for (int i = 0; i < 3; i++) {
        	times[i] = routes[i].getTravelDuration();
        	distances[i] = routes[i].getTravelDistance();
        }
        return error;
	}
		
	
	private class BingTask extends AsyncTask<Void, Void, Void>
	{
		@Override
    	protected void onPreExecute()
    	{
    	       progress = ProgressDialog.show(MainActivity.this, "Please wait...","Calculating times...");
    	}
		protected Void doInBackground(Void... v) {

			getBingPart1();
			return null;
		}
		
		protected void onPostExecute(Void result) {
			progress.dismiss();
		}
	}
	private void beginCalcRoute() {
		// assume latitude and longitude are also valid
    	

        //get current loc
        Location currLoc = getCurrentLocation();
        currentLocation = new Waypoint();
        currentLocation.setGPS(currLoc.getLatitude(), currLoc.getLongitude());

    	//new BingTask().execute();

        		getBingPart1();
        		getBingPart2();
        		compareTimes();

        //TODO:FIX LATER!

		/*Taxi taxi= new Taxi();
    	setModeDisplay(currentMode);
    	TextView timeView = (TextView)findViewById(R.id.travelTime);
    	if(currentMode==TAXI)
    		((TextView)findViewById(R.id.travelCost)).setText("$"+(int)taxi.calcFare(routes[0].getTravelDistance(), "Philidelphia"));
    	timeView.setText(timeStr);*/
        if (error != null)
        	Toast.makeText(this, error, 5).show();
	}
	private class CallCabClickListener implements DialogInterface.OnClickListener
	{
		public MainActivity activity;
		public CallCabClickListener(MainActivity a)
		{
			activity=a;
		}
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			activity.getCab(activity.home.getAddress());
		}
		
	}
	private class CallFriendClickListener implements DialogInterface.OnClickListener
	{
		public MainActivity activity;
		public CallFriendClickListener(MainActivity a)
		{
			activity=a;
		}
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			activity.call("7135779828");
		}
		
	}
	private AlertDialog alert, alert2, friend;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle bun) {
        super.onCreate(bun);	
        setContentView(R.layout.main);
        friend = new AlertDialog.Builder(this).create();
        friend.setTitle("Call a friend!");
        friend.setMessage("Hit Help! to phone a friend.");
        friend.setButton2("Not now!", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		} );
        friend.setButton("Help!", new CallFriendClickListener(this));
        
        Button go = (Button)findViewById(R.id.go);
        alert = new AlertDialog.Builder(this).create();
        alert.setTitle("Call a cab?");
        alert.setMessage("We will call a cab to your location if you confirm.");
        alert.setButton("Okay", new CallCabClickListener(this) {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				alert2= new AlertDialog.Builder(MainActivity.this).create();
				alert2.setTitle("Confirm");
				alert2.setMessage("Are you sure you want to call a cab?");
				alert2.setButton("Yes", new CallCabClickListener(this.activity));
				alert2.setButton2("No", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				alert2.show();
			}
		});
        alert.setButton2("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
        go.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (currentMode == TAXI) {
					// pop up taxi context menu
					alert.show();
				}
				else if (currentMode == WALK || currentMode == TRAIN) {
					if (currentLocation != null) {
						showMap(currentLocation, home, currentMode);
					}
				}else if(currentMode ==TREE)
				{
					friend.show();
				}
			}
		});
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String homeAddr = prefs.getString("homeAddress", null);
        if (homeAddr == null) {
        	// open the new settings activity
        	Intent i = new Intent(this, SettingsActivity.class);
        	startActivityForResult(i, 0);
        }
        else {
        	home = new Waypoint();
        	home.address = homeAddr;
        	timeValue = prefs.getInt("timeValue", 50);
        	beginCalcRoute();
        }
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.maincontextmenu, menu);
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getItemId() == R.id.settingsButton) {
    	 	Intent i = new Intent(this, SettingsActivity.class);
        	startActivityForResult(i, 0);
        	return true;
    	}
    	return super.onOptionsItemSelected(item);
    }
    //TODO fix this
    
	public void getCab(String addr)
	{
		sendSMS(addr, "7134878351");  //862442
	}
	
	public void cancelCab()
	{
		sendSMS("STOP", "7135779828");  //862442
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
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle("Cab sent!");
        alert.setMessage("You should receive a confirmation soon!");
        alert.setButton("Okay!", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
        alert.show();
        
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
    	double minScore = Double.MAX_VALUE;
    	currentMode = TREE;
    	for (int i = 0; i < 3; i++) {
    		float cost = getCost(i, distances[i]);
    		double score = times[i] * timeValue + cost;
    		if (score < minScore && times[i] != -1) {
    			currentMode = i;
    			minScore = score;
    		}
    	}
    	setModeDisplay(currentMode);
    }
    
    private float getCost(int mode, double distance) {
    	if (mode == WALK) {
    		return 0;
    	}
    	if (mode == TRAIN) {
    		return 2f;
    	}
    	if (mode == TAXI) {
    		return (float) new Taxi().calcFare(distance, "philadelphia");
    	}
    	return 0;
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