/*package sqrrl.GetMeHome;

import java.util.Date;

import android.os.AsyncTask;
import android.widget.Toast;

public class BingAsyncTask extends AsyncTask<BingAsyncResult.BingAsyncParams, Integer, BingAsyncTask.BingAsyncResult> {
	private BingAsyncParams params;
	
	protected BingAsyncResult doInBackground(BingAsyncParams params) {
		// assume 2 points
		this.params = params;
		Waypoint start = params.start;
		Waypoint end = params.end;
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
        onPostExecute(res);
        return res;
	}

	public class BingAsyncParams {
		Waypoint start, end;
	}
	public class BingAsyncResult {
		public double taxi, walk, train;
		public String error;
	}
}*/
