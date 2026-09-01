<script setup>
import { computed, reactive, ref } from 'vue'
import { onMounted } from 'vue'
import { useTaskStore } from '../../stores/tasks'
import { STATUS } from '../../data/taskRepository'
import { todayISO } from '../../utils/date'

const taskStore = useTaskStore()
const editingId = ref('')
const errors = reactive({ title: '', dueDate: '' })
const form = reactive({
  title: '',
  description: '',
  dueDate: todayISO(),
  status: STATUS.TODO
})
const isEditing = computed(() => Boolean(editingId.value))
const statusOptions = [
  { label: 'Not started', value: STATUS.TODO },
  { label: 'In progress', value: STATUS.DOING },
  { label: 'Finished', value: STATUS.DONE }
]
const statusIndex = computed(() => Math.max(0, statusOptions.findIndex((item) => item.value === form.status)))

onMounted(() => {
  const pages = getCurrentPages()
  const current = pages[pages.length - 1]
  const options = current?.options || {}
  editingId.value = options?.id || ''
  if (editingId.value) {
    const task = taskStore.tasks.find((item) => item.id === editingId.value)
    if (task) {
      form.title = task.title
      form.description = task.description
      form.dueDate = task.dueDate || todayISO()
      form.status = task.status
      uni.setNavigationBarTitle({ title: 'Edit task' })
    }
  } else {
    uni.setNavigationBarTitle({ title: 'New task' })
  }
})

function chooseStatus(event) {
  form.status = statusOptions[event.detail.value].value
}

function validate() {
  errors.title = form.title.trim() ? '' : 'Give this task a short, clear title.'
  errors.dueDate = form.dueDate ? '' : 'Choose a due date or remove the date.'
  return !errors.title && !errors.dueDate
}

function save() {
  if (!validate()) return
  const payload = {
    title: form.title,
    description: form.description,
    dueDate: form.dueDate,
    status: form.status
  }
  if (isEditing.value) taskStore.update(editingId.value, payload)
  else taskStore.add(payload)
  uni.showToast({ title: isEditing.value ? 'Task updated' : 'Task added', icon: 'success' })
  setTimeout(() => uni.navigateBack(), 220)
}
</script>

<template>
  <view class="edit-page safe-bottom">
    <view class="edit-intro">
      <text class="eyebrow">{{ isEditing ? 'REFINE THE NEXT STEP' : 'A SMALL STEP COUNTS' }}</text>
      <text class="edit-title">{{ isEditing ? 'Keep the plan honest.' : 'What needs your attention?' }}</text>
      <text class="edit-subtitle">A title is required. Everything else can stay lightweight.</text>
    </view>

    <view class="form-card">
      <view class="field-group">
        <text class="field-label">Task title <text class="required">*</text></text>
        <input v-model="form.title" class="field-input" placeholder="e.g. Outline the presentation" placeholder-class="field-placeholder" maxlength="80" />
        <text v-if="errors.title" class="field-error">{{ errors.title }}</text>
      </view>

      <view class="field-group">
        <text class="field-label">Description <text class="optional">Optional</text></text>
        <textarea v-model="form.description" class="field-textarea" placeholder="What does done look like?" placeholder-class="field-placeholder" maxlength="300" auto-height />
      </view>

      <view class="field-group">
        <text class="field-label">Due date</text>
        <picker mode="date" :value="form.dueDate" @change="form.dueDate = $event.detail.value">
          <view class="picker-field"><text>{{ form.dueDate || 'Choose a date' }}</text><text class="picker-arrow">→</text></view>
        </picker>
        <text v-if="errors.dueDate" class="field-error">{{ errors.dueDate }}</text>
      </view>

      <view class="field-group">
        <text class="field-label">Progress</text>
        <picker mode="selector" :range="statusOptions" range-key="label" :value="statusIndex" @change="chooseStatus">
          <view class="picker-field"><text>{{ taskStore.STATUS_LABELS[form.status] }}</text><text class="picker-arrow">→</text></view>
        </picker>
      </view>

      <button class="save-button" @tap="save">{{ isEditing ? 'Save changes' : 'Add task' }}</button>
      <button class="cancel-button" @tap="uni.navigateBack()">Cancel</button>
    </view>
  </view>
</template>

<style lang="scss">
.edit-page {
  min-height: 100vh;
  padding: 74rpx 40rpx 60rpx;
  background: #f6f4ee;
}

.eyebrow {
  display: block;
  color: #477269;
  font-size: 21rpx;
  font-weight: 700;
  letter-spacing: 3rpx;
}

.edit-title {
  display: block;
  margin-top: 18rpx;
  color: #183b32;
  font-size: 50rpx;
  font-weight: 750;
  line-height: 1.15;
  letter-spacing: -2rpx;
}

.edit-subtitle {
  display: block;
  margin-top: 18rpx;
  color: #6c7d75;
  font-size: 25rpx;
  line-height: 1.55;
}

.form-card {
  margin-top: 56rpx;
  padding: 34rpx 28rpx 28rpx;
  border: 2rpx solid #e4e7dd;
  border-radius: 30rpx;
  background: rgba(255, 255, 255, .64);
}

.field-group + .field-group { margin-top: 34rpx; }

.field-label {
  display: block;
  margin-bottom: 12rpx;
  color: #36564d;
  font-size: 24rpx;
  font-weight: 700;
}

.required { color: #a34c42; }
.optional { margin-left: 10rpx; color: #8a9a92; font-size: 21rpx; font-weight: 400; }

.field-input,
.field-textarea,
.picker-field {
  width: 100%;
  border: 2rpx solid #d9e0d8;
  border-radius: 20rpx;
  background: rgba(255, 255, 255, .78);
  color: #183b32;
  font-size: 29rpx;
}

.field-input { height: 96rpx; padding: 0 24rpx; }
.field-textarea { min-height: 150rpx; padding: 22rpx 24rpx; line-height: 1.5; }
.field-placeholder { color: #8a9a92; }
.field-error { display: block; margin-top: 10rpx; color: #a34c42; font-size: 23rpx; }

.picker-field {
  display: flex;
  height: 96rpx;
  align-items: center;
  justify-content: space-between;
  padding: 0 24rpx;
}

.picker-arrow { color: #477269; font-size: 34rpx; }

.save-button,
.cancel-button {
  width: 100%;
  height: 92rpx;
  border-radius: 22rpx;
  font-size: 28rpx;
  line-height: 92rpx;
}

.save-button { margin-top: 42rpx; background: #183b32; color: #fff; font-weight: 700; }
.cancel-button { margin-top: 12rpx; background: transparent; color: #477269; }
</style>
