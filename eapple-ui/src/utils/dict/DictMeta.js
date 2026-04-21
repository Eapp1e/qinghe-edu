import { mergeRecursive } from "@/utils/eapple"
import DictOptions from './DictOptions'

/**
 * @classdesc 瀛楀吀鍏冩暟鎹?
 * @property {String} type 绫诲瀷
 * @property {Function} request 璇锋眰
 * @property {String} label 鏍囩瀛楁
 * @property {String} value 鍊煎瓧娈?
 */
export default class DictMeta {
  constructor(options) {
    this.type = options.type
    this.request = options.request
    this.responseConverter = options.responseConverter
    this.labelField = options.labelField
    this.valueField = options.valueField
    this.lazy = options.lazy === true
  }
}


/**
 * 瑙ｆ瀽瀛楀吀鍏冩暟鎹?
 * @param {Object} options
 * @returns {DictMeta}
 */
DictMeta.parse= function(options) {
  let opts = null
  if (typeof options === 'string') {
    opts = DictOptions.metas[options] || {}
    opts.type = options
  } else if (typeof options === 'object') {
    opts = options
  }
  opts = mergeRecursive(DictOptions.metas['*'], opts)
  return new DictMeta(opts)
}
