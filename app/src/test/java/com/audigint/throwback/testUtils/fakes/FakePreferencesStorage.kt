package com.audigint.throwback.testUtils.fakes

import com.audigint.throwback.data.PreferencesStorage
import com.audigint.throwback.util.Constants

class FakePreferencesStorage : PreferencesStorage {
    override var accessToken = ""
    override var position = Constants.DEFAULT_POSITION
    override var year = Constants.DEFAULT_YEAR
    override var tokenExp: Long = -1
}