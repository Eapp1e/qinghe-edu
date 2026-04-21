/**
* v-dialogDrag 寮圭獥鎷栨嫿
* Copyright (c) 2026 Eapp1e
*/

export default {
  bind(el, binding, vnode, oldVnode) {
    const value = binding.value
    if (value == false) return
    // 鑾峰彇鎷栨嫿鍐呭澶撮儴
    const dialogHeaderEl = el.querySelector('.el-dialog__header')
    const dragDom = el.querySelector('.el-dialog')
    dialogHeaderEl.style.cursor = 'move'
    // 鑾峰彇鍘熸湁灞炴€?ie dom鍏冪礌.currentStyle 鐏嫄璋锋瓕 window.getComputedStyle(dom鍏冪礌, null)
    const sty = dragDom.currentStyle || window.getComputedStyle(dragDom, null)
    dragDom.style.position = 'absolute'
    dragDom.style.marginTop = 0
    let width = dragDom.style.width
    if (width.includes('%')) {
      width = +document.body.clientWidth * (+width.replace(/\%/g, '') / 100)
    } else {
      width = +width.replace(/\px/g, '')
    }
    dragDom.style.left = `${(document.body.clientWidth - width) / 2}px`
    // 榧犳爣鎸変笅浜嬩欢
    dialogHeaderEl.onmousedown = (e) => {
      // 榧犳爣鎸変笅锛岃绠楀綋鍓嶅厓绱犺窛绂诲彲瑙嗗尯鐨勮窛绂?(榧犳爣鐐瑰嚮浣嶇疆璺濈鍙绐楀彛鐨勮窛绂?
      const disX = e.clientX - dialogHeaderEl.offsetLeft
      const disY = e.clientY - dialogHeaderEl.offsetTop

      // 鑾峰彇鍒扮殑鍊煎甫px 姝ｅ垯鍖归厤鏇挎崲
      let styL, styT

      // 娉ㄦ剰鍦╥e涓?绗竴娆¤幏鍙栧埌鐨勫€间负缁勪欢鑷甫50% 绉诲姩涔嬪悗璧嬪€间负px
      if (sty.left.includes('%')) {
        styL = +document.body.clientWidth * (+sty.left.replace(/\%/g, '') / 100)
        styT = +document.body.clientHeight * (+sty.top.replace(/\%/g, '') / 100)
      } else {
        styL = +sty.left.replace(/\px/g, '')
        styT = +sty.top.replace(/\px/g, '')
      }

      // 榧犳爣鎷栨嫿浜嬩欢
      document.onmousemove = function (e) {
        // 閫氳繃浜嬩欢濮旀墭锛岃绠楃Щ鍔ㄧ殑璺濈 锛堝紑濮嬫嫋鎷借嚦缁撴潫鎷栨嫿鐨勮窛绂伙級
        const l = e.clientX - disX
        const t = e.clientY - disY

        let finallyL = l + styL
        let finallyT = t + styT

        // 绉诲姩褰撳墠鍏冪礌
        dragDom.style.left = `${finallyL}px`
        dragDom.style.top = `${finallyT}px`

      }

      document.onmouseup = function (e) {
        document.onmousemove = null
        document.onmouseup = null
      }
    }
  }
}
