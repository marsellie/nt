<template>
  <v-dialog max-width="450" v-model="dialogModel">
    <v-card tile flat color="#add8e6">
      <v-card-title v-if="isLogin">
        <v-spacer/>
        Вход
        <v-spacer/>
        <v-btn @click="isLogin=false" text :ripple="false">Регистрация
          <v-icon>keyboard_arrow_right</v-icon>
        </v-btn>
      </v-card-title>

      <v-card-title v-if="!isLogin">
        <v-btn @click="isLogin=true" text :ripple="false">
          <v-icon>keyboard_arrow_left</v-icon>
          Вход
        </v-btn>
        <v-spacer/>
        Регистрация
        <v-spacer/>
      </v-card-title>

      <v-row class="mx-4 py-4">
        <v-divider/>
      </v-row>

      <login v-if="isLogin"></login>
      <registration v-if="!isLogin"/>
    </v-card>
  </v-dialog>
</template>

<script>
import {mapActions, mapState} from 'vuex'
import Login from "./Login.vue"
import Registration from "./Registration.vue";

export default {
  components: {Login, Registration},
  computed: {
    ...mapState('app', ['isAuthFormOpen']),
    dialogModel: {
      get: function () {
        return this.isAuthFormOpen
      },
      set: function (value) {
        this.setAuthForm(value)
      }
    },
  },
  data() {
    return {
      isLogin: true,
    }
  },
  methods: {
    ...mapActions('app', ["setAuthForm"])
  },
}
</script>

<style>

</style>