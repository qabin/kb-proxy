import {axiosInstance} from "../../../plugins/axios";

export function ajax_add_folder(name) {
  let form = {
    name: name
  }
  return axiosInstance({
    url: '/api/folders',
    method: 'post',
    data: form
  })
}

export function ajax_delete_folder(id) {
  return axiosInstance({
    url: '/api/folders/' + id,
    method: 'delete',
  })
}

export function ajax_update_folder(id, name) {
  let form = {
    name: name,
    id: id
  }
  return axiosInstance({
    url: '/api/folders/' + id,
    method: 'patch',
    data: form
  })
}

export function ajax_get_folder_list(kw) {
  let form = {
    kw: kw,
  }
  return axiosInstance({
    url: '/api/folders/_search',
    method: 'get',
    params: form
  })
}
