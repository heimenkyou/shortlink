import ElementPlus, { ElMessage } from "element-plus";
import "element-plus/dist/index.css";
import { createPinia } from "pinia";
import { createApp } from "vue";

import App from "./App.vue";
import router from "./router";

const app = createApp(App);

app.config.errorHandler = (error) => {
	console.error(error);
	ElMessage.error(error?.message || "页面运行异常");
};

window.addEventListener("unhandledrejection", (event) => {
	console.error(event.reason);
	ElMessage.error(event.reason?.message || "发生未处理的异步异常");
});

app.use(createPinia());
app.use(router);
app.use(ElementPlus);

app.mount("#app");
