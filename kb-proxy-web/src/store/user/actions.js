import {ajax_login_in, ajax_login_out} from "../../api/user/user_login_api";
import {ajax_get_user_info} from '../../api/user/user_info_api'
import {notify_err, notify_ok} from '../../plugins/PpNotify'
import router from '../../router/index'
import localStorage from '../../utils/local_storage_utils'

const getHello = (name) => {
  let hour = new Date().getHours();
  if (hour < 9)
    return '劳模你好早！';
  else if (hour < 11)
    return '早上好！' + name;
  else if (hour < 12)
    return '你好 ' + name;
  else if (hour < 19)
    return '下午好！' + name;
  else if (hour < 22)
    return '晚上好！';
  else
    return '劳模注意身体啊！'
};

export const getUserInfo = ({state}) => {
  return new Promise((resolve, reject) => {
    state.is_login
      ? resolve()
      : ajax_get_user_info()
        .then(d => {
          if (d.status === 1) {
            state.login_name = d.data.login_name;
            state.nick_name = d.data.nick_name;
            state.ip = d.data.ip
            state.is_login = true
            state.id = d.data.id
            state.status = d.data.status
            resolve()
          } else {
            localStorage.set('isLogin', false);
          }
        })
        .catch((e) => {
          // notify_err('获取用户信息异常');
          // reject(e)
        })
  })
};

export const refreshUserInfo = ({state}) => {
  return new Promise((resolve, reject) => {
    ajax_get_user_info()
      .then(d => {
        if (d.status === 1) {
          state.login_name = d.data.login_name;
          state.nick_name = d.data.nick_name;
          state.ip = d.data.ip
          state.id = d.data.id
          state.is_login = true
          state.status = d.data.status
          resolve()
        } else {
          localStorage.set('isLogin', false);
        }
      })
      .catch((e) => {
        // notify_err('获取用户信息异常');
        // reject(e)
      })
  })
};


export const clearLoginState = ({state}) => {
  return new Promise((r, j) => {
    state.is_login = false;
    r();
    router.push({name: "Login"})
  });
};

export const login = (context, form) => {
  return new Promise((r, j) => {
    ajax_login_in(form)
      .then((d) => {
        if (d.status === 1) {
          localStorage.set('isLogin', true);
          getUserInfo(context)
            .then(() => {
              r();
              notify_ok(getHello(context.state.nick_name));
              router.push({path: "/"});
            })
            .catch(j)
        } else {
          notify_err(d.message)
        }

      })
      .catch(j);
  });
};

export const logout = (context) => {
  return new Promise((r, j) => {
    ajax_login_out()
      .then(() => {
        notify_ok('已登出系统');
        localStorage.set('isLogin', false);
        clearLoginState(context).then(r).catch(j)
        router.push({path: '/login'})
      })
      .catch(j)

  })
};
