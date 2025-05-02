package pi.project.grapify.data.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import pi.project.grapify.data.repository.GrapeLeafDiseaseRepository
import pi.project.grapify.data.repository.GrapeLeafDiseaseRepositoryImpl
import pi.project.grapify.domain.service.ImageProcessor
import pi.project.grapify.domain.service.ImageProcessorImpl
import pi.project.grapify.domain.service.ModelAnalyzer
import pi.project.grapify.domain.service.TFLiteModelAnalyzer
import pi.project.grapify.presentation.viewmodel.GrapeLeafDiseaseViewModel

class AppModule {
    val appModule = module {
        // Sediakan context aplikasi
        single { androidContext() }

        // Service Layer
        single<ImageProcessor> { ImageProcessorImpl() }
        single<ModelAnalyzer> { TFLiteModelAnalyzer(get()) }

        // Repository Layer
        single<GrapeLeafDiseaseRepository> { GrapeLeafDiseaseRepositoryImpl(get(), get(), get()) }

        // ViewModel
        viewModel { GrapeLeafDiseaseViewModel(get()) }
    }
}