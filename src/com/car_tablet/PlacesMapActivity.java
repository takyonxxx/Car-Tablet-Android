package com.car_tablet;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
public class PlacesMapActivity extends MapActivity {

    private MyLocationOverlay mMyLocationOverlay;    
    MapView mapView;
	private AddItemizedOverlay itemizedOverlay;	
	List<Overlay> mapOverlays;
	GeoPoint p=null,geoPointTarget=null,geoPointCurrent=null;	
	MapController mc;		
	OverlayItem overlayitem;
	private EditText targetlat,targetlon;
	private TextView distanceTarget,currentlat,currentlon;			
	private Button btn_goto;
	private LocationManager locManager;
	private LocationListener locListener;
	private Location mobileLocation;	
	private double CRNTLatitude;
	private double CRNTLongitude;
	public float CRNTBearing;
	private boolean gotostatus = false;
	private gauge_bearing targetbearing;	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.map_places);	
		try{
    	this.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				Intent i = getIntent();				
				mapView = (MapView) findViewById(R.id.mapView);		
				mapView.setBuiltInZoomControls(true);	
				mapView.setSatellite(true);	
						
				mc = mapView.getController();
				mc.setZoom(17);				
				targetbearing = (gauge_bearing) findViewById(R.id.gauge_bearing);						
				targetbearing.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
				MapOverlay mapOverlay = new MapOverlay();
		        List<Overlay> listOfOverlays = mapView.getOverlays();
		        listOfOverlays.clear();
		        listOfOverlays.add(mapOverlay);    
				mapOverlays = mapView.getOverlays();							
					
				targetlat= (EditText) findViewById(R.id.targetlat);
				targetlon= (EditText) findViewById(R.id.targetlon);	
				currentlat= (TextView) findViewById(R.id.currentlat);
				currentlon= (TextView) findViewById(R.id.currentlon);	
				
				distanceTarget= (TextView) findViewById(R.id.distancetarget);	
				//distanceTarget.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);				
				btn_goto=(Button) findViewById(R.id.btn_goto);	
				
				String user_latitude = i.getStringExtra("user_latitude");
				String user_longitude = i.getStringExtra("user_longitude");	
				if(Double.parseDouble(user_latitude)==0 || Double.parseDouble(user_longitude)==0)
				{
					LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					List<String> matchingProviders = locationManager.getAllProviders();
					for (String provider: matchingProviders) {
					  Location location = locationManager.getLastKnownLocation(provider);	
					  if(location!=null)
					  {
						  p = new GeoPoint((int) (location.getLatitude() * 1E6),
									(int) (location.getLongitude() * 1E6));     
					  }						  
					}
				}else{				
					p = new GeoPoint((int) (Double.parseDouble(user_latitude) * 1E6),
						(int) (Double.parseDouble(user_longitude) * 1E6)); 		
				}
				
               targetlat.setText(Double.toString(p.getLatitudeE6() / 1E6));
			   targetlon.setText(Double.toString(p.getLongitudeE6() /1E6));
								
				distanceTarget.requestFocus();
				setgeopoint(Double.toString(p.getLatitudeE6() / 1E6),Double.toString(p.getLongitudeE6() /1E6),targetlat.getText().toString(),targetlon.getText().toString());
				GetCurrentLocation();
		}catch(Exception e){}
				btn_goto.setOnClickListener(new OnClickListener() {
		            @Override
		            public void onClick(View v) {
		            	if(btn_goto.getText().equals("Go TO"))
		            	{
		            		setgeopoint(Double.toString(CRNTLatitude),Double.toString(CRNTLongitude),targetlat.getText().toString(),targetlon.getText().toString());	
			            	gotostatus=true;			            	
			            	btn_goto.setText("STOP");	
		            	}else
		            	{		            		
		            		gotostatus=false;
		            		btn_goto.setText("Go TO");	
		            	}
		            }
		        });		
				
	}	
	public static int metersToRadius(float meters, MapView map, double latitude) {
	    return (int) (map.getProjection().metersToEquatorPixels(meters) * (1/ Math.cos(Math.toRadians(latitude))));         
	}
	private void GetCurrentLocation() { 	
		locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);			
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
										
					CRNTLatitude=mobileLocation.getLatitude();					
					CRNTLongitude=mobileLocation.getLongitude();
					CRNTBearing=mobileLocation.getBearing();					
					setgeopoint(Double.toString(CRNTLatitude),Double.toString(CRNTLongitude),targetlat.getText().toString(),targetlon.getText().toString());
				} 
			}
		};
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
	} 
	private void setgeopoint(String current_latitude,String current_longitude,String target_latitude,String target_longitude)
	{		
		mapOverlays.clear();
		MapOverlay mapOverlay = new MapOverlay();
        List<Overlay> listOfOverlays = mapView.getOverlays();
        listOfOverlays.clear();
        listOfOverlays.add(mapOverlay);    
		mapOverlays = mapView.getOverlays();	
		
		geoPointCurrent = new GeoPoint((int) (Double.parseDouble(current_latitude) * 1E6),
				(int) (Double.parseDouble(current_longitude) * 1E6));
		Drawable drawable_user = this.getResources()
				.getDrawable(R.drawable.mark_blue);		
		itemizedOverlay = new AddItemizedOverlay(drawable_user, this);	
		overlayitem = new OverlayItem(geoPointCurrent, "Your Location","That is you!");
		itemizedOverlay.addOverlay(overlayitem);		
		mapOverlays.add(itemizedOverlay);
		itemizedOverlay.populateNow();	
		mapView.getController();
		mc.animateTo(geoPointCurrent);		
		mapView.invalidate();	
		targetbearing.rotaterose(-1*CRNTBearing);
		currentlat.setText("Lat:" + String.format("%10.6f", geoPointCurrent.getLatitudeE6() / 1E6));
		currentlon.setText("Lon:" + String.format("%10.6f", geoPointCurrent.getLongitudeE6() /1E6));
		if(gotostatus)  
		{
			geoPointTarget = new GeoPoint((int) (Double.parseDouble(target_latitude) * 1E6),
					(int) (Double.parseDouble(target_longitude) * 1E6));
			Location currentLocation = new Location("reverseGeocoded");
			currentLocation.setLatitude(geoPointCurrent.getLatitudeE6()/ 1e6);          
			currentLocation.setLongitude(geoPointCurrent.getLongitudeE6()/ 1e6); 
			targetbearing.setval(GetBearing(geoPointCurrent,geoPointTarget));			
            Location targetLocation = new Location("reverseGeocoded");
            targetLocation.setLatitude(geoPointTarget.getLatitudeE6()/ 1e6);           
            targetLocation.setLongitude(geoPointTarget.getLongitudeE6()/ 1e6); 
            String str=null;
        	double distance = (int)currentLocation.distanceTo(targetLocation); 
        	if(distance<=999)
        		 str ="Distance: " + String.valueOf(String.format("%.0f",distance)) + " m";
        	else
        		 str ="Distance: " + String.valueOf(String.format("%.1f",distance/1000)) + " km";
            distanceTarget.setText(str);           
            Drawable drawable_target = this.getResources()
    				.getDrawable(R.drawable.mark_red);		
    		itemizedOverlay = new AddItemizedOverlay(drawable_target, this);	
    		overlayitem = new OverlayItem(geoPointTarget, "Target","That is you!");
    		itemizedOverlay.addOverlay(overlayitem);		
    		mapOverlays.add(itemizedOverlay);
    		itemizedOverlay.populateNow();	   		
    		mapView.invalidate();    		
		}
	}		

	class MapOverlay extends Overlay
    {		
		
		@Override
        public boolean onTouchEvent(MotionEvent event, MapView mapView) 
        {  
			if(!gotostatus)   
			{
	            if (event.getAction() == 1) {                
	                GeoPoint p = mapView.getProjection().fromPixels(
	                    (int) event.getX(),
	                    (int) event.getY());                   
	                    targetlat.setText(Double.toString(p.getLatitudeE6() / 1E6));
	    				targetlon.setText(Double.toString(p.getLongitudeE6() /1E6));
	            }                    
            } 
            return false;
        } 
				
		@Override
	      public boolean draw(Canvas canvas, MapView mapView, boolean shadow,long when) {	        
			 Paint paint;
	         paint = new Paint();
	         paint.setColor(Color.RED);
	         paint.setAntiAlias(true);
	         paint.setStyle(Style.STROKE);
	         paint.setStrokeWidth(2);
	         Point pt1 = new Point();
	         Point pt2 = new Point();
	         Projection projection = mapView.getProjection();
	         if(geoPointCurrent!=null && geoPointTarget!=null && gotostatus)
	         {
		         projection.toPixels(geoPointCurrent, pt1);
		         projection.toPixels(geoPointTarget, pt2);
		         canvas.drawLine(pt1.x, pt1.y, pt2.x, pt2.y, paint);
	         }		                 
	         super.draw(canvas, mapView, shadow);
	         return true;
	      }
		
			
    }
	 double DEG_PER_RAD = (180.0 / Math.PI);
     private float GetBearing(GeoPoint first,GeoPoint second)
     {    	 
    	 float result=0;
    	 double lat1=first.getLatitudeE6() / 1E6;
    	 double lon1=first.getLongitudeE6() / 1E6;
    	 double lat2=second.getLatitudeE6() / 1E6;
    	 double lon2=second.getLongitudeE6() / 1E6;
    	 double dLon = lon2 - lon1;
    	 double y = Math.sin(dLon) * Math.cos(lat2);
    	 double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
    	 result=(float) Math.toDegrees(Math.atan2(y, x));
    	 float driving=0;  
    	 if (result<0)
	 	 	{
    		 result=(float) (-1*result);
	 	    }
	 	 else 
		 	{	 		 
	 		 result = (float) (360-result);
		 	}
    	 driving = result - CRNTBearing ;     	
    	 return driving;
     }
  
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}