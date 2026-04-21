/**
 * v-dialogDragWidth 鍙嫋鍔ㄥ脊绐楀搴︼紙鍙充晶杈癸級
 * Copyright (c) 2026 Eapp1e
 */

export default {
  bind(el) {
    const dragDom = el.querySelector('.el-dialog')
    const lineEl = document.createElement('div')
    lineEl.style = 'width: 5px; background: inherit; height: 80%; position: absolute; right: 0; top: 0; bottom: 0; margin: auto; z-index: 1; cursor: w-resize;'
    lineEl.addEventListener('mousedown',
      function (e) {
        // 榧犳爣鎸変笅锛岃绠楀綋鍓嶅厓绱犺窛绂诲彲瑙嗗尯鐨勮窛绂?
        const disX = e.clientX - el.offsetLeft
        // 褰撳墠瀹藉害
        const curWidth = dragDom.offsetWidth
        document.onmousemove = function (e) {
          e.preventDefault() // 绉诲姩鏃剁鐢ㄩ粯璁や簨浠?
          // 閫氳繃浜嬩欢濮旀墭锛岃绠楃Щ鍔ㄧ殑璺濈
          const l = e.clientX - disX
          dragDom.style.width = `${curWidth + l}px`
        }
        document.onmouseup = function (e) {
          document.onmousemove = null
          document.onmouseup = null
        }
      }, false)
    dragDom.appendChild(lineEl)
  }
}

