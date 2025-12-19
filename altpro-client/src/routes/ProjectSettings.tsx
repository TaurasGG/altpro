import { useEffect, useState } from 'react'
import { del, get, post, put } from '../api'

type Project = { id: string, organizationId: string, name: string, description?: string, members?: string[] }
type OrgMember = { userId: string, role: 'ADMIN' | 'MEMBER' }

export default function ProjectSettings() {
  const params = new URLSearchParams(window.location.search)
  const orgId = params.get('org') || ''
  const projectId = params.get('project') || ''
  const [project, setProject] = useState<Project | null>(null)
  const [form, setForm] = useState({ name: '', description: '' })
  const [memberForm, setMemberForm] = useState({ username: '', email: '' })
  const [confirming, setConfirming] = useState(false)
  const [orgMembers, setOrgMembers] = useState<OrgMember[]>([])
  const [selectedMember, setSelectedMember] = useState<string>('')
  const [profiles, setProfiles] = useState<Record<string, { username?: string, displayName?: string }>>({})

  async function load() {
    if (!orgId || !projectId) return
    const p = await get<Project>(`/api/orgs/${orgId}/projects/${projectId}`)
    setProject(p)
    setForm({ name: p.name, description: p.description || '' })
    const org = await get<any>(`/api/orgs/${orgId}`)
    setOrgMembers(org.members || [])
    const ids = Array.from(new Set([...(p.members || []), ...((org.members || []).map((m: OrgMember) => m.userId))]))
    for (const uid of ids) {
      if (uid && !profiles[uid]) {
        const res = await fetch(`http://localhost:9000/auth/users/by-id/${uid}`).then(r => r.ok ? r.json() : null).catch(() => null)
        if (res) setProfiles(prev => ({ ...prev, [uid]: { username: res.username, displayName: res.displayName } }))
      }
    }
  }
  useEffect(() => { load() }, [])

  async function save() {
    if (!project) return
    const updated = await put<Project>(`/api/orgs/${orgId}/projects/${project.id}`, { ...project, name: form.name, description: form.description })
    setProject(updated)
  }
  async function invite() {
    if (!project) return
    await post(`/api/orgs/${orgId}/projects/${project.id}/members`, memberForm)
    load()
    setMemberForm({ username: '', email: '' })
  }
  async function addFromDropdown() {
    if (!project || !selectedMember) return
    await post(`/api/orgs/${orgId}/projects/${project.id}/members`, { username: '', email: selectedMember })
    setSelectedMember('')
    load()
  }
  async function removeMember(memberId: string) {
    if (!project) return
    await del(`/api/orgs/${orgId}/projects/${project.id}/members/${memberId}`)
    load()
  }
  async function deleteProject() {
    if (!project) return
    await del(`/api/orgs/${orgId}/projects/${project.id}`)
    window.location.href = `/org-home?focus=${orgId}`
  }

  return (
    <div>
      <h2>Project Settings</h2>
      {!project && <p>Loading…</p>}
      {project && (
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
            <div style={{ marginBottom: 8 }}>
              {project.members?.map(m => {
                const p = profiles[m] || {}
                const label = `${p.displayName || ''}${p.username ? ` (@${p.username})` : ''}`.trim() || m
                return (
                  <div key={m} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '6px 0' }}>
                    <span>{label}</span>
                    <button className="btn secondary" onClick={() => removeMember(m)}>Remove</button>
                  </div>
                )
              })}
            </div>
            <div className="field">
              <label>Add from organization</label>
              <select value={selectedMember} onChange={e => setSelectedMember(e.target.value)}>
                <option value="">Select member</option>
                {orgMembers.map(m => {
                  const p = profiles[m.userId] || {}
                  const label = `${p.displayName || ''}${p.username ? ` (@${p.username})` : ''}`.trim() || m.userId
                  return <option key={m.userId} value={m.userId}>{label} ({m.role})</option>
                })}
              </select>
              <button className="btn" style={{ marginTop: 8 }} onClick={addFromDropdown}>Add</button>
            </div>
            <div className="field">
              <label>Invite by @username or email</label>
              <input value={memberForm.username} onChange={e => setMemberForm({ ...memberForm, username: e.target.value, email: '' })} placeholder="@username" />
            </div>
            <div className="field">
              <input value={memberForm.email} onChange={e => setMemberForm({ ...memberForm, email: e.target.value, username: '' })} placeholder="email@example.com" />
            </div>
            <button className="btn" onClick={invite}>Invite</button>
          </div>
          <div className="card" style={{ marginTop: 12 }}>
            <h3>Danger</h3>
            {!confirming ? (
              <button className="btn secondary" onClick={() => setConfirming(true)}>Delete Project</button>
            ) : (
              <div style={{ display: 'flex', gap: 8 }}>
                <button className="btn" onClick={deleteProject}>Confirm Delete</button>
                <button className="btn secondary" onClick={() => setConfirming(false)}>Cancel</button>
              </div>
            )}
          </div>
        </>
      )}
    </div>
  )
}
