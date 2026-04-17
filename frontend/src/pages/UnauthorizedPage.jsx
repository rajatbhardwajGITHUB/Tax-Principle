import { Link } from 'react-router-dom';

export default function UnauthorizedPage() {
  return (
    <section className="page-card narrow centered-card">
      <h1>403 - Unauthorized</h1>
      <p>You do not have access to this page.</p>
      <Link to="/services" className="btn">
        Go to services
      </Link>
    </section>
  );
}
