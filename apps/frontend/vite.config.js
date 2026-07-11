import { fileURLToPath, URL } from "node:url";

import vue from "@vitejs/plugin-vue";
import { defineConfig } from "vite";
import vueDevTools from "vite-plugin-vue-devtools";

/**
 * 通过代理把管理端和项目端接口分别转发到本地后端，方便直接联调。
 */
export default defineConfig({
	plugins: [vue(), vueDevTools()],
	resolve: {
		alias: {
			"@": fileURLToPath(new URL("./src", import.meta.url)),
		},
	},
	server: {
		proxy: {
			"/api/short-link/admin": {
				target: "http://127.0.0.1:8002",
				changeOrigin: true,
			},
			"/api/short-link/v1": {
				target: "http://127.0.0.1:8001",
				changeOrigin: true,
			},
		},
	},
});
