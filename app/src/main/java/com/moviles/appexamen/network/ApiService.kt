package com.moviles.appexamen.network

import com.moviles.appexamen.models.Course
import com.moviles.appexamen.models.Student
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
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
        @Part file: MultipartBody.Part? // This is for the file
    ): Course

    @Multipart
    @PUT("api/course/{id}")
    suspend fun updateCourse(
        @Path("id") id: Int,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("schedule") schedule: RequestBody,
        @Part("professor") professor: RequestBody,
        @Part file: MultipartBody.Part? // Add the file as Multipart
    ): Course


    @DELETE("api/course/{id}")
    suspend fun deleteCourse(@Path("id") id: Int?): Response<Unit>

    // For students //
    @GET("api/course/{courseId}/students")
    suspend fun getStudentsByCourse(@Path("courseId") courseId: Int): List<Student>

    @POST("api/course/{courseId}/students")
    suspend fun addStudent(
        @Path("courseId") courseId: Int,
        @Body student: Student
    ):  Response<Student>

    @DELETE("api/student/{id}")
    suspend fun deleteStudent(@Path("id") id: Int?): Response<Unit>

    @POST("students")
    suspend fun createStudent(@Body student: Student): Student

    @PUT("api/course/{courseId}/students/{id}")
    suspend fun updateStudent(
        @Path("courseId") courseId: Int,
        @Path("id") id: Int,
        @Body student: Student
    ): Response<Student>

    // TO VIEW THE DETAILS
    @GET("api/student/{id}")
    suspend fun getStudentById(@Path("id") id: Int): Student

    interface ApiService {
        @GET("students/{id}")
        suspend fun getStudentById(@Path("id") id: Int): Response<Student>
    }


}