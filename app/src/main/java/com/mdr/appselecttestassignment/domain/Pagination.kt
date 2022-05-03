package com.mdr.appselecttestassignment.domain

class Pagination<T>(
    val hasMore: Boolean,
    val results: List<T>
)