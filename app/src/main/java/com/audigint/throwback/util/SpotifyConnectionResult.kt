package com.audigint.throwback.util

sealed class SpotifyConnectionResult {
    object Success : SpotifyConnectionResult()
    data class Error(val message: String) : SpotifyConnectionResult()
}