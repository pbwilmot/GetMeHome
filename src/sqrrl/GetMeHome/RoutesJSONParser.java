package sqrrl.GetMeHome;
import org.json.*;
public class RoutesJSONParser {
	
	private JSONObject json;
	private String sjson;
	private int statuscode,duration;
	private double distance;
	public RoutesJSONParser(String s)
	{
		sjson=s;
		duration=-1;
		distance = -1;
	}
	public boolean makeJSON()
	{
		try {
			json= new JSONObject(sjson);
			return true;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public boolean seedData()
	{
		try {
			statuscode=json.getInt("statusCode");
			if(statuscode!=200)
			{
				return false;
			}
			else
			{
				JSONArray resourcesets= json.getJSONArray("resourceSets");
				JSONObject resourceset= resourcesets.getJSONObject(0);
				JSONArray resources= resourceset.getJSONArray("resources");
				JSONObject route= resources.getJSONObject(0);
				
				duration= route.getInt("travelDuration");
				distance= route.getDouble("travelDistance");
				return true;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return false;
		}

	}
	public int getStatusCode()
	{
		return statuscode;
	}
	public int getTravelDuration()
	{
		return duration; 
	}
	public double getTravelDistance() 
	{
		return distance*.6214; //conversion to miles
	}
	public String prettyJSON()
	{
		try {
			return json.toString(4);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "{}";
		}
	}
}
