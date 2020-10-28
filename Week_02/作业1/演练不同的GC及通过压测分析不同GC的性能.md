# 演练不同的GC及通过压测分析不同GC的性能

<a name="mnn2Z"></a>
### 在不同大小堆内存下各个GC的情况总结
> 机器环境为CPU6核12线程，内存16G

<a name="45n3X"></a>
### 1. Serial GC
> java -XX:+UseSerialGC -Xms128m -Xmx128m -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis<br />

启动串行GC在初始化堆内存和最大堆内存都在128M的情况下，此时总堆内存比较小，发生GC的频率很高，经过较少次数的Young GC，发生Full GC的次数是Young GC的3倍之多。老年代在最后两次Young GC就被占满了，之后开始Full GC。老年代一直在总堆容量的68%左右徘徊，并没有多少实际的减少，最后很快就发生了内存溢出。堆显示情况，新生代使用达到100%，from区达到96%，老年代使用率也达到99%。在不设置-Xms128的情况下和设置的情况下结果差不多。<br />

> java -XX:+UseSerialGC -Xms512m -Xmx512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis

串行GC在堆内存初始化和最大值都为512m的情况下，程序执行成功，没有发生内存溢出，供生成对象13000+。Young GC发生的次数明显多于Full GC，且Young GC和Full GC的执行时间相对于128M堆内存下有所增长。Full GC时，Old区大小一直也保持在总堆的68%左右，且几乎没有减少。堆显示情况，新生区少量占用，老年代几乎被占满。<br />

> java -XX:+UseSerialGC -Xms2g -Xmx2g -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis<br />

串行GC在堆内存初始化和最大值都为2g的情况下，程序执行成功，没有发生内存溢出，供生成对象19000+，明显高于512M内存的情况下。且只发生了多次Young GC，并没有产生Full GC，但是GC处理时间明显高于低内存的时候。堆内存显示，新生区几乎没占用，from区几乎被占满，老年代也没有被占满。

> java -XX:+UseSerialGC -Xms4g -Xmx4g -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis

串行GC在堆内存初始化和最大值都为4g的情况下，程序执行成功，没有发生内存溢出，供生成对象2000+，相对2G堆内存并没有太多的增长，而随着内存继续的扩大，反而有下降的趋势。只发生了Young GC，且次数比2G堆内存时还要少，但GC执行时间明显增长。

---

<a name="iGuWu"></a>
### 2. Parallel GC
> java -XX:+UseParallelGC -Xms128m -Xmx128m -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis<br />

在128M堆内存情况下，并行GC发生的GC的频率比较高，且Full GC次数明显大于Young GC次数，但GC执行时间很短。老年代达到总堆的60%左右开始Full GC，且最后居于68%左右后不再有明显下降，多次Full GC后很快就发生内存溢出。最后堆信息显示新生代和老年代几乎被占满。<br />

> java -XX:+UseParallelGC -Xms512m -Xmx512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis

在512M堆内存情况下，GC频率也较高，但Young GC次数明显高于Full GC，Young GC的执行时间很短，而Full GC的执行时间明显大于Serial GC的Full GC时间，达到了300ms/次，而Serial GC的Full GC时，只有30ms/次，人的感官上明显觉得Parallel GC有延迟。在同样的堆大小下，生成的对象数量也比Serial GC时要少得多。<br />

> java -XX:+UseParallelGC -Xms2g -Xmx2g -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis

在2G堆内存的情况下，发生GC的次数明显减少，几乎都是Young GC，但Young GC的执行时间也比内存较低的时候要高。但是生成的对象明显高于内存低的时候。并行GC随着堆内存的增加，发生GC的频率逐渐减少，但是GC的执行时间逐渐增加。<br />

> java -XX:+UseParallelGC -Xms4g -Xmx4g -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis

在4G堆内存情况下，发生的GC的频率比2G更低，但GC执行时间相应增加，生成的对象数量也更多。

---

<a name="5FuC9"></a>
### 3. CMS GC
> java -XX:+UseConcMarkSweepGC -Xms128m -Xmx128m -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis

128M堆内存下，发生GC频率高，发生Full GC的频率也明显高于Young GC，出现多次内存分配失败，并发标记失败，新生代和老年代很快被占满，发生内存溢出，但GC执行时间很短。<br />

> java -XX:+UseConcMarkSweepGC -Xms512m -Xmx512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis

512堆内存下，触发多次并发标记，没有发生Full GC，但GC执行时间相应增加。<br />

> java -XX:+UseConcMarkSweepGC -Xms2g -Xmx2g -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis

2G堆内存下，Young GC次数相对更少，并发标记次数也少了，生成的对象多了。随着内存增大，young GC的执行时间比堆内存低的时候要高，但总体GC频率降低了。<br />

> java -XX:+UseConcMarkSweepGC -Xms4g -Xmx4g -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis<br />

4G堆内存下，没有发生并发标记，因为老年代还没有达到并发标记的内存使用量，只有Young GC的处理，但是执行时间也随着堆内存的增大而增加，但生成的对象数并没有随着堆内存的增加而增多。

---

<a name="B64kZ"></a>
### 4. G1 GC
> java -XX:+UseG1GC -Xms512m -Xmx512m -XX:+PrintGC -XX:+PrintGCDateStamps GCLogAnalysis

512堆内存下，发生多次young区的对象转移，并发标记频率较高，也出现了多次混合模式，产生了较多的Full GC次数。<br />

> java -XX:+UseG1GC -Xms4g -Xmx4g -XX:+PrintGC -XX:+PrintGCDateStamps GCLogAnalysis

4G堆内存情况下，只发生了young区的对象转移暂停，但未出现并发标记，且每次转移暂停的时间随着young区的增大而增加。

---

<a name="eGyxW"></a>
## 压测总结
<a name="160Cs"></a>
### 串行GC(Serial GC)
> java -jar -XX:+UseSerialGC -Xms512m -Xmx512m gateway-server-0.0.1-SNAPSHOT.jar

![s512m.png](https://cdn.nlark.com/yuque/0/2020/png/343353/1603902868701-b76dc729-61e3-4707-b9ab-438e0c826aa0.png#align=left&display=inline&height=705&margin=%5Bobject%20Object%5D&name=s512m.png&originHeight=705&originWidth=1025&size=29221&status=done&style=none&width=1025)<br />

> java -jar -XX:+UseSerialGC -Xms4g -Xmx4g gateway-server-0.0.1-SNAPSHOT.jar

![s4g.png](https://cdn.nlark.com/yuque/0/2020/png/343353/1603902874334-bdf39cec-4bec-4116-b0e8-eb2e53fbc7b2.png#align=left&display=inline&height=692&margin=%5Bobject%20Object%5D&name=s4g.png&originHeight=692&originWidth=1032&size=31863&status=done&style=none&width=1032)<br />
<br />使用串行GC来启动程序，4G堆内存时的请求延迟反而比512M堆内存的时候要高得多，且RPS也有所下降，成功请求数也并未比低堆内存的时候要高。

---

<a name="ihSUM"></a>
### 并行GC(Parallel GC)
> java -jar -XX:+UseParallelGC -Xms512m -Xmx512m gateway-server-0.0.1-SNAPSHOT.jar

![p512m.png](https://cdn.nlark.com/yuque/0/2020/png/343353/1603903381611-9cc42ce0-e8b0-4c1b-a997-26d70f26b212.png#align=left&display=inline&height=688&margin=%5Bobject%20Object%5D&name=p512m.png&originHeight=688&originWidth=1035&size=28745&status=done&style=none&width=1035)
> java -jar -XX:+UseParallelGC -Xms4g -Xmx4g gateway-server-0.0.1-SNAPSHOT.jar

![p4g.png](https://cdn.nlark.com/yuque/0/2020/png/343353/1603903579408-0d07d759-a5f4-48c8-8d35-81ccdea53282.png#align=left&display=inline&height=701&margin=%5Bobject%20Object%5D&name=p4g.png&originHeight=701&originWidth=1025&size=32962&status=done&style=none&width=1025)<br />**使用并行GC启动程序，在堆内存增加的情况下，总请求数和吞吐量有少量增长，且最大请求延迟也有一定的增加。**

---

<a name="vsnkD"></a>
### CMS GC
> java -jar -XX:+UseConcMarkSweepGC -Xms512m -Xmx512m gateway-server-0.0.1-SNAPSHOT.jar

![cms512m.png](https://cdn.nlark.com/yuque/0/2020/png/343353/1603904038225-75287320-2004-4432-af09-43f9bf0a2855.png#align=left&display=inline&height=689&margin=%5Bobject%20Object%5D&name=cms512m.png&originHeight=689&originWidth=1016&size=28815&status=done&style=none&width=1016)
> java -jar -XX:+UseConcMarkSweepGC -Xms4g -Xmx4g gateway-server-0.0.1-SNAPSHOT.jar

![cms4g.png](https://cdn.nlark.com/yuque/0/2020/png/343353/1603904269860-8c37cc37-41e6-47fd-9ad0-50d5208a90ef.png#align=left&display=inline&height=687&margin=%5Bobject%20Object%5D&name=cms4g.png&originHeight=687&originWidth=1012&size=29090&status=done&style=none&width=1012)<br />**在使用CMS GC时，吞吐量略低于并行GC的吞吐量，但延迟比并行GC的要低。**

---

<a name="qWgDB"></a>
### G1 GC
> java -jar -XX:+UseG1GC -Xms512m -Xmx512m gateway-server-0.0.1-SNAPSHOT.jar

![g1512m.png](https://cdn.nlark.com/yuque/0/2020/png/343353/1603904585652-612df840-16db-42ea-96c7-8db9b3d7a25a.png#align=left&display=inline&height=691&margin=%5Bobject%20Object%5D&name=g1512m.png&originHeight=691&originWidth=1011&size=29557&status=done&style=none&width=1011)
> java -jar -XX:+UseG1GC -Xms4g -Xmx4g gateway-server-0.0.1-SNAPSHOT.jar

![g14g.png](https://cdn.nlark.com/yuque/0/2020/png/343353/1603904933304-b2d03ebb-bd7d-4325-8d18-b9999b887553.png#align=left&display=inline&height=689&margin=%5Bobject%20Object%5D&name=g14g.png&originHeight=689&originWidth=1022&size=36546&status=done&style=none&width=1022)<br />**G1 GC在增加堆内存后吞吐量有一定的提升，但延迟大幅降低，比并行GC和CMS GC都要低得多。**<br />

