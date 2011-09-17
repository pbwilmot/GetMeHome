package sqrrl.BingMapsWebRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class DirectionsRequester {
	
	private Waypoint start;
	private Waypoint destination;
	private String apikey;
	private Date date;
	public DirectionsRequester(Waypoint a, Waypoint b, Date d, String api)
	{
		this.setStart(a);
		this.setDestination(b);
		this.apikey= api;
		date=d;
	}
	public Waypoint getStart() {
		return start;
	}
	public void setStart(Waypoint a) {
		this.start = a;
	}
	public Waypoint getDestination() {
		return destination;
	}
	public void setDestination(Waypoint b) {
		this.destination = b;
	}
	
	public String makeRequest(String type)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss");

		String transittime=sdf.format(date);
		String uri= "http://dev.virtualearth.net/REST/V1/Routes/%s?wp.0=%s&wp.1=%s&timeType=Departure&dateTime=%s&output=json&key=%s";
		String.format(uri, type, start.getBest(), destination.getBest(), transittime, apikey);
		HttpClient client= new DefaultHttpClient();
		HttpGet request= new HttpGet();
		try {
			request.setURI(new URI(uri));
			HttpResponse hresponse= client.execute(request);
			if(hresponse.getStatusLine().getStatusCode()!=200)
			{
				System.exit(1);
			}
			BufferedReader in= new BufferedReader(new InputStreamReader( hresponse.getEntity().getContent()));
			StringBuffer sb= new StringBuffer("");
			String line=in.readLine();
			while(line!=null)
			{
				sb.append(in.readLine()+"\n");
			}
			return sb.toString();
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		
	}


			
	

}
