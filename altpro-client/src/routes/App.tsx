import { Link, Route, Routes, useNavigate } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { FiMenu } from 'react-icons/fi'
import Home from './Home'
import Organizations from './Organizations'
import Projects from './Projects'
import Tasks from './Tasks'
import Callback from './Callback'
import Register from './Register'

export default function App() {
  const [open, setOpen] = useState(false)
  const navigate = useNavigate()
  const token = localStorage.getItem('access_token')

  useEffect(() => {
    setOpen(false)
  }, [navigate])

  return (
    <>
      <header>
        <div className="container nav">
          <div className="brand">AltPro</div>
          <nav className="links" aria-label="Primary">
            <Link to="/">Home</Link>
            <Link to="/orgs">Organizations</Link>
            <Link to="/projects">Projects</Link>
            <Link to="/tasks">Tasks</Link>
            {!token ? <Link to="/register">Register</Link> : <button className="btn secondary" onClick={() => { localStorage.clear(); navigate('/'); }}>Logout</button>}
          </nav>
          <button className="hamburger" aria-label="Menu" onClick={() => setOpen(o => !o)}><FiMenu /></button>
        </div>
        {open && (
          <div className="container" style={{ paddingBottom: 12 }}>
            <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
              <Link to="/">Home</Link>
              <Link to="/orgs">Organizations</Link>
              <Link to="/projects">Projects</Link>
              <Link to="/tasks">Tasks</Link>
              {!token ? <Link to="/register">Register</Link> : <button className="btn secondary" onClick={() => { localStorage.clear(); navigate('/'); }}>Logout</button>}
            </div>
          </div>
        )}
      </header>
      <main>
        <div className="container" style={{ padding: '20px 0' }}>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/orgs" element={<Organizations />} />
            <Route path="/projects" element={<Projects />} />
            <Route path="/tasks" element={<Tasks />} />
            <Route path="/callback" element={<Callback />} />
            <Route path="/register" element={<Register />} />
          </Routes>
        </div>
      </main>
      <footer>
        <div className="container">
          <div>© {new Date().getFullYear()} AltPro</div>
          <div>Built with React + Vite</div>
          <div>Colors follow accessible contrast</div>
        </div>
      </footer>
    </>
  )
}

