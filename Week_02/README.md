学习笔记
1. GC日志解读与分析
2. JVM线程堆栈数据分析
3. 内存分析相关工具
4. JVM问题分析调优经验
5. GC疑难情况问题分析
6. JVM常见面试问题汇总
压测工具
安装windows版: SuperBenchMarker，可以通过chocolatey来安装，所以先要安装chocolatey。
SuperBenchMarker下载地址:https://github.com/aliostad/SuperBenchmarker
1. 安装Chocolatey，官网: https://chocolatey.org/，安装介绍: https://chocolatey.org/install
2. 在Windows下启动管理员权限的PowerShell，如果安装了Terminal更好，但是要以管理权限启动，先打开PowerShell，键入以下内容，Chocolatey会自动安装好。
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))
3. 安装好Chocolatey后就可以开始安装SuperBenchMarker了，按下面命令行操作即可。
4. 命令行执行choco list --local-only，可以查看chocolatey安装了哪些包。
SuperBenchMarker的使用
1. GC日志解读与分析
对没有编译过的类编译一下，
javac xxx.java
如果字符集错误
javac -encoding UTF-8 GCLogAnalysis.java
打印GC日志: 
java -XX:+PrintGCDetails GCLogAnalysis
打印日志，并输出日志到指定名称的文本文件中，不再控制台中输出，
启动方式:
java -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
jdk8默认并行GC日志分析
young区的大小会随着gc次数而递增，总的堆大小也会随着gc次数递增，且gc处理的时间也会相应增加。其中Times中的user参数代表实际gc处理时暂停的时间。
Serial GC日志分析
启动方式:
java -XX:+UseSerialGC -Xms512m -Xmx512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
Parallel GC日志分析
启动方式:
java -XX:+UseParallelGC -Xms512m -Xmx512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
CMS GC日志分析
启动方式:
java -XX:+UseConcMarkSweepGC -Xms512m -Xmx512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
SuperBenchMarker压测工具使用
 sb -u http://localhost:8088/api/hello -c 20 -N 60
sb表示superbenchmarker,
-u 指定url
-c 指定并发请求数量
-N 指定压测时间，单位:秒
RPS: Requests Per Second，每秒承受的请求数量，即吞吐量。
遇到的问题
1. 在分析parallel gc时，young区gc后少了55MB，而整个堆显示只少了40MB，为什么说少了的那个15MB晋升到了old区，parallel gc默认young区的对象要经历15次young gc才会晋升到old区吗？