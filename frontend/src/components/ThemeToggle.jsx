import { Moon, Sun } from 'lucide-react';
import { useTheme } from '../context/ThemeContext';

export default function ThemeToggle({ compact = false }) {
  const { theme, toggleTheme } = useTheme();
  const Icon = theme === 'light' ? Moon : Sun;
  return (
    <button
      type="button"
      className={`theme-toggle ${compact ? 'theme-toggle--compact' : ''}`}
      onClick={toggleTheme}
      aria-label={`Switch to ${theme === 'light' ? 'dark' : 'light'} mode`}
      title={`Switch to ${theme === 'light' ? 'dark' : 'light'} mode`}
    >
      <Icon size={18} />
      {!compact && <span>{theme === 'light' ? 'Dark' : 'Light'}</span>}
    </button>
  );
}
