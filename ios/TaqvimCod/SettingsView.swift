import SwiftUI

struct SettingsView: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.colorScheme) private var scheme
    @AppStorage("lang") var lang = "fa"
    @AppStorage("theme") var theme = 0
    @AppStorage("hijri_adj") var hijriAdj = -1

    private var c: (bg: Color, surface: Color, surfaceAlt: Color, text: Color, textSub: Color,
                    accent: Color, accentSoft: Color, gradStart: Color, gradEnd: Color,
                    red: Color, redSoft: Color, amber: Color, green: Color) { Pal.colors(scheme) }

    var body: some View {
        NavigationStack {
            Form {
                Section(L.t("theme")) {
                    Picker(L.t("theme"), selection: $theme) {
                        Text(L.t("theme_auto")).tag(0)
                        Text(L.t("theme_light")).tag(1)
                        Text(L.t("theme_dark")).tag(2)
                    }
                    .pickerStyle(.segmented)
                }
                Section(L.t("language")) {
                    Picker(L.t("language"), selection: $lang) {
                        Text(L.t("persian")).tag("fa")
                        Text(L.t("english")).tag("en")
                    }
                    .pickerStyle(.segmented)
                }
                Section(L.t("hijri_adjust")) {
                    Picker(L.t("hijri_adjust"), selection: $hijriAdj) {
                        Text("-۲").tag(-2)
                        Text("-۱ (پیشنهادی ایران)").tag(-1)
                        Text("۰").tag(0)
                        Text("+۱").tag(1)
                        Text("+۲").tag(2)
                    }
                    Text(L.t("hijri_note"))
                        .font(.custom(FontNames.regular, size: 11))
                        .foregroundColor(c.textSub)
                }
                Section(L.t("privacy_title")) {
                    Text(L.t("privacy_text"))
                        .font(.custom(FontNames.regular, size: 12.5))
                }
            }
            .navigationTitle(L.t("settings"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .confirmationAction) {
                    Button(L.t("ok")) { dismiss() }
                }
            }
        }
    }
}

struct AboutView: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.colorScheme) private var scheme

    private var c: (bg: Color, surface: Color, surfaceAlt: Color, text: Color, textSub: Color,
                    accent: Color, accentSoft: Color, gradStart: Color, gradEnd: Color,
                    red: Color, redSoft: Color, amber: Color, green: Color) { Pal.colors(scheme) }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 14) {
                    Text(L.t("app_name"))
                        .font(.custom(FontNames.bold, size: 26))
                        .foregroundColor(c.accent)
                    Text("Version 1.1.1 (iOS)")
                        .font(.custom(FontNames.regular, size: 12))
                        .foregroundColor(c.textSub)
                    Text(L.t("desc_line"))
                        .font(.custom(FontNames.regular, size: 13.5))
                        .foregroundColor(c.text)
                    Text("🔒 " + L.t("privacy_text"))
                        .font(.custom(FontNames.regular, size: 11.5))
                        .foregroundColor(c.textSub)

                    Divider()
                    HStack(spacing: 4) {
                        Text(L.t("developer") + ":")
                            .foregroundColor(c.textSub)
                        Text("Matin")
                            .font(.custom(FontNames.semibold, size: 13))
                            .foregroundColor(c.text)
                    }

                    Text(L.t("contact"))
                        .font(.custom(FontNames.bold, size: 14))
                    VStack(alignment: .leading, spacing: 8) {
                        link("https://matinzeus.ir")
                        link("https://matin-wp-pro.ir")
                        link("https://dyno-tuning-znj.ir")
                        link("https://elambar-zanjan.ir")
                        Text("\(L.t("telegram")): t.me/matin3620")
                            .font(.custom(FontNames.regular, size: 13))
                            .foregroundColor(c.accent)
                        Text("\(L.t("instagram")): instagram.com/matin_web_site")
                            .font(.custom(FontNames.regular, size: 13))
                            .foregroundColor(c.accent)
                    }
                }
                .padding(20)
                .frame(maxWidth: .infinity, alignment: .leading)
            }
            .background(c.bg)
            .navigationTitle(L.t("about"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .confirmationAction) {
                    Button(L.t("ok")) { dismiss() }
                }
            }
        }
    }

    private func link(_ url: String) -> some View {
        if let u = URL(string: url) {
            return AnyView(Link(destination: u) {
                    Text(url)
                        .font(.custom(FontNames.regular, size: 13))
                        .foregroundColor(c.accent)
                }
                .font(.custom(FontNames.regular, size: 13))
                .foregroundColor(c.accent))
        }
        return AnyView(EmptyView())
    }
}
