package pi.project.grapify

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import pi.project.grapify.data.di.AppModule

class GrapeLeafApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@GrapeLeafApplication)
            modules(AppModule().appModule)
        }
    }
}