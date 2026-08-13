
# Restaurant Management Admin UI Skill

## 1. Design Direction

后台管理系统整体采用：

> **简约、克制、轻餐饮品牌感、偏高级餐厅管理系统**

视觉关键词：

* 米白色背景
* 暖灰 / 棕色文字
* 白色内容卡片
* 细边框
* 大留白
* 低饱和度
* 少量阴影
* Serif 标题字体
* 无强烈渐变
* 无大面积鲜艳颜色
* 不使用过度圆角
* 不追求传统后台系统的「蓝色 + Element Plus 卡片」风格

整体感觉接近：

```text
现代餐厅
+
精品咖啡馆
+
轻量级管理后台
```

---

# 2. Global Color System

## 页面背景

```css
background: #f7f5f1;
```

用于整个管理页面。

---

## 主文字

```css
color: #302b27;
```

用于：

* 页面标题
* 重要文字
* 当前选中状态
* 核心数字

---

## 次级文字

```css
color: #8b8176;
```

用于：

* 页面描述
* 标签
* 辅助说明
* 时间
* 次要信息

---

## 边框

主要使用：

```css
border: 1px solid #e4ddd5;
```

或者：

```css
border: 1px solid #e6e0d9;
```

分割线：

```css
border-color: #eee9e4;
```

---

## 主按钮

```css
background: #3d3731;
color: white;
```

Hover：

```css
background: #51483f;
```

---

## Hover 背景

```css
background: #eee9e3;
```

---

# 3. Typography

## 页面标题

使用：

```css
font-family: "Cormorant Garamond", serif;
```

典型：

```css
.page-header h1 {
  margin: 0;
  font-size: 42px;
  font-weight: 500;
  color: #302b27;
}
```

标题不要使用特别粗的字体。

---

## 页面英文眉题

页面标题上方使用英文小标签：

```text
DISH MANAGEMENT
TAKEAWAY
ORDER MANAGEMENT
CATEGORY MANAGEMENT
TABLE MANAGEMENT
```

样式：

```css
.eyebrow {
  font-size: 11px;
  letter-spacing: 3px;
  color: #8b8176;
  margin-bottom: 8px;
}
```

作用：

> 增加页面层次和餐厅品牌感。

---

## 页面描述

```css
font-size: 14px;
color: #8b8176;
```

例如：

```text
管理餐厅菜品、分类与上架状态
管理外卖订单与配送前的制作流程
```

---

# 4. Page Layout

所有主要后台页面建议采用：

```text
页面
│
├── Header
│   ├── Eyebrow
│   ├── H1
│   ├── Description
│   └── Actions
│
├── Stats
│
├── Toolbar
│
├── Content
│
└── Dialog / Detail
```

页面基础：

```css
.page {
  padding: 36px 42px;
  min-height: 100%;
  background: #f7f5f1;
}
```

---

# 5. Page Header

统一：

```css
.page-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 32px;
}
```

左侧：

```text
英文模块名

中文页面标题

页面说明
```

右侧：

```text
刷新
新增 / 创建 / 其他主要操作
```

例如：

```text
DISH MANAGEMENT

菜品管理
管理餐厅菜品、分类与上架状态

                              ↻ 刷新   ＋ 新增菜品
```

---

# 6. Stats Cards

统计区域使用简洁白色卡片。

```css
.stats {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 14px;
  margin-bottom: 28px;
}
```

卡片：

```css
.stat-card {
  background: #fff;
  padding: 22px;
  border: 1px solid #e6e0d9;
}
```

标签：

```css
.stat-card span {
  display: block;
  font-size: 12px;
  color: #999087;
  margin-bottom: 10px;
}
```

数字：

```css
.stat-card strong {
  font-family: "Cormorant Garamond", serif;
  font-size: 32px;
  font-weight: 500;
  color: #302b27;
}
```

---

# 7. Toolbar

Toolbar 不使用厚重卡片。

主要采用：

```text
底部细线
```

例如：

```css
.toolbar {
  border-bottom: 1px solid #ddd6ce;
  margin-bottom: 24px;
}
```

---

# 8. Filter Tabs

筛选按钮采用「文字 + 底部线」形式。

不要做成：

```text
[全部] [已上架] [已下架]
```

这种传统 Element Plus 按钮组。

而是：

```text
全部    已上架    已下架
────
```

样式：

```css
.filter-group {
  display: flex;
  gap: 28px;
}

.filter-group button {
  border: none;
  background: transparent;
  padding: 12px 0;
  color: #918980;
  cursor: pointer;
  font-size: 14px;
  border-bottom: 2px solid transparent;
}

.filter-group button.active {
  color: #302b27;
  border-bottom-color: #302b27;
}
```

---

# 9. Search / Select

搜索区域可以放在 Toolbar 右侧。

原则：

* 不使用强烈边框颜色
* 不使用蓝色 focus 风格
* 控件尽量简洁
* 与页面整体暖色调保持一致

结构：

```text
[全部 已上架 已下架]             [全部分类] [搜索菜品名称...]
```

---

# 10. Content Cards

主要内容使用白色卡片：

```css
background: #fff;
border: 1px solid #e4ddd5;
```

Hover：

```css
box-shadow:
  0 10px 30px rgba(40, 35, 30, .06);
```

不要使用：

```css
border-radius: 20px;
```

这种非常明显的 SaaS 风格。

当前项目更偏：

```text
轻微 / 无圆角
+
细边框
+
留白
```

---

# 11. Dish Card

菜品管理采用：

```text
┌─────────────────────────────────────┐
│ 图片 │ DISH                         │
│      │ 菜品名称              已上架 │
│      │                              │
│      │ 分类     价格    排序  时间   │
│      │                              │
│      │ 描述                         │
│      │                              │
│      │ 编辑菜品          下架 删除  │
└─────────────────────────────────────┘
```

核心结构：

```text
dish-card
├── dish-image
└── dish-content
    ├── dish-top
    ├── dish-info
    ├── description
    └── dish-bottom
```

---

# 12. Status System

状态使用低饱和度颜色。

## 待确认

```css
.status1 {
  background: #f1ebe4;
  color: #7d6855;
}
```

## 制作中

```css
.status2 {
  background: #eee7dc;
  color: #755b43;
}
```

## 待取餐 / 待上菜

```css
.status3 {
  background: #e9eee9;
  color: #526354;
}
```

## 已完成

```css
.status4 {
  background: #e5eee7;
  color: #46604d;
}
```

## 已取消

```css
.status5 {
  background: #eee8e8;
  color: #806767;
}
```

菜品：

```css
.status1 {
  background: #e5eee7;
  color: #46604d;
}

.status0 {
  background: #eee8e8;
  color: #806767;
}
```

原则：

> **状态颜色只做辅助，不抢视觉中心。**

---

# 13. Information Layout

信息展示优先采用：

```css
display: grid;
grid-template-columns: repeat(4, 1fr);
```

例如：

```text
分类          价格          排序          创建时间
川菜          ¥38           10            2026-08-11
```

标签：

```css
font-size: 12px;
color: #9b938b;
```

内容：

```css
font-size: 14px;
font-weight: 500;
color: #403a35;
```

---

# 14. Money

金额使用 Serif 字体：

```css
.amount {
  font-family: "Cormorant Garamond", serif;
  font-size: 20px;
}
```

例如：

```text
¥38.00
```

订单总金额：

```css
font-family: "Cormorant Garamond", serif;
font-size: 28px;
font-weight: 500;
```

---

# 15. Buttons

## Primary

用于：

* 新增
* 创建
* 开始制作
* 制作完成
* 确认完成
* 保存修改

```css
.primary-btn {
  border: none;
  background: #3d3731;
  color: white;
  padding: 10px 18px;
  cursor: pointer;
}
```

---

## Secondary

用于：

* 上架
* 下架
* 次级操作

尽量使用：

```css
background: transparent;
border: 1px solid #d8d0c7;
color: #4d4640;
```

---

## Detail

查看详情等低优先级操作：

```css
.detail-btn {
  border: none;
  background: transparent;
  color: #70675f;
}
```

Hover：

```css
color: #302b27;
```

---

## Danger

删除 / 取消等危险操作使用低调的危险色。

不要做成非常鲜艳的红色大按钮。

---

# 16. Dialog

Element Plus 的 `el-dialog` 可以继续使用。

例如：

```vue
<el-dialog
  v-model="dialogVisible"
  title="新增菜品"
  width="620px"
>
```

但 Dialog 内部内容仍然保持当前设计语言：

```text
简洁
大量留白
细分割线
低饱和颜色
```

Footer：

```text
取消        创建菜品
```

而不是大量 Element Plus 默认按钮。

---

# 17. Empty State

统一：

```text
—

暂无数据

当前没有符合条件的数据
```

例如：

```css
.loading,
.empty {
  text-align: center;
  padding: 80px 20px;
  color: #918980;
}
```

空状态不要使用特别大的插画。

---

# 18. Loading

统一简单文字：

```text
正在加载菜品...
正在加载订单...
正在加载数据...
```

保持克制。

---

# 19. Order Management Style

外卖管理已经确定为主要参考页面。

订单卡片：

```text
┌───────────────────────────────────────┐
│ ORDER                         待确认 │
│ #202608101234                         │
│                                       │
│ 取餐人       下单时间     类型   金额 │
│ 张三         18:30       外卖   ¥68  │
│                                       │
│ 查看详情             开始制作  取消   │
└───────────────────────────────────────┘
```

因此后续：

* 堂食管理
* 外卖管理
* 订单管理
* 我的订单
* 订单详情

都尽量遵循这个结构。

---

# 20. Detail Dialog

详情统一采用：

```text
订单基本信息
────────────

顾客信息

────────────

ORDER ITEMS

菜品              数量       小计

────────────

TOTAL                         ¥xxx
```

例如菜品：

```css
.dish-row {
  display: grid;
  grid-template-columns: 1fr 80px 100px;
  align-items: center;
}
```

---

# 21. Responsive

桌面优先。

900px 以下：

```css
.stats {
  grid-template-columns: repeat(2, 1fr);
}

.order-info {
  grid-template-columns: repeat(2, 1fr);
}
```

600px 以下：

```css
.page {
  padding: 24px 18px;
}

.order-info {
  grid-template-columns: 1fr;
}
```

---

# 22. Vue Code Style

Vue 页面统一使用：

```vue
<script setup>
```

API 不直接散落在页面中。

优先：

```js
import {
  getDishList,
  addDish,
  updateDish,
  deleteDish,
  updateDishStatus
} from "@/api/dish"
```

而不是：

```js
request.get("/dish")
request.post("/dish")
request.put("/dish")
```

页面负责：

```text
UI
状态
交互
数据展示
```

API 文件负责：

```text
HTTP 请求
```

---

# 23. API Naming

统一：

```js
getDishList()
addDish()
updateDish()
deleteDish()
updateDishStatus()
```

例如：

```js
export function getDishList(params) {
  return request.get("/dish", {
    params
  })
}
```

页面调用：

```js
const res = await getDishList(params)
```

---

# 24. Function Naming

页面方法推荐：

```text
loadDishes()
loadCategories()

openAdd()
openEdit()

saveDish()

changeDishStatus()
deleteDish()

changeStatusFilter()

countByStatus()

getCategoryName()

formatTime()
```

避免同一个函数承担多个含义。

例如：

❌

```js
changeStatus()
```

同时处理：

```text
筛选状态
+
修改菜品状态
```

应该拆成：

```js
changeStatusFilter()
changeDishStatus()
```

---

# 25. Data Flow

推荐：

```text
Vue Page
   ↓
API Module
   ↓
Axios / request
   ↓
Spring Boot Controller
   ↓
Service
   ↓
Mapper
   ↓
MySQL
```

不要在 Vue 页面中直接设计后端业务逻辑。

---

# 26. CRUD 页面统一模板

以后做：

* 菜品管理
* 分类管理
* 餐桌管理
* 原材料管理
* 费用管理

都优先采用：

```text
Header
   ↓
Stats
   ↓
Toolbar
   ↓
List / Card
   ↓
Dialog
```

例如费用管理：

```text
FEE MANAGEMENT

费用管理
管理人工、水电煤气及行政费用

                          ↻ 刷新   ＋ 新增费用

全部费用   本月费用   人工费   水电煤气   行政费

────────────────────────────────────

费用记录卡片
```

而不是每个页面重新设计一套 UI。

---

# 27. Overall Design Rule

最重要的一条：

> **后续新增页面不要追求“新风格”，而是把已有的外卖管理页面作为 Design System 基准。**

统一：

```text
米白背景
↓
暖棕文字
↓
白色卡片
↓
细边框
↓
Serif 大标题
↓
英文 Eyebrow
↓
低饱和状态色
↓
细线 Tab
↓
简洁按钮
↓
大量留白
```

最终整个后台应该给人的感觉是：

> **这是同一个餐厅管理系统，而不是几个不同开发者拼起来的页面。** 🍽️

