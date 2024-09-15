package org.kiteio.punica.ui.screen.bottom

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.datastore.preferences.core.edit
import kotlinx.serialization.Serializable
import org.kiteio.punica.R
import org.kiteio.punica.Toast
import org.kiteio.punica.candy.launchCatch
import org.kiteio.punica.datastore.Identified
import org.kiteio.punica.datastore.Todos
import org.kiteio.punica.datastore.remove
import org.kiteio.punica.datastore.set
import org.kiteio.punica.datastore.MutableStateBooleanSerializer
import org.kiteio.punica.getString
import org.kiteio.punica.ui.collectAsIdentifiedList
import org.kiteio.punica.ui.component.*
import org.kiteio.punica.ui.dp4
import java.util.*

/**
 * 待办
 */
@Composable
fun TodoScreen() {
    val coroutineScope = rememberCoroutineScope()
    val todos = Todos.collectAsIdentifiedList<Todo>()

    var todoDialogVisible by remember { mutableStateOf(false) }
    var deleteDialogVisible by remember { mutableStateOf(false) }
    var visibleTodo by remember { mutableStateOf<Todo?>(null) }

    ScaffoldBox(
        floatingActionButton = {
            FloatingActionButton(onClick = { visibleTodo = null; todoDialogVisible = true }) {
                Icon(imageVector = Icons.Rounded.Add)
            }
        }
    ) {
        LazyColumn(contentPadding = PaddingValues(dp4(2))) {
            items(todos.sortedBy { it.done.value }) { todo ->
                ElevatedCard(
                    onClick = { visibleTodo = todo; todoDialogVisible = true },
                    modifier = Modifier
                        .padding(dp4(2))
                        .animateItem(),
                    enabled = !todo.done.value
                ) {
                    Row(
                        modifier = Modifier.padding(dp4(4)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Title(text = todo.name)
                            SubduedText(text = todo.desc)
                            KVText(key = getString(R.string.deadline), value = todo.deadline)
                        }

                        Row {
                            IconButton(
                                onClick = {
                                    coroutineScope.launchCatch {
                                        val newTodo = !todo
                                        Todos.edit { it.set(newTodo) }
                                        Toast(if (newTodo.done.value) R.string.done else R.string.reset_complete).show()
                                    }
                                },
                                enabled = true
                            ) {
                                Icon(
                                    imageVector = if (todo.done.value) Icons.Rounded.Replay
                                    else Icons.Rounded.Done
                                )
                            }

                            IconButton(
                                onClick = {
                                    visibleTodo = todo
                                    deleteDialogVisible = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    TodoDialog(
        visible = todoDialogVisible,
        onDismiss = { todoDialogVisible = false },
        todo = visibleTodo
    )

    DeleteDialog(
        visible = deleteDialogVisible,
        onDismiss = { deleteDialogVisible = false },
        onConfirm = {
            coroutineScope.launchCatch {
                visibleTodo?.let { todo ->
                    Todos.edit { it.remove(todo) }
                }
                deleteDialogVisible = false
                Toast(R.string.deleted).show()
            }
        },
        desc = visibleTodo?.name
    )
}


/**
 * 待办编辑
 * @param visible
 * @param onDismiss
 * @param todo
 */
@Composable
private fun TodoDialog(visible: Boolean, onDismiss: () -> Unit, todo: Todo?) {
    DialogVisibility(visible = visible) {
        val coroutineScope = rememberCoroutineScope()

        var name by remember { mutableStateOf(todo?.name ?: "") }
        var desc by remember { mutableStateOf(todo?.desc ?: "") }
        var deadline by remember { mutableStateOf(todo?.deadline ?: "") }

        Dialog(
            title = { Text(text = getString(R.string.todo_editing)) },
            text = {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = getString(R.string.name)) }
                )
                Spacer(modifier = Modifier.height(dp4(2)))

                TextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text(text = getString(R.string.description)) }
                )
                Spacer(modifier = Modifier.height(dp4(2)))

                TextField(
                    value = deadline,
                    onValueChange = { deadline = it },
                    label = { Text(text = getString(R.string.deadline)) }
                )
            },
            onDismiss = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launchCatch {
                            Todos.edit {
                                it.set(
                                    Todo(
                                        todo?.id ?: Date().time.toString(), name, desc, deadline
                                    )
                                )
                                Toast(R.string.saved).show()
                                onDismiss()
                            }
                        }
                    }
                ) {
                    Text(text = getString(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text(text = getString(R.string.cancel)) }
            }
        )
    }
}


/**
 * 代办项
 * @property id
 * @property name 名称
 * @property desc 详细描述
 * @property deadline 截止时间
 * @property done 是否已完成
 */
@Serializable
class Todo(
    override val id: String,
    val name: String,
    val desc: String,
    val deadline: String,
    val done: @Serializable(with = MutableStateBooleanSerializer::class) MutableState<Boolean> = mutableStateOf(false)
) : Identified() {
    operator fun not() = this.also { done.value = !done.value }
}