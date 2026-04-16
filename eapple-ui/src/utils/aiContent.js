function escapeHtml(value) {
  return String(value || '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

function formatInline(text) {
  return escapeHtml(text)
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/__(.+?)__/g, '<strong>$1</strong>')
    .replace(/`([^`]+)`/g, '<code>$1</code>')
}

export function normalizeAiMarkdown(content) {
  if (!content) {
    return ''
  }
  return String(content)
    .replace(/\r\n/g, '\n')
    .replace(/```(?:markdown|md)?/gi, '')
    .replace(/```/g, '')
    .replace(/\n{3,}/g, '\n\n')
    .trim()
}

export function stripAiMarkdown(content) {
  const normalized = normalizeAiMarkdown(content)
  if (!normalized) {
    return ''
  }
  return normalized
    .replace(/^#{1,6}\s*/gm, '')
    .replace(/^\s*[-*]\s+/gm, '')
    .replace(/^\s*\d+\.\s+/gm, '')
    .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '$1')
    .replace(/\*\*(.+?)\*\*/g, '$1')
    .replace(/__(.+?)__/g, '$1')
    .replace(/`([^`]+)`/g, '$1')
    .replace(/\n{3,}/g, '\n\n')
    .trim()
}

export function renderAiContentHtml(content) {
  const normalized = normalizeAiMarkdown(content)
  if (!normalized) {
    return '<p>暂无可展示内容</p>'
  }

  const lines = normalized.split('\n')
  const blocks = []
  let listType = ''
  let listItems = []

  const flushList = () => {
    if (!listItems.length) {
      return
    }
    const tag = listType || 'ul'
    blocks.push(`<${tag}>${listItems.join('')}</${tag}>`)
    listItems = []
    listType = ''
  }

  lines.forEach((line) => {
    const trimmed = line.trim()
    if (!trimmed) {
      flushList()
      return
    }

    const headingMatch = trimmed.match(/^(#{1,6})\s+(.+)$/)
    if (headingMatch) {
      flushList()
      const level = Math.min(headingMatch[1].length + 1, 6)
      blocks.push(`<h${level}>${formatInline(headingMatch[2])}</h${level}>`)
      return
    }

    const unorderedMatch = trimmed.match(/^[-*•]\s+(.+)$/)
    if (unorderedMatch) {
      if (listType && listType !== 'ul') {
        flushList()
      }
      listType = 'ul'
      listItems.push(`<li>${formatInline(unorderedMatch[1])}</li>`)
      return
    }

    const orderedMatch = trimmed.match(/^\d+\.\s+(.+)$/)
    if (orderedMatch) {
      if (listType && listType !== 'ol') {
        flushList()
      }
      listType = 'ol'
      listItems.push(`<li>${formatInline(orderedMatch[1])}</li>`)
      return
    }

    flushList()
    blocks.push(`<p>${formatInline(trimmed)}</p>`)
  })

  flushList()
  return blocks.join('')
}
