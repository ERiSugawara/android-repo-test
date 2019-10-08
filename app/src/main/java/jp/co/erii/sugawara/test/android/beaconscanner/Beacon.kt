package jp.co.erii.sugawara.test.android.beaconscanner

data class Beacon(
    val bdAddress: String,
    val rssi: Int,
    val data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Beacon

        if (bdAddress != other.bdAddress) return false
        if (rssi != other.rssi) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bdAddress.hashCode()
        result = 31 * result + rssi
        result = 31 * result + data.contentHashCode()
        return result
    }

    override fun toString(): String {
        return "<${this.javaClass.simpleName}: address=$bdAddress, rssi=$rssi>"
    }
}