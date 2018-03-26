
  require 'json'
version = JSON.parse(File.read('../package.json'))["version"]

Pod::Spec.new do |s|

  s.name            = "RNVeery"
  s.version         = version
  s.homepage        = "https://github.com/roofstreet/Veery.rn.release"
  s.summary         = "A Veery bridge for react-native"
  s.license         = "MIT"
  s.author          = { "Malek Hassani" => "malek@roofstreet.io" }
  s.ios.deployment_target = '8.0'
  s.source          = { :git => "https://github.com/roofstreet/Veery.rn.release.git", :tag => "master" }
  s.source_files    = 'RNVeery/**/*.{h,m}'
  s.preserve_paths  = "**/*.js"

  s.dependency 'Veery', '1.3.0'

end