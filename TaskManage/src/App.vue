<script setup>
import { onMounted } from 'vue'
import { useAuthStore } from './stores/auth'

onMounted(() => {
  useAuthStore().hydrate()
})
</script>
