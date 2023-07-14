package dev.sanson.lightroom.sdk.model

enum class Rendition(val code: String) {
    Thumbnail("thumbnail2x"),
    SixForty("640"),
    TwelveEighty("1280"),
    TwentyFortyEight("2048"),
    TwentyFiveSixty("2560"),
    Full("fullsize"),
}
