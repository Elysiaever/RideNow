// import 'vue-router'; // 先导入官方类型，再扩展（关键！）

// // 扩展RouteMeta接口（官方已定义该接口，直接补充字段即可）
// declare module 'vue-router' {
//     interface RouteMeta {
//         requiresAuth?: boolean; // 是否需要登录
//         role?: 'PASSENGER' | 'DRIVER'; // 角色权限
//         // 后续需要其他meta字段（比如title、icon），直接在这里加
//         // title?: string; // 页面标题
//         // icon?: string; // 菜单图标
//     }
// }