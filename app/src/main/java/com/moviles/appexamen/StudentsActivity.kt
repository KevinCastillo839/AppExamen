package com.moviles.appexamen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.appexamen.models.Student
import com.moviles.appexamen.ui.theme.AppExamenTheme
import com.moviles.appexamen.viewmodel.CourseViewModel
import com.moviles.appexamen.viewmodel.CourseViewModelFactory
import com.moviles.appexamen.viewmodel.StudentViewModel
import com.moviles.appexamen.viewmodel.StudentViewModelFactory
import kotlinx.coroutines.launch


class StudentActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppExamenTheme {
                val context = LocalContext.current
                val courseId = intent.getIntExtra("COURSE_ID", -1)
                // ViewModel
                val studentViewModel: StudentViewModel = viewModel(
                    factory = StudentViewModelFactory(context)
                )

                val students by studentViewModel.students.collectAsState()
                val loadingState by studentViewModel.loadingState.collectAsState()

                var showForm by remember { mutableStateOf(false) }
                var selectedStudent by remember { mutableStateOf<Student?>(null) }

                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()

                // Snackbar cuando cambia estado
                LaunchedEffect(loadingState) {
                    if (loadingState.isNotEmpty()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(loadingState)
                        }
                    }
                }

                // Cargar estudiantes
                LaunchedEffect(Unit) {
                    studentViewModel.fetchStudents(courseId)
                    Log.d("StudentActivity", "Estudiantes cargados: ${students.size}") // Verifica si la lista tiene datos
                }


                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Gestión de estudiantes", color = Color.White) },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00695C))
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                selectedStudent = null
                                showForm = true
                            },
                            containerColor = Color(0xFF00695C)
                        ) {
                            Text("+", color = Color.White)
                        }
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    containerColor = Color(0xFFF5F5F5)
                ) { padding ->
                    if (showForm) {
                        StudentForm(
                            student = selectedStudent,
                            onSave = { student ->
                                if (student.id != null) {
                                    studentViewModel.updateStudent(student)
                                } else {
                                    studentViewModel.addStudent(student)
                                }
                                showForm = false
                            },

                            onCancel = {
                                showForm = false
                            },
                            modifier = Modifier.padding(padding).padding(16.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .padding(padding)
                                .padding(16.dp)
                        ) {
                            items(students) { student ->
                                StudentItem(
                                    student = student,
                                    onEdit = {
                                        selectedStudent = student
                                        showForm = true
                                    },
                                    onDelete = {
                                        studentViewModel.deleteStudent(student.id)
                                    },
                                    onViewDetails = {
                                        val intent = Intent(context, StudentDetailActivity::class.java)
                                        intent.putExtra("STUDENT_ID", student.id)
                                        context.startActivity(intent)
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
@Composable
fun StudentItem(
    student: Student,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewDetails: () -> Unit // Nueva función para ver detalles
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nombre: ${student.name}", style = MaterialTheme.typography.titleMedium)
            Text("Correo: ${student.email}")
            Text("Teléfono: ${student.phone}")

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Row {
                    TextButton(onClick = onEdit) {
                        Text("Editar")
                    }
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Eliminar")
                    }
                }
                Button(
                    onClick = onViewDetails,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                ) {
                    Text("Ver Detalles", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun StudentForm(
    student: Student?,
    onSave: (Student) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(student?.name ?: "") }
    var email by remember { mutableStateOf(student?.email ?: "") }
    var phone by remember { mutableStateOf(student?.phone ?: "") }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cancelar")
            }

            Button(onClick = {
                onSave(
                    Student(
                        id = student?.id,
                        name = name,
                        email = email,
                        phone = phone,
                        courseId = student?.courseId ?: 12 // Puedes ajustar esto si usas otro courseId
                    )
                )
            }) {
                Text("Guardar")
            }
        }
    }
}