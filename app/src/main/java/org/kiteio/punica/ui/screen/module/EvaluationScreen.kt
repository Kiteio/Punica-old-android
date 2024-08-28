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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import org.kiteio.punica.R
import org.kiteio.punica.Toast
import org.kiteio.punica.candy.launchCatching
import org.kiteio.punica.edu.system.EduSystem
import org.kiteio.punica.edu.system.api.EvaluateItem
import org.kiteio.punica.edu.system.api.evaluate
import org.kiteio.punica.edu.system.api.evaluateList
import org.kiteio.punica.getString
import org.kiteio.punica.ui.LocalViewModel
import org.kiteio.punica.ui.component.Dialog
import org.kiteio.punica.ui.component.DialogVisibility
import org.kiteio.punica.ui.component.Icon
import org.kiteio.punica.ui.component.IconText
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.RadioButton
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.component.SubduedText
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route
import org.kiteio.punica.ui.rememberRemoteList
import org.kiteio.punica.ui.runWithReLogin

/**
 * 教学评价
 */
@Composable
fun EvaluationScreen() {
    val eduSystem = LocalViewModel.current.eduSystem
    val coroutineScope = rememberCoroutineScope()
    val evaluateItems = rememberRemoteList(key = eduSystem) {
        eduSystem?.runWithReLogin { evaluateList().sortedBy { it.route == null } }
    }
    var stateSelectDialogVisible by remember { mutableStateOf(false) }
    var isAll by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(key1 = Unit) {
        if (eduSystem == null) Toast(R.string.not_logged_in).show()
    }

    ScaffoldBox(
        topBar = {
            NavBackTopAppBar(
                route = Route.Module.Evaluation,
                actions = {
                    IconButton(
                        onClick = {
                            if (evaluateItems.isNotEmpty()) {
                                isAll = false
                                stateSelectDialogVisible = true
                                selectedIndex = -1
                            } else {
                                Toast(R.string.noting_to_evaluate).show()
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Rounded.EditNote)
                    }
                }
            )
        }
    ) {
        LazyColumn(contentPadding = PaddingValues(dp4(2))) {
            itemsIndexed(evaluateItems) { index, item ->
                EvaluateItem(
                    evaluateItem = item,
                    onEvaluate = {
                        isAll = false
                        stateSelectDialogVisible = true
                        selectedIndex = index
                    },
                    modifier = Modifier
                        .padding(dp4(2))
                        .animateItem()
                )
            }
        }
    }

    StateSelectDialog(
        visible = stateSelectDialogVisible,
        onDismiss = { stateSelectDialogVisible = false },
        onSelect = { submit ->
            coroutineScope.launchCatching {
                eduSystem?.runWithReLogin {
                    if (selectedIndex == -1)
                        for (index in evaluateItems.indices.reversed())
                            evaluate(evaluateItems, coroutineScope, submit, index)
                    else evaluate(evaluateItems, coroutineScope, submit, selectedIndex)
                } ?: Toast(R.string.not_logged_in).show()
            }
        },
        isAll = selectedIndex == -1
    )
}


/**
 * 评教并更新 [evaluateItems]
 * @receiver [EduSystem]
 * @param evaluateItems [MutableList]<[EvaluateItem]>
 * @param submit
 * @param index
 */
private fun EduSystem.evaluate(
    evaluateItems: MutableList<EvaluateItem>,
    coroutineScope: CoroutineScope,
    submit: Boolean,
    index: Int
) = coroutineScope.launchCatching {
    with(evaluateItems) {
        var tmp = get(index).apply {
            if (route == null) return@launchCatching
        }
        evaluate(tmp, submit, false)

        if (submit) {
            tmp = tmp.copy(route = null)
            set(index, tmp)

            // 移动到末尾
            removeAt(index)
            add(tmp)
            Toast(getString(R.string.evaluated, tmp.name)).show()
        } else Toast(getString(R.string.saved) + get(index).name).show()
    }
}


/**
 * 评价项
 * @param evaluateItem
 * @param onEvaluate
 * @param modifier
 */
@Composable
private fun EvaluateItem(
    evaluateItem: EvaluateItem,
    onEvaluate: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(onClick = {}, modifier = modifier, enabled = evaluateItem.route != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dp4(4)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Title(text = evaluateItem.name)
                Spacer(modifier = Modifier.height(dp4(2)))
                IconText(text = evaluateItem.teacher, leadingIcon = Icons.Rounded.Person)
                Spacer(modifier = Modifier.height(dp4()))
                SubduedText(text = evaluateItem.sort)
            }

            if (evaluateItem.route != null) {
                Spacer(modifier = Modifier.width(dp4(4)))

                IconButton(onClick = onEvaluate) {
                    Icon(imageVector = Icons.Rounded.Edit)
                }
            }
        }
    }
}


/**
 * 选取提交状态
 * @param visible
 * @param onDismiss
 * @param onSelect
 * @param isAll 是否为列表所有项
 */
@Composable
private fun StateSelectDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onSelect: (submit: Boolean) -> Unit,
    isAll: Boolean
) {
    DialogVisibility(visible = visible) {
        val options = listOf("保存", "提交")
        var selectedIndex by remember { mutableIntStateOf(0) }

        Dialog(
            title = if (isAll) {
                { Text(text = "评价所有课程") }
            } else null,
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "您希望保存还是直接提交（我们推荐您先保存并检查后再提交）")
                    options.forEachIndexed { index, option ->
                        RadioButton(
                            selected = selectedIndex == index,
                            onClick = { selectedIndex = index },
                            label = option
                        )
                    }
                }
            },
            onDismiss = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = { onSelect(selectedIndex == 1); onDismiss() }
                ) { Text(text = getString(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = getString(R.string.cancel))
                }
            }
        )
    }
}