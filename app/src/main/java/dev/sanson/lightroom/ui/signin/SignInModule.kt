package dev.sanson.lightroom.ui.signin

import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class SignInModule {

    @Binds
    @IntoSet
    abstract fun bindSignInUiFactory(factory: SignInUiFactory): Ui.Factory

    @Binds
    @IntoSet
    abstract fun bindSignInPresenterFactory(factory: SignInPresenterFactory): Presenter.Factory
}
