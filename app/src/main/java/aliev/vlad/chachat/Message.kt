package aliev.vlad.chachat

import java.util.*

class Message(var text : String,var userName : String,var time : Long) {

    constructor(text: String, userName: String): this(text,userName, Date().time)
}