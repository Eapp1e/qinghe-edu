/**
 * v-dialogDragWidth 鍙嫋鍔ㄥ脊绐楅珮搴︼紙鍙充笅瑙掞級
 * Copyright (c) 2026 Eapp1e
 */

export default {
  bind(el) {
    const dragDom = el.querySelector('.el-dialog')
    const lineEl = document.createElement('div')
    lineEl.style = 'width: 6px; background: inherit; height: 10px; position: absolute; right: 0; bottom: 0; margin: auto; z-index: 1; cursor: nwse-resize;'
    lineEl.addEventListener('mousedown',
      function(e) {
        // 榧犳爣鎸変笅锛岃绠楀綋鍓嶅厓绱犺窛绂诲彲瑙嗗尯鐨勮窛绂?
        const disX = e.clientX - el.offsetLeft
        const disY = e.clientY - el.offsetTop
        // 褰撳墠瀹藉害 楂樺害
        const curWidth = dragDom.offsetWidth
        const curHeight = dragDom.offsetHeight
        document.onmousemove = function(e) {
          e.preventDefault() // 绉诲姩鏃剁鐢ㄩ粯璁や簨浠?
          // 閫氳繃浜嬩欢濮旀墭锛岃绠楃Щ鍔ㄧ殑璺濈
          const xl = e.clientX - disX
          const yl = e.clientY - disY
          dragDom.style.width = `${curWidth + xl}px`
          dragDom.style.height = `${curHeight + yl}px`
        }
        document.onmouseup = function(e) {
          document.onmousemove = null
          document.onmouseup = null
        }
      }, false)
    dragDom.appendChild(lineEl)
  }
}

