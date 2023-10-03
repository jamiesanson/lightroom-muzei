package dev.sanson.lightroom.ui.source

import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class ChooseSourceModule {

    @Binds
    @IntoSet
    abstract fun bindChooseSourceUiFactory(factory: ChooseSourceUiFactory): Ui.Factory

    @Binds
    @IntoSet
    abstract fun bindChooseSourcePresenterFactory(factory: ChooseSourcePresenterFactory): Presenter.Factory
}
