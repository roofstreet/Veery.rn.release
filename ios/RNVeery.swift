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
        
        if let locations = veery.getLocationHistory(format, since as Date, until as Date){
            callback([veeryLocationsToNSDictionary(locations: locations,format: format)])
        }else{
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
        veery.registerNotification(subscription as String, format as String)
    }
    @objc func unregisterNotification(_ subscription : NSString){
        veery.unregisterNotification(subscription as String)
    }
    // MARK: - APNs
    
    @objc func setVeeryToken(_ token : NSString){
        let tokend = Data(hexString : token as String)
      
        
        veery.setAPNSToken(token: tokend! )
        
    }
    @objc func VeeryNotificationHandler(_ data : NSDictionary,_ callback : RCTResponseSenderBlock){
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
    
    // MARK: - Get Status
    @objc (getStatus:)
    func getStatus(callback : RCTResponseSenderBlock) -> Void{
        callback([veery.getStatus()])
    }
    
    // MARK: - System Authorization
    @objc func systemAuthorization(_ autho : NSInteger, _ callback : RCTResponseSenderBlock)-> Void{
        callback([veery.systemAuthorization(autho)])
    }
    // MARK: - User Agreement
    @objc (userAgreement:)
    func userAgreement(callback : RCTResponseSenderBlock)-> Void{
        callback([veery.userAgreement()])
    }
    // MARK: - User Agreement Age
    @objc (userAgreementAge:)
    func userAgreementAge(callback : RCTResponseSenderBlock)-> Void{
        callback([veery.userAgreementAge()])
    }
    // MARK: - User Agreed Purpose
    @objc func userAgreedPurpose(_ PurposeVersion : NSInteger, _ PurposeText : NSString, _ ApprovalButtonText : NSString, _ RejectionButtonText : NSString, _ Agreed : Bool)-> Void{
        veery.userAgreedPurpose(PurposeVersion: PurposeVersion, PurposeText: PurposeText as String, ApprovalButtonText: ApprovalButtonText as String, RejectionButtonText: RejectionButtonText as String, Agreed: Agreed)
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
        self.bridge.eventDispatcher().sendAppEvent(withName: "veeryLocationUpdate", body: location)
    }
    // MARK: - veeryRouteMatch
    func veeryRouteMatch(_ veery: Veery, locationsHistory: Veery.LocationsHistory) {
        self.bridge.eventDispatcher().sendAppEvent(withName: "veeryRouteMatch", body: veeryLocationsToNSDictionary(locations: locationsHistory, format: Veery.HISTORY_ROUTEMATCH))
    
    }
    
    // MARK: - veeryPoiUpdate
    func veeryPoiUpdate(_ veery: Veery, poi: Veery.Pois) {
        self.bridge.eventDispatcher().sendAppEvent(withName: "veeryPoisUpdate", body: poisToNSDictionary(pois: poi))
        
    }
    
    
    func veeryPredictionUpdate(_ veery: Veery, Predictions: Veery.Predictions) {
        self.bridge.eventDispatcher().sendAppEvent(withName: "veeryPredictionUpdate", body: predictionToNSDictionary(predictions: Predictions))
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
        if let geojs = predictions.toGeoJson(){
            do {
                let data = try JSONSerialization.data(withJSONObject: geojs, options: [])
                _prediction["Trip"] = String(data: data, encoding: .utf8)!
                _prediction["toGeoJSON"] = String(data: data, encoding: .utf8)!
                
            } catch let error as NSError {
                NSLog("Failed to load GEOJSON: \(error.localizedDescription)")
            }
        }
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
    @objc func activateWithOptin(_ activate : Int,_ version : Int ,_ optinView : NSDictionary,_ optinImage : NSDictionary,_ optinText : NSDictionary,_ optinButtonOK : NSDictionary,_ optinButtonNO : NSDictionary,_ NbProposal : Int, _ proposalCycle : Int ){
        
        let view = Veery.OptinView()
        view.height = optinView["height"] as! Int
        view.width = optinView["width"] as! Int
        view.x = optinView["X"] as! Float
        view.y = optinView["Y"] as! Float
        view.backgroundColor = optinView["backgroundColor"] as! String
        view.cornerRadius = optinView["cornerRadius"] as! Int
        
        let image = Veery.OptinImage()
        image.height = optinImage["height"] as! Int
        image.width = optinImage["width"] as! Int
        image.x = optinImage["X"] as! Float
        image.y = optinImage["Y"] as! Float
        image.name = optinImage["name"] as! String
        image.bottomMargin =  optinImage["bottomMargin"] as! Int
        image.topMargin =  optinImage["topMargin"] as! Int
        image.leftMargin =  optinImage["leftMargin"] as! Int
        image.rightMargin =  optinImage["rightMargin"] as! Int
        
        let text = Veery.OptinText()
        text.height = optinText["height"] as! Int
        text.width = optinText["width"] as! Int
        text.x =  optinText["X"] as! Float
        text.y = optinText["Y"] as! Float
        text.message = optinText["message"] as! String
        text.bottomMargin =  optinText["bottomMargin"] as! Int
        text.topMargin =  optinText["topMargin"] as! Int
        text.leftMargin =  optinText["leftMargin"] as! Int
        text.rightMargin =  optinText["rightMargin"] as! Int
        
        
        let btnOK = Veery.OptinButton()
        btnOK.height = optinButtonOK["height"] as! Int
        btnOK.width = optinButtonOK["width"] as! Int
        btnOK.text = optinButtonOK["text"] as! String
        btnOK.color = optinButtonOK["color"] as! String
        btnOK.textColor = optinButtonOK["textColor"] as! String
        btnOK.bottomMargin =  optinButtonOK["bottomMargin"] as! Int
        btnOK.topMargin =  optinButtonOK["topMargin"] as! Int
        btnOK.leftMargin =  optinButtonOK["leftMargin"] as! Int
        btnOK.rightMargin =  optinButtonOK["rightMargin"] as! Int
        btnOK.cornerRadius =  optinButtonOK["cornerRadius"] as! Int
        
        let btnNO = Veery.OptinButton()
        btnNO.height = optinButtonNO["height"] as! Int
        btnNO.width = optinButtonNO["width"] as! Int
        btnNO.text = optinButtonNO["text"] as! String
        btnNO.color = optinButtonNO["color"] as! String
        btnNO.textColor = optinButtonNO["textColor"] as! String
        btnNO.bottomMargin =  optinButtonNO["bottomMargin"] as! Int
        btnNO.topMargin =  optinButtonNO["topMargin"] as! Int
        btnNO.leftMargin =  optinButtonNO["leftMargin"] as! Int
        btnNO.rightMargin =  optinButtonNO["rightMargin"] as! Int
        btnNO.cornerRadius =  optinButtonNO["cornerRadius"] as! Int
        
        veery.activateWithOptin(activate: activate, version: version, optinView: view, optinImage: image, optinText: text, optinButtonOK: btnOK, optinButtonNO: btnNO,NbProposal: NbProposal,proposalCycle: proposalCycle)
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
