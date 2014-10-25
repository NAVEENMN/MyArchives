package com.example.metster;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.metster.Profession_info.info;

public class Upload_Image extends Activity {


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
	ProgressDialog pd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload__image);
		
		ImageButton imageButton =(ImageButton)findViewById(R.id.ProfileImage);
		
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
	                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	                picturePath = cursor.getString(columnIndex);
	                cursor.close();
	               
	    			ParcelFileDescriptor fd;
	    	        try {
	    	            fd = getContentResolver().openFileDescriptor(data.getData(), "r");
	    	            try {
	    	              ImageButton imageButton =(ImageButton)findViewById(R.id.ProfileImage);
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
		
	public void updateimage(View v){
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
              image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);            
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
                builder.setMessage("Your Image was updated.")
                       .setCancelable(false)
                       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                               System.gc();
                               Intent I = new Intent(Upload_Image.this, Upload_Image.class);
                               startActivity(I);
                               finish();
                           }
                       });
                AlertDialog alert = builder.create();
                alert.show();     
            }
            
            
            }catch( Throwable t ) { //Exception handling of nested exceptions is painfully clumsy in Java
                
            }
	}
	
	
	public void update_image(final View v) {
		v.setEnabled(false);
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			
			@Override
			protected void onPreExecute() {
			    pd = new ProgressDialog(Upload_Image.this);
				pd.setTitle("Processing...");
				pd.setMessage("Please wait.");
				pd.setCancelable(false);
				pd.setIndeterminate(true);
				pd.show();
				Log.w("pre","executed");
			}
				
			@Override
			protected Void doInBackground(Void... arg0) {
				
			
		            	try {
							server_response = new RequestTask().execute("http://54.183.113.236/metster/profiledataupdate.php", appkey, info.useraccnumber, "1", info.userstayingin, info.userprofession, info.userworksat,info.userfacebook,
				            		info.userlinkedin, "1", "1", info.userabout, image_str, "1").get();
							Log.w("back","doing");
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							Log.w("update","failed");
							e.printStackTrace();
						}


		      
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				if (pd!=null) {
					Log.w("post","run");
					pd.dismiss();
					v.setEnabled(true);
				}
			}
				
		};
		task.execute((Void[])null);
	}
	
	@Override
	public void onBackPressed() {
	 
		Intent I = new Intent(Upload_Image.this, Login.class);
        startActivity(I);
        finish();
			
	}
}
