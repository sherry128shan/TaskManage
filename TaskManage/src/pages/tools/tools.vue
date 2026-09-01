<script setup>
import { ref } from 'vue'
import { onMounted } from 'vue'
import AppNav from '../../components/AppNav.vue'
import { setTorch } from '../../native/torch'

const platform = ref('web')
const torchOn = ref(false)
const cameraError = ref('')
const isDevice = ref(false)

onMounted(() => {
  const info = uni.getSystemInfoSync()
  platform.value = info.platform || 'web'
  isDevice.value = platform.value === 'android' || platform.value === 'ios'
})

async function toggleTorch() {
  const result = await setTorch(!torchOn.value)
  if (!result.supported) {
    uni.showToast({ title: result.message || 'Torch is not available here.', icon: 'none' })
    return
  }
  torchOn.value = !torchOn.value
}
</script>

<template>
  <view class="tools-page safe-bottom">
    <view class="tools-intro">
      <text class="eyebrow">SMALL UTILITIES</text>
      <text class="page-title">Tools for the in-between moments.</text>
      <text class="page-subtitle">These helpers stay out of the way until you need them.</text>
    </view>

    <view class="tool-panel camera-panel">
      <view class="panel-head">
        <view><text class="panel-kicker">MIRROR</text><text class="panel-title">A front-facing view</text></view>
        <text class="panel-status">{{ isDevice ? 'Device' : 'Preview' }}</text>
      </view>
      <view class="camera-frame">
        <camera v-if="isDevice" device-position="front" flash="off" class="camera-view" @error="cameraError = 'Camera permission is needed for the mirror.'" />
        <view v-else class="camera-placeholder"><text class="placeholder-icon">◌</text><text>Camera preview appears on an Android or iOS device.</text></view>
      </view>
      <text v-if="cameraError" class="tool-error">{{ cameraError }}</text>
    </view>

    <view class="tool-panel torch-panel">
      <view class="panel-head">
        <view><text class="panel-kicker">FLASHLIGHT</text><text class="panel-title">A little more light</text></view>
        <view class="torch-indicator" :class="{ on: torchOn }" />
      </view>
      <text class="panel-copy">Use the back camera light without leaving your task list.</text>
      <button class="tool-button" :class="{ active: torchOn }" @tap="toggleTorch">{{ torchOn ? 'Turn light off' : 'Turn light on' }}</button>
    </view>

    <AppNav active="tools" />
  </view>
</template>

<style lang="scss">
.tools-page {
  min-height: 100vh;
  padding: 76rpx 40rpx 180rpx;
  background: #f6f4ee;
}

.eyebrow,
.panel-kicker {
  display: block;
  color: #477269;
  font-size: 21rpx;
  font-weight: 700;
  letter-spacing: 3rpx;
}

.page-title {
  display: block;
  margin-top: 18rpx;
  color: #183b32;
  font-size: 48rpx;
  font-weight: 750;
  line-height: 1.15;
  letter-spacing: -2rpx;
}

.page-subtitle { display: block; margin-top: 18rpx; color: #6c7d75; font-size: 25rpx; line-height: 1.5; }

.tool-panel {
  margin-top: 48rpx;
  padding: 28rpx;
  border: 2rpx solid #e4e7dd;
  border-radius: 30rpx;
  background: rgba(255, 255, 255, .62);
}

.torch-panel { margin-top: 24rpx; }

.panel-head { display: flex; align-items: flex-start; justify-content: space-between; }
.panel-title { display: block; margin-top: 10rpx; color: #183b32; font-size: 31rpx; font-weight: 700; }
.panel-status { padding: 8rpx 14rpx; border-radius: 18rpx; background: #e4efe2; color: #477269; font-size: 20rpx; }

.camera-frame { height: 480rpx; margin-top: 26rpx; overflow: hidden; border-radius: 24rpx; background: #dce4da; }
.camera-view { width: 100%; height: 100%; }
.camera-placeholder { display: flex; height: 100%; flex-direction: column; align-items: center; justify-content: center; padding: 48rpx; color: #477269; font-size: 24rpx; line-height: 1.5; text-align: center; }
.placeholder-icon { display: block; margin-bottom: 20rpx; color: #183b32; font-size: 80rpx; }
.tool-error { display: block; margin-top: 16rpx; color: #a34c42; font-size: 23rpx; }
.panel-copy { display: block; margin-top: 22rpx; color: #6c7d75; font-size: 24rpx; line-height: 1.5; }

.torch-indicator { width: 24rpx; height: 24rpx; margin-top: 10rpx; border-radius: 50%; background: #c8d1ca; }
.torch-indicator.on { background: #d2a24d; box-shadow: 0 0 22rpx rgba(210, 162, 77, .65); }

.tool-button { height: 88rpx; margin-top: 28rpx; border-radius: 20rpx; background: #183b32; color: #fff; font-size: 27rpx; line-height: 88rpx; }
.tool-button.active { background: #a34c42; }
</style>
