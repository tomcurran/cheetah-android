package org.tomcurran.cheetah.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _loggingIn = MutableLiveData<Boolean>()
    val loggingIn: LiveData<Boolean> = _loggingIn

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    init {
        _loggingIn.value = false
        _name.value = "Android"
    }

    fun login() {
        viewModelScope.launch {
            _loggingIn.value = true
            kotlinx.coroutines.delay(2000)
            _name.value = "Android (updated)"
            _loggingIn.value = false
        }
    }
}