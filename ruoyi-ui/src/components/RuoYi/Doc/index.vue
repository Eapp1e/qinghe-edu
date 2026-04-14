<template>
  <div>
    <svg-icon icon-class="question" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'EduPlatformDoc',
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
