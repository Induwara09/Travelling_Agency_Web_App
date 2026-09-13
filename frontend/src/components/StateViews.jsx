import { AlertTriangle, Compass, LoaderCircle } from 'lucide-react';

export function LoadingState({ label = 'Loading your journey...' }) {
  return <div className="state-view"><LoaderCircle className="spin" /><p>{label}</p></div>;
}

export function EmptyState({ title = 'Nothing here yet', text = 'New experiences are on the way.' }) {
  return <div className="state-view state-view--card"><Compass /><h3>{title}</h3><p>{text}</p></div>;
}

export function ErrorState({ message, onRetry }) {
  return <div className="state-view state-view--error"><AlertTriangle /><h3>We hit a little detour</h3><p>{message}</p>{onRetry && <button className="button button--secondary" onClick={onRetry}>Try again</button>}</div>;
}
