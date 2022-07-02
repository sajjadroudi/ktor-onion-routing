package ir.roudi.notification

import ir.roudi.Logger

object NotificationDataSource {

    private val storage = mutableListOf<Notification>()

    fun saveNotification(notification: Notification) {
        storage += notification
        Logger.log("NotificationDataSource", "A new notif saved!")
    }

    fun getAllNotifications() : List<Notification> {
        Logger.log("NotificationDataSource", "All notifications are gotten")
        return storage.toList()
    }

}