package com.tiendacomics20.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User (
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val mobile: String = "",
    val address: String = "",
    val image: String = "",
    val profileCompleted: Int = 0
): Parcelable