import { useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { exchangeCodeForToken } from '../sso'

export default function Callback() {
  const [params] = useSearchParams()
  const navigate = useNavigate()

  useEffect(() => {
    const code = params.get('code')
    if (!code) return
    exchangeCodeForToken(code)
      .then(() => navigate('/'))
      .catch(() => navigate('/'))
  }, [params, navigate])

  return <div className="card">Authenticating...</div>
}

