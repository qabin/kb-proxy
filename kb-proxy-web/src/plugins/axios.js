import axios from 'axios'

import {Loading, QSpinnerBars} from 'quasar-framework/dist/quasar.mat.esm'

const axiosInstance = axios.create({
  timeout: 60000
})

axiosInstance.interceptors.request.use(
  config => {
    Loading.show({
      spinner: QSpinnerBars,
      delay: 200,
      customClass: 'grey-1',
      spinnerColor: 'white',
      spinnerSize: 70
    });
    return config
  },
  error => {
    return Promise.reject(error)
  });


// respone interceptor
axiosInstance.interceptors.response.use(
  response => {
    Loading.hide();
    const res = response.data;
    // 1001:未登录;1002:toke异常
    // if (res.status === 1001 || res.status === 1002) {
    //   // vm.$q.warning(res.message);
    //   // store.dispatch('FedLogOut').then(() => {
    //   //   location.reload();
    //   // });
    //   // return Promise.reject('error');
    // } else {
    //   return response.data;
    // }
    return response.data;
  },
  error => {
    Loading.hide();
    return Promise.reject(error)
  });

export {
  axiosInstance
}
