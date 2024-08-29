package org.kiteio.punica.ui.screen.module

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.PhoneInTalk
import androidx.compose.material.icons.rounded.Timelapse
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.Copy
import org.kiteio.punica.R
import org.kiteio.punica.Toast
import org.kiteio.punica.candy.collectAsState
import org.kiteio.punica.candy.setText
import org.kiteio.punica.datastore.Keys
import org.kiteio.punica.datastore.Preferences
import org.kiteio.punica.edu.EmergencyCall
import org.kiteio.punica.getString
import org.kiteio.punica.ui.component.Icon
import org.kiteio.punica.ui.component.IconText
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.component.TabPager
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.component.rememberTabPagerState
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route
import org.kiteio.punica.ui.subduedContentColor

/**
 * 紧急电话
 */
@Composable
fun EmergencyCallsScreen() {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val preferences by Preferences.data.collectAsState()
    val tabPagerState = rememberTabPagerState(R.string.campus_canton, R.string.campus_foshan)

    LaunchedEffect(key1 = preferences) {
        preferences?.get(Keys.campusId)?.let {
            tabPagerState.scrollToPage(it - 1)
        }
    }

    ScaffoldBox(
        topBar = { NavBackTopAppBar(route = Route.Module.EmergencyCalls, shadowElevation = 0.dp) }
    ) {
        TabPager(state = tabPagerState, tabContent = { Text(text = getString(it)) }) { page ->
            LazyColumn(contentPadding = PaddingValues(dp4(2))) {
                items(if (page == 0) EmergencyCall.Canton.values else EmergencyCall.Foshan.values) {
                    Call(
                        emergencyCall = it,
                        onCopy = {
                            clipboardManager.setText(it.phoneNumber)
                            Toast(R.string.copied).show()
                        },
                        onOpenInPhoneBook = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${it.phoneNumber}")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.padding(dp4(2))
                    )
                }
            }
        }
    }
}


/**
 * 电话
 * @param emergencyCall
 * @param onCopy 复制
 * @param onOpenInPhoneBook 在电话本打开
 * @param modifier
 */
@Composable
private fun Call(
    emergencyCall: EmergencyCall,
    onCopy: () -> Unit,
    onOpenInPhoneBook: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(onClick = {}, modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dp4(4)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Title(text = getString(emergencyCall.nameResId))
                Spacer(modifier = Modifier.height(dp4()))

                CompositionLocalProvider(
                    value = LocalTextStyle provides MaterialTheme.typography.bodyMedium.copy(
                        color = subduedContentColor()
                    )
                ) {
                    IconText(
                        text = emergencyCall.workingHours(),
                        leadingIcon = Icons.Rounded.Timelapse
                    )
                    IconText(text = emergencyCall.phoneNumber, leadingIcon = Icons.Rounded.Call)
                }
            }
            Row {
                IconButton(onClick = onCopy) {
                    Icon(imageVector = TablerIcons.Copy)
                }
                Spacer(modifier = Modifier.width(dp4()))
                IconButton(onClick = onOpenInPhoneBook) {
                    Icon(imageVector = Icons.Rounded.PhoneInTalk)
                }
            }
        }
    }
}