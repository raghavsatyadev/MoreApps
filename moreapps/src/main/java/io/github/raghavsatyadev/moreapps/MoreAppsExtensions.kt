package io.github.raghavsatyadev.moreapps

inline val <T : Any> T.kotlinFileName: String
    get() = javaClass.simpleName + ".kt"