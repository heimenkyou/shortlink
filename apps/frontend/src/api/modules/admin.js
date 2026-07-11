import http from "@/api/http";

const ADMIN_BASE = "/api/short-link/admin/v1";

/**
 * 注册用户。
 *
 * @param {{ username: string, password: string, realName: string, phone: string, mail: string }} data 注册参数
 * @returns {Promise<unknown>} 注册结果
 */
export function registerUser(data) {
	return http.post(`${ADMIN_BASE}/user`, data);
}

/**
 * 检查用户名是否已存在。
 *
 * @param {string} username 用户名
 * @returns {Promise<boolean>} 用户名是否已存在
 */
export function hasUsername(username) {
	return http.get(`${ADMIN_BASE}/user/has-username`, {
		params: { username },
	});
}

/**
 * 用户登录。
 *
 * @param {{ username: string, password: string }} data 登录参数
 * @returns {Promise<{ token: string }>} 登录结果
 */
export function loginUser(data) {
	return http.post(`${ADMIN_BASE}/user/login`, data);
}

/**
 * 检查 token 是否有效。
 *
 * @param {string} token 登录 token
 * @returns {Promise<boolean>} token 是否有效
 */
export function checkLogin(token) {
	return http.get(`${ADMIN_BASE}/user/check-login`, {
		params: { token },
	});
}

/**
 * 根据用户名查询用户。
 *
 * @param {string} username 用户名
 * @returns {Promise<unknown>} 用户信息
 */
export function getUserByUsername(username) {
	return http.get(`${ADMIN_BASE}/user/${encodeURIComponent(username)}`);
}

/**
 * 修改用户信息。
 *
 * @param {{ username: string, password: string, realName: string, phone: string, mail: string }} data 用户更新参数
 * @returns {Promise<unknown>} 更新结果
 */
export function updateUser(data) {
	return http.put(`${ADMIN_BASE}/user`, data);
}

/**
 * 退出登录。
 *
 * @param {string} token 登录 token
 * @returns {Promise<unknown>} 退出结果
 */
export function logoutUser(token) {
	return http.post(`${ADMIN_BASE}/user/logout`, null, {
		params: { token },
	});
}

/**
 * 新增分组。
 *
 * @param {{ name: string }} data 分组参数
 * @returns {Promise<unknown>} 新增结果
 */
export function createGroup(data) {
	return http.post(`${ADMIN_BASE}/group`, data);
}

/**
 * 查询分组列表。
 *
 * @returns {Promise<unknown[]>} 分组列表
 */
export function listGroups() {
	return http.get(`${ADMIN_BASE}/group`);
}

/**
 * 修改分组。
 *
 * @param {{ gid: string, name: string }} data 分组更新参数
 * @returns {Promise<unknown>} 更新结果
 */
export function updateGroup(data) {
	return http.put(`${ADMIN_BASE}/group`, data);
}

/**
 * 删除分组。
 *
 * @param {string} gid 分组标识
 * @returns {Promise<unknown>} 删除结果
 */
export function deleteGroup(gid) {
	return http.delete(`${ADMIN_BASE}/group/${encodeURIComponent(gid)}`);
}

/**
 * 分组排序。
 *
 * @param {Array<{ gid: string, sortOrder: number }>} data 排序参数
 * @returns {Promise<unknown>} 排序结果
 */
export function sortGroups(data) {
	return http.put(`${ADMIN_BASE}/group/sort`, data);
}
