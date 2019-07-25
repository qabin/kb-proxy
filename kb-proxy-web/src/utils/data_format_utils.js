export function string_to_json(v) {
  try {
    return JSON.parse(v)
  } catch (e) {
    return v
  }
}

export function header_map_to_arr(v) {
  try {
    v = JSON.parse(v)
  } catch (e) {
    v = null
  }
  let headerArr = [];
  if (v) {
    Object.keys(v).map(k => {
      let headerMap = {
        key: k,
        value: v[k],
        description: null
      }
      headerArr.push(headerMap)
    })
    return headerArr
  }
  return []
}


export function header_arr_to_map(v) {
  if (v && v.length > 0) {
    let headerMap = {};
    v.map(h => {
      if (h.key) {
        headerMap[h.key] = h.value
      }
    })
    return headerMap;
  }
  return {};
}
