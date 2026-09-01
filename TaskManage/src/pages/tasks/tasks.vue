<script setup>
import { computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { useTaskStore } from '../../stores/tasks'
import { formatDate } from '../../utils/date'
import AppNav from '../../components/AppNav.vue'

const auth = useAuthStore()
const taskStore = useTaskStore()
const username = computed(() => auth.user?.username?.split('@')[0] || 'there')
const activeTasks = computed(() => taskStore.tasks.filter((task) => task.status !== taskStore.STATUS.DONE))
const finishedTasks = computed(() => taskStore.tasks.filter((task) => task.status === taskStore.STATUS.DONE))

onMounted(() => {
  if (!auth.hydrated) auth.hydrate()
  ensureSession()
  taskStore.refresh()
})

function ensureSession() {
  if (!auth.isLoggedIn) uni.reLaunch({ url: '/pages/login/login' })
}

function openCreate() {
  uni.navigateTo({ url: '/pages/tasks/edit' })
}

function openEdit(task) {
  uni.navigateTo({ url: `/pages/tasks/edit?id=${encodeURIComponent(task.id)}` })
}

function showActions(task) {
  uni.showActionSheet({ itemList: ['Edit task', 'Delete task'] }).then(({ tapIndex }) => {
    if (tapIndex === 0) openEdit(task)
    if (tapIndex === 1) confirmDelete(task)
  }).catch(() => {})
}

function confirmDelete(task) {
  uni.showModal({
    title: 'Delete task?',
    content: 'This task will be removed from this device.',
    confirmText: 'Delete',
    confirmColor: '#A34C42'
  }).then(({ confirm }) => {
    if (confirm) {
      taskStore.remove(task.id)
      uni.showToast({ title: 'Task deleted', icon: 'none' })
    }
  })
}

function toggle(task) {
  taskStore.toggle(task.id)
}
</script>

<template>
  <view class="tasks-page safe-bottom">
    <view class="tasks-header">
      <view>
        <text class="eyebrow">YOUR DAY, {{ username.toUpperCase() }}</text>
        <text class="page-title">Make progress visible.</text>
      </view>
      <view class="progress-orb">
        <text class="progress-value">{{ taskStore.completion }}%</text>
        <text class="progress-label">done</text>
      </view>
    </view>

    <view class="summary-line">
      <text>{{ activeTasks.length }} open {{ activeTasks.length === 1 ? 'task' : 'tasks' }}</text>
      <text class="summary-dot">·</text>
      <text>{{ taskStore.doneCount }} finished</text>
    </view>

    <view v-if="!taskStore.isReady" class="state-card">
      <text class="state-title">Loading your tasks…</text>
    </view>

    <view v-else-if="taskStore.tasks.length === 0" class="empty-state">
      <view class="empty-symbol">＋</view>
      <text class="state-title">Start with one small thing.</text>
      <text class="state-copy">Add a task and give it a clear next step. You can edit or delete it anytime.</text>
      <button class="primary-button compact" @tap="openCreate">Create first task</button>
    </view>

    <view v-else class="task-sections">
      <view v-if="activeTasks.length" class="task-section">
        <view class="section-heading">
          <text>Open tasks</text>
          <text class="section-count">{{ activeTasks.length }}</text>
        </view>
        <view v-for="task in activeTasks" :key="task.id" class="task-row" @tap="openEdit(task)">
          <button class="check-button" :class="{ checked: task.status === taskStore.STATUS.DONE }" :aria-label="`Mark ${task.title} complete`" @tap.stop="toggle(task)">
            <text v-if="task.status === taskStore.STATUS.DONE">✓</text>
          </button>
          <view class="task-copy">
            <text class="task-title">{{ task.title }}</text>
            <text class="task-meta">{{ task.description || 'No description' }}</text>
            <text class="task-date">{{ formatDate(task.dueDate) }} · {{ taskStore.STATUS_LABELS[task.status] }}</text>
          </view>
          <button class="more-button" aria-label="More actions" @tap.stop="showActions(task)">•••</button>
        </view>
      </view>

      <view v-if="finishedTasks.length" class="task-section finished-section">
        <view class="section-heading">
          <text>Finished</text>
          <text class="section-count">{{ finishedTasks.length }}</text>
        </view>
        <view v-for="task in finishedTasks" :key="task.id" class="task-row finished-row" @tap="openEdit(task)">
          <button class="check-button checked" :aria-label="`Mark ${task.title} open`" @tap.stop="toggle(task)">✓</button>
          <view class="task-copy">
            <text class="task-title">{{ task.title }}</text>
            <text class="task-date">{{ formatDate(task.dueDate) }} · Finished</text>
          </view>
          <button class="more-button" aria-label="More actions" @tap.stop="showActions(task)">•••</button>
        </view>
      </view>
    </view>

    <button v-if="taskStore.tasks.length" class="floating-add" aria-label="Create task" @tap="openCreate">＋</button>
    <AppNav active="tasks" />
  </view>
</template>

<style lang="scss">
.tasks-page {
  min-height: 100vh;
  padding: 84rpx 40rpx 180rpx;
  background: #f6f4ee;
}

.tasks-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.eyebrow {
  display: block;
  color: #477269;
  font-size: 21rpx;
  font-weight: 700;
  letter-spacing: 3rpx;
}

.page-title {
  display: block;
  max-width: 470rpx;
  margin-top: 16rpx;
  color: #183b32;
  font-size: 54rpx;
  font-weight: 750;
  line-height: 1.12;
  letter-spacing: -2rpx;
}

.progress-orb {
  display: flex;
  width: 132rpx;
  height: 132rpx;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 2rpx solid #b8d1be;
  border-radius: 50%;
  background: #e4efe2;
}

.progress-value {
  color: #183b32;
  font-size: 32rpx;
  font-weight: 800;
}

.progress-label {
  margin-top: 4rpx;
  color: #477269;
  font-size: 20rpx;
}

.summary-line {
  display: flex;
  gap: 14rpx;
  margin-top: 28rpx;
  color: #6c7d75;
  font-size: 25rpx;
}

.summary-dot { color: #a9b9ad; }

.state-card,
.empty-state {
  margin-top: 80rpx;
  padding: 56rpx 36rpx;
  border: 2rpx solid #e4e7dd;
  border-radius: 28rpx;
  background: rgba(255, 255, 255, .58);
  text-align: center;
}

.empty-symbol {
  width: 80rpx;
  height: 80rpx;
  margin: 0 auto 28rpx;
  border-radius: 24rpx;
  background: #183b32;
  color: #fff;
  font-size: 48rpx;
  line-height: 78rpx;
}

.state-title {
  display: block;
  color: #183b32;
  font-size: 32rpx;
  font-weight: 700;
}

.state-copy {
  display: block;
  margin: 18rpx auto 0;
  color: #6c7d75;
  font-size: 25rpx;
  line-height: 1.55;
}

.primary-button.compact {
  width: 100%;
  height: 88rpx;
  margin-top: 34rpx;
  border-radius: 20rpx;
  background: #183b32;
  color: #fff;
  font-size: 27rpx;
  line-height: 88rpx;
}

.task-sections { margin-top: 64rpx; }

.task-section + .task-section { margin-top: 58rpx; }

.section-heading {
  display: flex;
  align-items: center;
  gap: 14rpx;
  margin-bottom: 18rpx;
  color: #36564d;
  font-size: 25rpx;
  font-weight: 750;
  letter-spacing: 1rpx;
}

.section-count {
  min-width: 36rpx;
  padding: 4rpx 10rpx;
  border-radius: 20rpx;
  background: #e2ece1;
  color: #477269;
  font-size: 20rpx;
  text-align: center;
}

.task-row {
  display: flex;
  align-items: flex-start;
  min-height: 146rpx;
  padding: 26rpx 0;
  border-bottom: 2rpx solid #e4e7dd;
}

.check-button {
  display: flex;
  width: 54rpx;
  height: 54rpx;
  flex: none;
  align-items: center;
  justify-content: center;
  margin: 4rpx 22rpx 0 0;
  padding: 0;
  border: 3rpx solid #9bb5a6;
  border-radius: 50%;
  background: transparent;
  color: #fff;
  font-size: 30rpx;
  font-weight: 800;
  line-height: 48rpx;
}

.check-button.checked { border-color: #183b32; background: #183b32; }

.task-copy { min-width: 0; flex: 1; }

.task-title {
  display: block;
  overflow: hidden;
  color: #183b32;
  font-size: 30rpx;
  font-weight: 700;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.task-meta {
  display: block;
  overflow: hidden;
  margin-top: 8rpx;
  color: #71827a;
  font-size: 23rpx;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.task-date {
  display: block;
  margin-top: 12rpx;
  color: #477269;
  font-size: 21rpx;
}

.more-button {
  width: 72rpx;
  height: 64rpx;
  flex: none;
  margin: -6rpx -12rpx 0 8rpx;
  padding: 0;
  background: transparent;
  color: #7b8b84;
  font-size: 28rpx;
  line-height: 64rpx;
}

.finished-row .task-title { color: #7b8b84; text-decoration: line-through; }
.finished-row .task-date { color: #8a9a92; }

.floating-add {
  position: fixed;
  z-index: 11;
  right: 40rpx;
  bottom: 148rpx;
  width: 112rpx;
  height: 112rpx;
  border-radius: 36rpx;
  background: #183b32;
  color: #fff;
  font-size: 54rpx;
  line-height: 108rpx;
  box-shadow: 0 18rpx 36rpx rgba(24, 59, 50, .22);
}
</style>
