package com.moviles.appexamen

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.appexamen.models.Student

class StudentsActivity : ComponentActivity() {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val studentViewModel: StudentViewModel = viewModel()
            val courseId = 1 // Suponiendo que estás visualizando estudiantes para el curso con id 1
            val students by studentViewModel.getStudentsByCourse(courseId).collectAsState(initial = emptyList())

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Students by Course") },
                        backgroundColor = Color(0xFF6200EE)
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { /* Navegar a la pantalla para crear estudiante */ },
                        containerColor = Color(0xFF6200EE)
                    ) {
                        Text("+", color = Color.White)
                    }
                }
            ) { paddingValues ->
                LazyColumn(modifier = Modifier.padding(paddingValues)) {
                    items(students) { student ->
                        StudentCard(student = student)
                    }
                }
            }
        }
    }
}

@Composable
fun StudentCard(student: Student) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Name: ${student.name}")
            Text("Email: ${student.email}")
            Text("Phone: ${student.phone}")
        }
    }
}
