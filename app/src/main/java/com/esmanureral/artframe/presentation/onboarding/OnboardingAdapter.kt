package com.esmanureral.artframe.presentation.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.esmanureral.artframe.databinding.ItemOnboardingPageBinding

class OnboardingAdapter(
    private val items: List<OnboardingPage>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(
        val binding: ItemOnboardingPageBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        return OnboardingViewHolder(inflateBinding(parent))
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        bindPage(holder.binding, items[position])
    }

    override fun getItemCount(): Int = items.size

    override fun onViewRecycled(holder: OnboardingViewHolder) {
        super.onViewRecycled(holder)
        holder.binding.lottie.cancelAnimation()
        holder.binding.lottie.clearAnimation()
    }

    private fun inflateBinding(parent: ViewGroup): ItemOnboardingPageBinding {
        return ItemOnboardingPageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    private fun bindPage(binding: ItemOnboardingPageBinding, item: OnboardingPage) {
        bindTexts(binding, item)
        bindLottie(binding, item)
    }

    private fun bindTexts(binding: ItemOnboardingPageBinding, item: OnboardingPage) {
        binding.apply {
            tvTitle.setText(item.titleRes)
            tvDesc.setText(item.descRes)
        }
    }

    private fun bindLottie(binding: ItemOnboardingPageBinding, item: OnboardingPage) {
        val context = binding.root.context

        LottieCompositionFactory.fromAsset(context, item.lottieAsset)
            .addListener { composition ->
                handleLottieResult(binding, composition)
            }
    }

    private fun handleLottieResult(
        binding: ItemOnboardingPageBinding,
        composition: com.airbnb.lottie.LottieComposition?
    ) {
        if (composition != null) showAndStartAnimation(binding, composition)
        else hideAnimation(binding)
    }

    private fun showAndStartAnimation(
        binding: ItemOnboardingPageBinding,
        composition: com.airbnb.lottie.LottieComposition
    ) {
        with(binding.lottie) {
            setComposition(composition)
            repeatCount = LottieDrawable.INFINITE
            visibility = View.VISIBLE
            if (!isAnimating) playAnimation()
        }
    }

    private fun hideAnimation(binding: ItemOnboardingPageBinding) {
        binding.lottie.apply {
            cancelAnimation()
            clearAnimation()
            visibility = View.GONE
        }
    }
}