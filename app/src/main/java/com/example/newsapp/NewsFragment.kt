package com.example.newsapp

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.browser.customtabs.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.newsapp.models.News
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome"
private const val TAG = "NewsFragment"

class NewsFragment(private val category: String = "") : Fragment() {

    private val news = arrayListOf<News>()
    private val adapter = NewsAdapter(news)
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var mainViewModel: MainViewModel
    private var country = "in"
    private var searchQuery = ""
    private var queryJob: Job? = null

    private var mCustomTabsClient: CustomTabsClient? = null
    private var mCustomTabsSession: CustomTabsSession? = null

    // Custom tab service connection
    private val connection = object : CustomTabsServiceConnection() {
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
            mCustomTabsClient = client
            mCustomTabsClient?.warmup(2)
            mCustomTabsSession = mCustomTabsClient?.newSession(object : CustomTabsCallback() {

            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_news, container, false)
        // Binding to service
        CustomTabsClient.bindCustomTabsService(
            requireContext(),
            CUSTOM_TAB_PACKAGE_NAME,
            connection
        )

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        setHasOptionsMenu(true)

        newsRecyclerView = view.findViewById(R.id.news_recycler_view)
        newsRecyclerView.setHasFixedSize(true)
        newsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        newsRecyclerView.adapter = adapter

        return view
    }

    private fun doAPICall() {
        mainViewModel.getNews(searchQuery, category, country)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.main_menu, menu)

        val item = menu.findItem(R.id.filterMenu)
        val subMenu = item.subMenu

        mainViewModel.countries.values.mapIndexed { index: Int, value: String ->
            val sMenu = subMenu.add(R.id.country_menu, mainViewModel.countryId[index], 0, value)
            if (country == value) {
                sMenu.isChecked = true
            }
        }

        subMenu.setGroupCheckable(R.id.country_menu, true, true)

        val searchMenu = menu.findItem(R.id.search_menu)
        val searchView: SearchView = searchMenu.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                queryJob?.cancel()

                mainViewModel.coroutineScope.launch(Dispatchers.Main) {
                    if (newText != null && newText.length % 3 == 0) {
                        delay(5000)
                        searchQuery = newText.trim()
                        doAPICall()
                    }
                }
                return false
            }
        })
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE

        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.isCheckable && !item.isChecked) {
            item.isChecked = true

            for ((key, value) in mainViewModel.countries) {
                if (value == item.title) {
                    country = key
                    break
                }
            }
            doAPICall()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.news.observe(viewLifecycleOwner, { newsList ->
            news.clear()
            news.addAll(newsList)
            adapter.notifyDataSetChanged()

            val otherLikelyUrls = arrayListOf<Bundle>()
            for (index in newsList.indices) {
                if (index != 0) {
                    val bundle = Bundle().apply {
                        putParcelable(CustomTabsService.KEY_URL, Uri.parse(newsList[index].url))
                    }
                    otherLikelyUrls.add(bundle)
                }
            }
            val res =
                mCustomTabsSession?.mayLaunchUrl(Uri.parse(newsList[0].url), null, otherLikelyUrls)
            Log.d(TAG, "onViewCreated: $res")
        })
        doAPICall()
    }

    private fun launchUrl(url: String?) {
        if (url == null) {
            Toast.makeText(context, "Url not found", Toast.LENGTH_SHORT).show()
            return
        }
        val uri = Uri.parse(url)
        val pi = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = CustomTabsIntent.Builder(mCustomTabsSession)

        val color = ResourcesCompat.getColor(resources, R.color.purple_500, null)
        builder.setToolbarColor(color)
        builder.addDefaultShareMenuItem()

        // Add menu item
        builder.addMenuItem("Custom Menu Item", pi)

        // Animation Enter/Exit
        builder.setStartAnimations(
            requireContext(),
            R.anim.slide_left_in,
            R.anim.slide_left_out
        )
        builder.setExitAnimations(
            requireContext(),
            R.anim.slide_right_in,
            R.anim.slide_right_out,
        )

        // Add Custom Back Button Icon
        Glide.with(requireContext()).asBitmap().load(R.drawable.icon_back).into(object :
            CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                builder.setCloseButtonIcon(resource)
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(requireContext(), uri)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
    }

    private inner class NewsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val category: TextView = itemView.findViewById(R.id.news_category)
        private val title: TextView = itemView.findViewById(R.id.news_title)
        private val image: ImageView = itemView.findViewById(R.id.news_image)
        private val favouriteButton: ImageView = itemView.findViewById(R.id.favouriteButton)

        fun bind(news: News) {
            title.text = news.title
            category.text = news.source.name

            Glide.with(this@NewsFragment)
                .asBitmap()
                .transform(CenterCrop(), RoundedCorners(20))
                .load(news.urlToImage)
                .into(image)

            favouriteButton.setOnClickListener {
                mainViewModel.addFavourite(news)
                Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT)
                    .show()
            }

            itemView.setOnClickListener {
                launchUrl(news.url)
            }
        }
    }

    private inner class NewsAdapter(private val newsList: List<News>) :
        RecyclerView.Adapter<NewsHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {
            return NewsHolder(layoutInflater.inflate(R.layout.news_list_item, parent, false))
        }

        override fun onBindViewHolder(holder: NewsHolder, position: Int) {
            holder.bind(newsList[position])
        }

        override fun getItemCount() = newsList.size
    }
}