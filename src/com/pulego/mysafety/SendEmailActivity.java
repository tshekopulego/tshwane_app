package com.pulego.mysafety;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SendEmailActivity extends Activity {

	Button buttonSend;
	EditText textTo;
	EditText textSubject;
	EditText textMessage;

	String error;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.report_error);

		buttonSend = (Button) findViewById(R.id.btnSend);
		textTo = (EditText) findViewById(R.id.editTextTo);
		textSubject = (EditText) findViewById(R.id.editTextSubject);
		textMessage = (EditText) findViewById(R.id.editTextMessage);

		Bundle extras = getIntent().getExtras();

		error = extras.getString("error");
		
		textMessage.setText(error);

		buttonSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String to = "support@pulego.co.za";
				String subject = textSubject.getText().toString();
				String message = error;

				Intent email = new Intent(Intent.ACTION_SEND);
				email.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
				// email.putExtra(Intent.EXTRA_CC, new String[]{ to});
				// email.putExtra(Intent.EXTRA_BCC, new String[]{to});
				email.putExtra(Intent.EXTRA_SUBJECT, subject);
				email.putExtra(Intent.EXTRA_TEXT, message);

				// need this to prompts email client only
				email.setType("message/rfc822");

				startActivity(Intent.createChooser(email, "Choose an Email client :"));

			}
		});
	}
}