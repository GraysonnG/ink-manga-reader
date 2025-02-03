package com.blanktheevil.inkmangareader.reader

import android.content.Context
import coil.executeBlocking
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.blanktheevil.inkmangareader.data.repositories.chapter.ChapterRepository
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaRepository
import com.blanktheevil.inkmangareader.data.repositories.mappers.currentChapter
import com.blanktheevil.inkmangareader.data.repositories.mappers.nextChapter
import com.blanktheevil.inkmangareader.data.repositories.mappers.prevChapter
import com.blanktheevil.inkmangareader.download.DownloadManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class InkReaderManager(
    private val context: Context,
    private val chapterRepository: ChapterRepository,
    private val mangaRepository: MangaRepository,
    private val downloadManager: DownloadManager,
) : ReaderManager {
    private val readerScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val _state = MutableStateFlow(ReaderManagerState())
    override val state: StateFlow<ReaderManagerState> = _state.asStateFlow()

    override fun setChapter(chapterId: String) {
        readerScope.launch {
            val isChapterDownloaded = false // TODO: Download Manager Things

            updateState { copy(
                currentChapterId = chapterId,
                currentPage = 0,
                currentChapterPageUrls = emptyList(),
                currentChapterLoading = true,
                currentChapterPagesLoaded = true,
            ) }

            val getChapterDataJob = async { getChapterData(chapterId = chapterId) }
            val getChapterPagesDataJob = async { getChapterPagesData(chapterId = chapterId, isDownloaded = isChapterDownloaded) }

            awaitAll(
                getChapterDataJob,
                getChapterPagesDataJob
            )

            if (_state.value.currentChapterPageUrls.size == 1) {
                markChapterRead(true)
            }

            updateState { copy(
                expanded = true,
            ) }

            if (!isChapterDownloaded) {
                preloadChapterPages(_state.value.currentChapterPageUrls)
            }

            _state.value.mangaId?.let { mangaId ->
                mangaRepository.getAggregate(mangaId = mangaId).onSuccess { chapters ->
                    updateState { copy(
                        chapters = chapters,
                        currentLinkedChapter = chapters.currentChapter(chapterId)
                    ) }
                }
            }
        }
    }

    private suspend fun getChapterData(chapterId: String) {
        chapterRepository.getEager(chapterId = chapterId)
            .onSuccess { chapter ->
                updateState { copy(
                    currentChapter = chapter,
                    currentChapterLoading = false,
                    mangaId = chapter.relatedMangaId,
                    manga = chapter.relatedManga,
                    readerType = if (chapter.relatedManga?.tags?.any { t -> t.equals("Long Strip", true) } == true) {
                        ReaderType.VERTICAL
                    } else {
                        ReaderType.PAGE // TODO: SettingsManager stuff
                    }
                ) }

                if (chapter.relatedMangaId != null) {
                    readerScope.launch {
                        mangaRepository.get(chapter.relatedMangaId, hardRefresh = false)
                            .collectLatest {
                                it.onSuccess { manga ->
                                    updateState { copy(
                                        manga = manga
                                    ) }
                                }
                            }
                    }
                }
            }
    }

    private suspend fun getChapterPagesData(
        chapterId: String,
        isDownloaded: Boolean,
    ) {
        if (isDownloaded) {

        } else {
            chapterRepository
                .getPages(chapterId, false) // TODO: SettingsManager stuff
                .onSuccess { pages ->
                    updateState { copy(
                        currentChapterPagesLoaded = false,
                        currentChapterPageUrls = pages,
                        currentChapterPageLoaded = List(pages.size) { false }
                            .toMutableList(),
                    ) }
                }
        }
    }

    override fun closeReader() {
        updateState { copy(
            currentPage = 0,
            currentChapterId = null,
            currentChapter = null,
            currentChapterPageUrls = emptyList(),
            manga = null,
            mangaId = null,
            chapters = emptyList(),
            currentLinkedChapter = null,
        ) }
    }

    override fun expandReader() {
        updateState { copy(
            expanded = true
        ) }
    }

    override fun shrinkReader() {
        updateState { copy(
            expanded = false
        ) }
    }

    override fun nextPage() {
        val nextPage = _state.value.currentPage + 1

        if (nextPage == _state.value.currentChapterPageUrls.lastIndex) {
            markChapterRead(true)
        }

        when {
            nextPage < _state.value.currentChapterPageUrls.size -> {
                updateState { copy(
                    currentPage = nextPage
                ) }
            }

            else -> {
                nextChapter()
            }
        }
    }

    override fun prevPage() {
        val prevPage = _state.value.currentPage - 1

        if (prevPage >= 0) {
            updateState { copy(
                currentPage = prevPage
            ) }
        } else {
            prevChapter()
        }
    }

    override fun nextChapter() {
        _state.value.currentLinkedChapter?.let { currentChapter ->
            val nextChapter = _state.value.chapters.nextChapter(currentChapter)
            if (nextChapter != null) {
                updateState { copy(
                    currentLinkedChapter = nextChapter
                ) }
                setChapter(nextChapter.id)
            } else {
                closeReader()
            }
        }
    }

    override fun prevChapter() {
        _state.value.currentLinkedChapter?.let { currentChapter ->
            val prevChapter = _state.value.chapters.prevChapter(currentChapter)
            if (prevChapter != null) {
                updateState { copy(
                    currentLinkedChapter = prevChapter
                ) }
                setChapter(prevChapter.id)
            } else {
                closeReader()
            }
        }
    }

    override fun markChapterRead(
        isRead: Boolean,
        mangaId: String?,
        chapterId: String?,
    ) {
        val currentMangaId = mangaId ?: _state.value.mangaId ?: return
        val currentChapterId = chapterId ?: _state.value.currentChapterId ?: return

        readerScope.launch {
            chapterRepository.markAsRead(
                mangaId = currentMangaId,
                chapterId = currentChapterId,
                isRead = isRead
            )
        }
    }

    private fun updateState(transform: ReaderManagerState.() -> ReaderManagerState) {
        _state.value = transform(_state.value)
    }

    private suspend fun preloadChapterPages(
        urls: List<String>
    ) = coroutineScope {
        val chunkedUrls = urls.chunked(4)
        for (chunkIndex in chunkedUrls.indices) {
            val requests = chunkedUrls[chunkIndex].map { url ->
                ImageRequest.Builder(context)
                    .data(url)
                    .dispatcher(Dispatchers.IO)
                    .build()
            }

            requests.mapIndexed { index, it ->
                val imageIndex = (chunkIndex * 4) + index

                async {
                    val result = context.imageLoader.executeBlocking(it)
                    val updatedList = ArrayList<Boolean>(_state.value.currentChapterPageLoaded)

                    if (result is SuccessResult && _state.value.readerType == ReaderType.PAGE) {
                        _state.value = _state.value.copy(
                            currentChapterPageLoaded = updatedList
                                .also {
                                    it[imageIndex] = true
                                }
                        )
                    }
                }
            }.awaitAll()
        }
    }
}