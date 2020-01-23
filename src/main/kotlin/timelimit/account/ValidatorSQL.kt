package main.kotlin.timelimit.account

class ValidatorSQL {
    private val defaultLengths : Map<String, Int> = mapOf(
        Pair("login", 32),
        Pair("password", 32),
        Pair("token", 32),
        Pair("email", 64),
        Pair("first_name", 64),
        Pair("second_name", 64),
        Pair("vk_profile", 64),
        Pair("country", 64)
    )

    fun getLength(field: String) : Int {
        return defaultLengths.getOrDefault(field, Int.MAX_VALUE)
    }

    fun checkLength(field: String, value: String) : Boolean {
        return (getLength(field) >= value.length)
    }

    companion object {
        private val instance = ValidatorSQL()
        fun getInstance() : ValidatorSQL {
            return instance
        }
    }
}