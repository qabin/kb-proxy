<template>
  <q-layout view="hHr LpR lFf">
    <!--head-->
    <q-layout-header>
      <keep-alive>
        <router-view name="header"></router-view>
      </keep-alive>
    </q-layout-header>
    <!--left menu-->
    <q-layout-drawer q-layout-drawer class="pp-drawer" side="left"
                     mini
                     :mini-width="miniWidth"
                     :content-style="{maxWidth: '200px'}"
                     noHideOnRouteChange
                     behavior="desktop"
                     v-model="showLeft">
      <keep-alive>
        <router-view name="left"></router-view>
      </keep-alive>
    </q-layout-drawer>
    <q-page-container style="padding-top: 0px">
      <!--bread crumbs(path bar)-->
      <div class="flex no-wrap items-center">
        <keep-alive>
          <router-view name="path"></router-view>
        </keep-alive>
      </div>
      <!--main page-->
      <keep-alive>
        <router-view name="page"></router-view>
      </keep-alive>
    </q-page-container>
    <keep-alive>
      <router-view name="footer"></router-view>
    </keep-alive>
  </q-layout>
</template>

<script>
  export default {
    name: "index",
    data: () => {
      return {
        showLeft: true,
        miniWidth: 50
      }
    },
    mounted() {
      this.$store.dispatch('user/getUserInfo').then().catch();
    }
  }
</script>

<style scoped>
</style>
