import React from 'react';

const S = (props: React.SVGProps<SVGSVGElement>) => ({
  viewBox: '0 0 24 24',
  fill: 'none',
  stroke: 'currentColor',
  strokeWidth: 2,
  strokeLinecap: 'round' as const,
  strokeLinejoin: 'round' as const,
  'aria-hidden': true,
  ...props,
});

export const IconSun = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <circle cx="12" cy="12" r="4" />
    <path d="M12 2v2m0 16v2M4.9 4.9l1.4 1.4m11.4 11.4 1.4 1.4M2 12h2m16 0h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4" />
  </svg>
);

export const IconMoon = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <path d="M21 12.8A9 9 0 1 1 11.2 3a7 7 0 0 0 9.8 9.8z" />
  </svg>
);

export const IconCalendar = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <rect x="3" y="4" width="18" height="18" rx="3" />
    <path d="M16 2v4M8 2v4M3 10h18" />
  </svg>
);

export const IconTools = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <path d="M7 4v6m0 4v6M7 14a2 2 0 1 0 0-4 2 2 0 0 0 0 4zM17 4v10m0 4v2m0-6a2 2 0 1 0 0-4 2 2 0 0 0 0 4z" />
  </svg>
);

export const IconMosque = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <path d="M12 2s4 2.5 4 5c0 1.7-1.8 3-4 3s-4-1.3-4-3c0-2.5 4-5 4-5z" />
    <path d="M5 21v-6c0-2.2 3.1-4 7-4s7 1.8 7 4v6" />
    <path d="M3 21h18M9 21v-4a3 3 0 0 1 6 0v4" />
  </svg>
);

export const IconInfo = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <circle cx="12" cy="12" r="9" />
    <path d="M12 8h.01M12 12v4" />
  </svg>
);

export const IconChevronRight = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <path d="m9 6 6 6-6 6" />
  </svg>
);

export const IconChevronLeft = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <path d="m15 6-6 6 6 6" />
  </svg>
);

export const IconCopy = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <rect x="9" y="9" width="12" height="12" rx="2.5" />
    <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1" />
  </svg>
);

export const IconShare = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <circle cx="18" cy="5" r="3" />
    <circle cx="6" cy="12" r="3" />
    <circle cx="18" cy="19" r="3" />
    <path d="m8.6 13.5 6.8 4M15.4 6.5l-6.8 4" />
  </svg>
);

export const IconCheck = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <path d="m4.5 12.5 5 5 10-11" />
  </svg>
);

export const IconCompass = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <circle cx="12" cy="12" r="9" />
    <path d="m15.5 8.5-2 5-5 2 2-5 5-2z" />
  </svg>
);

export const IconClock = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <circle cx="12" cy="12" r="9" />
    <path d="M12 7v5l3 3" />
  </svg>
);

export const IconSparkle = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <path d="M12 3l1.9 5.1L19 10l-5.1 1.9L12 17l-1.9-5.1L5 10l5.1-1.9L12 3z" />
    <path d="M19 15l.9 2.4L22 18.3l-2.1.9L19 21.5l-.9-2.3-2.1-.9 2.1-.9L19 15z" />
  </svg>
);

export const IconNote = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <path d="M8 3h8a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2z" />
    <path d="M9.5 8h5m-5 4h5m-5 4h3" />
  </svg>
);

export const IconConvert = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <path d="M4 8h13m0 0-3.5-3.5M17 8l-3.5 3.5" />
    <path d="M20 16H7m0 0 3.5-3.5M7 16l3.5 3.5" />
  </svg>
);

export const IconGift = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <rect x="3" y="9" width="18" height="4" rx="1" />
    <path d="M5 13v6a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2v-6M12 9v12" />
    <path d="M12 9c-1.5 0-4.5-.5-4.5-3A2.5 2.5 0 0 1 12 4.5 2.5 2.5 0 0 1 16.5 6c0 2.5-3 3-4.5 3z" />
  </svg>
);

export const IconDownload = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <path d="M12 3v12m0 0 4.5-4.5M12 15l-4.5-4.5" />
    <path d="M4 17v2a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-2" />
  </svg>
);

export const IconGithub = (p: React.SVGProps<SVGSVGElement>) => (
  <svg viewBox="0 0 24 24" fill="currentColor" aria-hidden {...p}>
    <path d="M12 1.5A10.5 10.5 0 0 0 1.5 12c0 4.64 3 8.58 7.18 9.97.53.1.72-.23.72-.5v-1.78c-2.92.64-3.54-1.4-3.54-1.4-.48-1.22-1.17-1.54-1.17-1.54-.95-.65.07-.64.07-.64 1.06.07 1.61 1.08 1.61 1.08.94 1.6 2.46 1.14 3.06.87.1-.68.37-1.14.66-1.4-2.33-.27-4.78-1.17-4.78-5.2 0-1.14.4-2.08 1.07-2.81-.1-.27-.47-1.34.1-2.79 0 0 .88-.28 2.87 1.07a9.97 9.97 0 0 1 5.24 0c2-1.35 2.87-1.07 2.87-1.07.57 1.45.2 2.52.1 2.79.67.73 1.07 1.67 1.07 2.8 0 4.05-2.46 4.94-4.8 5.2.38.33.71.96.71 1.95v2.88c0 .28.19.6.73.5A10.5 10.5 0 0 0 22.5 12 10.5 10.5 0 0 0 12 1.5z" />
  </svg>
);

export const IconGlobe = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <circle cx="12" cy="12" r="9" />
    <path d="M3 12h18M12 3a15 15 0 0 1 0 18 15 15 0 0 1 0-18z" />
  </svg>
);

export const IconTelegram = (p: React.SVGProps<SVGSVGElement>) => (
  <svg viewBox="0 0 24 24" fill="currentColor" aria-hidden {...p}>
    <path d="M21.9 4.6c.3-1.3-.7-1.9-1.6-1.5L2.9 10.2c-1.3.5-1.2 1.2-.2 1.5l4.4 1.4 1.6 4.9c.2.5.4.7.8.7.4 0 .6-.2 1-.5l2.2-2.1 4.5 3.3c.8.5 1.4.2 1.7-.8l3-13.9-1 .4zM8.5 12.9l8.9-5.6c.4-.3.8-.1.5.2l-7.3 6.6-.3 3-1.8-4.2z" />
  </svg>
);

export const IconInstagram = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <rect x="3" y="3" width="18" height="18" rx="5" />
    <circle cx="12" cy="12" r="4" />
    <path d="M17.2 6.8h.01" />
  </svg>
);

export const IconGlobeLink = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <circle cx="12" cy="12" r="9" />
    <path d="M3 12h18M12 3a15 15 0 0 1 0 18 15 15 0 0 1 0-18z" />
  </svg>
);

export const IconWarning = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <path d="M12 3 2.5 20h19L12 3z" />
    <path d="M12 9v5m0 3h.01" />
  </svg>
);

export const IconArrowLeft = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <path d="M19 12H5m0 0 6 6m-6-6 6-6" />
  </svg>
);

export const IconUser = (p: React.SVGProps<SVGSVGElement>) => (
  <svg {...S(p)}>
    <circle cx="12" cy="8" r="4" />
    <path d="M4 21c0-4 3.6-6 8-6s8 2 8 6" />
  </svg>
);
