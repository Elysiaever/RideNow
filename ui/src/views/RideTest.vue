<template>
  <div class="ride-container">
    <!-- 左侧：创建行程 -->
    <el-card class="ride-card">
      <h2>创建新行程</h2>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="乘客ID" prop="passengerId">
          <el-input v-model="form.passengerId" placeholder="请输入乘客ID" />
        </el-form-item>

        <el-form-item label="出发点" prop="origin">
          <el-input v-model="form.origin" placeholder="请输入出发地" />
        </el-form-item>

        <el-form-item label="目的地" prop="destination">
          <el-input v-model="form.destination" placeholder="请输入目的地" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm">提交行程</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>

      <el-result
        v-if="createdRide"
        icon="success"
        title="行程创建成功"
        :sub-title="'行程ID：' + createdRide.rideId"
      ></el-result>
    </el-card>

    <!-- 右侧：查询历史行程 -->
    <el-card class="ride-card">
      <h2>查询历史行程</h2>

      <el-form inline>
        <el-form-item label="乘客ID">
          <el-input v-model="searchPassengerId" placeholder="请输入乘客ID" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchHistory">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table
        v-if="historyRides.length"
        :data="historyRides"
        style="width: 100%; margin-top: 20px"
      >
        <el-table-column prop="rideId" label="行程ID" />
        <el-table-column prop="origin" label="出发点" />
        <el-table-column prop="destination" label="目的地" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-select
              v-model="scope.row.status"
              placeholder="选择状态"
              size="small"
              @change="changeStatus(scope.row)"
            >
              <el-option label="REQUESTED" value="REQUESTED" />
              <el-option label="DISPATCHING" value="DISPATCHING" />
              <el-option label="ACCEPTED" value="ACCEPTED" />
              <el-option label="ARRIVED" value="ARRIVED" />
              <el-option label="PICKEDUP" value="PICKEDUP" />
              <el-option label="ONGOING" value="ONGOING" />
              <el-option label="FINISHED" value="FINISHED" />
              <el-option label="CANCELLED" value="CANCELLED" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="requestTime" label="请求时间" />
        <el-table-column prop="price" label="价格" />
      </el-table>

      <div v-else style="margin-top: 20px">暂无历史行程</div>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import type { FormInstance } from "element-plus";
import {
  createRide,
  getHistoryRidesByPassengerId,
  updateRideStatus,
  type CreateRideDto,
  type Ride
} from "@/api/ride";

// 左侧创建表单
const formRef = ref<FormInstance>();
const form = reactive<CreateRideDto>({
  passengerId: "",
  origin: "",
  destination: "",
});
const rules = {
  passengerId: [{ required: true, message: "请输入乘客ID", trigger: "blur" }],
  origin: [{ required: true, message: "请输入出发点", trigger: "blur" }],
  destination: [{ required: true, message: "请输入目的地", trigger: "blur" }],
};
const createdRide = ref<Ride | null>(null);

const submitForm = () => {
  formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return;
    try {
      const res = await createRide(form);
      createdRide.value = res.data;
      ElMessage.success("行程创建成功！");
    } catch (e) {
      ElMessage.error("创建失败，请检查后端服务是否启动");
    }
  });
};

const resetForm = () => {
  formRef.value?.resetFields();
  createdRide.value = null;
};

// 右侧查询历史
const searchPassengerId = ref("");
const historyRides = ref<Ride[]>([]);

const fetchHistory = async () => {
  if (!searchPassengerId.value) {
    ElMessage.warning("请输入乘客ID");
    return;
  }
  try {
    const res = await getHistoryRidesByPassengerId(searchPassengerId.value);
    historyRides.value = res.data;
  } catch (e) {
    ElMessage.error("查询失败，请检查后端服务");
  }
};

// 修改状态
const changeStatus = async (ride: Ride) => {
  try {
    await updateRideStatus(ride.rideId, ride.status);
    ElMessage.success("状态更新成功");
  } catch (e) {
    ElMessage.error("状态更新失败");
  }
};
</script>

<style scoped>
.ride-container {
  display: flex;
  gap: 20px;
  margin: 40px;
}

.ride-card {
  flex: 1;
}
</style>
