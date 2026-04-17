const SESSION_KEY = 'um_session';

function notifySessionChange() {
  window.dispatchEvent(new Event('auth:changed'));
}

export function getSession() {
  const raw = localStorage.getItem(SESSION_KEY);
  if (!raw) return null;

  try {
    return JSON.parse(raw);
  } catch {
    localStorage.removeItem(SESSION_KEY);
    return null;
  }
}

export function saveSession(session) {
  localStorage.setItem(SESSION_KEY, JSON.stringify(session));
  notifySessionChange();
}

export function clearSession() {
  localStorage.removeItem(SESSION_KEY);
  notifySessionChange();
}

export function mergeSession(partial) {
  const current = getSession() || {};
  const next = { ...current, ...partial };
  saveSession(next);
  return next;
}
