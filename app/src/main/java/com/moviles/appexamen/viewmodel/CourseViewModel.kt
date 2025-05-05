package com.moviles.appexamen.viewmodel

import android.net.http.HttpException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.appexamen.models.Course
import com.moviles.appexamen.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

import java.io.File

class CourseViewModel : ViewModel() {

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> get() = _courses

    fun fetchCourses() {
        viewModelScope.launch {
            try {
                _courses.value = RetrofitInstance.api.getCourses()
                Log.i("CourseViewModel", "Fetching courses from API... ${_courses.value}")
            } catch (e: Exception) {
                Log.e("CourseViewModelError", "Error fetching courses: ${e}")
            }
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
    fun updateCourse(course: Course) {
        viewModelScope.launch {
            try {
                Log.i("CourseViewModel", "Updating course: $course")
                val response = RetrofitInstance.api.updateCourse(course.id, course)
                _courses.value = _courses.value.map {
                    if (it.id == response.id) response else it
                }
                Log.i("CourseViewModel", "Course updated: $response")
            } catch (e: HttpException) {

                Log.e("CourseViewModelError", "HTTP Error: ${e.message}")
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
