package com.example.todolist.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import com.example.todolist.controller.TaskController
import com.example.todolist.model.entity.Task
import com.example.todolist.model.entity.TaskState
import com.example.todolist.navigation.NavRoutes
import com.example.todolist.ui.theme.Green
import com.example.todolist.ui.theme.Red
import com.example.todolist.ui.theme.Orange
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun TaskListView(controller: TaskController, navController: NavController) {
    val scope = rememberCoroutineScope()
    val tasks by controller.getAllTasks().collectAsState(initial = emptyList())
    var sortedAsc by remember { mutableStateOf(true) }

    // Tri des tâches par état (TODO -> LATE -> DONE) puis par date si renseignée
    val sortedTasks = remember(tasks, sortedAsc) {
        val stateOrder = mapOf(
            TaskState.LATE to 0,
            TaskState.TODO to 1,
            TaskState.DONE to 2
        )
        if (sortedAsc) {
            tasks.sortedWith(compareBy(
                { stateOrder[it.state] },
                { it.dueDate ?: LocalDate.MAX }
            ))
        } else {
            tasks.sortedWith(compareByDescending<Task> { stateOrder[it.state] }
                .thenByDescending { it.dueDate ?: LocalDate.MIN })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Text(
                text = "Tic-Task",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight(1000)
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp, 200.dp, 28.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tâches :",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(end = 16.dp),
                    fontWeight = FontWeight(1000)
                )

                Button(
                    onClick = {
                        scope.launch {
                            navController.navigate(NavRoutes.TaskAdd.route)
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp)
                        .padding(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Ajouter une tâche",
                        tint = Color(MaterialTheme.colorScheme.onSecondary.value)
                    )
                }
            }
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(10.dp, 6.dp)
            ) {
                Button (
                    onClick = {
                        sortedAsc = !sortedAsc
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                    modifier = Modifier.padding(0.dp)
                ) {
                    Text(
                        text = "Filtrer",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight(1000)
                    )
                }
                Icon(
                    imageVector = if (sortedAsc) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = "sort by state",
                    tint = Color(MaterialTheme.colorScheme.onSurface.value)
                )
            }
        }

        // Affichage de la liste des tâches
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Aucune tâche pour le moment")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortedTasks) { task ->
                    TaskItem(
                        task = task,
                        displayTask = {
                            navController.navigate(NavRoutes.TaskDetail.createRoute(task.id))
                        },
                        onMarkTodo = {
                            scope.launch {
                                controller.markTaskAsTodo(task)
                            }
                        },
                        onMarkComplete = {
                            scope.launch {
                                controller.completeTask(task)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onMarkTodo: () -> Unit,
    onMarkComplete: () -> Unit,
    displayTask: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    // Point de couleur selon l'état
    val stateColor = when (task.state) {
        TaskState.TODO -> Color(Orange.value) // Orange
        TaskState.LATE -> Color(Red.value) // Rouge
        TaskState.DONE -> Color(Green.value) // Vert
    }
    Card(
        colors = Color(MaterialTheme.colorScheme.surfaceContainerLow.value).let { backgroundColor ->
            CardDefaults.cardColors(containerColor = backgroundColor)
        },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(stateColor)
                )
                Button(
                    onClick = displayTask,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 4.em,
                        fontWeight = FontWeight(1000)
                    )
                }
            }
            Row (verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { expanded = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = stateColor),
                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
                    modifier = Modifier
                        .width(80.dp)
                        .padding(0.dp)
                ) {
                    Text(
                        text = task.state.toString(),
                        fontWeight = FontWeight(600),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("À faire") },
                        onClick = {
                            expanded = false
                            onMarkTodo()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Réalisée") },
                        onClick = {
                            expanded = false
                            onMarkComplete()
                        }
                    )
                }

            }
        }
    }
}