const AUTH_SERVER = 'http://localhost:9000'
const CLIENT_ID = 'altpro-web'
const REDIRECT_URI = 'http://localhost:3000/callback'
const SCOPE = 'openid api.read api.write'

function base64urlencode(str: ArrayBuffer) {
  const bytes = new Uint8Array(str)
  let binary = ''
  for (let i = 0; i < bytes.byteLength; i++) binary += String.fromCharCode(bytes[i])
  return btoa(binary).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '')
}

export async function loginWithPkce() {
  const codeVerifier = (Math.random().toString(36).slice(2) + Math.random().toString(36).slice(2)).slice(0, 64)
  const enc = new TextEncoder()
  const challenge = await crypto.subtle.digest('SHA-256', enc.encode(codeVerifier)).then(base64urlencode)
  localStorage.setItem('pkce_verifier', codeVerifier)
  const url = new URL(`${AUTH_SERVER}/oauth2/authorize`)
  url.searchParams.set('response_type', 'code')
  url.searchParams.set('client_id', CLIENT_ID)
  url.searchParams.set('redirect_uri', REDIRECT_URI)
  url.searchParams.set('scope', SCOPE)
  url.searchParams.set('code_challenge', challenge)
  url.searchParams.set('code_challenge_method', 'S256')
  window.location.href = url.toString()
}

export async function exchangeCodeForToken(code: string) {
  const verifier = localStorage.getItem('pkce_verifier')!
  const body = new URLSearchParams({
    grant_type: 'authorization_code',
    client_id: CLIENT_ID,
    code,
    redirect_uri: REDIRECT_URI,
    code_verifier: verifier
  })
  const res = await fetch(`${AUTH_SERVER}/oauth2/token`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body
  })
  if (!res.ok) throw new Error('Token exchange failed')
  const data = await res.json()
  localStorage.setItem('access_token', data.access_token)
  localStorage.setItem('id_token', data.id_token ?? '')
}

export function authHeader(): Record<string, string> {
  const token = localStorage.getItem('access_token')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

export function logout() {
  localStorage.clear()
  window.location.href = `${AUTH_SERVER}/logout`
}
