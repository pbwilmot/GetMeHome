package sqrrl.BingMapsWebRequest;

public class Waypoint {

		int latitude, longitude;
		String address;
		public Waypoint(){
			latitude=0; //defaults
			longitude=0;
			
		}
		public void setGPS(int lat, int lon)
		{
			latitude=lat;
			longitude=lon;
		}
		public void setAddress(String add)
		{
			address=add;
		}
		public int getLatitude()
		{
			return latitude;
		}
		public int getLongitude()
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
				return address;
		}
		
		
		
}
