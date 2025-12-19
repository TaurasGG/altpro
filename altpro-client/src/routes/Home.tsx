import { loginWithPkce } from '../sso'
import { useEffect, useState } from 'react'
import { get, post } from '../api'

export default function Home() {
  const token = localStorage.getItem('access_token')
  const [invitations, setInvitations] = useState<any[]>([])
  const [orgs, setOrgs] = useState<any[]>([])
  const [showCreate, setShowCreate] = useState(false)
  const [form, setForm] = useState({ name: '', description: '' })

  async function load() {
    const invs = await get<any[]>('/api/invitations/mine').catch(() => [])
    setInvitations(invs)
    const organizations = await get<any[]>('/api/orgs').catch(() => [])
    setOrgs(organizations)
  }
  useEffect(() => { if (token) load() }, [token])

  async function createOrg() {
    const created = await post<any>('/api/orgs', form)
    window.location.href = `/org-home?focus=${created.id}`
  }

  if (!token) {
    return (
      <div className="grid">
        <div className="card" style={{ gridColumn: 'span 8' }}>
          <h1>Welcome to AltPro</h1>
          <p>Organize projects across organizations. Secure SSO via AltPro Auth.</p>
          <div style={{ display: 'flex', gap: 8 }}>
            <a href="/auto-login" className="btn">Login</a>
            <a href="http://localhost:9000/register.html" className="btn secondary">Register</a>
          </div>
        </div>
        <div className="card" style={{ gridColumn: 'span 4' }}>
          <img className="responsive" src="https://images.unsplash.com/photo-1521737604893-d14cc237f11d?q=80&w=1200&auto=format&fit=crop" alt="Teamwork" />
        </div>
      </div>
    )
  }

  return (
    <div className="grid">
      <div className="card" style={{ gridColumn: 'span 6' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h2>Invitations</h2>
        </div>
        {invitations.length === 0 && <p>No invitations</p>}
        {invitations.map(i => (
          <div key={i.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '6px 0', borderBottom: '1px solid #334155' }}>
            <div>
              <div>Organization: {i.organizationId}</div>
              <div style={{ color: '#94a3b8' }}>{i.inviteeUsername ? `@${i.inviteeUsername}` : i.inviteeEmail}</div>
            </div>
            <div style={{ display: 'flex', gap: 8 }}>
              <button className="btn" onClick={async () => { await post(`/api/invitations/${i.id}/accept`, {}); window.location.href = `/org-home?focus=${i.organizationId}` }}>Accept</button>
              <button className="btn secondary" onClick={async () => { await post(`/api/invitations/${i.id}/decline`, {}); load() }}>Decline</button>
            </div>
          </div>
        ))}
      </div>
      <div className="card" style={{ gridColumn: 'span 6' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h2>My Organizations</h2>
          <button className="btn" onClick={() => setShowCreate(true)}>New Organization</button>
        </div>
        {orgs.map(o => (
          <div key={o.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '6px 0', borderBottom: '1px solid #334155' }}>
            <div>
              <strong>{o.name}</strong>
              <div style={{ color: '#94a3b8' }}>{o.description}</div>
            </div>
            <a className="btn" href={`/org-home?focus=${o.id}`}>Open</a>
          </div>
        ))}
      </div>
      {showCreate && (
        <div className="modal-backdrop">
          <div className="modal">
            <h3>Create Organization</h3>
            <div className="field">
              <label>Name</label>
              <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} />
            </div>
            <div className="field">
              <label>Description</label>
              <textarea value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} />
            </div>
            <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
              <button className="btn secondary" onClick={() => setShowCreate(false)}>Cancel</button>
              <button className="btn" onClick={createOrg}>Create</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

