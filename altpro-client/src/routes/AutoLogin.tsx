import { useEffect } from 'react'
import { loginWithPkce } from '../sso'

export default function AutoLogin() {
  useEffect(() => { loginWithPkce() }, [])
  return <div className="card">Logging you in…</div>
}

