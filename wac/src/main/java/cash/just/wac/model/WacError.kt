package cash.just.wac.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WacError(
    @field:Json(name = "code") val code: String,
    @field:Json(name = "server_message") val server_message: String)

data class WacErrorResponse(val result:String, val error:WacError)