package com.meplus.speech.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by dandanba on 3/30/16.
 */
data class Music(
        @JsonProperty("singer") var singer: String,
        @JsonProperty("sourceName") var sourceName: String,
        @JsonProperty("name") var name: String,
        @JsonProperty("downloadUrl") var downloadUrl: String
)