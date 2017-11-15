
# react-native-veery

## Getting started

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

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
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


## Usage
```javascript
import RNVeery from 'react-native-veery';

// TODO: What to do with the module?
RNVeery;
```
  