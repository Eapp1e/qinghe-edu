import { mergeRecursive } from "@/utils/eapple"
import dictConverter from './DictConverter'

export const options = {
  metas: {
    '*': {
      /**
       * 瀛楀吀璇锋眰锛屾柟娉曠鍚嶄负function(dictMeta: DictMeta): Promise
       */
      request: (dictMeta) => {
        console.log(`load dict ${dictMeta.type}`)
        return Promise.resolve([])
      },
      /**
       * 瀛楀吀鍝嶅簲鏁版嵁杞崲鍣紝鏂规硶绛惧悕涓篺unction(response: Object, dictMeta: DictMeta): DictData
       */
      responseConverter,
      labelField: 'label',
      valueField: 'value',
    },
  },
  /**
   * 榛樿鏍囩瀛楁
   */
  DEFAULT_LABEL_FIELDS: ['label', 'name', 'title'],
  /**
   * 榛樿鍊煎瓧娈?
   */
  DEFAULT_VALUE_FIELDS: ['value', 'id', 'uid', 'key'],
}

/**
 * 鏄犲皠瀛楀吀
 * @param {Object} response 瀛楀吀鏁版嵁
 * @param {DictMeta} dictMeta 瀛楀吀鍏冩暟鎹?
 * @returns {DictData}
 */
function responseConverter(response, dictMeta) {
  const dicts = response.content instanceof Array ? response.content : response
  if (dicts === undefined) {
    console.warn(`no dict data of "${dictMeta.type}" found in the response`)
    return []
  }
  return dicts.map(d => dictConverter(d, dictMeta))
}

export function mergeOptions(src) {
  mergeRecursive(options, src)
}

export default options
