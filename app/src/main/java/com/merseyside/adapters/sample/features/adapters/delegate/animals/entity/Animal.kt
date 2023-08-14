package com.merseyside.adapters.sample.features.adapters.delegate.animals.entity

import com.merseyside.merseyLib.kotlin.contract.Identifiable

abstract class Animal(
    open val name: String,
    open val age: Int
): Identifiable<String> {

    override val id by lazy { name }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Animal) return false

        if (name != other.name) return false
        return age == other.age
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + age
        return result
    }
}