package sqrrl.GetMeHome;

import java.util.Date;

import android.os.AsyncTask;
import android.widget.Toast;

public class BingAsyncTask extends AsyncTask<Waypoint, Integer, BingAsyncTask.BingAsyncResult> {
	protected BingAsyncResult doInBackground(Waypoint... points) {
		// assume 2 points
		Waypoint start = points[0];
		Waypoint end = points[1];
        DirectionsRequester request = new DirectionsRequester(start,end, new Date(), "Agyyu_epFtQ_RqoTaAFljASv6f16TWTsxF8KBb06X7E5ZADQOCdXi-aj1ZxeSI8u");
        
        RoutesJSONParser routes[]= new RoutesJSONParser[3];
        String[] modes = new String[] { "Driving", "Walking", "Transit" };
        for (int i = 0; i < 3; i++) {
        	routes[i] = new RoutesJSONParser(request.makeRequest(modes[i]));
        }
        BingAsyncResult res = new BingAsyncResult();
        
        for(int x=0; x< routes.length; x++)
        {	
        	if(!routes[x].makeJSON()) {
        		res.error = "Could not get a " + modes[x] + " route. Check your data connection.";
        	}else if(!routes[x].seedData()) {
        		res.error = "Bad data from Bing on " + modes[x] + " route! Blame Microsoft!";
        	}
        }
        res.taxi=routes[0].getTravelDuration();
        res.walk=routes[1].getTravelDuration();
        res.train=routes[2].getTravelDuration();
        return res;
	}
	public class BingAsyncResult {
		public double taxi, walk, train;
		public String error;
	}
}
