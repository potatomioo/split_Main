import SwiftUI
import Firebase
@main
struct iOSApp: App {
    // Add this init with the FirebaseApp.configure()
    init(){
        FirebaseApp.configure()

        // Configure Google SignIn
    
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
