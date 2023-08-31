package dev.sanson.lightroom.ui.filter

import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class FilterAssetsModule {

    @Binds
    @IntoSet
    abstract fun bindFilterAssetsUiFactory(factory: FilterAssetsUiFactory): Ui.Factory

    @Binds
    @IntoSet
    abstract fun bindFilterAssetsPresenterFactory(factory: FilterAssetsPresenterFactory): Presenter.Factory
}
