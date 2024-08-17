package org.kiteio.punica.ui.screen.module

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AllInbox
import androidx.compose.material.icons.rounded.AppSettingsAlt
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.LocalLibrary
import androidx.compose.material.icons.rounded.PersonalVideo
import androidx.compose.material.icons.rounded.Portrait
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.SpaceDashboard
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import compose.icons.TablerIcons
import compose.icons.tablericons.Copy
import org.kiteio.punica.R
import org.kiteio.punica.Toast
import org.kiteio.punica.candy.route
import org.kiteio.punica.candy.setText
import org.kiteio.punica.edu.CampusNet
import org.kiteio.punica.edu.SecondClass
import org.kiteio.punica.edu.WebVPN
import org.kiteio.punica.edu.system.EduSystem
import org.kiteio.punica.getString
import org.kiteio.punica.openUri
import org.kiteio.punica.ui.component.Icon
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.component.SubduedText
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route

/**
 * 常用网站
 */
@Composable
fun WebSiteScreen() {
    val clipboardManager = LocalClipboardManager.current
    val websites = listOf(
        Website(R.string.gdufe_all, Icons.Rounded.AllInbox, WebVPN.root),
        Website(
            R.string.gdufe_all_app, Icons.Rounded.AppSettingsAlt, "https://gctzy.gdufe.edu.cn"
        ),
        Website(R.string.gdufe_portal, Icons.Rounded.Portrait, "https://imy.gdufe.edu.cn/"),
        Website(R.string.gdufe_finance, Icons.Rounded.AttachMoney, "https://cwsfxt.gdufe.edu.cn"),
        Website(R.string.gdufe_edu_system, Icons.Rounded.School, EduSystem.route { BASE }),
        Website(
            R.string.gdufe_blackboard, Icons.Rounded.SpaceDashboard, "https://bb.gdufe.edu.cn"
        ),
        Website(Route.Module.SecondClass, SecondClass.root),
        Website(Route.Module.CampusNet, CampusNet.root),
        Website(R.string.gdufe_mooc, Icons.Rounded.PersonalVideo, "https://www.gdufemooc.cn"),
        Website(R.string.library, Icons.Rounded.LocalLibrary, "https://lib.gdufe.edu.cn/main.htm")
    )

    ScaffoldBox(topBar = { NavBackTopAppBar(route = Route.Module.Website) }) {
        LazyColumn(contentPadding = PaddingValues(dp4(2))) {
            items(websites) {
                WebsiteItem(
                    website = it,
                    onCopy = {
                        clipboardManager.setText(it.url)
                        Toast(R.string.copied).show()
                    },
                    modifier = Modifier.padding(dp4(2))
                )
            }
        }
    }
}


/**
 * 网站项
 * @param website
 * @param modifier
 */
@Composable
private fun WebsiteItem(
    website: Website,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    with(website) {
        ElevatedCard(onClick = { openUri(url) }, modifier = modifier) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dp4(4))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = icon)
                    Spacer(modifier = Modifier.width(dp4(4)))

                    Column {
                        Title(text = getString(nameResId))
                        SubduedText(text = url)
                    }
                }

                IconButton(onClick = onCopy) {
                    Icon(imageVector = TablerIcons.Copy)
                }
            }
        }
    }
}


/**
 * 网站描述
 * @property nameResId
 * @property icon
 * @property url
 */
data class Website(
    @StringRes val nameResId: Int,
    val icon: ImageVector,
    val url: String,
) {
    constructor(route: Route, url: String) : this(route.nameResId, route.icon, url)
}