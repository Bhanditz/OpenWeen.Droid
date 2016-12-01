package moe.tlaster.openween.activity

import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout

import moe.tlaster.openween.R
import moe.tlaster.openween.fragment.user.Favorite
import moe.tlaster.openween.fragment.user.Timeline

class WeiboListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weibo_list)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        if (intent.extras.getString(getString(R.string.weibo_list_type_name)) == getString(R.string.weibo_list_type_status_name)) {
            val timeline = Timeline.create(intent.extras.getLong(getString(R.string.weibo_list_user_id_name)))
            fragmentTransaction.add(R.id.weibo_list_fragment_container, timeline)
        } else if (intent.extras.getString(getString(R.string.weibo_list_type_name)) == getString(R.string.weibo_list_type_favor_name)) {
            val favorite = Favorite.create(intent.extras.getLong(getString(R.string.weibo_list_user_id_name)))
            fragmentTransaction.add(R.id.weibo_list_fragment_container, favorite)
        }
        fragmentTransaction.commit()
    }
}
