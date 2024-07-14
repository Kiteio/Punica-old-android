package org.kiteio.punica.datastore

/**
 * 本地存储标识
 * @property id 唯一标识
 */
abstract class Identified {
    abstract val id: String


    override fun hashCode() = id.hashCode()


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Identified

        return id == other.id
    }
}