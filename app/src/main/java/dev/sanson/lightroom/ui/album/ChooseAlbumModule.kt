package dev.sanson.lightroom.ui.album

import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class ChooseAlbumModule {

    @Binds
    @IntoSet
    abstract fun bindChooseAlbumUiFactory(factory: ChooseAlbumUiFactory): Ui.Factory

    @Binds
    @IntoSet
    abstract fun bindChooseAlbumPresenterFactory(factory: ChooseAlbumPresenterFactory): Presenter.Factory
}
