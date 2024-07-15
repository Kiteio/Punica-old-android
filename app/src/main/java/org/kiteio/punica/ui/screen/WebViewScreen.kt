package org.kiteio.punica.ui.screen

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import io.ktor.http.HttpHeaders
import org.kiteio.punica.candy.URLs
import org.kiteio.punica.ui.component.ScaffoldBox

/**
 * 网页
 * @param navBackStackEntry
 */
@Composable
fun WebViewScreen(navBackStackEntry: NavBackStackEntry) {
    val state = rememberWebViewState(
        url = navBackStackEntry.arguments?.getString("url").let { Uri.decode(it) }
            ?: URLs.PUNICA,
        additionalHttpHeaders = mapOf(HttpHeaders.UserAgent to System.getProperty("http.agent")!!)
    )
    val navigator = rememberWebViewNavigator()

    ScaffoldBox {
        WebView(state = state, navigator = navigator)
    }
}