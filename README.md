# Create Powerful Panel
Create Powerful Panel 是一个为 Create 模组添加高级工厂面板功能的扩展模组，提供了配方倍乘功能，让玩家能够更灵活地控制工厂面板的输出数量。

## 功能特性
- 配方倍乘 ：为工厂面板添加了倍乘功能，可设置从 -1（无倍乘）到 6400 的倍乘系数
- 直观的 GUI 界面 ：在工厂面板界面中添加了一个专门的倍乘控制组件，使用潜影盒图标显示
- 多语言支持 ：支持英文和中文语言
- 网络同步 ：通过网络数据包确保客户端和服务器之间的倍乘设置同步
## 如何使用
1. 放置并配置一个工厂面板
2. 右键点击打开工厂面板界面
3. 在界面中找到潜影盒图标，使用上下箭头调整倍乘系数
4. 倍乘系数为 -1 表示无倍乘，其他值表示输出数量的倍数
5. 点击确认按钮保存设置
## 安装方法
1. 确保已安装 Minecraft Forge 或 NeoForge（兼容版本）
2. 确保已安装 Create 模组（兼容版本）
3. 将 Create Powerful Panel 模组的 jar 文件放入 mods 文件夹
4. 启动游戏，享受增强的工厂面板功能
## 兼容性
- 适用于 Minecraft 1.21.1
- 需要 Create 模组
- 兼容 NeoForge
## 技术细节
- 模组 ID ：createpowerfulpanel
- 版本 ：1.0.0
- 主要类 ：
    - CreatePowerfulPanel ：主模组类
    - MixinFactoryPanelScreen ：修改工厂面板界面
    - RecipeMultiplier ：倍乘功能接口
    - CPPPackets ：网络数据包处理
## 许可证
All Rights Reserved

## 开发者
- jooi
## 鸣谢
- 感谢 Create 模组团队提供的优秀基础
- 感谢 NeoForged 提供的模组开发工具
  注 ：本模组为 Create 模组的扩展，需要 Create 模组才能正常运行。