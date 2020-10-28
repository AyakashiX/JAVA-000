# Day03 - JVM调优和面试经验

1. GC日志解读与分析
1. JVM线程堆栈数据分析
1. 内存分析相关工具
1. JVM问题分析调优经验
1. GC疑难情况问题分析
1. JVM常见面试问题汇总


<br />

<a name="bOjN4"></a>
### 压测工具
安装windows版: SuperBenchMarker，可以通过chocolatey来安装，所以先要安装chocolatey。<br />SuperBenchMarker下载地址:[https://github.com/aliostad/SuperBenchmarker](https://github.com/aliostad/SuperBenchmarker)

1. 安装Chocolatey，官网: [https://chocolatey.org/](https://chocolatey.org/)，安装介绍: [https://chocolatey.org/install](https://chocolatey.org/install)
1. 在Windows下启动管理员权限的PowerShell，如果安装了Terminal更好，但是要以管理权限启动，先打开PowerShell，键入以下内容，Chocolatey会自动安装好。
```shell
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))
```
![chocolatey.png](https://cdn.nlark.com/yuque/0/2020/png/343353/1603445185698-54064548-bdb9-4b58-adfc-9035a63ffc41.png#align=left&display=inline&height=652&margin=%5Bobject%20Object%5D&name=chocolatey.png&originHeight=652&originWidth=1096&size=55609&status=done&style=none&width=1096)

3. 安装好Chocolatey后就可以开始安装SuperBenchMarker了，按下面命令行操作即可。

![微信截图_20201023172752.png](https://cdn.nlark.com/yuque/0/2020/png/343353/1603445281022-40517748-a6bd-4fee-8c19-4c7fdda0b42a.png#align=left&display=inline&height=351&margin=%5Bobject%20Object%5D&name=%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20201023172752.png&originHeight=351&originWidth=893&size=17321&status=done&style=none&width=893)

4. 命令行执行choco list --local-only，可以查看chocolatey安装了哪些包。



<a name="GYlt4"></a>
### SuperBenchMarker的使用


<a name="KgCbn"></a>
## 1. GC日志解读与分析
对没有编译过的类编译一下，<br />javac xxx.java

如果字符集错误<br />javac -encoding UTF-8 GCLogAnalysis.java

打印GC日志: <br />java -XX:+PrintGCDetails GCLogAnalysis

打印日志，并输出日志到指定名称的文本文件中，不再控制台中输出，<br />启动方式:
> java -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis



<a name="mlfgu"></a>
### jdk8默认并行GC日志分析
young区的大小会随着gc次数而递增，总的堆大小也会随着gc次数递增，且gc处理的时间也会相应增加。其中Times中的user参数代表实际gc处理时暂停的时间。<br />![2020-10-28_142701.png](https://cdn.nlark.com/yuque/0/2020/png/343353/1603868770966-b39723f4-b998-4032-a880-713708114682.png#align=left&display=inline&height=902&margin=%5Bobject%20Object%5D&name=2020-10-28_142701.png&originHeight=902&originWidth=1855&size=242020&status=done&style=shadow&width=1855)<br />

<a name="MyeYx"></a>
### Serial GC日志分析
启动方式:
> java -XX:+UseSerialGC -Xms512m -Xmx512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis


<a name="v2UAl"></a>
### Parallel GC日志分析
启动方式:
> java -XX:+UseParallelGC -Xms512m -Xmx512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis



<a name="uX33W"></a>
### CMS GC日志分析
启动方式:
> java -XX:+UseConcMarkSweepGC -Xms512m -Xmx512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis



<a name="Q4y0j"></a>
### SuperBenchMarker压测工具使用
>  sb -u http://localhost:8088/api/hello -c 20 -N 60
> sb表示superbenchmarker,
> -u 指定url
> -c 指定并发请求数量
> -N 指定压测时间，单位:秒



> RPS: Requests Per Second，每秒承受的请求数量，即吞吐量。


<br />

<a name="2kteB"></a>
## 遇到的问题

1. 在分析parallel gc时，young区gc后少了55MB，而整个堆显示只少了40MB，为什么说少了的那个15MB晋升到了old区，parallel gc默认young区的对象要经历15次young gc才会晋升到old区吗？



<br />
<br />
<br />
<br />
<br />
<br />
<br />
<br />
<br />
<br />

