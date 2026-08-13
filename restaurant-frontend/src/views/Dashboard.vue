<template>
  <div class="page">

    <!-- Page Header -->
    <div class="page-header">
      <div>
        <div class="eyebrow">DASHBOARD</div>
        <h1 class="page-title">首页</h1>
        <p class="page-desc">今日餐厅运营数据概览</p>
      </div>
    </div>

    <!-- Stats -->
    <div class="stats">
      <div class="stat-card" v-for="item in statCards" :key="item.key">
        <span>{{ item.label }}</span>
        <strong>{{ statistics[item.key] }}</strong>
      </div>
    </div>

    <!-- Hot Dishes -->
    <div class="card">
      <div class="card-header">
        <span class="card-title">今日热销菜品 TOP 5</span>
      </div>

      <el-table :data="hotDishes" :show-header="true" size="default">
        <el-table-column type="index" label="排名" width="70">
          <template #default="scope">
            <span class="rank-num">{{ scope.$index + 1 }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="dishName" label="菜品名称" />

        <el-table-column prop="quantity" label="销量" width="120">
          <template #default="scope">
            <span class="cell-sub">{{ scope.row.quantity }} 份</span>
          </template>
        </el-table-column>

        <el-table-column prop="amount" label="总额" width="140">
          <template #default="scope">
            <span class="amount">¥{{ (scope.row.amount || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Trend Chart -->
    <div class="card" style="margin-top: 24px;">
      <div class="card-header">
        <span class="card-title">今日营业趋势</span>
      </div>
      <div ref="trendRef" style="width:100%;height:380px"></div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { getDashboardStatistics, getHotDishes, getOrderTrend } from "@/api/dashboard"
import * as echarts from "echarts"

const hotDishes = ref([])
const trendRef = ref(null)
const orderTrend = ref([])

const statistics = ref({
  todayOrderCount: 0,
  todayAmount: 0,
  todayDineInCount: 0,
  todayTakeoutCount: 0,
  pendingOrderCount: 0,
  completedOrderCount: 0
})

const statCards = [
  { key: "todayOrderCount",   label: "今日订单" },
  { key: "todayAmount",       label: "今日营业额" },
  { key: "todayDineInCount",  label: "堂食订单" },
  { key: "todayTakeoutCount", label: "外带订单" },
]

async function loadOrderTrend() {
  try {
    const res = await getOrderTrend()
    if (res.data.code === 200) {
      orderTrend.value = res.data.data
      renderChart()
    }
  } catch (e) { console.error(e) }
}

async function loadHotDishes() {
  try {
    const res = await getHotDishes()
    if (res.data.code === 200) {
      hotDishes.value = res.data.data
    }
  } catch (e) { console.error(e) }
}

async function loadStatistics() {
  try {
    const res = await getDashboardStatistics()
    if (res.data.code === 200) {
      statistics.value = res.data.data
    }
  } catch (e) { console.error(e) }
}

function renderChart() {
  if (!trendRef.value) return

  const chart = echarts.init(trendRef.value)

  chart.setOption({
    tooltip: { trigger: "axis" },
    legend: {
      data: ["订单数", "营业额"],
      right: 0,
      top: 0,
      textStyle: { color: '#8b8176', fontSize: 12 }
    },
    grid: { left: 48, right: 48, top: 40, bottom: 28 },
    xAxis: {
      type: "category",
      data: orderTrend.value.map(i => i.time),
      axisLine: { lineStyle: { color: '#e4ddd5' } },
      axisTick: { show: false },
      axisLabel: { color: '#8b8176', fontSize: 12 }
    },
    yAxis: [
      {
        type: "value",
        name: "订单数",
        splitLine: { lineStyle: { color: '#eee9e4', type: 'dashed' } },
        axisLabel: { color: '#8b8176', fontSize: 12 },
        nameTextStyle: { color: '#8b8176', fontSize: 12 }
      },
      {
        type: "value",
        name: "营业额",
        splitLine: { show: false },
        axisLabel: { color: '#8b8176', fontSize: 12 },
        nameTextStyle: { color: '#8b8176', fontSize: 12 }
      }
    ],
    series: [
      {
        name: "订单数",
        type: "line",
        smooth: true,
        symbol: 'circle',
        symbolSize: 5,
        data: orderTrend.value.map(i => i.orderCount),
        lineStyle: { color: '#3d3731', width: 2 },
        itemStyle: { color: '#3d3731', borderWidth: 2, borderColor: '#fff' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(61, 55, 49, 0.08)' },
            { offset: 1, color: 'rgba(61, 55, 49, 0.01)' }
          ])
        }
      },
      {
        name: "营业额",
        type: "bar",
        yAxisIndex: 1,
        barWidth: 18,
        data: orderTrend.value.map(i => i.amount),
        itemStyle: {
          color: '#d8d0c7',
          borderRadius: [2, 2, 0, 0]
        }
      }
    ]
  })
}

onMounted(() => {
  loadStatistics()
  loadHotDishes()
  loadOrderTrend()
})
</script>

<style scoped>
.page {
  padding: 36px 42px;
  min-height: 100%;
  background: var(--surface-page);
}

/* ── Page Header ── */
.page-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 32px;
}

.eyebrow {
  font-size: 11px;
  letter-spacing: 3px;
  color: var(--text-3);
  margin-bottom: 8px;
  font-weight: 500;
}

.page-title {
  font-family: "Cormorant Garamond", serif;
  font-size: 36px;
  font-weight: 500;
  color: var(--text-1);
  margin-bottom: 4px;
  line-height: 1.2;
}

.page-desc {
  font-size: 14px;
  color: var(--text-3);
}

/* ── Stats ── */
.stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  margin-bottom: 28px;
}

.stat-card {
  background: #fff;
  padding: 22px;
  border: 1px solid var(--border-light);
}

.stat-card span {
  display: block;
  font-size: 12px;
  color: var(--text-4);
  margin-bottom: 10px;
}

.stat-card strong {
  font-family: "Cormorant Garamond", serif;
  font-size: 32px;
  font-weight: 500;
  color: var(--text-1);
}

/* ── Card ── */
.card {
  background: #fff;
  border: 1px solid var(--border);
  padding: 0;
}

.card-header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-faint);
}

.card-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-1);
}

/* ── Table ── */
.card :deep(.el-table) {
  border: none;
}

.card :deep(.el-table th.el-table__cell) {
  background: #fff;
}

/* ── Ranking ── */
.rank-num {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  font-size: 12px;
  font-weight: 500;
  color: var(--text-3);
}

.cell-sub {
  font-size: 13px;
  color: var(--text-2);
}

.amount {
  font-family: "Cormorant Garamond", serif;
  font-size: 16px;
  font-weight: 500;
  color: var(--text-1);
}

/* ── Responsive ── */
@media (max-width: 900px) {
  .stats {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .page {
    padding: 24px 18px;
  }
}
</style>
