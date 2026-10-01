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

`data/benchmark` 中的文件是不同业务场景、不同规模的搜索词库。每次选择一份，两种算法使用同一份数据比较，不需要逐个运行。文件首行是记录数，后续每行是“权重 + 制表符 + 查询文本”；权重用于结果排名，不同数据集的权重含义可能不同。

| 数据文件（位于 `data/benchmark/`） | 记录数 | 内容与用途 |
|---|---:|---|
| `actors.txt` | 2,875,183 | 演员姓名；本课主要性能对比数据 |
| `words-333333.txt` | 333,333 | 英文单词；演示词语补全 |
| `mandarin.txt` | 94,339 | 中文词语；演示中文前缀补全 |
| `2grams.txt` | 277,718 | 两词短语；演示含空格的短语补全 |
| `3grams.txt` | 1,020,009 | 三词短语；演示短语补全及大数据查询 |
| `4grams.txt` | 1,034,307 | 四词短语；演示较长文本的补全 |
| `5grams.txt` | 1,044,268 | 五词短语；演示较长文本的补全 |
| `bing.txt` | 250,000 | 搜索词；模拟搜索框联想 |
| `alexa.txt` | 1,000,000 | 网站域名；模拟网址补全 |
| `cities.txt` | 93,827 | 城市名称；模拟地点选择 |
| `movies.txt` | 229,447 | 电影名称；模拟电影搜索 |
| `imdb-votes.txt` | 82,455 | 带投票权重的电影名称；观察按权重排序的结果 |
| `artists.txt` | 43,848 | 艺人名称；模拟音乐搜索 |
| `songs.txt` | 922,229 | 歌曲名称；模拟音乐搜索及大数据查询 |
| `metal-albums.txt` | 3,000 | 金属音乐专辑名称；快速体验专辑搜索 |
| `baby-names.txt` | 31,109 | 人名；模拟姓名补全 |
| `nasdaq.txt` | 2,658 | 公司名称；模拟公司搜索 |
| `pu-courses.txt` | 6,771 | 课程名称；模拟选课搜索 |
| `trademarks.txt` | 92,254 | 商标相关名称；体验另一种文本检索场景 |
| `redditors.txt` | 10,000 | Reddit 社区名称；模拟社区搜索 |
| `wiktionary.txt` | 10,000 | 英文词库；用于快速体验 |

本课可按以下顺序使用数据：

| 目的 | 数据文件 | 输入前缀 | 匹配数 |
|---|---|---|---:|
| 检查算法是否正确 | `data/classroom/tiny.txt` | 使用文件中词条的前缀 | 随前缀变化 |
| 比较 linear 与 binary 的查询耗时 | `data/benchmark/actors.txt` | `Frank W` | 309 |
| 说明同一套算法可处理中文 | `data/benchmark/mandarin.txt` | `我` | 17 |
| 补充英文词语示例 | `data/benchmark/words-333333.txt` | `app` | 468 |

当前查询按原始文本开头匹配，并区分大小写。中文和英文使用相同的前缀查找逻辑；输入 `wo` 不会自动匹配“我”，因为项目没有实现拼音转换、翻译或全文包含搜索。

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
| app/BenchmarkDialog.java | 展示性能报告和导出本次测量的 CSV 快照 |
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
| benchmark/BenchmarkRunner.java | 预热并多次测量计数与取结果，输出中位数和 P95；计时口径与 GUI 一致 |

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

## GUI 性能对比

输入前缀后点击“性能对比”，在后台按当前数据和显示上限测试所有已登记算法；main 中为 linear 与 binary。每种预热 20 次、测量 100 次，结果窗口展示匹配数、中位耗时、P95 以及展示结果是否一致。构建引擎、读取数据、检查一致性和界面绘制不计入查询时间。

点击报告中的“导出 CSV…”保存当前报告快照。未完成的算法显示错误原因，耗时留空，不能据此宣称结果一致；无需修改本课三个 TODO 之外的代码来使用此功能。
