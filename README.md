# Autocomplete Lab

Java 前缀补全项目，包含可切换数据和算法的桌面 GUI、命令行入口及 JUnit 测试。JDK 17 或以上，使用 Maven 构建。

## 打开与运行

在 IntelliJ IDEA 中打开根目录 pom.xml，作为 Maven 项目导入。运行 app 包中的 AutocompleteGui.main，Program arguments 留空即可：默认 tiny.txt、linear、最多 10 条结果。数据与算法可在 GUI 内切换。已有的算法 TODO 保留，未完成实现不能用于查询。

主类完整名称：edu.course.autocomplete.app.AutocompleteGui。

```bash
mvn compile
java -cp target/classes edu.course.autocomplete.app.AutocompleteGui
```

可选启动参数用于指定初始配置，例如：

```bash
java -cp target/classes edu.course.autocomplete.app.AutocompleteGui data/benchmark/actors.txt linear 10
```

只在本地运行配置需要时设置 VM options 为 -Xmx2g。不要把 IDEA 的本机路径配置提交进仓库。

## 代码位置

所有包都在 edu.course.autocomplete 下，测试按相同职责分组：

| 包 | 内容 |
|---|---|
| app | GUI、命令行入口、算法创建与选项登记 |
| search | 检索合同、linear、binary、二分首尾边界工具 |
| model | 词条数据 Term |
| comparison | 前缀、权重、最终结果排名的三种比较规则 |
| io | 文本文件读取与格式验证 |
| compatibility | Princeton 数组接口的兼容包装 |
| benchmark | 查询耗时统计与 CSV 输出 |

main 分支保留 linear 与 binary；

源码中的 TODO 及测试初始失败是练习工程的既有状态；

作业要求见 [Java05：二分查找与 Autocomplete](assignments/java05_二分查找与Autocomplete.md)，详细讲解放在课程讲义中。

## 数据

数据全部位于本工程，不依赖外部教师目录。data/classroom 保存小样例，data/benchmark 保存大数据。完整清单见 [data/README.md](data/README.md)。

GUI 下拉框切换同目录文件，“打开数据文件…”选择其他目录。算法选项来自 app/EngineFactory。数据读取、排序和索引构建不计入 query 时间；比较时保持相同文件、前缀和显示数量。

## 测试

```bash
mvn test
mvn -Dtest=TermTest,DatasetLoaderTest,AutocompleteGuiDatasetTest,BenchmarkRunnerTest test
```

第一个命令运行全部已有测试，包括尚未完成的二分算法测试；第二个命令验证已提供的数据模型、加载器、GUI 配置及基准输出逻辑。

统一通过 Maven 构建和测试，不再维护额外的离线脚本及 JUnit JAR。

## Git

.gitignore 忽略 .idea、.vscode、target、编译产物、系统缓存及本地环境文件。Java 源码、测试、pom.xml 和数据文件保留在版本控制中。

完成本地验证后，通过 Git 提交并推送修改；克隆项目可得到相同的数据和工程结构。IDE 配置由每台机器本地生成。

## 分支

main 是当前二分查找练习工程；feature/trie-search 在此基础上加入 Trie 骨架及扩展测试。切换分支后重新编译，GUI 自动读取对应的算法列表。后续可将 feature/trie-search 合并回 main，TODO 的完成情况仍以测试为准。

## 每个源文件的职责

| 文件 | 职责 |
|---|---|
| app/AutocompleteGui.java | 窗口、数据与算法切换、结果展示和查询计时 |
| app/AutocompleteCli.java | 单次命令行查询 |
| app/EngineFactory.java | 登记算法名称，创建接口对应的实现 |
| search/AutocompleteEngine.java | 规定所有算法共同遵循的查询合同 |
| search/LinearAutocomplete.java | 扫描全部词条，提供已完成的对照实现 |
| search/BinarySearchAutocomplete.java | 将有序词条、前缀边界和结果输出接起来 |
| search/BinarySearchDeluxe.java | 按 Comparator 找第一个、最后一个相等位置 |
| model/Term.java | 保存文本和权重，提供自然顺序与比较规则入口 |
| comparison/PrefixOrderComparator.java | 只比较前 r 个位置，供前缀范围定位使用 |
| comparison/ReverseWeightComparator.java | 只按权重降序，权重相同返回零 |
| comparison/WeightThenQueryComparator.java | 按权重降序，再按文本升序，稳定确定展示顺序 |
| io/DatasetLoader.java | 读取声明数量、非负权重及原始查询文本 |
| compatibility/Autocomplete.java | 将 binary 的 List 结果适配为原课程的 Term[] API，不是 GUI 入口 |
| benchmark/BenchmarkRunner.java | 预热并多次测量 allMatches，输出中位数和 P95；与 GUI 两次调用合计计时口径不同 |

测试与被测类放在同名职责包中：GUI 配置、模型、加载器、二分工具、检索合同、数组接口兼容、性能 CSV 输出分别测试。

| 测试文件 | 验证内容 |
|---|---|
| app/AutocompleteGuiDatasetTest.java | 无参数启动、文件列表、数据替换和算法注册 |
| search/BinarySearchDeluxeTest.java | 重复元素首尾、空数组、无匹配及非法参数 |
| search/AutocompleteEngineContractTest.java | linear 与 binary 的结果、数量、排序和查询合同 |
| model/TermTest.java | 不可变词条、自然顺序、权重与前缀比较规则 |
| io/DatasetLoaderTest.java | UTF-8 文本、记录数、制表符、权重和末尾空行 |
| compatibility/AutocompleteCompatibilityTest.java | 旧数组接口的结果、输入拷贝及非法参数 |
| benchmark/BenchmarkRunnerTest.java | 性能统计 CSV 的表头、行数和匹配数 |
