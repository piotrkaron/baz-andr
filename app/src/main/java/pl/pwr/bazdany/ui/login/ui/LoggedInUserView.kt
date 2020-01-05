package pl.pwr.bazdany.ui.login.ui

import pl.pwr.bazdany.ui.login.data.model.LoginResponse

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
    val displayName: String,
    val dto: LoginResponse
    //... other data fields that may be accessible to the UI
)
