package com.moviles.appexamen

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.appexamen.models.Student
import com.moviles.appexamen.viewmodel.StudentViewModel

class StudentDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val studentViewModel: StudentViewModel = viewModel()
            val studentId = 1 // Suponiendo que estamos buscando el estudiante con ID 1
            val student by studentViewModel.getStudentById(studentId).collectAsState(initial = null)

            if (student == null) {
                Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
            }

            Scaffold(
                topBar = {
                    TopAppBar(title = { Text("Student Profile") }, backgroundColor = Color(0xFF6200EE))
                }
            ) { paddingValues ->
                student?.let {
                    Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
                        Text("Name: ${it.name}")
                        Text("Email: ${it.email}")
                        Text("Phone: ${it.phone}")
                        Text("Course ID: ${it.courseId}")
                    }
                }
            }
        }
    }
}
