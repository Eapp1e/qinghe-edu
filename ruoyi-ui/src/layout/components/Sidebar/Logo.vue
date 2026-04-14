<template>
  <div
    class="sidebar-logo-container"
    :class="{ collapse: collapse }"
    :style="{ backgroundColor: sideTheme === 'theme-dark' && navType !== 3 ? variables.menuBackground : variables.menuLightBackground }"
  >
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <span
          class="sidebar-title only-text"
          :style="{ color: sideTheme === 'theme-dark' && navType !== 3 ? variables.logoTitleColor : variables.logoLightTitleColor }"
        >
          课
        </span>
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <span
          class="sidebar-title"
          :style="{ color: sideTheme === 'theme-dark' && navType !== 3 ? variables.logoTitleColor : variables.logoLightTitleColor }"
        >
          课后服务平台
        </span>
      </router-link>
    </transition>
  </div>
</template>

<script>
import variables from '@/assets/styles/variables.scss'

export default {
  name: 'SidebarLogo',
  props: {
    collapse: {
      type: Boolean,
      required: true
    }
  },
  computed: {
    variables() {
      return variables
    },
    sideTheme() {
      return this.$store.state.settings.sideTheme
    },
    navType() {
      return this.$store.state.settings.navType
    }
  }
}
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 0.45s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  height: 50px;
  padding: 0;
  overflow: hidden;

  .sidebar-logo-link {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 50px;
    margin: 0;
    padding: 0 16px;
    border-radius: 0;
    background: linear-gradient(135deg, rgba(255, 250, 240, 0.08) 0%, rgba(145, 194, 110, 0.12) 100%);
    overflow: hidden;
  }

  .sidebar-title {
    display: block;
    margin: 0;
    font-size: 16px;
    font-weight: 700;
    line-height: 16px;
    letter-spacing: 0.2px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    text-align: center;
  }

  .sidebar-title.only-text {
    font-size: 18px;
    line-height: 18px;
  }

  &.collapse {
    padding: 0;

    .sidebar-logo-link {
      width: 40px;
      height: 40px;
      margin: 5px auto;
      padding: 0;
      border-radius: 10px;
    }
  }
}
</style>
