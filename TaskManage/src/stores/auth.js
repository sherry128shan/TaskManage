import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { readStorage, removeStorage, writeStorage } from '../utils/storage'
import { createId } from '../utils/ids'

const USERS_KEY = 'users'
const SESSION_KEY = 'session'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const hydrated = ref(false)
  const isLoggedIn = computed(() => Boolean(user.value))

  function hydrate() {
    user.value = readStorage(SESSION_KEY, null)
    hydrated.value = true
  }

  function persistSession(nextUser) {
    user.value = nextUser
    writeStorage(SESSION_KEY, nextUser)
  }

  function register(username, password) {
    const cleanUsername = username.trim().toLowerCase()
    const users = readStorage(USERS_KEY, [])
    if (users.some((item) => item.username === cleanUsername)) {
      return { ok: false, message: 'This account already exists.' }
    }
    // Local-only prototype: replace this with a server-side auth flow before release.
    const record = { id: createId('user'), username: cleanUsername, password }
    writeStorage(USERS_KEY, [...users, record])
    return { ok: true }
  }

  function login(username, password) {
    const cleanUsername = username.trim().toLowerCase()
    const record = readStorage(USERS_KEY, []).find(
      (item) => item.username === cleanUsername && item.password === password
    )
    if (!record) return { ok: false, message: 'Check your username and password.' }
    persistSession({ id: record.id, username: record.username })
    return { ok: true }
  }

  function logout() {
    user.value = null
    removeStorage(SESSION_KEY)
  }

  return { user, hydrated, isLoggedIn, hydrate, register, login, logout }
})
