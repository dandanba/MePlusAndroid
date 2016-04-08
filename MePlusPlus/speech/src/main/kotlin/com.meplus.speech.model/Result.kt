package com.meplus.speech.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by dandanba on 3/30/16.
 */
data class Result(
        @JsonProperty("rc") var rc: Int,
        @JsonProperty("operation") var operation: String,
        @JsonProperty("service") var service: String,
        @JsonProperty("answer") var answer: Answer,
        @JsonProperty("text") var text: String,
        @JsonProperty("moreResults") var moreResults: List<Answer>
)