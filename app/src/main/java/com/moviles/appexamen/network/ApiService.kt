package com.moviles.appexamen.network

import com.moviles.appexamen.models.Course
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @GET("api/course")
    suspend fun getCourses(): List<Course>


    @Multipart
    @POST("api/course")
    suspend fun addCourse(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,

        @Part("schedule") schedule: RequestBody,
        @Part("professor") date: RequestBody,
        @Part file: MultipartBody.Part? // Esto es para el archivo
    ): Course

    @PUT("api/course/{id}")
    suspend fun updateCourse(@Path("id") id: Int?, @Body courseDto: Course): Course

    @DELETE("api/course/{id}")
    suspend fun deleteCourse(@Path("id") id: Int?): Response<Unit>
}