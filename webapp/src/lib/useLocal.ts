import { useEffect, useState } from 'react';

/** localStorage-backed state (SSR-safe, JSON, resilient). */
export function useLocal<T>(key: string, initial: T): [T, (v: T | ((prev: T) => T)) => void] {
  const [value, setValue] = useState<T>(() => {
    try {
      const raw = localStorage.getItem(key);
      return raw !== null ? (JSON.parse(raw) as T) : initial;
    } catch {
      return initial;
    }
  });

  useEffect(() => {
    try {
      localStorage.setItem(key, JSON.stringify(value));
    } catch {
      /* storage full/blocked — ignore */
    }
  }, [key, value]);

  return [value, setValue];
}
