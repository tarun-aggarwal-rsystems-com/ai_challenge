export default function SdlcResults({ result }) {
  if (!result) return null

  const { projectSummary, ambiguities, userStories, summaryError } = result

  return (
    <div className="sdlc">
      {summaryError ? (
        <section
          className="sdlc__panel sdlc__panel--summary-error"
          aria-labelledby="sdlc-summary-heading"
          role="alert"
        >
          <h3 id="sdlc-summary-heading" className="sdlc__panel-title">
            Project Summary
          </h3>
          <p className="sdlc__summary-error-text">{summaryError}</p>
        </section>
      ) : projectSummary.trim() ? (
        <section
          className="sdlc__panel"
          aria-labelledby="sdlc-summary-heading"
        >
          <h3 id="sdlc-summary-heading" className="sdlc__panel-title">
            Project Summary
          </h3>
          <p className="sdlc__prose">{projectSummary}</p>
        </section>
      ) : null}

      {userStories.length > 0
        ? userStories.map((story) => (
            <article
              key={story.id}
              className="sdlc__panel sdlc__story"
              aria-labelledby={`sdlc-story-title-${story.id}`}
            >
              <h3 id={`sdlc-story-title-${story.id}`} className="sdlc__story-title">
                {story.title}
              </h3>
              {story.description.trim() ? (
                <div className="sdlc__story-section">
                  <span className="sdlc__label">Description</span>
                  <p className="sdlc__prose">{story.description}</p>
                </div>
              ) : null}
              {story.acceptanceCriteria.length > 0 ? (
                <div className="sdlc__story-section">
                  <span className="sdlc__label">Acceptance criteria</span>
                  <ol className="sdlc__ol">
                    {story.acceptanceCriteria.map((c, j) => (
                      <li key={j} className="sdlc__ol-item">
                        {c}
                      </li>
                    ))}
                  </ol>
                </div>
              ) : null}
              {story.testCases.length > 0 ? (
                <div className="sdlc__story-section">
                  <span className="sdlc__label">Test cases</span>
                  <ol className="sdlc__ol">
                    {story.testCases.map((t, j) => (
                      <li key={j} className="sdlc__ol-item">
                        {t}
                      </li>
                    ))}
                  </ol>
                </div>
              ) : null}
              {story.storyPoints != null ? (
                <div className="sdlc__story-section">
                  <span className="sdlc__label">Estimate</span>
                  <p
                    className="sdlc__prose"
                    aria-label={`Estimate: ${story.storyPoints} story points`}
                  >
                    <span className="sdlc__estimate-value">{story.storyPoints}</span> Story Points
                  </p>
                </div>
              ) : null}
              {story.reasoning.trim() ? (
                <div className="sdlc__story-section">
                  <span className="sdlc__label">Reasoning</span>
                  <p className="sdlc__prose sdlc__prose--muted">{story.reasoning}</p>
                </div>
              ) : null}
            </article>
          ))
        : null}

      {ambiguities.length > 0 ? (
        <section
          className="sdlc__panel"
          aria-labelledby="sdlc-ambiguities-heading"
        >
          <h3 id="sdlc-ambiguities-heading" className="sdlc__panel-title">
            Ambiguities
          </h3>
          <ul className="sdlc__list">
            {ambiguities.map((item, idx) => (
              <li key={idx} className="sdlc__list-item">
                {item}
              </li>
            ))}
          </ul>
        </section>
      ) : null}
    </div>
  )
}
