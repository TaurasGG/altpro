import { useEffect, useState } from 'react'
import { del, get, post, put } from '../api'

type OrgMember = { userId?: string, email?: string, role: 'ADMIN' | 'MEMBER' }
type Invitation = { id: string, organizationId: string, inviteeUsername?: string, inviteeEmail?: string, status?: string }
type Organization = { id: string, name: string, description?: string, members?: OrgMember[] }

export default function OrganizationSettings() {
  const params = new URLSearchParams(window.location.search)
  const orgId = params.get('org') || ''
  const [org, setOrg] = useState<Organization | null>(null)
  const [form, setForm] = useState({ name: '', description: '' })
  const [inviteForm, setInviteForm] = useState({ username: '', email: '' })
  const [invitations, setInvitations] = useState<Invitation[]>([])
  const [confirmingDelete, setConfirmingDelete] = useState(false)
  const [profiles, setProfiles] = useState<Record<string, { username?: string, displayName?: string }>>({})

  async function load() {
    if (!orgId) return
    const data = await get<Organization>(`/api/orgs/${orgId}`)
    setOrg(data)
    setForm({ name: data.name, description: data.description || '' })
    const invs = await get<Invitation[]>(`/api/orgs/${orgId}/invitations`).catch(() => [])
    setInvitations(invs)
    const ids = Array.from(new Set((data.members || []).map(m => m.userId).filter(Boolean) as string[]))
    for (const uid of ids) {
      if (uid && !profiles[uid]) {
        const res = await fetch(`http://localhost:9000/auth/users/by-id/${uid}`).then(r => r.ok ? r.json() : null).catch(() => null)
        if (res) setProfiles(prev => ({ ...prev, [uid]: { username: res.username, displayName: res.displayName } }))
      }
    }
  }
  useEffect(() => { load() }, [])

  async function save() {
    if (!org) return
    const updated = await put<Organization>(`/api/orgs/${org.id}`, { ...org, name: form.name, description: form.description })
    setOrg(updated)
  }

  async function promote(memberId: string) {
    await put(`/api/orgs/${orgId}/members/${memberId}/role`, { role: 'ADMIN' })
    load()
  }
  async function demote(memberId: string) {
    await put(`/api/orgs/${orgId}/members/${memberId}/role`, { role: 'MEMBER' })
    load()
  }
  async function removeMember(memberId: string, role: 'ADMIN' | 'MEMBER') {
    if (role === 'ADMIN') return
    await del(`/api/orgs/${orgId}/members/${memberId}`)
    load()
  }

  async function sendInvite() {
    await post(`/api/orgs/${orgId}/invitations`, inviteForm)
    setInviteForm({ username: '', email: '' })
    load()
  }
  async function cancelInvite(inviteId: string) {
    await del(`/api/orgs/${orgId}/invitations/${inviteId}`)
    setInvitations(prev => prev.filter(i => i.id !== inviteId))
  }

  async function deleteOrg() {
    if (!org) return
    await del(`/api/orgs/${org.id}`)
    window.location.href = '/'
  }

  return (
    <div>
      <h2>Organization Settings</h2>
      {!org && <p>Loading…</p>}
      {org && (
        <>
          <div className="card">
            <div className="field">
              <label>Name</label>
              <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} />
            </div>
            <div className="field">
              <label>Description</label>
              <textarea value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} />
            </div>
            <button className="btn" onClick={save}>Save</button>
          </div>
          <div className="card" style={{ marginTop: 12 }}>
            <h3>Members</h3>
            {(org.members || []).map(m => {
              const id = m.userId || m.email || ''
              const p = m.userId ? profiles[m.userId] || {} : {}
              const label = m.userId ? `${p.displayName || ''}${p.username ? ` (@${p.username})` : ''}`.trim() || id : (m.email || '')
              return (
                <div key={id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '6px 0', borderBottom: '1px solid #334155' }}>
                  <div>{label} ({m.role})</div>
                  <div style={{ display: 'flex', gap: 8 }}>
                    {m.role === 'MEMBER' && <button className="btn" onClick={() => promote(id)}>Make Admin</button>}
                    {m.role === 'ADMIN' && <button className="btn secondary" onClick={() => demote(id)}>Remove Admin</button>}
                    <button className="btn secondary" disabled={m.role === 'ADMIN'} onClick={() => removeMember(id, m.role)}>Remove</button>
                  </div>
                </div>
              )
            })}
          </div>
          <div className="card" style={{ marginTop: 12 }}>
            <h3>Invitations</h3>
            {(invitations.length === 0) && <p>No invitations</p>}
            {invitations.map(i => (
              <div key={i.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '6px 0', borderBottom: '1px solid #334155' }}>
                <div>
                  <div>{i.inviteeUsername ? `@${i.inviteeUsername}` : i.inviteeEmail}</div>
                </div>
                <button className="btn secondary" onClick={() => cancelInvite(i.id)}>Cancel</button>
              </div>
            ))}
            <div style={{ marginTop: 12 }}>
              <h4>Send new invite</h4>
              <div className="field">
                <label>Username (@username) or Email</label>
                <input value={inviteForm.username} onChange={e => setInviteForm({ ...inviteForm, username: e.target.value, email: '' })} placeholder="@username" />
              </div>
              <div className="field">
                <input value={inviteForm.email} onChange={e => setInviteForm({ ...inviteForm, email: e.target.value, username: '' })} placeholder="email@example.com" />
              </div>
              <button className="btn" onClick={sendInvite}>Send Invite</button>
            </div>
          </div>
          <div className="card" style={{ marginTop: 12 }}>
            <h3>Danger</h3>
            {!confirmingDelete ? (
              <button className="btn secondary" onClick={() => setConfirmingDelete(true)}>Delete Organization</button>
            ) : (
              <div style={{ display: 'flex', gap: 8 }}>
                <button className="btn" onClick={deleteOrg}>Confirm Delete</button>
                <button className="btn secondary" onClick={() => setConfirmingDelete(false)}>Cancel</button>
              </div>
            )}
          </div>
        </>
      )}
    </div>
  )
}
