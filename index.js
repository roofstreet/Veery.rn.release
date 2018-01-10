
import { NativeModules,Platform} from 'react-native';

const { RNVeery } = NativeModules;

class Veery {
  constructor() {

  }

 static  GEOJSON_LINESTRING = "LineString";
  static  GEOJSON_MULTIPOINT =  "MultiPoint";
  static  HISTORY_RAW = 1;
  static  HISTORY_ROUTEMATCH = 2;
  static  DEACTIVATE_ALL = -4;
  static  FOREGROUND = 0;
  static  BACKGROUND = 4;
  static  BACKEND = 8;
  static  GEOPROFILE = 12;
  static  NOTIF_HELLO = "hello";
  static  NOTIF_ROUTEMATCH = "routematch";
  static  NOTIF_POI = "poi";
  static  NOTIF_PREDICTION = "prediction";

  static serviceConnect(){
    RNVeery.serviceConnect();
  }
  static serviceDisconnect(){
    RNVeery.serviceDisconnect();
  }
  static serviceResume(){
    RNVeery.serviceResume();
  }
  static servicePause(){
    RNVeery.servicePause();
  }
  static setApiKeySecret(key){
    RNVeery.setApiKeySecret(key);
  }
  static activate(x){
    RNVeery.activate(x);
  }
//--------------currentLocation--------------------------------
  static getCurrentLocation(callback){
    RNVeery.getCurrentLocation(
      (position)=> {
        if (position !== null) {
          callback(position);
        }else {
          callback(null);
        }

      }
    )
  }

  static getCurrentLocationAge(callback){
    RNVeery.getCurrentLocationAge(
      (locationAge)=>{
        if (locationAge !== null) {
          callback(locationAge);
        }else {
          callback(null);
        }
        }
    )
  }
  static requestLocationUpdate(){
    RNVeery.requestLocationUpdate();
  }
  static stopLocationUpdate(){
    RNVeery.stopLocationUpdate();
  }
  //-------------------------Get Status--------------------------------------
  static getStatus(callback){
    RNVeery.getStatus(
      (status) => {
        callback(status);
      }
    )
  }
//-------------------------Pois-------------------------------------
  static getPois(callback){
    RNVeery.getPois(
      (call) => {
        if (call !== null){
          let pois = new Veery.Pois(call);
          callback(pois);
        }else {
          callback(null)
        }

      }
    )
  }
  static requestPoiUpdate(){
    RNVeery.requestPoiUpdate();
  }
  static stopPoiUpdate(){
    RNVeery.stopPoiUpdate();
  }
  static  Pois = class {
    constructor(pois) {
      this.pois = pois;//JSON.parse(pois);
      // console.log('constructor POIS ----toarray-----',pois.toArray);
      // console.log('constructor POIS ------count---',pois.count);
      }
    toArray(){
      // console.log('toArray POIS------------Module gagné',this.pois.toArray);
      return this.pois.toArray;
    }

    toGeoJSONArray(){
      return this.pois.toGeoJSONArray;
    }
    count(){
      return this.pois.count;
    }
    getWeight(x){
      return this.pois.getWeight[x];
    }
  }

  //------------------------LocationHistory--------------------------

  static getLocationHistory(format,since,until,callback){
    RNVeery.getLocationHistory(format,since,until,
      (call) => {
        //console.log('getlocation history App.js------call--------->',call,'<------------');
        if (call !== null){
          let locations = new Veery.Locations(call,format)
          callback(locations);
        }else{
          callback(null);
        }

      })
  }
  static countLocationHistory(format,since,until,callback){
    RNVeery.countLocationHistory(format,since,until,
    (count) =>{
      //console.log('VeeryModule : countLocationHistory [count]',count);
      callback(count);
    })
  }

  // static updateLocationTest(){
  //   RNVeery.updateLocation();
  // }
  static requestRouteMatch(){
    RNVeery.requestRouteMatch();
  }
  static stopRouteMatch(){
    RNVeery.stopRouteMatch();
  }

  static Locations = class {
    constructor(locations,format) {
      this.locations = locations;
      this.format = format;
    }
    toArray(){
      return this.locations.toArray;
    }
    toGeoJSON(geometrie){
      if (this.format == Veery.HISTORY_RAW) {
        if (geometrie == Veery.GEOJSON_MULTIPOINT) {
          return this.locations.toGEOJSON_MULTIPOINT;
        }else{
          return this.locations.toGEOJSON_LINESTRING;
        }
      }
      return;
    }
    toGeoJSONArray(){
      if (this.format == Veery.HISTORY_ROUTEMATCH) {
        return this.locations.toGeoJSONArray;
      }
      return;
    }
    getBoundingBox(){
      return this.locations.getBoundingBox;
    }
  }
  //------------------------Predictions-------------------------

  static getNextTrip(callback){
    RNVeery.getNextTrip(
      (nexttrip) =>{
        if (nexttrip !== null) {
          let predictions = new Veery.Predictions(nexttrip);
          callback(predictions);
        }else {
          callback(null)
        }

      }
    )
  }
  static requestPredictionUpdate(){
    RNVeery.requestPredictionUpdate();
  }
  static stopPredictionUpdate(){
    RNVeery;stopPredictionUpdate();
  }
  static Predictions = class {
    constructor(predictions) {
      this.predictions = predictions;
    }
      isOK(){
      return this.predictions.isOK;
    }
      isOutdated(){
      return this.predictions.isOutdated;
    }
      getProbability(){
      return this.predictions.probability;
    }
      getDestinationLongitude(){
      return this.predictions.DestinationLongitude;
    }
      getDestinationLatitude(){
      return this.predictions.DestinationLatitude;
    }
     getTrip(){
      return this.predictions.Trip;
    }
     toGeoJSON(){
      return this.predictions.toGeoJSON;
    }
     toLocations(){
      return this.predictions.toLocations;
    }
     getStartTrip(){
      return this.predictions.startTrip;
    }
     getStartTime(){
      return this.predictions.startTime;
    }
     getStartName(){
      return this.predictions.startName;
    }
     getArrivalTime(){
      return this.predictions.arrivalTime;
    }
     getArrivalTimeUTC(){
      return this.predictions.arrivalTimeUTC;
    }
     getArrivalName(){
      return this.predictions.arrivalName;
    }
  }
  // Subscribes and Tags

  static setVeeryToken(token){
    RNVeery.setVeeryToken(token);
  }
  static VeeryNotificationHandler(notification, callback){
    if (Platform.OS === 'ios' ) {
      RNVeery.VeeryNotificationHandler(notification.getData(), (call) => {
        callback(call);
      });
    }else {
      // TODO : for Android
      let from = notification.messageFrom;
      //console.log("VeeryNotificationHandler --Android--messagefrom :",from);
      if (from === "veery") {
        
        if (notification.subscription !== undefined) {
          let sub = notification.subscription;
          //console.log("VeeryNotificationHandler --Android--sub :",sub);
          if(notification.message !== undefined){
            let msg = notification.message;
            //console.log("VeeryNotificationHandler --Android--msg :",msg);
            //console.log("VeeryNotificationHandler --Android--message :",msg.message);
            switch (sub) {
              case "routematch":
              case "routematch_silent":
                RNVeery.VeeryNotificationHandler(sub,msg.trip_id);
                break;
              case "prediction":
              case "prediction_silent":
                RNVeery.VeeryNotificationHandler(sub,msg.prediction_id);
                break;
              case "poi":
              case "poi_silent":
                RNVeery.VeeryNotificationHandler(sub,msg.poi_id);
                break;
              case "hello_silent":
                RNVeery.VeeryNotificationHandler(sub,0);
                break;
              default:
                break;
            }
          }
        }
        callback(true);
      }else {
        callback(false);
      }
    }

  }
  static registerNotification(subscription , format){
    //console.log('VeeryModule ===> registerNotification',subscription)
    RNVeery.registerNotification(subscription,format);
  }
  static unregisterNotification(subscription){
    RNVeery.unregisterNotification(subscription);
  }
  static setTags(tagName,value){
    RNVeery.setTags(tagName,value);
  }
  static getTags(tagName){
    RNVeery.getTags(tagName);
  }
  static unsetTags(tagName){
    RNVeery.unsetTags(tagName);
  }
  static resetLocalHistory(){
    RNVeery.resetLocalHistory();
  }
  static resetBackendHistory(){
    RNVeery.resetBackendHistory();
  }
  static resetGeoProfileHistory(){
    RNVeery.resetGeoProfileHistory();
  }

}
//export {Veery};
module.exports = Veery;
