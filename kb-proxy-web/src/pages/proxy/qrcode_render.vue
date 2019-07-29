<template>
  <div>
    <div class="q-pa-md" id="qrCode" ref="qrCodeDiv"/>
    <div class="items-center row flex-center q-pl-md q-pr-md q-pb-md">
      <q-btn label="点击下载" color="primary" style="width: 100%" v-on:click="this.download_ca"/>
    </div>
  </div>

</template>

<script>
  import QRCode from 'qrcodejs2'

  export default {
    name: "QRCodeRender",
    data: () => ({
      code: null
    }),
    computed: {
      ca_url() {
        return 'http://' + this.$store.state.user.ip + ':8080/static/ca/kb-proxy-mitm.pem'
      },
    },
    methods: {
      bindQRCode() {
        if (null == this.code)
          this.code = new QRCode(this.$refs.qrCodeDiv, {
            text: this.ca_url,
            width: 200,
            height: 200,
            colorDark: "#333333", //二维码颜色
            colorLight: "#ffffff", //二维码背景色
            correctLevel: QRCode.CorrectLevel.L//容错率，L/M/H
          })
      },
      download_ca() {
        window.open(this.ca_url, '_blank')
      }
    },
  }
</script>
