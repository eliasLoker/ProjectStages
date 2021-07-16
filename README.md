# ProjectStages

![alt text](https://github.com/eliasLoker/ProjectStages/blob/master/scheme.png?raw=true "Описание будет тут")

This application is based on the MVI architecture principle. The application has an Interactor middleware. The role of this layer is in Coroutine context switching and wrapping the result of a database query in a ResultWrapper class (ex. Kotlin Result). MutableStateFlow and Channel (for single-events) are used to deliver events to the View-layer. The ViewBinding library is used to work with the view-layer. The ViewModel gets Flow from the Interactor to automatically monitor changes in the Room database.
