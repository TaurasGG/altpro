import { useEffect, useState } from 'react'
import { get, post } from '../api'

type Project = { id: string, name: string, description?: string }
type OrgDetails = { id: string, name: string, description?: string, members?: { userId?: string, email?: string, role: 'ADMIN' | 'MEMBER' }[] }

function decodeIdTokenSub(): string | null {
  const idToken = localStorage.getItem('id_token') || ''
  if (!idToken.includes('.')) return null
  try {
    const payload = JSON.parse(atob(idToken.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')))
    return payload.sub || payload.userId || null
  } catch {
    return null
  }
}

export default function OrganizationHome() {
  const params = new URLSearchParams(window.location.search)
  const orgId = params.get('focus') || ''
  const [org, setOrg] = useState<OrgDetails | null>(null)
  const [projects, setProjects] = useState<Project[]>([])
  const [showCreate, setShowCreate] = useState(false)
  const [form, setForm] = useState({ name: '', description: '' })
  const me = decodeIdTokenSub()

  useEffect(() => {
    if (!orgId) return
    get<Project[]>(`/api/orgs/${orgId}/projects`).then(setProjects)
    get<OrgDetails>(`/api/orgs/${orgId}`).then(setOrg).catch(() => setOrg(null))
  }, [orgId])

  async function leaveOrg() {
    await post(`/api/orgs/${orgId}/leave`, {})
    window.location.href = '/'
  }
  async function createProject() {
    const created = await post<Project>(`/api/orgs/${orgId}/projects`, { ...form, organizationId: orgId })
    window.location.href = `/tasks?project=${created.id}&org=${orgId}`
  }

  const isAdmin = !!org?.members?.some(m => (m.userId === me || m.email === me) && m.role === 'ADMIN')

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ marginBottom: 4 }}>{org?.name || 'Organization'}</h2>
          {org?.description && <div style={{ color: '#94a3b8' }}>{org.description}</div>}
        </div>
        <div style={{ display: 'flex', gap: 8 }}>
          <a className="btn secondary" href="/">Home</a>
          <a className="btn" href={`/org-settings?org=${orgId}`}>Settings</a>
          <button className="btn secondary" onClick={leaveOrg}>Leave</button>
        </div>
      </div>
      <div className="card">
        <h3>Projects</h3>
        <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: 8 }}>
          <button className="btn" onClick={() => setShowCreate(true)}>New Project</button>
        </div>
        {projects.map(p => (
          <div key={p.id} style={{ padding: '6px 0', borderBottom: '1px solid #334155', display: 'flex', justifyContent: 'space-between' }}>
            <div>
              <strong>{p.name}</strong>
              <div style={{ color: '#94a3b8' }}>{p.description}</div>
            </div>
            <div style={{ display: 'flex', gap: 8 }}>
              <a className="btn" href={`/tasks?project=${p.id}&org=${orgId}`}>Open Tasks</a>
              <a className="btn secondary" href={`/project-settings?project=${p.id}&org=${orgId}`}>Settings</a>
            </div>
          </div>
        ))}
      </div>
      {showCreate && (
        <div className="modal-backdrop">
          <div className="modal">
            <h3>Create Project</h3>
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
              <button className="btn" onClick={createProject}>Create</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
