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
| app | GUI、命令行入口 |
| engine | 搜索接口、工厂、linear / binary 与结果排名 |
| model | Term 和词条比较规则 |
| search | BinarySearchDeluxe 二分边界工具 |
| data | 数据加载 |
| benchmark | 查询性能测量与 CSV 输出 |

main 分支保留 linear 与 binary；后续 Trie 及大小写无关搜索的扩展测试保存在 feature/trie-search 分支。源码中的 TODO 及测试初始失败是练习工程的既有状态；任务与讲解放在课程讲义中。

## 数据

数据全部位于本工程，不依赖外部教师目录。data/classroom 保存小样例，data/benchmark 保存大数据，data/expected-output 保存输出格式示例。完整清单见 [data/README.md](data/README.md)。

GUI 下拉框切换同目录文件，“打开数据文件…”选择其他目录。算法选项来自 engine/EngineFactory。数据读取、排序和索引构建不计入 query 时间；比较时保持相同文件、前缀和显示数量。

## 测试

```bash
mvn test
mvn -Dtest=TermTest,DatasetLoaderTest,AutocompleteGuiDatasetTest,BenchmarkRunnerTest test
```

第一个命令运行全部已有测试，包括尚未完成的二分算法测试；第二个命令验证已提供的数据模型、加载器、GUI 配置及基准输出逻辑。

离线测试脚本在 scripts/test.sh 和 scripts/test.ps1，配套 JUnit 位于 scripts/lib。

## Git

.gitignore 忽略 .idea、.vscode、target、编译产物、系统缓存及本地环境文件。Java 源码、测试、pom.xml、scripts/lib 中的离线依赖和数据文件保留在版本控制中。

完成本地验证后，通过 Git 提交并推送修改；克隆项目可得到相同的数据和工程结构。IDE 配置由每台机器本地生成。

## 分支

main 是当前二分查找练习工程；feature/trie-search 在此基础上加入 Trie 骨架及扩展测试。切换分支后重新编译，GUI 自动读取对应的算法列表。后续可将 feature/trie-search 合并回 main，TODO 的完成情况仍以测试为准。
