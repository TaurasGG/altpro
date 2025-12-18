import { Link, Route, Routes, useNavigate } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { FiMenu } from 'react-icons/fi'
import Home from './Home'
import Dashboard from './Dashboard'
import Organizations from './Organizations'
import Projects from './Projects'
import Tasks from './Tasks'
import Callback from './Callback'
import { logout } from '../sso'
import RegisterRedirect from './RegisterRedirect'
import AutoLogin from './AutoLogin'
import OrganizationHome from './OrganizationHome'
import ProjectSettings from './ProjectSettings'

export default function App() {
  const [open, setOpen] = useState(false)
  const navigate = useNavigate()
  const token = localStorage.getItem('access_token')
  const [confirmLogout, setConfirmLogout] = useState(false)

  useEffect(() => {
    setOpen(false)
  }, [navigate])

  return (
    <>
      <header>
        <div className="container nav">
          <div className="brand">AltPro</div>
          <nav className="links" aria-label="Primary">
            {!token ? (
              <>
                <Link to="/">Home</Link>
                <Link to="/register">Register</Link>
              </>
            ) : (
              <>
                <Link to="/">Home</Link>
                <Link to="/dashboard">Dashboard</Link>
                <a href="http://localhost:9000/settings.html" style={{ color: 'white', textDecoration: 'none', fontWeight: 600 }}>Settings</a>
                <button className="btn secondary" onClick={() => setConfirmLogout(true)}>Logout</button>
              </>
            )}
          </nav>
          <button className="hamburger" aria-label="Menu" onClick={() => setOpen(o => !o)}><FiMenu /></button>
        </div>
        {open && (
          <div className="container" style={{ paddingBottom: 12 }}>
            <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
              <Link to="/">Home</Link>
              {token ? (
                <>
                  <Link to="/dashboard">Dashboard</Link>
                  <a href="http://localhost:9000/settings.html">Settings</a>
                  <button className="btn secondary" onClick={() => setConfirmLogout(true)}>Logout</button>
                </>
              ) : (
                <Link to="/register">Register</Link>
              )}
            </div>
          </div>
        )}
      </header>
      <main>
        <div className="container" style={{ padding: '20px 0' }}>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/orgs" element={<Organizations />} />
            <Route path="/projects" element={<Projects />} />
            <Route path="/tasks" element={<Tasks />} />
            <Route path="/callback" element={<Callback />} />
            <Route path="/register" element={<RegisterRedirect />} />
            <Route path="/auto-login" element={<AutoLogin />} />
            <Route path="/org-home" element={<OrganizationHome />} />
            <Route path="/project-settings" element={<ProjectSettings />} />
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
      {confirmLogout && (
        <div className="modal-backdrop">
          <div className="modal">
            <h3>Confirm Logout</h3>
            <p>Are you sure you want to log out?</p>
            <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
              <button className="btn secondary" onClick={() => setConfirmLogout(false)}>Cancel</button>
              <button className="btn" onClick={() => logout()}>Logout</button>
            </div>
          </div>
        </div>
      )}
    </>
  )
}

