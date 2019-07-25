<template>
  <!-- 页眉的第一行是一个QToolbar -->
  <div>
    <q-toolbar style="max-height: 50px">
      <q-icon
        size="30px"
        name="public"/>
      <span class="text-weight-bold q-ml-md">代理工具平台</span>

      <div class="absolute-right row">
        <env-selector ref="EnvSelector"
        @click.native="env_selector_click"></env-selector>

        <q-btn class="full-height shadow-0"
               @click.native=user_info_btn_click
               size="lg"
               icon="account_circle">
          <q-tooltip :offset="[5,5]">{{user_info_btn_tip()}}</q-tooltip>
          <q-popover>
            <user-info ref="UserInfo"/>
          </q-popover>
        </q-btn>
        <q-btn class="full-height shadow-0"
               icon="exit_to_app"
               size="lg"
               @click.native="$store.dispatch('user/logout')"
        >
          <q-tooltip :offset="[5,5]">退出</q-tooltip>
        </q-btn>
      </div>
    </q-toolbar>
  </div>
</template>
<script>
  import UserInfo from '../pages/user/info'
  import EnvSelector from '../pages/env/env_selector'
  export default {
    name: 'header-menu',
    mixins: [],
    components: {UserInfo,EnvSelector},
    props: {
      defaultMiniMenu: false,
    },
    data: () => {
      return {
        show_log_out: false,
        host_ip: null,
      }
    },
    computed: {
      userName() {
        return this.$store.state.user.nick_name
      },
    },
    methods: {
      user_info_btn_click() {
        if (this.userName) {
          this.$refs.UserInfo.refresh_user_info();
        } else {
          this.$router.push(({path: '/login'}))
        }
      },
      user_info_btn_tip() {
        if (this.userName) {
          return '欢迎，' + this.userName
        } else {
          return '登录/注册'
        }
      },
      env_selector_click(){
        this.$refs.EnvSelector.refresh_env_list()
      }
    },
    mounted() {
      this.$nextTick(this.bindQRCode)
    }
  }
</script>
