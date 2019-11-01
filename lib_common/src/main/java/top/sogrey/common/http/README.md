
简单介绍Retrofit、OKHttp和RxJava之间的关系：

- Retrofit：Retrofit是Square公司开发的一款针对Android 网络请求的框架（底层默认是基于OkHttp 实现）。
- OkHttp：也是Square公司的一款开源的网络请求库。
- RxJava ："a library for composing asynchronous and event-based programs using observable sequences for the Java VM"（一个在 Java VM 上使用可观测的序列来组成异步的、基于事件的程序的库）。RxJava使异步操作变得非常简单。

各自职责：

- Retrofit 负责 请求的数据 和 请求的结果，使用 接口的方式 呈现，
- OkHttp 负责请求的过程，
- RxJava 负责异步，各种线程之间的切换。

