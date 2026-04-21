/**
 * v-dialogDragWidth 可拖动弹窗高度（右下角）
 * Copyright (c) 2026 Eapp1e
 */

export default {
  bind(el) {
    const dragDom = el.querySelector('.el-dialog')
    const lineEl = document.createElement('div')
    lineEl.style =
      'width: 6px; background: inherit; height: 10px; position: absolute; right: 0; bottom: 0; margin: auto; z-index: 1; cursor: nwse-resize;'
    lineEl.addEventListener(
      'mousedown',
      function (e) {
        const disX = e.clientX - el.offsetLeft
        const disY = e.clientY - el.offsetTop
        const curWidth = dragDom.offsetWidth
        const curHeight = dragDom.offsetHeight
        document.onmousemove = function (event) {
          event.preventDefault()
          const xl = event.clientX - disX
          const yl = event.clientY - disY
          dragDom.style.width = `${curWidth + xl}px`
          dragDom.style.height = `${curHeight + yl}px`
        }
        document.onmouseup = function () {
          document.onmousemove = null
          document.onmouseup = null
        }
      },
      false
    )
    dragDom.appendChild(lineEl)
  }
}
