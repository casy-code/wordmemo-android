# 仪器化测试用例总结

## 一、现有用例（按模块）

### 1. 应用启动 (AppLaunchTest) - 2 个
| 用例 | 说明 |
|------|------|
| appLaunchesSuccessfully | 验证 MainActivity 启动不崩溃，nav_host/nav_view 存在 |
| applicationContextIsWordMemoApplication | 验证 Application 为 WordMemoApplication 且 appContainer 已初始化 |

### 2. MainActivity (MainActivityTest) - 4 个
| 用例 | 说明 |
|------|------|
| mainActivityLaunches | 验证 MainActivity 可启动 |
| bottomNavigationViewIsDisplayed | 验证底部导航栏显示 |
| learningFragmentIsStartDestination | 验证默认显示学习页面 |
| navHostFragmentIsDisplayed | 验证 NavHostFragment 显示 |

### 3. Fragment 单元 (LearningFragmentTest, ReviewFragmentTest) - 4 个
| 用例 | 说明 |
|------|------|
| learningFragmentLaunchesWithoutCrash | LearningFragment 启动不崩溃 |
| learningFragmentShowsContent | LearningFragment 内容加载 |
| testReviewFragmentLaunches | ReviewFragment 启动 |
| testReviewFragmentViewsCreated | ReviewFragment 视图创建 |

### 4. UI 集成 (UIIntegrationTest) - 2 个
| 用例 | 说明 |
|------|------|
| testLearningFragmentNavigation | LearningFragment 导航 |
| testLearningFragmentLoads | LearningFragment 加载 |

### 5. 数据库 (AppDatabaseTest) - 4 个
| 用例 | 说明 |
|------|------|
| testDatabaseCreation | 数据库创建及 DAO 可用 |
| testCompleteWorkflow | 完整工作流（词库→单词→学习记录） |
| testMultipleWordLists | 多词库管理 |
| testCascadeDelete | 级联删除 |

### 6. DAO 层 (WordDaoTest, WordListDaoTest, LearningRecordDaoTest) - 26 个
- **WordDaoTest** (10): insert, multiple, update, delete, getByContent, search, byDifficulty, deleteAll, getAllWords
- **WordListDaoTest** (9): insert, multiple, update, delete, getByName, getAll, deleteAll, flow
- **LearningRecordDaoTest** (7): insert, getByWordAndList, update, delete, todayCount, getRecordsByListId, deleteAll

### 7. 集成测试 (DataPersistence, FinalIntegration, LearningFlowIntegration) - 25 个
- **DataPersistenceIntegrationTest** (9): 词库/单词/学习记录持久化、事务、级联删除、批量、一致性、搜索、更新
- **FinalIntegrationTest** (9): 完整流程、多词库、错误处理、压力、并发、备份、稳定性、边界
- **LearningFlowIntegrationTest** (7): 学习流程、持久化、多词、复习、失败恢复、并发、统计

### 8. 性能 (PerformanceTest) - 9 个
| 用例 | 说明 |
|------|------|
| testBulkInsertPerformance | 批量插入 1000 单词 |
| testSearchPerformance | 搜索性能 |
| testSM2AlgorithmPerformance | SM-2 算法性能 |
| testLearningRecordInsertPerformance | 学习记录插入 |
| testQueryPerformance | 查询性能 |
| testMemoryUsageWithLargeDataset | 大数据集内存 |
| testConcurrentOperationsPerformance | 并发操作 |
| testUpdatePerformance | 更新性能 |

---

### 9. StatisticsFragmentTest - 3 个（新增）
| 用例 | 说明 |
|------|------|
| statisticsFragmentLaunchesWithoutCrash | 统计页启动不崩溃 |
| statisticsFragmentShowsStatsCards | 统计卡片显示 |
| statisticsFragmentRefreshButtonExists | 刷新按钮存在 |

### 10. SettingsFragmentTest - 2 个（新增）
| 用例 | 说明 |
|------|------|
| settingsFragmentLaunches | 设置页启动 |
| settingsFragmentShowsContent | 设置页内容显示 |

### 11. MainActivity 导航增强 - 4 个（新增）
| 用例 | 说明 |
|------|------|
| canNavigateToStatisticsTab | 可切换到统计 tab |
| canNavigateToSettingsTab | 可切换到设置 tab |
| canNavigateToReviewTab | 可切换到复习 tab |
| canNavigateBackToLearningTab | 可切回学习 tab |

---

## 二、总计

- **现有**: 约 76 个仪器化用例
- **补充后**: 约 85 个用例
- **覆盖**: 应用启动、主界面、4 个 Fragment、底部导航、数据库、DAO、集成、性能
