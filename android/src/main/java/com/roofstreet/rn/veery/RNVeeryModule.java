
package com.roofstreet.rn.veery;

import android.annotation.TargetApi;
import android.location.Location;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.firebase.iid.FirebaseInstanceId;
import com.roofstreet.android.veery.Veery;

import org.json.JSONObject;

import java.util.Calendar;

// veery v2
public class RNVeeryModule extends ReactContextBaseJavaModule implements LifecycleEventListener, Veery.LocationUpdate ,Veery.RouteMatch,Veery.PoiUpdate, Veery.PredictionUpdate/*, Veery.Synchronize*/{
  public static final String REACT_CLASS = "RNVeery";

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
    TOKEN = FirebaseInstanceId.getInstance().getToken();

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

    if(veery != null) {
      veery.serviceConnect();
    }else {
      ifVeery();
    }

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
  }
  @ReactMethod
  public void activate(int level){
   if(ifVeery())
    veery.activate(level);
  }
  @ReactMethod
  public void setVeeryToken(String token){

    if(ifVeery() && !TOKEN.equals("")) {
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
  //--------------------------------------------------------
  @ReactMethod
  public void systemAuthorization(int autho,Callback callback){
    if (ifVeery())
      callback.invoke(veery.systemAuthorization(autho));


  }

  @ReactMethod
  public void userAgreement(Callback callback){
    if (ifVeery())
      callback.invoke(veery.userAgreement());
  }

  @ReactMethod
  public void userAgreementAge(Callback callback){
    if (ifVeery())
      callback.invoke(new Double(veery.userAgreementAge()));
  }

  @ReactMethod
  public void userAgreedPurpose(int PurposeVersion,String PurposeText,String approvalButtonText, String rejectionButtonText, Boolean agreed){

    if (ifVeery())
      veery.userAgreedPurpose(PurposeVersion,PurposeText,approvalButtonText,rejectionButtonText,agreed);
  }
  //--------------------------Outils-------------------------
  private WritableMap veeryLocationsToWritableMap (Veery.Locations mLocations, int format){


     final WritableMap map = new WritableNativeMap();

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
         maps.pushMap(map1);
       }
     map.putArray("toArray", maps);
     if (format == Veery.HISTORY_RAW) {
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
         }
       map.putArray("toGeoJSONArray", writableArray);
     }
     if (mLocations.getBoundingBox() != null) {
       WritableMap northeast = new WritableNativeMap();
       northeast.putDouble("latitude", mLocations.getBoundingBox().northeast.latitude);
       northeast.putDouble("longitude", mLocations.getBoundingBox().northeast.longitude);

       WritableMap southwest = new WritableNativeMap();
       southwest.putDouble("latitude", mLocations.getBoundingBox().southwest.latitude);
       southwest.putDouble("longitude", mLocations.getBoundingBox().southwest.longitude);

       WritableMap boundingbox = new WritableNativeMap();
       boundingbox.putMap("northeast", northeast);
       boundingbox.putMap("southwest", southwest);

       map.putMap("getBoundingBox", boundingbox);
     }


    return map;
  }

  private WritableMap poisToWritableMap(Veery.Pois pois){
    WritableMap map = new WritableNativeMap();

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


    WritableArray toGeoJSONArray = new WritableNativeArray();
    JSONObject[] objects =  pois.toGeoJSONArray();
    if (objects != null)
      for (int i=0 ; i < objects.length ; i++){
        toGeoJSONArray.pushString(objects[i].toString());
      }
    map.putArray("toGeoJSONArray",toGeoJSONArray);


    map.putInt("count",pois.count());


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

      WritableMap startTrip = new WritableNativeMap();
      startTrip.putDouble("longitude", predictions.getStartTrip().getLongitude());
      startTrip.putDouble("latitude", predictions.getStartTrip().getLatitude());
      map.putMap("startTrip", startTrip);

      Calendar startdate = predictions.getStartTime();
      map.putString("startTime", dateToString(startdate));
      map.putString("startName", predictions.getStartName());

      Calendar arrivaldate = predictions.getArrivalTime();
      map.putString("arrivalTime", dateToString(arrivaldate));
      map.putDouble("arrivalTimeUTC", predictions.getArrivalTimeUTC());
      map.putString("arrivalName", predictions.getArrivalName());
    }
    return map;
  }

  private boolean ifVeery(){
    if (veery == null) {
      if (getCurrentActivity()!=null) {
        try {
          veery = new Veery(getCurrentActivity());
          veery.serviceConnect();
          if (!TOKENN_SENT && !TOKEN.equals("")) {
            veery.setFirebaseToken(TOKEN);
            TOKENN_SENT = true;
          }

          return true;
        }catch (NullPointerException e){
          e.printStackTrace();
          return false;
        }
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
//      Log.i(REACT_CLASS,"-----onHostResume---");
      veery.serviceResume();
    }
  }

  @Override
  public void onHostPause() {
    if (veery != null){
//      Log.i(REACT_CLASS,"-----onHostPause---");
      veery.servicePause();
    }
  }

  @Override
  public void onHostDestroy() {
    if (veery != null){
//      Log.i(REACT_CLASS,"-----onHostDestroy---");
      try {
        veery.serviceDisconnect();
        veery = null;
      }catch (Throwable t){
        ////
      }
    }
  }

  @ReactMethod
  public void activateWithOptin(int activate,int version,ReadableMap view,ReadableMap img, ReadableMap txt,ReadableMap btnOK,ReadableMap btnNO, int NbProposal, int proposalCycle){

      if (ifVeery()) {

          Veery.OptinView optinView = new Veery.OptinView();
          optinView.width = view.getInt("width");
          optinView.height = view.getInt("height");
          optinView.x = (float) view.getDouble("X");
          optinView.y = (float) view.getDouble("Y");
          optinView.backgroundColor = view.getString("backgroundColor");
          optinView.cornerRadius = view.getInt("cornerRadius");


          Veery.OptinImage optinImage = new Veery.OptinImage();
          optinImage.name = img.getString("name");
          optinImage.width = img.getInt("width");
          optinImage.height = img.getInt("height");
          optinImage.x = (float) img.getDouble("X");
          optinImage.y = (float) img.getDouble("Y");
          optinImage.topMargin = img.getInt("topMargin");
          optinImage.bottomMargin = img.getInt("bottomMargin");
          optinImage.rightMargin = img.getInt("rightMargin");
          optinImage.leftMargin = img.getInt("leftMargin");

          Veery.OptinText optinText = new Veery.OptinText();
          optinText.message = txt.getString("message");
          optinText.width = txt.getInt("width");
          optinText.height = txt.getInt("height");
          optinText.x = (float) txt.getDouble("X");
          optinText.y = (float) txt.getDouble("Y");
          optinText.topMargin = txt.getInt("topMargin");
          optinText.bottomMargin = txt.getInt("bottomMargin");
          optinText.rightMargin = txt.getInt("rightMargin");
          optinText.leftMargin = txt.getInt("leftMargin");

          Veery.OptinButton optinButtonOK = new Veery.OptinButton();
          optinButtonOK.text = btnOK.getString("text");
          optinButtonOK.textColor = btnOK.getString("textColor");
          optinButtonOK.color = btnOK.getString("color");
          optinButtonOK.width = btnOK.getInt("width");
          optinButtonOK.height = btnOK.getInt("height");
          optinButtonOK.topMargin = btnOK.getInt("topMargin");
          optinButtonOK.bottomMargin = btnOK.getInt("bottomMargin");
          optinButtonOK.rightMargin = btnOK.getInt("rightMargin");
          optinButtonOK.leftMargin = btnOK.getInt("leftMargin");
          optinButtonOK.cornerRadius = btnOK.getInt("cornerRadius");

          Veery.OptinButton optinButtonNO = new Veery.OptinButton();
          optinButtonNO.text = btnNO.getString("text");
          optinButtonNO.textColor = btnNO.getString("textColor");
          optinButtonNO.color = btnNO.getString("color");
          optinButtonNO.width = btnNO.getInt("width");
          optinButtonNO.height = btnNO.getInt("height");
          optinButtonNO.topMargin = btnNO.getInt("topMargin");
          optinButtonNO.bottomMargin = btnNO.getInt("bottomMargin");
          optinButtonNO.rightMargin = btnNO.getInt("rightMargin");
          optinButtonNO.leftMargin = btnNO.getInt("leftMargin");
          optinButtonNO.cornerRadius = btnNO.getInt("cornerRadius");


          try{
              veery.activateWithOptin(activate,version,optinView, optinImage, optinText, optinButtonOK, optinButtonNO,NbProposal,proposalCycle,getCurrentActivity());
          }catch (WindowManager.BadTokenException e){
              Log.e("RNVeery"," optin : WindowManager.BadTokenException :: "+e);
          }
      }
  }


}