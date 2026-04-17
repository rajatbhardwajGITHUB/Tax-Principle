export default function Loader({ label = 'Loading...' }) {
  return (
    <div className="centered-card">
      <div className="spinner" aria-hidden="true" />
      <p>{label}</p>
    </div>
  );
}
