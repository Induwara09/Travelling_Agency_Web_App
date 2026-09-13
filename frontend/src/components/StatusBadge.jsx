export default function StatusBadge({ value }) {
  const label = value || 'UNKNOWN';
  return <span className={`status status--${label.toLowerCase()}`}>{label.replace('_', ' ')}</span>;
}
