import { readStorage, writeStorage } from '../utils/storage'
import { createId } from '../utils/ids'

function storageKey(userId) {
  return `tasks:${userId}`
}

export const STATUS = Object.freeze({
  TODO: 'todo',
  DOING: 'doing',
  DONE: 'done'
})

export const STATUS_LABELS = Object.freeze({
  [STATUS.TODO]: 'Not started',
  [STATUS.DOING]: 'In progress',
  [STATUS.DONE]: 'Finished'
})

function normalizeTask(task) {
  return {
    id: task.id || createId('task'),
    title: String(task.title || '').trim(),
    description: String(task.description || '').trim(),
    dueDate: task.dueDate || '',
    status: Object.values(STATUS).includes(task.status) ? task.status : STATUS.TODO,
    createdAt: task.createdAt || Date.now(),
    updatedAt: task.updatedAt || Date.now()
  }
}

export function listTasks(userId) {
  return readStorage(storageKey(userId), []).map(normalizeTask)
}

export function saveTasks(userId, tasks) {
  writeStorage(storageKey(userId), tasks.map(normalizeTask))
}

export function createTask(userId, input) {
  const task = normalizeTask({ ...input, id: createId('task'), createdAt: Date.now(), updatedAt: Date.now() })
  const tasks = listTasks(userId)
  saveTasks(userId, [task, ...tasks])
  return task
}

export function updateTask(userId, id, input) {
  const tasks = listTasks(userId)
  const index = tasks.findIndex((task) => task.id === id)
  if (index < 0) return null
  const updated = normalizeTask({ ...tasks[index], ...input, id, updatedAt: Date.now() })
  tasks[index] = updated
  saveTasks(userId, tasks)
  return updated
}

export function deleteTask(userId, id) {
  const tasks = listTasks(userId)
  saveTasks(userId, tasks.filter((task) => task.id !== id))
}
