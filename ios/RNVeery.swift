//
//  RNVeery.swift
//  RNVeery
//
//  Created by Malek Hassani on 15/11/2017.
//  Copyright © 2017 Facebook. All rights reserved.
//

import Foundation
//  Created by react-native-create-bridge


import Veery

//https://stackoverflow.com/questions/29586667/react-native-pushnotificationios-doesnt-listen-push-notification

@objc(RNVeery)
class RNVeery : RCTViewManager , VeeryDelegate{
    //  // Export constants to use in your native module
    //  override func constantsToExport() -> [String : Any]! {
    //    return ["EXAMPLE_CONSTANT": "example"]
    //  }
    let veery = Veery()
    var mRequestLocation = false
    var mRequestRouteMatch = false
    var mRequestPoiUpdate = false
    var mRequestPrediction = false
    
    // Return the native view that represents your React component
    override func view() -> UIView! {
        return UIView()
    }
    
    // Implement methods that you want to export to the native module
    //  @objc func exampleMethod() {
    //    // The bridge eventDispatcher is used to send events from native to JS env
    //    // No documentation yet on DeviceEventEmitter: https://github.com/facebook/react-native/issues/2819
    //    self.bridge.eventDispatcher().sendAppEvent(withName: "EXAMPLE_EVENT", body: nil)
    //  }
    // MARK: - Veery Methodes
    @objc func serviceConnect(){
        NSLog("VeeryModule, -----serviceConnect------> called")
        veery.delegate = self
        veery.serviceConnect()
    }
    @objc func activate(_ level : NSInteger){
        veery.activate(service:  level)
    }
    @objc func setApiKeySecret(_ apikey : NSString){
        veery.setApiKeySecret(apikey as String)
        
    }
    // MARK: - Current Location
    //-------------------current Location------------------------------------------
    @objc func requestLocationUpdate(){
        mRequestLocation = true
        veery.requestLocationUpdate()
    }
    @objc func stopLocationUpdate(){
        mRequestLocation = false
        veery.stopLocationUpdate()
    }
    @objc (getCurrentLocation:)
    func getCurrentLocation(callback : RCTResponseSenderBlock) -> Void{
        if let loc = veery.getCurrentLocation(){
            let location  = [
                "altitude" : loc.altitude as Double,
                "latitude" : loc.coordinate.latitude as Double,
                "longitude" : loc.coordinate.longitude as Double,
                "course" : loc.course as Double,
                "speed" : loc.speed as Double,
                "horizontalAccuracy" : loc.horizontalAccuracy as Double,
                "verticalAccuracy" : loc.verticalAccuracy as Double,
                "timestamp" : loc.timestamp.description as NSString
                ] as NSDictionary
            
            callback([location])
        }else{
            callback([NSNull()])
        }
        
    }
    // MARK: - LOCATIONS History
    //@objc(getLocationHostory::::)
    @objc func getLocationHistory(_ format : NSInteger,_ since : NSDate,_ until : NSDate,_ callback : RCTResponseSenderBlock) -> Void{
        //    NSLog("getLocationHistory ------VeeryModuleManager---------\(format)-----\(since)-------\(until)------------")
        if let locations = veery.getLocationHistory(format, since as Date, until as Date){
            callback([veeryLocationsToNSDictionary(locations: locations,format: format)])
        }else{
            //      NSLog("getLocationHistory ------VeeryModuleManager-----------NSNull--")
            callback([NSNull()])
        }
    }
    
    @objc func requestRouteMatch(){
        mRequestRouteMatch = true
        veery.requestRouteMatch()
    }
    @objc func stopRouteMatch(){
        mRequestRouteMatch = false
        veery.stopRouteMatch()
    }
    
    // MARK: - POIs
    
    @objc func getPois(_ callback : RCTResponseSenderBlock) -> Void{
        let pois = veery.getPois()
        //NSLog("getPois -----count-->\(pois.count())")
        callback([poisToNSDictionary(pois: pois)])
        
    }
    @objc func requestPoiUpdate(){
        mRequestPoiUpdate = true
        veery.requestPoiUpdate()
    }
    @objc func stopPoisUpdate(){
        mRequestPoiUpdate = false
        veery.requestPoiUpdate()
    }
    
    // MARK: - Predictions
    @objc func getNextTrip(_ callback : RCTResponseSenderBlock) -> Void{
        if let prediction = veery.getNextTrip(){
//            NSLog("getNextTrip -----getArrivalName-->\(prediction.getArrivalName())")
            callback([predictionToNSDictionary(predictions: prediction)])
        }else{
            callback([NSNull()])
        }
    }
    @objc func requestPredictionUpdate(){
        mRequestPrediction = true
        veery.requestPredictionUpdate()
    }
    @objc func stopPredictionUpdate(){
        mRequestPrediction = false
        veery.stopPredictionUpdate()
    }
    // MARK: - Notfications
    @objc func registerNotification(_ subscription : NSString ,_ format : NSString ){
//        NSLog("registerNotification ------------\(subscription)----\(format)")
        veery.registerNotification(subscription as String, format as String)
    }
    @objc func unregisterNotification(_ subscription : NSString){
        veery.unregisterNotification(subscription as String)
    }
    // MARK: - APNs
    
    @objc func setVeeryToken(_ token : NSString){
        NSLog("setVeeryToken ------------------- \(token)")
        let tokend = Data(hexString : token as String)
        //    NSLog("setVeeryToken -------data------------ \(tokend?.description)")
        //    var tkn = ""
        //    for i in 0..<tokend!.count{
        //      tkn  = tkn + String(format: "%02.2hhX", arguments : [tokend![i]])
        //    }
        //    NSLog("setVeeryToken -------tkn------------ \(tkn)")
        
        veery.setAPNSToken(token: tokend! )//token.data(using: String.Encoding.utf8.rawValue, allowLossyConversion: false)!
        
    }
    @objc func VeeryNotificationHandler(_ data : NSDictionary,_ callback : RCTResponseSenderBlock){
//        NSLog("ReactVeery VeeryNotificationHandler-----\(data as! [AnyHashable : Any])")
        callback([veery.apnsMessageHandler(data as! [AnyHashable : Any])])
    }
    // MARK: - Tags
    @objc func setTags(_ tagName : NSString,_ value : NSString){
        veery.setTags(name: tagName  as String, value: value as String)
    }
    
    @objc func getTags(_ tagName :NSString ,_ callback : RCTResponseSenderBlock) -> Void{
        callback([veery.getTags(name: tagName as String)])
    }
    
    @objc func unsetTags(_ tagName : NSString){
        veery.unsetTags(name: tagName  as String)
    }
    
    // MARK: - RESET
    @objc func resetLocalHistory(){
        veery.resetLocalHistory()
    }
    @objc func resetBackendHistory(){
        veery.resetBackendHistory()
    }
    @objc func resetGeoProfileHistory(){
        veery.resetGeoProfileHistory()
    }
    
    // MARK: - Delegates
    
    // MARK: - veeryDidReceiveNewLocations
    func veeryDidReceiveNewLocations(_ veery: Veery, newLocations: [CLLocation]) {
        let loc = newLocations[0]
        let location  = [
            "altitude" : loc.altitude as Double,
            "latitude" : loc.coordinate.latitude as Double,
            "longitude" : loc.coordinate.longitude as Double,
            "course" : loc.course as Double,
            "speed" : loc.speed as Double,
            "horizontalAccuracy" : loc.horizontalAccuracy as Double,
            "verticalAccuracy" : loc.verticalAccuracy as Double,
            "timestamp" : loc.timestamp.description as NSString
            ] as NSDictionary
        self.bridge.eventDispatcher().sendAppEvent(withName: "LocationUpdate", body: location)
    }
    // MARK: - veeryRouteMatch
    func veeryRouteMatch(_ veery: Veery, locationsHistory: Veery.LocationsHistory) {
        self.bridge.eventDispatcher().sendAppEvent(withName: "RouteMatch", body: veeryLocationsToNSDictionary(locations: locationsHistory, format: Veery.HISTORY_ROUTEMATCH))
    
    }
    
    // MARK: - veeryPoiUpdate
    func veeryPoiUpdate(_ veery: Veery, poi: Veery.Pois) {
        self.bridge.eventDispatcher().sendAppEvent(withName: "PoisUpdate", body: poisToNSDictionary(pois: poi))
    }
    
    
    func veeryPredictionUpdate(_ veery: Veery, Predictions: Veery.Predictions) {
        self.bridge.eventDispatcher().sendAppEvent(withName: "PredictionUpdate", body: predictionToNSDictionary(predictions: Predictions))
    }
    
    
    // MARK: - Outils
    
    // MARK: - veeryLocationsToNSDictionary
    func veeryLocationsToNSDictionary(locations : Veery.LocationsHistory, format : Int) -> NSDictionary {
        // var locs : [String : Any] = [:]
        var toArray : [NSDictionary] = []
        for loc in locations.toArray(){
            let position  = [
                "altitude" : loc.altitude as Double,
                "latitude" : loc.coordinate.latitude as Double,
                "longitude" : loc.coordinate.longitude as Double,
                "course" : loc.course as Double,
                "speed" : loc.speed as Double,
                "horizontalAccuracy" : loc.horizontalAccuracy as Double,
                "verticalAccuracy" : loc.verticalAccuracy as Double,
                "timestamp" : loc.timestamp.description as NSString
                ] as NSDictionary
            toArray.append(position)//append(position as NSDictionary)
        }
        
        //locs ["toArray"] =  toArray
        
        var toGEOJSON_LINESTRING = ""
        var toGEOJSON_MULTIPOINT = ""
        var toGeoJsonArray : [String] = []
        if format == Veery.HISTORY_RAW {
            toGEOJSON_LINESTRING = String(data: locations.toGeoJSON(geometry: Veery.GEOJSON_LINESTRING)!, encoding: .utf8)!
            toGEOJSON_MULTIPOINT = String(data: locations.toGeoJSON(geometry: Veery.GEOJSON_MULTIPOINT)!, encoding: .utf8)!
            
        }else{
            let object = locations.toGeoJSONArray()
            for obj in object{
                let geojson = String(data : obj,encoding: .utf8)
                toGeoJsonArray.append(geojson!)
            }
            
        }
        let boundingbox = locations.getBoundingBox()!
        let northest : CLLocationCoordinate2D = boundingbox[1]
        let southwest : CLLocationCoordinate2D =  boundingbox[0]
        let southwestcoor = ["latitude" : southwest.latitude , "longitude" : southwest.longitude]
        let northestcoor = ["latitude" : northest.latitude , "longitude" : northest.longitude]
        
        
        
        //locs["getBoundingBox"] = ["southwest" : southwestcoor,"northest" : northestcoor]
        
        let locs = [
            "toArray" :  toArray as NSArray,
            "toGEOJSON_LINESTRING" : toGEOJSON_LINESTRING as NSString,
            "toGEOJSON_MULTIPOINT" : toGEOJSON_MULTIPOINT as NSString,
            "toGeoJSONArray" : toGeoJsonArray as NSArray,
            "getBoundingBox" : ["southwest" : southwestcoor,"northest" : northestcoor] as NSDictionary
            
            ] as NSDictionary
//        NSLog("veeryLocationsToWritableMap -----locs [toGEOJSON_MULTIPOINT]------->\(locs ["toGEOJSON_MULTIPOINT"]!)")
//        NSLog("veeryLocationsToWritableMap -----locs[getBoundingBox]------->\(locs["getBoundingBox"]!)")
//        NSLog("veeryLocationsToWritableMap -----locs [toArray]------->\(locs ["toArray"]!)")
//        NSLog("veeryLocationsToWritableMap -----locs [toGeoJSONArray]------->\(locs ["toGeoJSONArray"]!)")
//        NSLog("veeryLocationsToWritableMap -----locs ---------->\(locs )")
        return locs as NSDictionary
    }
    
    
    // MARK: - poisToNSDictionary
    func poisToNSDictionary(pois : Veery.Pois) -> NSDictionary {
        var pointsOfIntersets : [String : Any] = [:]
        var toArray : [NSDictionary] = []
        for poi in pois.toArray() {
            let location = ["longitude" : poi.longitude,"latitude" : poi.latitude]
            toArray.append(location as NSDictionary)
        }
        pointsOfIntersets["toArray"] = toArray as NSArray
        let object = pois.toGeoJSONArray()
        var toGeoJsonArray : [String] = []
        for obj in object{
            let geojson = String(data : obj,encoding: .utf8)
            toGeoJsonArray.append(geojson!)
        }
        pointsOfIntersets["toGeoJSONArray"] = toGeoJsonArray as NSArray
        pointsOfIntersets["count"] = pois.count() as NSInteger
        var getWeight : [Double] = []
        for i in 0..<pois.count(){
            getWeight.insert(pois.getWeight(index: i), at: i)
        }
        pointsOfIntersets["getWeight"] = getWeight as NSArray
        return pointsOfIntersets as NSDictionary
    }
    
    
    // MARK: - predictionNSDictionary
    func predictionToNSDictionary(predictions : Veery.Predictions) -> NSDictionary {
        var _prediction : [String : Any] = [:]
        
        _prediction["isOK"] = predictions.isOk() as Bool
        _prediction["isOutdated"] = predictions.isOutdated() as Bool
        _prediction["probability"] = predictions.getProbability() as Double
        _prediction["DestinationLongitude"] = predictions.getNextDestination()?.coordinate.longitude
        _prediction["DestinationLatitude"] = predictions.getNextDestination()?.coordinate.latitude
        _prediction["Trip"] = predictions.toGeoJson()?.description
        _prediction["toGeoJSON"] = predictions.toGeoJson()?.description
        var tolocations : [NSDictionary] = []
        for loc in predictions.toLocationCoordinate2D()! {
            let location = ["longitude" : loc.longitude,"latitude" : loc.latitude]
            tolocations.append(location as NSDictionary)
        }
        _prediction["toLocations"] = tolocations as NSArray
        let starttrip : NSDictionary = ["longitude" : predictions.getStartTrip().longitude,"latitude" : predictions.getStartTrip().latitude]
        _prediction["startTrip"] = starttrip as NSDictionary
        if let start = predictions.getStartTime() {
            _prediction["startTime"] = dateToString(date:predictions.getStartTime()!) as NSString
        }else{
            _prediction["startTime"] = " " as NSString
        }
        
        _prediction["startName"] = predictions.getStartName() as NSString
        if let arrivalT = predictions.getArrivalTime() {
            _prediction["arrivalTime"] = dateToString(date: predictions.getArrivalTime()!) as NSString
            _prediction["arrivalTimeUTC"] = dateToString(date: predictions.getArrivalTime()!) as NSString
        }else{
            _prediction["arrivalTime"] = " " as NSString
            _prediction["arrivalTimeUTC"] = " " as NSString
        }
        
        _prediction["arrivalName"] = predictions.getArrivalName() as NSString
        
        return _prediction as NSDictionary
    }
    
    func dateToString(date: Date) -> String{
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "YYYY-MM-dd'T'HH:mm:ss.SSSZ"
        return dateFormatter.string(from: date)
    }
    
    
}
extension Data {
    init?(hexString: String) {
        let len = hexString.count / 2
        var data = Data(capacity: len)
        for i in 0..<len {
            let j = hexString.index(hexString.startIndex, offsetBy: i*2)
            let k = hexString.index(j, offsetBy: 2)
            let bytes = hexString[j..<k]
            if var num = UInt8(bytes, radix: 16) {
                data.append(&num, count: 1)
            } else {
                return nil
            }
        }
        self = data
    }
}
