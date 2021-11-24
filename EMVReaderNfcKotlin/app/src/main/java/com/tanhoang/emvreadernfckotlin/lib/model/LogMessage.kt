package com.tanhoang.emvreadernfckotlin.lib.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

/**
 * @author AliMertOzdemir
 * @class LogMessage
 * @created 21.04.2020
 */
class LogMessage : Parcelable {
    var command: String?
    var request: String?
    var response: String?

    constructor(command: String?, request: String?, response: String?) {
        this.command = command
        this.request = request
        this.response = response
    }

    protected constructor(`in`: Parcel) {
        command = `in`.readString()
        request = `in`.readString()
        response = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(command)
        dest.writeString(request)
        dest.writeString(response)
    }

    companion object CREATOR : Creator<LogMessage> {
        override fun createFromParcel(parcel: Parcel): LogMessage {
            return LogMessage(parcel)
        }

        override fun newArray(size: Int): Array<LogMessage?> {
            return arrayOfNulls(size)
        }
    }
}