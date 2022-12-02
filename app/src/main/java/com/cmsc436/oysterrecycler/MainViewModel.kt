package com.cmsc436.oysterrecycler

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class MainViewModel(application: Application) : AndroidViewModel(application) {
    var curDriverID = -1
    var curRestaurantID = -1
}