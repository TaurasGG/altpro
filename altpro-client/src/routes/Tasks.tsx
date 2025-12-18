import { useEffect, useState } from 'react'
import { get, post, put, del } from '../api'

type Organization = { id: string, name: string }
type Project = { id: string, name: string }
type Task = { id: string, projectId: string, title: string, description?: string, status: 'TODO'|'IN_PROGRESS'|'DONE', priority: number, assignee?: string }
type Comment = { id: string, taskId: string, author: string, text: string }

export default function Tasks() {
  const [orgs, setOrgs] = useState<Organization[]>([])
  const [projects, setProjects] = useState<Project[]>([])
  const [selectedOrg, setSelectedOrg] = useState<string>('')
  const [selectedProject, setSelectedProject] = useState<string>('')
  const [tasks, setTasks] = useState<Task[]>([])
  const [comments, setComments] = useState<Record<string, Comment[]>>({})
  const [taskForm, setTaskForm] = useState({ title: '', description: '', status: 'TODO', priority: 1 })
  const [commentForm, setCommentForm] = useState({ text: '' })

  useEffect(() => { get<Organization[]>('/api/orgs').then(setOrgs) }, [])

  useEffect(() => {
    if (!selectedOrg) return
    get<Project[]>(`/api/orgs/${selectedOrg}/projects`).then(setProjects)
  }, [selectedOrg])

  useEffect(() => {
    if (!selectedProject || !selectedOrg) return
    get<Task[]>(`/api/orgs/${selectedOrg}/tasks/project/${selectedProject}`).then(setTasks)
  }, [selectedProject, selectedOrg])

  async function createTask() {
    const created = await post<Task>(`/api/orgs/${selectedOrg}/tasks`, {
      projectId: selectedProject,
      title: taskForm.title,
      description: taskForm.description,
      status: taskForm.status,
      priority: Number(taskForm.priority)
    })
    setTasks(t => [created, ...t])
    setTaskForm({ title: '', description: '', status: 'TODO', priority: 1 })
  }

  async function updateTask(id: string, status: Task['status']) {
    const task = tasks.find(t => t.id === id)!
    const updated = await put<Task>(`/api/orgs/${selectedOrg}/tasks/${id}`, { ...task, status })
    setTasks(t => t.map(x => x.id === id ? updated : x))
  }

  async function deleteTask(id: string) {
    await del(`/api/orgs/${selectedOrg}/tasks/${id}`)
    setTasks(t => t.filter(x => x.id !== id))
  }

  async function loadComments(taskId: string) {
    const list = await get<Comment[]>(`/api/orgs/${selectedOrg}/comments/task/${taskId}`)
    setComments(c => ({ ...c, [taskId]: list }))
  }

  async function addComment(taskId: string) {
    const created = await post<Comment>(`/api/orgs/${selectedOrg}/comments`, { taskId, text: commentForm.text })
    setComments(c => ({ ...c, [taskId]: [created, ...(c[taskId] ?? [])] }))
    setCommentForm({ text: '' })
  }

  return (
    <div>
      <h2>Tasks</h2>
      <div className="grid">
        <div className="card" style={{ gridColumn: 'span 4' }}>
          <div className="field">
            <label>Organization</label>
            <select value={selectedOrg} onChange={e => setSelectedOrg(e.target.value)}>
              <option value="">Select organization</option>
              {orgs.map(o => <option key={o.id} value={o.id}>{o.name}</option>)}
            </select>
          </div>
          <div className="field">
            <label>Project</label>
            <select value={selectedProject} onChange={e => setSelectedProject(e.target.value)} disabled={!selectedOrg}>
              <option value="">Select project</option>
              {projects.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
            </select>
          </div>
          <div className="field">
            <label>Title</label>
            <input value={taskForm.title} onChange={e => setTaskForm({ ...taskForm, title: e.target.value })} />
          </div>
          <div className="field">
            <label>Description</label>
            <textarea value={taskForm.description} onChange={e => setTaskForm({ ...taskForm, description: e.target.value })} />
          </div>
          <div className="field">
            <label>Status</label>
            <select value={taskForm.status} onChange={e => setTaskForm({ ...taskForm, status: e.target.value as Task['status'] })}>
              <option value="TODO">TODO</option>
              <option value="IN_PROGRESS">IN_PROGRESS</option>
              <option value="DONE">DONE</option>
            </select>
          </div>
          <div className="field">
            <label>Priority</label>
            <input type="number" min={1} max={5} value={taskForm.priority} onChange={e => setTaskForm({ ...taskForm, priority: Number(e.target.value) })} />
          </div>
          <button className="btn" disabled={!selectedProject} onClick={createTask}>Create Task</button>
        </div>

        <div className="card" style={{ gridColumn: 'span 8' }}>
          <h3>List</h3>
          {tasks.map(t => (
            <div key={t.id} style={{ borderBottom: '1px solid #334155', padding: '10px 0' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <strong>{t.title}</strong>
                  <div style={{ color: '#94a3b8' }}>{t.description}</div>
                </div>
                <div style={{ display: 'flex', gap: 8 }}>
                  <select value={t.status} onChange={e => updateTask(t.id, e.target.value as Task['status'])}>
                    <option value="TODO">TODO</option>
                    <option value="IN_PROGRESS">IN_PROGRESS</option>
                    <option value="DONE">DONE</option>
                  </select>
                  <button className="btn secondary" onClick={() => deleteTask(t.id)}>Delete</button>
                </div>
              </div>
              <div style={{ marginTop: 8 }}>
                <button className="btn" onClick={() => loadComments(t.id)}>Load comments</button>
                <div style={{ marginTop: 8 }}>
                  <div className="field">
                    <label>Add comment</label>
                    <input value={commentForm.text} onChange={e => setCommentForm({ text: e.target.value })} />
                  </div>
                  <button className="btn secondary" onClick={() => addComment(t.id)}>Add</button>
                </div>
                {(comments[t.id] ?? []).map(c => (
                  <div key={c.id} style={{ marginTop: 6 }}>
                    <strong>{c.author}</strong>: {c.text}
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

