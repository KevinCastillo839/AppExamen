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
import com.moviles.appexamen.models.Course
import com.moviles.appexamen.network.RetrofitInstance
import com.moviles.appexamen.repository.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

import java.io.File

class CourseViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CourseViewModel::class.java)) {
            return CourseViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
class CourseViewModel(private val context: Context) : ViewModel() {

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> get() = _courses


    private val _loadingState = MutableStateFlow<String>("")
    val loadingState: StateFlow<String> get() = _loadingState
    private val repository = CourseRepository(context)
    private val _dataSource = MutableStateFlow<String>("")
    val dataSource: StateFlow<String> get() = _dataSource
    // Agregar propiedad para el estado de carga


    var eventsRomm: List<Course> = listOf()

    fun loadCourses() {
        viewModelScope.launch {
            _courses.value = repository.getCourses()
        }
    }


    fun saveCourses(eventsList: List<Course>) {
        viewModelScope.launch {
            repository.insertCourse(eventsList)
        }
    }
    fun fetchCourses() {
        viewModelScope.launch {
            _loadingState.value = "Verificando conexión..."

            try {
                val hasInternet = hasInternetConnection(context)

                if (hasInternet) {
                    _loadingState.value = "Cargando cursos desde la API..."
                    val apiCourses = RetrofitInstance.api.getCourses()
                    repository.clearCourses()
                    repository.insertCourse(apiCourses)
                    Log.i("CourseViewModel", "Datos de cursos sincronizados con API")
                    _loadingState.value = "Cursos cargados desde la API"
                } else {
                    _loadingState.value = "No hay conexión a Internet. Cargando desde la caché..."
                    Log.i("CourseViewModel", "Sin conexión a Internet, cargando desde la caché...")
                }

                val localCourses = repository.getCourses()
                _courses.value = localCourses

            } catch (e: Exception) {
                Log.e("CourseViewModelError", "Error al cargar cursos: ${e.message}", e)
                _loadingState.value = "Error al cargar los cursos"

                val localCourses = repository.getCourses()
                _courses.value = localCourses
            }
        }
    }

   // Agregar esta función
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

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun addCourse(course: Course, file: File?) {
        viewModelScope.launch {
            try {
                // Preparamos las partes para la petición multipart
                val namePart = course.name.toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionPart = course.description.toRequestBody("text/plain".toMediaTypeOrNull())
                val schedulePart = course.schedule.toRequestBody("text/plain".toMediaTypeOrNull())
                val professorPart = course.professor.toRequestBody("text/plain".toMediaTypeOrNull())

                val filePart = file?.let {
                    val requestFile = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("file", it.name, requestFile)
                }

                // Hacemos la llamada al servicio API
                val response = RetrofitInstance.api.addCourse(
                    name = namePart,
                    description = descriptionPart,
                    schedule = schedulePart,
                    date = professorPart,
                    file = filePart
                )

                // Si la respuesta es exitosa, actualizamos la lista de cursos
                _courses.value = _courses.value.orEmpty() + response
                Log.i("CourseViewModel", "Response: $response")
            } catch (e: HttpException) {

                Log.e("CourseViewModelError", "HTTP Error: ${e.message}")
            } catch (e: Exception) {
                Log.e("CourseViewModelError", "Error: ${e.message}", e)
            }
        }
    }



    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun updateCourse(course: Course, file: File?) {
        viewModelScope.launch {
            try {
                Log.i("CourseViewModel", "Updating course: $course")

                // Convertir los parámetros a RequestBody
                val namePart = course.name.toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionPart = course.description.toRequestBody("text/plain".toMediaTypeOrNull())
                val schedulePart = course.schedule.toRequestBody("text/plain".toMediaTypeOrNull())
                val professorPart = course.professor.toRequestBody("text/plain".toMediaTypeOrNull())

                // Convertir el archivo a MultipartBody.Part si existe
                val filePart = file?.let {
                    val fileRequestBody = it.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("file", it.name, fileRequestBody)
                }

                // Asegurarse de que el id no sea nulo
                val courseId = course.id ?: throw IllegalArgumentException("Course id cannot be null")

                // Realizar la solicitud de actualización
                val response = RetrofitInstance.api.updateCourse(
                    id = courseId, // Usamos un valor no nulo para el id
                    name = namePart,
                    description = descriptionPart,
                    schedule = schedulePart,
                    professor = professorPart,
                    file = filePart
                )

                // Actualizar la lista de cursos con la respuesta obtenida
                _courses.value = _courses.value.map {
                    if (it.id == response.id) response else it
                }

                Log.i("CourseViewModel", "Course updated: $response")
            } catch (e: HttpException) {
                Log.e("CourseViewModelError", "HTTP Error: ${e.message}")
            } catch (e: IllegalArgumentException) {
                Log.e("CourseViewModelError", "Invalid argument: ${e.message}")
            } catch (e: Exception) {
                Log.e("CourseViewModelError", "Error updating course: ${e.message}", e)
            }
        }
    }




    fun deleteCourse(courseId: Int?) {
        courseId?.let { id ->
            viewModelScope.launch {
                try {
                    RetrofitInstance.api.deleteCourse(id)
                    _courses.value = _courses.value.filter { it.id != id }
                    Log.i("CourseViewModel", "Course deleted with id: $id")
                } catch (e: Exception) {
                    Log.e("CourseViewModelError", "Error deleting course: ${e.message}")
                }
            }
        } ?: Log.e("CourseViewModelError", "Error: courseId is null")
    }
}
