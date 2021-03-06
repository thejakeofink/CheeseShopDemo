package com.vikingsen.cheesedemo.ux.cheeselist


import com.vikingsen.cheesedemo.model.database.cheese.Cheese

interface CheeseListContract {
    interface View {
        fun showLoading(loading: Boolean)
        fun showCheeses(cheeses: List<Cheese>)
        fun showError()
    }
}
