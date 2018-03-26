
//#import "RNVeery.h"
//
//@implementation RNVeery
//
//- (dispatch_queue_t)methodQueue
//{
//    return dispatch_get_main_queue();
//}
//RCT_EXPORT_MODULE()
//
//@end

//  Created by react-native-create-bridge

// import RCTViewManager
#if __has_include(<React/RCTViewManager.h>)
#import <React/RCTViewManager.h>
#elif __has_include("RCTViewManager.h")
#import "RCTViewManager.h"
#else
//#import "React/RCTViewManager.h" // Required when used as a Pod in a Swift project
#endif

// import RCTEventDispatcher
#if __has_include(<React/RCTEventDispatcher.h>)
#import <React/RCTEventDispatcher.h>
#elif __has_include("RCTEventDispatcher.h")
#import "RCTEventDispatcher.h"
#else
//#import "React/RCTEventDispatcher.h" // Required when used as a Pod in a Swift project
#endif
#

// Export a native module
// https://facebook.github.io/react-native/docs/native-modules-ios.html#exporting-swift
@interface RCT_EXTERN_MODULE(RNVeery , RCTViewManager)


// Map native properties to React Component props
// https://facebook.github.io/react-native/docs/native-components-ios.html#properties
//RCT_EXPORT_VIEW_PROPERTY("exampleProp", NSString)

// Export methods to a native module
// https://facebook.github.io/react-native/docs/native-modules-ios.html#exporting-swift
//RCT_EXTERN_METHOD(exampleMethod)
RCT_EXTERN_METHOD(serviceConnect)
RCT_EXTERN_METHOD(activate : (NSInteger *)level)
RCT_EXTERN_METHOD(setApiKeySecret : (NSString *)apikey)

RCT_EXTERN_METHOD(getCurrentLocation : (RCTResponseSenderBlock *)callback)
RCT_EXTERN_METHOD(requestLocationUpdate)

RCT_EXTERN_METHOD(getLocationHistory:(NSInteger *)format:(NSDate *)since:(NSDate *)until:(RCTResponseSenderBlock)callback)
RCT_EXTERN_METHOD(requestRouteMatch)
RCT_EXTERN_METHOD(stopRouteMatch)

RCT_EXTERN_METHOD(getPois : (RCTResponseSenderBlock *)callback)
RCT_EXTERN_METHOD(requestPoiUpdate)
RCT_EXTERN_METHOD(stopPoisUpdate)

RCT_EXTERN_METHOD(getNextTrip : (RCTResponseSenderBlock *)callback)
RCT_EXTERN_METHOD(requestPredictionUpdate)
RCT_EXTERN_METHOD(stopPredictionUpdate)

RCT_EXTERN_METHOD(registerNotification:(NSString *)subscription:(NSString *) format)
RCT_EXTERN_METHOD(unregisterNotification:(NSString *) subscription)

RCT_EXTERN_METHOD(setTags: (NSString *)tagName : (NSString *)value)
RCT_EXTERN_METHOD(getTags:(NSString *)tagName : (RCTResponseSenderBlock)callback )
RCT_EXTERN_METHOD(unsetTags: (NSString *)tagName )

RCT_EXTERN_METHOD(resetLocalHistory)
RCT_EXTERN_METHOD(resetBackendHistory)
RCT_EXTERN_METHOD(resetGeoProfileHistory)

RCT_EXTERN_METHOD(setVeeryToken:(NSString *)token)
RCT_EXTERN_METHOD(VeeryNotificationHandler:(NSDictionary *)data : (RCTResponseSenderBlock)callback)

RCT_EXTERN_METHOD(getStatus: (RCTResponseSenderBlock *)callback)

RCT_EXTERN_METHOD(systemAuthorization: (NSInteger *)autho : (RCTResponseSenderBlock)callback)
RCT_EXTERN_METHOD(userAgreement: (RCTResponseSenderBlock *)callback)
RCT_EXTERN_METHOD(userAgreementAge: (RCTResponseSenderBlock *)callback)
RCT_EXTERN_METHOD(userAgreedPurpose: (NSInteger *)PurposeVersion :(NSString *)PurposeText :(NSString *)ApprovalButtonText : (NSString *)RejectionButtonText : (BOOL *)Agreed )

@end
