import { useState } from 'react'
import SdlcResults from './components/SdlcResults.jsx'
import { normalizeGenerateStoriesResponse } from './utils/sdlc.js'
import './App.css'

const apiOrigin = (import.meta.env.VITE_API_URL ?? '').replace(/\/$/, '')
const API = apiOrigin ? `${apiOrigin}/api/sdlc/generate-stories` : '/api/sdlc/generate-stories'

export default function App() {
  const [input, setInput] = useState('')
  const [result, setResult] = useState(null)
  const [status, setStatus] = useState('idle')
  const [error, setError] = useState(null)

  async function handleSend() {
    setError(null)
    setStatus('loading')
    try {
      const res = await fetch(API, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ text: input }),
      })
      const data = await res.json().catch(() => ({}))
      if (!res.ok) {
        throw new Error(data.error || `Request failed (${res.status})`)
      }
      setResult(normalizeGenerateStoriesResponse(data))
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Something went wrong')
      setResult(null)
    } finally {
      setStatus('idle')
    }
  }

  return (
    <div className="responser">
      <header className="responser__header">
        <h1 className="responser__title">AI-Powered Requirement Assistant</h1>
        <p className="responser__subtitle">
          Enter requirements and get developer artifacts in structured format.
        </p>
      </header>

      <section
        className="responser__panel responser__panel--input"
        aria-labelledby="requirements-heading"
      >
        <h2 id="requirements-heading" className="responser__panel-title">
          Requirements (in Raw Text Format)
        </h2>
        <textarea
          id="requirements-text"
          className="responser__textarea responser__textarea--horizontal"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="Input your requirements"
          rows={5}
          disabled={status === 'loading'}
        />
        <div className="responser__actions">
          <button
            type="button"
            className="responser__send"
            onClick={handleSend}
            disabled={status === 'loading' || !input.trim()}
          >
            {status === 'loading' ? 'Sending…' : 'Send'}
          </button>
        </div>
      </section>

      <div
        className="responser__results"
        role="main"
        aria-label="Generated artifacts"
        aria-live="polite"
        aria-busy={status === 'loading'}
      >
        {error ? (
          <section className="responser__panel responser__panel--message">
            <p className="responser__error">{error}</p>
          </section>
        ) : result ? (
          <SdlcResults result={result} />
        ) : (
          <section className="responser__panel responser__panel--message">
            <p className="responser__placeholder">
              After you send requirements, this Requirement Assistant will generate project summary, user stories, and ambiguities.
              The generated artifacts will get displayed here..
            </p>
          </section>
        )}
      </div>
    </div>
  )
}
