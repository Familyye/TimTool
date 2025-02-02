package top.sacz.timtool.hook.core

import com.alibaba.fastjson2.TypeReference
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.factory.HookItemFactory
import top.sacz.xphelper.util.ConfigUtils

class HookItemLoader {


    /**
     * 加载配置到对象
     */
    fun loadConfig() {
        //读取配置 如果不存在则new一个
        val type = object : TypeReference<HashMap<String, Boolean>>() {}
        var config: Map<String, Boolean>? = ConfigUtils("item_config").getObject("item_config", type)
        if (config == null) {
            config = HashMap()
        }
        //真正去读配置到对象
        config.forEach { (key, value) ->
            val hookItem = HookItemFactory.findHookItemByPath(key)
            if (hookItem != null) {
                hookItem.isEnabled = value
            }
        }
    }

    /**
     * 加载并判断哪些需要加载
     */
    fun loadHookItem() {
        //加载前进行一次api分配
        ApiProcessor.processor()
        //开始判断哪些需要加载
        val allHookItems = HookItemFactory.getAllItemList()
        allHookItems.forEach { hookItem ->
            if (hookItem is BaseSwitchFunctionHookItem && hookItem.isEnabled) {
                hookItem.startLoad()
            } else {
                hookItem.startLoad()
            }
        }
    }

    fun saveConfig() {
        val config = HashMap<String, Boolean>()
        val allHookItems = HookItemFactory.getAllSwitchFunctionItemList()
        allHookItems.forEach { hookItem ->
            config[hookItem.path] = hookItem.isEnabled
        }
        ConfigUtils("item_config").put("item_config", config)
    }
}
