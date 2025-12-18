import { useEffect } from 'react'

export default function RegisterRedirect() {
  useEffect(() => { window.location.href = 'http://localhost:9000/register.html' }, [])
  return <div className="card">Redirecting to registration…</div>
}

