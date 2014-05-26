package com.example.metster;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.sendgrid.SendGrid;

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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		
		//-----------------------------------------------
				Firebase dataRef = new Firebase("https://metster.firebaseIO.com/totalusers/count");
		        dataRef.addValueEventListener(new ValueEventListener() {
		            @Override
		            public void onDataChange(DataSnapshot snapshot) {
		                val =  (String) snapshot.getValue();
		                userid = Integer.parseInt(val);
		                Log.w("count",val);
		            }

		            @Override
					public void onCancelled(FirebaseError arg0) {
						// TODO Auto-generated method stub
						
					}
		        });
				//------------------------------------------------
		
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
		
	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------
	public void radio1_onClick(View v)
	{
	    CompoundButton b1=(CompoundButton)findViewById(R.id.radioButton1);
	    CompoundButton b2=(CompoundButton)findViewById(R.id.radioButton2);
	    CompoundButton b3=(CompoundButton)findViewById(R.id.radioButton3);
	    b1.setChecked(true); /* light the button */
	    b2.setChecked(false); /* unlight the button */
	    b3.setChecked(false); /* unlight the button */
	}
	public void radio2_onClick(View v)
	{
	    CompoundButton b1=(CompoundButton)findViewById(R.id.radioButton1);
	    CompoundButton b2=(CompoundButton)findViewById(R.id.radioButton2);
	    CompoundButton b3=(CompoundButton)findViewById(R.id.radioButton3);
	    b1.setChecked(false); /* light the button */
	    b2.setChecked(true); /* unlight the button */
	    b3.setChecked(false); /* unlight the button */
	}
	public void radio3_onClick(View v)
	{
	    CompoundButton b1=(CompoundButton)findViewById(R.id.radioButton1);
	    CompoundButton b2=(CompoundButton)findViewById(R.id.radioButton2);
	    CompoundButton b3=(CompoundButton)findViewById(R.id.radioButton3);
	    b1.setChecked(false); /* light the button */
	    b2.setChecked(false); /* unlight the button */
	    b3.setChecked(true); /* unlight the button */
	}
	//--------------------------------------------------------------------------
	
	public void pickimage(View view) {
		Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
	}
	//------------------------------ signup -------------------------------------
	public void Signupaccount(View view) {
		
		
		String gender = " ";
		
		final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox1);
		CompoundButton b1=(CompoundButton)findViewById(R.id.radioButton1);
	    CompoundButton b2=(CompoundButton)findViewById(R.id.radioButton2);
	    CompoundButton b3=(CompoundButton)findViewById(R.id.radioButton3);
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
        if(!(b1.isChecked()) && !(b2.isChecked()) && !(b3.isChecked()) ){
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
            String reply = new RequestTask().execute("http://www.naveenmn.com/metstersignup.php", fFirstName, fLastName, fBirthday,fEmail, fPassword, image_str, gender).get();
          
            //---------------------------------------- To fire base
            
         // Setup our Firebase ref
            Firebase baseroot = new Firebase("https://metster.firebaseIO.com/");
    	    // Do something in response to button
            Firebase uroot = baseroot.child("totalusers");
            Firebase count = uroot.child("count");
            //-------------------------------------------
            
            Firebase dataRef = new Firebase("https://metster.firebaseIO.com/totalusers/count");
            dataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    val =  (String) snapshot.getValue();
                    userid = Integer.parseInt(val);
                    Log.w("count",val);
                }

                @Override
    			public void onCancelled(FirebaseError arg0) {
    				// TODO Auto-generated method stub
    				
    			}
            });
            //-------------------------------------------
            
            userid +=1;
            reply = Integer.toString(userid);
            count.setValue(Integer.toString(userid));
            Firebase user = uroot.child("User"+Integer.toString(userid));
            	Firebase fname = user.child("First Name");
            	Firebase lname = user.child("Last Name");
            	Firebase email = user.child("Email");
            	Firebase password = user.child("Password");
            	Firebase usrimage = user.child("Image");
            	Firebase usrgender = user.child("Gender");
            	fname.setValue(fFirstName);
            	lname.setValue(fLastName);
            	email.setValue(fEmail);
            	password.setValue(fPassword);
            	usrimage.setValue(image_str);
            	usrgender.setValue(gender);
            
            //-------------------------------------- write result to file 
            String filename = "myfile.txt";
            String string = reply;
            FileOutputStream outputStream;

            try {
              outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
              outputStream.write(string.getBytes());
              outputStream.close();
            } catch (Exception e) {
              e.printStackTrace();
            }
            }catch( Throwable t ) { //Exception handling of nested exceptions is painfully clumsy in Java
                if( t instanceof ExecutionException ) {
                    t = t.getCause();
                }
            }
            //------------------- read from file
            String ret = "";
            try {
                InputStream inputStream = openFileInput("myfile.txt");

                if ( inputStream != null ) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        stringBuilder.append(receiveString);
                    }

                    inputStream.close();
                    ret = stringBuilder.toString();
                    Log.d("infile",ret);
                }
            }
            catch (FileNotFoundException e) {
                Log.e("login activity", "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e("login activity", "Can not read file: " + e.toString());
            }
           
           //--------------------------------------------------------------
 //------------------------------------------------------------------------ 
            //------------------------------------------------------------------------         
            // check box condition
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
            //sendEmail(fEmail);
    	}//Sign up account ends here
    	

	public void sendEmail(String fEmail){
		//SendGrid Integration
		SendGrid sendgrid = new SendGrid("kaushalp88", "kaushal88");
		sendgrid.addTo(fEmail);
		sendgrid.setFrom("navimn1991@gmail.com");
		sendgrid.setSubject("Welcome to Metster");
		sendgrid.setText("Welcome to Metser. Thank you for signing up with metster.");

		sendgrid.send();
	}
    }
