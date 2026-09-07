export default function AdminPageHeader({ eyebrow = 'Admin workspace', title, text, action }) {
  return <div className="admin-page-header"><div><span className="eyebrow">{eyebrow}</span><h1>{title}</h1><p>{text}</p></div>{action}</div>;
}
