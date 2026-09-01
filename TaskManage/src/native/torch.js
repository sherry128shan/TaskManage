export async function setTorch(enabled) {
  // #ifdef APP-PLUS
  if (uni.getSystemInfoSync().platform === 'android') {
    try {
      const main = plus.android.runtimeMainActivity()
      const manager = main.getSystemService('camera')
      const CameraCharacteristics = plus.android.importClass('android.hardware.camera2.CameraCharacteristics')
      const ids = manager.getCameraIdList()
      for (let index = 0; index < ids.length; index += 1) {
        const id = ids[index]
        const characteristics = manager.getCameraCharacteristics(id)
        const available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
        const facing = characteristics.get(CameraCharacteristics.LENS_FACING)
        if (available && facing === CameraCharacteristics.LENS_FACING_BACK) {
          manager.setTorchMode(id, enabled)
        }
      }
      return { supported: true }
    } catch {
      return { supported: false, message: 'Torch access was denied by the device.' }
    }
  }
  // #endif
  return { supported: false, message: 'Torch control needs an iOS native plugin on this target.' }
}
