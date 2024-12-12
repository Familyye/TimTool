package top.sacz.timtool.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.util.FixContextUtil
import top.sacz.timtool.BuildConfig
import top.sacz.timtool.R
import top.sacz.timtool.hook.HookEnv
import top.sacz.timtool.hook.core.factory.HookItemFactory
import top.sacz.timtool.net.UpdateService
import top.sacz.timtool.ui.adapter.ItemListAdapter
import top.sacz.timtool.ui.view.CustomRecycleView

class SettingDialog {


    @SuppressLint("InflateParams")
    fun show(activity: Context) {

        val updateService = UpdateService()
        //尝试直接获取更新
        if (updateService.hasUpdate()) {
            updateService.showUpdateDialog()

            if (updateService.isForceUpdate()) {
                return
            }
        }


        val messageText = activity.getString(R.string.setting_message)
            .format(BuildConfig.VERSION_NAME, HookEnv.getAppName(), HookEnv.getVersionName())


        val dialog = MessageDialog.build()
            .setTitleIcon(R.drawable.ic_github)
            .setTitle(R.string.app_name)
            .setMessage(messageText)
            .show()

        dialog.dialogImpl.apply {
            val rootView =
                FixContextUtil.getFixLayoutInflater(activity).inflate(R.layout.layout_setting, null)
            onBindView(rootView)

            boxList.addView(
                rootView,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )

            txtDialogTitle.setOnClickListener {
                onGithubClick(it)
            }

            txtDialogTip.setOnClickListener {
                onTelegramClick(it)
            }
        }


    }

    private fun onTelegramClick(view: View) {
        //跳转到浏览器
        val url = "https://t.me/timtool"
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(url)
        view.context.startActivity(intent)
    }

    private fun onGithubClick(view: View) {
        //跳转到浏览器
        val url = "https://github.com/suzhelan/TimTool"
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(url)
        view.context.startActivity(intent)
    }

    private fun onBindView(rootView: View) {
        val ibViewAllUpdateLog = rootView.findViewById<View>(R.id.ib_update_log)
        ibViewAllUpdateLog.setOnClickListener {
            UpdateLogDialog().show()
        }
        val ibTelegram = rootView.findViewById<View>(R.id.ib_telegram)
        ibTelegram.setOnClickListener {
            onTelegramClick(it)
        }
        val itemViewList = rootView.findViewById<CustomRecycleView>(R.id.rv_item_list)
        val itemList = HookItemFactory.getAllSwitchFunctionItemList()
        val adapter = ItemListAdapter()
        adapter.submitList(itemList)
        itemViewList.layoutManager = LinearLayoutManager(rootView.context)
        itemViewList.adapter = adapter
    }

}