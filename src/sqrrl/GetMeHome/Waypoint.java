package sqrrl.GetMeHome;

import android.net.Uri;

public class Waypoint {

		double latitude, longitude;
		String address;
		public Waypoint(){
		}
		public void setGPS(double d, double e)
		{
			latitude=d;
			longitude=e;
		}
		public void setAddress(String add)
		{
			address=add;
		}
		public double getLatitude()
		{
			return latitude;
		}
		public double getLongitude()
		{
			return longitude;
		}
		public String getAddress()
		{
			return address;
		}
		public String getLatLongString()
		{
			return ""+latitude+","+longitude;
		}
		public String getBest()
		{
			if(address==null)
			{
				return getLatLongString();
			}
			else
				return Uri.encode(address);
		}
		
		
		
}