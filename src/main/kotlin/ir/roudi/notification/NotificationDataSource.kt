package ir.roudi.notification

object NotificationDataSource {

    private val storage = mutableListOf<Notification>()

    fun saveNotification(notification: Notification) {
        storage += notification
    }

    fun getAllNotifications() : List<Notification> = storage.toList()

}