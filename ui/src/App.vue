<script setup lang="ts">

import axios from 'axios';

function hello(){
  axios.get('http://localhost:80/api/driver/hello').then(function (response) {
    console.log(response);
  });
}
</script>

<template>
  <h1>Let's ride now!</h1>
  <p>
    This is a Blank Page...
  </p>
  <button @click="hello">Click me</button>
</template>

<style scoped></style>
