package org.kiteio.punica.ui.screen.bottom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import kotlinx.serialization.Serializable
import org.kiteio.punica.R
import org.kiteio.punica.Toast
import org.kiteio.punica.candy.launchCatching
import org.kiteio.punica.datastore.Identified
import org.kiteio.punica.datastore.Todos
import org.kiteio.punica.datastore.remove
import org.kiteio.punica.datastore.set
import org.kiteio.punica.getString
import org.kiteio.punica.ui.collectAsIdentifiedList
import org.kiteio.punica.ui.component.DeleteDialog
import org.kiteio.punica.ui.component.Dialog
import org.kiteio.punica.ui.component.DialogVisibility
import org.kiteio.punica.ui.component.Icon
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.component.SubduedText
import org.kiteio.punica.ui.component.TextField
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.dp4
import java.util.Date

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
            FloatingActionButton(onClick = { todoDialogVisible = true }) {
                Icon(imageVector = Icons.Rounded.Add)
            }
        }
    ) {
        LazyColumn(contentPadding = PaddingValues(dp4(2))) {
            items(todos.sortedBy { it.done }) { todo ->
                ElevatedCard(
                    onClick = { visibleTodo = todo; todoDialogVisible = true },
                    modifier = Modifier.padding(dp4(2)),
                    enabled = !todo.done
                ) {
                    Row(
                        modifier = Modifier.padding(dp4(4)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Title(text = todo.name)
                            SubduedText(text = todo.desc)
                            SubduedText(text = todo.deadline.toString())
                        }

                        Row {
                            IconButton(
                                onClick = {
                                    coroutineScope.launchCatching {
                                        val newTodo = !todo
                                        Todos.edit { it.set(newTodo) }
                                        Toast(
                                            if (newTodo.done) R.string.done else R.string.reset
                                        ).show()
                                    }
                                },
                                enabled = true
                            ) {
                                Icon(
                                    imageVector = if (todo.done) Icons.Rounded.Replay
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
        onDismiss = { todoDialogVisible = false; visibleTodo = null },
        todo = visibleTodo
    )

    DeleteDialog(
        visible = deleteDialogVisible,
        onDismiss = {
            coroutineScope.launch {
                deleteDialogVisible = false; delay(50); visibleTodo = null
            }
        },
        onConfirm = {
            visibleTodo?.let { todo ->
                coroutineScope.launchCatching {
                    Todos.edit { it.remove(todo) }
                }
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
                TextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text(text = getString(R.string.description)) })
                TextField(
                    value = deadline,
                    onValueChange = { deadline = it },
                    label = { Text(text = getString(R.string.deadline)) })
            },
            onDismiss = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launchCatching {
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
    val done: Boolean = false
) : Identified() {
    operator fun not() = copy(!done)


    private fun copy(done: Boolean) = Todo(id, name, desc, deadline, done)
}