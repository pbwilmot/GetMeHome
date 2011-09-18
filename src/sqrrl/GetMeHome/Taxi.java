package sqrrl.GetMeHome;

import java.util.HashMap;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

public class Taxi {
private HashMap<String, double[]> hm = new HashMap<String, double[]>();
	public Taxi()
	{
		// Create a hash map 
		 
		// Put elements to the map 
		hm.put("honolulu", new double[]{new Double(3.00), new Double(0.25), new Double(0.75)}); 
		hm.put("san diego", new double[]{new Double(2.25), new Double(0.10), new Double(0.25)}); 
		hm.put("miami", new double[]{new Double(2.50), new Double(1/6), new Double(0.40)}); 
		hm.put("san francisco", new double[]{new Double(2.85), new Double(0.20), new Double(0.45)}); 
		hm.put("boston", new double[]{new Double(1.75), new Double(0.125), new Double(0.30)});
		hm.put("los angeles", new double[]{new Double(2.20), new Double(1/11), new Double(0.2)}); 
		hm.put("seattle", new double[]{new Double(2.50), new Double(.1), new Double(0.2)}); 
		hm.put("las vegas", new double[]{new Double(3.20), new Double(1/8), new Double(0.25)}); 
		hm.put("st. louis", new double[]{new Double(2.50), new Double(0.1), new Double(0.20)}); 
		hm.put("philadelphia", new double[]{new Double(2.30), new Double(1/7), new Double(0.3)}); 
		hm.put("atlanta", new double[]{new Double(2.50), new Double(1/8), new Double(0.25)}); 
		hm.put("orlando", new double[]{new Double(2.00), new Double(.25), new Double(0.25)}); 
		hm.put("minneapolis", new double[]{new Double(2.50), new Double(0.2), new Double(0.38)}); 
		hm.put("denver", new double[]{new Double(1.60), new Double(1/8), new Double(0.25)}); 
		hm.put("new york", new double[]{new Double(2.50), new Double(0.2), new Double(0.4)}); 
		hm.put("phoenix", new double[]{new Double(2.50), new Double(1/6), new Double(0.3)}); 
		hm.put("houston", new double[]{new Double(2.50), new Double(1/6), new Double(0.3)}); 
		hm.put("chicago", new double[]{new Double(2.25), new Double(1/9), new Double(0.2)}); 
		hm.put("dallas", new double[]{new Double(2.25), new Double(1/9), new Double(0.2)}); 
		hm.put("new orleans", new double[]{new Double(2.50), new Double(1/8), new Double(0.2)}); 
		hm.put("detroit", new double[]{new Double(2.50), new Double(1/8), new Double(0.2)}); 
		hm.put("baltimore", new double[]{new Double(1.80), new Double(1/8), new Double(0.2)}); 
		hm.put("cleveland", new double[]{new Double(1.80), new Double(1/6), new Double(0.4)}); 

	}
	
	public double calcFare(double distance, String city)
	{
		double fare = 0.0;
		if(hm.containsKey(city.toLowerCase())){
			double[] temp = hm.get(city.toLowerCase());
			fare= temp[0]+((distance/temp[1]) * temp[2]);
		}
		else
		{
			fare= 2.50 + (distance * 5 * .4); //default
		}

		return fare;
	}
	
	public void getCab(String addr)
	{
		sendSMS(addr, "6177715184");  //862442
	
	}
	
	public void cancelCab()
	{
		sendSMS("STOP", "6177715184");  //862442

	}
	
    //---sends an SMS message to another device---
    private void sendSMS(String phoneNumber, String message, Context cntx, Class c)
    {        
        PendingIntent pi = PendingIntent.getActivity(cntx, 0,
            new Intent(cntx, c), 0);                
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);        
    } 
    
    
    
	
}
