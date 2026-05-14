import { useState } from 'react'
import './App.css'

const apiOrigin = (import.meta.env.VITE_API_URL ?? '').replace(/\/$/, '')
// const API = apiOrigin ? `${apiOrigin}/api/transform` : '/api/transform'
const API = apiOrigin ? `${apiOrigin}/api/sdlc/generate-stories` : '/api/sdlc/generate-stories'

export default function App() {
  const [input, setInput] = useState('')
  const [output, setOutput] = useState('')
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
      if (typeof data.text !== 'string') {
        throw new Error('Unexpected response from server')
      }
      setOutput(data.text)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Something went wrong')
      setOutput('')
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

      <div className="responser__panes">
        <section className="responser__pane responser__pane--input" aria-labelledby="input-heading">
          <h2 id="input-heading" className="responser__pane-title">
           Enter your requirements here...
          </h2>
          <label className="responser__label" htmlFor="long-text">
            Requirements
          </label>
          <textarea
            id="long-text"
            className="responser__textarea"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            placeholder="Enter your requirements in text format..."
            rows={16}
            disabled={status === 'loading'}
          />
          <button
            type="button"
            className="responser__send"
            onClick={handleSend}
            disabled={status === 'loading' || !input.trim()}
          >
            {status === 'loading' ? 'Sending…' : 'Send'}
          </button>
        </section>

        <section className="responser__pane responser__pane--output" aria-labelledby="output-heading">
          <h2 id="output-heading" className="responser__pane-title">
            Developer Artifacts
          </h2>
          <div
            className="responser__output"
            role="region"
            aria-live="polite"
            aria-busy={status === 'loading'}
          >
            {error ? (
              <p className="responser__error">{error}</p>
            ) : output ? (
              <pre className="responser__output-text">{output}</pre>
            ) : (
              <p className="responser__placeholder">
                Developer artifacts will be shown here after you send your requirements.
              </p>
            )}
          </div>
        </section>
      </div>
    </div>
  )
}
