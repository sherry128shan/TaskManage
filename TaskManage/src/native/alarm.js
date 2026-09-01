import { dateTimeToTimestamp } from '../utils/date'

export async function scheduleAlarm(alarm) {
  // #ifdef APP-PLUS
  const delay = Math.max(0, Math.round((alarm.timestamp - Date.now()) / 1000))
  if (delay > 0) {
    plus.push.createMessage(alarm.title, JSON.stringify({ type: 'taskmanage-alarm', id: alarm.id }), {
      title: 'TaskManage reminder',
      delay,
      sound: 'system',
      cover: true
    })
    return { supported: true }
  }
  // #endif
  return { supported: false }
}

export async function cancelAlarm() {
  // #ifdef APP-PLUS
  // The standard runtime does not expose a portable cancellation handle for every
  // push provider. Persisted alarms are still removed from the app store; production
  // builds should pair this adapter with the chosen native notification plugin.
  // #endif
  return { supported: false }
}

export function buildAlarmInput(date, time) {
  return { date, time, timestamp: dateTimeToTimestamp(date, time) }
}
