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
import androidx.compose.material.icons.rounded.ContactPhone
import androidx.compose.material.icons.rounded.PhoneInTalk
import androidx.compose.material.icons.rounded.Timelapse
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import compose.icons.TablerIcons
import compose.icons.tablericons.Copy
import org.kiteio.punica.R
import org.kiteio.punica.Toast
import org.kiteio.punica.edu.EmergencyCall
import org.kiteio.punica.getString
import org.kiteio.punica.ui.component.Icon
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.component.Text
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route

/**
 * 紧急电话
 */
@Composable
fun EmergencyCallsScreen() {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    ScaffoldBox(topBar = { NavBackTopAppBar(route = Route.Module.EmergencyCalls) }) {
        LazyColumn(contentPadding = PaddingValues(dp4(2))) {
            items(EmergencyCall.values) {
                Call(
                    emergencyCall = it,
                    onCopy = {
                        clipboardManager.setText(buildAnnotatedString { append(it.phoneNumber) })
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
    ElevatedCard(onClick = { }, modifier = modifier) {
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
                Text(text = emergencyCall.workingHours(), leadingIcon = Icons.Rounded.Timelapse)
                Text(text = emergencyCall.phoneNumber, leadingIcon = Icons.Rounded.PhoneInTalk)
            }
            Row {
                IconButton(onClick = onCopy) {
                    Icon(imageVector = TablerIcons.Copy)
                }
                Spacer(modifier = Modifier.width(dp4()))
                IconButton(onClick = onOpenInPhoneBook) {
                    Icon(imageVector = Icons.Rounded.ContactPhone)
                }
            }
        }
    }
}