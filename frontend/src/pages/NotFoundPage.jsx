import { Link } from 'react-router-dom';

export default function NotFoundPage() {
  return (
    <section className="page-card narrow centered-card">
      <h1>404 - Page Not Found</h1>
      <p>We could not find the page you requested.</p>
      <Link to="/services" className="btn">
        Go home
      </Link>
    </section>
  );
}
