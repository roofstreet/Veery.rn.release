
# react-native-veery

## Getting started

`$ npm install`

`$ npm install react-native-veery --save`

### Mostly automatic installation

`$ react-native link react-native-veery`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-veery` and add `RNVeery.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNVeery.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.reactlibrary.RNVeeryPackage;` to the imports at the top of the file
  - Add `new RNVeeryPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-veery'
  	project(':react-native-veery').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-veery/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-veery')
  	```

## import veery Pod
in your Podfile add the following :

1. At the top
`source 'https://github.com/roofstreet/cocoa.repo.git'`
2. in Target

        target 'yourAppName' do
        # Pods for VeeryDemoReactNative
        # TO DO : Add this line
        pod 'RNVeery', :podspec => '../node_modules/react-native-veery/ios'
        
        end
## Usage
```javascript
import Veery from 'react-native-veery';

// TODO: What to do with the module?
Veery;
```
  
