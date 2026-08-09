<template>
  <div class="dashboard">

    <!-- Stat cards -->
    <el-row :gutter="16">
      <el-col :span="6" v-for="item in statCards" :key="item.key">
        <div class="stat-card" :class="'stat-' + item.key">
          <div class="stat-label">{{ item.label }}</div>
          <div class="stat-value">{{ statistics[item.key] }}</div>
          <div class="stat-sub">{{ item.sub }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- Hot dishes -->
    <el-card class="section-card">
      <template #header>
        <div class="section-header">
          <span class="section-title">今日热销菜品 TOP 5</span>
        </div>
      </template>

      <el-table :data="hotDishes" :show-header="true" size="default">
        <el-table-column type="index" label="排名" width="70">
          <template #default="scope">
            <span v-if="scope.$index < 3" class="rank-medal">
              {{ ['🥇','🥈','🥉'][scope.$index] }}
            </span>
            <span v-else class="rank-num">{{ scope.$index + 1 }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="dishName" label="菜品名称" />

        <el-table-column prop="quantity" label="销量" width="110">
          <template #default="scope">
            <el-tag size="small" type="success" effect="plain" round>
              {{ scope.row.quantity }} 份
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="totalAmount" label="总额" width="120">
          <template #default="scope">
            <span class="amount-text">¥{{ (scope.row.totalAmount || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Trend chart -->
    <el-card class="section-card">
      <template #header>
        <div class="section-header">
          <span class="section-title">今日营业趋势</span>
        </div>
      </template>
      <div ref="trendRef" style="width:100%;height:380px"></div>
    </el-card>

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
  { key: "todayOrderCount",      label: "今日订单",  sub: "笔" },
  { key: "todayAmount",          label: "今日营业额", sub: "元" },
  { key: "todayDineInCount",     label: "堂食订单",  sub: "笔" },
  { key: "todayTakeoutCount",    label: "外带订单",  sub: "笔" },
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
      textStyle: { color: '#8a8d94', fontSize: 12 }
    },
    grid: { left: 48, right: 48, top: 40, bottom: 28 },
    xAxis: {
      type: "category",
      data: orderTrend.value.map(i => i.time),
      axisLine: { lineStyle: { color: '#e8e5e0' } },
      axisTick: { show: false },
      axisLabel: { color: '#8a8d94', fontSize: 12 }
    },
    yAxis: [
      {
        type: "value",
        name: "订单数",
        splitLine: { lineStyle: { color: '#f0ede8', type: 'dashed' } },
        axisLabel: { color: '#8a8d94', fontSize: 12 },
        nameTextStyle: { color: '#8a8d94', fontSize: 12 }
      },
      {
        type: "value",
        name: "营业额",
        splitLine: { show: false },
        axisLabel: { color: '#8a8d94', fontSize: 12 },
        nameTextStyle: { color: '#8a8d94', fontSize: 12 }
      }
    ],
    series: [
      {
        name: "订单数",
        type: "line",
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        data: orderTrend.value.map(i => i.orderCount),
        lineStyle: { color: '#c8913a', width: 2 },
        itemStyle: { color: '#c8913a', borderWidth: 2, borderColor: '#fff' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(200, 145, 58, 0.15)' },
            { offset: 1, color: 'rgba(200, 145, 58, 0.01)' }
          ])
        }
      },
      {
        name: "营业额",
        type: "bar",
        yAxisIndex: 1,
        barWidth: 20,
        data: orderTrend.value.map(i => i.amount),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#5a8f6a' },
            { offset: 1, color: '#7ab588' }
          ]),
          borderRadius: [3, 3, 0, 0]
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
.dashboard {
  padding: 0;
}

/* ── Stat cards ── */
.stat-card {
  background: var(--surface-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: var(--sp-5);
  position: relative;
  overflow: hidden;
  transition: box-shadow var(--duration) var(--ease);
}

.stat-card:hover {
  box-shadow: var(--shadow-md);
}

/* Left accent border — the one bold move */
.stat-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
}

.stat-todayOrderCount::before   { background: var(--accent); }
.stat-todayAmount::before       { background: var(--success); }
.stat-todayDineInCount::before  { background: var(--warning); }
.stat-todayTakeoutCount::before { background: var(--info); }

.stat-label {
  font-size: 13px;
  color: var(--text-3);
  margin-bottom: var(--sp-2);
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-1);
  line-height: 1.2;
}

.stat-sub {
  font-size: 12px;
  color: var(--text-3);
  margin-top: var(--sp-1);
}

/* ── Section cards ── */
.section-card {
  margin-top: var(--sp-5);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-1);
}

/* ── Ranking ── */
.rank-medal {
  font-size: 18px;
}

.rank-num {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  font-size: 12px;
  font-weight: 600;
  color: var(--text-3);
  background: var(--surface-warm);
}

.amount-text {
  font-weight: 600;
  color: var(--danger);
}
</style>
