import { defineStore } from "pinia";
import { computed, ref } from "vue";

const TOKEN_STORAGE_KEY = "shortlink_admin_token";

export const useAuthStore = defineStore("auth", () => {
	const tokenValue = ref(localStorage.getItem(TOKEN_STORAGE_KEY) || "");

	const isLoggedIn = computed(() => Boolean(tokenValue.value));

	/**
	 * 统一维护 token，避免请求头和页面状态不一致。
	 *
	 * @param {string} token 登录 token
	 * @returns {void} 无返回值
	 */
	function setToken(token) {
		tokenValue.value = token || "";
		if (tokenValue.value) {
			localStorage.setItem(TOKEN_STORAGE_KEY, tokenValue.value);
			return;
		}
		localStorage.removeItem(TOKEN_STORAGE_KEY);
	}

	/**
	 * 返回认证状态仓库。
	 *
	 * @returns {{ token: import("vue").Ref<string>, isLoggedIn: import("vue").ComputedRef<boolean>, setToken: (token: string) => void }} 仓库对象
	 */
	return {
		token: tokenValue,
		isLoggedIn,
		setToken,
	};
});
