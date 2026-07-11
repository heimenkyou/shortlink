<!-- 临时测试页面：仅用于接口联调验证，后续会重写页面与样式。 -->
<script setup>
/* biome-ignore-all lint/correctness/noUnusedImports: Vue 模板和事件表达式会使用这些导入 */
/* biome-ignore-all lint/correctness/noUnusedVariables: Vue 模板和事件表达式会使用这些变量 */
import { reactive, ref } from "vue";

import { createShortLink, pageShortLink } from "@/api/modules/link";

const loading = ref(false);
const responseText = ref("");

const createForm = reactive({
	domain: "",
	originUrl: "",
	gid: "",
	createdType: 1,
	validDateType: 0,
	validDate: "",
	describe: "",
});

const pageForm = reactive({
	current: 1,
	size: 10,
	gid: "",
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
	} finally {
		loading.value = false;
	}
}
</script>

<template>
	<section>
		<h1>短链接接口联调</h1>

		<el-space alignment="start" wrap>
			<el-card header="创建短链接">
				<el-form label-width="120px">
					<el-form-item label="域名">
						<el-input v-model="createForm.domain" />
					</el-form-item>
					<el-form-item label="原始链接">
						<el-input v-model="createForm.originUrl" />
					</el-form-item>
					<el-form-item label="分组 gid">
						<el-input v-model="createForm.gid" />
					</el-form-item>
					<el-form-item label="创建类型">
						<el-input-number v-model="createForm.createdType" :min="0" />
					</el-form-item>
					<el-form-item label="有效期类型">
						<el-input-number v-model="createForm.validDateType" :min="0" :max="1" />
					</el-form-item>
					<el-form-item label="有效期">
						<el-input v-model="createForm.validDate" />
					</el-form-item>
					<el-form-item label="描述">
						<el-input v-model="createForm.describe" type="textarea" />
					</el-form-item>
					<el-button
						:loading="loading"
						type="primary"
						@click="
							runRequest('创建短链接结果', () =>
								createShortLink({
									...createForm,
									createdType: Number(createForm.createdType),
									validDateType: Number(createForm.validDateType),
								}),
							)
						"
					>
						创建
					</el-button>
				</el-form>
			</el-card>

			<el-card header="分页查询">
				<el-form label-width="120px">
					<el-form-item label="当前页">
						<el-input-number v-model="pageForm.current" :min="1" />
					</el-form-item>
					<el-form-item label="每页条数">
						<el-input-number v-model="pageForm.size" :min="1" />
					</el-form-item>
					<el-form-item label="分组 gid">
						<el-input v-model="pageForm.gid" />
					</el-form-item>
					<el-button
						:loading="loading"
						@click="
							runRequest('分页查询结果', () =>
								pageShortLink({
									...pageForm,
									current: Number(pageForm.current),
									size: Number(pageForm.size),
								}),
							)
						"
					>
						查询
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
