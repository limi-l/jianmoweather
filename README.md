### 笔记
在解决 Compose 函数重组问题：
1. 分表查询, 将各自 Flow<List<OneDay>> 结果取出 List<OneDay> 单独放入各自 uiState, 在将 uiState转成
   StateFlow<uiState>. 也就是不使用 combine
   > 好处: 多表插入的时候不会多次影响该表查询结果

2. 分表查询, 将各自 Flow<List<OneDay>> 都放入同一个 combine 中, 然后 new UiState , 返回 StateFlow<UiState>

3. 使用关系查询, 查询结果都放在一个 Pojo 类中 也就是 Flow<Pojo>, 放入 combine , 然后 new UiState , 返回 StateFlow<UiState>

尝试了上面三种方式从数据库查询数据, 然后在 ComposeUi 中收集 StateFlow 完成显示 , 除了第 1 个方案, 剩下两个方案的问题就是触发多次重组， 然后就有了以下的疑问:

"为什么多次重组?" - 因为只有 Compose 数据对象结构发生变化的时候才会重组，而一个 Compose 多次重组则是受多表分开插入的导致的.
"为什么每次插入相同的数据都会导致数据结构发生变化?" - 因为我在 Entity 类中使用了自增长的_id, 每次插入数据库的这个_id 都不一样
"为什么 Pojo 每次查询完的内存地址都会不一样?" - 在获得查询结果时使用了 .distinctUntilChanged() 来避免重复结构的对象, 又因为上面 _id
的不一样导致内嵌类或者关联类的结构不一样,最终 .distinctUntilChanged() 就会丢弃上一个对象, 已让新的 pojo 发送

- 总结:
    - 上面的各种问题其实都是，数据插入的时，因为 _id 自增长问题, 导致每次数据结构都不一样, 改为手动设置 id, 这样让查询结果的结构更加稳定, pojo 内存地址也变得稳定
    


### 笔记1
````kotlin
fun observerLocation(): Flow<AMapLocation> = callbackFlow {
	val callback = AMapLocationListener { location ->
		when(val result = location.status()) {
			is LocationSuccess -> {
				// send() 是可挂起函数
				// trySend() 不是可挂起函数，能返回发送的状态，管道满时，返回 false, 也就是意味发送失败
                
				// trySendBlocking 是可以在不同条件下决定是使用 send() 还是使用 trySend() 的一个扩展函数
				// 如果管道没有满，则使用 trySend() 去发送，如果满了, 则开启一个协程，并使用可挂起的 send() 去发送，当 send() 发送失败, 则抛出异常
				// trySendBlocking 内部开启了一个 runBlocking 协程（ runBlocking 会阻塞当前线程，因此在 runBlocking 内的代码状态是安全的），
                // 因为 runBlocking 已经开启了一个协程, 所以千万不要自己再开起一个协程来使用 trySendBlocking , 避免两层协程
				trySendBlocking(location) // 内部：发送成功：直接 return ，返回 false 发送失败：意味管道满了 或者 当前管道被关闭
			}
			is LocationError -> cancel(CancellationException(result.throwable.message))
		}
	}
	client.setLocationListener(callback)
	awaitClose { client.unRegisterLocationListener(callback) } // 当订阅者停止监听，利用挂起函数 "awaitClose" 来解除订阅
}
````



### 笔记2
kotlin 引用比较 "==="  等同 java "=="
kotlin 的 equals 和 kotlin 的 "==" 是一样的，属于结构比较, kotlin 的 equals 通常用于数据类重写
equals 默认先判断当前对象的类型是否一致,如果一致，则判断该对象的内部对象的内存地址是否一样 【重要】
一般 String ,Int 这些常量或者基本数据类型, 地址的异同 只根据 数据内容的异同就能看的出来
只要里面的数据一样,equals 就相等
只要里面的地址变了，equals 比较出来的肯定是不一样的


````kotlin
@Composable
fun myTest(t: Temperature) {
	// 每次刷新, weatherPojo 的地址不一样, 所以当外部的 uiState 进行 equals  时, 结果都不是 true
	Text(text = "$t")
}
````



### 笔记
load 命名: 数据 或 异常 
get 命名: 数据 或 Null 
find 命名: 大数据 或 Null



### 笔记
launch 不阻塞当前线程      
runBlocking 中断阻塞当前线程



### 笔记
org.gradle.jvmargs=-Xmx4096M : gradle jvm 的最大堆内存 (Xmx代表最大,Xms代表最小)
-Dkotlin.daemon.jvm.options\="-Xmx1024M" gradle 守护进程的最大堆内存



### 笔记
- 接口 > 接口函数 -> 使用

```kotlin
interface Printer {
	fun print()
}

fun Printer(block: () -> Unit): Printer {  // 带有返回值的普通函数
	return object : Printer {
		override fun print() = block()
	}
}
val p = Printer {
	println("Hello!")
}
```

- 接口函数 - 使用

```kotlin
fun interface Printer {  // 接口函数
	fun println()
}
val p = Printer {
	println("Hello!")
}
```



### 笔记
```kotlin
object Lifecycle {
	private const val version = "2.5.0-alpha04"
	// val model: MyViewModel by viewModels() // ktx： 通过 by 关键字来生成
	const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version" // 创建 ViewModel
	const val runtimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$version"    // Only Lifecycle
	const val livedataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:$version"  // LiveData

	// 在 Activity / Fragment 中使用原始方式创建 ViewModel:
	// viewModel = ViewModelProvider(this)[ExampleViewModel::class.java] //defaultFactory() 无参数 ViewModel

	// 在 Compose 函数中创建 Activity/Fragment 的 ViewModel:
	// viewModel: ExampleViewModel = viewModel()
	// viewModel: ExampleViewModel = viewModel(object():ViewModelProvider.Factory)
    // 需要添加以下依赖: "androidx.lifecycle:lifecycle-viewmodel-compose:$version"
}
```


### 笔记
```kotlin
val callback = object : AMapLocationListener {
	// 笔记：对于大量的业务逻辑的数据读写安全，可以将协程创建在单个线程上 newSingleThreadContext 
	// 然后将业务逻辑放在协程内执行，这样读写安全且速度快，对基本数据如 Int Float 等的读写则建议使用原子类 AtomInt

	// 所谓的安全就是确保数据的完整性和统一性，当一个线程A在写，则应当确保另一个准备读取的线程B 等待 线程A写完 之后再读
	// 而这个等待的机制就叫做 “阻塞” 比如 java 的: synchronized 和 ReentrantLock
	// 对于 Coroutine 协程，则使用的是 Mutex 互斥的方案来确保数据安全，他不会阻塞下层的线程， 但更建议在多线程上使用
	val mutex = Mutex() //互斥锁，先到先获得锁
	var observeJob: Job? = null
	override fun onLocationChanged(location: AMapLocation) {
		observeJob?.cancel()
		observeJob = launch {
			mutex.withLock {
				//observer(location.status())
			}
		}
	}
}
```



### 笔记
Modifier.navigationBarsPadding() :
如果父 Layout 不设置，子 View 设置了，则子 View 会让父 Layout 膨胀变大（父 Layout 高度增加），但父 Layout 依然占据 systemBar 空间
如果父 Layout 设置了，子 View 不设置，则子 view 并不会去占据 systemBar 空间, 父 Layout 会影响 子 View 的位置
总结：子 View 永远不会改变 父 Layout 的空间位置，但可以更改父 Layout 的大小



### 笔记
compose 中获取 context
```kotlin
val context = LocalContext.current
val contentPadding = rememberInsetsPaddingValues( //获取 systemBar 高度
    insets = LocalWindowInsets.current.systemBars,
    applyBottom = false,
    applyTop = true
)
```



### 笔记
RecyclerView 和 LazyColumn 取消吸顶的阴影效果
```
android:overScrollMode="never"
```
```kotlin
LocalOverScrollConfiguration provides null
```



### 笔记
查找 Compose 不稳定的对象，官方以后可能会将该功能添加到 AS Layout Inspector 中
```
./gradlew assembleRelease -Pjianmoweather.enableComposeCompilerReports=true --rerun-tasks
```



### 笔记
kotlin channel
```kotlin
val channel = remember { Channel<Int> { Channel.Factory.CONFLATED } }
var clickCount by remember { mutableStateOf(0) }
LaunchedEffect(channel) {
    channel.receiveAsFlow().collect { index ->
        val result = snackBarHostState.showSnackbar(
            message = "已删除一个城市",
            actionLabel = "撤销"
        )
        when (result) {
            SnackbarResult.ActionPerformed -> {
                Timber.d("----------------$index") // 撤销
            }
            SnackbarResult.Dismissed -> {
                //viewModel.deleteFavorite()
            } // 调用数据库删除
        }
    }
}
channel.trySend(++clickCount)
```



### 笔记
```kotlin
        val density = LocalDensity.current
        with(density) {
            // toPx()
        }
```




