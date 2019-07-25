import Vue from 'vue'
import Vuex from 'vuex'
import repeater from './repeater'
import mock from './mock'
import user from './user'
import env from './env'

Vue.use(Vuex);

const store = new Vuex.Store({
  modules: {repeater, mock, user, env},
});

export default store
