import axios from "axios";
import { ElMessage } from "element-plus";

import { useAuthStore } from "@/stores/auth";

const SUCCESS_CODE = "0";

/**
 * 统一处理前后端约定的返回结构，避免业务层重复判断。
 *
 * @returns {import("axios").AxiosInstance} Axios 实例
 */
const http = axios.create({
	timeout: 10000,
});

/**
 * 在请求发出前补齐认证头，避免页面层重复拼接 token。
 *
 * @param {import("axios").InternalAxiosRequestConfig} config 请求配置
 * @returns {import("axios").InternalAxiosRequestConfig} 处理后的请求配置
 */
http.interceptors.request.use((config) => {
	const authStore = useAuthStore();
	const nextConfig = {
		...config,
		headers: {
			...(config.headers ?? {}),
		},
	};

	if (authStore.token) {
		nextConfig.headers.token = authStore.token;
	}

	return nextConfig;
});

http.interceptors.response.use(
	/**
	 * 把后端统一返回结构解包成业务数据。
	 *
	 * @param {import("axios").AxiosResponse} response 响应对象
	 * @returns {unknown} 解包后的业务数据
	 */
	(response) => {
		const result = response.data;

		if (!result || result.code === SUCCESS_CODE) {
			return result?.data;
		}

		const error = new Error(result.message || "请求失败");
		error.name = "ApiError";
		error.result = result;
		ElMessage.error(result.message || "请求失败");
		throw error;
	},
	/**
	 * 统一处理网络错误和非业务异常。
	 *
	 * @param {any} error 异常对象
	 * @returns {Promise<never>} 始终抛出异常
	 */
	(error) => {
		const message =
			error.response?.data?.message || error.message || "网络请求失败";
		ElMessage.error(message);
		throw error;
	},
);

export default http;
