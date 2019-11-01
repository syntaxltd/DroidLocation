# DroidLocation  [![](https://jitpack.io/v/JobGetabu/DroidLocation.svg)](https://jitpack.io/#JobGetabu/DroidLocation)

Getting location updates requires lots of boilerplate code in Android, You need to take care of
- Google Play services availability Check, Update Google play Service Dialog
- Creation of GoogleApiClient and its callbacks connected,disconnected etc.
- Stopping and releasing resources for location updates
- Handling Location permission scenarios
- Checking Location services are On or Off
- Getting last known location is not so easy either
- Fallback to last known location if not getting location after certain duration

**DroidLocation** does all this stuff in background, so that you can concentrate on your business logic than handling all above

Extend your `Activity` from `DroidLocationAppCompatActivity` or `DroidLocationActivity`:

*Create location request according to your needs*

```java
LocationRequest locationRequest = new LocationRequest()
        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        .setInterval(5000)
        .setFastestInterval(5000);
```                        
*Create DroidLocation request, and set locationRequest created*
```java
DroidLocationRequest droidLocationRequest = new DroidLocationRequestBuilder()
        .setLocationRequest(locationRequest)
        .setFallBackToLastLocationTime(3000)
        .build();
}
```
**Request Single location update like this**
```java
requestSingleLocationFix(droidLocationRequest);
```
**Or Request Multiple location updates like this**
```java
requestLocationUpdates(droidLocationRequest);
```

**You're good to go!**, You will get below callbacks now in your activity

```java
    @Override
    public void onLocationPermissionGranted() {
    }

    @Override
    public void onLocationPermissionDenied() {
    }

    @Override
    public void onLocationReceived(Location location) {
        requestAddressServices(location)
    }

    @Override
    public void onLocationAddressReceived(String fullAddress) {
         Log.d(TAG, fullAddress)
    }

    @Override
    public void onLocationProviderEnabled() {
    }

    @Override
    public void onLocationProviderDisabled() {
    }
```

**Additional Options**

Specify what messages you want to show to user using *DroidLocationRequestBuilder*
```java
DroidLocationRequest droidLocationRequest = new DroidLocationRequestBuilder()
.setLocationRequest(locationRequest)
.setLocationPermissionDialogTitle(getString(R.string.location_permission_dialog_title))
.setLocationPermissionDialogMessage(getString(R.string.location_permission_dialog_message))
.setLocationPermissionDialogNegativeButtonText(getString(R.string.not_now))
.setLocationPermissionDialogPositiveButtonText(getString(R.string.yes))
.setLocationSettingsDialogTitle(getString(R.string.location_services_off))
.setLocationSettingsDialogMessage(getString(R.string.open_location_settings))
.setLocationSettingsDialogNegativeButtonText(getString(R.string.not_now))
.setLocationSettingsDialogPositiveButtonText(getString(R.string.yes))
.build();
```




# Get started 

In your `build.gradle`:

Add the following maven{} line to your **PROJECT** build.gradle file

```
allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }   // add this line
		}
	}
```

**com.google.android.gms:play-services-location** dependency also needs to be added like this

**x.x.x** can be replaced with google play service version your app is using [versions information available here](https://developers.google.com/android/guides/releases) 

## for non androidX
```gradle
 dependencies {
    implementation 'com.github.JobGetabu:DroidLocation:v1.0'
    implementation "com.google.android.gms:play-services-location:x.x.x"
 }
```

 ## for androidX
```gradle
  dependencies {
     implementation 'com.github.JobGetabu:DroidLocation:v3.0.0'
     implementation "com.google.android.gms:play-services-location:x.x.x"
  }
```

## TODO 

- [ ] Add library to jcenter
- [x] migrate to androidX
- [ ] Add kotlin docs
- [ ] Creating geofences



## Library License

    Copyright 2019 Job Getabu

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
