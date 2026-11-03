package af.shizuku.manager.settings.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.FrameLayout
import af.shizuku.manager.R

/**
 * 附加功能页外壳。
 *
 * 写法与 [SettingsScreen] 保持一致：Material3 Scaffold + TopAppBar，内容区用
 * AndroidView(FrameLayout) 承载 PreferenceFragment。本页不包含搜索层与一指缩放，
 * 避免与设置页耦合。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtraScreen(
    title: String,
    onNavigateUp: () -> Unit,
    onContainerCreated: () -> Unit
) {
    val isOneUi = af.shizuku.manager.ShizukuSettings.isOneUiThemeEnabled()

    BackHandler { onNavigateUp() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (isOneUi) {
                LargeTopAppBar(
                    title = { Text(text = title) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateUp) {
                            Icon(
                                painter = painterResource(R.drawable.ic_back_24),
                                contentDescription = stringResource(R.string.accessibility_back)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = if (af.shizuku.manager.ShizukuSettings.isBlurUiEnabled())
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                        else
                            MaterialTheme.colorScheme.surfaceContainer,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            } else {
                TopAppBar(
                    title = { Text(text = title) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateUp) {
                            Icon(
                                painter = painterResource(R.drawable.ic_back_24),
                                contentDescription = stringResource(R.string.accessibility_back)
                            )
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        AndroidView(
            factory = { context ->
                FrameLayout(context).apply {
                    id = R.id.fragment_container
                    clipToPadding = false
                    post { onContainerCreated() }
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
        )
    }
}
