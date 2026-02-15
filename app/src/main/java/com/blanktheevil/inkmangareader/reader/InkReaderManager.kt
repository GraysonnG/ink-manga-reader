package com.blanktheevil.inkmangareader.reader

import android.content.Context
import android.util.Log
import coil.executeBlocking
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.blanktheevil.inkmangareader.bookmark.BookmarkManager
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.repositories.chapter.ChapterRepository
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaRepository
import com.blanktheevil.inkmangareader.data.repositories.mappers.currentChapter
import com.blanktheevil.inkmangareader.data.repositories.mappers.nextChapter
import com.blanktheevil.inkmangareader.data.repositories.mappers.prevChapter
import com.blanktheevil.inkmangareader.download.DownloadManager
import com.blanktheevil.inkmangareader.hasActiveInternetConnection
import com.blanktheevil.inkmangareader.launchAsUnit
import com.blanktheevil.inkmangareader.log
import com.blanktheevil.inkmangareader.orFalse
import com.blanktheevil.inkmangareader.settings.SettingsManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class InkReaderManager(
    private val context: Context,
    private val chapterRepository: ChapterRepository,
    private val mangaRepository: MangaRepository,
    private val downloadManager: DownloadManager,
    private val bookmarkManager: BookmarkManager,
    private val settingsManager: SettingsManager,
) : ReaderManager {
    private val readerScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val _state = MutableStateFlow(ReaderManagerState())
    override val state: StateFlow<ReaderManagerState> = _state.asStateFlow()

    companion object {
        private const val LONG_STRIP = "Long Strip"
        private val TAG = InkReaderManager::class.java.simpleName
    }

    private var preloadImagesJob: Pair<String, Job>? = null

    override fun setChapter(chapterId: String) = readerScope.launchAsUnit {
        val isChapterDownloaded = downloadManager.isChapterDownloaded(chapterId)
        val getChapterDataJob = async { getChapterData(chapterId = chapterId, isDownloaded = isChapterDownloaded) }
        val getChapterPagesDataJob = async { getChapterPagesData(chapterId = chapterId, isDownloaded = isChapterDownloaded) }

        if (preloadImagesJob != null && preloadImagesJob?.first != chapterId) {
            preloadImagesJob?.second?.cancel()
        }

        updateState { copy(
            currentChapterId = chapterId,
            currentPage = 0,
            currentChapterPageUrls = emptyList(),
            currentChapterLoading = true,
            currentChapterPagesLoaded = true,
        ) }

        awaitAll(
            getChapterDataJob,
            getChapterPagesDataJob
        )

        setBookmark()

        if (_state.value.currentChapterPageUrls.size == 1) {
            markChapterRead(true)
        }

        updateState { copy(
            expanded = true,
        ) }

        if (!isChapterDownloaded && preloadImagesJob?.first != chapterId) {
            preloadImagesJob = chapterId to preloadChapterPages(_state.value.currentChapterPageUrls)
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

    private suspend fun getChapterData(chapterId: String, isDownloaded: Boolean) {
        val readerType = settingsManager.settingsState.firstOrNull()?.defaultReaderType
            ?: ReaderType.PAGE

        val chapterEither = if (isDownloaded) {
            downloadManager.getChapterData(chapterId)
        } else {
            chapterRepository.getEager(chapterId)
        }

        chapterEither.onSuccess { chapter ->
            updateState { copy(
                currentChapter = chapter,
                currentChapterLoading = false,
                mangaId = chapter.relatedMangaId,
                manga = chapter.relatedManga,
                readerType = chapter.relatedManga.getReaderType(readerType)
            ) }

            if (chapter.relatedManga == null && chapter.relatedMangaId != null) {
                mangaRepository.getEager(chapter.relatedMangaId).onSuccess { manga ->
                    updateState { copy(
                        manga = manga,
                        readerType = manga.getReaderType(readerType)
                    ) }
                }
            }
        }
    }

    private suspend fun getChapterPagesData(
        chapterId: String,
        isDownloaded: Boolean,
    ) {
        val pagesEither = if (isDownloaded) {
            downloadManager.getChapterPages(chapterId)
        } else {
            val dataSaver = settingsManager.settingsState.firstOrNull()?.dataSaver.orFalse()
            chapterRepository.getPages(chapterId, dataSaver)
        }

        pagesEither.onSuccess { pages ->
            updateState { copy(
                currentChapterPagesLoaded = false,
                currentChapterPageUrls = pages,
                currentChapterPageLoaded = List(pages.size) { isDownloaded }.toMutableList()
            ) }
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
        // TODO: this is a temp measure to prevent crashes in offline mode
        if (!context.hasActiveInternetConnection) {
            closeReader()
            return
        }

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
        // TODO: this is a temp measure to prevent crashes in offline mode
        if (!context.hasActiveInternetConnection) {
            closeReader()
            return
        }

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

    private fun Manga?.getReaderType(default: ReaderType): ReaderType = if (
        this?.tags?.any { it.equals(LONG_STRIP, true) } == true
    ) ReaderType.VERTICAL else default

    private fun setBookmark() = with(_state.value) {
        if (this.currentChapterId != null && this.mangaId != null) {
            bookmarkManager.setBookmark(
                mangaId,
                currentChapterId
            )
        }
    }

    private fun updateState(transform: ReaderManagerState.() -> ReaderManagerState) {
        _state.value = transform(_state.value)
    }

    private fun preloadChapterPages(
        urls: List<String>
    ) = readerScope.launchPreloadJob {
        log("preloadChapterPages: Started", tag = TAG)
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
                                .also { it[imageIndex] = true }
                        )
                    }
                }
            }.awaitAll()
        }
    }

    private fun CoroutineScope.launchPreloadJob(block: suspend CoroutineScope.() -> Unit): Job =
        launch(block = block).apply {
            invokeOnCompletion { cause ->
                when (cause) {
                    null -> {
                        preloadImagesJob = null
                        log("preloadChapterPages: Success", tag = TAG)
                    }
                    is CancellationException -> {
                        log("preloadChapterPages: Cancelled", Log::w, tag = TAG)
                    }
                    else -> {
                        log("preloadChapterPages: Error", Log::e, tag = TAG)
                        cause.printStackTrace()
                    }
                }
            }
        }
}