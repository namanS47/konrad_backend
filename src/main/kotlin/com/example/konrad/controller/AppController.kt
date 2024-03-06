package com.example.konrad.controller

import com.example.konrad.aws.s3.AwsS3Service
import com.example.konrad.model.DoctorDataModel
import com.example.konrad.model.DriverDataModel
import com.example.konrad.model.FileUploadModel
import com.example.konrad.model.ServiceProviderDataModel
import com.example.konrad.model.jwt_models.UserDetailsModel
import com.example.konrad.services.*
import com.example.konrad.services.jwtService.JwtUserDetailsService
import jakarta.annotation.security.RolesAllowed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.net.URL

@RestController
@RequestMapping("/")
class AppController(
        @Autowired private val distanceMatrixServices: DistanceMatrixServices,
        @Autowired private val jwtUserDetailsService: JwtUserDetailsService,
        @Autowired private val aggregatorService: AggregatorService,
        @Autowired private val doctorService: DoctorService,
        @Autowired private val driverService: DriverService,
        @Autowired private val userService: UserService,
    @Autowired private val awsService: AwsS3Service
) {
//    @GetMapping("/")
//    fun runDistanceMatrix() {
//        distanceMatrixServices.getDistance()
//    }

    @GetMapping("/")
    fun getHello(): String = "Hello Naman"

    @RolesAllowed("ADMIN")
    @PostMapping("/addUser")
    fun addAdmin(@RequestBody adminDataModel: UserDetailsModel): ResponseEntity<*> {
        return jwtUserDetailsService.addUser(adminDataModel)
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/admin/aggregator")
    fun addServiceProvider(@RequestBody serviceProviderDataModel: ServiceProviderDataModel): ResponseEntity<*> {
        return aggregatorService.addServiceProvider(serviceProviderDataModel)
    }

    @GetMapping("/doctor")
    fun fetchDoctorDetailsByToken(@RequestHeader(name="Authorization") doctorToken: String): ResponseEntity<*> {
        return doctorService.getDoctorDetails(doctorToken)
    }

    @GetMapping("/doctor/id")
    fun fetchDoctorDetailsById(@RequestHeader id: String): ResponseEntity<*> {
        return ResponseEntity.ok(doctorService.getDoctorDetailsById(id))
    }

    @PutMapping("/doctor")
    fun updateDoctor(@RequestBody doctorDataModel: DoctorDataModel): ResponseEntity<*> {
        return doctorService.updateDoctorDetails(doctorDataModel)
    }

    @GetMapping("/driver")
    fun fetchDriverDetailsByToken(@RequestHeader(name="Authorization") driverToken: String): ResponseEntity<*> {
        return driverService.getDriverDetails(driverToken)
    }

    @GetMapping("/driver/id")
    fun fetchDriverDetailsById(@RequestHeader id: String): ResponseEntity<*> {
        return ResponseEntity.ok(driverService.getDriverDetailsById(id))
    }

    @PutMapping("/driver")
    fun updateDriver(@RequestBody driverDataModel: DriverDataModel): ResponseEntity<*> {
        return driverService.updateDriverDetails(driverDataModel)
    }

    @GetMapping("/driver/bookings")
    fun fetchAllBookingsAssociatedWithDriver(
        @RequestHeader(name="Authorization") driverToken: String): ResponseEntity<*> {
        return driverService.fetchAllBookingsAssociatedWithDriver(driverToken)
    }

    @GetMapping("/user/bookings")
    fun fetchAllBookingsAssociatedWithUser(
        @RequestHeader(name="Authorization") driverToken: String): ResponseEntity<*> {
        return userService.fetchAllBookingsAssociatedWithUser(driverToken)
    }

    @PostMapping("/saveImage")
    fun saveFileToS3(@RequestPart("file") file: MultipartFile): ResponseEntity<*> {
        val response =  awsService.uploadFileToPrivateBucket(file)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/imageUrl")
    fun fetchS3ImageUrl(@RequestHeader imageUrl: String): URL {
        return awsService.generatePreSignedUrl(imageUrl)
    }

    @PostMapping("/file")
    fun uploadFile(
        @RequestPart("file") file: MultipartFile?,
        @RequestPart("fileS3Path") fileS3Path: String?,
        @RequestPart("userId") userId: String?,
        @RequestPart("patientId") patientId: String?,
        @RequestPart("bookingId") bookingId: String?,
        @RequestPart("title") title: String?,
        @RequestPart("fileType") fileType: String?,
        @RequestPart("fileFormat") fileFormat: String?
    ): ResponseEntity<*> {
        return awsService.saveFile(file, fileS3Path, userId, patientId, bookingId, title, fileType, fileFormat)
    }

    @PostMapping("/bulkFiles")
    fun uploadBulkFiles(@RequestBody fileDetailsList: List<FileUploadModel>): ResponseEntity<*> {
        return awsService.saveBulkFile(fileDetailsList)
    }

    @GetMapping("/files")
    fun fetchFiles(
        @RequestHeader("userId") userId: String?,
        @RequestHeader("patientId") patientId: String?,
        @RequestHeader("bookingId") bookingId: String?,
        @RequestHeader("title") title: String?,
        @RequestHeader("fileType") fileType: List<String>?,
        @RequestParam("page") page: Int = 1,
        @RequestParam("pageSize") pageSize: Int?
    ): ResponseEntity<*> {
        return awsService.getFileDetails(userId, patientId, bookingId, title, fileType, page, pageSize)
    }
}