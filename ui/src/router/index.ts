import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/ride",
      name: "Ride",
      component: () => import("@/views/RideTest.vue")
    },
  ],
})

export default router
