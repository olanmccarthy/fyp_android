package com.olan.finalyearproject

import android.app.Application
import com.olan.finalyearproject.models.User

//singleton that holds the user across whole app
class UserClient: Application() {
    var user: User? = null
}