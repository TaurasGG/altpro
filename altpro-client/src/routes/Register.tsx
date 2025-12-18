import { useState } from 'react'

export default function Register() {
  const [email, setEmail] = useState('')
  const [username, setUsername] = useState('')
  const [displayName, setDisplayName] = useState('')
  const [password, setPassword] = useState('')
  const [message, setMessage] = useState('')

  async function submit() {
    const res = await fetch('http://localhost:9000/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, username, displayName, password })
    })
    if (res.ok) setMessage('Registered! You can login now.')
    else setMessage(await res.text())
  }

  return (
    <div className="card" style={{ maxWidth: 480 }}>
      <h2>Register</h2>
      <div className="field">
        <label>Email</label>
        <input type="email" value={email} onChange={e => setEmail(e.target.value)} placeholder="you@example.com" />
      </div>
      <div className="field">
        <label>Username</label>
        <input value={username} onChange={e => setUsername(e.target.value)} placeholder="yourname" />
      </div>
      <div className="field">
        <label>Display Name</label>
        <input value={displayName} onChange={e => setDisplayName(e.target.value)} placeholder="Your Name" />
      </div>
      <div className="field">
        <label>Password</label>
        <input type="password" value={password} onChange={e => setPassword(e.target.value)} />
      </div>
      <button className="btn" onClick={submit}>Register</button>
      {message && <p style={{ marginTop: 10 }}>{message}</p>}
    </div>
  )
}

