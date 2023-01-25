import UIKit
import Flutter
import Firebase
import flutter_downloader

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
  FirebaseApp.configure()
  GeneratedPluginRegistrant.register(with:self)
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
}

FlutterDownloaderPlugin.setPluginRegistrantCallback(registerPlugins)
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }

   override func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {

    if (url.absoluteString.contains("thirdPartyLoginResult")) {
        return NaverThirdPartyLoginConnection.getSharedInstance().application(app, open: url, options: options)
    }
    return super.application(app, open: url, options: options)

  }
}