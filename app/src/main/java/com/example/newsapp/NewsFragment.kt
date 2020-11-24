package com.example.newsapp

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.newsapp.models.News
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewsFragment(private val category: String = "") : Fragment() {

    private val news = arrayListOf<News>()
    private val adapter = NewsAdapter(news)
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var mainViewModel: MainViewModel
    private var country = "in"
    private var searchQuery = ""
    private var queryJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_news, container, false)

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
        })
        doAPICall()
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