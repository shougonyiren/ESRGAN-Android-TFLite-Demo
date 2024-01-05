package com.aiji.vrcapture.mybus

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.blankj.utilcode.util.GsonUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect

/**

 * @Author : liuhao02

 * @Time : On 2022/10/31 6:01 PM

 * @Description : FlowBus

 */
class FlowBus : ViewModel() {
    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { FlowBus() }
    }

    //正常事件
    private val events = mutableMapOf<String, Event<*>>()

    //粘性事件   即发射的事件如果早于注册，那么注册之后依然可以接收到的事件称为粘性事件
    private val stickyEvents = mutableMapOf<String, Event<*>>()

    fun with(key: String, isSticky: Boolean = false): Event<Any> {
        return with(key, Any::class.java, isSticky)
    }

    fun <T> with(eventType: Class<T>, isSticky: Boolean = false): Event<T> {
        return with(eventType.name, eventType, isSticky)
    }

    @Synchronized
    fun <T> with(key: String, type: Class<T>?, isSticky: Boolean): Event<T> {
        val flows = if (isSticky) stickyEvents else events
        if (!flows.containsKey(key)) {
            flows[key] = Event<T>(key, isSticky)
        }
        return flows[key] as Event<T>
    }


    class Event<T>(private val key: String, isSticky: Boolean) {

        // private mutable shared flow
        private val _events = MutableSharedFlow<T>(
            replay = if (isSticky) 1 else 0,
            extraBufferCapacity = Int.MAX_VALUE
        )

        // publicly exposed as read-only shared flow
        val events = _events.asSharedFlow()

        /**
         * need main thread execute
         */
        fun observeEvent(
            lifecycleOwner: LifecycleOwner,
            dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
            minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
            action: (t: T) -> Unit
        ) {
            lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    Log.d("FlowBus", "EventBus.onDestroy:remove key=$key")
                    val subscriptCount = _events.subscriptionCount.value
                    if (subscriptCount <= 0)
                        instance.events.remove(key)
                }
            })
            lifecycleOwner.lifecycleScope.launch(dispatcher) {
                lifecycleOwner.lifecycle.whenStateAtLeast(minActiveState) {
                    events.collect {
                        try {
                            action(it)
                        } catch (e: Exception) {
                            Log.d("FlowBus", "ker=$key , error=${e.message}")
                        }
                    }
                }
            }
        }

        /**
         * send value
         */
        suspend fun setValue(
            event: T,
            dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate
        ) {
            withContext(dispatcher) {
                _events.emit(event)
            }

        }
    }
}

//利用扩展函数
fun LifecycleOwner.observeEvent(
    dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    isSticky: Boolean = false,
    action: (t: EventMessage) -> Unit
) {
    ApplicationScopeViewModelProvider
        .getApplicationScopeViewModel(FlowBus::class.java)
        .with(EventMessage::class.java, isSticky = isSticky)
        .observeEvent(this@observeEvent, dispatcher, minActiveState, action)
}

fun postValue(
    event: EventMessage,
    delayTimeMillis: Long = 0,
    isSticky: Boolean = false,
    dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) {
    Log.d("FlowBus", "FlowBus:send_${Thread.currentThread().name}_${ GsonUtils.toJson(event)}")
    // LjyLogUtil.d("FlowBus:send_${Thread.currentThread().name}_${GsonUtils.toJson(event)}")
    ApplicationScopeViewModelProvider
        .getApplicationScopeViewModel(FlowBus::class.java)
        .viewModelScope
        .launch(dispatcher) {
            delay(delayTimeMillis)
            ApplicationScopeViewModelProvider
                .getApplicationScopeViewModel(FlowBus::class.java)
                .with(EventMessage::class.java, isSticky = isSticky)
                .setValue(event)
        }
}

private object ApplicationScopeViewModelProvider : ViewModelStoreOwner {

    private val eventViewModelStore: ViewModelStore = ViewModelStore()

//    override fun getViewModelStore(): ViewModelStore {
//        return eventViewModelStore
//    }

    private val mApplicationProvider: ViewModelProvider by lazy {
        ViewModelProvider(
            ApplicationScopeViewModelProvider,
            ViewModelProvider.AndroidViewModelFactory.getInstance(FlowBusInitializer.application)
        )
    }

    fun <T : ViewModel> getApplicationScopeViewModel(modelClass: Class<T>): T {
        return mApplicationProvider[modelClass]
    }

    /**
     * The owned [ViewModelStore]
     */
    override val viewModelStore: ViewModelStore
        get() = eventViewModelStore

}

object FlowBusInitializer {
    lateinit var application: Application

    //在Application中初始化
    fun init(application: Application) {
        FlowBusInitializer.application = application
    }
}

