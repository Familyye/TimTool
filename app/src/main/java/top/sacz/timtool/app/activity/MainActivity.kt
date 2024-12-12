package top.sacz.timtool.app.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import top.sacz.timtool.BuildConfig
import top.sacz.timtool.R
import top.sacz.timtool.databinding.ActivityMainBinding
import top.sacz.timtool.hook.util.ToastTool
import top.sacz.timtool.net.UpdateService
import top.sacz.timtool.ui.dialog.UpdateLogDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val hideActivityName: String
        get() = "$packageName.app.activity.HideMainActivity"

    private val showActivityName: String
        get() = javaClass.name

    @set:UiThread
    @get:UiThread
    var isHideActivity: Boolean
        get() {
            val componentName = ComponentName(this, hideActivityName)
            return componentName.getEnable(this)
        }
        set(enabled) {
            val hide = ComponentName(this, hideActivityName)
            hide.setEnable(this, enabled)
            val show = ComponentName(this, showActivityName)
            show.setEnable(this, !enabled)
        }


    private val bind by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)
        requestTransparentStatusBar()
        initUI()
    }

    private fun initUI() {
        initBuildInfo()
        val updateService = UpdateService()

        updateService.requestUpdateAsync { hasUpdate ->
            if (hasUpdate) {
                updateService.showUpdateDialog()
            }
        }
        //隐藏图标
        val cbHideIcon = bind.cbHideIcon
        cbHideIcon.isChecked = isHideActivity
        cbHideIcon.setOnCheckedChangeListener { _, isChecked ->
            ToastTool.show(isChecked)
            isHideActivity = isChecked
        }
    }

    fun onTelegramClick(view: View) {
        //跳转到浏览器
        val url = "https://t.me/timtool"
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    fun onGithubClick(view: View) {
        //跳转到浏览器
        val url = "https://github.com/suzhelan/TimTool"
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    fun onClickShowUpdateLogDialog(view: View) {
        //展示更新dialog
        val updateLogDialog = UpdateLogDialog()
        updateLogDialog.show()
    }

    private fun initBuildInfo() {
        val dateFormat = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault())
        val buildTime = dateFormat.format(Date(BuildConfig.BUILD_TIMESTAMP))
        val buildInfo = getString(R.string.build_info).format(BuildConfig.VERSION_NAME, buildTime)
        bind.tvBuildInfo.text = buildInfo
    }

    private fun requestTransparentStatusBar() {
        //状态栏透明
        enableEdgeToEdge()
        //设置布局和状态栏的适配和间隔
        ViewCompat.setOnApplyWindowInsetsListener(bind.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

fun ComponentName.getEnable(ctx: Context): Boolean {
    val packageManager: PackageManager = ctx.packageManager
    val list = packageManager.queryIntentActivities(
        Intent().setComponent(this), PackageManager.MATCH_DEFAULT_ONLY
    )
    return list.isNotEmpty()
}

fun ComponentName.setEnable(ctx: Context, enabled: Boolean) {
    val packageManager: PackageManager = ctx.packageManager
    if (this.getEnable(ctx) == enabled) return
    packageManager.setComponentEnabledSetting(
        this,
        if (enabled) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP
    )
}