import {axiosInstance} from "../../../plugins/axios";

export function ajax_stop_proxy_server() {
  return axiosInstance({
    url: '/api/proxy/_stop',
    method: 'get',
  })
}


export function ajax_start_proxy_server() {
  return axiosInstance({
    url: '/api/proxy/_start',
    method: 'get',
  })
}
