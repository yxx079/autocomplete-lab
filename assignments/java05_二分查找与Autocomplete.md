# Java05 作业：完成 Autocomplete 的二分检索

本课最终成果是在已有 Autocomplete 项目中实现二分检索：输入一个前缀，找到全部匹配词条，并使用已经提供的排名与界面显示结果。整数数组 Lab 是实现前的练习；本项目最终需要完成两个文件中的三个方法。

## 1. 本次要实现的代码

所有源码路径都相对于项目根目录 `autocomplete-lab`。

| 文件 | 方法 | 实现要求 |
|---|---|---|
| [BinarySearchDeluxe.java](../src/main/java/edu/course/autocomplete/search/BinarySearchDeluxe.java) | `firstIndexOf(values, key, comparator)` | 返回第一个与 key 比较结果为零的下标；不存在或数组为空时返回 `-1` |
| 同上 | `lastIndexOf(values, key, comparator)` | 返回最后一个与 key 比较结果为零的下标；不存在或数组为空时返回 `-1` |
| [BinarySearchAutocomplete.java](../src/main/java/edu/course/autocomplete/search/BinarySearchAutocomplete.java) | `matchingRange(prefix)` | 组合前缀比较器及首尾查找方法，返回匹配区间 `{first, last}`；无匹配返回 `{-1, -1}` |

### firstIndexOf：找到后继续向左找

输入数组已经按传入的 Comparator 排好序。维护尚未排除的闭区间 `[low, high]`，每次检查中点。找到相等元素时，先保存它的位置，再继续查左侧，确认是否有更早的匹配。

例如 `{"a", "b", "b", "b", "c"}` 查找 `"b"`，应返回 `1`。

### lastIndexOf：找到后继续向右找

使用相同的二分结构，但相等时保存中点并继续检查右侧。上面的数组查找 `"b"`，应返回 `3`。

两个方法都必须使用 Comparator 判断大小，不能用 `==` 判断对象相等，也不能改成逐个扫描。若使用 `comparator.compare(values[middle], key)`，负数表示中点较小，往右查；正数表示中点较大，往左查。若颠倒比较参数，方向也要相应调整，不能混用两种写法。

### matchingRange：把边界查找接入前缀补全

按下面的步骤组合已有功能：

1. 使用 `Term.byPrefixOrder(prefix.length())` 创建前缀 Comparator。
2. 创建查询文本为 `prefix` 的临时 Term；权重不参与此次比较。
3. 调用 `firstIndexOf` 找到第一个匹配位置。
4. 若结果为 `-1`，返回 `{-1, -1}`。
5. 再调用 `lastIndexOf`，返回 `{first, last}`。

例如已经排序的词条为 `ape、app、apple、application、apply、banana`，输入 `app` 应得到 `{1, 4}`，数量为 `4 - 1 + 1 = 4`。不能找到 `apple` 就立即结束，因为还需要其他匹配项。

空前缀匹配全部词条；空词库没有匹配。边界查找要求使用 `O(log n)` 次比较；前缀比较本身还需要读取文本，因此不要把整个结果复制、排名过程都描述为 `O(log n)`。

## 2. 已提供的部分与本课范围

词条模型、比较器、数据读取、构造器中的预排序、权重排名、linear 对照实现、GUI 和计时逻辑已经提供。保持现有包名、类名、方法签名与测试要求，主要补全上述三个 TODO；无需重新写界面或数据加载。

Trie 和大小写无关搜索属于 `feature/trie-search` 分支的后续扩展，不是本课必做作业。本课使用 main 为起点，并在自己的作业分支中实现 binary。

## 3. 获取项目并创建自己的本地分支

先在 IntelliJ Terminal 或系统终端中进入项目根目录，即包含 `pom.xml` 的目录。使用 JDK 17 或以上及 Maven；也可以在 IntelliJ 中直接运行测试。

如果已经有本地项目，无需重新 clone。首次获取时，根据仓库权限选择一种方式：

- 有课程仓库写入权限：可以直接 clone `https://github.com/yxx079/autocomplete-lab.git`。
- 没有写入权限：先在 GitHub 上 Fork 到自己的账号，再从 Fork 页面复制克隆地址。后面 push 到自己的 Fork，而不是要求向课程仓库写入。

克隆自己的 Fork 的示例（将 `YOUR_USERNAME` 替换为自己的 GitHub 用户名）：

```bash
git clone https://github.com/YOUR_USERNAME/autocomplete-lab.git
cd autocomplete-lab
```

确认当前仓库、分支和本地修改：

```bash
git remote -v
git branch --show-current
git status
```

在没有未提交修改的情况下，更新 main，再创建并切换到作业分支：

```bash
git switch main
git pull --ff-only origin main
git switch -c homework/java05-yourname
```

将 `yourname` 换成自己的英文标识，例如 `homework/java05-liming`。`git switch -c` 同时创建本地分支并切换过去；此时 GitHub 上还没有这个分支。若分支已经创建过，使用 `git switch homework/java05-yourname`，不要再次加 `-c`。

发现 `git status` 中已有修改时，先确认它们属于哪个任务；不要为了切换分支而删除或覆盖自己的代码。

## 4. 按顺序实现并运行测试

建议依次完成 `firstIndexOf`、`lastIndexOf`、`matchingRange`。每完成一部分检查改动，并运行对应测试。

```bash
# 验证二分查找的首尾边界
mvn -Dtest=BinarySearchDeluxeTest test

# 验证 linear 与 binary 的结果、数量、排名和非法参数处理
mvn -Dtest=AutocompleteEngineContractTest test

# 最后运行整个 main 分支的测试
mvn test
```

在初始工程中，尚未完成的 TODO 会使部分测试失败，这是预期状态。只完成第一个方法时，第二个方法的测试仍可能失败；三个方法完成后应通过全部 main 测试，不通过修改断言或跳过测试来完成验收。

至少核对：重复值的首尾、只有一个元素、全部元素相等、空数组、没有结果、空前缀，以及中文前缀。发现失败时读测试名称、期望值与实际值，再检查边界更新和 Comparator 参数顺序。

## 5. 用真实项目验收结果与性能

在 IntelliJ 中运行 `edu.course.autocomplete.app.AutocompleteGui`，Program arguments 留空即可。先用 linear 确认界面正常；完成 binary 后，用算法下拉框切换。

也可以使用终端：

```bash
mvn compile
java -cp target/classes edu.course.autocomplete.app.AutocompleteGui
```

点击“打开数据文件…”选择 `data/benchmark/actors.txt`，用同一份数据分别查询 linear 和 binary：

| 前缀 | 预期匹配数 | 要观察什么 |
|---|---:|---|
| `Frank W` | 309 | 两边匹配数量、排名后的前十条是否相同 |
| `zzzzzzzz` | 0 | 没有结果时，两种查找策略的耗时区别 |

注意 `Frank W` 的大小写和中间空格。切换算法后先查询几次，再观察相同最终前缀的耗时。GUI 的 query 时间包含 `numberOfMatches` 与 `allMatches` 的查询，不包含数据读取、引擎构建与预排序。机器不同，具体毫秒数不同；不要求达到固定倍数。输入最终前缀后也可点击“性能对比”，自动预热 20 次、测量 100 次，在报告窗口查看中位数、P95 和结果一致性；“导出 CSV…”保存这次报告。binary 尚未完成时会显示失败原因。该按钮和统计逻辑已经提供，不属于本课需要实现的三个方法。

若需要更小的数据，用 `data/benchmark/words-333333.txt` 查询 `app`，应匹配 468 条。不要用六条词的小样例证明性能差距；小样例主要用于检查结果是否正确。

## 6. 查看修改、暂存与写 commit message

修改之后，先检查当前分支及改了哪些文件：

```bash
git branch --show-current
git status
git diff --stat
git diff
```

`git status` 显示文件状态，`git diff` 显示未暂存的代码变化。终端显示分页内容时，按 `q` 退出。新建的未跟踪文件先在编辑器中检查，暂存后再用 `git diff --staged` 查看。

只暂存这次作业涉及的代码：

```bash
git add src/main/java/edu/course/autocomplete/search/BinarySearchDeluxe.java
git add src/main/java/edu/course/autocomplete/search/BinarySearchAutocomplete.java
git diff --staged
git status
```

`git add` 是选择本次提交的内容，不是上传。暂存后普通 `git diff` 可能没有输出，此时用 `git diff --staged` 查看将要提交的改动；`git diff HEAD` 可以查看已暂存和未暂存的已跟踪文件改动。

commit message 写清“做了什么”，例如全部完成后：

```bash
git commit -m "完成二分首尾查找并接入前缀匹配范围"
```

也可以在每个有意义的阶段分别提交，例如“实现二分查找左边界”“实现二分查找右边界”“接入 Autocomplete 前缀范围”。根据实际改动选用，不要为凑记录连续提交相同内容，也不要只写 `update`、`修改` 或 `完成作业`。

查看最近的提交和最后一次提交的具体修改：

```bash
git log --oneline -5
git show --stat HEAD
git show HEAD
```

## 7. 把作业分支 push 到远程

首次推送自己的作业分支（使用与创建时一致的名称）：

```bash
git push -u origin homework/java05-yourname
```

`origin` 是 `git remote -v` 中的远程仓库；`-u` 建立本地分支与远程分支的跟踪关系。push 后 GitHub 才会出现这个分支。

同一分支后续修改，重新检查、测试、暂存和 commit，再运行：

```bash
git push
```

检查上传状态：

```bash
git status
git branch -vv
```

然后在 GitHub 的分支选择器中找到自己的 `homework/java05-...`，确认两个实现文件和最新提交都已经更新。没有权限时使用自己的 Fork，或按课程约定申请协作者权限；不需要强制推送。

## 8. 最终提交内容

提交自己的远程作业分支链接，并说明：

- 三个方法已经完成，`mvn test` 的实际结果。
- 同一数据下，linear 与 binary 的匹配数量、排名结果是否一致。
- 大数据查询的观察，以及为什么 binary 能减少检查位置。

当前步骤只要求提交远程作业分支，不要求把答案合并进课程 main，也不要求完成 Trie 或大小写扩展。

Git 命令与 Fork 工作流可查阅 [git diff 官方文档](https://git-scm.com/docs/git-diff) 和 [GitHub 项目贡献说明](https://docs.github.com/en/get-started/exploring-projects-on-github/contributing-to-a-project)。
