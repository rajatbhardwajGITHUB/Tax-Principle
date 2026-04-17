export default function ErrorAlert({ message }) {
  if (!message) return null;

  return (
    <div className="alert error" role="alert">
      {message}
    </div>
  );
}
