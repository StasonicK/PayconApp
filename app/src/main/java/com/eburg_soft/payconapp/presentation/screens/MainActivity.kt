package com.eburg_soft.payconapp.presentation.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eburg_soft.payconapp.R
import com.eburg_soft.payconapp.presentation.screens.fragments.goods.GoodsFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.frame_container, GoodsFragment.newInstance(), GoodsFragment::class.java.simpleName)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        fragmentManager.findFragmentById(R.id.frame_container)
        if (fragmentManager.backStackEntryCount > 1) {
            fragmentManager.popBackStack()
        } else finish()
    }
}