package com.blanktheevil.inkmangareader.di

import androidx.room.Room
import com.blanktheevil.inkmangareader.adapters.JSONObjectAdapter
import com.blanktheevil.inkmangareader.data.api.GithubApi
import com.blanktheevil.inkmangareader.data.api.MangaDexApi
import com.blanktheevil.inkmangareader.data.auth.SessionManager
import com.blanktheevil.inkmangareader.data.dto.RelationshipList
import com.blanktheevil.inkmangareader.data.repositories.auth.AuthRepository
import com.blanktheevil.inkmangareader.data.repositories.auth.AuthRepositoryImpl
import com.blanktheevil.inkmangareader.data.repositories.chapter.ChapterRepository
import com.blanktheevil.inkmangareader.data.repositories.chapter.ChapterRepositoryImpl
import com.blanktheevil.inkmangareader.data.repositories.list.UserListRepository
import com.blanktheevil.inkmangareader.data.repositories.list.UserListRepositoryImpl
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaRepository
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaRepositoryImpl
import com.blanktheevil.inkmangareader.data.repositories.user.UserRepository
import com.blanktheevil.inkmangareader.data.repositories.user.UserRepositoryImpl
import com.blanktheevil.inkmangareader.data.room.InkDatabase
import com.blanktheevil.inkmangareader.data.state.ModelStateProvider
import com.blanktheevil.inkmangareader.download.DownloadManager
import com.blanktheevil.inkmangareader.download.DownloadManagerImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import okhttp3.OkHttpClient
import org.json.JSONObject
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.Date

const val MANGADEX_BASE_URL = "https://api.mangadex.org"
const val GITHUB_BASE_URL = "https://antsylich.github.io"

val appModule = module {
    single {
        OkHttpClient.Builder().build()
    }

    single {
        Moshi.Builder()
            .add(JSONObject::class.java, JSONObjectAdapter())
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .add(RelationshipList::class.java, RelationshipList.Adapter())
            .build()
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            InkDatabase::class.java,
            InkDatabase.NAME,
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single<MangaDexApi> {
        Retrofit.Builder()
            .baseUrl(MANGADEX_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .client(get())
            .build()
            .create()
    }

    single<GithubApi> {
        Retrofit.Builder()
            .baseUrl(GITHUB_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .client(get())
            .build()
            .create()
    }

    singleOf(::DownloadManagerImpl) { bind<DownloadManager>() }

    singleOf(::SessionManager) { createdAtStart() }
    single { get<InkDatabase>().mangaDao() }
    single { get<InkDatabase>().chapterDao() }
    single { get<InkDatabase>().listDao() }
    single { get<InkDatabase>().modelStateDao() }
    single { get<InkDatabase>().downloadDao() }


    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    singleOf(::MangaRepositoryImpl) { bind<MangaRepository>() }
    singleOf(::ChapterRepositoryImpl) { bind<ChapterRepository>() }
    singleOf(::UserListRepositoryImpl) { bind<UserListRepository>() }

    singleOf(::ModelStateProvider)
}