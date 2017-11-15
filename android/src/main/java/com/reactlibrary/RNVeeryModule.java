
package com.reactlibrary;

import android.location.Location;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.roofstreet.android.veery.Veery;

import org.json.JSONObject;

import java.util.Calendar;

public class RNVeeryModule extends ReactContextBaseJavaModule implements Veery.LocationUpdate ,Veery.RouteMatch,Veery.PoiUpdate, Veery.PredictionUpdate{
  public static final String REACT_CLASS = "RNVeery";
  public static final int DEACTIVATE_ALL = Veery.DEACTIVATE_ALL;
  public static final int FOREGROUND = Veery.FOREGROUND;
  public static final int BACKGROUND = Veery.BACKGROUND;
  public static final int BACKEND = Veery.BACKEND;
  public static final int GEOPROFILE = Veery.GEOPROFILE;
  private boolean mRequestLocation = false;
  private boolean mRequestRouteMatch = false;
  private boolean mRequestPoiUpdate = false;
  private boolean mRequestPrediction = false;
  //Veery.Locations mLocations = null;
  public  Veery veery;// = new Veery(getReactApplicationContext());
  static   ReactApplicationContext reactContext;

  public RNVeeryModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNVeery";
  }

  private static void emitDeviceEvent(String eventName, @Nullable WritableMap eventData) {
    // A method for emitting from the native side to JS
    // https://facebook.github.io/react-native/docs/native-modules-android.html#sending-events-to-javascript
    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, eventData);
  }
  @ReactMethod
  public void exampleMethod () {
    // An example native method that you will expose to React
    // https://facebook.github.io/react-native/docs/native-modules-android.html#the-toast-module
  }
  @ReactMethod
  public void serviceConnect(){
    //veery = new Veery(getReactApplicationContext().getApplicationContext());
    //final Activity activity = getCurrentActivity();
    //veery = new Veery(activity);
    ifVeery();
    veery.serviceConnect();
    Log.i("RNVeery","ServiceConnect");
  }
  @ReactMethod
  public void serviceDisconnect(){
    ifVeery();

    veery.serviceDisconnect();
  }

  @ReactMethod
  public void serviceResume(){
    ifVeery();
    veery.serviceResume();
  }

  @ReactMethod
  public void servicePause(){
    ifVeery();
    veery.servicePause();
  }

  @ReactMethod
  public void setApiKeySecret(String apiKeySecret){
    ifVeery();
    veery.setApiKeySecret(apiKeySecret);
    Log.i(REACT_CLASS,"setApiKeySecret"+apiKeySecret);
  }
  @ReactMethod
  public void activate(int level){
    ifVeery();
    veery.activate(level);
    Log.i(REACT_CLASS,"activate "+level);
  }
  @ReactMethod
  public void setVeeryToken(String token){
    ifVeery();
    veery.setFirebaseToken(token);

  }
  @ReactMethod
  public void VeeryNotificationHandler(String data, Callback callback){
    ifVeery();
    //callback.invoke(veery.firebaseMessageHandler(data));
  }

  //-----------------------Geolocation-----------------------------------
  @ReactMethod
  public void getCurrentLocation(Callback position){
    ifVeery();
    Location location =  veery.getCurrentLocation();

    if (location != null) {
      final WritableMap map = new WritableNativeMap();
      map.putDouble("Accuracy", location.getAccuracy());
      map.putDouble("Altitude", location.getAltitude());
      map.putDouble("Bearing", location.getBearing());
      map.putDouble("ElapsedRealtimeNanos", location.getElapsedRealtimeNanos());
      map.putDouble("Latitude", location.getLatitude());
      map.putDouble("Longitude", location.getLongitude());
      map.putString("Provider", location.getProvider());
      map.putDouble("Speed", location.getSpeed());
      map.putDouble("Time", location.getTime());
      //Log.i(REACT_CLASS,"count = " +veery.countLocationHistory(1,null,null)+ ", Location = " + map.toString());
      position.invoke(map);
    }
  }
  @ReactMethod
  public void getCurrentLocationAge(){
    ifVeery();
    veery.getCurrentLocationAge();
  }

  @ReactMethod
  public void stopLocationUpdate(){
    ifVeery();
    mRequestLocation = false;
    veery.stopLocationUpdate();
  }

  ///For Debug

  // TODO : delete
  @ReactMethod
  public void updateLocation(){
    ifVeery();
    if (veery.getCurrentLocation() != null)
      onLocationUpdate(veery.getCurrentLocation(),veery.getCurrentLocationAge());
  }

  @ReactMethod
  public void requestLocationUpdate(){
    ifVeery();
    mRequestLocation = true;
  }
  //-----------------LocationHistory-------------------

  @ReactMethod
  public void getLocationHistory(int format, double since,double until, Callback callback){
    ifVeery();

    Veery.Locations mLocations = veery.getLocationHistory(format,(long) since, (long) until);
    if (mLocations != null){
      callback.invoke(veeryLocationsToWritableMap(mLocations,format));
    }else{
      callback.invoke(null);
    }

  }
  @ReactMethod
  public void countLocationHistory(int format, double since, double until, Callback callback){
    ifVeery();
    callback.invoke(veery.countLocationHistory(format,(long)since,(long) until));
  }
  @ReactMethod
  public void requestRouteMatch(){
    ifVeery();
    mRequestRouteMatch = true;
  }

  @ReactMethod
  public void stopRouteMatch(){
    ifVeery();
    mRequestRouteMatch = false;
  }
  //-----------------------POIs----------------------------

  @ReactMethod
  public void getPois(Callback callback){
    ifVeery();
    Veery.Pois pois = veery.getPois();
    if (pois != null){
      callback.invoke(poisToWritableMap(pois));
    }else{
      callback.invoke(null);
    }
  }
  @ReactMethod
  public void requestPoiUpdate(){
    ifVeery();
    mRequestPoiUpdate = true;
  }
  @ReactMethod
  public void stopPoiUpdate(){
    ifVeery();
    mRequestPoiUpdate =false;
  }
  //-----------------Predicted Trip-----------------------

  @ReactMethod
  public void getNextTrip(Callback callback){
    ifVeery();
    Veery.Predictions predictions = veery.getNextTrip();
    if (predictions != null){
      callback.invoke(predictionsToWritableMap(predictions));
    }else{
      callback.invoke(null);
    }

  }

  @ReactMethod
  public void requestPredictionUpdate(){
    ifVeery();
    mRequestPrediction = true;
  }

  @ReactMethod
  public void stopPredictionUpdate(){
    ifVeery();
    mRequestPrediction = false;
  }
  //-----------------CallBack---------------------------


  @Override
  public void onLocationUpdate(Location location, long l) {
    ifVeery();
    if (mRequestLocation) {
      final WritableMap map = new WritableNativeMap();
      map.putDouble("Accuracy", location.getAccuracy());
      map.putDouble("Altitude", location.getAltitude());
      map.putDouble("Bearing", location.getBearing());
      map.putDouble("ElapsedRealtimeNanos", location.getElapsedRealtimeNanos());
      map.putDouble("Latitude", location.getLatitude());
      map.putDouble("Longitude", location.getLongitude());
      map.putString("Provider", location.getProvider());
      map.putDouble("Speed", location.getSpeed());
      map.putDouble("Time", location.getTime());
      map.putDouble("age", l);
      //Log.i(REACT_CLASS, "LocationUpdate = " + map.toString());
      emitDeviceEvent("LocationUpdate", map);
    }
  }

  @Override
  public void onRouteMatch(Veery.Locations locations) {
    ifVeery();
    if (mRequestRouteMatch){
      emitDeviceEvent("RouteMatch",veeryLocationsToWritableMap(locations,Veery.HISTORY_ROUTEMATCH));
    }
  }

  @Override
  public void onPoiUpdate(Veery.Pois pois) {
    if (mRequestPoiUpdate){
      emitDeviceEvent("PoisUpdate",poisToWritableMap(pois));
    }
  }

  @Override
  public void onPredictionUpdate(Veery.Predictions predictions) {
    if (mRequestPrediction)
      emitDeviceEvent("PredictionUpdate",predictionsToWritableMap(predictions));
  }
  //---------------------Subscribes and Tags----------------------------------
  @ReactMethod
  public void registerNotification(String subscription , String format){
    ifVeery();
    veery.registerNotification(subscription,format);
  }
  @ReactMethod
  public void unregisterNotification(String subscription){
    ifVeery();
    veery.unregisterNotification(subscription);
  }
  @ReactMethod
  public void setTags(String tagName, String value){
    ifVeery();
    veery.setTags(tagName,value);
  }

  @ReactMethod
  public void getTags(String tagName, Callback callback){
    ifVeery();
    callback.invoke(veery.getTags(tagName));
  }

  @ReactMethod
  public void unsetTags(String tagName){
    ifVeery();
    veery.unsetTags(tagName);
  }
  //---------------------------RESET DATA----------------------------
  @ReactMethod
  public void resetLocalHistory(){
    ifVeery();
    veery.resetLocalHistory();
  }
  @ReactMethod
  public void resetBackendHistory(){
    ifVeery();
    veery.resetBackendHistory();
  }
  @ReactMethod
  public void resetGeoProfileHistory(){
    ifVeery();
    veery.resetGeoProfileHistory();
  }
  //--------------------------Outils-------------------------
  private WritableMap veeryLocationsToWritableMap (Veery.Locations mLocations, int format){
    ifVeery();

    final WritableMap map = new WritableNativeMap();
    //ArrayList<WritableMap> maps = new ArrayList<>();
    WritableArray maps = new WritableNativeArray();
    Location[] locations = mLocations.toArray();
    if (locations != null)
      for (int i =0; i < locations.length; i++){
        final WritableMap map1 = new WritableNativeMap();
        map1.putDouble("Accuracy", locations[i].getAccuracy());
        map1.putDouble("Altitude", locations[i].getAltitude());
        map1.putDouble("Bearing", locations[i].getBearing());
        map1.putDouble("ElapsedRealtimeNanos", locations[i].getElapsedRealtimeNanos());
        map1.putDouble("Latitude", locations[i].getLatitude());
        map1.putDouble("Longitude", locations[i].getLongitude());
        map1.putString("Provider", locations[i].getProvider());
        map1.putDouble("Speed", locations[i].getSpeed());
        map1.putDouble("Time", locations[i].getTime());
        //Log.i(REACT_CLASS,"mLocations["+i+"] = "+ map1);
        maps.pushMap(map1);
      }
    map.putArray("toArray",  maps);
    if (format == Veery.HISTORY_RAW/* HISTORY_RAW*/) {
      if ( mLocations.toGeoJSON(Veery.GEOJSON_LINESTRING)!= null) {
        map.putString("toGEOJSON_LINESTRING", mLocations.toGeoJSON(Veery.GEOJSON_LINESTRING).toString());
        map.putString("toGEOJSON_MULTIPOINT", mLocations.toGeoJSON(Veery.GEOJSON_MULTIPOINT).toString());
      }
    }else {
      final JSONObject[] objects = mLocations.toGeoJSONArray();

      WritableArray writableArray = new WritableNativeArray();
      if (objects != null)
        for (int i =0;i < objects.length;i++){
          writableArray.pushString(objects.toString());
          //Log.i(REACT_CLASS,"toGeoJSONArray mLocations["+i+"] = "+ objects.toString());
        }
      map.putArray("toGeoJSONArray",writableArray);
    }
    if (mLocations.getBoundingBox() != null) {
      WritableMap northeast = new WritableNativeMap();
      northeast.putDouble("latitude", mLocations.getBoundingBox().northeast.latitude);
      northeast.putDouble("longitude", mLocations.getBoundingBox().northeast.longitude);
      //Log.i(REACT_CLASS,"northeast = "+ northeast);
      WritableMap southwest = new WritableNativeMap();
      southwest.putDouble("latitude", mLocations.getBoundingBox().southwest.latitude);
      southwest.putDouble("longitude", mLocations.getBoundingBox().southwest.longitude);
      //Log.i(REACT_CLASS,"southwest = "+ southwest);
      WritableMap boundingbox = new WritableNativeMap();
      boundingbox.putMap("northeast", northeast);
      boundingbox.putMap("southwest", southwest);
      //Log.i(REACT_CLASS,"getBoundingBox = "+ boundingbox);
      map.putMap("getBoundingBox", boundingbox);
    }
    //Log.i(REACT_CLASS,"getLocationHistory = "+ map);

    return map;
  }

  private WritableMap poisToWritableMap(Veery.Pois pois){
    WritableMap map = new WritableNativeMap();
    // Pois.toArray()
    WritableArray toArray = new WritableNativeArray();
    Location[] locations = pois.toArray();
    if (locations != null)
      for (int i = 0; i < locations.length; i++){
        WritableMap location = new WritableNativeMap();
        location.putDouble("longitude",locations[i].getLongitude());
        location.putDouble("latitude",locations[i].getLatitude());
        toArray.pushMap(location);
      }

    map.putArray("toArray",toArray);

    // Pois.toGeoJSONArray()
    WritableArray toGeoJSONArray = new WritableNativeArray();
    JSONObject[] objects =  pois.toGeoJSONArray();
    if (objects != null)
      for (int i=0 ; i < objects.length ; i++){
        toGeoJSONArray.pushString(objects[i].toString());
      }
    map.putArray("toGeoJSONArray",toGeoJSONArray);

    // Pois.count()
    map.putInt("count",pois.count());

    // Pois.getWeight
    WritableArray getWeight = new WritableNativeArray();
    for (int i=0; i < pois.count() ; i++){
      getWeight.pushDouble(pois.getWeight(i));
    }
    map.putArray("getWeight",getWeight);


    return map;
  }

  private WritableMap predictionsToWritableMap(Veery.Predictions predictions){
    WritableMap map = new WritableNativeMap();

    if (predictions.isOK()) {

      map.putBoolean("isOK", predictions.isOK());

      map.putBoolean("isOutdated", predictions.isOutdated());


      map.putDouble("probability", predictions.getProbability());


      map.putDouble("DestinationLongitude", predictions.getDestinationLongetude());
      // Predictions.getDestinationLatitude()
      map.putDouble("DestinationLatitude", predictions.getDestinationLatitude());


      map.putString("Trip", predictions.getTrip().toString());

      map.putString("toGeoJSON", predictions.toGeojson().toString());


      WritableArray tolocations = new WritableNativeArray();
      Location[] locations = predictions.toLocations();

      for (int i = 0; i < locations.length; i++) {
        WritableMap location = new WritableNativeMap();
        location.putDouble("longitude", locations[i].getLongitude());
        location.putDouble("latitude", locations[i].getLatitude());
        tolocations.pushMap(location);
      }

      map.putArray("toLocations", tolocations);
      // Predictions.getStartTrip()
      WritableMap startTrip = new WritableNativeMap();
      startTrip.putDouble("longitude", predictions.getStartTrip().getLongitude());
      startTrip.putDouble("latitude", predictions.getStartTrip().getLatitude());
      map.putMap("startTrip", startTrip);
      // Predictions.startTime()
      Calendar startdate = predictions.getStartTime();
      map.putString("startTime", dateToString(startdate));
      // Predictions.getStartName()
      map.putString("startName", predictions.getStartName());
      // Predictions.getArrivalTime()
      Calendar arrivaldate = predictions.getArrivalTime();
      map.putString("arrivalTime", dateToString(arrivaldate));
      // Predictions.getArrivalTimeUTC()
      map.putDouble("arrivalTimeUTC", predictions.getArrivalTimeUTC());
      // Predictions.getArrivalName()
      map.putString("arrivalName", predictions.getArrivalName());
    }
    return map;
  }

  private void ifVeery(){
    if (veery == null) {
      veery = new Veery(getCurrentActivity());
      veery.serviceConnect();
    }
  }

  private String dateToString(Calendar cal){
    return String.valueOf(cal.get(Calendar.YEAR))+"-"+intToString(cal.get(Calendar.MONTH))+"-"+intToString(cal.get(Calendar.DAY_OF_MONTH))
            +" "+intToString(cal.get(Calendar.HOUR_OF_DAY))+":"+intToString(cal.get(Calendar.MINUTE))+":"+intToString(cal.get(Calendar.SECOND));
  }
  private    static String intToString(int d){
    String s;
    if (d > 9){
      s = String.valueOf(d);
    }else{
      s = "0"+d;
    }
    return s;
  }

}