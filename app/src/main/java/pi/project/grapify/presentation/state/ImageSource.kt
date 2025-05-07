package pi.project.grapify.presentation.state

sealed class ImageSource {
  data object None : ImageSource()
  data object Gallery : ImageSource()
  data object Camera : ImageSource()
}