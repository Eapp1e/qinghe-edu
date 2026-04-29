<script>
export default {
  name: 'MenuItem',
  functional: true,
  props: {
    icon: {
      type: String,
      default: ''
    },
    title: {
      type: String,
      default: ''
    }
  },
  render(h, context) {
    const { icon, title } = context.props
    const roleKeys = (((context.parent || {}).$store || {}).getters || {}).roles || []
    const isStudentOrParent = roleKeys.includes('edu_student') || roleKeys.includes('edu_parent')
    const normalizedTitle = (title || '').trim()
    const displayTitle = isStudentOrParent && ['报名记录', '上课记录'].includes(normalizedTitle)
      ? '学习记录'
      : title
    const vnodes = []

    if (icon) {
      vnodes.push(<svg-icon icon-class={icon}/>)
    }

    if (displayTitle) {
      if (displayTitle.length > 5) {
        vnodes.push(<span slot='title' title={(displayTitle)}>{(displayTitle)}</span>)
      } else {
        vnodes.push(<span slot='title'>{(displayTitle)}</span>)
      }
    }
    return vnodes
  }
}
</script>
