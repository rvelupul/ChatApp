package com.chatapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chatapp.R
import com.chatapp.model.MessageModel
import com.chatapp.utils.Utils.getDate

class ChatListAdapter(
    val userId: String,
    val messageModels: ArrayList<MessageModel?>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ViewType.TEXT_MESSAGE_OWN.value) {
            SelfTextMessageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_text_chat_own, parent, false)
            )
        } else {
            OtherTextMessageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_text_chat_other, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.e("onBindViewHolder ", messageModels.size.toString() + "")
        if (holder is SelfTextMessageViewHolder) {
            holder.tvMsg.text = messageModels[position]?.msg
            holder.tvTime.text =
                getDate(messageModels[position]?.timeStamp!!.toLong())
        } else {
            (holder as OtherTextMessageViewHolder).tvMsg.text = """
                ${messageModels[position]?.msg}
                """.trimIndent()
            holder.tvTime.text =
                getDate(messageModels[position]?.timeStamp!!.toLong())
            holder.tvName.text =
                messageModels[position]?.senderId
        }
    }

    override fun getItemCount(): Int {
        return messageModels.size
    }

    internal inner class SelfTextMessageViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val tvMsg: TextView
        val tvTime: TextView

        init {
            tvMsg = itemView.findViewById(R.id.tvMsg)
            tvTime = itemView.findViewById(R.id.tvTime)
        }
    }

    internal inner class OtherTextMessageViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val tvMsg: TextView
        val tvTime: TextView
        val tvName: TextView

        init {
            tvMsg = itemView.findViewById(R.id.tvMsg)
            tvName = itemView.findViewById(R.id.tvName)
            tvTime = itemView.findViewById(R.id.tvTime)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val messageModel = messageModels[position]
        return if (messageModel?.senderId == userId) {
            ViewType.TEXT_MESSAGE_OWN.value
        } else {
            ViewType.TEXT_MESSAGE_OTHER.value
        }
    }

    internal enum class ViewType(val value: Int) {
        TEXT_MESSAGE_OWN(0), TEXT_MESSAGE_OTHER(1);
    }
}