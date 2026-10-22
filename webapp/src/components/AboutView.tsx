import {
  IconDownload, IconGithub, IconGlobe, IconTelegram, IconInstagram,
  IconArrowLeft, IconInfo, IconWarning, IconNote,
} from './icons';

interface Props {
  theme: 'light' | 'dark';
  setTheme: (t: 'light' | 'dark') => void;
  hijriAdj: number;
  setHijriAdj: (v: number) => void;
  toast: (s: string) => void;
}

const LINKS = [
  {
    href: 'https://github.com/Matin-mohebi/TaqvimCod/releases/latest',
    icon: <IconDownload />,
    t: 'دریافت نسخهٔ اندروید (APK)',
    d: 'آخرین نسخهٔ منتشرشده از صفحهٔ Releases گیت‌هاب',
  },
  {
    href: 'https://github.com/Matin-mohebi/TaqvimCod',
    icon: <IconGithub />,
    t: 'سورس‌کد پروژه (گیت‌هاب)',
    d: 'اندروید (Kotlin/Java) · iOS (Swift) · همین نسخهٔ وب',
  },
  {
    href: './landing.html',
    icon: <IconGlobe />,
    t: 'صفحهٔ معرفی پروژه',
    d: 'خلاصهٔ امکانات و راهنمای دریافت برنامه',
  },
  {
    href: 'https://github.com/Matin-mohebi',
    icon: <IconGithub />,
    t: 'matin-code — سازنده',
    d: 'طراح سایت و توسعه‌دهنده وب در گیت‌هاب',
  },
  {
    href: 'https://matinzeus.ir',
    icon: <IconGlobe />,
    t: 'matinzeus.ir — سایت اصلی',
    d: 'طراحی سایت و توسعهٔ وب',
  },
  {
    href: 'https://matin-wp-pro.ir',
    icon: <IconGlobe />,
    t: 'matin-wp-pro.ir — فروشگاه کانفیگ',
    d: 'خرید کانفیگ و خدمات',
  },
  {
    href: 'https://t.me/matin3620',
    icon: <IconTelegram />,
    t: 'تلگرام: matin3620@',
    d: 'ارتباط مستقیم با سازنده',
  },
  {
    href: 'https://instagram.com/matin_web_site',
    icon: <IconInstagram />,
    t: 'اینستاگرام: matin_web_site@',
    d: 'نمونه‌کارها و پشت‌صحنه',
  },
];

export default function AboutView({ theme, setTheme, hijriAdj, setHijriAdj, toast }: Props) {
  const clearPlanner = () => {
    if (!confirm('همهٔ کارهای ذخیره‌شدهٔ برنامه‌ریز حذف شوند؟')) return;
    try { localStorage.removeItem('taqvim.planner.v1'); } catch { /* ignore */ }
    toast('برنامه‌ریز پاک شد');
    window.setTimeout(() => window.location.reload(), 600);
  };

  return (
    <>
      <section className="card" aria-label="درباره برنامه">
        <div className="about-app">
          <img src="./icons/icon-192.png" alt="لوگوی تقویم" width={64} height={64} />
          <div>
            <h3>تقویم | Taqvim</h3>
            <p>
              تقویم فارسیِ روزمره؛ ساده، دقیق و خصوصی.
              نسخهٔ وب با همان موتور محاسباتی نسخهٔ اندروید و iOS — تقویم جلالی،
              قمری جدولی با تصحیح ایران، مناسبت‌های رسمی، اوقات شرعی و برنامه‌ریز روزانه.
            </p>
          </div>
        </div>
      </section>

      <section className="card" aria-label="تنظیمات">
        <h3 className="card-title"><IconInfo /> تنظیمات</h3>
        <div className="setting-row">
          <div>
            <div className="s-label">حالت نمایش</div>
            <div className="s-desc">روشن، تاریک یا هماهنگ با دستگاه</div>
          </div>
          <select
            className="pick"
            value={theme}
            onChange={(e) => setTheme(e.target.value as 'light' | 'dark')}
            aria-label="حالت نمایش"
          >
            <option value="light">روشن</option>
            <option value="dark">تاریک</option>
          </select>
        </div>
        <div className="setting-row">
          <div>
            <div className="s-label">تصحیح تقویم قمری</div>
            <div className="s-desc">پیش‌فرض ایران: ۱− روز</div>
          </div>
          <select
            className="pick"
            value={hijriAdj}
            onChange={(e) => setHijriAdj(+e.target.value)}
            aria-label="تصحیح قمری"
          >
            {[-2, -1, 0, 1, 2].map((v) => (
              <option key={v} value={v}>{v > 0 ? `+${v}` : v}</option>
            ))}
          </select>
        </div>
        <div className="setting-row">
          <div>
            <div className="s-label">پاک‌کردن داده‌های برنامه‌ریز</div>
            <div className="s-desc">همهٔ کارهای ذخیره‌شده از مرورگر شما حذف می‌شود</div>
          </div>
          <button className="mini-btn ghost" onClick={clearPlanner}>پاک‌کردن</button>
        </div>
      </section>

      <section className="card" aria-label="حریم خصوصی">
        <h3 className="card-title"><IconWarning /> حریم خصوصی</h3>
        <p style={{ margin: 0, fontSize: 13, lineHeight: 2, color: 'var(--muted)' }}>
          این وب‌اپ هیچ سروری ندارد؛ همهٔ محاسبات (تاریخ‌ها، مناسبت‌ها، اوقات شرعی) و
          کارهای برنامه‌ریز فقط روی دستگاه شما انجام و ذخیره می‌شود. هیچ داده‌ای ارسال
          نمی‌شود و برنامه برای استفادهٔ آفلاین هم کار می‌کند.
        </p>
      </section>

      <section className="card" aria-label="پیوندها">
        <h3 className="card-title"><IconNote /> پیوندهای مفید</h3>
        <div className="link-list">
          {LINKS.map((l) => (
            <a key={l.t} className="link-item" href={l.href} target="_blank" rel="noreferrer">
              <span className="li-icon">{l.icon}</span>
              <span className="li-txt">
                <span className="li-t">{l.t}</span><br />
                <span className="li-d">{l.d}</span>
              </span>
              <span className="li-arrow"><IconArrowLeft /></span>
            </a>
          ))}
        </div>
      </section>
    </>
  );
}
