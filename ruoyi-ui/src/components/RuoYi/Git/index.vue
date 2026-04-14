<template>
  <div>
    <svg-icon icon-class="github" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'EduPlatformRepo',
  data() {
    return {
      url: 'https://gitee.com/eapp1e/edu-after-school'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>
