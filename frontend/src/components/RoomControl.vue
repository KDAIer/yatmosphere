<template>
  <div class="room-control">
    <div class="light-control">
      <label>主灯</label>
      <label class="switch">
        <input type="checkbox" v-model="lightState" />
        <span class="slider"></span>
      </label>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const lightState = ref(true);
</script>