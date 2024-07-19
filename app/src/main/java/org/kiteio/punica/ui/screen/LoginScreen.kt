package org.kiteio.punica.ui.screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateBefore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.http.Cookie
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.kiteio.punica.datastore.Preferences
import org.kiteio.punica.R
import org.kiteio.punica.Toast
import org.kiteio.punica.datastore.Users
import org.kiteio.punica.candy.Toast
import org.kiteio.punica.candy.launchCatching
import org.kiteio.punica.candy.limit
import org.kiteio.punica.datastore.Keys
import org.kiteio.punica.datastore.get
import org.kiteio.punica.edu.WebVPN
import org.kiteio.punica.edu.foundation.User
import org.kiteio.punica.edu.system.EduSystem
import org.kiteio.punica.getString
import org.kiteio.punica.ui.AppViewModel
import org.kiteio.punica.ui.LocalNavController
import org.kiteio.punica.ui.LocalViewModel
import org.kiteio.punica.ui.component.Icon
import org.kiteio.punica.ui.component.Image
import org.kiteio.punica.ui.component.PasswordField
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.component.SubduedText
import org.kiteio.punica.ui.component.TextField
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.dp4

/**
 * 登录页
 */
@Composable
fun LoginScreen() {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = LocalViewModel.current
    val navController = LocalNavController.current

    var isLogoScaled by remember { mutableStateOf(false) }
    val logoSize by animateDpAsState(
        targetValue = if (isLogoScaled) dp4(25) else dp4(30),
        label = "logoSize"
    )

    LaunchedEffect(key1 = Unit) {
        while (true) {
            delay(4000)
            repeat(2) {
                isLogoScaled = true
                delay(100)
                isLogoScaled = false
                delay(100)
            }
        }
    }

    ScaffoldBox {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(dp4(2))
        ) {
            Icon(imageVector = Icons.AutoMirrored.Rounded.NavigateBefore)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(dp4(8))
        ) {
            Logo(
                size = logoSize,
                onClick = {
                    coroutineScope.launch {
                        isLogoScaled = true
                        delay(100)
                        isLogoScaled = false
                    }
                }
            )
            Spacer(modifier = Modifier.height(dp4(6)))

            Slogan(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.fillMaxHeight(0.06f))

            with(viewModel { LoginViewModel(viewModel) }) {
                LaunchedEffect(key1 = name) {
                    // 新账号刷新 Cookie
                    if (name.length == 11) {
                        cookies = mutableSetOf()
                    }
                }

                TextField(
                    value = name,
                    onValueChange = { value -> name = value.filter { it.isDigit() }.limit(11) },
                    enabled = interactable,
                    label = { Text(text = getString(R.string.student_id)) },
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(dp4(6)))

                PasswordField(
                    value = pwd,
                    onValueChange = { pwd = it },
                    enabled = interactable,
                    label = { Text(text = getString(R.string.portal_password)) },
                    imeAction = ImeAction.Done
                )
                Spacer(modifier = Modifier.height(dp4(8)))

                Button(
                    onClick = when {
                        viewModel.eduSystem?.name == name -> ::logout
                        interactable -> {
                            { login { navController.popBackStack() } }
                        }

                        else -> ::cancel
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = name.length == 11 && pwd.isNotEmpty() || name == viewModel.eduSystem?.name
                ) {
                    Text(
                        text = getString(
                            when {
                                viewModel.eduSystem?.name == name -> R.string.logout
                                interactable -> R.string.login
                                else -> R.string.cancel
                            }
                        )
                    )
                }
            }
        }
        SubduedText("Kiteio", modifier = Modifier.align(Alignment.BottomCenter))
    }
}


class LoginViewModel(private val viewModel: AppViewModel) : ViewModel() {
    var name by mutableStateOf("")
    var pwd by mutableStateOf("")
    private var secondClassPwd by mutableStateOf("")
    private var campusNetPwd by mutableStateOf("")
    var cookies = mutableSetOf<Cookie>()
    var interactable by mutableStateOf(true)
    private var job: Job? = null

    init {
        // 初始化用户
        viewModelScope.launchCatching {
            Preferences.data.map { it[Keys.lastUsername] }.firstOrNull()?.let { username ->
                refreshUser(username)
            }
        }
    }


    /**
     * 刷新用户信息
     * @param username 学号
     */
    private suspend fun refreshUser(username: String) {
        Users.data.map { it.get<User>(username) }.firstOrNull()?.let {
            name = it.name
            pwd = it.pwd
            secondClassPwd = it.secondClassPwd
            campusNetPwd = it.campusNetPwd
            cookies = it.cookies
        }
    }


    /**
     * 登录
     */
    fun login(onLoggedIn: () -> Unit) {
        interactable = false
        val user = User(name, pwd, secondClassPwd, campusNetPwd, cookies)
        job = viewModelScope.launchCatching(onCatch = { Toast().show(); interactable = true }) {
            flow {
                emit(EduSystem.login(user, false))
            }.cancellable().catch {
                if (it is ConnectTimeoutException) {
                    WebVPN.login(name, pwd, cookies)
                    emit(EduSystem.login(user, true))
                } else throw it
            }.collect {
                viewModel.onLoggedIn(it, user)
                Toast(getString(R.string.logged_in)).show()
                interactable = true
                onLoggedIn()
            }
        }
    }


    /**
     * 取消登录
     */
    fun cancel() {
        if (!interactable) {
            job?.cancel()
            interactable = true
        }
    }


    /**
     * 退出登录
     */
    fun logout() {
        viewModelScope.launch {
            viewModel.onLogout()
            refreshUser(name)  // 防止在退出登录前更改了密码
            Toast(R.string.logout).show()
        }
    }
}


/**
 * Logo
 * @param size Logo 大小
 * @param onClick
 * @param modifier
 */
@Composable
private fun Logo(size: Dp, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.height(remember { size }), contentAlignment = Alignment.Center) {
        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Image(
                painter = painterResource(R.drawable.punica),
                modifier = Modifier
                    .size(size)
                    .clickable(onClick = onClick)
                    .scale(2.1f)
            )
        }
    }
}


/**
 * Slogan
 * @param modifier
 */
@Composable
private fun Slogan(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Title(text = getString(R.string.slogan))
        Spacer(modifier = Modifier.height(dp4()))
        SubduedText(
            text = buildString {
                appendLine(getString(R.string.security_convenient_intelligent))
                appendLine(getString(R.string.open_source_under_the_MIT_license))
            }
        )
    }
}