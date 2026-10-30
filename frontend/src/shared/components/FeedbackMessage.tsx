type FeedbackMessageProps = {
  message: string | null
  tone?: 'error' | 'success' | 'info'
}

function FeedbackMessage({ message, tone = 'info' }: FeedbackMessageProps) {
  if (!message) return null

  return (
    <p className={`feedback feedback--${tone}`} role={tone === 'error' ? 'alert' : 'status'}>
      {message}
    </p>
  )
}

export default FeedbackMessage
