package com.example.pebblepush;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private Button abc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		abc = (Button) findViewById( R.id.button1 );
		abc.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Push var = new Push( getApplicationContext() );
				var.sendData( "Test" );
				//System.out.println( "hi" );
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
