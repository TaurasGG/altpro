import { useEffect, useState } from 'react'
import { get, post } from '../api'

type Invitation = { id: string, organizationId: string, inviteeUsername?: string, inviteeEmail?: string, status: string }
type Organization = { id: string, name: string, description?: string }

export default function Dashboard() {
  const [invitations, setInvitations] = useState<Invitation[]>([])
  const [orgs, setOrgs] = useState<Organization[]>([])

  async function load() {
    const invs = await get<Invitation[]>('/api/invitations/mine')
    setInvitations(invs)
    const organizations = await get<Organization[]>('/api/orgs')
    setOrgs(organizations)
  }

  useEffect(() => { load() }, [])

  async function accept(id: string) {
    await post(`/api/invitations/${id}/accept`, {})
    const inv = invitations.find(i => i.id === id)
    if (inv) window.location.href = `/org-home?focus=${inv.organizationId}`
    else load()
  }
  async function decline(id: string) {
    await post(`/api/invitations/${id}/decline`, {})
    load()
  }

  return (
    <div className="grid">
      <div className="card" style={{ gridColumn: 'span 6' }}>
        <h2>Invitations</h2>
        {invitations.length === 0 && <p>No invitations</p>}
        {invitations.map(i => (
          <div key={i.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '6px 0', borderBottom: '1px solid #334155' }}>
            <div>
              <div>Organization: {i.organizationId}</div>
              <div style={{ color: '#94a3b8' }}>{i.inviteeUsername ? `@${i.inviteeUsername}` : i.inviteeEmail}</div>
            </div>
            <div style={{ display: 'flex', gap: 8 }}>
              <button className="btn" onClick={() => accept(i.id)}>Accept</button>
              <button className="btn secondary" onClick={() => decline(i.id)}>Decline</button>
            </div>
          </div>
        ))}
      </div>
      <div className="card" style={{ gridColumn: 'span 6' }}>
        <h2>My Organizations</h2>
        {orgs.map(o => (
          <div key={o.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '6px 0', borderBottom: '1px solid #334155' }}>
            <div>
              <strong>{o.name}</strong>
              <div style={{ color: '#94a3b8' }}>{o.description}</div>
            </div>
            <a className="btn" href={`/orgs?focus=${o.id}`}>Open</a>
          </div>
        ))}
      </div>
    </div>
  )
}

