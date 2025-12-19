import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'

const AUTH_SERVER = 'http://localhost:9000'

export default function Logout() {
  const navigate = useNavigate()
  const idTokenHint = localStorage.getItem('id_token') || ''

  useEffect(() => {
    try {
      localStorage.clear()
    } catch {}
    setTimeout(() => {
      navigate('/')
    }, 1600)
  }, [navigate])

  return (
    <div className="card">
      <h3>Logging out…</h3>
      <p>Your session is ending. You will be redirected shortly.</p>
      <iframe title="auth-logout" src={`${AUTH_SERVER}/logout?id_token_hint=${encodeURIComponent(idTokenHint)}&post_logout_redirect_uri=${encodeURIComponent(window.location.origin + '/')}`} style={{ display: 'none' }} />
    </div>
  )
}
