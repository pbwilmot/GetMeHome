package sqrrl.GetMeHome;

import java.text.DecimalFormat;

import android.content.*;
import android.os.*;
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
		doneButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("homeAddress", address.getText().toString());
				setResult(RESULT_OK, intent);
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
