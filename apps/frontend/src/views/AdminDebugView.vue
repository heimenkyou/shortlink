<!-- 临时测试页面：仅用于接口联调验证，后续会重写页面与样式。 -->
<script setup>
/* biome-ignore-all lint/correctness/noUnusedImports: Vue 模板和事件表达式会使用这些导入 */
/* biome-ignore-all lint/correctness/noUnusedVariables: Vue 模板和事件表达式会使用这些变量 */
import { reactive, ref } from "vue";

import {
	checkLogin,
	createGroup,
	deleteGroup,
	getUserByUsername,
	hasUsername,
	listGroups,
	loginUser,
	logoutUser,
	registerUser,
	sortGroups,
	updateGroup,
	updateUser,
} from "@/api/modules/admin";
import { useAuthStore } from "@/stores/auth";

const authStore = useAuthStore();
const loading = ref(false);
const responseText = ref("");

const registerForm = reactive({
	username: "",
	password: "",
	realName: "",
	phone: "",
	mail: "",
});

const loginForm = reactive({
	username: "",
	password: "",
});

const queryForm = reactive({
	username: "",
});

const updateUserForm = reactive({
	username: "",
	password: "",
	realName: "",
	phone: "",
	mail: "",
});

const createGroupForm = reactive({
	name: "",
});

const updateGroupForm = reactive({
	gid: "",
	name: "",
});

const deleteGroupForm = reactive({
	gid: "",
});

const sortGroupForm = reactive({
	gid: "",
	sortOrder: 0,
});

/**
 * 格式化请求结果，便于直接在测试页查看接口返回。
 *
 * @param {string} title 标题
 * @param {unknown} data 响应数据
 * @returns {void} 无返回值
 */
function setResponse(title, data) {
	responseText.value = `${title}\n${JSON.stringify(data, null, 2)}`;
}

/**
 * 统一包裹联调请求，复用 loading 和结果输出。
 *
 * @param {string} title 标题
 * @param {() => Promise<unknown>} request 请求函数
 * @returns {Promise<unknown>} 请求结果
 */
async function runRequest(title, request) {
	loading.value = true;
	try {
		const data = await request();
		setResponse(title, data);
		return data;
	} finally {
		loading.value = false;
	}
}
</script>

<template>
	<section>
		<h1>管理端接口联调</h1>
		<p>当前 token：{{ authStore.token || "未登录" }}</p>
		<p>登录状态：{{ authStore.isLoggedIn ? "已保存 token" : "未保存 token" }}</p>

		<el-space alignment="start" wrap>
			<el-card header="注册">
				<el-form label-width="100px">
					<el-form-item label="用户名">
						<el-input v-model="registerForm.username" />
					</el-form-item>
					<el-form-item label="密码">
						<el-input v-model="registerForm.password" />
					</el-form-item>
					<el-form-item label="真实姓名">
						<el-input v-model="registerForm.realName" />
					</el-form-item>
					<el-form-item label="手机号">
						<el-input v-model="registerForm.phone" />
					</el-form-item>
					<el-form-item label="邮箱">
						<el-input v-model="registerForm.mail" />
					</el-form-item>
					<el-button
						:loading="loading"
						type="primary"
						@click="runRequest('注册结果', () => registerUser({ ...registerForm }))"
					>
						提交
					</el-button>
				</el-form>
			</el-card>

			<el-card header="登录与用户">
				<el-form label-width="100px">
					<el-form-item label="登录用户名">
						<el-input v-model="loginForm.username" />
					</el-form-item>
					<el-form-item label="登录密码">
						<el-input v-model="loginForm.password" />
					</el-form-item>
					<el-space wrap>
						<el-button
							:loading="loading"
							type="primary"
							@click="
								runRequest('登录结果', () => loginUser({ ...loginForm })).then((data) => {
									authStore.setToken(data?.token || '');
								})
							"
						>
							登录
						</el-button>
						<el-button
							:loading="loading"
							@click="runRequest('登录校验', () => checkLogin(authStore.token))"
						>
							检查登录
						</el-button>
						<el-button
							:loading="loading"
							@click="
								runRequest('退出登录结果', () => logoutUser(authStore.token)).then(() => {
									authStore.setToken('');
								})
							"
						>
							退出登录
						</el-button>
					</el-space>
				</el-form>

				<el-divider />

				<el-form label-width="100px">
					<el-form-item label="查询用户名">
						<el-input v-model="queryForm.username" />
					</el-form-item>
					<el-space wrap>
						<el-button
							:loading="loading"
							@click="runRequest('用户名存在性', () => hasUsername(queryForm.username))"
						>
							检查用户名
						</el-button>
						<el-button
							:loading="loading"
							@click="runRequest('用户详情', () => getUserByUsername(queryForm.username))"
						>
							查询用户
						</el-button>
					</el-space>
				</el-form>

				<el-divider />

				<el-form label-width="100px">
					<el-form-item label="用户名">
						<el-input v-model="updateUserForm.username" />
					</el-form-item>
					<el-form-item label="密码">
						<el-input v-model="updateUserForm.password" />
					</el-form-item>
					<el-form-item label="真实姓名">
						<el-input v-model="updateUserForm.realName" />
					</el-form-item>
					<el-form-item label="手机号">
						<el-input v-model="updateUserForm.phone" />
					</el-form-item>
					<el-form-item label="邮箱">
						<el-input v-model="updateUserForm.mail" />
					</el-form-item>
					<el-button
						:loading="loading"
						@click="runRequest('更新用户结果', () => updateUser({ ...updateUserForm }))"
					>
						更新用户
					</el-button>
				</el-form>
			</el-card>

			<el-card header="分组">
				<el-form label-width="100px">
					<el-form-item label="新增名称">
						<el-input v-model="createGroupForm.name" />
					</el-form-item>
					<el-space wrap>
						<el-button
							:loading="loading"
							type="primary"
							@click="runRequest('创建分组结果', () => createGroup({ ...createGroupForm }))"
						>
							新增分组
						</el-button>
						<el-button :loading="loading" @click="runRequest('分组列表', () => listGroups())">
							查询分组
						</el-button>
					</el-space>
				</el-form>

				<el-divider />

				<el-form label-width="100px">
					<el-form-item label="分组 gid">
						<el-input v-model="updateGroupForm.gid" />
					</el-form-item>
					<el-form-item label="新名称">
						<el-input v-model="updateGroupForm.name" />
					</el-form-item>
					<el-button
						:loading="loading"
						@click="runRequest('更新分组结果', () => updateGroup({ ...updateGroupForm }))"
					>
						修改分组
					</el-button>
				</el-form>

				<el-divider />

				<el-form label-width="100px">
					<el-form-item label="删除 gid">
						<el-input v-model="deleteGroupForm.gid" />
					</el-form-item>
					<el-button
						:loading="loading"
						@click="runRequest('删除分组结果', () => deleteGroup(deleteGroupForm.gid))"
					>
						删除分组
					</el-button>
				</el-form>

				<el-divider />

				<el-form label-width="100px">
					<el-form-item label="排序 gid">
						<el-input v-model="sortGroupForm.gid" />
					</el-form-item>
					<el-form-item label="排序值">
						<el-input-number v-model="sortGroupForm.sortOrder" />
					</el-form-item>
					<el-button
						:loading="loading"
						@click="
							runRequest('分组排序结果', () =>
								sortGroups([
									{
										gid: sortGroupForm.gid,
										sortOrder: Number(sortGroupForm.sortOrder),
									},
								]),
							)
						"
					>
						分组排序
					</el-button>
				</el-form>
			</el-card>
		</el-space>

		<el-divider />
		<el-input
			:model-value="responseText"
			:rows="20"
			readonly
			type="textarea"
		/>
	</section>
</template>
