import { Link } from 'react-router-dom';

const metrics = [
  { value: '45+', label: 'Advisory tracks aligned to the reference service universe' },
  { value: '24/7', label: 'Account visibility for founders, admins, and clients' },
  { value: '1', label: 'Unified workspace for login, services, and purchase tracking' }
];

const features = [
  {
    title: 'Founder-Ready Access',
    text: 'OTP-backed sign-in, account protection, and a cleaner onboarding flow for clients.'
  },
  {
    title: 'Premium Service Catalog',
    text: 'Browse compliance and tax services in a card-based catalog inspired by the reference site.'
  },
  {
    title: 'Execution Console',
    text: 'Admins can manage service visibility, pricing, and workflow updates from one dashboard.'
  }
];

export default function LandingPage() {
  return (
    <section className="landing">
      <div className="landing-hero">
        <div className="hero-panel hero-panel-split">
          <div className="hero-copy">
            <p className="kicker">Legal-Tech Advisory Workspace</p>
            <h1>Bring the Tax Principals client experience into your internal product.</h1>
            <p className="muted">
              A polished service and client-management interface with premium visuals, OTP access, and a more
              trust-building presentation inspired by the reference website.
            </p>
            <div className="hero-actions">
              <Link className="btn" to="/login">
                Enter Client Workspace
              </Link>
              <Link className="btn secondary" to="/register">
                Start New Account
              </Link>
            </div>
          </div>

          <aside className="hero-highlights app-hero-highlights">
            <p className="kicker">Platform Snapshot</p>
            <div className="metric-grid">
              {metrics.map((item) => (
                <div className="metric" key={item.value}>
                  <strong>{item.value}</strong>
                  <span>{item.label}</span>
                </div>
              ))}
            </div>
          </aside>
        </div>
      </div>

      <section className="section-preview">
        <div className="section-preview-copy">
          <p className="kicker">Designed To Match</p>
          <h2>Soft editorial typography, teal-slate gradients, and advisory-style presentation.</h2>
          <p className="muted">
            The refreshed interface borrows the reference project’s premium legal and tax brand language while
            staying practical for day-to-day operations.
          </p>
        </div>
        <div className="trust-grid compact-trust-grid">
          <div className="trust-item"><strong>Elegant</strong>Visual hierarchy</div>
          <div className="trust-item"><strong>Clear</strong>Account journeys</div>
          <div className="trust-item"><strong>Consistent</strong>Admin and user surfaces</div>
        </div>
      </section>

      <div className="feature-grid">
        {features.map((item) => (
          <article className="feature-card" key={item.title}>
            <h3>{item.title}</h3>
            <p>{item.text}</p>
          </article>
        ))}
      </div>
    </section>
  );
}
