package com.mdr.appselecttestassignment.domain

interface MovieRepo {
    suspend fun fetch(offset: Int = 0): Result<Pagination<Movie>>
}