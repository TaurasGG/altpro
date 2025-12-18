import { useEffect, useState } from 'react'
import { get, post, put, del } from '../api'

type OrgMember = { email: string, role: 'ADMIN' | 'MEMBER' }
type Organization = { id: string, name: string, description?: string, createdAt?: string, members?: OrgMember[] }

export default function Organizations() {
  const [orgs, setOrgs] = useState<Organization[]>([])
  const [showModal, setShowModal] = useState(false)
  const [form, setForm] = useState({ name: '', description: '' })
  const [inviteForm, setInviteForm] = useState({ username: '', email: '' })
  const [activeOrg, setActiveOrg] = useState<string>('')

  async function load() {
    const data = await get<Organization[]>('/api/orgs')
    setOrgs(data)
  }

  useEffect(() => { load() }, [])

  async function createOrg() {
    const created = await post<Organization>('/api/orgs', form)
    setOrgs(o => [created, ...o])
    setShowModal(false)
    setForm({ name: '', description: '' })
  }

  async function invite() {
    await post(`/api/orgs/${activeOrg}/invitations`, inviteForm)
    setInviteForm({ username: '', email: '' })
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
        <h2>Organizations</h2>
        <button className="btn" onClick={() => setShowModal(true)}>New Organization</button>
      </div>
      <div className="grid">
        {orgs.map(o => (
          <div key={o.id} className="card" style={{ gridColumn: 'span 6' }}>
            <h3 style={{ marginTop: 0 }}>{o.name}</h3>
            <p>{o.description}</p>
            <p><small>Members: {o.members?.length ?? 0}</small></p>
            <div style={{ display: 'flex', gap: 8 }}>
              <button className="btn" onClick={() => setActiveOrg(o.id)}>Settings</button>
              <a className="btn secondary" href={`/projects?org=${o.id}`}>Projects</a>
            </div>
            {activeOrg === o.id && (
              <div style={{ marginTop: 10 }}>
                <h4>Invite member</h4>
                <div className="field">
                  <label>Username (@username) or Email</label>
                  <input value={inviteForm.username} onChange={e => setInviteForm({ ...inviteForm, username: e.target.value, email: '' })} placeholder="@username" />
                </div>
                <div className="field">
                  <input value={inviteForm.email} onChange={e => setInviteForm({ ...inviteForm, email: e.target.value, username: '' })} placeholder="email@example.com" />
                </div>
                <button className="btn" onClick={invite}>Send Invite</button>
              </div>
            )}
          </div>
        ))}
      </div>

      {showModal && (
        <div className="modal-backdrop" role="dialog" aria-modal="true">
          <div className="modal">
            <h3>Create Organization</h3>
            <div className="field">
              <label>Name</label>
              <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} placeholder="Org name" />
            </div>
            <div className="field">
              <label>Description</label>
              <textarea value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} placeholder="Short description" />
            </div>
            <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
              <button className="btn secondary" onClick={() => setShowModal(false)}>Cancel</button>
              <button className="btn" onClick={createOrg}>Create</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
