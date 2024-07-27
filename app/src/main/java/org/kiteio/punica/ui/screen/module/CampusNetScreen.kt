package org.kiteio.punica.ui.screen.module

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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PermIdentity
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kiteio.punica.datastore.CampusNetUsers
import org.kiteio.punica.R
import org.kiteio.punica.Toast
import org.kiteio.punica.datastore.Users
import org.kiteio.punica.candy.catching
import org.kiteio.punica.candy.collectAsState
import org.kiteio.punica.candy.launchCatching
import org.kiteio.punica.candy.limit
import org.kiteio.punica.datastore.get
import org.kiteio.punica.datastore.remove
import org.kiteio.punica.datastore.set
import org.kiteio.punica.edu.CampusNet
import org.kiteio.punica.edu.foundation.CampusNetUser
import org.kiteio.punica.edu.foundation.User
import org.kiteio.punica.getString
import org.kiteio.punica.ui.collectAsIdentifiedList
import org.kiteio.punica.ui.component.DeleteDialog
import org.kiteio.punica.ui.component.Dialog
import org.kiteio.punica.ui.component.DialogVisibility
import org.kiteio.punica.ui.component.Icon
import org.kiteio.punica.ui.component.IconText
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.component.TextField
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route
import org.kiteio.punica.ui.rememberUser
import java.util.Date

/**
 * 校园网
 */
@Composable
fun CampusNetScreen() {
    val coroutineScope = rememberCoroutineScope()
    val campusNetUsers = CampusNetUsers.collectAsIdentifiedList<CampusNetUser>()
    var campusNetUserDialogVisible by remember { mutableStateOf(false) }
    var deleteDialogVisible by remember { mutableStateOf(false) }
    var visibleCampusNetUser by remember { mutableStateOf<CampusNetUser?>(null) }

    ScaffoldBox(
        topBar = { NavBackTopAppBar(route = Route.Module.CampusNet) },
        floatingActionButton = {
            FloatingActionButton(onClick = { campusNetUserDialogVisible = true }) {
                Icon(imageVector = Icons.Rounded.Add)
            }
        }
    ) {
        LazyColumn(contentPadding = PaddingValues(dp4(2))) {
            items(campusNetUsers) { campusNetUser ->
                val user = rememberUser(name = campusNetUser.name)
                var checked by remember { mutableStateOf(false) }

                // 自动登录
                LaunchedEffect(key1 = user) { checked = campusNetUser.login(user) }

                ElevatedCard(
                    onClick = {
                        visibleCampusNetUser = campusNetUser
                        campusNetUserDialogVisible = true
                    },
                    modifier = Modifier
                        .padding(dp4(2))
                        .animateItem()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dp4(4)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Title(
                                text = campusNetUser.desc,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(dp4()))
                            IconText(
                                text = campusNetUser.name,
                                leadingIcon = Icons.Rounded.PermIdentity,
                                leadingText = getString(R.string.student_id),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.width(dp4(2)))

                        Row {
                            IconButton(
                                onClick = {
                                    visibleCampusNetUser = campusNetUser
                                    deleteDialogVisible = true
                                }
                            ) {
                                Icon(imageVector = Icons.Rounded.Close)
                            }
                            Spacer(modifier = Modifier.width(dp4(2)))
                            Switch(
                                checked = checked,
                                onCheckedChange = {
                                    coroutineScope.launchCatching {
                                        if (it) {
                                            checked = campusNetUser.login(user) {
                                                Toast(R.string.connected).show()
                                            }
                                        } else {
                                            checked = false
                                            CampusNet.logout(campusNetUser.ip)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    DeleteDialog(
        visible = deleteDialogVisible,
        onDismiss = {
            coroutineScope.launch {
                deleteDialogVisible = false; delay(50); visibleCampusNetUser = null
            }
        },
        onConfirm = {
            visibleCampusNetUser?.let { campusNetUser ->
                coroutineScope.launchCatching {
                    CampusNetUsers.edit { it.remove(campusNetUser) }
                }
            }
        },
        desc = visibleCampusNetUser?.desc
    )

    CampusNetUserDialog(
        visible = campusNetUserDialogVisible,
        onDismiss = { visibleCampusNetUser = null; campusNetUserDialogVisible = false },
        campusNetUser = visibleCampusNetUser
    )
}


/**
 * 登录校园网，登录成功返回 true
 * @receiver [CampusNetUser]
 * @param user
 * @param onLoggedIn 登录成功回调
 * @return [Boolean]
 */
private suspend fun CampusNetUser.login(user: User?, onLoggedIn: () -> Unit = {}) = user?.run {
    catching<Boolean> {
        CampusNet.login(name, user.campusNetPwd, ip)
        onLoggedIn()
        true
    }
} ?: false


/**
 * 校园网用户编辑
 * @param visible
 * @param onDismiss
 * @param campusNetUser
 */
@Composable
private fun CampusNetUserDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    campusNetUser: CampusNetUser?
) {
    DialogVisibility(visible = visible) {
        val coroutineScope = rememberCoroutineScope()
        val users by Users.data.collectAsState()
        var name by remember { mutableStateOf(campusNetUser?.name ?: "") }
        var pwd by remember { mutableStateOf("") }
        var ipPlaceholder by remember { mutableStateOf("") }
        var ip by remember { mutableStateOf(campusNetUser?.ip ?: "") }
        var desc by remember { mutableStateOf(campusNetUser?.desc ?: "") }

        // 更新密码
        LaunchedEffect(key1 = users, key2 = name) {
            if (name.length == 11) {
                users?.get<User>(name)?.campusNetPwd?.let { pwd = it }
            }
        }

        // 更新 ip
        LaunchedEffect(key1 = Unit) {
            ipPlaceholder =
                CampusNet.ip() ?: getString(R.string.connect_to_the_campus_net_to_get_ip)
        }

        Dialog(
            title = { Text(text = getString(R.string.account_editing)) },
            text = {
                TextField(
                    value = name,
                    onValueChange = { name = it.limit(11) },
                    label = {
                        Text(text = getString(R.string.student_id) + getString(R.string.required))
                    }
                )
                Spacer(modifier = Modifier.height(dp4(2)))

                TextField(
                    value = pwd,
                    onValueChange = { pwd = it },
                    label = {
                        Text(
                            text = getString(R.string.campus_net_password)
                                    + getString(R.string.required)
                        )
                    },
                    placeholder = { Text(text = getString(R.string.default_is_last_8_of_id_card)) }
                )
                Spacer(modifier = Modifier.height(dp4(2)))

                TextField(
                    value = ip,
                    onValueChange = { value -> ip = value.filter { it.isDigit() || it == '.' } },
                    label = { Text(text = getString(R.string.device_ip)) },
                    placeholder = { Text(text = ipPlaceholder) }
                )
                Spacer(modifier = Modifier.height(dp4(2)))

                TextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text(text = getString(R.string.description)) }
                )
            },
            onDismiss = onDismiss,
            confirmButton = {
                TextButton(
                    enabled = name.length == 11,
                    onClick = {
                        coroutineScope.launchCatching {
                            val myIp = ip.ifBlank { ipPlaceholder }
                            val myDesc = desc.ifBlank { "${getString(R.string.campus_net)}$myIp" }
                            val user = CampusNetUser(
                                name, myIp, myDesc, campusNetUser?.id ?: Date().time.toString()
                            )

                            CampusNetUsers.edit {
                                it.set(user)
                            }
                            Users.edit {
                                it.set(it.get<User>(name)?.apply { campusNetPwd = pwd }
                                    ?: User(name, campusNetPwd = pwd))
                            }

                            Toast(R.string.saved).show()
                            onDismiss()
                        }
                    }
                ) { Text(text = getString(R.string.save)) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text(text = getString(R.string.cancel)) }
            }
        )
    }
}