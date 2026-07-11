import { createRouter, createWebHistory } from "vue-router";

import AdminDebugView from "@/views/AdminDebugView.vue";
import LinkDebugView from "@/views/LinkDebugView.vue";

const router = createRouter({
	history: createWebHistory(import.meta.env.BASE_URL),
	routes: [
		{
			path: "/",
			name: "admin-debug",
			component: AdminDebugView,
		},
		{
			path: "/link",
			name: "link-debug",
			component: LinkDebugView,
		},
	],
});

export default router;
