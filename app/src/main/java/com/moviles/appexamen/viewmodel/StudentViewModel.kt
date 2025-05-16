package com.moviles.appexamen.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.http.HttpException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moviles.appexamen.models.Student
import com.moviles.appexamen.network.RetrofitInstance
import com.moviles.appexamen.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StudentViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentViewModel::class.java)) {
            return StudentViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class StudentViewModel(private val context: Context) : ViewModel() {

    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> get() = _students

    private val _loadingState = MutableStateFlow<String>("")
    val loadingState: StateFlow<String> get() = _loadingState

    private val repository = StudentRepository(context)

    // Load students from the local database
    fun loadStudents(courseId: Int) {
        viewModelScope.launch {
            _students.value = repository.getStudentsByCourse(courseId)
        }
    }


    //Synchronize students from the API and the local database
    fun fetchStudents(courseId: Int) {
        viewModelScope.launch {
            _loadingState.value = "Verificando conexión..."

            try {
                val hasInternet = hasInternetConnection(context)

                if (hasInternet) {
                    _loadingState.value = "Cargando estudiantes desde la API..."
                    val apiStudents = RetrofitInstance.api.getStudentsByCourse(courseId)
                    repository.clearStudents()

                    repository.insertStudents(apiStudents, courseId)

                    Log.i("StudentViewModel", "Datos de estudiantes sincronizados con API")
                    _loadingState.value = "Estudiantes cargados desde la API"
                } else {
                    _loadingState.value = "No hay conexión a Internet. Cargando desde la caché..."
                    Log.i("StudentViewModel", "Sin conexión a Internet, cargando desde la caché...")
                }

                val localStudents = repository.getStudentsByCourse(courseId)
                _students.value = localStudents

            } catch (e: Exception) {
                Log.e("StudentViewModelError", "Error al cargar estudiantes: ${e.message}", e)
                _loadingState.value = "Error al cargar los estudiantes"

                val localStudents = repository.getStudentsByCourse(courseId)
                _students.value = localStudents
            }
        }
    }

    //Check if there is an Internet connection
    private fun hasInternetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

//    Method to add
fun addStudent(student: Student) {
    viewModelScope.launch {
        try {
            val response = RetrofitInstance.api.addStudent(student.courseId!!, student)
            if (response.isSuccessful) {
                val createdStudent = response.body()
                if (createdStudent != null) {
                    _students.value += createdStudent
                    Log.i("ViewModelInfo", "Estudiante agregado: $createdStudent")
                } else {
                    Log.e("ViewModelError", "Respuesta vacía al agregar estudiante")
                }
            } else {
                Log.e("ViewModelError", "Error al agregar estudiante: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("ViewModelError", "Error inesperado al agregar estudiante", e)
        }
    }
}

// Method to edit
fun updateStudent(student: Student) {
    viewModelScope.launch {
        try {
            val response = RetrofitInstance.api.updateStudent(
                courseId = student.courseId!!,
                id = student.id!!,
                student = student
            )

            if (response.isSuccessful) {
                val updatedStudent = response.body()
                if (updatedStudent != null) {
                    _students.value = _students.value.map {
                        if (it.id == updatedStudent.id) updatedStudent else it
                    }
                    Log.i("ViewModelInfo", "Estudiante actualizado: $updatedStudent")
                } else {
                    Log.e("ViewModelError", "Respuesta vacía al actualizar estudiante")
                }
            } else {
                Log.e("ViewModelError", "Error al actualizar estudiante: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("ViewModelError", "Error inesperado al actualizar estudiante", e)
        }
    }
}


    private val _student = MutableStateFlow<Student?>(null)
    val student: StateFlow<Student?> = _student

    fun getStudentById(id: Int) {
        viewModelScope.launch {
            val studentData = repository.getStudentById(id)
            _student.value = studentData
        }
    }




    // Delete a studen
    fun deleteStudent(studentId: Int?) {
        studentId?.let { id ->
            viewModelScope.launch {
                try {
                    RetrofitInstance.api.deleteStudent(id)
                    _students.value = _students.value.filter { it.id != id }
                    Log.i("StudentViewModel", "Student deleted with id: $id")
                } catch (e: Exception) {
                    Log.e("StudentViewModelError", "Error deleting student: ${e.message}")
                }
            }
        } ?: Log.e("StudentViewModelError", "Error: studentId is null")
    }
}
