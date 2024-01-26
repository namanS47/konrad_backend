package com.example.konrad.model
import com.google.gson.annotations.SerializedName


data class DirectionApiResponseModel(
//    @SerializedName("geocoded_waypoints" ) var geocodedWaypoints : ArrayList<GeocodedWaypoints> = arrayListOf(),
    @SerializedName("routes"             ) var routes            : ArrayList<Routes>            = arrayListOf(),
    @SerializedName("status"             ) var status            : String?                      = null
)

//data class GeocodedWaypoints (
//
//    @SerializedName("geocoder_status" ) var geocoderStatus : String?           = null,
//    @SerializedName("place_id"        ) var placeId        : String?           = null,
//    @SerializedName("types"           ) var types          : ArrayList<String> = arrayListOf()
//
//)

data class Northeast (

    @SerializedName("lat" ) var lat : Double? = null,
    @SerializedName("lng" ) var lng : Double? = null

)

data class Southwest (

    @SerializedName("lat" ) var lat : Double? = null,
    @SerializedName("lng" ) var lng : Double? = null

)

data class Bounds (

    @SerializedName("northeast" ) var northeast : Northeast? = Northeast(),
    @SerializedName("southwest" ) var southwest : Southwest? = Southwest()

)

data class Distance (

    @SerializedName("text"  ) var text  : String? = null,
    @SerializedName("value" ) var value : Int?    = null

)

data class Duration (

    @SerializedName("text"  ) var text  : String? = null,
    @SerializedName("value" ) var value : Int?    = null

)

data class EndLocation (

    @SerializedName("lat" ) var lat : Double? = null,
    @SerializedName("lng" ) var lng : Double? = null

)

data class StartLocation (

    @SerializedName("lat" ) var lat : Double? = null,
    @SerializedName("lng" ) var lng : Double? = null

)

data class Steps (

    @SerializedName("distance"          ) var distance         : Distance?      = Distance(),
    @SerializedName("duration"          ) var duration         : Duration?      = Duration(),
    @SerializedName("end_location"      ) var endLocation      : EndLocation?   = EndLocation(),
    @SerializedName("html_instructions" ) var htmlInstructions : String?        = null,
    @SerializedName("polyline"          ) var polyline         : Polyline?      = Polyline(),
    @SerializedName("start_location"    ) var startLocation    : StartLocation? = StartLocation(),
    @SerializedName("travel_mode"       ) var travelMode       : String?        = null

)

data class Legs (

    @SerializedName("distance"            ) var distance          : Distance?         = Distance(),
    @SerializedName("duration"            ) var duration          : Duration?         = Duration(),
//    @SerializedName("end_address"         ) var endAddress        : String?           = null,
//    @SerializedName("end_location"        ) var endLocation       : EndLocation?      = EndLocation(),
//    @SerializedName("start_address"       ) var startAddress      : String?           = null,
//    @SerializedName("start_location"      ) var startLocation     : StartLocation?    = StartLocation(),
//    @SerializedName("steps"               ) var steps             : ArrayList<Steps>  = arrayListOf(),
//    @SerializedName("traffic_speed_entry" ) var trafficSpeedEntry : ArrayList<String> = arrayListOf(),
//    @SerializedName("via_waypoint"        ) var viaWaypoint       : ArrayList<String> = arrayListOf()

)

data class OverviewPolyline (

    @SerializedName("points" ) var points : String? = null

)

data class Routes (

//    @SerializedName("bounds"            ) var bounds           : Bounds?           = Bounds(),
//    @SerializedName("copyrights"        ) var copyrights       : String?           = null,
    @SerializedName("legs"              ) var legs             : ArrayList<Legs>   = arrayListOf(),
    @SerializedName("overview_polyline" ) var overviewPolyline : OverviewPolyline? = OverviewPolyline(),
//    @SerializedName("summary"           ) var summary          : String?           = null,
//    @SerializedName("warnings"          ) var warnings         : ArrayList<String> = arrayListOf(),
//    @SerializedName("waypoint_order"    ) var waypointOrder    : ArrayList<String> = arrayListOf()

)

data class Polyline (

    @SerializedName("points" ) var points : String? = null

)
