import { readStorage, writeStorage } from '../utils/storage'
import { createId } from '../utils/ids'

function storageKey(userId) {
  return `alarms:${userId}`
}

export function listAlarms(userId) {
  return readStorage(storageKey(userId), [])
    .map((alarm) => ({ ...alarm, enabled: alarm.enabled !== false }))
    .sort((a, b) => a.timestamp - b.timestamp)
}

export function saveAlarms(userId, alarms) {
  writeStorage(storageKey(userId), alarms)
}

export function createAlarm(userId, input) {
  const alarm = {
    id: createId('alarm'),
    title: String(input.title || 'Task reminder').trim() || 'Task reminder',
    date: input.date,
    time: input.time,
    timestamp: input.timestamp,
    enabled: true
  }
  saveAlarms(userId, [alarm, ...listAlarms(userId)])
  return alarm
}

export function deleteAlarm(userId, id) {
  saveAlarms(userId, listAlarms(userId).filter((alarm) => alarm.id !== id))
}
