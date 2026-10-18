# 餐饮管理系统 · 代码审查报告

审查范围：`restaurant-frontend`（Vue 3 + Element Plus，14385 行）与 `restaurant-management`（Spring Boot 3.5 + MyBatis-Plus，4033 行）
审查时间：2026-08-31

---

## 总览

| 维度 | 评价 | 说明 |
|---|---|---|
| 分层结构 | 良好 | Controller / Service / Mapper / DTO / VO 职责清晰，DTO 与实体分离到位 |
| SQL 安全 | 良好 | 4 个 Mapper XML 全部使用 `#{}` 预编译，无 SQL 注入 |
| 业务校验 | 中等 | 订单状态机、桌号查重、分类查重都做了，但覆盖不全 |
| **权限控制** | **不及格** | 只认证不授权，任何登录用户可调用全部管理接口 |
| 数据安全 | 不及格 | 密码明文存储、JWT 密钥硬编码进 Git |
| 事务管理 | 中等 | 关键写操作有 `@Transactional`，但缺 rollbackFor 与并发保护 |
| 可维护性 | 中等 | 组件过大、调试代码残留、死代码未清理 |
| 测试 | 不及格 | 前后端均无有效测试 |

---

## P0 · 必须立即修复（安全漏洞）

### 1. 只认证不授权，任意登录用户可操作全部管理接口

`JwtInterceptor.java:107-120` 解析出 `role` 后存入 request，**但全项目没有任何一处读取它做判断**：

```java
request.setAttribute("role", claims.get("role"));
return true;   // 只要签名合法就无条件放行
```

同时 `WebConfig.java:22-35` 只放行了三个登录注册接口，其余 `/api/**` 一律只要求「有合法 token」。

后果：顾客（role=user）登录后，直接 curl 即可调用：

| 接口 | 越权后果 |
|---|---|
| `GET /api/orders` | 查看全店所有订单 |
| `PUT /api/orders/status` | 把任意订单改成「已完成」「已取消」 |
| `GET /api/users` | 拉取全部用户名单 |
| `POST /api/user` | **创建 role=admin 的管理员账号（提权）** |
| `DELETE /api/user/{id}` | 删除任意账号（仅 admin 用户名被保护） |
| `POST/PUT/DELETE /api/dish` | 改菜品价格、删菜品 |
| `POST /api/orders/takeaway` | 伪造外卖订单 |

前端路由守卫只是障眼法——它拦的是浏览器里的页面跳转，拦不住 API 请求。

**修复方案**：引入 Spring Security 或自建角色注解。

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    String[] value();
}
```

在拦截器/切面中读取方法上的注解，与 `claims.get("role")` 比对。管理类接口统一标注 `@RequireRole("admin")`，顾客接口标注 `@RequireRole({"user","admin"})`。

另外 `UserAddRequest.role` 只有 `@NotBlank`，无取值白名单，必须加枚举校验，防止传 `admin` 提权。

### 2. 密码明文存储与比对

`UserService.java:54-62`：

```java
wrapper.eq("username", username)
       .eq("password", password);   // 数据库里存的是明文
```

`CustomerService.java:48-51` 同样用 `equals` 直接比对明文。注册路径 `CustomerService.java:114` 也是原样入库。

数据库一旦泄露，所有用户密码裸奔；而且很多用户多平台复用密码。

**修复**：加 `spring-security-crypto`，用 `BCryptPasswordEncoder`。

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
// 注册：encoder.encode(rawPassword)
// 登录：encoder.matches(rawPassword, user.getPassword())
```

注意 `User` 实体已经加了 `@JsonIgnore`（`User.java:16`），密码不会回传前端，这点做得对。

### 3. JWT 密钥硬编码并已提交进 Git

`JwtUtils.java:12-13`：

```java
private static final String SECRET = "restaurant-management-secret-key-2026";
```

这个字符串已在 Git 历史里（`git log` 可见）。任何人拿到仓库就能自己签一个 `role=admin` 的 token，直接绕过登录。配合第 1 条是致命组合。

**修复**：
- 密钥移到 `application.yml` 或环境变量，走 `${jwt.secret}` 注入
- 轮换密钥（旧密钥已泄露，必须换）
- `application.yml` 加入 `.gitignore`，改用 `application-example.yml` 模板

### 4. 数据库账号密码写进配置文件并入库

`application.yml:10-11`：`root` / `123456`。同样已提交。

**修复**：改用环境变量 `${DB_USERNAME}` / `${DB_PASSWORD}`，配置外置。

---

## P1 · 功能缺陷与并发风险（高）

### 5. 取消订单不释放餐桌 —— 业务 bug

`OrdersService.java:187` 的释放逻辑只在 `status == 4`（已完成）时触发：

```java
if (status == 4 && oldOrder.getOrderType() == 1 && ...) {
    table.setStatus(0);
    tableInfoMapper.updateById(table);
}
```

而状态机允许「待确认 → 已取消」（`OrdersService.java:141`）。**订单取消后餐桌永远卡在「使用中」，再也接不了新客，且无法删除（`TableService.java` 拒绝删除 status==1 的桌）。**

**修复**：条件改为 `status == 4 || status == 5`。

### 6. 下单的「检查-再更新」存在并发竞态

`OrdersService.java:282-290`：

```java
if (table.getStatus() != 0) {
    throw new RuntimeException("该餐桌当前不可用");
}
table.setStatus(1);
tableInfoMapper.updateById(table);   // 两步之间无锁
```

两个顾客同时下单同一张桌，都能通过检查，后一个覆盖前一个。

**修复**：改为条件更新并判断影响行数：

```java
LambdaUpdateWrapper<TableInfo> uw = new LambdaUpdateWrapper<>();
uw.eq(TableInfo::getId, table.getId())
  .eq(TableInfo::getStatus, 0)
  .set(TableInfo::getStatus, 1);
if (tableInfoMapper.update(null, uw) == 0) {
    throw new RuntimeException("该餐桌已被占用");
}
```

### 7. 订单号用时间戳，并发必撞

`OrdersService.java:254` 和 `:433`：

```java
order.setOrderNo(System.currentTimeMillis() + "");
```

同一毫秒内两单就重复，且订单号可枚举（泄露经营量）。

**修复**：改用 `yyyyMMddHHmmss + 6位随机/序列`，或雪花 ID。

### 8. 创建订单接口漏加 `@Valid`，空明细直接 NPE

`OrdersController.java:137`：

```java
public Result<?> add(@RequestBody OrderAddRequest request, ...) {
```

没有 `@Valid`，所以 `OrderAddRequest` 上的 `@NotNull details` **完全不生效**。传 `{"orderType":1,"details":null}` 会在 `OrdersService.java:223` 的 for 循环处抛 NPE，被全局异常处理器兜成「服务器内部异常」。

旁边的 `addTakeaway`（`:159`）就正确加了 `@Valid`，说明是遗漏。

**修复**：补 `@Valid`，并在 Service 内加 `if (CollectionUtils.isEmpty(details)) throw ...`。同时建议给 `OrderDetailRequest` 补数量校验。

### 9. 下单不校验菜品是否下架

`OrdersService.add` 和 `addTakeaway` 只判断菜品是否存在（`:228`、`:391`），没判断 `dish.getStatus()`。已下架（status=0）的菜仍然能被下单。

**修复**：增加 `if (dish.getStatus() != 1) throw new RuntimeException("菜品已下架：" + dish.getName());`

### 10. 删除菜品/分类不做关联校验，产生悬空数据

`DishService.java` 的 `delete` / `batchDelete` 直接物理删除。删掉后：
- 历史订单明细里的 `dish_id` 指向不存在的菜
- `DashboardMapper.xml` 的 `hotDishes` 按 `dish_id` 分组，历史数据会错位或丢失
- `CategoryService.delete` 同样，删分类后其下菜品 `category_id` 悬空，前台菜单该分类直接消失

**修复**：改成软删除（加 `deleted` 字段 + MyBatis-Plus 逻辑删除），或在删除前检查是否被引用并给出提示。菜品建议直接上软删除。

### 11. 仪表盘统计在空数据日返回 NULL

`DashboardMapper.xml` 的 `statistics` 里，只有 `todayAmount` 加了 `COALESCE`，其余四个：

```sql
SUM(CASE WHEN order_type = 1 THEN 1 ELSE 0 END) AS todayDineInCount,
SUM(CASE WHEN order_type = 2 THEN 1 ELSE 0 END) AS todayTakeoutCount,
SUM(CASE WHEN status IN (1,2,3) THEN 1 ELSE 0 END) AS pendingOrderCount,
SUM(CASE WHEN status = 4 THEN 1 ELSE 0 END) AS completedOrderCount
```

当天没有任何订单时这些 SUM 返回 NULL，`DashboardVO` 用 `Integer` 接收，前端拿到 null 后图表/数字会显示异常。

**修复**：全部套 `COALESCE(..., 0)`。

### 12. 上传接口的两个问题

`UploadController.java:26-28`：

```java
String suffix = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
```

文件名不含 `.` 时 `lastIndexOf` 返回 -1，`substring(-1)` 抛 `StringIndexOutOfBoundsException` → 500。

另外只校验扩展名，不校验文件内容。伪造 `shell.jpg`（实为 JSP/PHP）可上传，若后续部署到支持脚本解析的容器有 RCE 风险。

`UploadController.java:48-51` 用 `System.getProperty("user.dir")` 存文件——打成 jar 部署后，文件会落在启动命令所在目录，重新部署或换目录启动即丢失。

**修复**：
- 判空：`int idx = originalName.lastIndexOf('.'); if (idx < 0) return Result.error("文件格式错误");`
- 读文件头 magic bytes 校验真实类型
- 上传目录改为配置项 `app.upload-path`，默认值用绝对路径

### 13. 全局异常处理把业务异常统一返回 500

`GlobalExceptionHandler.java:13-19` 捕获 `RuntimeException` 返回 `Result.error()`（code 写死 500，`Result.java:24`）。业务校验失败（「用户名已存在」「餐桌不存在」）和真正的系统故障无法区分，前端也没法针对性处理。

**修复**：定义 `BusinessException`（`exception/BusinessException.java` 已存在但未使用），业务异常用它承载，处理器返回 code 400；未知异常返回 500 并保留堆栈日志。

### 14. 管理员登录有 NPE 风险

`LoginController.java:38`：`user.getRole().equals("user")` —— `role` 字段在数据库中若为 NULL 会直接 NPE。历史数据或手工插入的数据很可能没有 role。

**修复**：改为 `"user".equals(user.getRole())`。

---

## P2 · 工程质量与可维护性（中）

### 15. 前端路由导入路径大小写不匹配 —— 部署即失败

`router/index.js:14`：

```js
import Table from '@/views/table/Table.vue'   // 小写 table
```

实际目录是 `src/views/Table/`（大写 T，已确认）。

Windows 文件系统大小写不敏感，本地 `npm run dev` 能跑通；**但 Linux 构建机、Docker、Vercel/Netlify 上会直接 `Module not found` 编译失败**。这是典型的「本地好的、上线挂了」。

**修复**：改成 `@/views/Table/Table.vue`。建议在仓库加 ESLint 规则或在 CI 上做 Linux 构建来提前暴露。

### 16. `getOrderDetail` 把 ID 当 axios 配置传

`api/order.js`：

```js
export function getOrderDetail(id){
  return request.get("/orders/"+id, id)   // 第二个参数是 config，不是参数
}
```

传数字进去 axios 会忽略（不报错），但语义错误，将来加超时/取消token 时会冲突。改成 `request.get("/orders/" + id)` 即可。

### 17. 购物车金额用浮点累加

`stores/cart.js:25-38`：

```js
return total + Number(item.price) * item.quantity
```

JS 浮点会产生 `0.30000000000000004` 这类结果，金额展示和后续对账都会出问题。

**修复**：内部统一用「分」为单位的整数运算，展示时再除 100；或引入 decimal.js。

### 18. 前端路由守卫只判断有没有缓存，不校验 token 有效性

`router/index.js:173-192` 只检查 `localStorage.getItem("user")` 是否存在。token 2 小时过期后，页面照样能进，直到某个接口返回 401 才暴露。

同时 `utils/request.js:23-31` 的响应拦截器只做 `console.error` 后 reject，**没有统一处理 401 跳转登录**，每个调用点都要自己写。

**修复**：响应拦截器里判断 `error.response.status === 401` → 清除本地缓存 → `router.push('/login')`；守卫里顺带校验 token 过期时间（JWT payload 里有 exp，前端可直接解）。

### 19. 后端地址硬编码散落 6 个文件

`Cart.vue:188`、`Dish.vue:56/59/134`、`DishDetail.vue:130`、`Home.vue:697`、`Menu.vue:149`、`OrderMenu.vue:242` 都写死了 `http://localhost:8080`。

上线换域名要改 6 处，且这些请求绕过 axios 实例，**不带 token**。

**修复**：统一到 `.env` 的 `VITE_API_BASE_URL` + Vite dev proxy，图片拼接用统一工具函数。

### 20. CORS 放行所有来源，且重复配置

`CorsConfig.java:13-18` 用 `allowedOriginPatterns("*")`；`DishController.java:16` 和 `UploadController.java:14` 又各自加了 `@CrossOrigin`，配置重复。

开发期可以，生产必须收紧到具体域名列表。

### 21. 组件过大，需要拆分

| 文件 | 行数 |
|---|---|
| `views/Home.vue` | 2750 |
| `views/takeaway/Takeaway.vue` | 1944 |
| `views/OrderMenu.vue` | 957 |
| `views/OrderConfirm.vue` | 843 |
| `views/Table/Table.vue` | 630 |

单个文件近 2000 行，模板、样式、逻辑混在一起，改一处要滚动半天。建议按功能块拆成子组件（如 `TakeawayOrderList.vue`、`TakeawayAddDialog.vue`），样式抽到统一设计变量。

### 22. 调试代码与死代码未清理

- 后端 **13 处** `System.out.println`（`UserService.java:84/90/94`、`CategoryService.java`、`OrdersService.java:206`、控制器里多处打印用户ID）
- 前端 **13 处** `console.log`
- 死代码：`OrdersMapper.myList`（从未被调用）、`OrderDetailMapper.xml` 的 `listByOrderId`（Service 用的是 LambdaQueryWrapper）
- `views/customer/Login.vue` 是游离文件（git 里未跟踪），与 `CustomerLogin.vue` 重复，建议删除

**修复**：`System.out.println` 全换 Slint4j（`GlobalExceptionHandler` 已经在用 `@Slf4j`，保持一致）。

### 23. `OrdersMapper` 两个查询字段别名不一致

`listByUserId` 返回 `o.order_no`、`o.total_amount` 等 snake_case 列名，但 `OrderVO` 的字段是 `orderNo`、`totalAmount`（`OrderVO.java:15`）。MyBatis 默认不开启驼峰映射时这些字段**映射不上，值为 null**。

`OrdersController.myOrders` 用的正是这个方法，需要验证前端「我的订单」里订单号是否显示为空。

**修复**：补 `AS orderNo` 等别名，或全局开启 `map-underscore-to-camel-case: true`。

### 24. 大段重复代码

`OrdersService.add` 与 `addTakeaway` 中「遍历明细 → 查菜品 → 计算小计 → 保存明细」的逻辑几乎完全一样（约 60 行 × 2）。

**修复**：抽成 `private List<OrderDetail> buildDetails(List<OrderDetailRequest> items, Long orderId)`。

### 25. `.gitignore` 未排除上传目录

`git status` 显示 `?? restaurant-management/upload/`，用户上传的图片会被提交进仓库，仓库体积随运行时间无限膨胀。

**修复**：后端 `.gitignore` 加 `upload/`。

### 26. 零测试

后端只有 Spring Initializr 生成的空 `contextLoads`，前端无任何测试。订单状态机、金额计算、购物车这些核心逻辑完全没有回归保护。

**建议**：优先给三块补单元测试——`OrdersService.updateStatus` 的状态流转、`OrdersService.add` 的金额计算、购物车 store 的加减与总价。

---

## 建议的修复顺序

**第一批（安全，上线前必须完成）**
1. 引入角色校验注解，堵住越权
2. BCrypt 加密密码
3. JWT 密钥外置并轮换，数据库凭据外置
4. `application.yml` 加入 .gitignore

**第二批（功能正确性）**
5. 取消订单释放餐桌
6. 占座改条件更新
7. 订单号生成改为不重复方案
8. 补 `@Valid` + 空明细校验
9. 下架菜品禁止下单
10. 仪表盘 SUM 补 COALESCE
11. 上传接口判空 + 校验内容
12. 删除改软删除

**第三批（工程质量）**
13. 修复 `Table.vue` 大小写导入
14. 401 统一处理 + token 过期校验
15. API 地址环境变量化
16. 清理调试代码与死代码
17. 拆分大组件
18. 补核心逻辑测试

---

## 值得肯定的地方

- **金额由后端计算**：`OrdersService` 一律从数据库查菜品单价重新算总额，不接受前端传价，这点做对了
- **SQL 全部预编译**：4 个 XML 无一处 `${}`，无注入风险
- **DTO 与实体分离**：请求用 DTO、响应用 VO，没有把实体直接暴露给接口
- **密码字段加 `@JsonIgnore`**：`User.java:16` 主动防回传
- **订单状态机**：定义清晰的状态流转表，并阻止终态再修改
- **参数校验注解**：`UserAddRequest`、`CustomerRegisterRequest`、`TakeawayAddRequest` 的校验写得规范
- **顾客/管理员登录隔离**：`CustomerService` 加了 `.eq(User::getRole, "user")`，管理员账号无法从顾客入口登录

整体看，这是一个完成度相当高的课程/毕设级项目，业务覆盖面广。主要差距集中在**安全纵深**和**并发场景下的数据一致性**上——补上这两块就能到可上线的水准。
