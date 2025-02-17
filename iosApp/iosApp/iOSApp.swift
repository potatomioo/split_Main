import SwiftUI
import Firebase // Add this line
@main
struct iOSApp: App {
    // Add this init with the FirebaseApp.configure()
    init(){
        FirebaseApp.configure()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
