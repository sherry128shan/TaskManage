const PREFIX = 'taskmanage:'

function key(name) {
  return `${PREFIX}${name}`
}

export function readStorage(name, fallback) {
  try {
    const value = uni.getStorageSync(key(name))
    return value === '' || value === undefined || value === null ? fallback : value
  } catch {
    return fallback
  }
}

export function writeStorage(name, value) {
  uni.setStorageSync(key(name), value)
}

export function removeStorage(name) {
  uni.removeStorageSync(key(name))
}
