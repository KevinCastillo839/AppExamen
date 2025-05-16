package com.moviles.appexamen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.moviles.appexamen.models.Student
import com.moviles.appexamen.viewmodel.StudentViewModel

class StudentDetailActivity : ComponentActivity() {

    private val studentViewModel: StudentViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener el ID del estudiante enviado desde StudentActivity
        val studentId = intent.getIntExtra("STUDENT_ID", -1)

        setContent {
            // Observamos el estado del estudiante en el ViewModel
            val student by studentViewModel.student.collectAsState()

            // Lanzamos la carga del estudiante cuando la pantalla se crea
            LaunchedEffect(studentId) {
                studentViewModel.getStudentById(studentId)
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Perfil del Estudiante", color = Color.White) },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EE))
                    )
                }
            ) { paddingValues ->
                if (student != null) {
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Nombre: ${student!!.name}", style = MaterialTheme.typography.titleMedium)
                        Text("Correo: ${student!!.email}")
                        Text("Teléfono: ${student!!.phone}")
                        Text("ID del Curso: ${student!!.courseId}")
                        Text("ID del Estudiante: ${student!!.id}")
                    }
                } else {
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
