import SwiftUI

@main
struct TaqvimCodApp: App {
    @AppStorage("lang") var lang = "fa"
    @AppStorage("theme") var theme = 0          // 0 auto / 1 light / 2 dark
    @StateObject private var eventStore = EventStore.shared

    var preferredScheme: ColorScheme? {
        switch theme {
        case 1: return .light
        case 2: return .dark
        default: return nil
        }
    }

    var body: some Scene {
        WindowGroup {
            HomeView()
                .environmentObject(eventStore)
                .environment(\.layoutDirection, lang == "en" ? .leftToRight : .rightToLeft)
                .preferredColorScheme(preferredScheme)
                .onAppear {
                    UserDefaults.standard.register(defaults: [
                        "lang": "fa", "theme": 0, "hijri_adj": -1
                    ])
                    EventStore.shared.requestAccessAsync()
                }
        }
    }
}
