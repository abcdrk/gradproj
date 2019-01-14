EMPATICA API (2.1)
==================

The EmpaLink API allows you to create Android applications that can communicate with Empatica E4

First of all, you need to make sure your phone runs Android 4.4 KitKat (API level 19) or higher.
Android 4.3 doesn't offer a stable enough connection, while previous versions don't support Bluetooth 4.0 (BLE) at all, and, therefore, are not compatible with the Empatica API.

This release is meant to be used with Android Studio 3.1 or later.

DEPENDENCIES
------------

* [Ok HTTP Client](http://square.github.io/okhttp) - 2.5.0  

Since "Android Asynchronous Http Client" is no longer supported by Android, we replaced the library with Ok HTTP by Square.


INSTALLATION INSTRUCTIONS
-------------------------

1. Open your project in Android Studio 

2. Open your main build.gradle (project root) and change the trailing lines to:
	```
	allprojects {
	    repositories {
	        jcenter()
	        flatDir {
	            dirs 'libs'
	        }
             google()
	    }
	}
	```

3. Copy `empalink-2.2.aar` in the folder `libs` in the `app` folder (or in the folder with the name of your app). If the folder doesn't exist, create it.

4. Open your app `build.gradle` and, in the `dependencies { ... }` block, add the following line:

	* `compile 'com.squareup.okhttp:okhttp:2.5.0'`
	* `compile 'com.empatica.empalink:empalink:2.2@aar'`

5. Make sure your `build.gradle` has a `minSdkVersion 19` (or higher) line.

6. Sync gradle

_NOTE_: if you encounter any problem during the installation, have a look at our sample project at:
[EmpaLink Sample Project](https://github.com/empatica/empalink-sample-project-android)

USAGE
-----

First of all, you need to instantiate an `EmpaDeviceManager`, passing your application context, and references to classes implementing `EmpaDataDelegate` and `EmpaStatusDelegate`.  
Then, you must register your **API Key** using the Device Manager's `authenticateWithAPIKey()` method.

Here's an example:

	public class MainActivity extends Activity implements EmpaDataDelegate, EmpaStatusDelegate {

    	private EmpaDeviceManager deviceManager;

    	protected void onCreate(Bundle savedInstanceState) { 
    	
    		[...]

			deviceManager = new EmpaDeviceManager(getApplicationContext(), this, this);  
			deviceManager.authenticateWithAPIKey("YOUR API KEY");
		}
		
		[...]

	}

When the Device Manager is ready for use, your `EmpaStatusDelegate` will receive the `EmpaStatus.READY` value via `didUpdateStatus()`.  
The Device Manager is now ready to scan for Empatica Devices, using: `deviceManager.startScanning()`.  
If any devices are in range, you will receive them through the `EmpaStatusDelegate` callback `didDiscoverDevice(BluetoothDevice device, String deviceLabel, int rssi, boolean allowed)`.  
If `allowed` is true, you can then connect to the device as follows: `deviceManager.connectDevice(device)`.  
If the connection request is successful, the device will start streaming data, which will be transferred to your `EmpaDataDelegate` by invoking its callback methods.

Please check the Javadoc documentation for details about all the available methods.
