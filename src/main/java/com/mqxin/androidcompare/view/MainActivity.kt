package com.mqxin.androidcompare.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.leon.lfilepickerlibrary.LFilePicker
import com.leon.lfilepickerlibrary.utils.Constant
import com.mqxin.androidcompare.R
import com.mqxin.androidcompare.adapter.TextAdapter
import com.mqxin.androidcompare.core.DiffPosition
import com.mqxin.androidcompare.core.HirschbergComparision
import com.mqxin.androidcompare.core.StringDescription
import com.mqxin.androidcompare.utils.EncodeDetect
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.apache.commons.io.FileUtils
import java.io.File


class MainActivity : AppCompatActivity(), View.OnClickListener, View.OnLongClickListener {
    private lateinit var rvUp: RecyclerView
    private lateinit var rvDown: RecyclerView
    private lateinit var adapterUp: TextAdapter
    private lateinit var adapterDown: TextAdapter
    private lateinit var btnUp: Button
    private lateinit var btnDown: Button
    private lateinit var ivChangeUp: ImageView
    private lateinit var ivChangeDown: ImageView
    private lateinit var progressBar: ProgressBar
    private val TAG = "MainActivity"
    private val REQUEST_CODE_FROM_ACTIVITY_1 = 1000
    private val REQUEST_CODE_FROM_ACTIVITY_2 = 2000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putCharSequence("UP", adapterUp.text)
        outState?.putCharSequence("DOWN", adapterDown.text)
    }

    private fun initView(savedInstanceState: Bundle?) {
        Log.d(TAG, "initView")
        //actionBar.title = resources.getString(R.string.name)
        rvUp = findViewById(R.id.rv_up)
        rvDown = findViewById(R.id.rv_down)
        rvUp.layoutManager = LinearLayoutManager(this)
        rvDown.layoutManager = LinearLayoutManager(this)
        adapterUp = TextAdapter(this)
        adapterDown = TextAdapter(this)
        rvUp.adapter = adapterUp
        rvDown.adapter = adapterDown

        val textUp = savedInstanceState?.getCharSequence("UP", "")
        val textDown = savedInstanceState?.getCharSequence("DOWN", "")
        adapterUp.text = textUp as SpannableStringBuilder? ?: SpannableStringBuilder.valueOf("")
        adapterDown.text = textDown as SpannableStringBuilder? ?: SpannableStringBuilder.valueOf("")

        btnUp = findViewById(R.id.btn_up)
        btnDown = findViewById(R.id.btn_down)
        ivChangeUp = findViewById(R.id.iv_change_up)
        ivChangeDown = findViewById(R.id.iv_change_down)

        if (!TextUtils.isEmpty(textUp)) {
            btnUp.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(textDown)) {
            btnDown.visibility = View.GONE
        }

        btnUp.setOnClickListener(this)
        btnDown.setOnClickListener(this)
        ivChangeUp.setOnClickListener(this)
        ivChangeDown.setOnClickListener(this)
        ivChangeUp.setOnLongClickListener(this)
        ivChangeDown.setOnLongClickListener(this)

        progressBar = findViewById(R.id.progressBar)

        val listenerUp: RecyclerView.OnScrollListener
        var listenerDown: RecyclerView.OnScrollListener? = null
        listenerUp = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                rvDown.removeOnScrollListener(listenerDown)
                rvDown.scrollBy(dx, dy)
                rvDown.addOnScrollListener(listenerDown)
            }
        }
        rvUp.addOnScrollListener(listenerUp)

        listenerDown = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                rvUp.removeOnScrollListener(listenerUp)
                rvUp.scrollBy(dx, dy)
                rvUp.addOnScrollListener(listenerUp)
            }
        }
        rvDown.addOnScrollListener(listenerDown)

        /*rvUp.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            Log.d(TAG, "v: $v, scrollX: $scrollX, scrollY: $scrollY, oldScrollX: $oldScrollX, oldScrollY: $oldScrollY")
        }*/

        val bottomNavigationBar = findViewById<BottomNavigationBar>(R.id.bottomNavigationBar)

        bottomNavigationBar
                .addItem(BottomNavigationItem(R.drawable.ic_home_white_24dp, "Home"))
                .addItem(BottomNavigationItem(R.drawable.ic_book_white_24dp, "History"))
                .addItem(BottomNavigationItem(R.drawable.ic_favorite_white_24dp, "About"))
                .initialise()

        bottomNavigationBar.setTabSelectedListener(object : BottomNavigationBar.OnTabSelectedListener {
            override fun onTabReselected(position: Int) {

            }

            override fun onTabUnselected(position: Int) {

            }

            override fun onTabSelected(position: Int) {

            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_up -> {
                selectFile(REQUEST_CODE_FROM_ACTIVITY_1)
            }

            R.id.btn_down -> {
                selectFile(REQUEST_CODE_FROM_ACTIVITY_2, Constant.ICON_STYLE_BLUE)
            }

            R.id.iv_change_up -> {
                selectFile(REQUEST_CODE_FROM_ACTIVITY_1)
            }

            R.id.iv_change_down -> {
                selectFile(REQUEST_CODE_FROM_ACTIVITY_2, Constant.ICON_STYLE_BLUE)
            }
        }
    }

    override fun onLongClick(v: View?): Boolean {
        when (v?.id) {
            R.id.iv_change_up -> {
                adapterUp.text = SpannableStringBuilder.valueOf("")
                btnUp.visibility = View.VISIBLE
            }
            R.id.iv_change_down -> {
                adapterDown.text = SpannableStringBuilder.valueOf("")
                btnDown.visibility = View.VISIBLE
            }
        }
        return true
    }

    private fun selectFile(requestCode: Int, color: Int = Constant.ICON_STYLE_YELLOW) {
        LFilePicker()
                .withActivity(this)
                .withRequestCode(requestCode)
                .withIconStyle(color)
                .withMutilyMode(false)
                .withStartPath(File(Environment.getExternalStorageDirectory().absolutePath, Environment.DIRECTORY_DOWNLOADS).absolutePath)
                .withIsGreater(false)
                .withFileSize(500 * 1024)
                .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_FROM_ACTIVITY_1 -> {
                    Log.d(TAG, "REQUEST_CODE_FROM_ACTIVITY_1")
                    readFile(data, adapterUp, btnUp)
                }

                REQUEST_CODE_FROM_ACTIVITY_2 -> {
                    Log.d(TAG, "REQUEST_CODE_FROM_ACTIVITY_2")
                    readFile(data, adapterDown, btnDown)
                }
            }
        }
    }

    private fun readFile(data: Intent?, adapter: TextAdapter, button: Button) {
        progressBar.visibility = View.VISIBLE
        Observable
                .create<String> {
                    Log.d(TAG, "readFile")
                    val list = data?.getStringArrayListExtra("paths")
                    val filePath = list?.get(0)
                    val fileEncode = EncodeDetect.getJavaEncode(filePath)
                    val fileContent = FileUtils.readFileToString(File(filePath), fileEncode)
                    it.onNext(fileContent)
                }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    adapter.text = SpannableStringBuilder.valueOf(it)
                    button.visibility = View.GONE
                    progressBar.visibility = View.GONE

                    val textUp = adapterUp.text.toString()
                    val textDown = adapterDown.text.toString()

                    if (!TextUtils.isEmpty(textUp) && !TextUtils.isEmpty(textDown)) {
                        progressBar.visibility = View.VISIBLE
                        Observable
                                .create<String> {
                                    val sdUp = StringDescription(if (textUp.length < textDown.length) textUp else textDown)
                                    val sdDown = StringDescription(if (textUp.length < textDown.length) textDown else textUp)

                                    val mcsContent = HirschbergComparision().hirschbergAlgorithm(sdUp, sdDown)
                                    it.onNext(mcsContent)
                                }
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    setDifferentTextPartToRed(it, adapterUp)
                                    setDifferentTextPartToRed(it, adapterDown)
                                }
                    }
                }
    }

    private fun setDifferentTextPartToRed(mcs: String, adapter: TextAdapter) {
        Observable
                .create<SpannableStringBuilder> {
                    val list = getDiffPostList(adapter.text.toString(), mcs)
                    val builder = setDiffTextToRed(list, adapter.text.toString())
                    it.onNext(builder)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    adapter.text = it
                    progressBar.visibility = View.GONE
                }
    }

    private fun setDiffTextToRed(list: ArrayList<DiffPosition>, content: String): SpannableStringBuilder {
        val builder = SpannableStringBuilder(content)
        list.forEach {
            val redSpan = ForegroundColorSpan(Color.RED)
            builder.setSpan(redSpan, it.diffBegin, it.diffEnd + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return builder
    }

    private fun getDiffPostList(content: String, mcs: String): ArrayList<DiffPosition> {
        val list = ArrayList<DiffPosition>()
        var diffBegin = false
        var posContent = 0
        var posMCS = 0
        var curDiff: DiffPosition? = null
        //Log.d(TAG, "mcs.length: ${mcs.length}, content.length: ${content.length}")
        while (posMCS < mcs.length) {
            //Log.d(TAG, "posMCS: $posMCS, posContent: $posContent, thread name: ${Thread.currentThread().name}")
            if (posContent >= content.length) {
                if (diffBegin) {
                    curDiff!!.diffEnd = posContent - 1
                }
                break
            }
            if (content[posContent] != mcs[posMCS]) {
                if (!diffBegin) {
                    diffBegin = true
                    curDiff = DiffPosition()
                    curDiff.diffBegin = posContent
                    list.add(curDiff)
                }
                posContent++
            } else {
                if (diffBegin) {
                    diffBegin = false
                    curDiff!!.diffEnd = posContent - 1
                }
                posMCS++
                posContent++
            }
        }

        return list
    }
}
