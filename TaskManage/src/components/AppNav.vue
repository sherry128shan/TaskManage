<script setup>
import { useAuthStore } from '../stores/auth'

defineProps({ active: { type: String, default: 'tasks' } })
const auth = useAuthStore()

function go(path) {
  if (path === 'logout') {
    auth.logout()
    uni.reLaunch({ url: '/pages/login/login' })
    return
  }
  if (path === 'tasks') uni.reLaunch({ url: '/pages/tasks/tasks' })
  if (path === 'tools') uni.navigateTo({ url: '/pages/tools/tools' })
  if (path === 'alarm') uni.navigateTo({ url: '/pages/alarm/alarm' })
}
</script>

<template>
  <view class="app-nav safe-bottom">
    <button class="nav-item" :class="{ active: active === 'tasks' }" aria-label="Tasks" @tap="go('tasks')">
      <view class="nav-icon icon-tasks"><view /><view /><view /><view /></view>
      <text>Tasks</text>
    </button>
    <button class="nav-item" :class="{ active: active === 'alarm' }" aria-label="Reminders" @tap="go('alarm')">
      <view class="nav-icon icon-alarm" />
      <text>Reminders</text>
    </button>
    <button class="nav-item" :class="{ active: active === 'tools' }" aria-label="Tools" @tap="go('tools')">
      <view class="nav-icon icon-tools"><view /><view /><view /><view /></view>
      <text>Tools</text>
    </button>
    <button class="nav-item" aria-label="Sign out" @tap="go('logout')">
      <view class="nav-icon icon-exit" />
      <text>Sign out</text>
    </button>
  </view>
</template>

<style lang="scss">
.app-nav {
  position: fixed;
  z-index: 10;
  right: 0;
  bottom: 0;
  left: 0;
  display: flex;
  justify-content: space-around;
  padding-top: 14rpx;
  background: rgba(246, 244, 238, .96);
  border-top: 2rpx solid #e5e5dc;
  backdrop-filter: blur(14px);
}

.nav-item {
  display: flex;
  flex: 1;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
  min-height: 84rpx;
  padding: 0;
  background: transparent;
  color: #8a9a92;
  font-size: 21rpx;
  line-height: 1.2;
}

.nav-item.active {
  color: #183b32;
  font-weight: 700;
}

.nav-icon {
  position: relative;
  display: flex;
  height: 34rpx;
  width: 34rpx;
  align-items: center;
  justify-content: center;
  color: currentColor;
}

.icon-tasks { flex-wrap: wrap; gap: 3rpx; padding: 3rpx; }
.icon-tasks view, .icon-tools view { width: 12rpx; height: 12rpx; border-radius: 3rpx; background: currentColor; }
.icon-alarm { border: 3rpx solid currentColor; border-radius: 50%; }
.icon-alarm::after { position: absolute; width: 3rpx; height: 10rpx; background: currentColor; content: ''; transform: translateY(-4rpx); }
.icon-alarm::before { position: absolute; width: 10rpx; height: 3rpx; background: currentColor; content: ''; transform: translate(4rpx, 3rpx); }
.icon-exit { border: 3rpx solid currentColor; border-right: 0; border-radius: 7rpx 0 0 7rpx; }
.icon-exit::after { position: absolute; right: -3rpx; width: 19rpx; height: 3rpx; background: currentColor; content: ''; }
.icon-exit::before { position: absolute; right: -4rpx; width: 9rpx; height: 9rpx; border-top: 3rpx solid currentColor; border-right: 3rpx solid currentColor; content: ''; transform: rotate(45deg); }
</style>
