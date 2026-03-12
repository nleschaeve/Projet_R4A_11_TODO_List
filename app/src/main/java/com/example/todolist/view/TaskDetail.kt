package com.example.todolist.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todolist.controller.TaskController
import com.example.todolist.model.entity.TaskState
import com.example.todolist.ui.theme.Green
import com.example.todolist.ui.theme.Orange
import com.example.todolist.ui.theme.Red
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale
import coil.compose.AsyncImage

@Composable
fun TaskDetail(
    controller: TaskController,
    navController: NavController,
    taskId: Int
) {
    val scope = rememberCoroutineScope()
    val task by controller.getTaskById(taskId).collectAsState(initial = null)

    val formatterDate = DateTimeFormatter.ofPattern(
        "dd MMMM yyyy",
        Locale.FRENCH
    )
    val formatterTime = DateTimeFormatter.ofPattern(
        "HH:mm",
        Locale.FRENCH
    )

    // Snackbar pour confirmation
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        // Contenu principal
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            // Barre de titre avec bouton retour
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp)
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Détails de la Tâche",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Affichage des détails de la tâche
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                var showStateMenu by remember { mutableStateOf(false) }
                var selectedState by remember { mutableStateOf<TaskState?>(null) }

                // mettre à jour selectedState quand la task change
                LaunchedEffect(task) {
                    if (task != null) selectedState = task!!.state
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Title of the task
                    Text(
                        text = task?.title ?: "Chargement...",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight(1000),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        modifier = Modifier.weight(1f)
                    )

                    // State selector (dropdown) — on exclut LATE
                    Box {
                        val displayState = selectedState ?: task?.state
                        Box(
                            modifier = Modifier
                                .background(
                                    color = when (displayState) {
                                        TaskState.TODO -> Color(Orange.value)
                                        TaskState.LATE -> Color(Red.value)
                                        TaskState.DONE -> Color(Green.value)
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    shape = MaterialTheme.shapes.small
                                )
                                .clickable { showStateMenu = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = displayState.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight(600)
                                )
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = "Changer l'état",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(20.dp).padding(start = 4.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showStateMenu,
                            onDismissRequest = { showStateMenu = false }
                        ) {
                            // On propose uniquement TODO et DONE — on exclut LATE
                            TaskState.entries.filter { it != TaskState.LATE }.forEach { stateOption ->
                                DropdownMenuItem(
                                    text = { Text(stateOption.toString()) },
                                    onClick = {
                                        showStateMenu = false
                                        scope.launch {
                                            task?.let { current ->
                                                try {
                                                    // Utiliser updateTaskStateWithCheck pour vérifier le statut LATE
                                                    controller.updateTaskStateWithCheck(current, stateOption)

                                                    // Vérifier l'état après mise à jour
                                                    val updated = controller.getTaskById(taskId).first()
                                                    selectedState = updated?.state

                                                    // Afficher le message approprié
                                                    val message = if (updated?.state == stateOption) {
                                                        "État mis à jour : ${stateOption}"
                                                    } else if (stateOption == TaskState.TODO && updated?.state == TaskState.LATE) {
                                                        "La tâche est en retard, statut forcé à : ${updated.state}"
                                                    } else {
                                                        "État mis à jour : ${updated?.state}"
                                                    }
                                                    snackbarHostState.showSnackbar(message)
                                                } catch (e: Exception) {
                                                    snackbarHostState.showSnackbar("Erreur lors de la mise à jour : ${e.message}")
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Row() {
                    // Image of the task
                    if (!task?.imageUri.isNullOrEmpty()) {
                        AsyncImage(
                            model = task?.imageUri,
                            contentDescription = "Image de la tâche",
                            modifier = Modifier
                                .width(200.dp)
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Column() {
                        // Due date of the task
                        if (task?.dueDate != null) {
                            Text(
                                text = "Date limite : ${task?.dueDate?.format(formatterDate)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight(500),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        // Due Hour of the task
                        if (task?.dueTime != null) {
                            Text(
                                text = "Heure limite : ${task?.dueTime?.format(formatterTime)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight(500),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        // Periodicity of the task
                        if (task?.periodicity != null) {
                            Text(
                                text = "Périodicité : ${task?.periodicity}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight(500),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        // Priority of the task
                        if (task?.priority != null) {
                            Text(
                                text = "Priorité : ${task?.priority}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight(500),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                // Description of the task
                Text(
                    text = "Description :",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                OutlinedTextField(
                    value = task?.description ?: "Chargement...",
                    onValueChange = {},
                    readOnly = true,
                    minLines = 3,
                    maxLines = 10,
                    modifier = Modifier.fillMaxWidth()
                )
                // Buttons to modify or delete the task
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { navController.navigate("task_modify/${taskId}") },
                        enabled = task != null,
                        modifier = Modifier.width(120.dp)
                    ) {
                        Text("Modifier")
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                task?.let { controller.removeTask(it) }
                                navController.popBackStack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        enabled = task != null,
                        modifier = Modifier.width(120.dp)
                    ) {
                        Text("Supprimer", color = MaterialTheme.colorScheme.onError)
                    }
                }
            }
        }
    }
}