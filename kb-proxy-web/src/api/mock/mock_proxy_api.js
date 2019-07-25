import {axiosInstance} from "../../plugins/axios";

export function ajax_add_mock_proxy(model) {
  return axiosInstance({
    url: '/api/mock/proxy',
    method: 'post',
    data: model
  })
}


export function ajax_mock_proxy_search(kw, page, size) {
  let form = {
    kw: kw,
    page: page,
    size: size
  }
  return axiosInstance({
    url: '/api/mock/proxy/_search',
    method: 'get',
    params: form
  })
}


export function ajax_get_mock_proxy(id) {
  return axiosInstance({
    url: '/api/mock/proxy/' + id,
    method: 'get',
  })
}

export function ajax_delete_mock_proxy_by_id(id) {
  return axiosInstance({
    url: '/api/mock/proxy/' + id,
    method: 'delete',
  })
}

export function ajax_update_mock_proxy_by_id(id, body) {
  return axiosInstance({
    url: '/api/mock/proxy/' + id,
    method: 'patch',
    data: body
  })
}

export function ajax_update_mock_use_status_by_id(id, status) {
  let body = {
    is_used: status
  }
  return axiosInstance({
    url: '/api/mock/proxy/' + id,
    method: 'patch',
    data: body
  })
}
