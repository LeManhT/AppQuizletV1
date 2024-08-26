package com.example.quizletappandroidv1.listener

import com.example.quizletappandroidv1.models.FolderModel


interface RVFolderItem {
    fun handleClickFolderItem(folderItem: FolderModel, position: Int)
}