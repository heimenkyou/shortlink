import http from "@/api/http";

const PROJECT_BASE = "/api/short-link/v1";

/**
 * 创建短链接。
 *
 * @param {{ domain: string, originUrl: string, gid: string, createdType: number, validDateType: number, validDate: string, describe: string }} data 创建参数
 * @returns {Promise<unknown>} 创建结果
 */
export function createShortLink(data) {
	return http.post(`${PROJECT_BASE}/create`, data);
}

/**
 * 分页查询短链接。
 *
 * @param {{ current: number, size: number, gid: string }} data 分页参数
 * @returns {Promise<unknown>} 分页结果
 */
export function pageShortLink(data) {
	return http.post(`${PROJECT_BASE}/page`, data);
}
