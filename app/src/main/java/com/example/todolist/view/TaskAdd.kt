package com.example.todolist.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.todolist.R
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.example.todolist.controller.TaskController
import com.example.todolist.model.entity.Task
import com.example.todolist.model.entity.TaskState
import com.example.todolist.ui.theme.ticTaskTextFieldColors
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.LocalTime
import androidx.compose.ui.window.Dialog

@Composable
fun TaskAdd(
    controller: TaskController,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    var titleError by remember { mutableStateOf(false) }

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
                text = "Ajouter Tâche",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Formulaire d'ajout de tâche
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
                placeholder = { Text("Titre") },
                colors = ticTaskTextFieldColors(),
                supportingText = { if (titleError) Text(stringResource(R.string.title_error)) },
                isError = titleError,
                modifier = Modifier.fillMaxWidth()
            )
            // Date d'échéance (optionnel)
            OutlinedTextField(
                value = selectedDate?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                } ?: "",
                onValueChange = { },
                placeholder = { Text("Date d'échéance") },
                colors = ticTaskTextFieldColors(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Sélectionner une date"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            )

            // Afficher le DatePicker si demandé
            if (showDatePicker) {
                DatePickerModalInput(
                    onDateSelected = { dateMillis ->
                        selectedDate = dateMillis
                    },
                    onDismiss = { showDatePicker = false }
                )
            }
            // Heure d'échéance (optionnel)
            OutlinedTextField(
                value = selectedTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "",
                onValueChange = { },
                placeholder = {Text("Heure d'échéance")},
                colors = ticTaskTextFieldColors(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = {showTimePicker = true}) {
                        Icon(
                            imageVector = Icons.Filled.AccessTime,
                            contentDescription = "Sélectionner une heure"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTimePicker = true }
            )
            // Afficher le TimePicker si demandé
            if (showTimePicker) {
                TimePickerExample(
                    onTimeSelected = { time ->
                        selectedTime = time
                    },
                    onDismiss = { showTimePicker = false }
                )
            }

            // Description de la tâche (optionnel)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Description") },
                colors = ticTaskTextFieldColors(),
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    titleError = false
                    if (title.isEmpty()) {
                        titleError = true
                    } else {
                        scope.launch {

                            val task = Task(
                                title = title,
                                description = description,
                                dueDate = selectedDate?.let {
                                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                                },
                                dueTime = selectedTime,
                                state = TaskState.TODO,
                                imageUri = null
                            )
                            controller.addTask(task)
                            navController.popBackStack() // Retour à la liste après ajout
                        }
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Ajouter")
            }
        }
    }
}

