package com.example.epidemicapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.epidemicapp.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private val newsList = ArrayList<NewsData>()
    private var index = 0
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        //21表示5.0
        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT


        thread {
            epidemicReporting()
            runOnUiThread {
                Log.i("json1111", "111111111111111111111${newsList}")
                mBinding.RecyclerViewTest.layoutManager = LinearLayoutManager(this)
                val adapter = NewsAdapter(newsList)
                mBinding.RecyclerViewTest.adapter = adapter
            }
        }
    }

    private fun epidemicReporting() {
            try {
                //创建网络连接客户端对象
                val client = OkHttpClient()
                val request: Request = Request.Builder()
                    .url("https://lab.isaaclin.cn/nCoV/api/news")
                    .build()
                val response = client.newCall(request).execute()
                val bodyString = response.body?.string() ?: "目标地址暂无响应,请检查网络后重试"
                    parseGSON(bodyString)
                    epidemicData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }
    private fun parseGSON(data: String){
        try {
            val json = JSONObject(data)
            val jsonArr = json.getJSONArray("results")
            for (i in 0 until jsonArr.length()){
                val jsonData = jsonArr.getJSONObject(i)
                newsList.add(
                    NewsData(
                        jsonData.getString("title"),
                        jsonData.getString("summary"),
                        jsonData.getString("infoSource"),
                        time(jsonData.getString("pubDate")),
                        jsonData.getString("sourceUrl")
                    )
                )
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun epidemicData() {
            try {
                //创建网络连接客户端对象
                val client = OkHttpClient()
                val request: Request = Request.Builder()
                    .url("https://lab.isaaclin.cn/nCoV/api/overall")
                    .build()
                val response = client.newCall(request).execute()
                val bodyString = response.body?.string() ?: "目标地址暂无响应,请检查网络后重试"
                runOnUiThread {
                    parseObject(bodyString)
                    mBinding.time.text = "数据更新至 "+SimpleDateFormat("yyyy-MM-dd").format(Date())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }

    @SuppressLint("SetTextI18n")
    private fun parseObject(data: String) {
        try {
            val json = JSONObject(data)
            val jsonArr = json.getJSONArray("results")
            val jsonData = jsonArr.getJSONObject(0)
            mBinding.currentConfirmedCount.text = jsonData.getString("currentConfirmedCount")
            mBinding.currentConfirmedIncr.text = demo(jsonData.getString("currentConfirmedIncr"))
            mBinding.seriousCount.text = jsonData.getString("seriousCount")
            mBinding.seriousIncr.text = demo(jsonData.getString("seriousIncr"))
            mBinding.curedCount.text = jsonData.getString("curedCount")
            mBinding.curedIncr.text = demo(jsonData.getString("curedIncr"))
            mBinding.confirmedCount.text = jsonData.getString("confirmedCount")
            mBinding.confirmedIncr.text = demo(jsonData.getString("confirmedIncr"))
            mBinding.suspectedCount.text = jsonData.getString("suspectedCount")
            mBinding.suspectedIncr.text = demo(jsonData.getString("suspectedIncr"))
            mBinding.deadCount.text = jsonData.getString("deadCount")
            mBinding.deadIncr.text = demo(jsonData.getString("deadIncr"))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    //RecyclerView适配器
    inner class NewsAdapter(private val newsList: List<NewsData>) :
        RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val titleView: TextView = view.findViewById(R.id.titleView)
            val summary: TextView = view.findViewById(R.id.summary)
            val infoSource: TextView = view.findViewById(R.id.infoSource)
            val pubDate: TextView = view.findViewById(R.id.pubDate)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
            val viewHolder = ViewHolder(view)
            viewHolder.itemView.setOnClickListener {
                index = viewHolder.adapterPosition
                val list = newsList[index]
                //Toast.makeText(parent.context, "您点击了!!!${list.titleView}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MainActivity, WebActivity::class.java)
                intent.putExtra("url",list.sourceUrl)
                startActivity(intent)
            }
            return viewHolder
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data = newsList[position]
            holder.titleView.text = data.titleView
            holder.summary.text = data.summary
            holder.infoSource.text = data.infoSource
            holder.pubDate.text = data.pubDate

        }
        override fun getItemCount() = newsList.size
    }

    //格式化unix时间
    @SuppressLint("SimpleDateFormat")
    private fun time(time: String):String{
        val times = time.toBigInteger()
        val df = SimpleDateFormat("yyyy-MM-dd")
        return df.format(times)
    }

    //判断数值是正负
    private fun demo(number:String):String{
        val num = number.toInt()
        return if (num<0){
            num.toString()
        }else{
            "+$num"
        }
    }
}