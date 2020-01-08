package pl.pwr.bazdany

import pl.pwr.bazdany.ui.login.data.model.LoginResponse
import java.text.SimpleDateFormat
import java.util.*

object Session {

    var userId: Long? = null
        private set
    var token: String? = null
        private set
    var expiryDate: Date? = null
        private set

    var name: String? = null
        private set
    var surname: String? = null
        private set
    var weight: Int? = null
        private set
    var height: Int? = null
        private set
    var email: String? = null
        private set
    var birthdate: Date? = null
        private set

    fun setup(user: LoginResponse) {
        val dtf = SimpleDateFormat("dd-MM-yyyy")

        userId = user.userId
        token = user.token
        expiryDate = dtf.parse(user.expireAt)

        name = user.userDto.name
        surname = user.userDto.surname
        height = user.userDto.height
        weight = user.userDto.weight
        birthdate = dtf.parse(user.userDto.birth_day)
        email = user.userDto.email
    }

    fun destroy() {
        userId = null
        token = null
        expiryDate = null

        name = null
        surname = null
        height = null
        weight = null
        birthdate = null
        email = null
    }

}