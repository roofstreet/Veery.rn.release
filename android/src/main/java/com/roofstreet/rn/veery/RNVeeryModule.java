
package com.roofstreet.rn.veery;

import android.annotation.TargetApi;
import android.location.Location;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.LifecycleEventListener;
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

public class RNVeeryModule extends ReactContextBaseJavaModule implements LifecycleEventListener, Veery.LocationUpdate ,Veery.RouteMatch,Veery.PoiUpdate, Veery.PredictionUpdate{
  public static final String REACT_CLASS = "RNVeery";
  public static final int DEACTIVATE_ALL = Veery.DEACTIVATE_ALL;
  public static final int FOREGROUND = Veery.FOREGROUND;
  public static final int BACKGROUND = Veery.BACKGROUND;
  public static final int BACKEND = Veery.BACKEND;
  public static final int GEOPROFILE = Veery.GEOPROFILE;
  private static String TOKEN = "";
  private  static boolean TOKENN_SENT = false;
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
    reactContext.addLifecycleEventListener(this);
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
  public void serviceConnect(){

   if(ifVeery())
    Log.i("RNVeery","ServiceConnect");
  }
  @ReactMethod
  public void serviceDisconnect(){
   if(ifVeery())
     veery.serviceDisconnect();
  }

  @ReactMethod
  public void serviceResume(){
   if(ifVeery())
     veery.serviceResume();
  }

  @ReactMethod
  public void servicePause(){
   if(ifVeery())
      veery.servicePause();
  }

  @ReactMethod
  public void setApiKeySecret(String apiKeySecret){
   if(ifVeery())
    veery.setApiKeySecret(apiKeySecret);
    Log.i(REACT_CLASS,"setApiKeySecret"+apiKeySecret);
  }
  @ReactMethod
  public void activate(int level){
   if(ifVeery())
    veery.activate(level);
    Log.i(REACT_CLASS,"activate "+level);
  }
  @ReactMethod
  public void setVeeryToken(String token){

     if(ifVeery()) {
       veery.setFirebaseToken(token);
     }else{
       TOKEN = token;
       TOKENN_SENT = false;
     }

  }
  @ReactMethod
  public void VeeryNotificationHandler(String sub, int id){
   if(ifVeery())
    veery.firebaseMessageHandler(sub,id);
  }

  //-----------------------Geolocation-----------------------------------
  @ReactMethod
  public void getCurrentLocation(Callback position){
   if(ifVeery()) {
     Location location = veery.getCurrentLocation();

     if (location != null) {
       final WritableMap map = new WritableNativeMap();
       map.putDouble("accuracy", location.getAccuracy());
       map.putDouble("altitude", location.getAltitude());
       map.putDouble("bearing", location.getBearing());
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
         map.putDouble("elapsedRealtimeNanos", location.getElapsedRealtimeNanos());
       }
       map.putDouble("latitude", location.getLatitude());
       map.putDouble("longitude", location.getLongitude());
       map.putString("provider", location.getProvider());
       map.putDouble("speed", location.getSpeed());
       map.putDouble("time", location.getTime());
       //Log.i(REACT_CLASS,"count = " +veery.countLocationHistory(1,null,null)+ ", Location = " + map.toString());
       position.invoke(map);
     }
   }
  }
  @ReactMethod
  public void getCurrentLocationAge(Callback age){
   if(ifVeery())
    age.invoke((int)veery.getCurrentLocationAge());
  }

  @ReactMethod
  public void stopLocationUpdate(){
   if(ifVeery()) {
     mRequestLocation = false;
     veery.stopLocationUpdate();
   }
  }

  ///For Debug

  // TODO : delete
  @ReactMethod
  public void updateLocation(){
   if(ifVeery())
    if (veery.getCurrentLocation() != null)
      onLocationUpdate(veery.getCurrentLocation(),veery.getCurrentLocationAge());
  }

  @ReactMethod
  public void requestLocationUpdate(){
   if(ifVeery())
      mRequestLocation = true;
  }
  //-----------------LocationHistory-------------------

  @ReactMethod
  public void getLocationHistory(int format, double since,double until, Callback callback){
   if(ifVeery()) {

     Veery.Locations mLocations = veery.getLocationHistory(format, (long) since, (long) until);
     if (mLocations != null) {
       callback.invoke(veeryLocationsToWritableMap(mLocations, format));
     } else {
       callback.invoke(null);
     }
   }
  }
  @ReactMethod
  public void countLocationHistory(int format, double since, double until, Callback callback){
   if(ifVeery())
    callback.invoke(veery.countLocationHistory(format,(long)since,(long) until));
  }
  @ReactMethod
  public void requestRouteMatch(){
   if(ifVeery())
      mRequestRouteMatch = true;
  }

  @ReactMethod
  public void stopRouteMatch(){
   if(ifVeery())
      mRequestRouteMatch = false;
  }
  //-----------------------POIs----------------------------

  @ReactMethod
  public void getPois(Callback callback){
   if(ifVeery()) {
     Veery.Pois pois = veery.getPois();
     if (pois != null) {
       callback.invoke(poisToWritableMap(pois));
     } else {
       callback.invoke(null);
     }
   }
  }
  @ReactMethod
  public void requestPoiUpdate(){
   if(ifVeery())
      mRequestPoiUpdate = true;
  }
  @ReactMethod
  public void stopPoiUpdate(){
   if(ifVeery())
     mRequestPoiUpdate =false;
  }
  //-----------------Predicted Trip-----------------------

  @ReactMethod
  public void getNextTrip(Callback callback){
   if(ifVeery()) {
     Veery.Predictions predictions = veery.getNextTrip();
     if (predictions != null) {
       callback.invoke(predictionsToWritableMap(predictions));
     } else {
       callback.invoke(null);
     }
   }
  }

  @ReactMethod
  public void requestPredictionUpdate(){
   if(ifVeery())
    mRequestPrediction = true;
  }

  @ReactMethod
  public void stopPredictionUpdate(){
   if(ifVeery())
    mRequestPrediction = false;
  }
  //-----------------CallBack---------------------------



  @Override
  public void onLocationUpdate(Location location, long l) {

    if (mRequestLocation) {
      final WritableMap map = new WritableNativeMap();
      map.putDouble("accuracy", location.getAccuracy());
      map.putDouble("altitude", location.getAltitude());
      map.putDouble("bearing", location.getBearing());
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        map.putDouble("elapsedRealtimeNanos", location.getElapsedRealtimeNanos());
      }
      map.putDouble("latitude", location.getLatitude());
      map.putDouble("longitude", location.getLongitude());
      map.putString("provider", location.getProvider());
      map.putDouble("speed", location.getSpeed());
      map.putDouble("time", location.getTime());
      map.putDouble("age", l);
      //Log.i(REACT_CLASS, "LocationUpdate = " + map.toString());
      emitDeviceEvent("veeryLocationUpdate", map);
    }
  }

  @Override
  public void onRouteMatch(Veery.Locations locations) {
   if(ifVeery())
    if (mRequestRouteMatch){
      emitDeviceEvent("veeryRouteMatch",veeryLocationsToWritableMap(locations,Veery.HISTORY_ROUTEMATCH));
    }
  }

  @Override
  public void onPoiUpdate(Veery.Pois pois) {
    if (mRequestPoiUpdate){
      emitDeviceEvent("veeryPoisUpdate",poisToWritableMap(pois));
    }
  }

  @Override
  public void onPredictionUpdate(Veery.Predictions predictions) {
    if (mRequestPrediction)
      emitDeviceEvent("veeryPredictionUpdate",predictionsToWritableMap(predictions));
  }
  //---------------------Subscribes and Tags----------------------------------
  @ReactMethod
  public void registerNotification(String subscription , String format){
   if(ifVeery())
      veery.registerNotification(subscription,format);
  }
  @ReactMethod
  public void unregisterNotification(String subscription){
   if(ifVeery())
      veery.unregisterNotification(subscription);
  }
  @ReactMethod
  public void setTags(String tagName, String value){
   if(ifVeery())
      veery.setTags(tagName,value);
  }

  @ReactMethod
  public void getTags(String tagName, Callback callback){
   if(ifVeery())
      callback.invoke(veery.getTags(tagName));
  }

  @ReactMethod
  public void unsetTags(String tagName){
   if(ifVeery())
      veery.unsetTags(tagName);
  }
  //-----------------------------Get Status-----------------------------------
  @ReactMethod
  public void getStatus(Callback callback){
    if (ifVeery())
      callback.invoke(veery.getStatus());
  }
  //---------------------------RESET DATA----------------------------
  @ReactMethod
  public void resetLocalHistory(){
   if(ifVeery())
     veery.resetLocalHistory();
  }
  @ReactMethod
  public void resetBackendHistory(){
   if(ifVeery())
      veery.resetBackendHistory();
  }
  @ReactMethod
  public void resetGeoProfileHistory(){
   if(ifVeery())
      veery.resetGeoProfileHistory();
  }
  //--------------------------Outils-------------------------
  private WritableMap veeryLocationsToWritableMap (Veery.Locations mLocations, int format){


     final WritableMap map = new WritableNativeMap();

     //ArrayList<WritableMap> maps = new ArrayList<>();
     WritableArray maps = new WritableNativeArray();
     Location[] locations = mLocations.toArray();
     if (locations != null)
       for (int i = 0; i < locations.length; i++) {
         final WritableMap map1 = new WritableNativeMap();
         map1.putDouble("accuracy", locations[i].getAccuracy());
         map1.putDouble("altitude", locations[i].getAltitude());
         map1.putDouble("bearing", locations[i].getBearing());
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
           map1.putDouble("elapsedRealtimeNanos", locations[i].getElapsedRealtimeNanos());
         }
         map1.putDouble("latitude", locations[i].getLatitude());
         map1.putDouble("longitude", locations[i].getLongitude());
         map1.putString("provider", locations[i].getProvider());
         map1.putDouble("speed", locations[i].getSpeed());
         map1.putDouble("time", locations[i].getTime());
         //Log.i(REACT_CLASS,"mLocations["+i+"] = "+ map1);
         maps.pushMap(map1);
       }
     map.putArray("toArray", maps);
     if (format == Veery.HISTORY_RAW/* HISTORY_RAW*/) {
       if (mLocations.toGeoJSON(Veery.GEOJSON_LINESTRING) != null) {
         map.putString("toGEOJSON_LINESTRING", mLocations.toGeoJSON(Veery.GEOJSON_LINESTRING).toString());
         map.putString("toGEOJSON_MULTIPOINT", mLocations.toGeoJSON(Veery.GEOJSON_MULTIPOINT).toString());
       }
     } else {
       final JSONObject[] objects = mLocations.toGeoJSONArray();

       WritableArray writableArray = new WritableNativeArray();
       if (objects != null)
         for (int i = 0; i < objects.length; i++) {
           writableArray.pushString(objects[i].toString());
           //Log.i(REACT_CLASS,"toGeoJSONArray mLocations["+i+"] = "+ objects.toString());
         }
       map.putArray("toGeoJSONArray", writableArray);
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



      map.putBoolean("isOK", predictions.isOK());

      map.putBoolean("isOutdated", predictions.isOutdated());

    if (predictions.isOK()) {
      map.putDouble("probability", predictions.getProbability());


      map.putDouble("DestinationLongitude", predictions.getDestinationLongitude());
      // Predictions.getDestinationLatitude()
      map.putDouble("DestinationLatitude", predictions.getDestinationLatitude());


      map.putString("Trip", predictions.getTrip().toString());

      map.putString("toGeoJSON", predictions.toGeojson().toString());


      WritableArray tolocations = new WritableNativeArray();
      Location[] locations = predictions.toLocations();
      if (locations != null)
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

  private boolean ifVeery(){
    if (veery == null) {
      if (getCurrentActivity()!=null) {
        veery = new Veery(getCurrentActivity());
        veery.serviceConnect();
        if (!TOKENN_SENT){
          veery.setFirebaseToken(TOKEN);
          TOKENN_SENT = true;
        }
        return true;
      }
      return false;
    }
    return true;
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

  @Override
  public void onHostResume() {
    if (veery!= null){
      Log.i(REACT_CLASS,"-----onHostResume---");
      veery.serviceResume();
    }
  }

  @Override
  public void onHostPause() {
    if (veery != null){
      Log.i(REACT_CLASS,"-----onHostPause---");
      veery.servicePause();
    }
  }

  @Override
  public void onHostDestroy() {
    if (veery != null){
      Log.i(REACT_CLASS,"-----onHostDestroy---");
      veery.serviceDisconnect();
    }
  }
}