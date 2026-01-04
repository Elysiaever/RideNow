<template>
  <div class="ride-history-container">
    <h1>行程历史</h1>
    
    <div class="filter-section">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchRideHistory">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table
      :data="rideHistory"
      v-loading="loading"
      style="width: 100%"
      stripe
      @row-click="viewRideDetails"
    >
      <el-table-column prop="rideId" label="行程ID" width="180" />
      <el-table-column prop="driverId" label="司机ID" width="180" />
      <el-table-column prop="origin" label="出发地" width="200" show-overflow-tooltip />
      <el-table-column prop="destination" label="目的地" width="200" show-overflow-tooltip />
      <el-table-column prop="startTime" label="开始时间" width="160" />
      <el-table-column prop="endTime" label="结束时间" width="160" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusTagType(row.status)">{{ formatStatus(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="fare" label="费用" width="100">
        <template #default="{ row }">
          <span>¥{{ row.fare ? row.fare.toFixed(2) : '0.00' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click.stop="viewRideDetails(row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="[5, 10, 20, 50]"
      :background="true"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      class="pagination"
    />

    <!-- 行程详情弹窗 -->
    <el-dialog v-model="showDetailDialog" title="行程详情" width="600px">
      <div v-if="selectedRide" class="ride-detail-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="行程ID">{{ selectedRide.rideId }}</el-descriptions-item>
          <el-descriptions-item label="司机ID">{{ selectedRide.driverId }}</el-descriptions-item>
          <el-descriptions-item label="乘客ID">{{ selectedRide.passengerId }}</el-descriptions-item>
          <el-descriptions-item label="出发地">{{ selectedRide.origin }}</el-descriptions-item>
          <el-descriptions-item label="目的地">{{ selectedRide.destination }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ selectedRide.startTime }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ selectedRide.endTime }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusTagType(selectedRide.status)">{{ formatStatus(selectedRide.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="费用">¥{{ selectedRide.fare ? selectedRide.fare.toFixed(2) : '0.00' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getHistoryRidesByPassengerId } from '@/api/ride'
import { useUserStore } from '@/stores/userStore'
import { ElMessage } from 'element-plus'

// 类型定义
interface Ride {
  rideId: string
  driverId: string
  passengerId: string
  startTime: string
  endTime: string
  origin: string
  destination: string
  status: string
  fare?: number
}

// 响应式数据
const rideHistory = ref<Ride[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showDetailDialog = ref(false)
const selectedRide = ref<Ride | null>(null)
const filterForm = ref({
  dateRange: [] as string[] | null
})

// 获取用户信息
const userStore = useUserStore()

// 获取行程历史
const fetchRideHistory = async () => {
  loading.value = true
  try {
    // 模拟API调用，实际应该调用后端API
    // const response = await getHistoryRidesByPassengerId(userStore.userInfo.id)
    // rideHistory.value = response.data
    
    // 模拟数据
    rideHistory.value = [
      {
        rideId: 'RIDE001',
        driverId: 'DRIVER001',
        passengerId: userStore.userInfo.id || 'USER001',
        startTime: '2026-01-01 09:30:00',
        endTime: '2026-01-01 09:45:00',
        origin: '北京市朝阳区建国路1号',
        destination: '北京市朝阳区三里屯',
        status: 'COMPLETED',
        fare: 25.50
      },
      {
        rideId: 'RIDE002',
        driverId: 'DRIVER002',
        passengerId: userStore.userInfo.id || 'USER001',
        startTime: '2026-01-02 14:20:00',
        endTime: '2026-01-02 14:40:00',
        origin: '北京市海淀区中关村大街',
        destination: '北京市海淀区五道口',
        status: 'COMPLETED',
        fare: 18.00
      },
      {
        rideId: 'RIDE003',
        driverId: 'DRIVER003',
        passengerId: userStore.userInfo.id || 'USER001',
        startTime: '2026-01-03 18:15:00',
        endTime: '2026-01-03 18:35:00',
        origin: '北京市西城区西单',
        destination: '北京市东城区王府井',
        status: 'COMPLETED',
        fare: 22.30
      }
    ]
    total.value = rideHistory.value.length
  } catch (error) {
    console.error('获取行程历史失败:', error)
    ElMessage.error('获取行程历史失败')
  } finally {
    loading.value = false
  }
}

// 状态格式化
const formatStatus = (status: string) => {
  const statusMap: Record<string, string> = {
    'PENDING': '待接单',
    'ACCEPTED': '已接单',
    'ARRIVED': '已到达',
    'IN_TRIP': '行程中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

// 状态标签类型
const getStatusTagType = (status: string) => {
  const typeMap: Record<string, string> = {
    'PENDING': 'info',
    'ACCEPTED': 'warning',
    'ARRIVED': 'primary',
    'IN_TRIP': 'primary',
    'COMPLETED': 'success',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || 'info'
}

// 查看行程详情
const viewRideDetails = (ride: Ride) => {
  selectedRide.value = ride
  showDetailDialog.value = true
}

// 分页处理
const handleSizeChange = (size: number) => {
  pageSize.value = size
  fetchRideHistory()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  fetchRideHistory()
}

// 重置筛选条件
const resetFilter = () => {
  filterForm.value.dateRange = null
  fetchRideHistory()
}

onMounted(() => {
  fetchRideHistory()
})
</script>

<style scoped>
.ride-history-container {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.filter-section {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.ride-detail-content {
  padding: 10px 0;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}
</style>