package com.car_tablet;

import java.io.File;
import java.util.Locale;

import android.annotation.TargetApi;
import android.os.Build;

public class VoltReaderFactory {
	 static final String BUILD_MODEL = Build.MODEL.toLowerCase(Locale.ENGLISH);
	 @TargetApi(4)
	  static public Long getValue() {
	  File f = null;	
	  f = new File("/sys/devices/platform/cpcap_battery/power_supply/battery/voltage_now");
	  if (f.exists()) 
	  return OneLineReader.getValue(f, false);
	return null;		 
	 }
}