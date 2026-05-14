/**
 * Normalizes POST /api/sdlc/generate-stories JSON for the UI.
 * Accepts string or string[] for list-like fields; coerces unknown shapes safely.
 */
function toStringList(value) {
  if (value == null) return []
  if (Array.isArray(value)) {
    return value.map((item) => {
      if (typeof item === 'string') return item
      if (item != null && typeof item === 'object') {
        if (typeof item.text === 'string') return item.text
        if (typeof item.name === 'string') return item.name
        if (typeof item.title === 'string') return item.title
        return JSON.stringify(item)
      }
      return String(item)
    })
  }
  if (typeof value === 'string') return value.trim() ? [value] : []
  return [String(value)]
}

function normalizeServerErrorField(value) {
  if (value == null) return null
  if (typeof value === 'string') {
    const t = value.trim()
    return t || null
  }
  if (typeof value === 'object' && typeof value.message === 'string') {
    const t = value.message.trim()
    return t || null
  }
  const s = String(value).trim()
  return s || null
}

/** Coerces API `estimate` (or similar) to a display string for Story points; null if absent. */
function normalizeStoryPoints(value) {
  if (value == null || value === '') return null
  if (typeof value === 'number' && Number.isFinite(value)) return String(value)
  if (typeof value === 'string') {
    const t = value.trim()
    return t ? t : null
  }
  if (typeof value === 'object') return null
  return String(value)
}

export function normalizeGenerateStoriesResponse(data) {
  if (!data || typeof data !== 'object') {
    throw new Error('Unexpected response from server')
  }

  const projectSummary =
    typeof data.projectSummary === 'string' ? data.projectSummary : ''

  const ambiguities = toStringList(data.ambiguities)

  const rawStories = Array.isArray(data.userStories) ? data.userStories : []
  const summaryError =
    rawStories.length === 0 ? normalizeServerErrorField(data.error) : null

  const userStories = rawStories.map((s, i) => ({
    id: i,
    title: typeof s?.title === 'string' && s.title.trim() ? s.title : `User story ${i + 1}`,
    description: typeof s?.description === 'string' ? s.description : '',
    acceptanceCriteria: toStringList(s?.acceptanceCriteria),
    testCases: toStringList(s?.testCases),
    reasoning: typeof s?.reasoning === 'string' ? s.reasoning : '',
    storyPoints: normalizeStoryPoints(s?.estimate ?? s?.storyPoints),
  }))

  const hasContent =
    projectSummary.trim() !== '' ||
    ambiguities.length > 0 ||
    userStories.length > 0 ||
    summaryError != null

  if (!hasContent) {
    throw new Error('Unexpected response from server')
  }

  return { projectSummary, ambiguities, userStories, summaryError }
}
