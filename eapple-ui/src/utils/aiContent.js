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

export function sanitizeAiDisplayContent(content) {
  const normalized = stripAiMarkdown(content)
  if (!normalized) {
    return ''
  }

  const punctuationPattern = /[\u4e00-\u9fa5，。！？；：、】【》、]/
  const trailingAsciiPattern = /\s+(?:[A-Za-z]{2,12}|[A-Za-z]{2,12}\s+[A-Za-z]{2,12}|[A-Za-z]{2,12}\s+[A-Za-z]{2,12}\s+[A-Za-z]{2,12})$/

  const cleanedLines = normalized
    .replace(/\uFFFD/g, '')
    .split('\n')
    .map(line => line.trim())
    .filter(line => line && !/^[A-Za-z][A-Za-z0-9_\s-]{0,20}$/.test(line))
    .map((line) => {
      let cleaned = line
        .replace(/\s+[A-Za-z]{2,12}\s+[�]\s+[A-Za-z]{2,12}$/g, '')
        .replace(/\s{2,}/g, ' ')
        .trim()

      if (punctuationPattern.test(cleaned)) {
        cleaned = cleaned.replace(trailingAsciiPattern, '')
      }

      if (/^[A-Za-z]{2,12}(?:\s+[A-Za-z]{2,12}){0,2}$/.test(cleaned)) {
        cleaned = ''
      }

      return cleaned.trim()
    })
    .filter(Boolean)

  return cleanedLines.join('\n').replace(/\n{3,}/g, '\n\n').trim()
}

export function renderAiContentHtml(content) {
  const normalized = sanitizeAiDisplayContent(content)
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
