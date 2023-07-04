package com.merseyside.adapters.sample.features.adapters.delegate.animals.entity

data class Cat(
    override val name: String,
    override val age: Int,
    val breed: String
): Animal(name, age)