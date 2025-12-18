import { useEffect, useMemo, useState } from 'react'
import { get, post, del } from '../api'

type Organization = { id: string, name: string }
type Project = { id: string, organizationId: string, name: string, description?: string, members?: string[] }

export default function Projects() {
  const [orgs, setOrgs] = useState<Organization[]>([])
  const [selectedOrg, setSelectedOrg] = useState<string>('')
  const [projects, setProjects] = useState<Project[]>([])
  const [form, setForm] = useState({ name: '', description: '', members: '' })

  useEffect(() => {
    get<Organization[]>('/api/orgs').then(setOrgs)
  }, [])

  useEffect(() => {
    if (!selectedOrg) return
    get<Project[]>(`/api/orgs/${selectedOrg}/projects`).then(setProjects)
  }, [selectedOrg])

  async function createProject() {
    const body = {
      name: form.name,
      description: form.description,
      members: form.members.split(',').map(s => s.trim()).filter(Boolean),
      organizationId: selectedOrg
    }
    const created = await post<Project>(`/api/orgs/${selectedOrg}/projects`, body)
    setProjects(p => [created, ...p])
    setForm({ name: '', description: '', members: '' })
  }

  async function deleteProject(id: string) {
    await del(`/api/orgs/${selectedOrg}/projects/${id}`)
    setProjects(p => p.filter(x => x.id !== id))
  }

  return (
    <div>
      <h2>Projects</h2>
      <div className="card">
        <div className="field">
          <label>Organization</label>
          <select value={selectedOrg} onChange={e => setSelectedOrg(e.target.value)}>
            <option value="">Select organization</option>
            {orgs.map(o => <option key={o.id} value={o.id}>{o.name}</option>)}
          </select>
        </div>
        <div className="grid">
          <div className="card" style={{ gridColumn: 'span 6' }}>
            <h3>Create Project</h3>
            <div className="field">
              <label>Name</label>
              <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} />
            </div>
            <div className="field">
              <label>Description</label>
              <textarea value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} />
            </div>
            <div className="field">
              <label>Members (user IDs, comma-separated)</label>
              <input value={form.members} onChange={e => setForm({ ...form, members: e.target.value })} placeholder="u-alice, u-bob" />
            </div>
            <button className="btn" disabled={!selectedOrg} onClick={createProject}>Create</button>
          </div>
          <div className="card" style={{ gridColumn: 'span 6' }}>
            <h3>List</h3>
            {projects.map(p => (
              <div key={p.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '8px 0', borderBottom: '1px solid #334155' }}>
                <div>
                  <strong>{p.name}</strong>
                  <div style={{ color: '#94a3b8' }}>{p.description}</div>
                </div>
                <button className="btn secondary" onClick={() => deleteProject(p.id)}>Delete</button>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
