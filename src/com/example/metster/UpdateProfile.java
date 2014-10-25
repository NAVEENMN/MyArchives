package com.example.metster;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.metster.Login.User;
import com.example.metster.Profession_info.info;

public class UpdateProfile extends Activity {

	String val;
	int userid;
	String profileimage;
	public int imgstat = 0;
	String accnumber;
	String picturePath;
	String image_str = null;
	String fileimage =  "image.txt";
	String appkey = "n1a1v2e3e5n8m13y21s34o55r89e";
	String server_response = null;
	public Boolean is_image_from_gallery = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_profile);
			
		ImageButton imageButton =(ImageButton)findViewById(R.id.ProfileImage);
		
		Log.w("here","here");
		
		//--------------------------------> Read user ID
        
        try {
            FileInputStream inputStream = openFileInput("accounts.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                info.useraccnumber =  stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
            Toast.makeText(this, "Please create an account first", Toast.LENGTH_SHORT)
            .show();
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
            Toast.makeText(this, "Please create an account first", Toast.LENGTH_SHORT)
            .show();
        }	
		
		
      //--------------------------------> Read image from file
		try {
			FileInputStream inputStream = openFileInput("image.txt");

			if ( inputStream != null ) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();
				
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                	stringBuilder.append(receiveString);
				}
                inputStream.close();
                info.userprofileimage = stringBuilder.toString();
				}
			}
		catch (FileNotFoundException e) {
				Log.e("login activity", "File not found: " + e.toString());
				Toast.makeText(this, "Please create an account first", Toast.LENGTH_SHORT)
				            .show();
				} catch (IOException e) {
				            Log.e("login activity", "Can not read file: " + e.toString());
				            Toast.makeText(this, "Please create an account first", Toast.LENGTH_SHORT)
				            .show();
	    }     
		
					        
		        if (info.userprofileimage!=null){
                    byte[] decodedString = Base64.decode(info.userprofileimage, Base64.DEFAULT);
   		             Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
   		              imageButton.setImageBitmap(decodedByte);
                      }       
		
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
	                ImageButton imageButton =(ImageButton)findViewById(R.id.ImageButton01);
	                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	                picturePath = cursor.getString(columnIndex);
	                cursor.close();
	               
	    			ParcelFileDescriptor fd;
	    	        try {
	    	            fd = getContentResolver().openFileDescriptor(data.getData(), "r");
	    	            try {
	    	              Bitmap bmp = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor());
	    	              imageButton.setImageBitmap(bmp);
	    	              is_image_from_gallery = true;
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
		
	
	public void pickimage(View view) {
		Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
	}
	//------------------------------ update profile -------------------------------------
	public void UpdateUserProfile(View view) {
			
		//--------------------------------Data from fields-----------------------
		EditText iamlivingin = (EditText)  findViewById(R.id.iamlivingin) ;
		EditText myprofession = (EditText)  findViewById(R.id.myprofession) ;
		EditText iworkat = (EditText)  findViewById(R.id.iworkat) ;
		EditText status = (EditText)  findViewById(R.id.AboutMe) ;


        info.userstayingin = iamlivingin.getText().toString();
        info.userprofession = myprofession.getText().toString();
        info.userworksat = iworkat.getText().toString();
        info.userabout = status.getText().toString();
        
        
        if(is_image_from_gallery){
        
            //---------------------------------- image base64 compression      	
              Bitmap bitmapOrg= BitmapFactory.decodeFile(picturePath);
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
              //-------------------------------------- write image to file
              FileOutputStream outputStream;
                try {
                  outputStream = openFileOutput(fileimage, Context.MODE_PRIVATE);
                  outputStream.write(image_str.getBytes());
                  outputStream.close();
                } catch (Exception e) {
                  e.printStackTrace();
                }
                
               catch( Throwable t ) { //Exception handling of nested exceptions is painfully clumsy in Java
                    if( t instanceof ExecutionException ) {
                        t = t.getCause();
                    }
                }
              
        }
            try {
            	
            	server_response = new RequestTask().execute("http://54.183.113.236/metster/profiledataupdate.php", appkey, info.useraccnumber, "1", info.userstayingin, info.userprofession, info.userworksat,info.userfacebook,
            		info.userlinkedin, "1", "1", info.userabout, image_str, "1").get();
            Log.w("serversays",server_response);
            System.out.println(server_response.contains("no"));
            
            if(server_response.contains("no")){
            	Toast.makeText(getApplicationContext(), "We were unable to update!! Please try again.", Toast.LENGTH_SHORT).show();
            }
            else{
            	
            	AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Metster");
                builder.setMessage("Your Profile was updated.")
                       .setCancelable(false)
                       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                               System.gc();
                               Intent I = new Intent(UpdateProfile.this, LoadHome.class);
                               startActivity(I);
                               finish();
                           }
                       });
                AlertDialog alert = builder.create();
                alert.show();     
            }
            
            
            }catch( Throwable t ) { //Exception handling of nested exceptions is painfully clumsy in Java
                
            }
            
            //sendEmail(fEmail);
    	}//Update Profile account ends here
    	

	public boolean checkemail(String email)
	{

	    Pattern pattern = Pattern.compile(".+@.+\\.[a-z]+");
	    Matcher matcher = pattern.matcher(email);
	    return matcher.matches();

	}
	
	public void updatefacebook(View view){
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Connect better with your Facebook public URL ");
		alert.setMessage("https://www.facebook.com/ ");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		input.setHint("naveen.mn");
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  Editable value = input.getText();
		  info.userfacebook = value.toString();
		  // Do something with value!
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
		
    }
	
	public void updatelinkedin(View view){
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Connect better with your Linkedin public URL ");
		alert.setMessage("http://www.linkedin.com/ ");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);
		input.setHint("pub/naveen-mysore/3a/a92/760");

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  Editable value = input.getText();
		  info.userlinkedin = value.toString();
		  setTitle(User.usrstatus);
		  // Do something with value!
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
		
    }
	
	public void sendEmail(String fEmail){
	/*	//SendGrid Integration
		SendGrid sendgrid = new SendGrid("kaushalp88", "kaushal88");
		sendgrid.addTo(fEmail);
		sendgrid.setFrom("navimn1991@gmail.com");
		sendgrid.setSubject("Welcome to Metster");
		sendgrid.setText("Welcome to Metser. Thank you for signing up with metster.");

		sendgrid.send();
		*/
	}
	
	@Override
	public void onBackPressed() {
	 
		Intent I = new Intent(UpdateProfile.this, Login.class);
        startActivity(I);
        finish();
			
	}
	

}
