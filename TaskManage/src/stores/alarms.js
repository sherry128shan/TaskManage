import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { useAuthStore } from './auth'
import { createAlarm, deleteAlarm, listAlarms } from '../data/alarmRepository'
import { scheduleAlarm, cancelAlarm } from '../native/alarm'

export const useAlarmStore = defineStore('alarms', () => {
  const auth = useAuthStore()
  const alarms = ref([])
  const upcoming = computed(() => alarms.value.filter((alarm) => alarm.timestamp >= Date.now()))

  function refresh() {
    alarms.value = auth.user ? listAlarms(auth.user.id) : []
  }

  async function add(input) {
    const alarm = createAlarm(auth.user.id, input)
    alarms.value = listAlarms(auth.user.id)
    await scheduleAlarm(alarm)
    return alarm
  }

  async function remove(id) {
    await cancelAlarm(id)
    deleteAlarm(auth.user.id, id)
    alarms.value = listAlarms(auth.user.id)
  }

  return { alarms, upcoming, refresh, add, remove }
})
