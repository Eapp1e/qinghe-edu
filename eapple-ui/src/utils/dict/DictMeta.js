import { mergeRecursive } from "@/utils/eapple"
import DictOptions from './DictOptions'

/**
 * @classdesc 字典元数据
 * @property {String} type 字典类型
 * @property {Function} request 请求方法
 * @property {String} labelField 标签字段名
 * @property {String} valueField 值字段名
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
 * 解析字典元数据配置
 * @param {Object|string} options 配置项或字典类型
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
