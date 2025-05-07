package com.moviles.appexamen

import android.content.Context
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
                    val courseViewModel: CourseViewModel = viewModel()
                    val courses by courseViewModel.courses.collectAsState()
                    var showForm by remember { mutableStateOf(false) }
                    var selectedCourse by remember { mutableStateOf<Course?>(null) }

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
                                    if (course.id == null) {
                                        courseViewModel.addCourse(course, file)
                                    } else {
                                        courseViewModel.updateCourse(course)
                                    }
                                    showForm = false
                                },
                                onCancel = {
                                    showForm = false
                                }
                            )}
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

    // Abre el selector de imágenes
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

        // Botón para seleccionar imagen
        Button(
            onClick = {
                launcher.launch("image/*") // Selecciona solo imágenes
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar imagen", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mostrar la imagen seleccionada
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
                    // Convertir imageUri a un File si es necesario
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
    onDelete: () -> Unit
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
            }
        }
    }
}

//@Composable
//fun EventCard(event: Event, modifier: Modifier = Modifier) {
//    Card(
//        modifier = modifier.fillMaxWidth().padding(16.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            RemoteImage(IMAGES_BASE_URL + event.image)
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(event.name, style = MaterialTheme.typography.titleLarge)
//            Text(text = "📅 ${event.date}", style = MaterialTheme.typography.bodySmall)
//        }
//    }
//}

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