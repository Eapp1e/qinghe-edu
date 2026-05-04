<template>
  <div class="sidebar-logo-container" :class="[{ collapse: collapse }, `palette-${paletteMode}`]">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <span class="sidebar-title only-text">&#38738;</span>
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <span class="sidebar-title">&#38738;&#31166;&#26234;&#23398;</span>
      </router-link>
    </transition>
  </div>
</template>

<script>
export default {
  name: 'SidebarLogo',
  props: {
    collapse: {
      type: Boolean,
      required: true
    },
    paletteMode: {
      type: String,
      default: 'emerald'
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
  display: flex;
  align-items: center;
  justify-content: center;
  height: 58px;
  padding: 0;
  overflow: hidden;
  border-bottom: 1px solid rgba(95, 222, 214, 0.12);
  background:
    radial-gradient(circle at top left, rgba(186, 219, 157, 0.08), transparent 34%),
    linear-gradient(180deg, #193930 0%, #153229 100%);
  box-shadow: inset 0 -1px 0 rgba(255, 255, 255, 0.03);

  .sidebar-logo-link {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    margin: 0;
    padding: 0 18px;
    background: transparent;
    overflow: hidden;
    text-decoration: none;
  }

  .sidebar-title {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    margin: 0;
    padding-top: 1px;
    font-family: "STZhongsong", "Noto Serif SC", "Source Han Serif SC", "Songti SC", serif;
    font-size: 24px;
    font-weight: 700;
    line-height: 1;
    letter-spacing: 0.1em;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    text-align: center;
    color: #f7fbf6;
    text-shadow:
      0 1px 0 rgba(255, 255, 255, 0.04),
      0 2px 12px rgba(0, 0, 0, 0.16);
  }

  .sidebar-title.only-text {
    font-size: 20px;
    letter-spacing: 0.06em;
  }

  &.collapse {
    .sidebar-logo-link {
      width: 36px;
      height: 36px;
      margin: 8px auto;
      padding: 0;
      border-radius: 10px;
      border: 1px solid rgba(95, 222, 214, 0.16);
      background: linear-gradient(180deg, rgba(28, 60, 49, 0.96), rgba(22, 47, 39, 0.94));
    }
  }

  &.palette-classic {
    border-bottom-color: rgba(132, 170, 144, 0.14);
    background:
      radial-gradient(circle at top left, rgba(158, 191, 143, 0.08), transparent 34%),
      linear-gradient(180deg, #233128 0%, #1d2821 100%);

    &.collapse {
      .sidebar-logo-link {
        border-color: rgba(142, 180, 153, 0.16);
        background: linear-gradient(180deg, rgba(39, 54, 44, 0.96), rgba(30, 41, 34, 0.94));
      }
    }
  }
}
</style>
