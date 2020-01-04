package pl.pwr.bazdany

sealed class Effect<out R> {

    data class Success<out T>(val data: T) : Effect<T>() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Success<*>

            if (data != other.data) return false

            return true
        }

        override fun hashCode(): Int {
            return data?.hashCode() ?: 0
        }
    }

    data class Error(val message: String?) : Effect<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$message]"
        }
    }
}

/**
 * `true` if [Effect] is of type [Success] & holds non-null [Success.data].
 */
val Effect<*>.succeeded
    get() = this is Effect.Success && data != null