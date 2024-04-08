package com.example.konrad.controller

import com.example.konrad.aws.s3.AwsS3Service
import com.example.konrad.model.*
import com.example.konrad.model.jwt_models.FcmTokenDetailsModel
import com.example.konrad.model.jwt_models.UserDetailsModel
import com.example.konrad.services.*
import com.example.konrad.services.jwtService.JwtUserDetailsService
import com.example.konrad.services.stripe.StripePaymentService
import com.example.konrad.services.teleconsultation.TeleconsultationService
import jakarta.annotation.security.RolesAllowed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.net.URL

@RestController
@RequestMapping("/")
class AppController(
    @Autowired private val jwtUserDetailsService: JwtUserDetailsService,
    @Autowired private val aggregatorService: AggregatorService,
    @Autowired private val doctorService: DoctorService,
    @Autowired private val driverService: DriverService,
    @Autowired private val bookingService: BookingService,
    @Autowired private val awsService: AwsS3Service,
    @Autowired private val ratingService: RatingService,
    @Autowired private val notificationService: NotificationService,
    @Autowired private val stripePaymentService: StripePaymentService,
    @Autowired private val mapsService: MapsService,
    @Autowired private val teleconsultationService: TeleconsultationService
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
    fun fetchDoctorDetailsByToken(@RequestHeader(name = "Authorization") doctorToken: String): ResponseEntity<*> {
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
    fun fetchDriverDetailsByToken(@RequestHeader(name = "Authorization") driverToken: String): ResponseEntity<*> {
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
        @RequestHeader(name = "Authorization") driverToken: String,
        @RequestParam("filter") bookingFilter: String?,
        @RequestParam("page") page: Int = 1,
        @RequestParam("pageSize") pageSize: Int?,
        @RequestParam("modelList") modelList: List<String>?
    ): ResponseEntity<*> {
        return driverService.fetchAllBookingsAssociatedWithDriver(driverToken, modelList, bookingFilter, page, pageSize)
    }

    @GetMapping("/user/bookings")
    fun fetchAllBookingsAssociatedWithUser(
        @RequestHeader(name = "Authorization") userToken: String,
        @RequestParam("filter") bookingFilter: String?,
        @RequestParam("page") page: Int = 1,
        @RequestParam("pageSize") pageSize: Int?,
        @RequestParam("modelList") modelList: List<String>?
    ): ResponseEntity<*> {
        return bookingService.fetchAllBookingsAssociatedWithUser(userToken, modelList, bookingFilter, page, pageSize)
    }

    @PostMapping("/saveImage")
    fun saveFileToS3(@RequestPart("file") file: MultipartFile): ResponseEntity<*> {
        val response = awsService.uploadFileToPrivateBucket(file)
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

    @PostMapping("/rating")
    fun addRating(@RequestBody ratingModel: UserRatingModel): ResponseEntity<*> {
        return ratingService.addRating(ratingModel)
    }

    @GetMapping("/rating")
    fun getRatings(
        @RequestHeader("bookingId") bookingId: String?,
        @RequestHeader("userId") userId: String?,
        @RequestParam("page") page: Int = 1,
        @RequestParam("pageSize") pageSize: Int?,
        @RequestParam("modelList") modelList: List<String>?
    ): ResponseEntity<*> {
        return ratingService.getAllRatings(bookingId, userId, modelList, page, pageSize)
    }

    @PostMapping("/fcmToken")
    fun saveFcmToken(
        @RequestHeader(name = "Authorization") token: String,
        @RequestBody fcmTokenDetailsModel: FcmTokenDetailsModel
    ): ResponseEntity<*> {
        return notificationService.saveFcmToken(token, fcmTokenDetailsModel.fcmToken!!)
    }

    @DeleteMapping("/fcmToken")
    fun deleteFcmToken(@RequestHeader userId: String, @RequestHeader token: String): ResponseEntity<*> {
        return notificationService.deleteFcmToken(userId, token)
    }

    @GetMapping("/notifications")
    fun getAllNotificationsByUserId(
        @RequestHeader("userId") userId: String,
        @RequestParam("page") page: Int?,
        @RequestParam("pageSize") pageSize: Int?
    ): ResponseEntity<*> {
        return notificationService.getAllNotificationByUserId(userId, page, pageSize)
    }

    @PostMapping("/notification/test")
    fun testNotification(@RequestBody notificationDetailsModel: NotificationDetailsModel) {
        notificationService.sendNotification(notificationDetailsModel)
    }

    @PostMapping("/order/create")
    fun createPaymentIntent(@RequestBody paymentIntent: PaymentOrderModel): ResponseEntity<*> {
        return stripePaymentService.createPaymentIntent(paymentIntent)
    }

    @PostMapping("/stripe/webhook")
    fun stripeWebhookEvents(
        @RequestBody stripeEvent: String,
        @RequestHeader("Stripe-Signature") signature: String
    ): ResponseEntity<*> {
        return stripePaymentService.paymentEventsListener(stripeEvent, signature)
    }

    @GetMapping("/order/status")
    fun getOrderStatus(@RequestHeader intentId: String): ResponseEntity<*> {
        return stripePaymentService.getPaymentStatus(intentId)
    }

    @GetMapping("/place/autocomplete")
    //TODO: Make this api secure: sign the sessionToken with key and verify
    fun placeAutoCompleteApi(@RequestParam input: String, @RequestParam sessionToken: String): ResponseEntity<*> {
        return mapsService.autoCompleteApi(input, sessionToken)
    }

    @GetMapping("/place/details")
    //TODO: Make this api secure: sign the sessionToken with key and verify
    fun placeDetailsApi(@RequestParam placeId: String, @RequestParam sessionToken: String): ResponseEntity<*> {
        return mapsService.placeDetailsApi(placeId, sessionToken)
    }

    @GetMapping("/zoom/token")
    fun getZoomSessionToken(@RequestHeader(name = "Authorization") token: String,
                            @RequestHeader sessionName: String) : ResponseEntity<*> {
        return teleconsultationService.createZoomJwtToken(token, sessionName)
    }
}