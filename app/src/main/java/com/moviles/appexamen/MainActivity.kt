package com.moviles.appexamen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.moviles.appexamen.common.Constants.IMAGES_BASE_URL
import com.moviles.appexamen.models.Course
import com.moviles.appexamen.ui.theme.AppExamenTheme
import com.moviles.appexamen.viewmodel.CourseViewModel
import java.io.File
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.moviles.appexamen.viewmodel.CourseViewModelFactory
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.StateFlow


import java.io.FileOutputStream
import java.io.IOException

class MainActivity : ComponentActivity() {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppExamenTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF5F5F5) // Gris claro
                ) {
                    val context = LocalContext.current

                    // ViewModel
                    val courseViewModel: CourseViewModel = viewModel(
                        factory = CourseViewModelFactory(context)
                    )


                    // States
                    val courses by courseViewModel.courses.collectAsState()
                    val loadingState by courseViewModel.loadingState.collectAsState()

                    var showForm by remember { mutableStateOf(false) }
                    var selectedCourse by remember { mutableStateOf<Course?>(null) }

                    // Snackbar host state
                    val snackbarHostState = remember { SnackbarHostState() }
                    val coroutineScope = rememberCoroutineScope()

                    // Map of states to messages
                    val loadingMessages = mapOf(
                        "Cargando cursos desde la API..." to "Sincronizando datos desde la API...",
                        "Cursos cargados desde la API" to "Datos cargados desde la API.",
                        "No hay conexión a Internet. Cargando desde la caché..." to "Sin conexión, mostrando datos locales.",
                        "Error al cargar los cursos" to "Error al cargar los cursos. Reintentando..."
                    )



                    // Show Snackbar when loadingState changes
                    LaunchedEffect(loadingState) {
                        if (loadingState.isNotEmpty() && loadingState != "Verificando conexión...") {
                            coroutineScope.launch {
                                val message = loadingMessages[loadingState] ?: "Estado desconocido."
                                snackbarHostState.showSnackbar(
                                    message = message,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }

                    // Load courses
                    LaunchedEffect(Unit) {
                        courseViewModel.fetchCourses()
                    }

                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("Gestión de cursos", color = Color.White) },
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EE))
                            )
                        },
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = {
                                    selectedCourse = null
                                    showForm = true
                                },
                                containerColor = Color(0xFF6200EE)
                            ) {
                                Text("+", color = Color.White)
                            }
                        },
                        snackbarHost = { SnackbarHost(snackbarHostState) }, // This is where the SnackbarHost connects.
                        containerColor = Color(0xFFF5F5F5)
                    ) { paddingValues ->
                        if (showForm) {
                            Column(
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .padding(16.dp)
                            ) {
                                CourseForm(
                                    course = selectedCourse,
                                    onSave = { course, file ->
                                        // If the course has an ID (it is an existing course), then update it.
                                        if (course.id != null) {
                                            // In this case, we select the course and pass its ID.
                                            courseViewModel.updateCourse(course, file) //Pass the course and the file.
                                        } else {
                                            // If it doesn't have an ID (it's a new course), then add it.
                                            courseViewModel.addCourse(course, file)
                                        }
                                        showForm = false
                                    },
                                    onCancel = {
                                        showForm = false
                                    }
                                )


                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .padding(16.dp)
                            ) {
                                items(courses) { course ->
                                    CourseItem(
                                        course = course,
                                        onEdit = {
                                            selectedCourse = course
                                            showForm = true
                                        },
                                        onDelete = {
                                            courseViewModel.deleteCourse(course.id)
                                        },
                                        onViewStudents = {
                                            course.id?.let { id ->
                                                val intent = Intent(context, StudentActivity::class.java).apply {
                                                    putExtra("COURSE_ID", id) //Pass the course ID to the new activity.
                                                }
                                                context.startActivity(intent) //Start the new activity.
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
    }
}

@Composable
fun CourseForm(
    course: Course?,
    onSave: (Course, File?) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(TextFieldValue(course?.name ?: "")) }
    var description by remember { mutableStateOf(TextFieldValue(course?.description ?: "")) }
    var schedule by remember { mutableStateOf(TextFieldValue(course?.schedule ?: "")) }
    var professor by remember { mutableStateOf(TextFieldValue(course?.professor ?: "")) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }


    val context = LocalContext.current

    // Open the image picker.
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = Color(0xFF6200EE),
                unfocusedLabelColor = Color(0xFF666666)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = Color(0xFF6200EE),
                unfocusedLabelColor = Color(0xFF666666)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = schedule,
            onValueChange = { schedule = it },
            label = { Text("Horario") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = Color(0xFF6200EE),
                unfocusedLabelColor = Color(0xFF666666)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = professor,
            onValueChange = { professor = it },
            label = { Text("Profesor") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = Color(0xFF6200EE),
                unfocusedLabelColor = Color(0xFF666666)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Button to select image
        Button(
            onClick = {
                launcher.launch("image/*") //Select images only
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar imagen", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Show the selected image
        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    // Convert imageUri to a File if necessary
                    val file = imageUri?.let { uri ->
                        getFileFromUri(context, uri)
                    }
                    val newCourse = Course(
                        id = course?.id,
                        name = name.text,
                        description = description.text,
                        schedule = schedule.text,
                        professor = professor.text,
                        imageUrl = imageUri?.toString() ?: course?.imageUrl
                    )
                    onSave(newCourse, file)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text("Guardar", color = Color.White)
            }
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC6))
            ) {
                Text("Cancelar", color = Color.Black)
            }
        }
    }
}


@Composable
fun CourseItem(
    course: Course,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewStudents: () -> Unit// New parameter to handle the action of viewing students

) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if (!course.imageUrl.isNullOrEmpty()) {
                RemoteImage(IMAGES_BASE_URL + course.imageUrl)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(text = course.name ?: "", color = Color(0xFF000000), style = MaterialTheme.typography.titleMedium)
            Text(text = course.description ?: "", color = Color(0xFF666666))
            Text(text = "Horario: ${course.schedule}", color = Color(0xFF666666))
            Text(text = "Profesor: ${course.professor}", color = Color(0xFF666666))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC6))
                ) {
                    Text("Editar", color = Color.Black)
                }
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC6))
                ) {
                    Text("Eliminar", color = Color.Black)
                }
                Button(
                    onClick = onViewStudents,// Calls the function that sends the course ID
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                ) {
                    Text("Ver Estudiantes", color = Color.White)
                }
            }
        }
    }
}


@Composable
fun RemoteImage(imageUrl: String) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        contentScale = ContentScale.Fit
    )

}
fun getFileFromUri(context: Context, uri: Uri): File? {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("event_image_", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)

        inputStream?.copyTo(outputStream)

        return tempFile
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
}