package com.example.metster;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class SignUpActivity extends Activity {
	String val;
	int userid;
    String gender = " ";
    String picturePath = null;
    String finalimagepath = null;
    String image_str = null;
    String appkey = "n1a1v2e3e5n8m13y21s34o55r89e";
    String server_response = null;
    String fileimage =  "image.txt";
    public boolean image_from_gallery = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		getActionBar().hide();
		Drawable d = getResources().getDrawable(R.drawable.defaultprofile);
		Bitmap default_image = drawableToBitmap(d);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		default_image.compress(Bitmap.CompressFormat.JPEG, 100, out);
		byte[] byteArray = out.toByteArray();
		image_str =Base64.encodeToString(byteArray, Base64.DEFAULT);
	
	}
	
	public static Bitmap drawableToBitmap (Drawable drawable) {
	    if (drawable instanceof BitmapDrawable) {
	        return ((BitmapDrawable)drawable).getBitmap();
	    }

	    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap); 
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);

	    return bitmap;
	}
	
	/*	public void sendEmail(String fEmail){
	//SendGrid Integration
	SendGrid sendgrid = new SendGrid("kaushalp88", "kaushal88");
	sendgrid.addTo(fEmail);
	sendgrid.setFrom("navimn1991@gmail.com");
	sendgrid.setSubject("Welcome to Metster");
	sendgrid.setText("Welcome to Metser. Thank you for signing up with metster.");

	sendgrid.send();
} */
	
	public boolean checkemail(String email)
	{

	    Pattern pattern = Pattern.compile(".+@.+\\.[a-z]+");
	    Matcher matcher = pattern.matcher(email);
	    return matcher.matches();

	}
	
	public void radio1_onClick(View v)
	{
	    CompoundButton b1=(CompoundButton)findViewById(R.id.radioButton1);
	    CompoundButton b2=(CompoundButton)findViewById(R.id.radioButton2);
	    b1.setChecked(true); /* light the button */
	    b2.setChecked(false); /* unlight the button */
	}
	public void radio2_onClick(View v)
	{
	    CompoundButton b1=(CompoundButton)findViewById(R.id.radioButton1);
	    CompoundButton b2=(CompoundButton)findViewById(R.id.radioButton2);
	    b1.setChecked(false); /* light the button */
	    b2.setChecked(true); /* unlight the button */
	}

	public void pickimage(View view) {
		Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
	}
	//------------------------------ Sign up Profile -------------------------------------
	public void Signupaccount(View view) {
		
		final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox1);
		CompoundButton b1=(CompoundButton)findViewById(R.id.radioButton1);
	    CompoundButton b2=(CompoundButton)findViewById(R.id.radioButton2);
		//--------------------------------Data from fields-----------------------
    	EditText FirstName = (EditText)  findViewById(R.id.FirstName) ;
		EditText LastName = (EditText)  findViewById(R.id.LastName) ;
		EditText Email = (EditText)  findViewById(R.id.Email) ;
		EditText Password = (EditText)  findViewById(R.id.Password) ;
		EditText RetypePassword = (EditText)  findViewById(R.id.RetypePassword) ;
        final String fFirstName = FirstName.getText().toString();
        final String fLastName = LastName.getText().toString();
        final String fBirthday = "NULL";
        final String fEmail = Email.getText().toString();
        final String fPassword = Password.getText().toString();
        final String fRetypePassword = RetypePassword.getText().toString();
        //-----------------------------------------------------------------------
        
        if(TextUtils.isEmpty(fFirstName)) {
            Toast.makeText(this, "Please enter your First Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(fLastName)) {
            Toast.makeText(this, "Please enter your Last Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(fBirthday)) {
            Toast.makeText(this, "Please enter your Birthday", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(fEmail)) {
            Toast.makeText(this, "Please enter your Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(fPassword)) {
            Toast.makeText(this, "Please pick a Password", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(fRetypePassword)) {
            Toast.makeText(this, "Please confirm your Password", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!fPassword.equals(fRetypePassword)) {
            Toast.makeText(this, "Passwords does`nt match", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!(checkBox.isChecked())){
        	Toast.makeText(this, "Please check the terms and conditions inorder to Sign Up", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if( b1.isChecked()){
        	gender = "Male";
        }
        else{
        	if(b2.isChecked()){
        		gender = "Female";
        	}
        	else{
        		gender = "Unknow";
        	}
        }
        if(!(b1.isChecked()) && !(b2.isChecked()) ){
        	Toast.makeText(this, "Please pick up a gender", Toast.LENGTH_SHORT).show();
            return;
        }
    	               
            //---------------------------------- image base64 compression 
             if(image_from_gallery){
              finalimagepath = picturePath;              
              Bitmap bitmapOrg= BitmapFactory.decodeFile(finalimagepath);
              ByteArrayOutputStream stream = new ByteArrayOutputStream();
              //Resize the image
              double width = bitmapOrg.getWidth();
              double height = bitmapOrg.getHeight();
              double ratio = 400/width;
              int newheight = (int)(ratio*height);               
              bitmapOrg = Bitmap.createScaledBitmap(bitmapOrg, 400, newheight, true);
              bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, stream);
              byte[] byte_arr = stream.toByteArray();
              image_str =Base64.encodeToString(byte_arr, Base64.DEFAULT);
             }
              try {
            	  
              if(checkemail(fEmail)){
            	  server_response = new RequestTask().execute("http://54.183.113.236/metster/accountprocess.php", appkey, fFirstName, fLastName,fEmail,fPassword, image_str, gender
              		, "1", "1", "1", "1", "1", "1" ).get();
            	  String response = server_response.toString();
            	  Log.w("serversays",response);
            	  System.out.println(response.contains("no"));
            	  if(response.contains("no")){
            		  Toast.makeText(getApplicationContext(), "This email seems to already in use!! Please try again.", Toast.LENGTH_SHORT).show();
            	  }
            	  else{
              	//-------------------------------------> write usertoken to file
            		  String[] separated = response.split("-");
            		  String filetoken = "token.txt";
            		  String token = separated[1];
            		  String accountnumber = separated[0];
            		  String fileaccount = "accounts.txt";
            		  String string = token;
            		  FileOutputStream outputStream;
                //-------------------------------------- write token to file
                  try {
                    outputStream = openFileOutput(filetoken, Context.MODE_PRIVATE);
                    outputStream.write(string.getBytes());
                    outputStream.close();
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                  
                 catch( Throwable t ) { //Exception handling of nested exceptions is painfully clumsy in Java
                      if( t instanceof ExecutionException ) {
                          t = t.getCause();
                      }
                  }
                //-------------------------------------- write account to file 
                  string = accountnumber;
                  try {
                    outputStream = openFileOutput(fileaccount, Context.MODE_PRIVATE);
                    outputStream.write(string.getBytes());
                    outputStream.close();
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                  
                 catch( Throwable t ) { //Exception handling of nested exceptions is painfully clumsy in Java
                      if( t instanceof ExecutionException ) {
                          t = t.getCause();
                      }
                  }
                //-------------------------------------- write image to file 
                  string = image_str;
                  try {
                    outputStream = openFileOutput(fileimage, Context.MODE_PRIVATE);
                    outputStream.write(string.getBytes());
                    outputStream.close();
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                  
                 catch( Throwable t ) { //Exception handling of nested exceptions is painfully clumsy in Java
                      if( t instanceof ExecutionException ) {
                          t = t.getCause();
                      }
                  }
              	//--------------------------------------------------------------
              	AlertDialog.Builder builder = new AlertDialog.Builder(this);
                  builder.setTitle("Welcome to Metster");
                  builder.setMessage("Your account was sucessfully created.")
                         .setCancelable(false)
                         .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int id) {
                            	Intent intent = new Intent(SignUpActivity.this, HomescreenActivity.class);
                         		startActivity(intent);
                         		finish();
                             }
                         });
                  AlertDialog alert = builder.create();
                  alert.show();
              }
              }
              else{
              	Toast.makeText(getApplicationContext(), "This email seems to invalid!! Please try again.", Toast.LENGTH_SHORT).show();
              }
              
             }
              catch( Throwable t ) { //Exception handling of nested exceptions is painfully clumsy in Java
                  
              }
                 
           //sendEmail(fEmail);
    	}//Signup profile account ends here
	
	//--------------------------------Image to button  and Image path -----------------------
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(resultCode == RESULT_OK){ 
			Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            try {
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();
                
                ImageButton imageButton =(ImageButton)findViewById(R.id.ImageButton01);
    			ParcelFileDescriptor fd;
    	        try {
    	            fd = getContentResolver().openFileDescriptor(data.getData(), "r");
    	            Bitmap bmp = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor());
  	                imageButton.setImageBitmap(bmp);
    	          //-------------------------------------- write result to file 
    	            Log.w("sourcepath:",picturePath);
    	            image_from_gallery = true;
    	            }catch( Throwable t ) { //Exception handling of nested exceptions is painfully clumsy in Java
    	                if( t instanceof ExecutionException ) 
    	                {
    	                    t = t.getCause();
    	                }
    	            }               
              }
              catch(Exception e) {
                Log.e("Path Error", e.toString());
              }
		}       
        
    }
	
	
	
	
}