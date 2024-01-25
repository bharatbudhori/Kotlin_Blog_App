package com.example.blogapp.model

import android.os.Parcel
import android.os.Parcelable

data class BlogItemModel(
    val heading: String? = "",
    val userName: String? = "",
    val date: String? = "",
    val post: String? = "",
    val likeCount: Int = 0,
    val imageUrl: String? = "",
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(heading)
        parcel.writeString(userName)
        parcel.writeString(date)
        parcel.writeString(post)
        parcel.writeInt(likeCount)
        parcel.writeString(imageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BlogItemModel> {
        override fun createFromParcel(parcel: Parcel): BlogItemModel {
            return BlogItemModel(parcel)
        }

        override fun newArray(size: Int): Array<BlogItemModel?> {
            return arrayOfNulls(size)
        }
    }
}
