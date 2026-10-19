import SwiftUI
import EventKit

/// Main screen: gradient header + month grid + live day events + FAB.
struct HomeView: View {
    @EnvironmentObject var store: EventStore
    @Environment(\.colorScheme) private var systemScheme
    @AppStorage("lang") var lang = "fa"
    @AppStorage("theme") var theme = 0
    @AppStorage("hijri_adj") var hijriAdj = -1

    @State private var curJy = DateEngine.todayJ.y
    @State private var curJm = DateEngine.todayJ.m
    @State private var selJy = DateEngine.todayJ.y
    @State private var selJm = DateEngine.todayJ.m
    @State private var selJd = DateEngine.todayJ.d
    @State private var today = DateEngine.todayJ
    @State private var eventDays: Set<Int> = []
    @State private var dayEvents: [EKEvent] = []
    @State private var showEditor = false
    @State private var editingEvent: EKEvent?
    @State private var showSettings = false
    @State private var showAbout = false
    @State private var refreshTick = 0

    private var scheme: ColorScheme {
        switch theme {
        case 1: return .light
        case 2: return .dark
        default: return systemScheme
        }
    }
    private var c: (bg: Color, surface: Color, surfaceAlt: Color, text: Color, textSub: Color,
                    accent: Color, accentSoft: Color, gradStart: Color, gradEnd: Color,
                    red: Color, redSoft: Color, amber: Color, green: Color) { Pal.colors(scheme) }

    private var selectedJDate: JDate { JDate(y: selJy, m: selJm, d: selJd) }

    var body: some View {
        ZStack {
            c.bg.ignoresSafeArea()

            VStack(spacing: 0) {
                topBar
                headerCard
                ScrollView {
                    VStack(spacing: 8) {
                        MonthGridView(jy: curJy, jm: curJm, selectedJd: selJd,
                                      today: today, eventDays: eventDays, scheme: scheme,
                                      onTapDay: { jd in selectDay(jd) },
                                      onSwipe: { next in changeMonth(next: next) })
                            .padding(.horizontal, 6)
                            .id(refreshTick)
                        syncRow
                        eventsSection
                    }
                    .padding(.bottom, 90)
                }
            }

            // FAB
            VStack {
                Spacer()
                HStack {
                    Button { openEditor(nil) } label: {
                        Image(systemName: "plus")
                            .font(.system(size: 22, weight: .bold))
                            .foregroundColor(.white)
                            .frame(width: 56, height: 56)
                            .background(LinearGradient(colors: [c.gradStart, c.gradEnd],
                                                       startPoint: .topLeading, endPoint: .bottomTrailing))
                            .clipShape(RoundedRectangle(cornerRadius: 18))
                            .shadow(color: .black.opacity(0.25), radius: 6, y: 3)
                    }
                    .padding(.leading, 20)
                    Spacer()
                }
            }
        }
        .sheet(isPresented: $showEditor) {
            EventEditorView(editing: editingEvent, day: selectedJDate)
                .environment(\.layoutDirection, lang == "en" ? .leftToRight : .rightToLeft)
        }
        .sheet(isPresented: $showSettings) {
            SettingsView()
                .environment(\.layoutDirection, lang == "en" ? .leftToRight : .rightToLeft)
        }
        .sheet(isPresented: $showAbout) {
            AboutView()
                .environment(\.layoutDirection, lang == "en" ? .leftToRight : .rightToLeft)
        }
        .onAppear { loadMonth(); loadDay() }
        .onReceive(NotificationCenter.default.publisher(
            for: NSNotification.Name("NSCalendarDayChanged"))) { _ in reloadAll() }
        .onReceive(store.$authorized) { ok in if ok { reloadAll() } }
    }

    // MARK: top bar

    private var topBar: some View {
        HStack(spacing: 11) {
            Text("ت")
                .font(.custom(FontNames.bold, size: 20))
                .foregroundColor(.white)
                .frame(width: 40, height: 40)
                .background(LinearGradient(colors: [c.gradStart, c.gradEnd],
                                           startPoint: .topLeading, endPoint: .bottomTrailing))
                .clipShape(RoundedRectangle(cornerRadius: 13))
            VStack(alignment: .leading, spacing: 1) {
                Text(L.t("app_name"))
                    .font(.custom(FontNames.semibold, size: 18))
                    .foregroundColor(.white)
                Text(L.t("subtitle"))
                    .font(.custom(FontNames.regular, size: 10.5))
                    .foregroundColor(.white.opacity(0.72))
            }
            Spacer()
            Button { goToday() } label: {
                Text(L.t("today"))
                    .font(.custom(FontNames.medium, size: 12.5))
                    .foregroundColor(.white)
                    .padding(.horizontal, 13).padding(.vertical, 7)
                    .background(Color.white.opacity(0.16))
                    .clipShape(Capsule())
            }
            Menu {
                Button(L.t("settings")) { showSettings = true }
                Button(L.t("about")) { showAbout = true }
            } label: {
                Image(systemName: "gearshape")
                    .foregroundColor(.white)
                    .padding(9)
                    .background(Color.white.opacity(0.16))
                    .clipShape(Capsule())
            }
        }
        .padding(.horizontal, 16)
        .padding(.top, 8)
        .padding(.bottom, 14)
        .background(LinearGradient(colors: [c.gradStart, c.gradEnd],
                                   startPoint: .topLeading, endPoint: .bottomTrailing))
        .ignoresSafeArea(edges: .top)
    }

    // MARK: header card

    private var headerCard: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack {
                Button { changeMonth(next: false) } label: {
                    Image(systemName: "chevron.right")
                        .font(.system(size: 17, weight: .bold))
                        .foregroundColor(c.accent)
                        .padding(8)
                }
                Spacer()
                Text(L.fmtMonthYear(curJy, curJm))
                    .font(.custom(FontNames.semibold, size: 20))
                    .foregroundColor(c.text)
                Spacer()
                Button { changeMonth(next: true) } label: {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 17, weight: .bold))
                        .foregroundColor(c.accent)
                        .padding(8)
                }
            }
            .padding(.bottom, 6)

            Text(L.fmtJDate(selectedJDate))
                .font(.custom(FontNames.semibold, size: 16.5))
                .foregroundColor(c.text)
            let jdn = DateEngine.j2d(jy: selJy, jm: selJm, jd: selJd)
            Text("\(L.fmtGDate(DateEngine.d2g(jdn)))  •  \(L.fmtHDate(DateEngine.hijriFromJdn(jdn, adjust: hijriAdj)))")
                .font(.custom(FontNames.regular, size: 12))
                .foregroundColor(c.textSub)
                .padding(.top, 2)

            if !Holidays.forDay(jm: selJm, jd: selJd,
                                hm: DateEngine.hijriFromJdn(jdn, adjust: hijriAdj).m,
                                hd: DateEngine.hijriFromJdn(jdn, adjust: hijriAdj).d).isEmpty {
                let occs = Holidays.forDay(jm: selJm, jd: selJd,
                                           hm: DateEngine.hijriFromJdn(jdn, adjust: hijriAdj).m,
                                           hd: DateEngine.hijriFromJdn(jdn, adjust: hijriAdj).d)
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 6) {
                        ForEach(Array(occs.enumerated()), id: \.offset) { _, occ in
                            Text((occ.holiday ? "◼ " : "◇ ") + (L.isFa ? occ.fa : occ.en))
                                .font(.custom(FontNames.medium, size: 11.5))
                                .foregroundColor(occ.holiday ? c.red : c.textSub)
                                .padding(.horizontal, 11).padding(.vertical, 5)
                                .background(occ.holiday ? c.redSoft : c.surfaceAlt)
                                .clipShape(Capsule())
                        }
                    }
                    .padding(.top, 8)
                }
            }

            countdownCard.padding(.top, 10)
        }
        .padding(16)
        .background(c.surface)
        .clipShape(RoundedRectangle(cornerRadius: 22))
        .shadow(color: .black.opacity(scheme == .dark ? 0.3 : 0.06), radius: 8, y: 3)
        .padding(.horizontal, 12)
        .padding(.top, 10)
    }

    private var countdownCard: some View {
        HStack(spacing: 11) {
            if let nxt = Holidays.nextHoliday(from: today, hijriAdj: hijriAdj, maxDays: 366) {
                let (days, occ) = nxt
                Text(L.d(days))
                    .font(.custom(FontNames.bold, size: 15))
                    .foregroundColor(.white)
                    .frame(width: 42, height: 42)
                    .background(LinearGradient(colors: [c.gradStart, c.gradEnd],
                                               startPoint: .topLeading, endPoint: .bottomTrailing))
                    .clipShape(Circle())
                VStack(alignment: .leading, spacing: 1) {
                    Text(days == 0 ? L.t("today_holiday")
                            : "\(L.t("next_holiday")) • \(L.d(days)) \(L.t("days"))")
                        .font(.custom(FontNames.medium, size: 10))
                        .foregroundColor(c.textSub)
                    Text(L.isFa ? occ.fa : occ.en)
                        .font(.custom(FontNames.semibold, size: 13))
                        .foregroundColor(days == 0 ? c.red : c.text)
                        .lineLimit(1)
                }
                Spacer()
            } else {
                Text(L.t("no_holiday_soon"))
                    .font(.custom(FontNames.medium, size: 12))
                    .foregroundColor(c.textSub)
                Spacer()
            }
        }
        .padding(10)
        .background(c.surfaceAlt)
        .clipShape(RoundedRectangle(cornerRadius: 18))
    }

    // MARK: sync row

    private var syncRow: some View {
        HStack {
            Text(store.authorized
                 ? L.t("events_count").replacingOccurrences(of: "%@", with: L.d(dayEvents.count))
                 : L.t("events"))
                .font(.custom(FontNames.semibold, size: 15))
                .foregroundColor(c.text)
            Spacer()
            if store.authorized {
                HStack(spacing: 4) {
                    Circle().fill(c.green).frame(width: 6, height: 6)
                    Text(L.isFa ? "همگام‌سازی زنده" : "Live sync")
                        .font(.custom(FontNames.medium, size: 10))
                        .foregroundColor(c.green)
                }
                .padding(.horizontal, 10).padding(.vertical, 4)
                .background(c.surfaceAlt)
                .clipShape(Capsule())
            }
        }
        .padding(.horizontal, 18)
        .padding(.top, 8)
    }

    // MARK: events

    private var eventsSection: some View {
        VStack(spacing: 8) {
            if !store.authorized {
                permissionBanner
            } else if dayEvents.isEmpty {
                VStack(spacing: 4) {
                    Text(L.t("no_events"))
                        .font(.custom(FontNames.medium, size: 13.5))
                        .foregroundColor(c.textSub)
                    Text(L.t("no_events_hint"))
                        .font(.custom(FontNames.regular, size: 11.5))
                        .foregroundColor(c.textSub)
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 28)
                .background(c.surface)
                .clipShape(RoundedRectangle(cornerRadius: 18))
            } else {
                ForEach(dayEvents, id: \.eventIdentifier) { ev in
                    eventCard(ev)
                }
            }
        }
        .padding(.horizontal, 12)
    }

    private var permissionBanner: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(L.t("perm_title"))
                .font(.custom(FontNames.bold, size: 14))
                .foregroundColor(c.red)
            Text(L.t("perm_text"))
                .font(.custom(FontNames.regular, size: 12))
                .foregroundColor(c.textSub)
            Button { EventStore.shared.requestAccessAsync() } label: {
                Text(L.t("grant"))
                    .font(.custom(FontNames.medium, size: 12.5))
                    .foregroundColor(.white)
                    .padding(.horizontal, 14).padding(.vertical, 8)
                    .background(c.accent)
                    .clipShape(Capsule())
            }
            .padding(.top, 8)
        }
        .padding(16)
        .background(c.redSoft)
        .clipShape(RoundedRectangle(cornerRadius: 18))
    }

    private func eventCard(_ ev: EKEvent) -> some View {
        HStack(spacing: 11) {
            RoundedRectangle(cornerRadius: 2.5)
                .fill(Color(cgColor: ev.calendar.cgColor))
                .frame(width: 4, height: 46)
            VStack(alignment: .leading, spacing: 2) {
                Text(ev.title ?? L.t("empty_title"))
                    .font(.custom(FontNames.semibold, size: 14))
                    .foregroundColor(c.text)
                Text(ev.isAllDay ? L.t("all_day") : L.fmtTime(ev.startDate))
                    .font(.custom(FontNames.medium, size: 11))
                    .foregroundColor(c.accent)
                if let loc = ev.location, !loc.isEmpty {
                    Text(loc)
                        .font(.custom(FontNames.regular, size: 10.5))
                        .foregroundColor(c.textSub)
                        .lineLimit(1)
                }
            }
            Spacer()
            if ev.calendar.allowsContentModifications {
                VStack(spacing: 5) {
                    Button { openEditor(ev) } label: {
                        Image(systemName: "pencil")
                            .font(.system(size: 12))
                            .foregroundColor(c.accent)
                            .padding(7)
                            .background(c.accentSoft)
                            .clipShape(RoundedRectangle(cornerRadius: 10))
                    }
                    Button {
                        _ = store.delete(ev)
                        reloadAll()
                    } label: {
                        Image(systemName: "trash")
                            .font(.system(size: 12))
                            .foregroundColor(c.red)
                            .padding(7)
                            .background(c.redSoft)
                            .clipShape(RoundedRectangle(cornerRadius: 10))
                    }
                }
            }
        }
        .padding(11)
        .background(c.surface)
        .clipShape(RoundedRectangle(cornerRadius: 17))
        .shadow(color: .black.opacity(scheme == .dark ? 0.25 : 0.05), radius: 4, y: 2)
    }

    // MARK: actions / data

    private func goToday() {
        today = DateEngine.todayJ
        curJy = today.y; curJm = today.m
        selJy = today.y; selJm = today.m; selJd = today.d
        reloadAll()
    }

    private func changeMonth(next: Bool) {
        var m = next ? curJm + 1 : curJm - 1
        var y = curJy
        if m > 12 { m = 1; y += 1 }
        if m < 1 { m = 12; y -= 1 }
        curJy = y; curJm = m
        selJy = y; selJm = m
        let len = DateEngine.jalaaliMonthLength(y, m)
        if selJd > len { selJd = len }
        loadMonth()
    }

    private func selectDay(_ jd: Int) {
        selJd = jd; selJy = curJy; selJm = curJm
        loadDay()
    }

    private func openEditor(_ ev: EKEvent?) {
        editingEvent = ev
        showEditor = true
    }

    private func reloadAll() { loadMonth(); loadDay() }

    private func loadMonth() {
        today = DateEngine.todayJ
        guard store.authorized else { eventDays = []; return }
        let len = DateEngine.jalaaliMonthLength(curJy, curJm)
        let from = DateEngine.startOfDay(jdn: DateEngine.j2d(jy: curJy, jm: curJm, jd: 1))
        let to = DateEngine.startOfDay(jdn: DateEngine.j2d(jy: curJy, jm: curJm, jd: len)) + 86_400
        var days: Set<Int> = []
        for ev in store.events(from: from, to: to) {
            var cur = DateEngine.jdn(ofDate: ev.startDate)
            let end = DateEngine.jdn(ofDate: ev.endDate.addingTimeInterval(-1))
            while cur <= end {
                let j = DateEngine.d2j(cur)
                if j.y == curJy && j.m == curJm { days.insert(j.d) }
                cur += 1
                if days.count > 62 { break }
            }
        }
        eventDays = days
    }

    private func loadDay() {
        guard store.authorized else { dayEvents = []; return }
        let jdn = DateEngine.j2d(jy: selJy, jm: selJm, jd: selJd)
        let from = DateEngine.startOfDay(jdn: jdn)
        let to = from + 86_400
        dayEvents = store.events(from: from, to: to)
    }
}
