package com.group50.note_taking_app

import androidx.lifecycle.ViewModel

private const val TAG: String = "LoginViewModel";

class LoginViewModel: ViewModel() {

    var userName: String = ""
    var password: String = ""
}