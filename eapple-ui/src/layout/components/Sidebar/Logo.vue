<template>
  <div class="sidebar-logo-container" :class="{ collapse: collapse }">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <span class="sidebar-title only-text">课</span>
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <span class="sidebar-title">课后服务平台</span>
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
    radial-gradient(circle at top left, rgba(31, 228, 190, 0.08), transparent 34%),
    linear-gradient(180deg, #26352d 0%, #223028 100%);
  box-shadow: inset 0 -1px 0 rgba(255, 255, 255, 0.03);

  .sidebar-logo-link {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    margin: 0;
    padding: 0 14px;
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
    font-size: 15px;
    font-size: 17px;
    font-weight: 700;
    line-height: 1;
    letter-spacing: 0.02em;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    text-align: center;
    color: #f5fbf8;
    text-shadow: 0 1px 10px rgba(0, 0, 0, 0.16);
  }

  .sidebar-title.only-text {
    font-size: 18px;
  }

  &.collapse {
    .sidebar-logo-link {
      width: 36px;
      height: 36px;
      margin: 8px auto;
      padding: 0;
      border-radius: 10px;
      border: 1px solid rgba(95, 222, 214, 0.16);
      background: linear-gradient(180deg, rgba(44, 60, 52, 0.96), rgba(35, 48, 41, 0.94));
    }
  }
}
</style>
