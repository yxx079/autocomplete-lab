# JavaLab06 课后变式：返回名称筛选的数量

本次只完成一个课堂练习的小变化：课堂上打印符合长度要求的名称，课后改为返回符合要求的名称数量。保留课堂已经完成的修改。

## 实现

在自己的 autocomplete-lab 新建：

`src/main/java/edu/course/autocomplete/practice/NameLengthPractice.java`

包名 `edu.course.autocomplete.practice`，类名 `NameLengthPractice`。实现：

```java
public static int countLongNames(String[] names, int minimumLength)
```

输入保证：names是一个数组，里面每一项都是字符串；minimumLength是0或更大的整数。返回长度至少达到minimumLength的名称数量，不打印名称，也不修改原数组。长度刚好达到要求的名称也要计入。无需实现排序、检索或修改GUI。

| 数组里放哪些名称 | 至少几个字符 | 正确数量 |
|---|---:|---:|
| Rome、Kyoto、Reykjavik | 5 | 2 |
| Rome、Oslo、Lima | 4 | 3 |
| Rome、Oslo | 9 | 0 |
| 数组中没有名称 | 5 | 0 |
| 空字符串、Rome | 0 | 2 |

文件的可运行骨架如下；只填写countLongNames，然后在IntelliJ运行main。输出应为2、3、0。

```java
package edu.course.autocomplete.practice;

public final class NameLengthPractice {
    public static int countLongNames(String[] names, int minimumLength) {
        throw new UnsupportedOperationException("countLongNames 尚未完成");
    }
    public static void main(String[] args) {
        System.out.println(countLongNames(new String[] {"Rome", "Kyoto", "Reykjavik"}, 5));
        System.out.println(countLongNames(new String[] {"Rome", "Oslo", "Lima"}, 4));
        System.out.println(countLongNames(new String[] {"Rome", "Oslo"}, 9));
    }
}
```

至少记录前三组的实际输出，再检查数组没有名称，或长度要求为0的情况。可以用main运行；愿意练测试时，在相同职责的test包新增测试。已有项目测试保留原样。

## 提交

在现有自己的GitHub仓库提交代码与 `submissions/java06.md`。后者写：

1. 测试输入和正确结果。
2. 实际运行结果，文本或截图均可。
3. 两三句话说明计数何时增加、何时返回。
4. 一个仍不理解的问题；没有问题也可以说明目前最有把握的部分。

沿用已经使用的本地分支，无需另外创建分支。运行前在IntelliJ重新加载项目，运行NameLengthPractice.main，不需要打开GUI或填写启动参数。运行后，仅提交相关文件；不提交.idea、.vscode和target。提交仓库或commit链接供验收。
