package af.shizuku.manager.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.PreferenceFragmentCompat
import af.shizuku.core.ui.AppActivity
import af.shizuku.manager.R
import af.shizuku.manager.settings.compose.ExtraScreen

class ExtraSettingsActivity : AppActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private var currentTitle by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)

        currentTitle = getString(R.string.extra_screen_title)

        setContent {
            af.shizuku.core.ui.compose.AppTheme(
                isBlackNightTheme = af.shizuku.manager.app.ThemeHelper.isBlackNightTheme(this),
                isOneUi = af.shizuku.manager.ShizukuSettings.isOneUiThemeEnabled()
            ) {
                ExtraScreen(
                    title = currentTitle,
                    onNavigateUp = {
                        if (!onSupportNavigateUp()) finish()
                    },
                    onContainerCreated = {
                        val container = findViewById<android.view.ViewGroup>(R.id.fragment_container)
                        val frag = supportFragmentManager.findFragmentById(R.id.fragment_container)
                        if (container == null || container.childCount == 0) {
                            val orphaned = frag?.view
                            if (container != null && orphaned != null) {
                                (orphaned.parent as? android.view.ViewGroup)?.removeView(orphaned)
                                container.addView(
                                    orphaned,
                                    android.view.ViewGroup.LayoutParams(
                                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                        android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                )
                            } else if (frag == null) {
                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container, ExtraSettingsFragment())
                                    .commit()
                            }
                        }
                    }
                )
            }
        }
    }

    fun updateTitle(title: String) {
        currentTitle = title
    }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: androidx.preference.Preference): Boolean {
        val fragmentName = pref.fragment ?: return false
        val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, fragmentName)
        fragment.arguments = pref.extras

        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()

        currentTitle = pref.title?.toString() ?: currentTitle
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            return true
        }
        return super.onSupportNavigateUp()
    }
}
