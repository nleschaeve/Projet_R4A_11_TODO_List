package com.example.todolist.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import com.example.todolist.controller.TaskController
import com.example.todolist.model.entity.Task
import com.example.todolist.model.entity.TaskState
import com.example.todolist.model.entity.TaskPriority
import com.example.todolist.navigation.NavRoutes
import com.example.todolist.ui.theme.Green
import com.example.todolist.ui.theme.Red
import com.example.todolist.ui.theme.Orange
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.random.Random

@Composable
fun TaskListView(controller: TaskController, navController: NavController) {
    val scope = rememberCoroutineScope()
    val tasks by controller.getAllTasks().collectAsState(initial = emptyList())
    var sortedAsc by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }

    val congratulationMessages = listOf(
        "🥳 Bravo ! Tâche accomplie ! 🥳",
        "🚀 Félicitations ! Vous êtes efficace ! 🚀",
        "✨ Magnifique ! Une de moins ! ✨",
        "🎯 Quel talent ! Continuez comme ça ! 🎯",
        "💪 Productivité au top ! 💪"
    )

    // Alerte pour les tâches en retard (affichée une seule fois au changement du nombre)
    var lastLateCount by remember { mutableIntStateOf(0) }
    LaunchedEffect(tasks) {
        val currentLateCount = tasks.count { it.state == TaskState.LATE }
        if (currentLateCount > lastLateCount) {
            snackbarHostState.showSnackbar(
                message = "Attention : Vous avez $currentLateCount tâche(s) en retard ! ⚠️",
                duration = SnackbarDuration.Short
            )
        }
        lastLateCount = currentLateCount
    }

    // Tri des tâches par priorité (CRITICAL -> IMPORTANT -> NONE), puis par état (LATE -> TODO -> DONE), puis par date
    val sortedTasks = remember(tasks, sortedAsc) {
        val priorityOrder = mapOf(
            TaskPriority.CRITICAL to 0,
            TaskPriority.IMPORTANT to 1,
            TaskPriority.NONE to 2
        )
        val stateOrder = mapOf(
            TaskState.LATE to 0,
            TaskState.TODO to 1,
            TaskState.DONE to 2
        )
        if (sortedAsc) {
            tasks.sortedWith(compareBy(
                { priorityOrder[it.priority] },
                { stateOrder[it.state] },
                { it.dueDate ?: LocalDate.MAX }
            ))
        } else {
            tasks.sortedWith(compareByDescending<Task> { priorityOrder[it.priority] }
                .thenByDescending { stateOrder[it.state] }
                .thenByDescending { it.dueDate ?: LocalDate.MIN })
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 28.dp, vertical = 16.dp)
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
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { navController.navigate(NavRoutes.TaskAdd.route) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Ajouter")
                    }
                }
                
                Surface(
                    onClick = { sortedAsc = !sortedAsc },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filtrer",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight(1000)
                        )
                        Icon(
                            imageVector = if (sortedAsc) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                            contentDescription = null
                        )
                    }
                }
            }

            // Liste des tâches
            if (tasks.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucune tâche pour le moment")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sortedTasks, key = { it.id }) { task ->
                        Box(modifier = Modifier.animateItem()) {
                            TaskItem(
                                task = task,
                                displayTask = { navController.navigate(NavRoutes.TaskDetail.createRoute(task.id)) },
                                onMarkTodo = {
                                    scope.launch { 
                                        controller.markTaskAsTodo(task)
                                        controller.checkAndUpdateLateTasks()
                                    }
                                },
                                onMarkComplete = {
                                    scope.launch {
                                        controller.completeTask(task)
                                        snackbarHostState.showSnackbar(
                                            message = congratulationMessages[Random.nextInt(congratulationMessages.size)],
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            )
                        }
                    }
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
    
    // Animation de la couleur d'état
    val targetColor = when (task.state) {
        TaskState.TODO -> Orange
        TaskState.LATE -> Red
        TaskState.DONE -> Green
    }
    val animatedStateColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 500),
        label = "stateColor"
    )

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(animatedStateColor)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    modifier = Modifier.height(height = 65.dp)
                ) {
                    TextButton(
                        onClick = displayTask,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier.padding(0.dp)
                    ) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(0.dp)
                        )
                    }
                    if (task.priority.toString() != "Aucune") {
                        Text(
                            text = task.priority.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .offset(y = (-15).dp)
                                .padding(horizontal = 25.dp)
                        )
                    }
                }
            }
            
            Box {
                Button(
                    onClick = { expanded = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = animatedStateColor),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text(
                        text = task.state.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
                
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text("À faire") },
                        onClick = { onMarkTodo(); expanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Réalisée") },
                        onClick = { onMarkComplete(); expanded = false }
                    )
                }
            }
        }
    }
}