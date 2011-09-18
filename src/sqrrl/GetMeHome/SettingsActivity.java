package sqrrl.GetMeHome;

import java.text.DecimalFormat;
import java.util.prefs.Preferences;

import android.content.*;
import android.content.SharedPreferences.Editor;
import android.os.*;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.*;

import com.google.android.maps.*;

public class SettingsActivity extends MapActivity {

	EditText address;
	Button doneButton;
	SeekBar seek;
	TextView timeValueText;
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.settings);
		doneButton = (Button)findViewById(R.id.done);
		seek = (SeekBar)findViewById(R.id.timeValue);
		address = (EditText)findViewById(R.id.addressText);
		timeValueText = (TextView)findViewById(R.id.timeValueText);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		address.setText(prefs.getString("homeAddress", ""));
		seek.setProgress(prefs.getInt("timeValue", 0));
		
		doneButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				Editor editor = prefs.edit();
				editor.putInt("timeValue", seek.getProgress());
				editor.putString("homeAddress", address.getText().toString());
				editor.commit();
				finish();
			}
		});
		seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				timeValueText.setText("$" + new DecimalFormat("#.##").format((float)progress / 100) + "/min");
			}
		});
	}
}
