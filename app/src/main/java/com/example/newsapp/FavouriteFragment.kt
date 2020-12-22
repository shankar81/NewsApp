package com.example.newsapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.newsapp.databinding.FragmentFavouriteBinding
import com.example.newsapp.databinding.NewsListItemBinding
import com.example.newsapp.models.Favourite

private const val TAG = "FavouriteFragment"

class FavouriteFragment : Fragment() {

    private val news = arrayListOf<Favourite>()
    private val adapter = NewsAdapter(news)
    private lateinit var mainViewModel: MainViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: FragmentFavouriteBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        binding.newsRecyclerView.setHasFixedSize(true)
        binding.newsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.newsRecyclerView.adapter = adapter

        return binding.root
    }

    private fun doAPICall() {
        mainViewModel.getFavourites()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.favourites.observe(viewLifecycleOwner, { newsList ->
            news.clear()
            news.addAll(newsList)
            adapter.notifyDataSetChanged()
        })
        doAPICall()
    }

    private inner class NewsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemBinding = NewsListItemBinding.bind(itemView)

        init {
            itemBinding.favouriteButton.setImageResource(R.drawable.icon_delete)
        }

        fun bind(favourite: Favourite, position: Int) {
            itemBinding.newsTitle.text = favourite.title
            itemBinding.newsCategory.text = favourite.source.name

            Glide.with(this@FavouriteFragment)
                .asBitmap()
                .transform(CenterCrop(), RoundedCorners(20))
                .load(favourite.urlToImage)
                .into(itemBinding.newsImage)

            itemBinding.favouriteButton.setOnClickListener {
                mainViewModel.removeFavourite(favourite)
                Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show()
                news.remove(favourite)
                adapter.notifyItemRemoved(position)
            }

            itemView.setOnLongClickListener {
                Log.d(TAG, "setOnLongClickListener: ")
                // @Todo Select multiple
                true // Consume Event don't propagate
            }
        }
    }

    private inner class NewsAdapter(private val newsList: List<Favourite>) :
        RecyclerView.Adapter<NewsHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {
            return NewsHolder(layoutInflater.inflate(R.layout.news_list_item, parent, false))
        }

        override fun onBindViewHolder(holder: NewsHolder, position: Int) {
            holder.bind(newsList[position], position)
        }

        override fun getItemCount() = newsList.size
    }

    // Note: Fragments outlive their views. Make sure you clean up any references to the binding class instance
    // in the fragment's onDestroyView() method.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}