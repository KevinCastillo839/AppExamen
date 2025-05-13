/*package com.moviles.appexamen.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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

    // Cargar estudiantes desde la base de datos local
    fun loadStudents(courseId: Int) {
        viewModelScope.launch {
            _students.value = repository.getStudentsByCourse(courseId)
        }
    }

    // Guardar estudiantes en la base de datos local
    fun saveStudents(studentsList: List<Student>) {
        viewModelScope.launch {
            repository.insertStudents(studentsList)
        }
    }

    // Sincronizar estudiantes desde la API y la base de datos local
    fun fetchStudents(courseId: Int) {
        viewModelScope.launch {
            _loadingState.value = "Verificando conexión..."

            try {
                val hasInternet = hasInternetConnection(context)

                if (hasInternet) {
                    _loadingState.value = "Cargando estudiantes desde la API..."
                    val apiStudents = RetrofitInstance.api.getStudentsByCourse(courseId)
                    repository.clearStudents()
                    repository.insertStudents(apiStudents)
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

    // Verificar si hay conexión a Internet
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

    // Agregar un estudiante con imagen (si es necesario)
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun addStudent(student: Student, file: File?) {
        viewModelScope.launch {
            try {
                // Preparamos las partes para la petición multipart
                val namePart = student.name.toRequestBody("text/plain".toMediaTypeOrNull())
                val emailPart = student.email.toRequestBody("text/plain".toMediaTypeOrNull())
                val phonePart = student.phone.toRequestBody("text/plain".toMediaTypeOrNull())
                val courseIdPart = student.courseId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val filePart = file?.let {
                    val requestFile = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("file", it.name, requestFile)
                }

                // Hacemos la llamada al servicio API
                val response = RetrofitInstance.api.addStudent(
                    name = namePart,
                    email = emailPart,
                    phone = phonePart,
                    courseId = courseIdPart,
                    file = filePart
                )

                // Si la respuesta es exitosa, actualizamos la lista de estudiantes
                _students.value = _students.value.orEmpty() + response
                Log.i("StudentViewModel", "Response: $response")
            } catch (e: Exception) {
                Log.e("StudentViewModelError", "Error: ${e.message}", e)
            }
        }
    }

    // Eliminar un estudiante
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
*/