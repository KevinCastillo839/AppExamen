package com.moviles.appexamen

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.moviles.appexamen.models.Student

class StudentDetailActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener el ID enviado desde StudentActivity
        val studentId = intent.getIntExtra("STUDENT_ID", -1)

        setContent {
            var student by remember { mutableStateOf<Student?>(null) }

            // Simular carga de datos
            LaunchedEffect(Unit) {
                // Simular búsqueda en la base de datos (quemado)
                student = Student(
                    id = studentId,
                    name = "Estudiante Ejemplo",
                    email = "ejemplo@student.com",
                    phone = "8888-8888",
                    courseId = 123
                )
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Perfil del Estudiante", color = Color.White) },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EE))
                    )
                }
            ) { paddingValues ->
                student?.let {
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Nombre: ${it.name}", style = MaterialTheme.typography.titleMedium)
                        Text("Correo: ${it.email}")
                        Text("Teléfono: ${it.phone}")
                        Text("ID del Curso: ${it.courseId}")
                        Text("ID del Estudiante: ${it.id}")
                    }
                } ?: run {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Cargando información del estudiante...")
                    }
                }
            }
        }
    }
}

