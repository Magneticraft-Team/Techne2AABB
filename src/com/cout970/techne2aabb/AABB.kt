package com.cout970.techne2aabb

import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 21/08/2016.
 */
class AABB {

    val min: IVector3
    val max: IVector3

    constructor(a: IVector3, b: IVector3) {
        min = a.min(b)
        max = a.max(b)
    }

    override fun toString(): String {
        return "AABB(min=$min, max=$max)"
    }

    fun translate(a: IVector3): AABB = AABB(min + a, max + a)
    fun rotate(rot: IQuaternion): AABB = AABB(rot.rotate(min).round(), rot.rotate(max).round())
}