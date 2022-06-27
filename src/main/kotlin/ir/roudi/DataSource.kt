package ir.roudi

object DataSource {

    private val storage = mutableListOf<Notification>()

    fun saveNotification(notification: Notification) {
        storage += notification
    }

    fun getAllNotifications() : List<Notification> = storage.toList()

}