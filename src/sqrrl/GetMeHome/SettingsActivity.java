package sqrrl.GetMeHome;

import android.content.*;
import android.os.*;
import android.view.View;
import android.widget.*;

import com.google.android.maps.*;

public class SettingsActivity extends MapActivity {

	EditText address;
	Button doneButton;
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.settings);
		doneButton = (Button)findViewById(R.id.done);
		address = (EditText)findViewById(R.id.addressText);
		doneButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("homeAddress", address.getText().toString());
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
}
