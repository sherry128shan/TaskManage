<script setup>
import { computed, reactive } from 'vue'
import { onMounted } from 'vue'
import AppNav from '../../components/AppNav.vue'
import { useAlarmStore } from '../../stores/alarms'
import { buildAlarmInput } from '../../native/alarm'
import { todayISO } from '../../utils/date'

const alarmStore = useAlarmStore()
const form = reactive({ title: '', date: todayISO(), time: '09:00' })
const error = computed(() => {
  if (!form.date || !form.time) return 'Choose a date and time.'
  if (buildAlarmInput(form.date, form.time).timestamp <= Date.now()) return 'Choose a time in the future.'
  return ''
})

onMounted(() => alarmStore.refresh())

async function addAlarm() {
  if (error.value) return
  await alarmStore.add({ ...form, ...buildAlarmInput(form.date, form.time) })
  form.title = ''
  uni.showToast({ title: 'Reminder saved', icon: 'success' })
}

function removeAlarm(id) {
  uni.showModal({
    title: 'Delete reminder?',
    content: 'This reminder will be removed from this device.',
    confirmText: 'Delete',
    confirmColor: '#A34C42',
    success: ({ confirm }) => {
      if (confirm) alarmStore.remove(id)
    }
  })
}
</script>

<template>
  <view class="alarm-page safe-bottom">
    <view class="alarm-intro">
      <text class="eyebrow">GENTLE NUDGES</text>
      <text class="page-title">Remember at the right time.</text>
      <text class="page-subtitle">Reminders are stored with your tasks on this device.</text>
    </view>

    <view class="form-card">
      <view class="field-group">
        <text class="field-label">Reminder name <text class="optional">Optional</text></text>
        <input v-model="form.title" class="field-input" placeholder="e.g. Review the outline" placeholder-class="field-placeholder" maxlength="80" />
      </view>
      <view class="split-fields">
        <view class="field-group split-field">
          <text class="field-label">Date</text>
          <picker mode="date" :value="form.date" @change="form.date = $event.detail.value"><view class="picker-field">{{ form.date }}</view></picker>
        </view>
        <view class="field-group split-field">
          <text class="field-label">Time</text>
          <picker mode="time" :value="form.time" @change="form.time = $event.detail.value"><view class="picker-field">{{ form.time }}</view></picker>
        </view>
      </view>
      <text v-if="error" class="field-error">{{ error }}</text>
      <button class="save-button" :disabled="Boolean(error)" @tap="addAlarm">Save reminder</button>
    </view>

    <view class="reminder-list">
      <view class="section-heading"><text>Upcoming</text><text class="section-count">{{ alarmStore.upcoming.length }}</text></view>
      <view v-if="!alarmStore.alarms.length" class="empty-reminders">No reminders yet.</view>
      <view v-for="alarm in alarmStore.alarms" :key="alarm.id" class="reminder-row">
        <view class="reminder-time"><text>{{ alarm.time }}</text><text>{{ alarm.date }}</text></view>
        <text class="reminder-title">{{ alarm.title }}</text>
        <button class="delete-button" aria-label="Delete reminder" @tap="removeAlarm(alarm.id)">×</button>
      </view>
    </view>
    <AppNav active="alarm" />
  </view>
</template>

<style lang="scss">
.alarm-page { min-height: 100vh; padding: 76rpx 40rpx 180rpx; background: #f6f4ee; }
.eyebrow { display: block; color: #477269; font-size: 21rpx; font-weight: 700; letter-spacing: 3rpx; }
.page-title { display: block; margin-top: 18rpx; color: #183b32; font-size: 48rpx; font-weight: 750; line-height: 1.15; letter-spacing: -2rpx; }
.page-subtitle { display: block; margin-top: 18rpx; color: #6c7d75; font-size: 25rpx; line-height: 1.5; }
.form-card { margin-top: 48rpx; padding: 30rpx 28rpx 28rpx; border: 2rpx solid #e4e7dd; border-radius: 30rpx; background: rgba(255, 255, 255, .64); }
.field-group + .field-group { margin-top: 28rpx; }
.field-label { display: block; margin-bottom: 12rpx; color: #36564d; font-size: 24rpx; font-weight: 700; }
.optional { margin-left: 10rpx; color: #8a9a92; font-size: 21rpx; font-weight: 400; }
.field-input, .picker-field { width: 100%; height: 92rpx; padding: 0 22rpx; border: 2rpx solid #d9e0d8; border-radius: 20rpx; background: rgba(255, 255, 255, .78); color: #183b32; font-size: 28rpx; line-height: 92rpx; }
.field-placeholder { color: #8a9a92; }
.split-fields { display: flex; gap: 20rpx; margin-top: 28rpx; }
.split-field { flex: 1; margin-top: 0 !important; }
.field-error { display: block; margin-top: 14rpx; color: #a34c42; font-size: 23rpx; }
.save-button { height: 88rpx; margin-top: 32rpx; border-radius: 20rpx; background: #183b32; color: #fff; font-size: 27rpx; line-height: 88rpx; }
.save-button[disabled] { opacity: .5; }
.reminder-list { margin-top: 58rpx; }
.section-heading { display: flex; align-items: center; gap: 14rpx; color: #36564d; font-size: 25rpx; font-weight: 750; }
.section-count { padding: 4rpx 12rpx; border-radius: 20rpx; background: #e2ece1; color: #477269; font-size: 20rpx; }
.empty-reminders { margin-top: 24rpx; color: #8a9a92; font-size: 25rpx; }
.reminder-row { display: flex; align-items: center; min-height: 118rpx; padding: 22rpx 0; border-bottom: 2rpx solid #e4e7dd; }
.reminder-time { width: 154rpx; flex: none; color: #477269; }
.reminder-time text:first-child { display: block; color: #183b32; font-size: 30rpx; font-weight: 750; }
.reminder-time text:last-child { display: block; margin-top: 5rpx; font-size: 21rpx; }
.reminder-title { overflow: hidden; flex: 1; color: #36564d; font-size: 27rpx; text-overflow: ellipsis; white-space: nowrap; }
.delete-button { width: 68rpx; height: 68rpx; flex: none; margin-right: -10rpx; padding: 0; background: transparent; color: #a34c42; font-size: 44rpx; line-height: 64rpx; }
</style>
