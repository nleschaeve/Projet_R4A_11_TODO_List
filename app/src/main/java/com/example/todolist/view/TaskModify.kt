package com.example.todolist.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todolist.controller.TaskController
import com.example.todolist.model.entity.TaskPeriodicity
import com.example.todolist.model.entity.TaskPriority
import com.example.todolist.ui.theme.ticTaskTextFieldColors
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskModify(
    controller: TaskController,
    navController: NavController,
    taskId: Int
) {
    val scope = rememberCoroutineScope()
    val task by controller.getTaskById(taskId).collectAsState(initial = null)

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf<LocalDate?>(null) }
    var dueTime by remember { mutableStateOf<LocalTime?>(null) }
    var periodicity by remember { mutableStateOf(TaskPeriodicity.NONE) }
    var priority by remember { mutableStateOf(TaskPriority.NONE) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showPeriodicityMenu by remember { mutableStateOf(false) }
    var showPriorityMenu by remember { mutableStateOf(false) }
    var isInitialized by remember { mutableStateOf(false) }

    // Initialiser les champs quand la tâche est chargée
    LaunchedEffect(task) {
        if (task != null && !isInitialized) {
            title = task!!.title
            description = task!!.description
            dueDate = task!!.dueDate
            dueTime = task!!.dueTime
            periodicity = task!!.periodicity
            priority = task!!.priority
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                text = "Modifier Tâche",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Afficher un indicateur de chargement si la tâche n'est pas encore chargée
        if (task == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Formulaire de modification
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Titre de la tâche
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Modification de la date d'échéance
                OutlinedTextField(
                    value = dueDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
                    onValueChange = { },
                    label = { Text("Date d'échéance") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Sélectionner une date"
                            )
                        }
                    }
                )

                // Afficher le DatePicker si demandé
                if (showDatePicker) {
                    DatePickerModalInput(
                        onDateSelected = { dateMillis ->
                            if (dateMillis != null) {
                                dueDate = Instant.ofEpochMilli(dateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
                            }
                        },
                        onDismiss = { showDatePicker = false }
                    )
                }

                // Modification de l'heure d'échéance
                OutlinedTextField(
                    value = dueTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "",
                    onValueChange = { },
                    label = { Text("Heure d'échéance") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTimePicker = true },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Sélectionner une heure"
                            )
                        }
                    }
                )

                // Afficher le TimePicker si demandé
                if (showTimePicker) {
                    TimePickerExample(
                        onTimeSelected = { time ->
                            dueTime = time
                        },
                        onDismiss = { showTimePicker = false }
                    )
                }

                // Périodicité de la tâche
                ExposedDropdownMenuBox(
                    expanded = showPeriodicityMenu,
                    onExpandedChange = { showPeriodicityMenu = !showPeriodicityMenu }
                ) {
                    OutlinedTextField(
                        value = periodicity.toString(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Périodicité") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPeriodicityMenu) },
                        colors = ticTaskTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = showPeriodicityMenu,
                        onDismissRequest = { showPeriodicityMenu = false }
                    ) {
                        TaskPeriodicity.entries.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption.toString()) },
                                onClick = {
                                    periodicity = selectionOption
                                    showPeriodicityMenu = false
                                }
                            )
                        }
                    }
                }

                // Priorité de la tâche
                ExposedDropdownMenuBox(
                    expanded = showPriorityMenu,
                    onExpandedChange = { showPriorityMenu = !showPriorityMenu }
                ) {
                    OutlinedTextField(
                        value = priority.toString(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Priorité") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPriorityMenu) },
                        colors = ticTaskTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = showPriorityMenu,
                        onDismissRequest = { showPriorityMenu = false }
                    ) {
                        TaskPriority.entries.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption.toString()) },
                                onClick = {
                                    priority = selectionOption
                                    showPriorityMenu = false
                                }
                            )
                        }
                    }
                }

                // Description de la tâche
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            navController.popBackStack()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(
                            text = "Annuler",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                task?.let { currentTask ->
                                    val updatedTask = currentTask.copy(
                                        title = title,
                                        description = description,
                                        dueDate = dueDate,
                                        dueTime = dueTime,
                                        periodicity = periodicity,
                                        priority = priority,
                                        imageUri = null
                                    )
                                    controller.updateTask(updatedTask)
                                }
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Enregistrer",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}