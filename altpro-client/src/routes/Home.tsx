import { loginWithPkce } from '../sso'

export default function Home() {
  return (
    <div className="grid">
      <div className="card" style={{ gridColumn: 'span 8' }}>
        <h1>Welcome to AltPro</h1>
        <p>Organize projects across organizations. Secure SSO via AltPro Auth.</p>
        <div style={{ display: 'flex', gap: 8 }}>
          <button className="btn" onClick={() => loginWithPkce()}>Login</button>
          <a href="/register" className="btn secondary">Register</a>
        </div>
      </div>
      <div className="card" style={{ gridColumn: 'span 4' }}>
        <img className="responsive" src="https://images.unsplash.com/photo-1521737604893-d14cc237f11d?q=80&w=1200&auto=format&fit=crop" alt="Teamwork" />
      </div>
    </div>
  )
}

