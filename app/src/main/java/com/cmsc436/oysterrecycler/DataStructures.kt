import com.google.firebase.Timestamp

data class Restaurant(
    val UID: String, val name: String, val email: String, val phone: String,
    val address: String,
    var activePickups: List<String>,
    val completedPickups: List<String>
) {

    fun serialize(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["UID"] = UID
        map["name"] = name
        map["email"] = email
        map["phone"] = phone
        map["address"] = address
        map["active_pickups"] = activePickups
        map["completed_pickups"] = completedPickups

        return map
    }
}

data class Driver(
    val UID: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val carLicensePlate: String,
    val carMake: String,
    val carColor: String, var activePickups: List<String>,
    var completedPickups: List<String>
) {
    // create a new driver
    fun serialize(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["UID"] = UID
        map["first_name"] = firstName
        map["last_name"] = lastName
        map["car_license_plate"] = carLicensePlate
        map["email"] = email
        map["car_make"] = carMake
        map["car_color"] = carColor
        map["active_pickups"] = activePickups
        map["completed_pickups"] = completedPickups
        return map
    }
}

data class Pickup (val UID: String,
              val restaurantID: String, val driverID: String, val when_date: String,
) {

    fun isDriverAssigned(): Boolean {
        return driverID!!.isNotEmpty()
    }
    
    fun serialize(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["UID"] = UID
        map["restaurantID"] = restaurantID
        map["driverID"] = driverID
        map["when"] = when_date
        return map
    }

}