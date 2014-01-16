package com.car_tablet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.regex.MatchResult;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
public class MainActivity<YourActivity> extends FragmentActivity 
implements OnSeekBarChangeListener, ActionBar.TabListener, OnCompletionListener{	
	private int scale_factor=1;	
	ViewPager mViewPager;
	SectionsPagerAdapter mSectionsPagerAdapter;	
	private ImageButton exit;
	private ImageButton btnPlay;
	private ImageButton btnMap;
	private ImageButton btnInternet;
	private ImageButton btnForward;
	private ImageButton btnBackward;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnAudioUp;
	private ImageButton btnAudioDown;
	private ImageButton btnAudioMute;
	private SeekBar songProgressBar;
	private TextView songTitleLabel,songCurrentDurationLabel,songTotalDurationLabel,songFoldersize,currentsong,ObdStatus;
	private TextView EngineLoad,Fuel_consumption,Battery,ObdTemp,BaroAltitude,Latitude,Longitude,Cpu_info,GpsfixText,GsmText,Battinfo;
	private Handler mHandler = new Handler();;
	private double gpsalt = 0;
	private long totalDuration = 0;
	private long currentDuration = 0;
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds
	private int currentSongIndex = 0; 		
	private  MediaController ctrl=null;
	private static String newparent="";
	private static String name="";	
	static int spc_count=-1;
	static int z=0;
	static int songid=1;		
	private ListView DeviceListView ; 
	private ListView MediaListView ; 	
	private ListView FolderListView ; 
	private ArrayList<String> folderShortList;
	private ArrayList<Double> avgconsumption;
	private ArrayAdapter<String> listAdapter ; 		
	private static ArrayAdapter<String> mediaAdapter ;	
	private static ArrayAdapter<String> folderAdapter ;	
	private static ArrayAdapter<String> folderNameAdapter ;	
	private static ArrayAdapter<String> folderShortAdapter ;	
	private BluetoothAdapter btAdapter = null;
	private BluetoothSocket btSocket = null;
	private BluetoothDevice btdevice ;	
	private OutputStream outStream = null;
	private InputStream instream = null;
	private final int REQUEST_ENABLE_BT = 1;
	private final int REQUEST_ENABLE_GPS= 2;
	private String obdaddress=null;
	private final UUID MY_UUID =UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");	
	private SensorEventListener mSensorListener;
	private SensorManager mSensorManager;
	private Sensor mSensor;	
	private float[] mValues;
	private LocationManager locManager;
	private LocationListener locListener;
	private Location mobileLocation;		
	private String deviceinfo="No Device";
	private String protocolinfo="No Protocol";		
	private LinearLayout playerfooter=null;
	private LinearLayout playerheader=null;
	private MediaPlayer player=null;	
	private int k;		
	private gauge_rpm rpm;
	private gauge_speed speed;	
	private boolean barometer = false,gps=false,phone=false,camera=false;
	private boolean videofullscreen = false;
	private boolean elmready = false;
	private boolean obdstart = false,paired=false;
	private boolean mute = false,firstsong=true;	
	public static String FILE_DIR =null;	
	public String CURRENT_SONG=null;
	private Utilities utils;
	private VideoView mVideoView;	
	private android.widget.RelativeLayout.LayoutParams layoutParams;
	private AudioManager amanager; 
	private float density,dpHeight,dpWidth;
	public static boolean isService,gpsfix = false; 
	private boolean totalmemok=false;
	private int totalmem=0;
	private Handler batthandler = new Handler();	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	      
    	 super.onCreate(savedInstanceState);    			
		 setContentView(R.layout.activity_main);		
			GetCurrentLocation();				
			Display display = getWindowManager().getDefaultDisplay();
		    DisplayMetrics outMetrics = new DisplayMetrics ();
		    display.getMetrics(outMetrics);
		    
		    density  = getResources().getDisplayMetrics().density;
		    dpHeight = outMetrics.heightPixels / density;
		    dpWidth  = outMetrics.widthPixels / density;
		    
			PackageManager PM= this.getPackageManager();
			gps=PM.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
	        barometer = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER);   
	        phone=PM.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
	        camera=PM.hasSystemFeature(PackageManager.FEATURE_CAMERA);
			// Set up the action bar.
			final ActionBar actionBar = getActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

			// Create the adapter that will return a fragment for each of the three
			// primary sections of the app.
			mSectionsPagerAdapter = new SectionsPagerAdapter(
					getSupportFragmentManager());

			// Set up the ViewPager with the sections adapter.
			mViewPager = (ViewPager) findViewById(R.id.pager);
			mViewPager.setAdapter(mSectionsPagerAdapter);

			// When swiping between different sections, select the corresponding
			// tab. We can also use ActionBar.Tab#select() to do this if we have
			// a reference to the Tab.
			mViewPager
					.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
						@Override
						public void onPageSelected(int position) {
							actionBar.setSelectedNavigationItem(position);
						}
					});

			// For each of the sections in the app, add a tab to the action bar.
			for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {			
				actionBar.addTab(actionBar.newTab()
						.setText(mSectionsPagerAdapter.getPageTitle(i))
						.setTabListener(this));
			}	
			
	}	
	@Override
	public void onPause()
	{
	    super.onPause();	  	    
	}
	@Override
	public void onResume()
	{
		super.onResume();
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.screenBrightness = -1;
		getWindow().setAttributes(params);
	}	  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	@Override
	public void onDestroy() {
	    super.onDestroy();	   
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}	
	@Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	   //Handle the back button
		
	   if (keyCode == KeyEvent.KEYCODE_BACK) {
	     //Ask the user if they want to quit
	     new AlertDialog.Builder(this)
	       .setIcon(android.R.drawable.ic_dialog_alert)
	       .setTitle("Exit")
	       .setMessage("Are you sure you want to leave?")
	       .setNegativeButton(android.R.string.cancel, null)
	       .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	         @Override
	         public void onClick(DialogInterface dialog, int which){
	           // Exit the activity	        	
	        	 try{    		
	     	 		mSensorManager.unregisterListener(mSensorListener);	 
		     	 		if (player != null) {
		     	 			player.stop();
		     	 			player.pause();
		     	 			player.release();
		     	 			player = null;
		     	 	      }
		     	 		resetConnection();
	     		 		if (locManager != null)	 
	     		 		{          
     			 			locManager.removeUpdates(locListener);
     			 			locManager=null;
	     		 	    }
	     		 		MainActivity.this.finish();
	     		 		System.exit(0);
	     		 		}
     		 		 catch (Exception e)  
     		         { 		 	    	
     		         }
	         }
	       })
	       .show();	 
	     if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
	    	 WindowManager.LayoutParams params = getWindow().getAttributes();
		 		params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		 		params.screenBrightness = -1;
		 		getWindow().setAttributes(params);
	         return true;
	     }
	     // Say that we've consumed the event
	     return true;
	   }
	   return super.onKeyDown(keyCode, event);
	 } 
	
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {			
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);			
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase();
			case 1:
				return getString(R.string.title_section2).toUpperCase();					
			}
			return null;
		}
		
	}		
	
	private void GetCurrentBT(){ 		
		 btAdapter = BluetoothAdapter.getDefaultAdapter();
		 if(btAdapter!=null) { 			 
			/* if (!btAdapter.isEnabled()) {	
			 Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
	         startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);	 
			  }else{	*/
		    	     ArrayList<String> deviceList = new ArrayList<String>();   
				     listAdapter = new ArrayAdapter<String>(this, R.layout.devicerow, deviceList);  
				     Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();	 
				     if (pairedDevices.size() > 0) {
				    	 paired=true;
				      for (BluetoothDevice device : pairedDevices) {	    	  
				    	  String str = device.getName() + " | "
			                     + device.getAddress();
				    	  if(str.indexOf("OBD")!=-1 || str.indexOf("ECU")!=-1 || str.indexOf("ELM")!=-1){
				    		 // StartOBD(str);	
				    		  listAdapter.add(str);				    		 
				    	  }		
				      }	 				       
			      } 
				     else{
				    	  listAdapter.add("No paired device!");	
				    	  paired=false;
				     }
				      DeviceListView.setAdapter( listAdapter );  
				      ObdStatus.setText("START OBD Device-->");	
			    /*}*/
		  }else ObdStatus.setText("Bluetooth NOT support...");	
	}
	 private Runnable battrunnable = new Runnable() {
		   @Override
		   public void run() { 			  			   
			   String batstr = null;
		          Long level = LevelReaderFactory.getValue();
		          Long temp = TempReaderFactory.getValue();
		          Long volt = VoltReaderFactory.getValue();
		            	batstr=(String.valueOf(level) + "%  " + 
						String.format("%1.2f",(double) volt/1000000)+"V  "+      			    			
						String.format("%d",temp/10)+"C°");         			    	
		            	Battinfo.setText(batstr); 				    	
		      batthandler.postDelayed(this, 1000);
		   }
		};
		private void Sleep(int time)
		{
			try {
				Thread.sleep((int) (time));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	private void SetCurrentBT(String cbt){ 
	     ArrayList<String> deviceList = new ArrayList<String>();   
	     listAdapter = new ArrayAdapter<String>(this, R.layout.devicerow, deviceList);  
	     listAdapter.clear();
	     listAdapter.add(cbt);	 
	     DeviceListView.setAdapter(null);
	     DeviceListView.setAdapter( listAdapter );  
	}
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  // TODO Auto-generated method stub
	  if(requestCode == REQUEST_ENABLE_BT){
		  GetCurrentBT();
	  }
	 /* if(requestCode == REQUEST_ENABLE_GPS){
		  GetCurrentLocation();
	  }	*/
	 }
	 private double dblLatitude;
	 private double dblLongitude;
	 private int Satellites=0;
	 private long gpstime;	
		private void GetCurrentLocation() { 
			   /*LocationManager alm =(LocationManager)this.getSystemService( Context.LOCATION_SERVICE );
		       if(!alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER ) )
		        {	        	
		        	Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
		        	startActivityForResult(myIntent, REQUEST_ENABLE_GPS);	        	
		        }*/
				 locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);		
				 try{locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
				 try{locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}
				 locListener = new LocationListener() {
					@Override
					public void onStatusChanged(String provider, int status,
							Bundle extras) {
						// TODO Auto-generated method stub
					}
					@Override
					public void onProviderEnabled(String provider) {
						// TODO Auto-generated method stub
					}
					@Override
					public void onProviderDisabled(String provider) {
						// TODO Auto-generated method stub
					}
					@Override
					public void onLocationChanged(Location location) {
						// TODO Auto-generated method stub
						mobileLocation = location;
						if (mobileLocation != null) {									
							if(!barometer)
							{
							BaroAltitude.setText(String.format("%.1f", mobileLocation.getAltitude()) + " m");	
							}
							if(!obdstart)
							{						
								speed.setval((int) ((mobileLocation.getSpeed()*3600)/1000));					
							}
							gpstime=mobileLocation.getTime();
							gpsalt=mobileLocation.getAltitude();
							dblLatitude=mobileLocation.getLatitude();					
							dblLongitude=mobileLocation.getLongitude();					
							Latitude.setText(String.format("%.6f", mobileLocation.getLatitude()));
							Longitude.setText(String.format("%.6f", mobileLocation.getLongitude()));						
						} 
					}
				};
				locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
				locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
				GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
		            @Override
		            public void onGpsStatusChanged(int event) {                
		                if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS || event == GpsStatus.GPS_EVENT_FIRST_FIX) {
		                    GpsStatus status = locManager.getGpsStatus(null);
		                    if(status != null) {
		                    	Iterable<GpsSatellite> sats = status.getSatellites();
		                        // Check number of satellites in list to determine fix state
		                        Satellites = 0;
		                        for (GpsSatellite sat : sats) {
		                            if(sat.usedInFix())
		                            {		                            	
		                            	Satellites++;  
		                            	gpsfix=true;
		                            	Date date = new Date(gpstime);      		   
		                         	    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		                               	String strgpstime = sdf.format(date);  		                               	
		                               	GpsfixText.setText("Time: "+strgpstime+"  Sats: "+String.format("%d", Satellites)+"  GpsFix: "+ gpsfix);		                             
		                            }
		                            else
		                            {
		                            	gpsfix=false;    
		                            	Calendar c = Calendar.getInstance();
			                   			SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
			                   		    String formattedTime = df.format(c.getTime());		
			                   			GpsfixText.setText("Time: "+formattedTime+"  Sats: "+String.format("%d", Satellites)+"  GpsFix: "+ gpsfix );
		                            }
		                                                  
		                        } 
		                       
		                    }                   	
		                }
		            }
		        };
		        locManager.addGpsStatusListener(gpsStatusListener);		       
		}
	public void getCurrentSensors(View obd) {
	BaroAltitude=(TextView) obd.findViewById(R.id.BaroAltitude);
	mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);		
	   mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
	    mSensorListener = new SensorEventListener() {
	        public void onSensorChanged(SensorEvent event) {
	        	Sensor sensor = event.sensor;	            
	            	mValues = event.values;	            	
	            	float altitude = (float) (44330 * (1 - Math.pow((mValues[0]/SensorManager.PRESSURE_STANDARD_ATMOSPHERE), 0.190295)));		   	           
	            	BaroAltitude.setText(String.format("%.1f", gpsalt) + " m / " + String.format("%.1f", altitude) + " m / " + String.format("%.1f", mValues[0]) + " mBar" );	
	            	Read_device_info();	            	
	        }
	        public void onAccuracyChanged(Sensor sensor, int accuracy) {
	        }	       
	    };	
	    if (mSensor != null){
	    	mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_FASTEST);		
	     }
	}
	
	public static String getConnection(Context context){
		 NetworkInfo info = getNetworkInfo(context);		
		 if(info.isConnected())
		 {
		 int type=info.getType();
		   if(type==ConnectivityManager.TYPE_WIFI){
	            return "Wifi";
	        }else if(type==ConnectivityManager.TYPE_MOBILE){	        	
	        	return info.getSubtypeName();         
	        }
		 }
		return "";
	    }
	private void Read_device_info()
	 {	
		Cpu_info.setText(get_memory());			
	 }
	
	  
    private String dataactivity,signalLevelString  = null;
	
	 private final PhoneStateListener phoneStateListener = new PhoneStateListener(){
        
         @Override
         public void onDataActivity(int direction)
         {
        	 String info = "";
        	 info += ("\tReceived: " + TrafficStats.getMobileRxBytes() /1024+" Kb\n");
        	 info += ("\tTransmitted: " + TrafficStats.getMobileTxBytes()/1024+" Kb\n");
	         dataactivity =info;
	         GsmText.setText(getGsmInfo(getBaseContext()).getNetworkOperatorName() + " " + getConnection(getBaseContext())  +"\n"+  " Signal:" + signalLevelString);
                 super.onDataActivity(direction);
         }
         @Override
         public void onSignalStrengthChanged(int asu)
         {
                            
        	int progress = (int) ((((float)asu)/31.0) * 100);
 		    signalLevelString = progress + " %"; 		   
      	    GsmText.setText(getGsmInfo(getBaseContext()).getNetworkOperatorName() + " " + getConnection(getBaseContext())  +"\n"+  " Signal:" + signalLevelString);
 			    super.onSignalStrengthChanged(asu);
         }
	 };   
	
	 private void startSignalLevelListener() {
		 try{
			 if(phone)
			 {
		        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);		      
		        int events = PhoneStateListener.LISTEN_SIGNAL_STRENGTH | PhoneStateListener.LISTEN_DATA_ACTIVITY ;
		        tm.listen(phoneStateListener, events);		       
			 }
		 }catch(Exception e){}
	    }
	 
	public static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }	
	public static TelephonyManager getGsmInfo(Context context){       
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager;
    }	
	private String get_memory()
	{
		if(!totalmemok)	
		{
			try {
				totalmem=getMemoryTotal();
				totalmemok=true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	  ProcessBuilder cmd;
	  String result="";
	  double availableMegs = 0,total=0;
	  ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	  MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
	  activityManager.getMemoryInfo(memoryInfo);
	  availableMegs = memoryInfo.availMem / 1048576L;	
	  total=Runtime.getRuntime().maxMemory()/1048576L;
	  return String.format("Mem Use: %1.0f MB", availableMegs) + " / " + String.format("%d MB", totalmem/1024);
	}
	 private static final String MEMTOTAL_PATTERN = "MemTotal[\\s]*:[\\s]*(\\d+)[\\s]*kB\n";
		public static int getMemoryTotal() throws Exception {
		    final MatchResult matchResult =matchSystemFile("/proc/meminfo", MEMTOTAL_PATTERN, 1000);

		    try {
		      if(matchResult.groupCount() > 0) {
		        return Integer.parseInt(matchResult.group(1));
		      } else {
		        throw new Exception();
		      }
		    } catch (final NumberFormatException e) {
		      throw new Exception(e);
		    }
		  }

		  private static MatchResult matchSystemFile(final String pSystemFile, final String pPattern, final int pHorizon) throws Exception {
		    InputStream in = null;
		    try {
		      final Process process = new ProcessBuilder(new String[] { "/system/bin/cat", pSystemFile }).start();

		      in = process.getInputStream();
		      final Scanner scanner = new Scanner(in);

		      final boolean matchFound = scanner.findWithinHorizon(pPattern, pHorizon) != null;
		      if(matchFound) {
		        return scanner.match();
		      } else {
		        throw new Exception();
		      }
		    } catch (final IOException e) {
		      throw new Exception(e);
		    } 
		      
		  }
	private void settxtscreen(int ort)
	{
		if(ort==1)
		{
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);			
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.internet).getId());
			findViewById(R.id.Cpu_info).setLayoutParams(layoutParams);
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);			
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.Cpu_info).getId());
			findViewById(R.id.BaroAltitude).setLayoutParams(layoutParams);
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);			
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.BaroAltitude).getId());
			findViewById(R.id.EngineLoad_text).setLayoutParams(layoutParams);	
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);			
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.EngineLoad_text).getId());
			findViewById(R.id.EngineLoad).setLayoutParams(layoutParams);	
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.EngineLoad).getId());
			findViewById(R.id.Fuel_consumption_text).setLayoutParams(layoutParams);	
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.Fuel_consumption_text).getId());
			findViewById(R.id.Fuel_consumption).setLayoutParams(layoutParams);	
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.Fuel_consumption).getId());
			findViewById(R.id.ObdTemp_text).setLayoutParams(layoutParams);	
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.ObdTemp_text).getId());
			findViewById(R.id.ObdTemp).setLayoutParams(layoutParams);		
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.ObdTemp).getId());
			findViewById(R.id.Battery_text).setLayoutParams(layoutParams);	
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.Battery_text).getId());
			findViewById(R.id.Battery).setLayoutParams(layoutParams);		
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.Battery).getId());
			findViewById(R.id.Latitude_text).setLayoutParams(layoutParams);		
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.Latitude_text).getId());
			findViewById(R.id.Latitude).setLayoutParams(layoutParams);	
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.Latitude).getId());
			findViewById(R.id.Longitude_text).setLayoutParams(layoutParams);	
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.Longitude_text).getId());
			findViewById(R.id.Longitude).setLayoutParams(layoutParams);
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.Longitude).getId());
			findViewById(R.id.GpsfixText).setLayoutParams(layoutParams);			
			
			
			
		}else if(ort==0)
		{
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.obd_header_bg).getId());
			findViewById(R.id.Cpu_info).setLayoutParams(layoutParams);
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.Cpu_info).getId());
			findViewById(R.id.BaroAltitude).setLayoutParams(layoutParams);
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.BaroAltitude).getId());
			findViewById(R.id.EngineLoad_text).setLayoutParams(layoutParams);	
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.EngineLoad_text).getId());
			findViewById(R.id.EngineLoad).setLayoutParams(layoutParams);	
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.EngineLoad).getId());
			findViewById(R.id.Fuel_consumption_text).setLayoutParams(layoutParams);	
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.Fuel_consumption_text).getId());
			findViewById(R.id.Fuel_consumption).setLayoutParams(layoutParams);	
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.Fuel_consumption).getId());
			findViewById(R.id.ObdTemp_text).setLayoutParams(layoutParams);	
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.ObdTemp_text).getId());
			findViewById(R.id.ObdTemp).setLayoutParams(layoutParams);		
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.ObdTemp).getId());
			findViewById(R.id.Battery_text).setLayoutParams(layoutParams);	
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.Battery_text).getId());
			findViewById(R.id.Battery).setLayoutParams(layoutParams);		
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.Battery).getId());
			findViewById(R.id.Latitude_text).setLayoutParams(layoutParams);		
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.Latitude_text).getId());
			findViewById(R.id.Latitude).setLayoutParams(layoutParams);	
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.Latitude).getId());
			findViewById(R.id.Longitude_text).setLayoutParams(layoutParams);	
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.Longitude_text).getId());
			findViewById(R.id.Longitude).setLayoutParams(layoutParams);
			
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);		
			layoutParams.addRule(RelativeLayout.BELOW,findViewById(R.id.Longitude).getId());
			findViewById(R.id.GpsfixText).setLayoutParams(layoutParams);
			
		}	
	}
	private void setscreen()
	{
		
		int ort=_getScreenOrientation();	
        // Checks the orientation of the screen
	    if (ort == 0) {	    	
	    		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(580/scale_factor, 580/scale_factor);
		    	lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);	
		    	lp.setMargins(0, 80/scale_factor, 0, 0);
		        rpm.setLayoutParams(lp);  
		        lp = new RelativeLayout.LayoutParams(580/scale_factor, 580/scale_factor);
		    	lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);	
		    	lp.setMargins(0, 80/scale_factor, 0, 0);
		        speed.setLayoutParams(lp);
		        settxtscreen(0);
		        layoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);	
				layoutParams.addRule(RelativeLayout.RIGHT_OF,mVideoView.getId());	
				layoutParams.addRule(RelativeLayout.ABOVE,songProgressBar.getId());	
				layoutParams.addRule(RelativeLayout.BELOW,playerheader.getId());	
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				MediaListView.setLayoutParams(layoutParams);							
				layoutParams = new RelativeLayout.LayoutParams(600/scale_factor,LayoutParams.FILL_PARENT);	
				layoutParams.addRule(RelativeLayout.RIGHT_OF,FolderListView.getId());
				layoutParams.addRule(RelativeLayout.ABOVE,songProgressBar.getId());	
				layoutParams.addRule(RelativeLayout.ALIGN_TOP,FolderListView.getId());					 
				mVideoView.setLayoutParams(layoutParams);
				layoutParams = new RelativeLayout.LayoutParams(250/scale_factor, LayoutParams.FILL_PARENT);	
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				layoutParams.addRule(RelativeLayout.BELOW,playerheader.getId());		
				layoutParams.addRule(RelativeLayout.ABOVE,playerfooter.getId());		
				FolderListView.setLayoutParams(layoutParams);			
	    } else if (ort == 1) {	    	
	    	RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(580/scale_factor, 580/scale_factor);
	    		lp.setMargins(0, 600/scale_factor, 0, 0);
	    		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	    		rpm.setLayoutParams(lp);  	    		
	    	    lp = new RelativeLayout.LayoutParams(580/scale_factor, 580/scale_factor);
		    	lp.setMargins(0, 80/scale_factor, 0, 0);
		    	lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		    	speed.setLayoutParams(lp);	
		    	settxtscreen(1);
				layoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 500/scale_factor);	
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);	
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);	
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);	
			    mVideoView.setLayoutParams(layoutParams);
				layoutParams = new RelativeLayout.LayoutParams(250/scale_factor, LayoutParams.FILL_PARENT);	
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				layoutParams.addRule(RelativeLayout.BELOW,mVideoView.getId());	
				layoutParams.addRule(RelativeLayout.ABOVE,songProgressBar.getId());	
				FolderListView.setLayoutParams(layoutParams);
				layoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);	
				layoutParams.addRule(RelativeLayout.BELOW,mVideoView.getId());	
				layoutParams.addRule(RelativeLayout.RIGHT_OF,FolderListView.getId());
				layoutParams.addRule(RelativeLayout.ABOVE,songProgressBar.getId());	
				MediaListView.setLayoutParams(layoutParams);			
	    }
	    else if (ort == 2) {	    	
		    	RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(580/scale_factor, 580/scale_factor);
		    	lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);	
		    	lp.setMargins(0, 80/scale_factor, 0, 0);
		        rpm.setLayoutParams(lp);  
		        lp = new RelativeLayout.LayoutParams(580/scale_factor, 580/scale_factor);
		    	lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);	
		    	lp.setMargins(0, 80/scale_factor, 0, 0);
		        speed.setLayoutParams(lp);  
		        settxtscreen(0);
		        layoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);	
				layoutParams.addRule(RelativeLayout.RIGHT_OF,mVideoView.getId());	
				layoutParams.addRule(RelativeLayout.ABOVE,songProgressBar.getId());	
				layoutParams.addRule(RelativeLayout.BELOW,playerheader.getId());	
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				MediaListView.setLayoutParams(layoutParams);							
				layoutParams = new RelativeLayout.LayoutParams(600/scale_factor,LayoutParams.FILL_PARENT);	
				layoutParams.addRule(RelativeLayout.RIGHT_OF,FolderListView.getId());	
				layoutParams.addRule(RelativeLayout.ABOVE,songProgressBar.getId());	
				layoutParams.addRule(RelativeLayout.ALIGN_TOP,FolderListView.getId());			 
				mVideoView.setLayoutParams(layoutParams);
				layoutParams = new RelativeLayout.LayoutParams(250/scale_factor, LayoutParams.FILL_PARENT);	
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				layoutParams.addRule(RelativeLayout.BELOW,playerheader.getId());		
				layoutParams.addRule(RelativeLayout.ABOVE,playerfooter.getId());		
				FolderListView.setLayoutParams(layoutParams);			
				
	    } else if (ort == 3) {	    	
		    	RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(580/scale_factor, 580/scale_factor);
	    		lp.setMargins(0, 600/scale_factor, 0, 0);
	    		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	    		rpm.setLayoutParams(lp);  	    		
	    	    lp = new RelativeLayout.LayoutParams(580/scale_factor, 580/scale_factor);
		    	lp.setMargins(0, 80/scale_factor, 0, 0);
		    	lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		    	speed.setLayoutParams(lp);  	        		
		    	settxtscreen(1);
				layoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 500/scale_factor);	
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);	
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);	
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);	
			    mVideoView.setLayoutParams(layoutParams);
				layoutParams = new RelativeLayout.LayoutParams(250/scale_factor, LayoutParams.FILL_PARENT);	
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				layoutParams.addRule(RelativeLayout.BELOW,mVideoView.getId());	
				layoutParams.addRule(RelativeLayout.ABOVE,songProgressBar.getId());	
				FolderListView.setLayoutParams(layoutParams);
				layoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);	
				layoutParams.addRule(RelativeLayout.BELOW,mVideoView.getId());	
				layoutParams.addRule(RelativeLayout.RIGHT_OF,FolderListView.getId());
				layoutParams.addRule(RelativeLayout.ABOVE,songProgressBar.getId());	
				MediaListView.setLayoutParams(layoutParams);			
	    }	 
	}
	private int _getScreenOrientation(){
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        return display.getOrientation();
	}
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    setscreen();
	  }	
	
	
	public int listposition=0;	 
	
	public void startOBD_GPS(final View obd_gps){		
		elmready=true;				
		obd_gps.setLayerType(View.LAYER_TYPE_SOFTWARE, null);	
		GpsfixText=(TextView) obd_gps.findViewById(R.id.GpsfixText);		
		exit = (ImageButton) obd_gps.findViewById(R.id.btn_exit);
		ObdStatus=(TextView) obd_gps.findViewById(R.id.ObdStatus);	
		rpm = (gauge_rpm)obd_gps.findViewById(R.id.gauge1);
	    speed = (gauge_speed)obd_gps.findViewById(R.id.gauge2);	 	   
	    EngineLoad = (TextView) obd_gps.findViewById(R.id.EngineLoad);	   
	    Fuel_consumption= (TextView) obd_gps.findViewById(R.id.Fuel_consumption);	  
	    Cpu_info= (TextView) obd_gps.findViewById(R.id.Cpu_info);	  
	    ObdTemp= (TextView) obd_gps.findViewById(R.id.ObdTemp);	  	
	    Battery= (TextView) obd_gps.findViewById(R.id.Battery);	  
	    BaroAltitude=(TextView) obd_gps.findViewById(R.id.BaroAltitude);
	    Latitude = (TextView) obd_gps.findViewById(R.id.Latitude);	   
	    Longitude = (TextView) obd_gps.findViewById(R.id.Longitude);	   
	    DeviceListView= (ListView) obd_gps.findViewById(R.id.devicelist); 	   
        DeviceListView.setClickable(true);     
        DeviceListView.bringToFront();          
        btnMap = (ImageButton) obd_gps.findViewById(R.id.openmap);  
        btnInternet = (ImageButton) obd_gps.findViewById(R.id.internet);  
        avgconsumption = new ArrayList<Double>();  
        Battinfo = (TextView) obd_gps.findViewById(R.id.battery_info);	        
        batthandler.postDelayed(battrunnable,1000);	
   
        if(barometer){
			getCurrentSensors(obd_gps); 
			  }	           
		GetCurrentBT();				
					  			
		exit.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {					 		
						AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
						alert.setTitle("EXIT OR SCREEN OFF");
						alert.setPositiveButton("Close App", new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog, int whichButton) {
						    	try{    		
					     	 		mSensorManager.unregisterListener(mSensorListener);	 
						     	 		if (player != null) {
						     	 			player.stop();
						     	 			player.pause();
						     	 			player.release();
						     	 			player = null;
						     	 	      }
						     	 		resetConnection();
					     		 		if (locManager != null)	 
					     		 		{          
				     			 			locManager.removeUpdates(locListener);
				     			 			locManager=null;
					     		 	    }
					     		 		MainActivity.this.finish();
					     		 		System.exit(0);
					     		 		}
				     		 		 catch (Exception e)  
				     		         { 		 	    	
				     		         }
						    }
						});
						alert.setNegativeButton("Screen Off", new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog, int whichButton) {
						    	 WindowManager.LayoutParams layoutParam = getWindow().getAttributes();
								 layoutParam.screenBrightness = 0; 
								 layoutParam.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
								 getWindow().setAttributes(layoutParam);
						    }
						});
						alert.show();						   							   
					}
				});		
		Cpu_info.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {										
				System.gc();
				Toast.makeText(getApplicationContext(), "Memory cleared...",Toast.LENGTH_SHORT).show();
			}
		});				
		btnMap.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {										
				Intent i = new Intent(getApplicationContext(),PlacesMapActivity.class);					
				i.putExtra("user_latitude", Double.toString(dblLatitude));
				i.putExtra("user_longitude",Double.toString(dblLongitude));					
				startActivity(i);				
			}
		});		
		btnInternet.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(),WebActivity.class);					
				startActivity(i);						
			}
		});	
		
       DeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {	       
	         public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {	
	        	 if(paired){
	        	    resetConnection();
	        		if(btAdapter.isDiscovering())btAdapter.cancelDiscovery();        	           
	                Object o = DeviceListView.getItemAtPosition(position);
	                String[] values = o.toString().split("\\|");
	                obdaddress = values[1];	                
	                ObdStatus.setText(obdaddress.trim());	    
	                btdevice = btAdapter.getRemoteDevice(obdaddress.trim());           
	    	   	     try {
	    	   			btSocket = btdevice.createRfcommSocketToServiceRecord(MY_UUID);
	    	   	     	 } catch (IOException e1) {} 
	    	   			 try {	
	    	   				 while(!btSocket.isConnected())
	    	   				 {
	    	   					 btSocket.connect();
	    	   				 }
	    	   				 if(btSocket.isConnected()){	
	    	   					avgconsumption.clear();	    	   					
	    	   					String read;
	    	   					SendData("ATZ\r");//reset
	    	   					read=readto();
	    	   					SendData("ATS0\r");//space characters off   
	    	   					read=readto();
	    	   				    SendData("ATE0\r"); //Echo off 
	    	   				    read=readto();		   	
	    	   				    SendData("ATL0\r"); //Linefeeds OFF 	
	    	   				    read=readto();		   			
	    	   			    	SendData("ATAT0\r");//set adaptive timing by atst enabled.    
	    	   			    	read=readto();	
	    	   			    	SendData("ATST10\r");//(adaptive timing x4 MS TIMEOUT)     	
	    	   			    	read=readto();
	    	   				    SendData("ATI\r");//get device name
	    	   				    deviceinfo=readto();
	    	   				    SendData("ATDP\r");//get protocol  
	    	   				    protocolinfo=readto();					    	   				   
		    	   				ObdStatus.setText("DEVICE : " + deviceinfo + "     PROTOCOL : " + protocolinfo);		
		    	   			    SendData("ATSPA0\r"); //set protocol auto  	
		 		   	            read=readto();		   	 
			    	   			 Handler handler = new Handler();
				   		   		 BluetoothSocketListener bsl =new BluetoothSocketListener(btSocket, handler);
				   		   		    Thread messageListener = new Thread(bsl);
				   		   		    messageListener.start();    		   		    
				   		   	    	SetCurrentBT(o.toString());				   		   	    	
				   		   	    	elmready=true;
				   		   	    	k=0;	
				   		   	    	obdstart=true;				   		   	       
				   		   	    	SendData("ATRV\r");
	    	   				 }	
	    	   				 else 
	    	   					 {		    	   					   	   		   		    
	    	   					ObdStatus.setText("ELM 327 Not Found !");
	    	   					obdstart=false;	    	   				    
	    	   					 }
	    	   			} catch (IOException e) {	    	   				   	   		   		   
	    	   				ObdStatus.setText("ELM 327 Not Found !");	
	    	   				obdstart=false;	    	   				
	    	   		}
	  		 }}
       		});		
      
			
	}
	private void  launchComponent(String packageName, String name){
	    Intent launch_intent = new Intent("android.intent.action.MAIN");
	    launch_intent.addCategory("android.intent.category.LAUNCHER");
	    launch_intent.setComponent(new ComponentName(packageName, name));
	    launch_intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);

	    startActivity(launch_intent);
	}
	public void startApplication(String application_name){
	    try{
	        Intent intent = new Intent("android.intent.action.MAIN");
	        intent.addCategory("android.intent.category.LAUNCHER");

	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	        List<ResolveInfo> resolveinfo_list = getPackageManager().queryIntentActivities(intent, 0);

	        for(ResolveInfo info:resolveinfo_list){
	            if(info.activityInfo.packageName.equalsIgnoreCase(application_name)){
	                launchComponent(info.activityInfo.packageName, info.activityInfo.name);
	                break;
	            }
	        }
	    }
	    catch (ActivityNotFoundException e) {
	        Toast.makeText(getApplicationContext(), "There was a problem loading the application: "+application_name,Toast.LENGTH_SHORT).show();
	    }
	}
	public void resetConnection() {
        if (instream != null) {
                try {instream.close();} catch (Exception e) {}
                instream = null;
        }

        if (outStream != null) {
                try {outStream.close();} catch (Exception e) {}
                outStream = null;
        }

        if (btSocket != null) {
                try {
                	while(btSocket==null)
                	{
                	btSocket.close();
                	btSocket=null;
                	}
                } catch (Exception e) {}              
        }

	}
	 private int rpmval=0,currenttemp=0;
	 public class MessagePoster implements Runnable {		   
		    private String message;

		    public MessagePoster(String message) {		     
		      this.message = message;
		    }         
		    public void run() {	
		    	String strmessage = message.replace(" ", "");		    	
                if (strmessage.indexOf("NODATA")!=-1)
                {
                	ObdStatus.setText("Engine Stopped...START OBD Device-->");	                	
                	EngineLoad.setText("0 %");
            	    Fuel_consumption.setText("0 %");
            	    ObdTemp.setText("0 C°");            	       
                }
                else{
                	
                int obdval=0;
	            String checkResponse = "";
		         if (message.length() > 4)
		            {
		                checkResponse = message.substring(0, 4);
		                if (message.substring(0, 2).equals("41"))
		                	try {		
		                	obdval = Integer.parseInt(message.substring(4, message.length()),16);
		                        } catch (NumberFormatException nFE) {}
		            }		    			    	
		         	if(checkResponse.equals("410C"))
		             {
		                 int val = (int)(obdval / 4);
		                 rpmval=val;
		                 rpm.setval((int)(val / 100));	 
		             }
		             else if (checkResponse.equals("410D"))
		             	 speed.setval(obdval);	                    
		             else if (checkResponse.equals("4105")){
		            	 int tempC = obdval - 40;
		            	 currenttemp=tempC;
		            	 ObdTemp.setText(Integer.toString(tempC) + " C°");	
		         		 }
		             else if (checkResponse.equals("4104"))
		             {
		            	 String avg;		            	
		            	 int calcLoad = obdval * 100 / 255; 
		            	 EngineLoad.setText(Integer.toString(calcLoad) + " %");  
		            	 avg=String.format("%10.1f", (0.001*0.004*4*rpmval*60*calcLoad/20)).trim();
		            	 avgconsumption.add( (0.001*0.004*4*rpmval*60*calcLoad/20));			            	
		            	 Fuel_consumption.setText(avg  + " / " + String.format("%10.1f",calculateAverage(avgconsumption)).trim() + " L/h"); 		                     	 
		             }			             	                      
		             else if (message.length() <= 5 && message.indexOf("V") != -1)//battery voltage
	                 {		            	
		            	 Battery.setText(message);          	
	                 }
                }
		    
		    if(elmready)
		      {
		    	  switch (k) {
		            case 0:
		            	 SendData("010C\r");//Engine RPM
		            	 k=1;
		                break;		
		            case 1:
		            	 SendData("0104\r");//Engine Load
		            	 k=2;
		                break;	
		            case 2:
		            	 SendData("0105\r");//Engine Temperature
		            	 k=3;
		                break;			           
		            case 3:
		            	 SendData("010D\r");//SPEED
		            	 k=4;
		                break;		           
		            case 4:
		            	 SendData("ATRV\r");//Battery voltage
		            	 k=0;
		                break;		
		            default:	    
		            	 elmready=true;
		            	 k=0;
		                break;
		                 }  
		      }
		    }     
		  }
	 private double calculateAverage(ArrayList<Double> listavg) {
		  Double sum = 0.0;
		  for (Double val : listavg) {
		      sum += val;
		  }
		  return sum.doubleValue() / listavg.size();
		}
	public class BluetoothSocketListener implements Runnable {

		  private BluetoothSocket socket;		
		  private Handler handler;

		  public BluetoothSocketListener(BluetoothSocket socket,Handler handler) {
		    this.socket = socket;		   
		    this.handler = handler;
		  }
		  public void run() {	
		  elmready=false;
		  while(true){
	            try {
	            	 InputStream instream = socket.getInputStream();	            
			            char r;
			            String msg = "";
			            while (true) {
			                    r = (char) instream.read();
			                    msg += r;
			                if (r == 0x3e) {
			                	 msg = msg.substring(0,msg.length()-2);
			                break;
			                    }
	                }
			    msg = msg.replaceAll(" ", "");
	            handler.post(new MessagePoster(msg.trim()));	
	            elmready=true;
	            } catch (IOException e) {	  			  
	  		  }   
		  } 
		  }
		}
	private String readto(){ 		
		  while(true){
	            try {
	            	 InputStream instream = btSocket.getInputStream();	            
			            char r;
			            String msg = "";
			            while (true) {
			                    r = (char) instream.read();
			                    msg += r;
			                if (r == 0x3e) {
			                	 msg = msg.substring(0,msg.length()-2);
			                break;
			                    }
	                }
			    msg = msg.replaceAll(" ", "");
	            return msg.trim();
	           
	            } catch (IOException e) {	  			  
	  		  }  
		  }
	} 	
	private void SendData(String message){	
		if(elmready==true)
		{
		    try {
		      outStream = btSocket.getOutputStream();
		    } catch (IOException e) {		    	
		    }
		    byte[] msgBuffer = message.getBytes();
		    try {
		      outStream.write(msgBuffer);
		    } catch (IOException e) {			    	
		    }
		}
	}
		
	public void startMEDYA(final View medya){
		   
		btnPlay = (ImageButton) medya.findViewById(R.id.btnPlay);		
		btnForward = (ImageButton) medya.findViewById(R.id.btnForward);
		btnBackward = (ImageButton) medya.findViewById(R.id.btnBackward);
		btnNext = (ImageButton) medya.findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) medya.findViewById(R.id.btnPrevious);
		btnAudioUp = (ImageButton) medya.findViewById(R.id.btnAudioUp);	
		btnAudioDown = (ImageButton) medya.findViewById(R.id.btnAudioDown);	
		btnAudioMute= (ImageButton) medya.findViewById(R.id.btnAudioMute);	
		songProgressBar = (SeekBar) medya.findViewById(R.id.songProgressBar);
		songTitleLabel = (TextView) medya.findViewById(R.id.songTitle);
		songCurrentDurationLabel = (TextView) medya.findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) medya.findViewById(R.id.songTotalDurationLabel);
		songFoldersize= (TextView) medya.findViewById(R.id.songFoldersize);
		currentsong= (TextView) medya.findViewById(R.id.currentsong);
		playerfooter=(LinearLayout)medya.findViewById(R.id.player_footer_bg);
		playerheader=(LinearLayout)medya.findViewById(R.id.player_header_bg);
		MediaListView= (ListView) medya.findViewById(R.id.medialist);  
		MediaListView.setClickable(true); 
		FolderListView= (ListView) medya.findViewById(R.id.folderlist);  
		FolderListView.setClickable(true); 
		songProgressBar.setOnSeekBarChangeListener(this);		
		mVideoView = (VideoView) medya.findViewById(R.id.surface_view);		
		mVideoView.bringToFront();			
		songProgressBar.setMax(100);	
		songTitleLabel.setGravity(Gravity.CENTER_VERTICAL);
		setscreen();					
		// songTitleLabel.setText ("dpHeight:"+ Float.toString(dpHeight) +"  dpWidth:"+  Float.toString(dpWidth));  
		 utils = new Utilities();		
         amanager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);   
         final int flagsNoUI = AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE |
                 AudioManager.FLAG_VIBRATE;
         amanager.setStreamVolume(AudioManager.STREAM_SYSTEM, 100, flagsNoUI);
         currentVolume = amanager.getStreamVolume(AudioManager.STREAM_MUSIC);
         
		 ArrayList<String> mediaList = new ArrayList<String>();   
		 mediaAdapter = new ArrayAdapter<String>(this, R.layout.mediarow, mediaList);  
		 
		 ArrayList<String> folderList = new ArrayList<String>();   
	     folderAdapter = new ArrayAdapter<String>(this, R.layout.folderrow, folderList); 	
	   
	     ArrayList<String> folderNameList = new ArrayList<String>();   
	     folderNameAdapter = new ArrayAdapter<String>(this, R.layout.folderrow, folderNameList); 	
	     
	     folderShortList = new ArrayList<String>(); 
	     folderShortAdapter= new ArrayAdapter<String>(this, R.layout.folderrow, folderShortList); 
	     
	     mVideoView.setBackgroundDrawable( new BitmapDrawable(BitmapFactory.decodeResource(this.getResources(), R.drawable.musiclogo)));	
	     
	     try  
         {	 player = new MediaPlayer();	   			
	   		 player.reset();	
	   		 player.setOnCompletionListener(MainActivity.this); // Important		    	
	   		 FILE_DIR="/sdcard"		  ; 
			 File aFile = new File(FILE_DIR);
			 ListMediaFolders(aFile);		
			 if(mediaAdapter.getCount()>0)
			 {
			 MediaListView.setAdapter(mediaAdapter );     
			 FolderListView.setAdapter(folderShortAdapter); 			 
			    ctrl = new MediaController(this);     
		   		ctrl.setMediaPlayer(mVideoView);    		   		
		   		mVideoView.setMediaController(ctrl);	
	            MediaListView.setSelection(0);	        
        	    Object o = MediaListView.getItemAtPosition(0);
                String[] values = o.toString().split("\\:");
                CURRENT_SONG=values[1].trim();	 
    	        FILE_DIR=folderNameAdapter.getItem(0).toString();
    	        songTitleLabel.setText ("Song "+Integer.toString(currentSongIndex+1) + ": "+ CURRENT_SONG);  
    	        songFoldersize.setText("Total: " + Integer.toString(mediaAdapter.getCount())); 
			 }else
				 songTitleLabel.setText("Could not found any media!"); 
    	          	       
         }
	    catch (Exception e)  
        {  	    	  
	    	songTitleLabel.setText(e.toString());  
        }
	    
	     mVideoView.setOnTouchListener(new VideoView.OnTouchListener() {
	       
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(player.isPlaying()){						
					if(!videofullscreen)
					{
						int ort=_getScreenOrientation();				        
						layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
						  if (ort == 0 || ort == 2) {	 
						  layoutParams.addRule(RelativeLayout.ALIGN_TOP,MediaListView.getId());	
						  }
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);							
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);	
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);				
						mVideoView.setLayoutParams(layoutParams);						
						mVideoView.bringToFront();	
						videofullscreen=true;						
					}else
					{	
						setscreen();							
						videofullscreen=false; 	
					}
				}
				
		    return false;
			}
	     });	    
	     btnPlay.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// check for already playing
					if(player.isPlaying()){
						if(player!=null){
							player.pause();
							// Changing button image to play button
							btnPlay.setImageResource(R.drawable.btn_play);
						}
					}else{
						// Resume song
						if(player!=null && CURRENT_SONG!=null && FILE_DIR!=null)
				        {		
							if(firstsong)
							{
							    Object o = MediaListView.getItemAtPosition(currentSongIndex);
				                String[] values = o.toString().split("\\:");
				                CURRENT_SONG=values[1].trim();				               
				    	        try {	    	        	
				    	        	FILE_DIR=folderNameAdapter.getItem(currentSongIndex).toString();	
				    	        	if(CURRENT_SONG.indexOf(".mp3")!=-1)
				    	        	createVideoThumbnail(true);
				    	        	else
				    	        	createVideoThumbnail(false);	
				    	        	PlayVideo(FILE_DIR + "/" + CURRENT_SONG);  
				    	        	MediaListView.setSelection(currentSongIndex);   
									firstsong=false;
									btnPlay.setImageResource(R.drawable.btn_pause);	
								} catch (IllegalArgumentException e) {									
									 songTitleLabel.setText(e.toString());							
								} catch (SecurityException e) {
									 songTitleLabel.setText(e.toString());	
								} catch (IllegalStateException e) {
									 songTitleLabel.setText(e.toString());	
								}
							}else
							{
								player.start();						
								btnPlay.setImageResource(R.drawable.btn_pause);	
							}
						}
					}
					
				}
			});
		btnAudioUp.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {				
				 amanager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
	                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);		
				 currentVolume = amanager.getStreamVolume(AudioManager.STREAM_MUSIC);
			}
		});
		btnAudioDown.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				 amanager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);	
				 currentVolume = amanager.getStreamVolume(AudioManager.STREAM_MUSIC);
			}
		});
		btnAudioMute.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {				
				if(!mute){
					amanager.setStreamVolume(AudioManager.STREAM_MUSIC,0, flagsNoUI);	
					mute=true;
					btnAudioMute.setImageResource(R.drawable.btn_audio_on);
				}else
				{					
					amanager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume,flagsNoUI);	
					mute=false;
					btnAudioMute.setImageResource(R.drawable.btn_audio_mute);
				}
			}
		});
		btnNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {				
				if((currentSongIndex < MediaListView.getCount()-1))	
				{
				currentSongIndex=currentSongIndex+1;	
				getsongfromlist(currentSongIndex); 
				songTitleLabel.setText ("Song "+Integer.toString(currentSongIndex + 1) +": "+ CURRENT_SONG);  
				MediaListView.setSelection(currentSongIndex);	
    	        try {   
    	        	if(CURRENT_SONG.indexOf(".mp3")!=-1)
	    	        	createVideoThumbnail(true);
	    	        	else
	    	        	createVideoThumbnail(false);	
    	        	PlayVideo(FILE_DIR + "/" + CURRENT_SONG);      	        	
					btnPlay.setImageResource(R.drawable.btn_pause);						
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}}
		});
		
		btnPrevious.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {				
				if(currentSongIndex>0)
				{					  
				currentSongIndex=currentSongIndex-1;	
				getsongfromlist(currentSongIndex); 
				songTitleLabel.setText ("Song "+Integer.toString(currentSongIndex + 1) +": "+ CURRENT_SONG);  
				MediaListView.setSelection(currentSongIndex);	
				try {	
					if(CURRENT_SONG.indexOf(".mp3")!=-1)
	    	        	createVideoThumbnail(true);
	    	        	else
	    	        	createVideoThumbnail(false);	
    	        	PlayVideo(FILE_DIR + "/" + CURRENT_SONG);      
					btnPlay.setImageResource(R.drawable.btn_pause);						
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}}
			}
		});
		btnForward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			if(currentSongIndex!=0)
				{
				// get current song position				
				int currentPosition = player.getCurrentPosition();
				// check if seekForward time is lesser than song duration
				if(currentPosition + seekForwardTime <= player.getDuration()-1000){
					// forward song
					try{
						player.seekTo(currentPosition + seekForwardTime);
					}catch (Exception e) {}      
				}else{
					// forward to end position
					try{
						player.seekTo(player.getDuration()-1000);
					}catch (Exception e) {}   
				}
				}
			}
		});		
	
		btnBackward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(currentSongIndex!=0)
				{
				// get current song position				
				int currentPosition = player.getCurrentPosition();
				// check if seekBackward time is greater than 0 sec
				if(currentPosition - seekBackwardTime >= 0){
					// forward song
					player.seekTo(currentPosition - seekBackwardTime);
				}else{
					// backward to starting position
					player.seekTo(0);
				}
				
				}
			}
		});
				
		MediaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {	       
	         public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
	        try{
	        	    Object o = MediaListView.getItemAtPosition(position);
	                String[] values = o.toString().split("\\:");
	                CURRENT_SONG=values[1].trim();	               
	                currentSongIndex=position;	                    			
	                songTitleLabel.setText ("Song "+Integer.toString(currentSongIndex + 1) +": "+ CURRENT_SONG);  
	    	        try {	    	        	
	    	        	FILE_DIR=folderNameAdapter.getItem(position).toString();	
	    	        	if(CURRENT_SONG.indexOf(".mp3")!=-1)
	    	        	createVideoThumbnail(true);
	    	        	else
	    	        	createVideoThumbnail(false);	
	    	        	PlayVideo(FILE_DIR + "/" + CURRENT_SONG);  
	    	        	MediaListView.setSelection(position);	    	        	
	    	        	player.start();						
						btnPlay.setImageResource(R.drawable.btn_pause);						
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						 songTitleLabel.setText(e.toString());							
					} catch (SecurityException e) {
						 songTitleLabel.setText(e.toString());	
					} catch (IllegalStateException e) {
						 songTitleLabel.setText(e.toString());	
					}
	         }catch (Exception e) { songTitleLabel.setText(e.toString());	} 	 	
	         }
			});
		FolderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {	       
	         public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {	  	        	
	        	 FILE_DIR=folderAdapter.getItem(position).toString();
			     listFile(FILE_DIR);	
			     songFoldersize.setText("Total: " + Integer.toString(mediaAdapter.getCount()));
	         }
			});		
		//songTitleLabel.setText ("W:"+Integer.toString(scrwidth) +"H:"+ Integer.toString(scrheight));  
	}
	
	public void createVideoThumbnail(boolean music) {
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
	    try {
	    	if(music)	   
		    {
		    	    retriever.setDataSource(FILE_DIR + "/" + CURRENT_SONG);	    	       	  
		    	    byte[] img = retriever.getEmbeddedPicture();//cause error
		    	    if (img != null)
		    	    	mVideoView.setBackgroundDrawable( new BitmapDrawable(BitmapFactory.decodeByteArray(img, 0,img.length)));
		    	    else   
		    	    	mVideoView.setBackgroundDrawable( new BitmapDrawable(BitmapFactory.decodeResource(this.getResources(), R.drawable.musiclogo)));	
		    	    
		    }else{
		    		mVideoView.setBackgroundResource(0);
		    }
	    } catch (RuntimeException ex) {
	        // Assume this is a corrupt video file.
	    } finally {
	        try {
	            retriever.release();
	        } catch (RuntimeException ex) {
	            // Ignore failures while cleaning up.
	        }
	    }	     
	}
	private int currentVolume=0;
	public void PlayVideo(String Path)
	{	
		
		try {
			
			player.stop();		
        	player.reset();	 			     
			player = new MediaPlayer();	   			
			player.setOnCompletionListener(MainActivity.this); // Important	
			
			songProgressBar.setProgress(0);
	   		player.setDisplay(mVideoView.getHolder());
	   		//player.setDataSource(Path);	 
	   		File file = new File(Path);
			FileInputStream inputStream = new FileInputStream(file);
			player.setDataSource(inputStream.getFD());			
			inputStream.close();
	        player.prepare();
			player.start();				
	    	updateProgressBar();
	    	currentVolume = amanager.getStreamVolume(AudioManager.STREAM_MUSIC);
		} catch (IllegalStateException e) {
			songTitleLabel.setText(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			songTitleLabel.setText(e.toString());
			e.printStackTrace();
		}         
    	
	}
	private Context getActivity() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getsongfromlist(int position)
	{
		 Object o = MediaListView.getItemAtPosition(position);
         String[] values = o.toString().split("\\:");
         CURRENT_SONG=values[1].trim();	 
         return CURRENT_SONG;
	}		
	
	public void ListMediaFolders(File aFile) {	
	    spc_count++;
	    String spcs = "";
	    for (z = 0; z < spc_count; z++)
	      spcs += " ";	     	
	     if(aFile.isFile()){
	 	    	name=aFile.getName();	 	    
	 	    	 if (name.indexOf(".mp3")!=-1 || name.indexOf(".mp4")!=-1 || 
	 			  name.indexOf(".avi")!=-1 || name.indexOf(".mkv")!=-1 || name.indexOf(".flv")!=-1 ||
	 			  name.indexOf(".mpg")!=-1 || name.indexOf(".mov")!=-1 || name.indexOf(".wmv")!=-1 ||
	 			  name.indexOf(".3gp")!=-1){
	 	    		 newparent=aFile.getParent().substring(8).trim();
	 	    		 String temp = new StringBuffer("Song " ).append(Integer.toString(songid)).append(": ").append(name).toString();					
	 				 songid++;
	 	    		 mediaAdapter.add(temp);	 	    		
	 	    		 folderNameAdapter.add(aFile.getParent());
	 				 if (!folderShortList.contains(newparent))	 	    			
	 	    		 {	 	    		
	 	             folderAdapter.add(aFile.getParent());	 	 
	 	             folderShortAdapter.add(newparent);	   
	 	    		 }		 				
	 	    	  }
	 	  }	    	
	    else if (aFile.isDirectory()) {	      
	      File[] listOfFiles = aFile.listFiles();
	      if(listOfFiles!=null) {
	        for (int i = 0; i < listOfFiles.length; i++)
	        {
	        	ListMediaFolders(listOfFiles[i]);
	        }
	      } else {
	        System.out.println(spcs + " [ACCESS DENIED]");
	      }
	    }
	    spc_count--;
	  }
	public static FilenameFilter filter = new FilenameFilter() {
	    public boolean accept(File dir, String name) {
	    	if (name.indexOf(".mp3")!=-1 || name.indexOf(".mp4")!=-1 || 
		 			  name.indexOf(".avi")!=-1 || name.indexOf(".mkv")!=-1 || name.indexOf(".flv")!=-1 ||
		 			  name.indexOf(".mpg")!=-1 || name.indexOf(".mov")!=-1 || name.indexOf(".wmv")!=-1 ||
		 			  name.indexOf(".3gp")!=-1)
	        return name != null;
			return false;
	    }
	};
	public void listFile(String folder) {			
		File dir = new File(folder); 
		if(dir.isDirectory()==false){
			System.out.println("Directory does not exists : " + folder);
			return;
		} 
		// list out all the file name and filter by the extension
		String[] list = dir.list(filter); 		
		
		    mediaAdapter.clear();
		    mediaAdapter.notifyDataSetChanged();			
		    folderNameAdapter.clear();
		    folderNameAdapter.notifyDataSetChanged();		
		     int i=1;
		     for (String file : list) {
					String temp = new StringBuffer("Song " ).append(Integer.toString(i)).append(": ").append(file).toString();					
					  mediaAdapter.add(temp);					  
					  folderNameAdapter.add(folder);					  
					  i++;					  
				} 		    
		      MediaListView.setAdapter( mediaAdapter );	 		     
	}
	public class GenericExtFilter implements FilenameFilter {
		 
		private String ext; 
		public GenericExtFilter(String ext) {
			this.ext = ext;
		}
 
		public boolean accept(File dir, String name) {
			return (name.endsWith(ext));
		}
	}	
	
	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
			   try{				   
			   songProgressBar.setVisibility(View.VISIBLE); 
			   totalDuration = player.getDuration();
			   currentDuration = player.getCurrentPosition();	
			   currentsong.setText(" - " + Integer.toString(currentSongIndex+1));
			   songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));			 
			   songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));	
			   int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
			   songProgressBar.setProgress(progress);	        	
				if(totalDuration>currentDuration && currentDuration>=totalDuration-1000)
				   {						 
					   if(currentSongIndex < (MediaListView.getCount()-1))	
						{
						  
							currentSongIndex=currentSongIndex+1;	
							getsongfromlist(currentSongIndex); 
							songTitleLabel.setText ("Song "+Integer.toString(currentSongIndex + 1) +": "+ CURRENT_SONG); 	
			    	        	PlayVideo(FILE_DIR + "/" + CURRENT_SONG);
								btnPlay.setImageResource(R.drawable.btn_pause);	
					}
				   }			
				    mHandler.postDelayed(this, 500);
			   }catch (Exception e) {	
				   setscreen();							
				   videofullscreen=false; 	
				   mediaAdapter.clear();
				   mediaAdapter.notifyDataSetChanged();				   
				   folderAdapter.clear();
				   folderAdapter.notifyDataSetChanged();
				   folderShortAdapter.clear();
				   folderShortAdapter.notifyDataSetChanged();
				   folderNameAdapter.clear();
				   folderNameAdapter.notifyDataSetChanged();	
				   newparent=null;
				   spc_count=-1;	
				   currentSongIndex=0;				   
				   z=0;		
				   songid=1;
				   try  
			         {	 player = new MediaPlayer();	   			
				   		 player.reset();	
				   		 player.setOnCompletionListener(MainActivity.this); // Important		    	
				   		 FILE_DIR="/sdcard"		  ; 
						 File aFile = new File(FILE_DIR);
						 ListMediaFolders(aFile);			
						 MediaListView.setAdapter( mediaAdapter );     
						 FolderListView.setAdapter(folderShortAdapter); 
					   		ctrl.setMediaPlayer(mVideoView);                
					   		mVideoView.setMediaController(ctrl); 
					   		songFoldersize.setText("Total: " + Integer.toString(mediaAdapter.getCount()));
			         }
				    catch (Exception ex)  
			        {  	    	  
				    	songTitleLabel.setText(ex.toString());  
			        }
			   } 	 	
		   }
		   
		};	
		public void updateProgressBar() {
	        mHandler.postDelayed(mUpdateTimeTask, 100);        
	    }	
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {			
		}
		public void onStartTrackingTouch(SeekBar seekBar) {
			// remove message Handler from updating progress bar
			if(player.isPlaying()){	
			mHandler.removeCallbacks(mUpdateTimeTask);
			}
	    }
	    public void onStopTrackingTouch(SeekBar seekBar) {
	    	if(player.isPlaying()){	
			mHandler.removeCallbacks(mUpdateTimeTask);
			int totalDuration = player.getDuration();
			int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
			
			// forward or backward to certain seconds
			player.seekTo(currentPosition);
			
			// update timer progress again
			updateProgressBar();
	    	}
	    }	 
	public class DummySectionFragment extends Fragment {
		
		public  final static String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			 Bundle args = getArguments();
				
			    switch (args.getInt(ARG_SECTION_NUMBER)){
			    case 1: 			    
			        //return inflater.inflate(R.layout.page_obd, container, false);
			        View obd_gps = inflater.inflate(R.layout.obd_gps, container, false);
			        startOBD_GPS(obd_gps);
			        return obd_gps;
			    case 2:
			    	 //return inflater.inflate(R.layout.page_gps, container, false);	
			    	    View medya = inflater.inflate(R.layout.player, container, false);
				        startMEDYA(medya);	
				        return medya;			    	
			    }
			    TextView textView = new TextView(getActivity());			    
			    return textView;
		}	
		 
	}
	@Override
	public void onCompletion(MediaPlayer arg0) {
		player.release();
		player = null;		
	}	
	
}

