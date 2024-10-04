package com.example.quizletappandroidv1.adapter

import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.quizletappandroidv1.databinding.LayoutFlashcardBinding
import com.example.quizletappandroidv1.models.FlashCardModel


class CreateSetItemAdapter(
) : RecyclerView.Adapter<CreateSetItemAdapter.CreateSetItemHolder>() {

    private var isDefinition: Boolean? = false
    private var isDefinitionTranslate: Boolean? = false

    private var listSet: List<FlashCardModel> = mutableListOf()

    interface OnIconClickListener {
        fun onIconClick(position: Int)
        fun onTranslateIconClick(position: Int, currentText: String)
        fun onDeleteClick(position: Int)
        fun onAddNewCard(position: Int)
        fun onChooseImage(position: Int)
    }

    private var onIconClickListener: OnIconClickListener? = null
    private val viewBinderHelper: ViewBinderHelper = ViewBinderHelper()

    inner class CreateSetItemHolder(val binding: LayoutFlashcardBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val listNewItemInSet = mutableListOf<FlashCardModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateSetItemHolder {
//        val binding = LayoutFlashcardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CreateSetItemHolder(
            LayoutFlashcardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
        )
    }

    override fun onBindViewHolder(holder: CreateSetItemHolder, position: Int) {
        viewBinderHelper.closeLayout(listSet[position].timeCreated.toString())
        viewBinderHelper.bind(
            holder.binding.swipeRevealLayout,
            listSet[position].timeCreated.toString()
        )

        val txtTerm = holder.binding.edtTerm
        val txtDefinition = holder.binding.edtDefinition

        listSet[position].imageUri?.let { uri ->
            holder.binding.imgFlashcard.setImageURI(uri)
        }

        if (listSet[position].imageUri != null) {
            holder.binding.imgFlashcard.apply {
                visibility = View.VISIBLE
                setImageURI(listSet[position].imageUri)
            }
        } else {
            holder.binding.imgFlashcard.visibility = View.GONE
        }

        holder.binding.btnVoiceTerm.setOnClickListener {
            isDefinition = false
            onIconClickListener?.onIconClick(position)
        }

        holder.binding.btnVoiceDefinition.setOnClickListener {
            isDefinition = true
            onIconClickListener?.onIconClick(position)
        }

        holder.binding.btnTranslateTerm.setOnClickListener {
            isDefinitionTranslate = false
            onIconClickListener?.onTranslateIconClick(position, txtTerm.text.toString())
        }

        holder.binding.btnTranslateDefinition.setOnClickListener {
            isDefinitionTranslate = true
            onIconClickListener?.onTranslateIconClick(position, txtDefinition.text.toString())
        }

        holder.binding.txtChooseImageFlashCard.setOnClickListener {
            onIconClickListener?.onChooseImage(position)
        }



        holder.binding.btnAddNewCard.setOnClickListener {
            onIconClickListener?.onAddNewCard(position)
        }

        val currentItem = listSet[position]
        val editableTerm = Editable.Factory.getInstance().newEditable(currentItem.term)
        val editableDesc = Editable.Factory.getInstance().newEditable(currentItem.definition)
        txtTerm.text = editableTerm
        txtDefinition.text = editableDesc
        if (currentItem.isNew == true) {
            listNewItemInSet.add(currentItem)
        }

        // Lắng nghe sự kiện khi dữ liệu thay đổi trong EditText
        txtTerm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?, start: Int, count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                charSequence: CharSequence?, start: Int, before: Int, count: Int
            ) {
                listSet.getOrNull(holder.adapterPosition)?.term = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable?) {
            }
        })

        txtDefinition.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?, start: Int, count: Int, after: Int
            ) {
                // Không cần thực hiện gì ở đây
            }

            override fun onTextChanged(
                charSequence: CharSequence?, start: Int, before: Int, count: Int
            ) {
                listSet.getOrNull(holder.adapterPosition)?.definition = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable?) {
//                if (listSet.getOrNull(holder.adapterPosition)?.term?.isEmpty() == true) {
//                    holder.binding.textFieldDefinition.apply {
//                        isErrorEnabled = true
//                        error = resources.getString(R.string.this_field_cannot_empty)
//                    }
//                }
            }
        })
        holder.binding.btnDeleteCard.setOnClickListener {
            onIconClickListener?.onDeleteClick(position)
        }
    }

    fun setOnIconClickListener(listener: OnIconClickListener) {
        this.onIconClickListener = listener
    }


    override fun getItemCount(): Int {
        return listSet.size
    }

    fun getListSet(): List<FlashCardModel> {
        return listSet
    }


    fun getIsDefinition(): Boolean? {
        return isDefinition
    }

    fun getIsDefinitionTranslate(): Boolean? {
        return isDefinitionTranslate
    }

    fun updateListSet(newList: List<FlashCardModel>) {
        this.listSet = newList
        notifyDataSetChanged()
    }

    fun updateImageUri(position: Int, uri: Uri) {
        if (position in listSet.indices) {
            (listSet as MutableList)[position].imageUri = uri
            notifyItemChanged(position)
        }
    }


}