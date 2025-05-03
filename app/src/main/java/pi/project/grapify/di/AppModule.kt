package pi.project.grapify.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pi.project.grapify.data.repository.GrapeLeafDiseaseRepository
import pi.project.grapify.data.repository.GrapeLeafDiseaseRepositoryImpl
import pi.project.grapify.domain.service.ImageProcessor
import pi.project.grapify.domain.service.ImageProcessorImpl
import pi.project.grapify.domain.service.ModelAnalyzer
import pi.project.grapify.domain.service.TFLiteModelAnalyzer
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGrapeLeafDiseaseRepository(
        @ApplicationContext context: Context,
        imageProcessor: ImageProcessor,
        modelAnalyzer: ModelAnalyzer
    ) : GrapeLeafDiseaseRepository {
        return GrapeLeafDiseaseRepositoryImpl(context, imageProcessor, modelAnalyzer)
    }

    @Provides
    @Singleton
    fun provideImageProcessor(): ImageProcessor {
        return ImageProcessorImpl()
    }

    @Provides
    @Singleton
    fun provideModelAnalyzer(
        @ApplicationContext context: Context
    ): ModelAnalyzer {
        return TFLiteModelAnalyzer(context)
    }
}