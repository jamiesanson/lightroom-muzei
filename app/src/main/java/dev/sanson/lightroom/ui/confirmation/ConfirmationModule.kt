package dev.sanson.lightroom.ui.confirmation

import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class ConfirmationModule {

    @Binds
    @IntoSet
    abstract fun bindConfirmationUiFactory(factory: ConfirmationUiFactory): Ui.Factory

    @Binds
    @IntoSet
    abstract fun bindConfirmationPresenterFactory(factory: ConfirmationPresenterFactory): Presenter.Factory
}
