package com.example.metster;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.sendgrid.SendGrid;

public class UpdateProfile extends Activity {

	String val;
	int userid;
	String profileimage;
	public int imgstat = 0;
	String accnumber;
	String imagepthu = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_profile);
		
		//------------------ Set up profile image
		
		ImageButton imageButton =(ImageButton)findViewById(R.id.ImageButton01);
		
		//--------------------------------> Read image from file
		//-------------------------------------
				
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
		                profileimage = stringBuilder.toString();
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
		//-----------------> string to bmp 
		        
		        if (profileimage!=null){
                    byte[] decodedString = Base64.decode(profileimage, Base64.DEFAULT);
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
	                String picturePath = cursor.getString(columnIndex);
	                cursor.close();
	               
	    			ParcelFileDescriptor fd;
	    	        try {
	    	            fd = getContentResolver().openFileDescriptor(data.getData(), "r");
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

	
	public void pickimage(View view) {
		Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
	}
	//------------------------------ signup -------------------------------------
	public void UpdateUserProfile(View view) {
			
		//--------------------------------Data from fields-----------------------
		EditText iamfrom = (EditText)  findViewById(R.id.iamfrom) ;
        EditText age = (EditText)  findViewById(R.id.MyPassion) ;
		EditText iamlivingin = (EditText)  findViewById(R.id.iamlivingin) ;
		EditText myprofession = (EditText)  findViewById(R.id.myprofession) ;
		EditText iworkat = (EditText)  findViewById(R.id.iworkat) ;
		EditText hobbies = (EditText)  findViewById(R.id.hobbies) ;
		EditText music = (EditText)  findViewById(R.id.music) ;
		EditText movies = (EditText)  findViewById(R.id.movies) ;
		EditText books = (EditText)  findViewById(R.id.books) ;
		EditText status = (EditText)  findViewById(R.id.AboutMe) ;
		
     //--------------------------------------------------------------
		//-----------------------------------------------------------
        final String userhometown = iamfrom.getText().toString();
        final String userage = age.getText().toString();
        final String userstayingin = iamlivingin.getText().toString();
        final String userprofession = myprofession.getText().toString();
        final String userworksat = iworkat.getText().toString();
        final String userhobbies = hobbies.getText().toString();
        final String usermusic = music.getText().toString();
        final String usermovies = movies.getText().toString();
        final String userbooks = books.getText().toString();
        final String userstatus = status.getText().toString();
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
                        
                        inputStream.close();
                        imgpthu = stringBuilder.toString();
                        Log.w("path:-",imgpthu);
                        imgstat = 1;
                    }
                }
                
                else{
                	imagepthu = "defaultprofile.png";
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
           //-------------------------- matching phpscript variable
            	String appkey = "n1a1v2e3e5n8m13y21s34o55r89e";
            	String reply = null;
           //---------------------------------------------------------> write image a local file
            	
            	
            	//--------------------------- retrive account number
            	
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
		                accnumber = stringBuilder.toString();
		                Log.w("serveraccnumber",accnumber);
		            }
		        }
		        catch (FileNotFoundException e) {
		        	accnumber = "not";//some garbage
		            Log.e("login activity", "File not found: " + e.toString());
		            Toast.makeText(this, "Please create an account first", Toast.LENGTH_SHORT)
		            .show();
		        } catch (IOException e) {
		        	accnumber = "not";//some garbage
		            Log.e("login activity", "Can not read file: " + e.toString());
		            Toast.makeText(this, "Please create an account first", Toast.LENGTH_SHORT)
		            .show();
		        }
            	
            	//-------------------------------------------------- 
            	String image = image_str ;
            	String fileimage =  "image.txt";
           //--------------------------------------------------------------
            if(true){
            reply = new RequestTask().execute("http://54.183.113.236/metster/profiledataupdate.php", appkey, accnumber, userhometown, userstayingin, userprofession, userworksat, userhobbies,
            		usermusic, usermovies, userbooks, userstatus, image, userage).get();
            String response = reply.toString();
            Log.w("serversays",response);
            String accfail = "no";
            System.out.println(response.contains(accfail));
            if(response.contains("no")){
            	Toast.makeText(getApplicationContext(), "We were unable to update!! Please try again.", Toast.LENGTH_SHORT).show();
            }
            else{
            	FileOutputStream outputStream;
            	
              //-------------------------------------- write image to file 
                String string = image;
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
                builder.setTitle("Metster");
                builder.setMessage("Your Profile was updated.")
                       .setCancelable(false)
                       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                               System.gc();
                               Intent I = new Intent(UpdateProfile.this, Login.class);
                               startActivity(I);
                               finish();
                           }
                       });
                AlertDialog alert = builder.create();
                alert.show();
                
                
                
            }
            }
            
            }catch( Throwable t ) { //Exception handling of nested exceptions is painfully clumsy in Java
                
            }
            
 //------------------------------------------------------------------------ 
            //------------------------------------------------------------------------         
            // check box condition
            
            //sendEmail(fEmail);
    	}//Update Profile account ends here
    	
	
	@Override
	public void onBackPressed() {
	 
		Intent I = new Intent(UpdateProfile.this, Login.class);
        startActivity(I);
        finish();
			
	}
	

	public boolean checkemail(String email)
	{

	    Pattern pattern = Pattern.compile(".+@.+\\.[a-z]+");
	    Matcher matcher = pattern.matcher(email);
	    return matcher.matches();

	}
	
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
