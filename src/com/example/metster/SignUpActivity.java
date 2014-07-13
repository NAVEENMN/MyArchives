package com.example.metster;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import android.graphics.BitmapFactory;
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
	public int imgstat = 0;
	
	public static class userinfo{
		
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		getActionBar().hide();
	}
	
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
	                String picturePath = cursor.getString(columnIndex);
	                cursor.close();
	                
	                ImageButton imageButton =(ImageButton)findViewById(R.id.ImageButton01);
	    			ParcelFileDescriptor fd;
	    	        try {
	    	            fd = getContentResolver().openFileDescriptor(data.getData(), "r");
	    	            
	    	          //----------------------------------------
	    	          //-------------------------------------- write result to file 
	    	            String filename = "path.txt";
	    	            String string = picturePath;
	    	            Log.w("sourcepath:",picturePath);
	    	            FileOutputStream outputStream;

	    	            try {
	    	              outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
	    	              outputStream.write(string.getBytes());
	    	              outputStream.close();
	    	              Bitmap bmp = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor());
	    	              imageButton.setImageBitmap(bmp);
	    	            } catch (Exception e) {
	    	              e.printStackTrace();
	    	            }
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
	//--------------------------------------------------------------------------
	
	public void pickimage(View view) {
		Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
	}
	//------------------------------ Sign up Profile -------------------------------------
	public void Signupaccount(View view) {
		
		
		String gender = " ";
		
		final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox1);
		CompoundButton b1=(CompoundButton)findViewById(R.id.radioButton1);
	    CompoundButton b2=(CompoundButton)findViewById(R.id.radioButton2);
		//--------------------------------Data from fields-----------------------
    	EditText FirstName = (EditText)  findViewById(R.id.FirstName) ;
		EditText LastName = (EditText)  findViewById(R.id.LastName) ;
		EditText Email = (EditText)  findViewById(R.id.Email) ;
		EditText Password = (EditText)  findViewById(R.id.Password) ;
		EditText RetypePassword = (EditText)  findViewById(R.id.RetypePassword) ;
		
     //--------------------------------------------------------------
		//-----------------------------------------------------------
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
        
        //-----------------------------------------------------------------------
    	  //------------------- read from file
            String imgpthu = "";
            try {
                InputStream inputStream = openFileInput("path.txt");

                if ( inputStream != null ) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        stringBuilder.append(receiveString);
                    }

                    inputStream.close();
                    imgpthu = stringBuilder.toString();
                    Log.w("path:-",imgpthu);
                    imgstat = 1;
                }
            }
            catch (FileNotFoundException e) {
                Log.e("login activity", "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e("login activity", "Can not read file: " + e.toString());
            }
           
           //--------------------------------------------------------------
            
            //---------------------------------- image base64 compression      	
              //-----------------------------------------------------------
              String imagepthu = "";
              if(imgstat == 1){imagepthu = imgpthu ;}
              else{imagepthu = "defaultprofile.png";}
              Bitmap bitmapOrg= BitmapFactory.decodeFile(imagepthu);
              ByteArrayOutputStream stream = new ByteArrayOutputStream();
              bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 90, stream);
              byte[] byte_arr = stream.toByteArray();
              String image_str =Base64.encodeToString(byte_arr, Base64.DEFAULT);
              //-----------------------------------------------------------
            
           //--------------------------------------------------------------           
            try {
            	Log.w("here","here");
           //-------------------------- matching phpscript variables
            	String firstname = fFirstName ;
            	String lastname = fLastName ;
            	String image = image_str ;
            	String appkey = "n1a1v2e3e5n8m13y21s34o55r89e";
                //gender is same
            	String femail = fEmail;
            	String fpassword = fPassword;
            	String reply = null;
           //---------------------------------------------------------> write image a local file
            	String fileimage =  "image.txt";
            	
           //--------------------------------------------------------------
            if(checkemail(fEmail)){
            reply = new RequestTask().execute("http://54.183.113.236/metster/accountprocess.php", appkey, firstname, lastname,femail,fpassword, image, gender
            		, firstname, firstname, firstname, firstname, firstname, firstname ).get();
            String response = reply.toString();
            Log.w("serversays",response);
            String accfail = "no";
            System.out.println(response.contains(accfail));
            if(response.contains("no")){
            	Toast.makeText(getApplicationContext(), "This email seems to already in use!! Please try again.", Toast.LENGTH_SHORT).show();
            }
            else{
            	//-------------------------------------> write usertoken to file
            	String[] separated = response.split("-");
            	String filetoken = "token.txt";
                String token = separated[1];
                String accountnumber = separated[0];
              //-------------------------------------- write result to file 
                String fileaccount = "accounts.txt";
                String string = token;
                FileOutputStream outputStream;

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
              //-------------------------------------- write result to file 
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
            	//--------------------------------------------------------------
              //-------------------------------------- write image to file 
                string = image;
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
    	}//Update profile account ends here
    	

	public boolean checkemail(String email)
	{

	    Pattern pattern = Pattern.compile(".+@.+\\.[a-z]+");
	    Matcher matcher = pattern.matcher(email);
	    return matcher.matches();

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
    }
