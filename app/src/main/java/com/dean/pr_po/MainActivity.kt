package com.dean.pr_po

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.dean.pr_po.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.synnapps.carouselview.ImageListener

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var allData: CardView
    private lateinit var userData: CardView
    var sampleImages = intArrayOf(
        R.drawable.wall_view,
        R.drawable.ic_logo
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.carouselView.setImageListener(imageListener)
        mainBinding.carouselView.pageCount = sampleImages.size

        mainBinding.allData.setOnClickListener(this)
        mainBinding.userData.setOnClickListener(this)
    }

    var imageListener = ImageListener { position: Int, imageView: ImageView ->
        imageView.setImageResource(
            sampleImages[position]
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            val gotoSplash = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(gotoSplash)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val toExit = Intent(Intent.ACTION_MAIN)
        toExit.addCategory(Intent.CATEGORY_HOME)
        toExit.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(toExit)
        finish()
    }

    override fun onClick(view: View?) {
        if (view?.getId() == R.id.all_data){
            val gotoSplash = Intent(this@MainActivity, AllDataActivity::class.java)
            startActivity(gotoSplash)
        }
        if (view?.getId() == R.id.user_data){
            val gotoSplash = Intent(this@MainActivity, UserDataActivity::class.java)
            startActivity(gotoSplash)
        }
    }
}