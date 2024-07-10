package org.kiteio.punica.candy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.Flow

/**
 * 初始值为 null 的 [collectAsState]
 * @receiver [Flow]<[T]>
 * @return [State]<[T]?>
 */
@Composable
fun <T> Flow<T>.collectAsState() = collectAsState(initial = null)