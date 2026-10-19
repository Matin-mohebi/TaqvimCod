import SwiftUI
import EventKit

/// Add / edit an event. DatePicker runs on the Persian calendar.
struct EventEditorView: View {
    @EnvironmentObject var store: EventStore
    @Environment(\.dismiss) private var dismiss
    @Environment(\.colorScheme) private var scheme
    @AppStorage("lang") var lang = "fa"

    let editing: EKEvent?
    let day: JDate

    @State private var title = ""
    @State private var note = ""
    @State private var allDay = false
    @State private var start = Date()
    @State private var end = Date().addingTimeInterval(3600)
    @State private var calendarId = ""
    @State private var showError = false

    private var c: (bg: Color, surface: Color, surfaceAlt: Color, text: Color, textSub: Color,
                    accent: Color, accentSoft: Color, gradStart: Color, gradEnd: Color,
                    red: Color, redSoft: Color, amber: Color, green: Color) { Pal.colors(scheme) }

    private var persianCal: Calendar { Calendar(identifier: .persian) }
    private var writable: [EKCalendar] { store.writableCalendars() }

    var body: some View {
        NavigationStack {
            Form {
                Section(L.t("title")) {
                    TextField(L.t("hint_title"), text: $title)
                }
                Section(L.t("date_label")) {
                    Text(L.fmtJDate(day))
                        .font(.custom(FontNames.semibold, size: 15))
                        .foregroundColor(c.text)
                }
                Section {
                    Toggle(L.t("all_day"), isOn: $allDay)
                    if !allDay {
                        DatePicker(L.t("start_time"), selection: $start,
                                   displayedComponents: .hourAndMinute)
                            .environment(\.calendar, persianCal)
                        DatePicker(L.t("end_time"), selection: $end, in: start...(start + 86_400),
                                   displayedComponents: .hourAndMinute)
                            .environment(\.calendar, persianCal)
                    }
                }
                Section(L.t("calendar_target")) {
                    Picker(L.t("calendar_target"), selection: $calendarId) {
                        ForEach(writable, id: \.calendarIdentifier) { cal in
                            Text(cal.title).tag(cal.calendarIdentifier)
                        }
                    }
                }
                Section(L.t("note")) {
                    TextField("", text: $note, axis: .vertical)
                        .lineLimit(1...4)
                }
                if editing != nil {
                    Section {
                        Button(role: .destructive) {
                            if let ev = editing {
                                _ = store.delete(ev)
                                dismiss()
                            }
                        } label: {
                            Label(L.t("delete"), systemImage: "trash")
                        }
                    }
                }
            }
            .navigationTitle(editing == nil ? L.t("add_event") : L.t("edit_event"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button(L.t("cancel")) { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button(L.t("save")) { save() }
                        .font(.custom(FontNames.semibold, size: 15))
                }
            }
            .alert(L.t("empty_title_required"), isPresented: $showError) {
                Button(L.t("ok"), role: .cancel) {}
            }
        }
        .environment(\.layoutDirection, lang == "en" ? .leftToRight : .rightToLeft)
        .onAppear { load() }
    }

    private func load() {
        if let ev = editing {
            title = ev.title ?? ""
            note = ev.notes ?? ""
            allDay = ev.isAllDay
            start = ev.startDate
            end = ev.endDate
            calendarId = ev.calendar?.calendarIdentifier ?? ""
        } else {
            if let first = writable.first { calendarId = first.calendarIdentifier }
        }
    }

    private func save() {
        let t = title.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !t.isEmpty else { showError = true; return }
        let dayStart = DateEngine.startOfDay(jdn: DateEngine.j2d(jy: day.y, jm: day.m, jd: day.d))
        var s = start, e = end
        if allDay {
            s = dayStart
            e = dayStart.addingTimeInterval(86_400)
        } else {
            let cal = Calendar.current
            let so = cal.dateComponents([.hour, .minute], from: start)
            let eo = cal.dateComponents([.hour, .minute], from: end)
            s = cal.date(bySettingHour: so.hour ?? 9, minute: so.minute ?? 0, second: 0,
                         of: dayStart) ?? dayStart
            e = cal.date(bySettingHour: eo.hour ?? 10, minute: eo.minute ?? 0, second: 0,
                         of: dayStart) ?? dayStart.addingTimeInterval(3600)
            guard e > s else { showError = true; return }
        }
        let target = writable.first { $0.calendarIdentifier == calendarId }
        let ok: Bool
        if let ev = editing {
            ok = store.update(ev, title: t, note: note.isEmpty ? nil : note,
                              start: s, end: e, allDay: allDay)
        } else {
            ok = store.save(title: t, note: note.isEmpty ? nil : note,
                            start: s, end: e, allDay: allDay, calendar: target)
        }
        if ok { dismiss() } else { showError = true }
    }
}
