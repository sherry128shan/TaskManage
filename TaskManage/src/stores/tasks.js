import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { useAuthStore } from './auth'
import {
  STATUS,
  STATUS_LABELS,
  createTask,
  deleteTask,
  listTasks,
  saveTasks,
  updateTask
} from '../data/taskRepository'

export const useTaskStore = defineStore('tasks', () => {
  const auth = useAuthStore()
  const tasks = ref([])
  const isReady = ref(false)
  const openTasks = computed(() => tasks.value.filter((task) => task.status !== STATUS.DONE))
  const doneCount = computed(() => tasks.value.filter((task) => task.status === STATUS.DONE).length)
  const completion = computed(() => (tasks.value.length ? Math.round((doneCount.value / tasks.value.length) * 100) : 0))

  function refresh() {
    if (!auth.user) {
      tasks.value = []
      isReady.value = true
      return
    }
    tasks.value = listTasks(auth.user.id)
      .sort((a, b) => (a.dueDate || '9999-12-31').localeCompare(b.dueDate || '9999-12-31'))
    isReady.value = true
  }

  function add(input) {
    const task = createTask(auth.user.id, input)
    tasks.value = [task, ...tasks.value]
    return task
  }

  function update(id, input) {
    const task = updateTask(auth.user.id, id, input)
    if (!task) return null
    const index = tasks.value.findIndex((item) => item.id === id)
    if (index >= 0) tasks.value[index] = task
    return task
  }

  function toggle(id) {
    const task = tasks.value.find((item) => item.id === id)
    if (!task) return null
    return update(id, { status: task.status === STATUS.DONE ? STATUS.TODO : STATUS.DONE })
  }

  function remove(id) {
    deleteTask(auth.user.id, id)
    tasks.value = tasks.value.filter((task) => task.id !== id)
  }

  function replace(nextTasks) {
    saveTasks(auth.user.id, nextTasks)
    tasks.value = nextTasks
  }

  return {
    tasks,
    openTasks,
    doneCount,
    completion,
    isReady,
    refresh,
    add,
    update,
    toggle,
    remove,
    replace,
    STATUS,
    STATUS_LABELS
  }
})
