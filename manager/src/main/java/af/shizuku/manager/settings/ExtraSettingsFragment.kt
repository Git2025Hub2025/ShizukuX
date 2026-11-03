package af.shizuku.manager.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.TwoStatePreference
import af.shizuku.manager.R
import af.shizuku.manager.ShizukuSettings
import af.shizuku.manager.ShizukuSettings.Keys.*

/**
 * 附加功能区。
 *
 * 只做两件事：加载 [R.xml.settings_extra_features]；给由其它页面迁来的条目重新接上
 * 它们原本的处理逻辑（处理逻辑本身仍留在 AdvancedSettingsFragment /
 * BehaviorSettingsFragment / DeviceOwnerSettingsFragment 中，未复制未重写）。
 * 所有 key、图标、开关状态均复用项目原有定义。
 */
class ExtraSettingsFragment : BaseSettingsFragment() {

    override fun getTitle(): CharSequence? = getString(R.string.extra_screen_title)

    override fun onCreateSettingsPreferences(savedInstanceState: Bundle?, rootKey: String?) {
        if (!isAdded) return
        setPreferencesFromResource(R.xml.settings_extra_features, rootKey)

        // Watchdog：与 BehaviorSettingsFragment 同一套读写与电量白名单引导
        findPreference<TwoStatePreference>(KEY_WATCHDOG)?.apply {
            isChecked = ShizukuSettings.isWatchdogRunning()
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue is Boolean) {
                    maybeToggleBatterySensitiveSetting(newValue) { result ->
                        if (result) {
                            ShizukuSettings.setWatchdog(context, newValue)
                            findPreference<TwoStatePreference>(KEY_WATCHDOG)?.isChecked = newValue
                        }
                    }
                }
                false
            }
        }

        // 备份 / 恢复：处理逻辑在 AdvancedSettingsFragment，这里只负责导航过去并高亮该条目，
        // 不复制任何备份实现。
        findPreference<Preference>("backup_settings")?.setOnPreferenceClickListener {
            navigateTo(AdvancedSettingsFragment(), "backup_settings", R.string.settings_advanced_diagnostics_title)
            true
        }
        findPreference<Preference>("restore_settings")?.setOnPreferenceClickListener {
            navigateTo(AdvancedSettingsFragment(), "restore_settings", R.string.settings_advanced_diagnostics_title)
            true
        }

        // Dhizuku 兼容桥：跳转入口方案（不迁移 Fragment，避免打断其内部状态机）
        findPreference<Preference>("extra_nav_device_owner")?.setOnPreferenceClickListener {
            navigateTo(DeviceOwnerSettingsFragment(), null, R.string.settings_permission_level_category_title)
            true
        }
    }

    private fun navigateTo(
        fragment: androidx.fragment.app.Fragment,
        highlightKey: String?,
        titleRes: Int
    ) {
        if (highlightKey != null) {
            fragment.arguments = Bundle().apply { putString("highlight_key", highlightKey) }
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
        (activity as? SettingsActivity)?.updateTitle(getString(titleRes))
    }
}
