package com.moviles.appexamen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.appexamen.viewmodel.StudentViewModel
import com.moviles.appexamen.viewmodel.StudentViewModelFactory
import androidx.compose.material.icons.filled.MenuBook



class StudentDetailActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val studentId = intent.getIntExtra("STUDENT_ID", -1)

        setContent {
            val context = LocalContext.current
            val studentViewModel: StudentViewModel = viewModel(factory = StudentViewModelFactory(context))
            val student by studentViewModel.student.collectAsState()

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
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (student != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                InfoRow(icon = Icons.Default.Person, label = "Nombre", value = student!!.name)
                                InfoRow(icon = Icons.Default.Email, label = "Correo", value = student!!.email)
                                InfoRow(icon = Icons.Default.Phone, label = "Teléfono", value = student!!.phone)
                                InfoRow(icon = Icons.Default.MenuBook, label = "ID del Curso", value = student!!.courseId.toString())

                                InfoRow(icon = Icons.Default.Badge, label = "ID del Estudiante", value = student!!.id.toString())
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = Color(0xFF6200EE))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Cargando información del estudiante...", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun InfoRow(icon: ImageVector, label: String, value: String) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = Color(0xFF6200EE))
            Column {
                Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Text(text = value, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
