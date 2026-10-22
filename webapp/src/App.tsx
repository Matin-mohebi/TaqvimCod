import { useCallback, useEffect, useMemo, useState } from 'react';
import { todayJdn } from './lib/dates';
import { useLocal } from './lib/useLocal';
import CalendarView from './components/CalendarView';
import ToolsView from './components/ToolsView';
import PrayerView from './components/PrayerView';
import AboutView from './components/AboutView';
import { IconCalendar, IconInfo, IconMoon, IconMosque, IconSun, IconTools } from './components/icons';

export type Tab = 'calendar' | 'tools' | 'prayer' | 'about';

const TABS: { id: Tab; label: string; icon: (p: { style?: React.CSSProperties }) => React.ReactNode }[] = [
  { id: 'calendar', label: 'تقویم', icon: IconCalendar },
  { id: 'tools', label: 'ابزارها', icon: IconTools },
  { id: 'prayer', label: 'اوقات شرعی', icon: IconMosque },
  { id: 'about', label: 'درباره', icon: IconInfo },
];

function ThemeIcon({ theme }: { theme: 'light' | 'dark' }) {
  return theme === 'light'
    ? <IconMoon style={{ width: 20, height: 20 }} />
    : <IconSun style={{ width: 20, height: 20 }} />;
}

export default function App() {
  const [theme, setTheme] = useLocal<'light' | 'dark'>(
    'taqvim.theme',
    typeof window !== 'undefined' && window.matchMedia?.('(prefers-color-scheme: dark)').matches ? 'dark' : 'light',
  );
  const [tab, setTab] = useLocal<Tab>('taqvim.tab', 'calendar');
  const [hijriAdj, setHijriAdj] = useLocal<number>('taqvim.hijriAdj', -1);
  const [toast, setToast] = useState<string | null>(null);
  const [today, setToday] = useState(() => todayJdn());

  useEffect(() => {
    document.documentElement.dataset.theme = theme;
    const meta = document.querySelector('meta[name="theme-color"]:not([media])');
    if (meta) meta.setAttribute('content', theme === 'dark' ? '#0A1416' : '#0C8A82');
  }, [theme]);

  // keep "today" fresh across midnight / resume
  useEffect(() => {
    const iv = window.setInterval(() => {
      const t = todayJdn();
      setToday((prev) => (prev === t ? prev : t));
    }, 30_000);
    const onVis = () => setToday(todayJdn());
    document.addEventListener('visibilitychange', onVis);
    return () => {
      window.clearInterval(iv);
      document.removeEventListener('visibilitychange', onVis);
    };
  }, []);

  const showToast = useCallback((msg: string) => {
    setToast(msg);
    window.setTimeout(() => setToast((cur) => (cur === msg ? null : cur)), 1900);
  }, []);

  const view = useMemo(() => {
    switch (tab) {
      case 'calendar': return <CalendarView hijriAdj={hijriAdj} today={today} toast={showToast} />;
      case 'tools': return <ToolsView hijriAdj={hijriAdj} today={today} toast={showToast} />;
      case 'prayer': return <PrayerView today={today} />;
      case 'about': return <AboutView theme={theme} setTheme={setTheme} hijriAdj={hijriAdj} setHijriAdj={setHijriAdj} toast={showToast} />;
    }
  }, [tab, hijriAdj, today, theme, setTheme, setHijriAdj, showToast]);

  return (
    <>
      <div className="app">
        <header className="topbar">
          <div className="brand">
            <img className="brand-logo" src="./icons/icon-192.png" alt="لوگوی تقویم" width={40} height={40} />
            <div>
              <div className="brand-name">تقویم</div>
              <div className="brand-sub">شمسی · میلادی · قمری — آفلاین و خصوصی</div>
            </div>
          </div>
          <button
            className="icon-btn"
            onClick={() => setTheme(theme === 'light' ? 'dark' : 'light')}
            aria-label={theme === 'light' ? 'حالت تاریک' : 'حالت روشن'}
            title={theme === 'light' ? 'حالت تاریک' : 'حالت روشن'}
          >
            <ThemeIcon theme={theme} />
          </button>
        </header>

        <main className="view-enter" key={tab + String(today)}>
          {view}
        </main>

        <footer className="footer">
          <div>
            نسخهٔ وبِ پروژهٔ <a href="https://github.com/Matin-mohebi/TaqvimCod" target="_blank" rel="noreferrer">TaqvimCod</a> — همهٔ محاسبات روی دستگاه شما انجام می‌شود.
          </div>
          <div>
            ساخته‌شده توسط <a href="https://github.com/Matin-mohebi" target="_blank" rel="noreferrer">matin-code</a> · <a href="https://matinzeus.ir" target="_blank" rel="noreferrer">matinzeus.ir</a>
          </div>
        </footer>
      </div>

      <nav className="bottom-nav" aria-label="ناوبری اصلی">
        <div className="nav-inner">
          {TABS.map((t) => (
            <button
              key={t.id}
              className={`nav-item${tab === t.id ? ' active' : ''}`}
              onClick={() => setTab(t.id)}
              aria-current={tab === t.id ? 'page' : undefined}
            >
              {t.id === 'calendar' && <IconCalendar />}
              {t.id === 'tools' && <IconTools />}
              {t.id === 'prayer' && <IconMosque />}
              {t.id === 'about' && <IconInfo />}
              <span>{t.label}</span>
            </button>
          ))}
        </div>
      </nav>

      {toast && <div className="toast" role="status">{toast}</div>}
    </>
  );
}
