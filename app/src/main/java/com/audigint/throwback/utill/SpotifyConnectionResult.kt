package com.audigint.throwback.utill

sealed class SpotifyConnectionResult {
    object Success : SpotifyConnectionResult()
    data class Error(val message: String) : SpotifyConnectionResult()
}