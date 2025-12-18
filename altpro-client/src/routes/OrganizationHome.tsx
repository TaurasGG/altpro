import { useEffect, useState } from 'react'
import { get, post } from '../api'

type Project = { id: string, name: string, description?: string }

export default function OrganizationHome() {
  const params = new URLSearchParams(window.location.search)
  const orgId = params.get('focus') || ''
  const [projects, setProjects] = useState<Project[]>([])

  useEffect(() => {
    if (!orgId) return
    get<Project[]>(`/api/orgs/${orgId}/projects`).then(setProjects)
  }, [orgId])

  async function leaveOrg() {
    await post(`/api/orgs/${orgId}/leave`, {})
    window.location.href = '/dashboard'
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>Organization</h2>
        <div style={{ display: 'flex', gap: 8 }}>
          <a className="btn" href={`/orgs`}>Settings</a>
          <button className="btn secondary" onClick={leaveOrg}>Leave</button>
        </div>
      </div>
      <div className="card">
        <h3>Projects</h3>
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
    </div>
  )
}

