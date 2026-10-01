# 数据

本项目自包含 30 份可加载数据，约 275 MB。数据文件采用“首行记录数 + 权重、制表符、查询文本”格式；查询文本可以包含空格和中文。

classroom 用于小规模功能及边界验证；benchmark 用于实际查询和性能比较。原有完整中文词库 mandarin.txt 已从 classroom 归入 benchmark。GUI 在运行时可切换文件和算法。

| 常用案例 | 前缀 | 匹配数 |
|---|---|---:|
| benchmark/actors.txt | Frank W | 309 |
| benchmark/words-333333.txt | app | 468 |
| benchmark/mandarin.txt | 我 | 17 |

当前是区分大小写的原文本前缀搜索，不是拼音、翻译或全文包含搜索。

来源：[Princeton Autocomplete 课程项目](https://www.cs.princeton.edu/courses/archive/fall25/cos226/assignments/autocomplete/specification.php)，数据从现有 Autocomplete_Data_Full 数据包整理，内容未修改。保留 acknowledgments.txt 原始随附文件；它不是数据来源许可证，也不是可加载数据。

| 文件 | 记录数 |
|---|---:|
| `classroom/empty-string.txt` | 17 |
| `classroom/fortune1000-randomly-ordered.txt` | 1,000 |
| `classroom/fortune1000-sorted-by-term.txt` | 1,000 |
| `classroom/fortune1000-sorted-by-weight.txt` | 1,000 |
| `classroom/fortune1000.txt` | 1,000 |
| `classroom/pokemon.txt` | 729 |
| `classroom/pu-buildings.txt` | 166 |
| `classroom/small.txt` | 10 |
| `classroom/tiny.txt` | 6 |
| `benchmark/2grams.txt` | 277,718 |
| `benchmark/3grams.txt` | 1,020,009 |
| `benchmark/4grams.txt` | 1,034,307 |
| `benchmark/5grams.txt` | 1,044,268 |
| `benchmark/actors.txt` | 2,875,183 |
| `benchmark/alexa.txt` | 1,000,000 |
| `benchmark/artists.txt` | 43,848 |
| `benchmark/baby-names.txt` | 31,109 |
| `benchmark/bing.txt` | 250,000 |
| `benchmark/cities.txt` | 93,827 |
| `benchmark/imdb-votes.txt` | 82,455 |
| `benchmark/mandarin.txt` | 94,339 |
| `benchmark/metal-albums.txt` | 3,000 |
| `benchmark/movies.txt` | 229,447 |
| `benchmark/nasdaq.txt` | 2,658 |
| `benchmark/pu-courses.txt` | 6,771 |
| `benchmark/redditors.txt` | 10,000 |
| `benchmark/songs.txt` | 922,229 |
| `benchmark/trademarks.txt` | 92,254 |
| `benchmark/wiktionary.txt` | 10,000 |
| `benchmark/words-333333.txt` | 333,333 |
